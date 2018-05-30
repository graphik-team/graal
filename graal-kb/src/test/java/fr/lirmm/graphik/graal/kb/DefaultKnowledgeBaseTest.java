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
package fr.lirmm.graphik.graal.kb;

import org.junit.Assert;
import org.junit.Test;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.NegativeConstraint;
import fr.lirmm.graphik.graal.api.core.Query;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.RuleSet;
import fr.lirmm.graphik.graal.api.io.ParseException;
import fr.lirmm.graphik.graal.api.kb.Approach;
import fr.lirmm.graphik.graal.api.kb.KnowledgeBase;
import fr.lirmm.graphik.graal.api.kb.KnowledgeBaseException;
import fr.lirmm.graphik.graal.core.atomset.graph.DefaultInMemoryGraphStore;
import fr.lirmm.graphik.graal.core.ruleset.LinkedListRuleSet;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.util.stream.IteratorException;
import fr.lirmm.graphik.util.stream.Iterators;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class DefaultKnowledgeBaseTest {

	/**
	 * Test method for
	 * {@link fr.lirmm.graphik.graal.kb.DefaultKnowledgeBase#DefaultKnowledgeBase(fr.lirmm.graphik.graal.api.io.Parser)}.
	 * 
	 * @throws ParseException
	 * @throws AtomSetException
	 */
	@Test
	public void testDefaultKnowledgeBaseParserOfObject() throws ParseException, AtomSetException {
		Atom aa = DlgpParser.parseAtom("q(a).");
		Atom ab = DlgpParser.parseAtom("q(b).");
		Atom ac = DlgpParser.parseAtom("q(c).");
		Rule r = DlgpParser.parseRule("[R] p(X) :- q(X).");
		NegativeConstraint nc = DlgpParser.parseNegativeConstraint("[NC] ! :- q(X), p(X).");

		KnowledgeBase kb = new DefaultKnowledgeBase(
				new DlgpParser("[R] p(X) :- q(X). q(a), q(b). q(c). [NC] ! :- q(X), p(X)."));

		Assert.assertTrue(kb.getOntology().contains(r));
		Assert.assertTrue(kb.getOntology().contains(nc));
		Assert.assertTrue(kb.getFacts().contains(aa));
		Assert.assertTrue(kb.getFacts().contains(ab));
		Assert.assertTrue(kb.getFacts().contains(ac));
		kb.close();
	}

	/**
	 * Test method for
	 * {@link fr.lirmm.graphik.graal.kb.DefaultKnowledgeBase#DefaultKnowledgeBase(fr.lirmm.graphik.graal.api.core.AtomSet, fr.lirmm.graphik.graal.api.io.Parser)}.
	 * @throws ParseException 
	 * @throws AtomSetException 
	 */
	@Test
	public void testDefaultKnowledgeBaseAtomSetParserOfObject() throws ParseException, AtomSetException {
		Atom aa = DlgpParser.parseAtom("q(a).");
		Atom ab = DlgpParser.parseAtom("q(b).");
		Atom ac = DlgpParser.parseAtom("q(c).");
		Rule r = DlgpParser.parseRule("[R] p(X) :- q(X).");
		NegativeConstraint nc = DlgpParser.parseNegativeConstraint("[NC] ! :- q(X), p(X).");

		AtomSet store = new DefaultInMemoryGraphStore();
		store.add(aa);
		KnowledgeBase kb = new DefaultKnowledgeBase(store,
				new DlgpParser("[R] p(X) :- q(X). q(b). q(c). [NC] ! :- q(X), p(X)."));
		
		Assert.assertTrue(kb.getOntology().contains(r));
		Assert.assertTrue(kb.getOntology().contains(nc));
		Assert.assertTrue(kb.getFacts().contains(aa));
		Assert.assertTrue(kb.getFacts().contains(ab));
		Assert.assertTrue(kb.getFacts().contains(ac));
		kb.close();
	}

	/**
	 * Test method for
	 * {@link fr.lirmm.graphik.graal.kb.DefaultKnowledgeBase#DefaultKnowledgeBase(fr.lirmm.graphik.graal.api.core.AtomSet, fr.lirmm.graphik.graal.api.core.RuleSet)}.
	 * @throws AtomSetException 
	 * @throws ParseException 
	 */
	@Test
	public void testDefaultKnowledgeBaseAtomSetRuleSet() throws AtomSetException, ParseException {
		Atom aa = DlgpParser.parseAtom("q(a).");
		Atom ab = DlgpParser.parseAtom("q(b).");
		Atom ac = DlgpParser.parseAtom("q(c).");
		Rule r = DlgpParser.parseRule("[R1] p(x) :- q(X).");
		NegativeConstraint nc = DlgpParser.parseNegativeConstraint("[NC] ! :- q(X), p(X).");

		AtomSet store = new DefaultInMemoryGraphStore();
		store.add(aa);
		store.add(ab);
		store.add(ac);
		RuleSet ruleset = new LinkedListRuleSet();
		ruleset.add(r);
		ruleset.add(nc);
		
		KnowledgeBase kb = new DefaultKnowledgeBase(store, ruleset);
		Assert.assertTrue(kb.getOntology().contains(r));
		Assert.assertTrue(kb.getOntology().contains(nc));
		Assert.assertTrue(kb.getFacts().contains(aa));
		Assert.assertTrue(kb.getFacts().contains(ab));
		Assert.assertTrue(kb.getFacts().contains(ac));
		kb.close();
	}

	/**
	 * Test method for
	 * {@link fr.lirmm.graphik.graal.kb.DefaultKnowledgeBase#isConsistent()}.
	 * @throws KnowledgeBaseException 
	 * @throws AtomSetException 
	 */
	@Test
	public void testIsConsistentFalse() throws KnowledgeBaseException, AtomSetException {
		KnowledgeBase kb = new DefaultKnowledgeBase(
				new DlgpParser("[R] p(X) :- q(X). q(a). [NC] ! :- q(X), p(X)."));
		Assert.assertFalse(kb.isConsistent());
		kb.close();
	}
	
	/**
	 * Test method for
	 * {@link fr.lirmm.graphik.graal.kb.DefaultKnowledgeBase#isConsistent()}.
	 * @throws KnowledgeBaseException 
	 * @throws AtomSetException 
	 * @throws KBBuilderException 
	 */
	@Test
	public void testIsConsistentFalse2() throws KnowledgeBaseException, AtomSetException, KBBuilderException {
		KBBuilder kbb = new KBBuilder();
		kbb.addAll(new DlgpParser("q2(X) :- q(X). pr(X) :- p(X), r(X). q(a), p(a), r(a). [NC] ! :- q2(X), pr(X)."));
		kbb.setApproach(Approach.REWRITING_FIRST);
		KnowledgeBase kb = kbb.build();
		
		Assert.assertFalse(kb.isConsistent());
		kb.close();
	}
	
	/**
	 * Test method for
	 * {@link fr.lirmm.graphik.graal.kb.DefaultKnowledgeBase#isConsistent()}.
	 */
	@Test
	public void testIsConsistentTrue() throws KnowledgeBaseException, AtomSetException {
		KnowledgeBase kb = new DefaultKnowledgeBase(
				new DlgpParser("[R] p(X) :- q(X). p(a). [NC] ! :- q(X), p(X)."));
		Assert.assertTrue(kb.isConsistent());
		kb.close();
	}

	/**
	 * Test method for
	 * {@link fr.lirmm.graphik.graal.kb.DefaultKnowledgeBase#saturate()}.
	 * @throws AtomSetException 
	 * @throws ParseException 
	 * @throws KnowledgeBaseException 
	 */
	@Test
	public void testSaturate() throws ParseException, AtomSetException, KnowledgeBaseException {
		KnowledgeBase kb = new DefaultKnowledgeBase(
				new DlgpParser("p(X) :- q(X). q(X) :- r(X). r(X) :- s(X).  s(a)."));
		kb.saturate();
		Assert.assertTrue(kb.getFacts().contains(DlgpParser.parseAtom("r(a).")));
		Assert.assertTrue(kb.getFacts().contains(DlgpParser.parseAtom("q(a).")));
		Assert.assertTrue(kb.getFacts().contains(DlgpParser.parseAtom("p(a).")));
		kb.close();
	}

	/**
	 * Test method for
	 * {@link fr.lirmm.graphik.graal.kb.DefaultKnowledgeBase#getRuleNames()}.
	 * @throws ParseException 
	 */
	@Test
	public void testGetRuleNames() throws ParseException {
		Rule r1 = DlgpParser.parseRule("[R1] p(x) :- q(X).");
		Rule r2 = DlgpParser.parseRule("[R2] q(x) :- r(X).");

		AtomSet store = new DefaultInMemoryGraphStore();
		RuleSet ruleset = new LinkedListRuleSet();
		ruleset.add(r1);
		ruleset.add(r2);
		
		KnowledgeBase kb = new DefaultKnowledgeBase(store, ruleset);
		
		Assert.assertTrue(kb.getRuleNames().contains("R1"));
		Assert.assertTrue(kb.getRuleNames().contains("R2"));
		
		Assert.assertEquals(r1, kb.getRule("R1"));
		Assert.assertEquals(r2, kb.getRule("R2"));
		kb.close();
	}


	/**
	 * Test method for
	 * {@link fr.lirmm.graphik.graal.kb.DefaultKnowledgeBase#addQuery(fr.lirmm.graphik.graal.api.core.Query)}.
	 * @throws ParseException 
	 */
	@Test
	public void testAddQuery() throws ParseException {
		Query q1 = DlgpParser.parseQuery("[Q1] ? :- p(X).");
		KnowledgeBase kb = new KBBuilder().build();
		kb.addQuery(q1);
		Assert.assertTrue(kb.getQueryNames().contains("Q1"));
		Assert.assertEquals(q1, kb.getQuery("Q1"));
		kb.close();
	}
	
	/**
	 * Test method for
	 * {@link fr.lirmm.graphik.graal.kb.DefaultKnowledgeBase#addQuery(fr.lirmm.graphik.graal.api.core.Query)}.
	 * @throws ParseException 
	 */
	@Test
	public void testAddSameQueryTwice() throws ParseException {
		Query q1 = DlgpParser.parseQuery("[Q1] ? :- p(X).");
		KnowledgeBase kb = new KBBuilder().build();
		kb.addQuery(q1);
		kb.addQuery(q1);
		Assert.assertTrue(kb.getQueryNames().contains("Q1"));
		Assert.assertEquals(q1, kb.getQuery("Q1"));
		Assert.assertEquals(1, kb.getQueryNames().size());
		kb.close();
	}
	
	/**
	 * Test method for
	 * {@link fr.lirmm.graphik.graal.kb.DefaultKnowledgeBase#addQuery(fr.lirmm.graphik.graal.api.core.Query)}.
	 * @throws ParseException 
	 */
	@Test
	public void testAddTwoQueryWithSameNames() throws ParseException {
		Query q1 = DlgpParser.parseQuery("[Q] ? :- q1(X).");
		Query q2 = DlgpParser.parseQuery("[Q] ? :- q2(X).");

		KnowledgeBase kb = new KBBuilder().build();
		kb.addQuery(q1);
		kb.addQuery(q2);
		Assert.assertTrue(kb.getQueryNames().contains("Q"));
		Assert.assertEquals(q1, kb.getQuery("Q"));
		Assert.assertEquals(2, kb.getQueryNames().size());
		kb.close();
	}

	@Test(expected = KnowledgeBaseException.class)
	public void githubIssue3_REWRITING_ONLY() throws ParseException, KBBuilderException, KnowledgeBaseException {
		KBBuilder kbb = new KBBuilder();
		kbb.addAll(new DlgpParser(""
				+ "p(a)."
				+ "q(X) :- p(X)."));
		kbb.setApproach(Approach.REWRITING_ONLY);
		KnowledgeBase kb = kbb.build();
		
		kb.saturate();
	}
	
	@Test
	public void githubIssue3_REWRITING_FIRST() throws KBBuilderException, KnowledgeBaseException, IteratorException {
		KBBuilder kbb = new KBBuilder();
		kbb.addAll(new DlgpParser(""
				+ "p(a)."
				+ "q(X) :- p(X)."));
		kbb.setApproach(Approach.REWRITING_FIRST);
		KnowledgeBase kb = kbb.build();
		
		kb.saturate();
		Assert.assertEquals(2, Iterators.count(kb.getFacts().iterator()));
	}
	
	@Test
	public void githubIssue3_SATURATION_FIRST() throws KBBuilderException, KnowledgeBaseException, IteratorException {
		KBBuilder kbb = new KBBuilder();
		kbb.addAll(new DlgpParser(""
				+ "p(a)."
				+ "q(X) :- p(X)."));
		kbb.setApproach(Approach.SATURATION_FIRST);
		KnowledgeBase kb = kbb.build();
		
		kb.saturate();
		Assert.assertEquals(2, Iterators.count(kb.getFacts().iterator()));
	}
	
	@Test
	public void githubIssue3_SATURATION_ONLY() throws KBBuilderException, KnowledgeBaseException, IteratorException {
		KBBuilder kbb = new KBBuilder();
		kbb.addAll(new DlgpParser(""
				+ "p(a)."
				+ "q(X) :- p(X)."));
		kbb.setApproach(Approach.SATURATION_ONLY);
		KnowledgeBase kb = kbb.build();
		
		kb.saturate();
		Assert.assertEquals(2, Iterators.count(kb.getFacts().iterator()));
	}

}
