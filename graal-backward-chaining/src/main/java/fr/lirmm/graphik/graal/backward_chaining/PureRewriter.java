/**
 * 
 */
package fr.lirmm.graphik.graal.backward_chaining;

import java.util.Collections;
import java.util.Date;
import java.util.Iterator;

import fr.lirmm.graphik.graal.backward_chaining.pure.QREAggregSingleRule;
import fr.lirmm.graphik.graal.backward_chaining.pure.QueryRewritingEngine;
import fr.lirmm.graphik.graal.backward_chaining.pure.queries.PureQuery;
import fr.lirmm.graphik.graal.backward_chaining.pure.rules.IDCompilation;
import fr.lirmm.graphik.graal.backward_chaining.pure.rules.RulesCompilation;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.ruleset.LinkedListRuleSet;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
public class PureRewriter extends AbstractBackwardChainer {

	Iterator<ConjunctiveQuery> rewrites;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * @throws Exception
	 * 
	 */
	public PureRewriter(ConjunctiveQuery query, Iterable<Rule> rules) {
		LinkedListRuleSet ruleset = new LinkedListRuleSet(rules);
		RulesCompilation comp = new IDCompilation();
		comp.code(ruleset, (new Date()).toString());

		PureQuery pquery = new PureQuery(query);
		QueryRewritingEngine qre = new QREAggregSingleRule(pquery, ruleset, comp);

		try {
			this.rewrites = qre.computeRewritings().iterator();
		} catch (Exception e) {
			this.rewrites = Collections.<ConjunctiveQuery> emptyList()
					.iterator();
		}
	}

	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public boolean hasNext() {
		return this.rewrites.hasNext();
	}

	@Override
	public ConjunctiveQuery next() {
		return this.rewrites.next();
	}

}
