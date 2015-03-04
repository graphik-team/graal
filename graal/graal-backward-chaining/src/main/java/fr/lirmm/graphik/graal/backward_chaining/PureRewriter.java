/**
 * 
 */
package fr.lirmm.graphik.graal.backward_chaining;

import java.util.Iterator;

import fr.lirmm.graphik.graal.backward_chaining.pure.AggregSingleRuleOperator;
import fr.lirmm.graphik.graal.backward_chaining.pure.RewritingAlgorithm;
import fr.lirmm.graphik.graal.backward_chaining.pure.RewritingOperator;
import fr.lirmm.graphik.graal.backward_chaining.pure.queries.PureQuery;
import fr.lirmm.graphik.graal.backward_chaining.pure.rules.NoCompilation;
import fr.lirmm.graphik.graal.backward_chaining.pure.rules.RulesCompilation;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.RuleUtils;
import fr.lirmm.graphik.graal.core.ruleset.IndexedByHeadPredicatesRuleSet;
import fr.lirmm.graphik.graal.core.ruleset.LinkedListRuleSet;
import fr.lirmm.graphik.util.Verbosable;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
public class PureRewriter extends AbstractBackwardChainer implements Verbosable {

	private PureQuery pquery;
	private LinkedListRuleSet ruleset;
	private RulesCompilation compilation;
	private Iterator<ConjunctiveQuery> rewrites = null;
	
	private boolean verbose;
	private boolean isUnfoldingEnable = true;
	private RewritingOperator operator;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * @throws Exception
	 * 
	 */
	public PureRewriter(ConjunctiveQuery query, Iterable<Rule> rules) {
		this(query, rules, new NoCompilation());
	}
	
	public PureRewriter(ConjunctiveQuery query, Iterable<Rule> rules,
			RulesCompilation compilation) {
		this(query, rules, compilation, new AggregSingleRuleOperator());
	}

	/**
	 * @throws Exception
	 * 
	 */
	public PureRewriter(ConjunctiveQuery query, Iterable<Rule> rules,
			RulesCompilation compilation, RewritingOperator operator) {
		this.pquery = new PureQuery(query);
		this.ruleset = new LinkedListRuleSet(RuleUtils.computeMonoPiece(rules.iterator()));
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
		ConjunctiveQuery query = this.rewrites.next();
		PureQuery.removeAnswerPredicate(query);
		return query;
	}
		
	/**
	 * Enable or disable unfolding. The unfolding is enable by default.
	 * @param isUnfoldingEnable
	 */
	public void enableUnfolding(boolean isUnfoldingEnable) {
		this.isUnfoldingEnable = isUnfoldingEnable;
	}
	
	public boolean isUnfoldingEnable() {
		return this.isUnfoldingEnable;
	}
	
	public void enableVerbose(boolean enable) {
		this.verbose = enable;
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
		if(this.isUnfoldingEnable) {
			if (this.getProfiler() != null) {
				this.getProfiler().start("unfolding time");
			}
	
			queries = this.compilation.unfold(queries);
	
			if (this.getProfiler() != null) {
				this.getProfiler().stop("unfolding time");
			}
		}
		
		this.rewrites = queries.iterator();
	}

}
