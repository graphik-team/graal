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
import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
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
		for (Rule r : getUnifiableRules(q.getAtomSet().getAllPredicates(),
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
