/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2016)
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

import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.core.factory.AtomSetFactory;
import fr.lirmm.graphik.graal.core.factory.DefaultRuleFactory;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class UnifierTest {
	
	/**
	 * Given s(X) -> p(X,Y), p(Y,Z). and p(U,V),p(V,W). computePieceUnifier
	 * should return {X->U, Y->V, Z->W}
	 */
	@Test
	public void pieceUnifierTest1() {
		Rule rule = DefaultRuleFactory.instance().create();
		rule.getBody().add(TestUtils.sX);
		rule.getHead().add(TestUtils.pXY);
		rule.getHead().add(TestUtils.pYZ);
		
		InMemoryAtomSet atomset = AtomSetFactory.instance().create();
		atomset.add(TestUtils.pUV);
		atomset.add(TestUtils.pVW);
		
		Collection<Substitution> unifiers = Unifier.instance().computePieceUnifier(rule, atomset);
		Assert.assertEquals(2, unifiers.size());
	}
	
	@Test
	public void pieceUnifierTest2() {
		Rule rule = DefaultRuleFactory.instance().create();
		rule.getBody().add(TestUtils.sX);
		rule.getHead().add(TestUtils.pXB);
		
		InMemoryAtomSet atomset = AtomSetFactory.instance().create();
		atomset.add(TestUtils.pAU);
		
		Collection<Substitution> unifiers = Unifier.instance().computePieceUnifier(rule, atomset);
		Assert.assertEquals(1, unifiers.size());
	}

	@Test
	public void constantUnification() {
		Rule rule = DefaultRuleFactory.instance().create();
		rule.getBody().add(TestUtils.sX);
		rule.getHead().add(TestUtils.pXB);

		InMemoryAtomSet atomset = AtomSetFactory.instance().create();
		atomset.add(TestUtils.pXA);

		Collection<Substitution> unifiers = Unifier.instance().computePieceUnifier(rule,
				atomset);
		Assert.assertEquals(0, unifiers.size());
	}

	@Test
	public void unificationExistentialWithFrontier() {
		Rule rule = DefaultRuleFactory.instance().create();
		rule.getBody().add(TestUtils.sX);
		rule.getHead().add(TestUtils.pXY);

		InMemoryAtomSet atomset = AtomSetFactory.instance().create();
		atomset.add(TestUtils.pUU);

		Collection<Substitution> unifiers = Unifier.instance().computePieceUnifier(rule, atomset);
		Assert.assertEquals(0, unifiers.size());
	}

	@Test
	public void pieceUnifierTest3() {
		Rule rule = DefaultRuleFactory.instance().create();
		rule.getBody().add(TestUtils.sX);
		rule.getHead().add(TestUtils.pXY);
		rule.getHead().add(TestUtils.pYZ);

		InMemoryAtomSet atomset = AtomSetFactory.instance().create();
		atomset.add(TestUtils.pTU);
		atomset.add(TestUtils.pUV);
		atomset.add(TestUtils.pVW);
		atomset.add(TestUtils.pWX);

		Collection<Substitution> unifiers = Unifier.instance().computePieceUnifier(rule, atomset);
		Assert.assertEquals(2, unifiers.size());
	}

	@Test
	public void example33MelanieThesis() {
		Rule rule = DefaultRuleFactory.instance().create();
		rule.getBody().add(TestUtils.sX);
		rule.getHead().add(TestUtils.pXY);

		InMemoryAtomSet atomset = AtomSetFactory.instance().create();
		atomset.add(TestUtils.pUV);
		atomset.add(TestUtils.pWV);
		atomset.add(TestUtils.pWT);
		atomset.add(TestUtils.pUW);

		Collection<Substitution> unifiers = Unifier.instance().computePieceUnifier(rule, atomset);
		Assert.assertEquals(2, unifiers.size());
	}

	@Test
	public void example35MelanieThesis() {
		Rule rule = DefaultRuleFactory.instance().create();
		rule.getBody().add(TestUtils.pXY);
		rule.getHead().add(TestUtils.qXY);

		InMemoryAtomSet atomset = AtomSetFactory.instance().create();
		atomset.add(TestUtils.qUV);
		atomset.add(TestUtils.pVW);
		atomset.add(TestUtils.qTW);

		Collection<Substitution> unifiers = Unifier.instance().computePieceUnifier(rule, atomset);
		print(unifiers);
		Assert.assertEquals(2, unifiers.size());
	}

	private void print(Collection<Substitution> list) {
		for (Substitution s : list) {
			System.out.println(s);
		}
	}
}
