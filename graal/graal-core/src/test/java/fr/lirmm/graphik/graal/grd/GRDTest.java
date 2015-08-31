/**
 * 
 */
package fr.lirmm.graphik.graal.grd;

import java.util.LinkedList;

import org.junit.Assert;
import org.junit.Test;

import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class GRDTest {

	@Test
	public void atomErasingFilterTest() {
		LinkedList<Rule> rules = new LinkedList<Rule>();
		rules.add(DlgpParser.parseRule("q(X) :- p(X)."));
		rules.add(DlgpParser.parseRule("p(X) :- q(X)."));
		rules.add(DlgpParser.parseRule("r(X) :- p(X)."));

		GraphOfRuleDependencies grd = new GraphOfRuleDependencies(rules, true, new AtomErasingFilter());
		Assert.assertFalse(grd.existUnifier(rules.get(0), rules.get(1)));
		Assert.assertFalse(grd.existUnifier(rules.get(1), rules.get(0)));
		Assert.assertTrue(grd.existUnifier(rules.get(1), rules.get(2)));
	}

}
