/**
 * 
 */
package fr.lirmm.graphik.graal.grd;

import java.util.LinkedList;

import org.junit.Assert;
import org.junit.Test;

import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.TestUtils;
import fr.lirmm.graphik.graal.core.factory.RuleFactory;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class GRDTest {

	@Test
	public void atomErasingFilterTest() {
		LinkedList<Rule> rules = new LinkedList<Rule>();
		rules.add(RuleFactory.instance().create(TestUtils.qX, TestUtils.rX));
		rules.add(RuleFactory.instance().create(TestUtils.rX, TestUtils.qX));
		rules.add(RuleFactory.instance().create(TestUtils.rX, TestUtils.pXY));

		GraphOfRuleDependencies grd = new GraphOfRuleDependencies(rules, true, new AtomErasingFilter());
		Assert.assertFalse(grd.existUnifier(rules.get(0), rules.get(1)));
		Assert.assertFalse(grd.existUnifier(rules.get(1), rules.get(0)));
		Assert.assertTrue(grd.existUnifier(rules.get(0), rules.get(2)));
	}

}
