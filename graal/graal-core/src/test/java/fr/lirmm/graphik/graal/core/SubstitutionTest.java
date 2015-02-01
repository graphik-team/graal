/**
 * 
 */
package fr.lirmm.graphik.graal.core;

import org.junit.Assert;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import fr.lirmm.graphik.graal.core.HashMapSubstitution;
import fr.lirmm.graphik.graal.core.Substitution;
import fr.lirmm.graphik.graal.core.Term;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
@RunWith(Theories.class)
public class SubstitutionTest {
	
	private static final Term X = new Term("X", Term.Type.VARIABLE);
	private static final Term Y = new Term("Y", Term.Type.VARIABLE);
	private static final Term Z = new Term("Z", Term.Type.VARIABLE);
	
	private static final Term A = new Term("a", Term.Type.CONSTANT);
	private static final Term B = new Term("b", Term.Type.CONSTANT);
	
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

		Assert.assertEquals(A, composition.getSubstitute(X));
		Assert.assertEquals(A, composition.getSubstitute(Y));
		Assert.assertEquals(A, composition.getSubstitute(Z));
	}
	
	@Theory
	public void aggregateTest1Inverse(Substitution s1, Substitution s2)  {
		s1.put(X,Y);
		s1.put(Y,Z);
		
		s2.put(Z,A);
		s2.put(Y,A);
		
		Substitution composition = s1.compose(s2);
		Assert.assertNotNull(composition);
		
		Assert.assertEquals(A, composition.getSubstitute(X));
		Assert.assertEquals(A, composition.getSubstitute(Y));
		Assert.assertEquals(A, composition.getSubstitute(Z));
	}

	@Theory
	public void aggregateTest2(Substitution s1, Substitution s2) {
		s1.put(Y,A);
		s1.put(Z,A);
		
		s2.put(Y,X);
		s2.put(Z,X);
		
		Substitution composition = s1.compose(s2);
		Assert.assertNotNull(composition);
		
		Assert.assertEquals(A, composition.getSubstitute(X));
		Assert.assertEquals(A, composition.getSubstitute(Y));
		Assert.assertEquals(A, composition.getSubstitute(Z));
	}
	
	@Theory
	public void aggregateTest2Inverse(Substitution s1, Substitution s2)  {
		s1.put(Y,X);
		s1.put(Z,X);
		
		s2.put(Y,A);
		s2.put(Z,A);
		
		Substitution composition = s1.compose(s2);
		Assert.assertNotNull(composition);
		
		Assert.assertEquals(A, composition.getSubstitute(X));
		Assert.assertEquals(A, composition.getSubstitute(Y));
		Assert.assertEquals(A, composition.getSubstitute(Z));
	}
	
	@Theory
	public void aggregateTest3(Substitution s1)  {
		s1.put(X,A);
		s1.compose(X,Y);
		
		Assert.assertNotNull(s1);
		
		Assert.assertEquals(A, s1.getSubstitute(X));
		Assert.assertEquals(A, s1.getSubstitute(Y));
	}
	
	@Theory
	public void aggregateImpossible(Substitution s1, Substitution s2)  {
		s1.put(X,A);
		s2.put(X,B);
		
		Substitution composition = s1.compose(s2);
		Assert.assertNull(composition);
	}
	
}
