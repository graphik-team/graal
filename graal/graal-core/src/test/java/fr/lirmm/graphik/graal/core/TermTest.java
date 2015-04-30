package fr.lirmm.graphik.graal.core;

import org.junit.Assert;
import org.junit.Test;

import fr.lirmm.graphik.util.Prefix;


/**
 * Unit test.
 */
public class TermTest 
{

	@Test
	public void constantConstructorTest() {
		String label = "label";
		Term.Type type = Term.Type.CONSTANT;
		Term term = new Term(label, type);
		
		Assert.assertTrue(Term.Type.CONSTANT.equals(term.getType()));
		Assert.assertTrue(term.getIdentifier().equals(
				Prefix.CONSTANT.getPrefix() + label));
	}
	
	@Test
	public void literalConstructorTest() {
		int value = 1;
		Term.Type type = Term.Type.LITERAL;
		Term term = new Term(1, type);

		Assert.assertTrue(Term.Type.LITERAL.equals(term.getType()));
		Assert.assertTrue(term.getIdentifier().equals(
				Prefix.LITERAL.getPrefix() + value));
	}

	@Test
	public void variableConstructorTest() {
		String label = "label";
		Term.Type type = Term.Type.VARIABLE;
		Term term = new Term(label, type);

		Assert.assertTrue(Term.Type.VARIABLE.equals(term.getType()));
		Assert.assertTrue(term.getIdentifier().equals(
				Prefix.EMPTY.getPrefix() + label));
	}
	
	@Test
	public void equalsTest() {
		String label = "label";
		Term.Type type = Term.Type.CONSTANT;
		Term term = new Term(label, type);
		
		Assert.assertTrue("Term not equals itself", term.equals(term));
		
		Term other = new Term(label, Term.Type.CONSTANT);
		Assert.assertTrue("Term not equals an other term", term.equals(other));
	}
}
