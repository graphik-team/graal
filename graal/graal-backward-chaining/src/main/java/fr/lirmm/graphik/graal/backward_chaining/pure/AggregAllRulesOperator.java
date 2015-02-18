package fr.lirmm.graphik.graal.backward_chaining.pure;

import java.util.Collection;
import java.util.LinkedList;

import fr.lirmm.graphik.graal.backward_chaining.pure.queries.MarkedQuery;
import fr.lirmm.graphik.graal.backward_chaining.pure.queries.QueryUtils;
import fr.lirmm.graphik.graal.backward_chaining.pure.rules.RulesCompilation;
import fr.lirmm.graphik.graal.backward_chaining.pure.utils.QueryUnifier;
import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.ruleset.IndexedByHeadPredicatesRuleSet;

/**
 * Rewriting operator ARAM
 * rewriting engine that rewrite query using
 * aggregation all rule of most general single piece-unifiers, selecting
 * unifiers during the aggregation and using a marking system to avoid
 * redundant rewritings
 * 
 * @author Mélanie KÖNIG Query
 */
public class AggregAllRulesOperator extends BasicAggregAllRulesOperator {
	
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
		LinkedList<QueryUnifier> unifiers;
		for (Rule r : getUnifiableRules(q.getAtomSet().predicatesIterator(),
				ruleSet, compilation)) {
			unifiers = getSinglePieceUnifiers(q, r, compilation);
			for (QueryUnifier u : unifiers) {
				rewriteSet.add(QueryUtils.rewriteWithMark(q, u));
			}
		}

		return rewriteSet;
	}

	// /////////////////////////////////////////////////////////////////////////
	// PROTECTED METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	@Override
	protected LinkedList<QueryUnifier> getSRUnifier(ConjunctiveQuery q, Rule r, RulesCompilation compilation) {
		LinkedList<QueryUnifier> unifiers = new LinkedList<QueryUnifier>();
		LinkedList<QueryUnifier> simpleUnifiers = new LinkedList<QueryUnifier>();
		/** compute the simple unifiers **/
		simpleUnifiers = getSinglePieceUnifiers(q, r, compilation);
		if (!simpleUnifiers.isEmpty()) {
			if (q instanceof MarkedQuery) {
				MarkedQuery copy = (MarkedQuery) new MarkedQuery(
						(MarkedQuery) q);
				copy.markAll();
				simpleUnifiers = getSinglePieceUnifiers(copy, r, compilation);
			}
		}
		/** compute the aggregated unifier by rule **/
		unifiers.addAll(getAggregatedUnifiers(simpleUnifiers));

		return unifiers;
	}
	
	/**
	 * Returns the list of the atoms of the query that can be unify with the
	 * head of R
	 * 
	 * @param query
	 *            the query to unify
	 * @param rule
	 *            the rule whose has the head to unify
	 * @return the list of the atoms of the query that have the same predicate
	 *         as the head atom of R and that are recently created in query
	 * @throws Exception
	 */
	public LinkedList<Atom> getUnifiableAtoms(MarkedQuery query, Rule rule, RulesCompilation compilation) {
		LinkedList<Atom> atoms = this.getUnifiableAtoms((ConjunctiveQuery)query, rule, compilation);
		LinkedList<Atom> res = new LinkedList<Atom>();
		
		// keep only the recently created so marked in query
		for (Atom a : atoms) {
			if (query.isMarked(a)) {
				res.add(a);
			}
		}
		
		return res;
	}

}
