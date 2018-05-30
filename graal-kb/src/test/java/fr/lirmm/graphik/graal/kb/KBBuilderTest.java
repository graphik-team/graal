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
package fr.lirmm.graphik.graal.kb;

import org.junit.Assert;
import org.junit.Test;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.Ontology;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.mapper.Mapper;
import fr.lirmm.graphik.graal.api.io.ParseException;
import fr.lirmm.graphik.graal.api.io.Parser;
import fr.lirmm.graphik.graal.api.kb.Approach;
import fr.lirmm.graphik.graal.api.kb.KnowledgeBase;
import fr.lirmm.graphik.graal.api.store.Store;
import fr.lirmm.graphik.graal.core.atomset.graph.DefaultInMemoryGraphStore;
import fr.lirmm.graphik.graal.core.mapper.PrefixMapper;
import fr.lirmm.graphik.graal.core.ruleset.DefaultOntology;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class KBBuilderTest {

	/**
	 * Test method for {@link fr.lirmm.graphik.graal.kb.KBBuilder#setStore(fr.lirmm.graphik.graal.api.store.Store)}.
	 */
	@Test
	public void testSetStore() {
		// Given
		KBBuilder kbb = new KBBuilder();
		Store store = new DefaultInMemoryGraphStore();
		
		// When
		kbb.setStore(store);
		KnowledgeBase kb = kbb.build();
		
		// Then
		Assert.assertTrue(kb.getFacts() == store);
	}

	/**
	 * Test method for {@link fr.lirmm.graphik.graal.kb.KBBuilder#setOntology(fr.lirmm.graphik.graal.api.core.RuleSet)}.
	 * @throws ParseException 
	 */
	@Test
	public void testSetOntology() throws ParseException {
		// Given
		KBBuilder kbb = new KBBuilder();
		Ontology ontology = new DefaultOntology();
		Rule r1 = DlgpParser.parseRule("[R1] p(X) :- q(X).");
		ontology.add(r1);
		
		// When
		kbb.setOntology(ontology);
		KnowledgeBase kb = kbb.build();
		
		// Then
		Assert.assertEquals(r1, kb.getRule("R1"));
		Assert.assertEquals(1, kb.getOntology().size());
	}
	
	/**
	 * Test method for {@link fr.lirmm.graphik.graal.kb.KBBuilder#addAll(fr.lirmm.graphik.util.stream.CloseableIterator)}.
	 * @throws KBBuilderException 
	 * @throws ParseException 
	 * @throws AtomSetException 
	 */
	@Test
	public void testAddAll() throws KBBuilderException, ParseException, AtomSetException {
		// Given
		KBBuilder kbb = new KBBuilder();
		Parser<Object> parser = new DlgpParser("[R1] p(X) :- q(X). p(a).");
		
		// When
		kbb.addAll(parser);
		KnowledgeBase kb = kbb.build();
		
		// Then
		Assert.assertEquals(DlgpParser.parseRule("[R1] p(X) :- q(X)."), kb.getRule("R1"));
		Assert.assertTrue(kb.getFacts().contains(DlgpParser.parseAtom("p(a).")));
	}

	/**
	 * Test method for {@link fr.lirmm.graphik.graal.kb.KBBuilder#add(fr.lirmm.graphik.graal.api.core.Rule)}.
	 * @throws ParseException 
	 * @throws KBBuilderException 
	 */
	@Test
	public void testAddRule() throws ParseException, KBBuilderException {
		// Given
		KBBuilder kbb = new KBBuilder();
		Rule r1 = DlgpParser.parseRule("[R1] p(X) :- q(X).");

		// When
		kbb.add(r1);
		KnowledgeBase kb = kbb.build();

		// Then
		Assert.assertEquals(r1, kb.getRule("R1"));
		Assert.assertEquals(1, kb.getOntology().size());
	}

	/**
	 * Test method for {@link fr.lirmm.graphik.graal.kb.KBBuilder#add(fr.lirmm.graphik.graal.api.core.Rule, fr.lirmm.graphik.graal.api.core.mapper.Mapper)}.
	 * @throws ParseException 
	 * @throws AtomSetException 
	 * @throws KBBuilderException 
	 */
	@Test
	public void testAddRuleMapper() throws ParseException, AtomSetException, KBBuilderException {		
		// Given
		KBBuilder kbb = new KBBuilder();
		Mapper mapper = new PrefixMapper("graphik#");
		Rule r1 = DlgpParser.parseRule("[R1] p(X) :- q(X).");
		
		// When
		kbb.add(r1, mapper);
		KnowledgeBase kb = kbb.build();
		
		// Then
		Assert.assertEquals(DlgpParser.parseRule("[R1] <graphik#p>(X) :- <graphik#q>(X)."), kb.getRule("R1"));
		Assert.assertEquals(1, kb.getOntology().size());
	}

	/**
	 * Test method for {@link fr.lirmm.graphik.graal.kb.KBBuilder#addRules(fr.lirmm.graphik.util.stream.CloseableIterator)}.
	 * @throws AtomSetException 
	 * @throws ParseException 
	 * @throws KBBuilderException 
	 */
	@Test
	public void testAddRulesCloseableIteratorOfObject() throws ParseException, AtomSetException, KBBuilderException {
		// Given
		KBBuilder kbb = new KBBuilder();
		Parser<Object> parser = new DlgpParser("[R1] p(X) :- q(X). p(a).");
		
		// When
		kbb.addRules(parser);
		KnowledgeBase kb = kbb.build();
		
		// Then
		Assert.assertEquals(DlgpParser.parseRule("[R1] p(X) :- q(X)."), kb.getRule("R1"));
		Assert.assertEquals(1, kb.getOntology().size());
		Assert.assertFalse(kb.getFacts().contains(DlgpParser.parseAtom("p(a).")));	
	}

	/**
	 * Test method for {@link fr.lirmm.graphik.graal.kb.KBBuilder#addRules(fr.lirmm.graphik.util.stream.CloseableIterator, fr.lirmm.graphik.graal.api.core.mapper.Mapper)}.
	 * @throws KBBuilderException 
	 * @throws ParseException 
	 * @throws AtomSetException 
	 */
	@Test
	public void testAddRulesCloseableIteratorOfObjectMapper() throws KBBuilderException, ParseException, AtomSetException {
		// Given
		KBBuilder kbb = new KBBuilder();
		Parser<Object> parser = new DlgpParser("[R1] p(X) :- q(X). p(a).");
		Mapper mapper = new PrefixMapper("graphik#");

		// When
		kbb.addRules(parser, mapper);
		KnowledgeBase kb = kbb.build();

		// Then
		Assert.assertEquals(DlgpParser.parseRule("[R1] <graphik#p>(X) :- <graphik#q>(X)."), kb.getRule("R1"));
		Assert.assertFalse(kb.getFacts().contains(DlgpParser.parseAtom("p(a).")));
	}

	/**
	 * Test method for {@link fr.lirmm.graphik.graal.kb.KBBuilder#add(fr.lirmm.graphik.graal.api.core.Atom)}.
	 * @throws ParseException 
	 * @throws KBBuilderException 
	 * @throws AtomSetException 
	 */
	@Test
	public void testAddAtom() throws ParseException, KBBuilderException, AtomSetException {
		// Given
		KBBuilder kbb = new KBBuilder();
		Atom a = DlgpParser.parseAtom("p(a).");

		// When
		kbb.add(a);
		KnowledgeBase kb = kbb.build();

		// Then
		Assert.assertTrue(kb.getFacts().contains(a));
	}

	/**
	 * Test method for {@link fr.lirmm.graphik.graal.kb.KBBuilder#add(fr.lirmm.graphik.graal.api.core.Atom, fr.lirmm.graphik.graal.api.core.mapper.Mapper)}.
	 * @throws ParseException 
	 * @throws KBBuilderException 
	 * @throws AtomSetException 
	 */
	@Test
	public void testAddAtomMapper() throws ParseException, KBBuilderException, AtomSetException {
		// Given
		KBBuilder kbb = new KBBuilder();
		Atom a = DlgpParser.parseAtom("p(a).");
		Mapper mapper = new PrefixMapper("graphik#");

		// When
		kbb.add(a, mapper);
		KnowledgeBase kb = kbb.build();

		// Then
		Assert.assertTrue(kb.getFacts().contains(DlgpParser.parseAtom("<graphik#p>(a).")));
	}

	/**
	 * Test method for {@link fr.lirmm.graphik.graal.kb.KBBuilder#addAtoms(fr.lirmm.graphik.util.stream.CloseableIterator)}.
	 * @throws KBBuilderException 
	 * @throws AtomSetException 
	 * @throws ParseException 
	 */
	@Test
	public void testAddAtomsCloseableIteratorOfObject() throws KBBuilderException, ParseException, AtomSetException {
		// Given
		KBBuilder kbb = new KBBuilder();
		Parser<Object> parser = new DlgpParser("[R1] p(X) :- q(X). p(a).");

		// When
		kbb.addAtoms(parser);
		KnowledgeBase kb = kbb.build();

		// Then
		Assert.assertTrue(kb.getFacts().contains(DlgpParser.parseAtom("p(a).")));
		Assert.assertEquals(0, kb.getOntology().size());

	}

	/**
	 * Test method for {@link fr.lirmm.graphik.graal.kb.KBBuilder#addAtoms(fr.lirmm.graphik.util.stream.CloseableIterator, fr.lirmm.graphik.graal.api.core.mapper.Mapper)}.
	 * @throws KBBuilderException 
	 * @throws AtomSetException 
	 * @throws ParseException 
	 */
	@Test
	public void testAddAtomsCloseableIteratorOfObjectMapper() throws KBBuilderException, ParseException, AtomSetException {
		// Given
		KBBuilder kbb = new KBBuilder();
		Parser<Object> parser = new DlgpParser("[R1] p(X) :- q(X). p(a).");
		Mapper mapper = new PrefixMapper("graphik#");

		// When
		kbb.addAtoms(parser, mapper);
		KnowledgeBase kb = kbb.build();

		// Then
		Assert.assertTrue(kb.getFacts().contains(DlgpParser.parseAtom("<graphik#p>(a).")));
		Assert.assertEquals(0, kb.getOntology().size());
	}

	/**
	 * Test method for {@link fr.lirmm.graphik.graal.kb.KBBuilder#setApproach(fr.lirmm.graphik.graal.api.kb.Approach)}.
	 */
	@Test
	public void testSetApproach() {
		// Given
		KBBuilder kbb = new KBBuilder();
		kbb.setApproach(Approach.SATURATION_ONLY);
		
		// When
		KnowledgeBase kb = kbb.build();

		// Then
		Assert.assertEquals(Approach.SATURATION_ONLY, kb.getApproach());
	}

	
	/**
	 * @throws KBBuilderException 
	 * 
	 */
	@Test(expected = KBBuilderException.class)
	public void testRuleWithEqualityInHeadBehavior() throws ParseException, KBBuilderException {
		KBBuilder kbb = new KBBuilder();
		kbb.addAll(new DlgpParser("X=Y :- p(X,Y)."));
		kbb.build();
	}
	
	/**
	 * @throws KBBuilderException 
	 * 
	 */
	@Test(expected = KBBuilderException.class)
	public void testRuleWithEqualityInBodyBehavior() throws ParseException, KBBuilderException {
		KBBuilder kbb = new KBBuilder();
		kbb.addAll(new DlgpParser("p(X,Y) :- q(X,Y), X=Y."));
		kbb.build();
	}
}
