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

	PureQuery pquery;
	LinkedListRuleSet ruleset;
	Iterator<ConjunctiveQuery> rewrites = null;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * @throws Exception
	 * 
	 */
	public PureRewriter(ConjunctiveQuery query, Iterable<Rule> rules) {
		this.pquery = new PureQuery(query);
		this.ruleset = new LinkedListRuleSet(rules);
	}

	// /////////////////////////////////////////////////////////////////////////
	// ITERATOR METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public boolean hasNext() {
		if (this.rewrites == null) {
			this.compute();
		}
		return this.rewrites.hasNext();
	}

	@Override
	public ConjunctiveQuery next() {
		if (this.rewrites == null) {
			this.compute();
		}
		return this.rewrites.next();
	}

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

	private void compute() {
		for (BackwardChainerListener listener : this.getListeners()) {
			listener.startPreprocessing();
		}
		// preprocessing
		RulesCompilation comp = new IDCompilation();
		comp.code(this.ruleset, (new Date()).toString());
		for (BackwardChainerListener listener : this.getListeners()) {
			listener.endPreprocessing();
			listener.startRewriting();
		}
		// rewriting
		QueryRewritingEngine qre = new QREAggregSingleRule(pquery, ruleset,
				comp);
		try {
			this.rewrites = qre.computeRewritings().iterator();
		} catch (Exception e1) {
			this.rewrites = Collections.<ConjunctiveQuery> emptyList()
					.iterator();
		}
		for (BackwardChainerListener listener : this.getListeners()) {
			listener.endRewriting();
		}
	}

}
