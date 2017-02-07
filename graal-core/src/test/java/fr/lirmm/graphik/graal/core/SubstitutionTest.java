/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2017)
 *
 * Contributors :
 *
 * Clément SIPIETER <clement.sipieter@inria.fr>
 * Mélanie KÖNIG
 * Swan ROCHER
 * Jean-François BAGET
 * Michel LECLÈRE
 * Marie-Laure MUGNIER <mugnier@lirmm.fr>
 *
 *
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
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

import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
@RunWith(Theories.class)
public class SubstitutionTest {
	
	private static final Variable S = DefaultTermFactory.instance().createVariable(
			"S");
	private static final Variable T = DefaultTermFactory.instance().createVariable(
			"T");
	private static final Variable U = DefaultTermFactory.instance().createVariable(
			"U");
	private static final Variable V = DefaultTermFactory.instance().createVariable(
			"V");
	private static final Variable X = DefaultTermFactory.instance().createVariable(
			"X");
	private static final Variable Y = DefaultTermFactory.instance().createVariable(
			"Y");
	private static final Variable Z = DefaultTermFactory.instance().createVariable(
			"Z");
	
	private static final Term A = DefaultTermFactory.instance().createConstant(
			"a");
	private static final Term B = DefaultTermFactory.instance().createConstant(
			"b");
	private static final Term C = DefaultTermFactory.instance().createConstant(
			"c");
	
	@DataPoints
	public static Substitution[] substitution() {
		return new Substitution[] { new HashMapSubstitution(), new TreeMapSubstitution() };
	}
	
	@Theory
	public void compositionTest1(Substitution s1, Substitution s2) {
		
		s1.put(X,Y);
		s1.put(S,T);
		
		s2.put(Y,Z);
		s2.put(U,V);
		
		
		Substitution comp = s2.compose(s1);
		System.out.println(comp);

		Assert.assertEquals(Z, comp.createImageOf(X));
		Assert.assertEquals(Z, comp.createImageOf(Y));
		Assert.assertEquals(Z, comp.createImageOf(Z));
		Assert.assertEquals(T, comp.createImageOf(S));
		Assert.assertEquals(V, comp.createImageOf(U));

	}

	@Theory
	public void aggregateTest1(Substitution s1, Substitution s2) {
		s1.put(Y,A);
		s1.put(Z,A);
		
		s2.put(X,Y);
		s2.put(Y,Z);
		
		Substitution aggregation = s1.aggregate(s2);
		Assert.assertNotNull(aggregation);

		Assert.assertEquals(A, aggregation.createImageOf(X));
		Assert.assertEquals(A, aggregation.createImageOf(Y));
		Assert.assertEquals(A, aggregation.createImageOf(Z));
	}
	
	@Theory
	public void aggregateTest1Inverse(Substitution s1, Substitution s2)  {
		s1.put(X,Z);
		s1.put(Y,Z);
		
		s2.put(Z,A);
		s2.put(Y,A);
		
		Substitution aggregation = s1.aggregate(s2);
		Assert.assertNotNull(aggregation);
		
		Assert.assertEquals(A, aggregation.createImageOf(X));
		Assert.assertEquals(A, aggregation.createImageOf(Y));
		Assert.assertEquals(A, aggregation.createImageOf(Z));
	}

	@Theory
	public void aggregateTest2(Substitution s1, Substitution s2) {
		s1.put(Y,A);
		s1.put(Z,A);
		
		s2.put(Y,X);
		s2.put(Z,X);
		
		Substitution aggregation = s1.aggregate(s2);
		Assert.assertNotNull(aggregation);
		
		Assert.assertEquals(A, aggregation.createImageOf(X));
		Assert.assertEquals(A, aggregation.createImageOf(Y));
		Assert.assertEquals(A, aggregation.createImageOf(Z));
	}
	
	@Theory
	public void aggregateTest2Inverse(Substitution s1, Substitution s2)  {
		s1.put(Y,X);
		s1.put(Z,X);
		
		s2.put(Y,A);
		s2.put(Z,A);
		
		Substitution aggregation = s1.aggregate(s2);
		Assert.assertNotNull(aggregation);
		
		Assert.assertEquals(A, aggregation.createImageOf(X));
		Assert.assertEquals(A, aggregation.createImageOf(Y));
		Assert.assertEquals(A, aggregation.createImageOf(Z));
	}
	
	@Theory
	public void aggregateTest3(Substitution s1)  {
		s1.put(X,A);
		s1.aggregate(X,Y);
		
		Assert.assertNotNull(s1);
		
		Assert.assertEquals(A, s1.createImageOf(X));
		Assert.assertEquals(A, s1.createImageOf(Y));
	}
	
	@Theory
	public void aggregateTest4(Substitution s1, Substitution s2)  {
		s1.put(X,Y);
		s1.put(Z,U);
		
		s2.put(Y,A);
		s2.put(Z,V);
		
		Substitution aggregation = s1.aggregate(s2);
		System.out.println(aggregation);
		Assert.assertNotNull(aggregation);
		
		Assert.assertEquals(A, aggregation.createImageOf(X));
		Assert.assertEquals(A, aggregation.createImageOf(Y));
		
		Assert.assertEquals(aggregation.createImageOf(Z), aggregation.createImageOf(U));
		Assert.assertEquals(aggregation.createImageOf(V), aggregation.createImageOf(U));

	}
	
	@Theory
	public void aggregateImpossibleTest1(Substitution s1, Substitution s2)  {
		s1.put(X,A);
		s2.put(X,B);
		
		Substitution aggregation = s1.aggregate(s2);
		Assert.assertNull(aggregation);
	}
	
	@Theory
	public void aggregateImpossibleTest2(Substitution s1, Substitution s2)  {
		s1.put(X,Z);
		s1.put(Y,Z);
		
		s2.put(X,A);
		s2.put(Y,B);
		
		Substitution aggregation = s1.aggregate(s2);
		Assert.assertNull(aggregation);
	}
	
	@Theory
	public void hashCode1(Substitution s1, Substitution s2)  {
		s1.put(X,Z);
		s2.put(X,Z);
		
		s1.put(Y,Z);
		s2.put(Y,Z);
		
		Assert.assertEquals(s1.hashCode(), s2.hashCode());
	}
	
	
	@Theory
	public void hashCode2(Substitution s1, Substitution s2)  {
		s1.put(X,A);
		s2.put(X,A);
		
		s1.put(Y,B);
		s2.put(Y,B);
		
		Assert.assertEquals(s1.hashCode(), s2.hashCode());
	}
	
	@Theory
	public void hashCode3(Substitution s1, Substitution s2)  {
		s1.put(X,A);
		s2.put(X,A);
		
		s1.put(Y,B);
		s2.put(Y,B);
		
		s1.put(Z,C);
		s2.put(Z,C);
		
		Assert.assertEquals(s1.hashCode(), s2.hashCode());
	}
	
	@Theory
	public void hashCodeDiff1(Substitution s1, Substitution s2)  {
		s1.put(X,Z);
		s2.put(Z,X);

		Assert.assertNotEquals(s1.hashCode(), s2.hashCode());
	}
	
	@Theory
	public void hashCodeDiff2(Substitution s1, Substitution s2)  {
		s1.put(X,Z);
		s2.put(X,A);
		
		s1.put(Y,A);
		s2.put(Y,Z);
		
		Assert.assertNotEquals(s1.hashCode(), s2.hashCode());
	}
	
	@Theory
	public void hashCode4(Substitution s1, Substitution s2)  {
		s1.put(X,Z);
		s1.put(Y,Z);
		
		s2.put(Y,Z);
		s2.put(X,Z);
		
		Assert.assertEquals(s1.hashCode(), s2.hashCode());
	}
	
}
