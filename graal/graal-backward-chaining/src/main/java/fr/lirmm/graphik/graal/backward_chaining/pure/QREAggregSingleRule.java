/**
 * 
 */
package fr.lirmm.graphik.graal.backward_chaining.pure;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import fr.lirmm.graphik.graal.backward_chaining.pure.queries.PureQuery;
import fr.lirmm.graphik.graal.backward_chaining.pure.rules.RulesCompilation;
import fr.lirmm.graphik.graal.backward_chaining.pure.utils.QueryUnifier;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.atomset.AtomSetException;

/**
 * @author Mélanie KÖNIG Query rewriting engine that rewrite query using
 *         aggregation by rule of most general single piece-unifiers
 */
@SuppressWarnings({ "unchecked" })
public class QREAggregSingleRule extends QueryRewritingEngine {

	public QREAggregSingleRule(PureQuery query, Iterable<Rule> rules,
			RulesCompilation order) {
		super(query, rules, order);
	}

	/**
	 * Returns all the aggregated unifiers compute from the given unifiers
	 */
	public LinkedList<QueryUnifier> getAggregatedUnifiers(
			LinkedList<QueryUnifier> unifToAggregate) {
		LinkedList<QueryUnifier> unifAggregated = new LinkedList<QueryUnifier>();
		LinkedList<QueryUnifier> restOfUnifToAggregate = (LinkedList<QueryUnifier>) unifToAggregate
				.clone();
		Iterator<QueryUnifier> itr = unifToAggregate.iterator();
		QueryUnifier u;
		while (itr.hasNext()) {
			u = itr.next();
			restOfUnifToAggregate.remove(u);

			// add to the result all the aggregated piece-unifier build from u
			// and the rest of the mgu single piece-unifiers
			unifAggregated.addAll(aggregate(u, restOfUnifToAggregate));
		}
		return unifAggregated;
	}

	/**
	 * Returns the list of all the aggregated unifiers that can be build from u
	 * and others unifiers of lu. recursive function
	 * 
	 * @param u
	 *            the unifier whose we want aggregate with the unifiers of lu
	 * @param lu
	 *            list of unifiers
	 * @return the list of all aggregated unifier build from u and unifiers of
	 *         lu
	 * @throws Exception
	 */
	protected LinkedList<QueryUnifier> aggregate(QueryUnifier u,
			LinkedList<QueryUnifier> l) {
		LinkedList<QueryUnifier> lu = (LinkedList<QueryUnifier>) l.clone();
		// if there is no more unifier to aggregate
		if (lu.isEmpty()) {
			LinkedList<QueryUnifier> res = new LinkedList<QueryUnifier>();
			res.add(u);
			return res;
		} else {
			QueryUnifier first = lu.getFirst(); // take the first one
			lu.removeFirst(); // remove first one from lu
			// if first can be aggregated with u
			LinkedList<QueryUnifier> res = aggregate(u, lu);
			if (u.isCompatible(first)) {
				// System.out.println("oui");
				// compute the others aggregation from the aggregation of u and
				// first and the rest of lu
				res.addAll(aggregate(u.aggregate(first), lu));
				// concatenate this result and the others aggregations from u
				// and the rest of lu

			}
			return res;
		}
	}

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
	protected Collection<ConjunctiveQuery> getRewritesFrom(ConjunctiveQuery q) {
		LinkedList<ConjunctiveQuery> rewriteSet = new LinkedList<ConjunctiveQuery>();
		Collection<QueryUnifier> unifiers = new LinkedList<QueryUnifier>();
		try {
			for (Rule r : getUnifiableRules(q.getAtomSet().getAllPredicates(),
					getRuleSet(), getRulesCompilation())) {
				unifiers.addAll(getSRUnifier(q, r));
			}
		} catch (AtomSetException e) {
		}

		/** compute the rewrite from the unifier **/
		ConjunctiveQuery a;
		for (QueryUnifier u : unifiers) {
			a = rewrite(q, u);
			if(a != null) {
				rewriteSet.add(a);
			}
		}

		return rewriteSet;
	}

	protected LinkedList<QueryUnifier> getSRUnifier(ConjunctiveQuery q, Rule r) {
		LinkedList<QueryUnifier> unifiers = new LinkedList<QueryUnifier>();

		/** compute the simple unifiers **/
		unifiers = getSinglePieceUnifiers(q, r);

		/** compute the aggregated unifier by rule **/
		if (!unifiers.isEmpty())
			unifiers = getAggregatedUnifiers(unifiers);

		return unifiers;
	}

}
