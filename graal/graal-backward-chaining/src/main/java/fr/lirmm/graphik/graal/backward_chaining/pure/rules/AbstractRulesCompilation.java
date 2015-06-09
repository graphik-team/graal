/* Graal v0.7.4
 * Copyright (c) 2014-2015 Inria Sophia Antipolis - Méditerranée / LIRMM (Université de Montpellier & CNRS)
 * All rights reserved.
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * Author(s): Clément SIPIETER
 *            Mélanie KÖNIG
 *            Swan ROCHER
 *            Jean-François BAGET
 *            Michel LECLÈRE
 *            Marie-Laure MUGNIER
 */
 /**
 * 
 */
package fr.lirmm.graphik.graal.backward_chaining.pure.rules;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import fr.lirmm.graphik.graal.backward_chaining.pure.utils.Misc;
import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.DefaultConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.InMemoryAtomSet;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.util.Profiler;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
public abstract class AbstractRulesCompilation implements RulesCompilation {

	private Profiler profiler;

	public void setProfiler(Profiler profiler) {
		this.profiler = profiler;
	}

	public Profiler getProfiler() {
		return this.profiler;
	}

	public Iterable<ConjunctiveQuery> unfold(
			Iterable<ConjunctiveQuery> pivotRewritingSet) {
		if (this.getProfiler() != null) {
			this.getProfiler().start("Unfolding time");
		}

		Collection<ConjunctiveQuery> unfoldingRewritingSet = this
				.developpRewriting(pivotRewritingSet);

		/** clean the rewrites to return **/
		Misc.computeCover(unfoldingRewritingSet);
		
		if (this.getProfiler() != null) {
			this.getProfiler().stop("Unfolding time");
			Iterator<?> it = unfoldingRewritingSet.iterator();
			int i = 0;
			while (it.hasNext()) {
				it.next();
				++i;
			}
			this.getProfiler().add("Unfolded rewritings", i);
		}

		return unfoldingRewritingSet;

	}

	/**
	 * Add in the given rewriting set the rewrites that can be entailed from the
	 * predicate order ex: the rewrite A(x) can be entailed from the rewrite
	 * B(x) and the predicate order A > B
	 * 
	 * @return
	 */
	private Collection<ConjunctiveQuery> developpRewriting(
			Iterable<ConjunctiveQuery> rewritingSet) {
		Collection<ConjunctiveQuery> unfoldingRewritingSet = new LinkedList<ConjunctiveQuery>();
		LinkedList<AtomSet> newQueriesBefore = new LinkedList<AtomSet>();
		LinkedList<AtomSet> newQueriesAfter = new LinkedList<AtomSet>();
		LinkedList<AtomSet> newQueriesTmp;
		Iterable<Atom> atomsRewritings;
		InMemoryAtomSet copy;

		// ConjunctiveQuery q;

		for (ConjunctiveQuery originalQuery : rewritingSet) {
			// q = query.getIrredondant(compilation);
			// for all atom of the query we will build a list of all the
			// rewriting
			newQueriesBefore.clear();
			newQueriesBefore.add(new LinkedListAtomSet());

			// we will build all the possible fact from the rewriting of the
			// atoms
			for (Atom a : originalQuery) {
				atomsRewritings = this.getRewritingOf(a);
				for (AtomSet q : newQueriesBefore) {
					// for each possible atom at the next position clone the
					// query and add the atom
					for (Atom atom : atomsRewritings) {//
						copy = new LinkedListAtomSet((Iterable<Atom>) q);
						copy.add(atom);
						newQueriesAfter.add(copy);
					}
				}

				// switch list
				newQueriesTmp = newQueriesBefore;
				newQueriesBefore = newQueriesAfter;
				newQueriesAfter = newQueriesTmp;
				newQueriesAfter.clear();
			}
			for (AtomSet a : newQueriesBefore) {
				unfoldingRewritingSet.add(new DefaultConjunctiveQuery(a,
						originalQuery.getAnswerVariables()));
			}
		}

		return unfoldingRewritingSet;
	}

	@Override
	public AtomSet getIrredondant(AtomSet atomSet) {
		AtomSet irr = new LinkedListAtomSet(atomSet);
		Iterator<Atom> i = irr.iterator();
		Iterator<Atom> j;
		Atom origin;
		Atom target;
		boolean isSubsumed;
		while (i.hasNext()) {
			target = i.next();
			j = irr.iterator();
			isSubsumed = false;
			while (j.hasNext() && !isSubsumed) {
				origin = j.next();
				if (target != origin && this.isImplied(target, origin)) {
					isSubsumed = true;
					i.remove();
				}
			}
		}

		return irr;
	}

	/**
	 * Remove compilable rule from ruleset and return a List of compilable
	 * rules.
	 *
	 * @param ruleset
	 * @return
	 */
	protected final LinkedList<Rule> extractCompilable(Iterator<Rule> ruleSet) {
		LinkedList<Rule> compilable = new LinkedList<Rule>();
		Rule r;

		while (ruleSet.hasNext()) {
			r = ruleSet.next();
			if (this.isCompilable(r)) {
				compilable.add(r);
				ruleSet.remove();
			}
		}

		if (this.getProfiler() != null) {
			this.getProfiler().add("Compiled rules", compilable.size());
		}

		return compilable;
	}

}
