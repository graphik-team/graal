/* Graal v0.7.4
 * Copyright (c) 2014-2015 Inria Sophia Antipolis - Méditerranée / LIRMM (Université de Montpellier & CNRS)
 * All rights reserved.
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * Author(s): Clément SIPIETER
 *            Mélanie KÖNIG
 *            Swan ROCHER
 *            Jean-François BAGET
 *            Michel LECLÈRE
 *            Marie-Laure MUGNIER
 */
 package fr.lirmm.graphik.graal.core;

import org.junit.Assert;
import org.junit.Test;

import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.core.term.Literal;
import fr.lirmm.graphik.graal.core.term.Term;


/**
 * Unit test.
 */
public class TermTest 
{

	@Test
	public void constantConstructorTest() {
		String label = "label";
		Term term = DefaultTermFactory.instance().createConstant(label);
		
		Assert.assertTrue(Term.Type.CONSTANT.equals(term.getType()));
		Assert.assertTrue(term.getIdentifier().toString()
.equals(label));
	}
	
	@Test
	public void literalConstructorTest() {
		int value = 1;
		Literal term = DefaultTermFactory.instance().createLiteral(1);

		Assert.assertTrue(Term.Type.LITERAL.equals(term.getType()));
		Assert.assertTrue(term.getValue().equals(value));
	}

	@Test
	public void variableConstructorTest() {
		String label = "label";
		Term term = DefaultTermFactory.instance().createVariable(label);

		Assert.assertTrue(Term.Type.VARIABLE.equals(term.getType()));
		Assert.assertTrue(term.getIdentifier().toString()
.equals(label));
	}
	
	@Test
	public void equalsTest() {
		String label = "label";
		Term term = DefaultTermFactory.instance().createConstant(label);
		
		Assert.assertTrue("Term not equals itself", term.equals(term));
		
		Term other = DefaultTermFactory.instance().createConstant(label);
		Assert.assertTrue("Term not equals an other term", term.equals(other));
	}
}
