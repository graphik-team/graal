package fr.lirmm.graphik.graal.core;

import org.junit.Assert;
import org.junit.Test;

import fr.lirmm.graphik.graal.core.Term;


/**
 * Unit test.
 */
public class TermTest 
{

	@Test
	public void constructorTest() {
		String label = "label";
		Term.Type type = Term.Type.CONSTANT;
		Term term = new Term(label, type);
		
		Assert.assertTrue(Term.Type.CONSTANT.equals(term.getType()));
		Assert.assertTrue(label.equals(term.getValue()));
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
