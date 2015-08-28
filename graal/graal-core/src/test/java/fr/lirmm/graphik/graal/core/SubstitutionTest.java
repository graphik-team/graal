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
 /**
 * 
 */
package fr.lirmm.graphik.graal.core;

import org.junit.Assert;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import fr.lirmm.graphik.graal.core.impl.HashMapSubstitution;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.core.term.Term;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
@RunWith(Theories.class)
public class SubstitutionTest {
	
	private static final Term X = DefaultTermFactory.instance().createVariable(
			"X");
	private static final Term Y = DefaultTermFactory.instance().createVariable(
			"Y");
	private static final Term Z = DefaultTermFactory.instance().createVariable(
			"Z");
	
	private static final Term A = DefaultTermFactory.instance().createConstant(
			"a");
	private static final Term B = DefaultTermFactory.instance().createConstant(
			"b");
	
	@DataPoints
	public static Substitution[] substitution() {
		return new Substitution[] {new HashMapSubstitution()};
	}

	@Theory
	public void aggregateTest1(Substitution s1, Substitution s2) {
		s1.put(Y,A);
		s1.put(Z,A);
		
		s2.put(X,Y);
		s2.put(Y,Z);
		
		Substitution composition = s1.compose(s2);
		Assert.assertNotNull(composition);

		Assert.assertEquals(A, composition.createImageOf(X));
		Assert.assertEquals(A, composition.createImageOf(Y));
		Assert.assertEquals(A, composition.createImageOf(Z));
	}
	
	@Theory
	public void aggregateTest1Inverse(Substitution s1, Substitution s2)  {
		s1.put(X,Y);
		s1.put(Y,Z);
		
		s2.put(Z,A);
		s2.put(Y,A);
		
		Substitution composition = s1.compose(s2);
		Assert.assertNotNull(composition);
		
		Assert.assertEquals(A, composition.createImageOf(X));
		Assert.assertEquals(A, composition.createImageOf(Y));
		Assert.assertEquals(A, composition.createImageOf(Z));
	}

	@Theory
	public void aggregateTest2(Substitution s1, Substitution s2) {
		s1.put(Y,A);
		s1.put(Z,A);
		
		s2.put(Y,X);
		s2.put(Z,X);
		
		Substitution composition = s1.compose(s2);
		Assert.assertNotNull(composition);
		
		Assert.assertEquals(A, composition.createImageOf(X));
		Assert.assertEquals(A, composition.createImageOf(Y));
		Assert.assertEquals(A, composition.createImageOf(Z));
	}
	
	@Theory
	public void aggregateTest2Inverse(Substitution s1, Substitution s2)  {
		s1.put(Y,X);
		s1.put(Z,X);
		
		s2.put(Y,A);
		s2.put(Z,A);
		
		Substitution composition = s1.compose(s2);
		Assert.assertNotNull(composition);
		
		Assert.assertEquals(A, composition.createImageOf(X));
		Assert.assertEquals(A, composition.createImageOf(Y));
		Assert.assertEquals(A, composition.createImageOf(Z));
	}
	
	@Theory
	public void aggregateTest3(Substitution s1)  {
		s1.put(X,A);
		s1.compose(X,Y);
		
		Assert.assertNotNull(s1);
		
		Assert.assertEquals(A, s1.createImageOf(X));
		Assert.assertEquals(A, s1.createImageOf(Y));
	}
	
	@Theory
	public void aggregateImpossible(Substitution s1, Substitution s2)  {
		s1.put(X,A);
		s2.put(X,B);
		
		Substitution composition = s1.compose(s2);
		Assert.assertNull(composition);
	}
	
}
