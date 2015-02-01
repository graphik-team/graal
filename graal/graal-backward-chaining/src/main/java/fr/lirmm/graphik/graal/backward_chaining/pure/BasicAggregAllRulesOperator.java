package fr.lirmm.graphik.graal.backward_chaining.pure;

import java.util.Collection;
import java.util.LinkedList;

import fr.lirmm.graphik.graal.backward_chaining.pure.queries.QueryUtils;
import fr.lirmm.graphik.graal.backward_chaining.pure.rules.RulesCompilation;
import fr.lirmm.graphik.graal.backward_chaining.pure.utils.QueryUnifier;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
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
		try {
			for (Rule r : getUnifiableRules(q.getAtomSet().getAllPredicates(),
					ruleSet, compilation)) {

				/** compute the single rule unifiers **/
				srUnifiers.addAll(getSRUnifier(q, r, compilation));
			}
		} catch (AtomSetException e) {
		}

		/** compute the aggregated unifier **/
		unifiers = getAggregatedUnifiers(srUnifiers);

		/** compute the rewrite from the unifier **/
		for (QueryUnifier u : unifiers) {
			ConjunctiveQuery a = QueryUtils.rewrite(q, u);
			currentRewrites.add(a);
		}

		return currentRewrites;
	}

}
