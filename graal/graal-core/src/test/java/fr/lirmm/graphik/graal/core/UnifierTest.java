/**
 * 
 */
package fr.lirmm.graphik.graal.core;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.factory.AtomSetFactory;
import fr.lirmm.graphik.graal.core.factory.RuleFactory;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class UnifierTest {
	
	private static Predicate p = new Predicate("p", 2);
	private static Predicate q = new Predicate("q", 1);
	
	private static final Term X = new Term("X", Term.Type.VARIABLE);
	private static final Term Y = new Term("Y", Term.Type.VARIABLE);
	private static final Term Z = new Term("Z", Term.Type.VARIABLE);
	private static final Term U = new Term("U", Term.Type.VARIABLE);
	private static final Term V = new Term("V", Term.Type.VARIABLE);
	private static final Term W = new Term("w", Term.Type.VARIABLE);
	
	private static final Term A = new Term("a", Term.Type.CONSTANT);
	private static final Term B = new Term("b", Term.Type.CONSTANT);
	
	private static Atom pXY, pYZ, pUV, pVW, pAU, pXA, pXB, qX;
	static {
		Term[] terms = new Term[2];
		terms[0] = X;
		terms[1] = Y;
		pXY = new DefaultAtom(p, Arrays.asList(terms));
		
		terms = new Term[2];
		terms[0] = Y;
		terms[1] = Z;
		pYZ = new DefaultAtom(p, Arrays.asList(terms));
		
		terms = new Term[2];
		terms[0] = U;
		terms[1] = V;
		pUV = new DefaultAtom(p, Arrays.asList(terms));
		
		terms = new Term[2];
		terms[0] = V;
		terms[1] = W;
		pVW = new DefaultAtom(p, Arrays.asList(terms));
		
		terms = new Term[2];
		terms[0] = A;
		terms[1] = U;
		pAU = new DefaultAtom(p, Arrays.asList(terms));

		terms = new Term[2];
		terms[0] = X;
		terms[1] = A;
		pXA = new DefaultAtom(p, Arrays.asList(terms));
		
		terms = new Term[2];
		terms[0] = X;
		terms[1] = B;
		pXB = new DefaultAtom(p, Arrays.asList(terms));
		
		terms = new Term[1];
		terms[0] = X;
		qX = new DefaultAtom(q, Arrays.asList(terms));
	}
	
	@Test
	public void pieceUnifierTest1() {
		Rule rule = RuleFactory.getInstance().createRule();
		rule.getBody().add(qX);
		rule.getHead().add(pXY);
		rule.getHead().add(pYZ);
		
		AtomSet atomset = AtomSetFactory.getInstance().createAtomSet();
		atomset.add(pUV);
		atomset.add(pVW);
		
		Collection<Substitution> unifiers = Unifier.getInstance().computePieceUnifier(rule, atomset);
		Assert.assertEquals(2, unifiers.size());
	}
	
	@Test
	public void pieceUnifierTest2() {
		Rule rule = RuleFactory.getInstance().createRule();
		rule.getBody().add(qX);
		rule.getHead().add(pXB);
		
		AtomSet atomset = AtomSetFactory.getInstance().createAtomSet();
		atomset.add(pAU);
		
		Collection<Substitution> unifiers = Unifier.getInstance().computePieceUnifier(rule, atomset);
		Assert.assertEquals(1, unifiers.size());
	}

	@Test
	public void constantUnification() {
		Rule rule = RuleFactory.getInstance().createRule();
		rule.getBody().add(qX);
		rule.getHead().add(pXB);

		AtomSet atomset = AtomSetFactory.getInstance().createAtomSet();
		atomset.add(pXA);

		Collection<Substitution> unifiers = Unifier.getInstance().computePieceUnifier(rule,
				atomset);
		Assert.assertEquals(0, unifiers.size());
	}

}
