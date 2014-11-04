package fr.lirmm.graphik.graal.backward_chaining.pure;

import java.util.Collection;
import java.util.LinkedList;

import fr.lirmm.graphik.graal.backward_chaining.pure.queries.PureQuery;
import fr.lirmm.graphik.graal.backward_chaining.pure.rules.RulesCompilation;
import fr.lirmm.graphik.graal.backward_chaining.pure.utils.QueryUnifier;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.atomset.AtomSetException;

/**
 * @author Mélanie KÖNIG Query rewriting engine that rewrite query using
 *         aggregation all rule of most general single piece-unifiers
 */
public class QREAggregAllRulesBasic extends QREAggregSingleRule {

	public QREAggregAllRulesBasic(PureQuery query) {
		super(query);
	}

	public QREAggregAllRulesBasic(PureQuery query, Iterable<Rule> rules) {
		super(query, rules);
	}

	public QREAggregAllRulesBasic(PureQuery query, Iterable<Rule> rules,
			RulesCompilation order) {
		super(query, rules, order);
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

		LinkedList<ConjunctiveQuery> currentRewrites = new LinkedList<ConjunctiveQuery>();
		LinkedList<QueryUnifier> SRUnifiers = new LinkedList<QueryUnifier>();
		LinkedList<QueryUnifier> unifiers = new LinkedList<QueryUnifier>();
		try {
			for (Rule r : getUnifiableRules(q.getAtomSet().getAllPredicates(),
					getRuleSet(), getRulesCompilation())) {

				/** compute the single rule unifiers **/
				SRUnifiers.addAll(getSRUnifier(q, r));
			}
		} catch (AtomSetException e) {
		}

		/** compute the aggregated unifier **/
		unifiers = getAggregatedUnifiers(SRUnifiers);

		/** compute the rewrite from the unifier **/
		for (QueryUnifier u : unifiers) {
			ConjunctiveQuery a = rewrite(q, u);
			currentRewrites.add(a);
		}

		return currentRewrites;
	}

}
