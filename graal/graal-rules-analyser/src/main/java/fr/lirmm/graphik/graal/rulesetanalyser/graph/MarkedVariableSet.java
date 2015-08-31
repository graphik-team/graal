/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2015)
 *
 * Contributors :
 *
 * Clément SIPIETER <clement.sipieter@inria.fr>
 * Mélanie KÖNIG
 * Swan ROCHER
 * Jean-François BAGET
 * Michel LECLÈRE
 * Marie-Laure MUGNIER <mugnier@lirmm.fr>
 *
 *
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
 /**
 * 
 */
package fr.lirmm.graphik.graal.rulesetanalyser.graph;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.term.Term;
import fr.lirmm.graphik.graal.rulesetanalyser.util.PredicatePosition;

/**
 * The marked variable set is built from a rule set by the following marking
 * procedure: (i) for each rule Ri and for each variable v occuring in its body,
 * if v does not occur in all atoms of its head, mark (each occurrence of) v in
 * its body; (ii) apply until a fixpoint is reached: for each rule Ri, if a
 * marked variable v appears at position p[k] in its body, then for each rule Rj
 * (including i = j) and for each variable x appearing at position p[k] in the
 * head of Rj, mark each occurence of x in the body of Rj.
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * @author Swan Rocher
 * 
 */
public class MarkedVariableSet {

	private LinkedList<MarkedRule> markedRuleSet;
	private Map<Predicate, LinkedList<MarkedRule>> map;
	private Queue<PredicatePosition> markedPosition;

	public static class MarkedRule {

		public MarkedRule(Rule rule) {
			this.rule = rule;
			this.markedVars = new TreeSet<Term>();
		}

		public Rule rule;
		public Set<Term> markedVars;
	}

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////

	public MarkedVariableSet(Iterable<Rule> rules) {
		this.markedPosition = new LinkedList<PredicatePosition>();
		this.markedRuleSet = new LinkedList<MarkedRule>();
		for (Rule r : rules) {
			this.markedRuleSet.add(new MarkedRule(r));
		}
		map = new HashMap<Predicate, LinkedList<MarkedRule>>();
		process();
	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	public Collection<MarkedRule> getMarkedRuleCollection() {
		return this.markedRuleSet;
	}

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

	private void process() {
		firstStep();
		secondStep();
	}

	/**
	 * for each rule Ri and for each variable v occuring in its body, if v does
	 * not occur in all atoms of its head, mark (each occurrence of) v in its
	 * body;
	 */
	private void firstStep() {
		for (MarkedRule markedRule : this.markedRuleSet) {
			// put rule in the map
			for (Atom atom : markedRule.rule.getHead()) {
				Predicate p = atom.getPredicate();
				LinkedList<MarkedRule> set = map.get(p);
				if (set == null) {
					set = new LinkedList<MarkedRule>();
					map.put(p, set);
				}
				set.add(markedRule);
			}

			// mark the rule
			testRule(markedRule);
		}
	}

	private void testRule(MarkedRule mrule) {
		Set<Term> bodyVars = mrule.rule.getBody().getTerms(Term.Type.VARIABLE);
		for (Term v : bodyVars) {
			for (Atom a : mrule.rule.getHead()) {
				if (!a.getTerms().contains(v)) {
					mark(v, mrule);
				}
			}
		}
	}

	private void mark(Term v, MarkedRule mrule) {
		if (!mrule.markedVars.contains(v)) {
			mrule.markedVars.add(v);
			for (Atom a : mrule.rule.getBody()) {
				int i = 0;
				for (Term t : a) {
					if (v.equals(t)) {
						this.markedPosition.add(new PredicatePosition(a
								.getPredicate(), i));
					}
					++i;
				}
			}
		}
	}

	/**
	 * apply until a fixpoint is reached: for each rule Ri, if a marked variable
	 * v appears at position p[k] in its body, then for each rule Rj (including
	 * i = j) and for each variable x appearing at position p[k] in the head of
	 * Rj, mark each occurence of x in the body of Rj.
	 */
	private void secondStep() {
		LinkedList<MarkedRule> mrList;
		PredicatePosition mpos;
		Term v;
		while (!this.markedPosition.isEmpty()) {
			mpos = this.markedPosition.poll();
			mrList = this.map.get(mpos.predicate);
			if (mrList != null) {
				for (MarkedRule mr : mrList) {
					for (Atom a : mr.rule.getHead()) {
						if (a.getPredicate().equals(mpos.predicate)) {
							v = a.getTerm(mpos.position);
							if (v.getType().equals(Term.Type.VARIABLE)) {
								this.mark(v, mr);
							}
						}
					}
				}
			}
		}
	}
}
