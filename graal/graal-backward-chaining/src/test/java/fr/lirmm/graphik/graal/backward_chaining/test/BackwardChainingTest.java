package fr.lirmm.graphik.graal.backward_chaining.test;

/**
 * 
 */

import org.junit.Assert;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import fr.lirmm.graphik.graal.backward_chaining.PureRewriter;
import fr.lirmm.graphik.graal.backward_chaining.pure.AggregAllRulesOperator;
import fr.lirmm.graphik.graal.backward_chaining.pure.AggregSingleRuleOperator;
import fr.lirmm.graphik.graal.backward_chaining.pure.BasicAggregAllRulesOperator;
import fr.lirmm.graphik.graal.backward_chaining.pure.RewritingOperator;
import fr.lirmm.graphik.graal.backward_chaining.pure.rules.HierarchicalCompilation;
import fr.lirmm.graphik.graal.backward_chaining.pure.rules.IDCompilation;
import fr.lirmm.graphik.graal.backward_chaining.pure.rules.NoCompilation;
import fr.lirmm.graphik.graal.backward_chaining.pure.rules.RulesCompilation;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.ruleset.LinkedListRuleSet;
import fr.lirmm.graphik.graal.core.ruleset.RuleSet;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.util.Iterators;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
@RunWith(Theories.class)
public class BackwardChainingTest {

	@DataPoints
	public static RulesCompilation[] compilations() {
		return new RulesCompilation[] {new NoCompilation(), new HierarchicalCompilation(), new IDCompilation()};
	}
	
	@DataPoints
	public static RewritingOperator[] operators() {
		return new RewritingOperator[] {new AggregAllRulesOperator(), new AggregSingleRuleOperator(), new BasicAggregAllRulesOperator()};
	}

	/**
	 * folding on answer variables
	 */
	@Theory
	public void forbiddenFoldingTest(RulesCompilation compilation, RewritingOperator operator) {
		RuleSet rules = new LinkedListRuleSet();
		rules.add(DlgpParser.parseRule("q(Y,X) :- p(X,Y)."));
		rules.add(DlgpParser.parseRule("p(X,Y) :- q(Y,X)."));

		ConjunctiveQuery query = DlgpParser.parseQuery("?(X,Y,Z) :- p(X, Y), q(Y,Z).");

		compilation.compile(rules.iterator());
		PureRewriter bc = new PureRewriter(query, rules, compilation, operator);
		bc.enableUnfolding(true);
		
		int i = Iterators.count(bc);
		Assert.assertEquals(4, i);
	}
	
	@Theory
	public void queriesCover(RulesCompilation compilation, RewritingOperator operator) {
		RuleSet rules = new LinkedListRuleSet();
		rules.add(DlgpParser.parseRule("p(X) :- r(Y, X)."));

		ConjunctiveQuery query = DlgpParser.parseQuery("?(X) :- p(X), r(a,X).");

		compilation.compile(rules.iterator());
		PureRewriter bc = new PureRewriter(query, rules, compilation, operator);
		bc.enableUnfolding(true);
		
		int i = Iterators.count(bc);
		Assert.assertEquals(1, i);
	}
}
