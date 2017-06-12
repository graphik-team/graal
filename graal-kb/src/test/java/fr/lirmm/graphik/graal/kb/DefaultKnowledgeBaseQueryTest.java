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

import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.io.ParseException;
import fr.lirmm.graphik.graal.api.kb.Approach;
import fr.lirmm.graphik.graal.api.kb.KnowledgeBase;
import fr.lirmm.graphik.graal.api.kb.KnowledgeBaseException;
import fr.lirmm.graphik.graal.api.util.TimeoutException;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.IteratorException;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class DefaultKnowledgeBaseQueryTest {

	/**
	 * Test method for
	 * {@link fr.lirmm.graphik.graal.kb.DefaultKnowledgeBase#query(fr.lirmm.graphik.graal.api.core.Query)}.
	 * @throws AtomSetException 
	 * @throws KnowledgeBaseException 
	 * @throws ParseException 
	 * @throws IteratorException 
	 */
	@Test
	public void testQuery() throws AtomSetException, ParseException, KnowledgeBaseException, IteratorException {
		KnowledgeBase kb = new DefaultKnowledgeBase(
				new DlgpParser("p(X) :- q(X). q(X) :- r(X). r(X) :- s(X).  s(a)."));
		CloseableIterator<Substitution> res = kb.query(DlgpParser.parseQuery("? :- p(a)."));
		Assert.assertTrue(res.hasNext());
		res.close();
		kb.close();
	}
	
	/**
	 * Test method for
	 * {@link fr.lirmm.graphik.graal.kb.DefaultKnowledgeBase#query(fr.lirmm.graphik.graal.api.core.Query, long)}.
	 * @throws AtomSetException 
	 * @throws KnowledgeBaseException 
	 * @throws ParseException 
	 * @throws IteratorException 
	 * @throws TimeoutException 
	 */
	@Test
	public void testQueryWithTimeout0() throws AtomSetException, ParseException, KnowledgeBaseException, IteratorException, TimeoutException {
		KnowledgeBase kb = new DefaultKnowledgeBase(
				new DlgpParser("p(X) :- q(X). q(X) :- r(X). r(X) :- s(X).  s(a)."));
		CloseableIterator<Substitution> res = kb.query(DlgpParser.parseQuery("? :- p(a)."), 0);
		Assert.assertTrue(res.hasNext());
		res.close();
		kb.close();
	}
	
	/**
	 * Test method for
	 * {@link fr.lirmm.graphik.graal.kb.DefaultKnowledgeBase#query(fr.lirmm.graphik.graal.api.core.Query, long)}.
	 * @throws AtomSetException 
	 * @throws KnowledgeBaseException 
	 * @throws ParseException 
	 * @throws IteratorException 
	 * @throws TimeoutException 
	 * @throws KBBuilderException 
	 */
	@Test(expected = TimeoutException.class)
	public void testInfinityQueryWithTimeout() throws AtomSetException, ParseException, KnowledgeBaseException, IteratorException, TimeoutException, KBBuilderException {
		KBBuilder kbBuilder = new KBBuilder();
		kbBuilder.setApproach(Approach.SATURATION_ONLY);
		kbBuilder.addAll(new DlgpParser("p(X,Y), h(Y) :- h(X). p(X,Z) :- p(X,Y), p(Y,Z). h(a)."));
		KnowledgeBase kb = kbBuilder.build();
		kb.query(DlgpParser.parseQuery("? :- p(a)."), 10000);
	}
	
	/**
	 * Test method for
	 * {@link fr.lirmm.graphik.graal.kb.DefaultKnowledgeBase#query(fr.lirmm.graphik.graal.api.core.Query, long)}.
	 * @throws AtomSetException 
	 * @throws KnowledgeBaseException 
	 * @throws ParseException 
	 * @throws IteratorException 
	 * @throws TimeoutException 
	 * @throws KBBuilderException 
	 */
	@Test(expected = KnowledgeBaseException.class)
	public void testSaturationFirstQueryWithNoProof() throws AtomSetException, ParseException, KnowledgeBaseException, IteratorException, TimeoutException, KBBuilderException {
		KBBuilder kbBuilder = new KBBuilder();
		kbBuilder.setApproach(Approach.SATURATION_FIRST);
		kbBuilder.addAll(new DlgpParser("p(X,Y), h(Y) :- h(X). p(X,Z) :- p(X,Y), p(Y,Z). h(a)."));
		KnowledgeBase kb = kbBuilder.build();
		kb.query(DlgpParser.parseQuery("? :- p(a)."));
	}
	
	/**
	 * Test method for
	 * {@link fr.lirmm.graphik.graal.kb.DefaultKnowledgeBase#query(fr.lirmm.graphik.graal.api.core.Query, long)}.
	 * @throws AtomSetException 
	 * @throws KnowledgeBaseException 
	 * @throws ParseException 
	 * @throws IteratorException 
	 * @throws TimeoutException 
	 * @throws KBBuilderException 
	 */
	@Test(expected = KnowledgeBaseException.class)
	public void testRewritingFirstQueryWithNoProof() throws AtomSetException, ParseException, KnowledgeBaseException, IteratorException, TimeoutException, KBBuilderException {
		KBBuilder kbBuilder = new KBBuilder();
		kbBuilder.setApproach(Approach.REWRITING_FIRST);
		kbBuilder.addAll(new DlgpParser("p(X,Y), h(Y) :- h(X). p(X,Z) :- p(X,Y), p(Y,Z). h(a)."));
		KnowledgeBase kb = kbBuilder.build();
		kb.query(DlgpParser.parseQuery("? :- p(a)."));
	}

}
