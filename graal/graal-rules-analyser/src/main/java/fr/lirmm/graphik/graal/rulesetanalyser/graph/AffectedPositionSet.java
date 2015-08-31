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

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.InMemoryAtomSet;
import fr.lirmm.graphik.graal.core.term.Term;
import fr.lirmm.graphik.graal.core.term.Term.Type;
import fr.lirmm.graphik.graal.rulesetanalyser.util.PredicatePosition;

/**
 * The affected position set is built from a rule set by the following
 * procedure: (i) for each rule and for each existentially quantified variable
 * occuring at position p[i] in its head, p[i] is affected; (ii) for each rule
 * and for each variable x that occurs only at affected positions in its body,
 * all positions q[j] in its head where occurs x are affected.
 * 
 * A variable is said to be affected if it occurs only at affected positions.
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * @author Swan Rocher
 * 
 */
public class AffectedPositionSet {

	private Iterable<Rule> ruleSet;
	private Set<PredicatePosition> affectedPosition;

	public AffectedPositionSet(Iterable<Rule> ruleSet) {
		this.affectedPosition = new TreeSet<PredicatePosition>();
		this.ruleSet = ruleSet;
		init();
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// GETTERS
	// /////////////////////////////////////////////////////////////////////////
	
	/**
	 * @return
	 */
	public Iterable<Rule> getRules() {
		return this.ruleSet;
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////

	public boolean isAffected(Predicate predicate, int position) {
		PredicatePosition predicatePosition = new PredicatePosition(predicate,
				position);
		return this.isAffected(predicatePosition);
	}

	public boolean isAffected(PredicatePosition pp) {
		return this.affectedPosition.contains(pp);
	}

	public Set<Term> getAllAffectedVariables(InMemoryAtomSet body) {
		return this.getAllAffectedVariables(body.getTerms(Type.VARIABLE), body);
	}

	public Set<Term> getAllAffectedFrontierVariables(Rule rule) {
		return this.getAllAffectedVariables(rule.getFrontier(), rule.getBody());
	}

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * return all variable in vars that is affected
	 * 
	 * @param variableFilter
	 * @param body
	 * @return
	 */
	private Set<Term> getAllAffectedVariables(Set<Term> vars, AtomSet body) {
		Set<Term> affectedVars = new TreeSet<Term>();
		affectedVars.addAll(vars);
		int i;
		for (Atom atom : body) {
			i = -1;
			for (Term t : atom) {
				++i;
				if (Type.VARIABLE.equals(t.getType())
						&& !this.isAffected(atom.getPredicate(), i)) {
					affectedVars.remove(t);
				}
			}
		}
		return affectedVars;
	}

	private void init() {
		step1();
		step2();
	}

	/**
	 * for each rule and for each existentially quantified variable occuring at
	 * position p[i] in its head, p[i] is affected;
	 */
	private void step1() {
		int i;
		Set<Term> existentials;
		PredicatePosition predicatePosition;

		for (Rule rule : ruleSet) {
			existentials = rule.getExistentials();
			for (Atom atom : rule.getHead()) {
				i = -1;
				for (Term t : atom) {
					++i;
					if (existentials.contains(t)) {
						predicatePosition = new PredicatePosition(
								atom.getPredicate(), i);
						this.affectedPosition.add(predicatePosition);
					}
				}
			}
		}
	}

	/**
	 * for each rule and for each variable x that occurs only at affected
	 * positions in its body, all positions q[j] in its head where occurs x are
	 * affected.
	 */
	private void step2() {
		AtomSet body;
		boolean isAffected;
		int i;
		Iterator<Atom> atomIt;
		Iterator<Term> termIt;
		Atom a;
		Term t;
		boolean fixPoint = false;

		while (!fixPoint) {
			fixPoint = true;
			for (Rule rule : ruleSet) {
				body = rule.getBody();
				for (Term term : rule.getBody().getTerms(Type.VARIABLE)) {
					isAffected = true;
					atomIt = body.iterator();
					while (atomIt.hasNext() && isAffected) {
						i = -1;
						a = atomIt.next();
						termIt = a.iterator();
						while (termIt.hasNext() && isAffected) {
							++i;
							t = termIt.next();
							if (term.equals(t)) {
								if (!isAffected(a.getPredicate(), i)) {
									isAffected = false;
								}
							}
						}
					}
					if (isAffected) {
						if (this.affectInHead(rule, term)) {
							fixPoint = false;
						}
					}
				}
			}
		}
	}

	/**
	 * @param rule
	 * @param term
	 */
	private boolean affectInHead(Rule rule, Term term) {
		int i;
		PredicatePosition predicatePosition;
		boolean addSomeAffectedPosition = false;

		for (Atom atom : rule.getHead()) {
			i = -1;
			for (Term t : atom) {
				++i;
				if (term.equals(t)) {
					predicatePosition = new PredicatePosition(
							atom.getPredicate(), i);
					if (!isAffected(predicatePosition)) {
						this.affectedPosition.add(predicatePosition);
						addSomeAffectedPosition = true;
					}
				}
			}
		}
		return addSomeAffectedPosition;
	}

};
