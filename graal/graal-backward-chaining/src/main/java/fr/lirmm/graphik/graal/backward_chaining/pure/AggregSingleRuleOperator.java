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
package fr.lirmm.graphik.graal.backward_chaining.pure;

import java.util.Collection;
import java.util.LinkedList;

import fr.lirmm.graphik.graal.backward_chaining.pure.queries.QueryUtils;
import fr.lirmm.graphik.graal.backward_chaining.pure.rules.RulesCompilation;
import fr.lirmm.graphik.graal.backward_chaining.pure.utils.QueryUnifier;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.ruleset.IndexedByHeadPredicatesRuleSet;

/**
 * Rewriting operator SRA
 * Query rewriting engine that rewrite query using
 * aggregation by rule of most general single piece-unifiers
 * 
 * @author Mélanie KÖNIG
 */
public class AggregSingleRuleOperator extends AbstractRewritingOperator {
	

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
	public Collection<ConjunctiveQuery> getRewritesFrom(ConjunctiveQuery q, IndexedByHeadPredicatesRuleSet ruleSet, RulesCompilation compilation) {
		LinkedList<ConjunctiveQuery> rewriteSet = new LinkedList<ConjunctiveQuery>();
		Collection<QueryUnifier> unifiers = new LinkedList<QueryUnifier>();
		for (Rule r : getUnifiableRules(q.getAtomSet().predicatesIterator(),
				ruleSet, compilation)) {
			unifiers.addAll(getSRUnifier(q, r, compilation));
		}

		/** compute the rewrite from the unifier **/
		ConjunctiveQuery a;
		for (QueryUnifier u : unifiers) {
			a = QueryUtils.rewrite(q, u);
			if(a != null) {
				rewriteSet.add(a);
			}
		}

		return rewriteSet;
	}
	
}
