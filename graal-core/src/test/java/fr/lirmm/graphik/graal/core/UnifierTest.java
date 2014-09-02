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
	
	private static final Term x = new Term("X", Term.Type.VARIABLE);
	private static final Term y = new Term("Y", Term.Type.VARIABLE);
	private static final Term z = new Term("Z", Term.Type.VARIABLE);
	private static final Term u = new Term("U", Term.Type.VARIABLE);
	private static final Term v = new Term("V", Term.Type.VARIABLE);
	private static final Term w = new Term("w", Term.Type.VARIABLE);
	
	private static final Term a = new Term("a", Term.Type.CONSTANT);
	private static final Term b = new Term("b", Term.Type.CONSTANT);
	
	private static Atom p_xy, p_yz, p_uv, p_vw, p_au, p_xb, q_x;
	static {
		Term[] terms = new Term[2];
		terms[0] = x;
		terms[1] = y;
		p_xy = new DefaultAtom(p, Arrays.asList(terms));
		
		terms = new Term[2];
		terms[0] = y;
		terms[1] = z;
		p_yz = new DefaultAtom(p, Arrays.asList(terms));
		
		terms = new Term[2];
		terms[0] = u;
		terms[1] = v;
		p_uv = new DefaultAtom(p, Arrays.asList(terms));
		
		terms = new Term[2];
		terms[0] = v;
		terms[1] = w;
		p_vw = new DefaultAtom(p, Arrays.asList(terms));
		
		terms = new Term[2];
		terms[0] = a;
		terms[1] = u;
		p_au = new DefaultAtom(p, Arrays.asList(terms));
		
		terms = new Term[2];
		terms[0] = x;
		terms[1] = b;
		p_xb = new DefaultAtom(p, Arrays.asList(terms));
		
		terms = new Term[1];
		terms[0] = x;
		q_x = new DefaultAtom(p, Arrays.asList(terms));
	}
	
	@Test
	public void pieceUnifierTest1() {
		Rule rule = RuleFactory.getInstance().createRule();
		rule.getBody().add(q_x);
		rule.getHead().add(p_xy);
		rule.getHead().add(p_yz);
		
		AtomSet atomset = AtomSetFactory.getInstance().createAtomSet();
		atomset.add(p_uv);
		atomset.add(p_vw);
		
		Collection<Substitution> unifiers = Unifier.computePieceUnifier(rule, atomset);
		Assert.assertEquals(2, unifiers.size());
	}
	
	@Test
	public void pieceUnifierTest2() {
		Rule rule = RuleFactory.getInstance().createRule();
		rule.getBody().add(q_x);
		rule.getHead().add(p_xb);
		
		AtomSet atomset = AtomSetFactory.getInstance().createAtomSet();
		atomset.add(p_au);
		
		Collection<Substitution> unifiers = Unifier.computePieceUnifier(rule, atomset);
		Assert.assertEquals(1, unifiers.size());
	}

}
