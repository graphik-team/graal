/**
 * 
 */
package fr.lirmm.graphik.graal.core;

import java.util.Arrays;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;

import fr.lirmm.graphik.graal.core.factory.RuleFactory;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class RuleSetTest {
	
	private static Predicate predicate = new Predicate("pred", 2);

	private static Rule rule1, rule2;
	static {
		Atom atom1, atom2;

		Term[] terms = new Term[2];
		terms[0] = new Term("X", Term.Type.VARIABLE);
		terms[1] = new Term("a", Term.Type.CONSTANT);
		atom1 = new DefaultAtom(predicate, Arrays.asList(terms));
		terms = new Term[2];
		terms[0] = new Term("X", Term.Type.VARIABLE);
		terms[1] = new Term("Y", Term.Type.VARIABLE);
		atom2 = new DefaultAtom(predicate, Arrays.asList(terms));

		
		rule1 = RuleFactory.getInstance().createRule();
		rule1.getBody().add(atom1);
		rule1.getHead().add(atom2);
		
		rule2 = RuleFactory.getInstance().createRule();
		rule2.getBody().add(atom2);
		rule2.getHead().add(atom1);
	}


	@Test
	public void test() {
		RuleSet rs = new LinkedListRuleSet();
		rs.add(rule1);
		rs.add(rule2);
		Iterator<Rule> it = rs.iterator();
		Assert.assertTrue(it.hasNext());
		it.next();
		Assert.assertTrue(it.hasNext());
	}
}
