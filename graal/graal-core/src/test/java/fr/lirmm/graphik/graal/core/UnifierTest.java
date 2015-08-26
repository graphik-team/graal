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

import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

import fr.lirmm.graphik.graal.core.atomset.InMemoryAtomSet;
import fr.lirmm.graphik.graal.core.factory.AtomSetFactory;
import fr.lirmm.graphik.graal.core.factory.RuleFactory;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.core.term.Term;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class UnifierTest {
	
	private static Predicate p = new Predicate("p", 2);
	private static Predicate q = new Predicate("q", 1);
	
	private static final Term X = DefaultTermFactory.instance().createVariable(
			"X");
	private static final Term Y = DefaultTermFactory.instance().createVariable(
			"Y");
	private static final Term Z = DefaultTermFactory.instance().createVariable(
			"Z");
	private static final Term U = DefaultTermFactory.instance().createVariable(
			"U");
	private static final Term V = DefaultTermFactory.instance().createVariable(
			"V");
	private static final Term W = DefaultTermFactory.instance().createVariable(
			"w");
	
	private static final Term A = DefaultTermFactory.instance().createConstant(
			"a");
	private static final Term B = DefaultTermFactory.instance().createConstant(
			"b");
	
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
		Rule rule = RuleFactory.instance().create();
		rule.getBody().add(qX);
		rule.getHead().add(pXY);
		rule.getHead().add(pYZ);
		
		InMemoryAtomSet atomset = AtomSetFactory.getInstance().createAtomSet();
		atomset.add(pUV);
		atomset.add(pVW);
		
		Collection<Substitution> unifiers = Unifier.instance().computePieceUnifier(rule, atomset);
		Assert.assertEquals(2, unifiers.size());
	}
	
	@Test
	public void pieceUnifierTest2() {
		Rule rule = RuleFactory.instance().create();
		rule.getBody().add(qX);
		rule.getHead().add(pXB);
		
		InMemoryAtomSet atomset = AtomSetFactory.getInstance().createAtomSet();
		atomset.add(pAU);
		
		Collection<Substitution> unifiers = Unifier.instance().computePieceUnifier(rule, atomset);
		Assert.assertEquals(1, unifiers.size());
	}

	@Test
	public void constantUnification() {
		Rule rule = RuleFactory.instance().create();
		rule.getBody().add(qX);
		rule.getHead().add(pXB);

		InMemoryAtomSet atomset = AtomSetFactory.getInstance().createAtomSet();
		atomset.add(pXA);

		Collection<Substitution> unifiers = Unifier.instance().computePieceUnifier(rule,
				atomset);
		Assert.assertEquals(0, unifiers.size());
	}

}
