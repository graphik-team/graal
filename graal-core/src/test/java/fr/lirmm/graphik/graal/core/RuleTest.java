/**
 * 
 */
package fr.lirmm.graphik.graal.core;

import java.util.Arrays;

import org.junit.Test;
import org.junit.Assert;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.factory.RuleFactory;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
public class RuleTest {

	private static Predicate predicate = new Predicate("pred", 2);

	private static Atom atom1, atom2, atom3;
	static {
		Term[] terms = new Term[2];
		terms[0] = new Term("X", Term.Type.VARIABLE);
		terms[1] = new Term("a", Term.Type.CONSTANT);
		atom1 = new DefaultAtom(predicate, Arrays.asList(terms));
		terms = new Term[2];
		terms[0] = new Term("X", Term.Type.VARIABLE);
		terms[1] = new Term("Y", Term.Type.VARIABLE);
		atom2 = new DefaultAtom(predicate, Arrays.asList(terms));
		terms = new Term[2];
		terms[0] = new Term("b", Term.Type.CONSTANT);
		terms[1] = new Term("Y", Term.Type.VARIABLE);
		atom3 = new DefaultAtom(predicate, Arrays.asList(terms));
	}

	@Test
	public void PiecesTest1() {
		Rule rule = RuleFactory.getInstance().createRule();
		rule.getHead().add(atom1);
		rule.getHead().add(atom2);
		rule.getHead().add(atom3);
		Assert.assertEquals(1, rule.getPieces().size());
	}
	
	@Test
	public void PiecesTest2() {
		Rule rule = RuleFactory.getInstance().createRule();
		rule.getBody().add(atom2);
		rule.getHead().add(atom1);
		rule.getHead().add(atom2);
		rule.getHead().add(atom3);
		Assert.assertEquals(3, rule.getPieces().size());
	}
	
	@Test
	public void PiecesTest3() {
		Rule rule = RuleFactory.getInstance().createRule();
		rule.getHead().add(atom3);
		rule.getHead().add(atom3);
		rule.getHead().add(atom3);
		Assert.assertEquals(1, rule.getPieces().size());
	}
}
