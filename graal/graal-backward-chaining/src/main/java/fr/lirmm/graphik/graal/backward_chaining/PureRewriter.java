/**
 * 
 */
package fr.lirmm.graphik.graal.backward_chaining;

import java.util.Iterator;

import fr.lirmm.graphik.graal.backward_chaining.pure.AggregAllRulesOperator;
import fr.lirmm.graphik.graal.backward_chaining.pure.AggregSingleRuleOperator;
import fr.lirmm.graphik.graal.backward_chaining.pure.RewritingAlgorithm;
import fr.lirmm.graphik.graal.backward_chaining.pure.AbstractRewritingOperator;
import fr.lirmm.graphik.graal.backward_chaining.pure.RewritingOperator;
import fr.lirmm.graphik.graal.backward_chaining.pure.queries.PureQuery;
import fr.lirmm.graphik.graal.backward_chaining.pure.rules.NoCompilation;
import fr.lirmm.graphik.graal.backward_chaining.pure.rules.RulesCompilation;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.UnionConjunctiveQueries;
import fr.lirmm.graphik.graal.core.ruleset.IndexedByHeadPredicatesRuleSet;
import fr.lirmm.graphik.graal.core.ruleset.LinkedListRuleSet;
import fr.lirmm.graphik.util.Verbosable;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
public class PureRewriter extends AbstractBackwardChainer implements Verbosable {

	PureQuery pquery;
	LinkedListRuleSet ruleset;
	RulesCompilation compilation;
	UnionConjunctiveQueries ucqs = null;
	Iterator<ConjunctiveQuery> rewrites = null;
	private boolean verbose;
	private RewritingOperator operator;

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
		this.compilation = new NoCompilation();
		this.compilation.compile(rules);
	}

	/**
	 * @throws Exception
	 * 
	 */
	public PureRewriter(ConjunctiveQuery query, Iterable<Rule> rules,
			RulesCompilation compilation, RewritingOperator operator) {
		this.pquery = new PureQuery(query);
		this.ruleset = new LinkedListRuleSet(rules);
		this.compilation = compilation;
		this.operator = operator;
	}

	// /////////////////////////////////////////////////////////////////////////
	// METHODS
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

	@Override
	public UnionConjunctiveQueries getUCQs() {
		if (this.ucqs == null) {
			this.compute();
		}
		return this.ucqs;
	}

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

	private void compute() {
		IndexedByHeadPredicatesRuleSet indexedRuleSet = new IndexedByHeadPredicatesRuleSet(this.ruleset);
		
		// rewriting
		RewritingAlgorithm algo = new RewritingAlgorithm(this.operator);
		
		algo.enableVerbose(this.verbose);
		operator.setProfiler(this.getProfiler());
		algo.setProfiler(this.getProfiler());
		
		Iterable<ConjunctiveQuery> queries = algo.execute(pquery, indexedRuleSet, compilation);

		// unfolding
		if (this.getProfiler() != null) {
			this.getProfiler().start("unfolding time");
		}

		this.ucqs = new UnionConjunctiveQueries(
				this.compilation.unfold(queries));
		this.rewrites = this.ucqs.iterator();

		if (this.getProfiler() != null) {
			this.getProfiler().stop("unfolding time");
		}

	}

	@Override
	public void enableVerbose(boolean enable) {
		this.verbose = enable;
	}

}
