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
 package fr.lirmm.graphik.graal.backward_chaining.pure;

import java.util.Collection;
import java.util.LinkedList;

import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.ruleset.IndexedByHeadPredicatesRuleSet;

/**
 * Rewriting operator ARA
 * rewriting engine that rewrite query using
 * aggregation all rule of most general single piece-unifiers
 *         
 * @author Mélanie KÖNIG
 */
public class BasicAggregAllRulesOperator extends AbstractRewritingOperator {

	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the rewrites compute from the given fact and the rule set of the
	 * receiving object.
	 * 
	 * @param q
	 *            A fact
	 * @return the ArrayList that contains the rewrites compute from the given
	 *         fact and the rule set of the receiving object.
	 * @throws Exception
	 */
	@Override
	public Collection<ConjunctiveQuery>  getRewritesFrom(ConjunctiveQuery q, IndexedByHeadPredicatesRuleSet ruleSet, RulesCompilation compilation) {		
		LinkedList<ConjunctiveQuery> currentRewrites = new LinkedList<ConjunctiveQuery>();
		LinkedList<QueryUnifier> srUnifiers = new LinkedList<QueryUnifier>();
		LinkedList<QueryUnifier> unifiers = new LinkedList<QueryUnifier>();
		for (Rule r : getUnifiableRules(q.getAtomSet().predicatesIterator(),
				ruleSet, compilation)) {

			/** compute the single rule unifiers **/
			srUnifiers.addAll(getSRUnifier(q, r, compilation));
		}


		/** compute the aggregated unifier **/
		unifiers = getAggregatedUnifiers(srUnifiers);

		/** compute the rewrite from the unifier **/
		for (QueryUnifier u : unifiers) {
			ConjunctiveQuery a = Utils.rewrite(q, u);
			currentRewrites.add(a);
		}

		return currentRewrites;
	}

}
