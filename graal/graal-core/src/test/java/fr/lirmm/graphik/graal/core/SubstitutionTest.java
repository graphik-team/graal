/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2015)
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
