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

import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

import fr.lirmm.graphik.graal.core.atomset.InMemoryAtomSet;
import fr.lirmm.graphik.graal.core.factory.AtomSetFactory;
import fr.lirmm.graphik.graal.core.factory.RuleFactory;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class UnifierTest {
	
	@Test
	public void pieceUnifierTest1() {
		Rule rule = RuleFactory.instance().create();
		rule.getBody().add(TestUtils.qX);
		rule.getHead().add(TestUtils.pXY);
		rule.getHead().add(TestUtils.pYZ);
		
		InMemoryAtomSet atomset = AtomSetFactory.getInstance().createAtomSet();
		atomset.add(TestUtils.pUV);
		atomset.add(TestUtils.pVW);
		
		Collection<Substitution> unifiers = Unifier.instance().computePieceUnifier(rule, atomset);
		Assert.assertEquals(2, unifiers.size());
	}
	
	@Test
	public void pieceUnifierTest2() {
		Rule rule = RuleFactory.instance().create();
		rule.getBody().add(TestUtils.qX);
		rule.getHead().add(TestUtils.pXB);
		
		InMemoryAtomSet atomset = AtomSetFactory.getInstance().createAtomSet();
		atomset.add(TestUtils.pAU);
		
		Collection<Substitution> unifiers = Unifier.instance().computePieceUnifier(rule, atomset);
		Assert.assertEquals(1, unifiers.size());
	}

	@Test
	public void constantUnification() {
		Rule rule = RuleFactory.instance().create();
		rule.getBody().add(TestUtils.qX);
		rule.getHead().add(TestUtils.pXB);

		InMemoryAtomSet atomset = AtomSetFactory.getInstance().createAtomSet();
		atomset.add(TestUtils.pXA);

		Collection<Substitution> unifiers = Unifier.instance().computePieceUnifier(rule,
				atomset);
		Assert.assertEquals(0, unifiers.size());
	}

}
