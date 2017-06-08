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
package fr.lirmm.graphik.graal.core.ruleset;

import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import fr.lirmm.graphik.graal.api.core.Ontology;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.core.DefaultRule;
import fr.lirmm.graphik.graal.core.TestUtils;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class DefaultOntologyTest {
	
	private Rule r1 = new DefaultRule("r1", new LinkedListAtomSet(), new LinkedListAtomSet());
	private Rule same1 = new DefaultRule("same", new LinkedListAtomSet(TestUtils.pAB), new LinkedListAtomSet());
	private Rule same2 = new DefaultRule("same", new LinkedListAtomSet(TestUtils.pBA), new LinkedListAtomSet());
	private Rule noName = new DefaultRule(new LinkedListAtomSet(), new LinkedListAtomSet());

	// /////////////////////////////////////////////////////////////////////////
	// EMPTY ONTO CASE
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Test method for
	 * {@link fr.lirmm.graphik.graal.core.ruleset.DefaultOntology#getRuleNames()}.
	 */
	@Test
	public void testGetRuleNames_empty() {
		// given
		Ontology onto = new DefaultOntology();
		// when
		Set<String> ruleNames = onto.getRuleNames();
		// then
		Assert.assertTrue(ruleNames.isEmpty());
	}

	/**
	 * Test method for
	 * {@link fr.lirmm.graphik.graal.core.ruleset.DefaultOntology#size()}.
	 */
	@Test
	public void testSize_empty() {
		// given
		Ontology onto = new DefaultOntology();
		// when
		int size = onto.size();
		// then
		Assert.assertEquals(0, size);
	}

	/**
	 * Test method for
	 * {@link fr.lirmm.graphik.graal.core.ruleset.DefaultOntology#iterator()}.
	 */
	@Test
	public void testIterator_empty() {
		// given
		Ontology onto = new DefaultOntology();
		// when
		Iterator<Rule> it = onto.iterator();
		// then
		Assert.assertFalse(it.hasNext());
	}

	// /////////////////////////////////////////////////////////////////////////
	// 
	// /////////////////////////////////////////////////////////////////////////
	
	/**
	 * Test method for
	 * {@link fr.lirmm.graphik.graal.core.ruleset.DefaultOntology#getRuleNames()}.
	 */
	@Test
	public void testGetRuleNames() {
		// given
		Ontology onto = new DefaultOntology();
		onto.add(r1);
		onto.add(noName);
		// when
		Set<String> ruleNames = onto.getRuleNames();
		// then
		Assert.assertTrue(ruleNames.contains(r1.getLabel()));
		Assert.assertEquals(2, ruleNames.size());
	}
	
	/**
	 * Test method for
	 * {@link fr.lirmm.graphik.graal.core.ruleset.DefaultOntology#add(Rule)}.
	 */
	@Test
	public void testAddDifferentRulesWithSameName() {
		// given
		Ontology onto = new DefaultOntology();
	
		// when
		onto.add(same1);
		onto.add(same2);
		
		// then
		Assert.assertEquals(2, onto.size());
	}
	
	/**
	 * Test method for
	 * {@link fr.lirmm.graphik.graal.core.ruleset.DefaultOntology#add(Rule)}.
	 */
	@Test
	public void testAddSameRuleTwice() {
		// given
		Ontology onto = new DefaultOntology();
	
		// when
		onto.add(same1);
		onto.add(same1);
		
		// then
		Assert.assertEquals(1, onto.size());
	}

	/**
	 * Test method for
	 * {@link fr.lirmm.graphik.graal.core.ruleset.DefaultOntology#remove(fr.lirmm.graphik.graal.api.core.Rule)}.
	 */
	@Test
	public void testRemove() {
		// given
		Ontology onto = new DefaultOntology();
		onto.add(r1);
		onto.remove(r1);
		// when
		Set<String> ruleNames = onto.getRuleNames();
		// then
		Assert.assertTrue(ruleNames.isEmpty());
	}

	/**
	 * Test method for
	 * {@link fr.lirmm.graphik.graal.core.ruleset.DefaultOntology#contains(fr.lirmm.graphik.graal.api.core.Rule)}.
	 */
	@Test
	public void testContains() {
		// given
		Ontology onto = new DefaultOntology();
		onto.add(r1);
		// when
		boolean b = onto.contains(r1);
		// then
		Assert.assertTrue(b);
	}
	
	/**
	 * Test method for
	 * {@link fr.lirmm.graphik.graal.core.ruleset.DefaultOntology#contains(fr.lirmm.graphik.graal.api.core.Rule)}.
	 */
	@Test
	public void testContainsSameRuleLabel() {
		// given
		Ontology onto = new DefaultOntology();
		onto.add(same1);
		// when
		boolean b = onto.contains(same2);
		// then
		Assert.assertFalse(b);
	}

	/**
	 * Test method for
	 * {@link fr.lirmm.graphik.graal.core.ruleset.DefaultOntology#size()}.
	 */
	@Test
	public void testSize() {
		// given
		Ontology onto = new DefaultOntology();
		onto.add(r1);
		onto.add(noName);
		onto.add(r1);
		// when
		int size = onto.size();
		// then
		Assert.assertEquals(2, size);
	}

	/**
	 * Test method for
	 * {@link fr.lirmm.graphik.graal.core.ruleset.DefaultOntology#iterator()}.
	 */
	@Test
	public void testIterator() {
		boolean isThereR1 = false;
		boolean isThereNoName = false;
		// given
		Ontology onto = new DefaultOntology();
		onto.add(r1);
		onto.add(noName);
		// when
		Iterator<Rule> it = onto.iterator();
		while(it.hasNext()) {
			Rule r = it.next();
			if(r == r1 && !isThereR1) {
				isThereR1 = true;
			} else if(r == noName && !isThereNoName) {
				isThereNoName = true;
			} else {
				fail("Not wanted rule: " + r);
			}
		}
		// then
		Assert.assertTrue("isThereR1",isThereR1);
		Assert.assertTrue("isThereNoName",isThereNoName);
	}

}
