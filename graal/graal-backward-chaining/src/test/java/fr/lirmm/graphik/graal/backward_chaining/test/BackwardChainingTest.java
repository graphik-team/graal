package fr.lirmm.graphik.graal.backward_chaining.test;

/**
 * 
 */

import org.junit.Assert;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import fr.lirmm.graphik.graal.backward_chaining.BackwardChainer;
import fr.lirmm.graphik.graal.backward_chaining.PureRewriter;
import fr.lirmm.graphik.graal.backward_chaining.pure.rules.HierarchicalCompilation;
import fr.lirmm.graphik.graal.backward_chaining.pure.rules.IDCompilation;
import fr.lirmm.graphik.graal.backward_chaining.pure.rules.NoCompilation;
import fr.lirmm.graphik.graal.backward_chaining.pure.rules.RulesCompilation;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.ruleset.LinkedListRuleSet;
import fr.lirmm.graphik.graal.core.ruleset.RuleSet;
import fr.lirmm.graphik.graal.io.dlgp.DlgpParser;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
@RunWith(Theories.class)
public class BackwardChainingTest {

	@DataPoints
	public static RulesCompilation[] substitution() {
		return new RulesCompilation[] {new NoCompilation(), new HierarchicalCompilation(), new IDCompilation()};
	}

	/**
	 * folding on answer variables
	 */
	@Theory
	public void forbiddenFoldingTest(RulesCompilation compilation) {
		RuleSet rules = new LinkedListRuleSet();
		rules.add(DlgpParser.parseRule("q(Y,X) :- p(X,Y)."));
		rules.add(DlgpParser.parseRule("p(X,Y) :- q(Y,X)."));

		ConjunctiveQuery query = DlgpParser.parseQuery("?(X,Y,Z) :- p(X, Y), q(Y,Z).");

		compilation.compile(rules);
		BackwardChainer bc = new PureRewriter(query, rules, compilation);
		
		int i = 0;
		while (bc.hasNext()) {
			++i;
			bc.next();
		}
		Assert.assertEquals(4, i);
	}
}
