/**
 * 
 */
package fr.lirmm.graphik.kb;

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
	
	private static final Term a = new Term("a", Term.Type.CONSTANT);
	private static final Term b = new Term("b", Term.Type.CONSTANT);
	
	@DataPoints
	public static Substitution[] substitution() {
		return new Substitution[] {new HashMapSubstitution()};
	}

	@Theory
	public void aggregateTest1(Substitution s1, Substitution s2) {
		s1.put(Y,a);
		s1.put(Z,a);
		
		s2.put(X,Y);
		s2.put(Y,Z);
		
		Substitution composition = s1.compose(s2);
		Assert.assertNotNull(composition);

		Assert.assertEquals(a, composition.getSubstitut(X));
		Assert.assertEquals(a, composition.getSubstitut(Y));
		Assert.assertEquals(a, composition.getSubstitut(Z));
	}
	
	@Theory
	public void aggregateTest1Inverse(Substitution s1, Substitution s2)  {
		s1.put(X,Y);
		s1.put(Y,Z);
		
		s2.put(Z,a);
		s2.put(Y,a);
		
		Substitution composition = s1.compose(s2);
		Assert.assertNotNull(composition);
		
		Assert.assertEquals(a, composition.getSubstitut(X));
		Assert.assertEquals(a, composition.getSubstitut(Y));
		Assert.assertEquals(a, composition.getSubstitut(Z));
	}

	@Theory
	public void aggregateTest2(Substitution s1, Substitution s2) {
		s1.put(Y,a);
		s1.put(Z,a);
		
		s2.put(Y,X);
		s2.put(Z,X);
		
		Substitution composition = s1.compose(s2);
		Assert.assertNotNull(composition);
		
		Assert.assertEquals(a, composition.getSubstitut(X));
		Assert.assertEquals(a, composition.getSubstitut(Y));
		Assert.assertEquals(a, composition.getSubstitut(Z));
	}
	
	@Theory
	public void aggregateTest2Inverse(Substitution s1, Substitution s2)  {
		s1.put(Y,X);
		s1.put(Z,X);
		
		s2.put(Y,a);
		s2.put(Z,a);
		
		Substitution composition = s1.compose(s2);
		Assert.assertNotNull(composition);
		
		Assert.assertEquals(a, composition.getSubstitut(X));
		Assert.assertEquals(a, composition.getSubstitut(Y));
		Assert.assertEquals(a, composition.getSubstitut(Z));
	}
	
	@Theory
	public void aggregateImpossible(Substitution s1, Substitution s2)  {
		s1.put(X,a);
		s2.put(X,b);
		
		Substitution composition = s1.compose(s2);
		Assert.assertNull(composition);
	}
	
}
