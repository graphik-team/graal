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
 /**
 * 
 */
package fr.lirmm.graphik.graal.store.test;

import org.junit.Assert;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.factory.ConjunctiveQueryFactory;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.homomorphism.StaticHomomorphism;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.util.stream.CloseableIterator;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
@RunWith(Theories.class)
public class ConjunctiveQueryTest {

	@DataPoints
	public static AtomSet[] atomset() {
		return TestUtil.getAtomSet();
	}

	/**
	 * Test an empty query with an empty atomSet that must have an empty substitution 
	 */
	@Theory
	public void emptyQueryAndEmptyAtomSetTest(AtomSet store) {
		try {
			InMemoryAtomSet queryAtomSet = new LinkedListAtomSet();
			ConjunctiveQuery query = ConjunctiveQueryFactory.instance().create(queryAtomSet);

			CloseableIterator<Substitution> subReader;
			Substitution sub;

			subReader = StaticHomomorphism.executeQuery(query, store);

			Assert.assertTrue(subReader.hasNext());
			sub = subReader.next();
			Assert.assertEquals(0, sub.getTerms().size());

			Assert.assertFalse(subReader.hasNext());
			subReader.close();
		} catch (Exception e) {
			Assert.assertTrue(e.getMessage(), false);
		}
	}
	
	/**
	 * Test an empty query that must have an empty substitution
	 */
	@Theory
	public void emptyQueryTest(AtomSet store) {
		try {
			store.addAll(DlgpParser.parseAtomSet("p(a,b), p(b,c), q(a,c,d)."));

			InMemoryAtomSet queryAtomSet = new LinkedListAtomSet();
			ConjunctiveQuery query = ConjunctiveQueryFactory.instance().create(queryAtomSet);

			CloseableIterator<Substitution> subReader;
			Substitution sub;

			subReader = StaticHomomorphism.executeQuery(query, store);

			Assert.assertTrue(subReader.hasNext());
			sub = subReader.next();
			Assert.assertEquals(0, sub.getTerms().size());

			Assert.assertFalse(subReader.hasNext());
			subReader.close();
		} catch (Exception e) {
			Assert.assertTrue(e.getMessage(), false);
		}
	}
	
	/**
	 * Test a query without answer
	 */
	@Theory
	public void noAnswerQueryTest(AtomSet store) {
		try {
			store.addAll(DlgpParser.parseAtomSet("p(a,b),p(b,c),q(a,c,d)."));

			ConjunctiveQuery query = DlgpParser.parseQuery("?(X) :- p(c,X).");

			CloseableIterator<Substitution> subReader;

			subReader = StaticHomomorphism.executeQuery(query, store);
			Assert.assertFalse(subReader.hasNext());
			subReader.close();
		} catch (Exception e) {
			Assert.assertTrue(e.getMessage(), false);
		}
	}

	/**
	 * Test a query without answer
	 */
	@Theory
	public void noAnswerQueryTest2(AtomSet store) {
		try {
			ConjunctiveQuery query = DlgpParser.parseQuery("?(Y,X) :- p(Y,X).");

			CloseableIterator<Substitution> subReader;

			subReader = StaticHomomorphism.executeQuery(query, store);
			Assert.assertFalse(subReader.hasNext());
			subReader.close();
		} catch (Exception e) {
			Assert.assertTrue(e.getMessage(), false);
		}
	}
	
	/**
	 * Test a query without answer
	 */
	@Theory
	public void noAnswerQueryTest3(AtomSet store) {
		try {
			store.addAll(DlgpParser.parseAtomSet("p(a,b), r(c,c)."));
			ConjunctiveQuery query = DlgpParser.parseQuery("?(Y,X) :- p(a,X), q(X,Y).");

			CloseableIterator<Substitution> subReader;

			subReader = StaticHomomorphism.executeQuery(query, store);
			Assert.assertFalse(subReader.hasNext());
			subReader.close();
		} catch (Exception e) {
			Assert.assertTrue(e.getMessage(), false);
		}
	}
	
	

	/**
	 * Test a boolean query
	 */
	@Theory
	public void booleanQueryTest(AtomSet store) {
		try {
			store.addAll(DlgpParser.parseAtomSet("p(a,b).p(b,c).q(a,c,d).q(d,c,a)."));

			InMemoryAtomSet queryAtomSet = new LinkedListAtomSet();
			queryAtomSet.add(DlgpParser.parseAtom("q(a,c,d)."));
			ConjunctiveQuery query = ConjunctiveQueryFactory.instance().create(queryAtomSet);

			CloseableIterator<Substitution> subReader;
			Substitution sub;

			subReader = StaticHomomorphism.executeQuery(query, store);

			Assert.assertTrue(subReader.hasNext());
			sub = subReader.next();
			Assert.assertEquals(0, sub.getTerms().size());

			Assert.assertFalse(subReader.hasNext());
			subReader.close();
		} catch (Exception e) {
			Assert.assertTrue(e.getMessage(), false);
		}
	}

	/**
	 * Test a boolean query
	 */
	@Theory
	public void booleanQueryWithoutAnswerTest(AtomSet store) {
		try {
			InMemoryAtomSet queryAtomSet = new LinkedListAtomSet();
			queryAtomSet.add(DlgpParser.parseAtom("q(a,c,d)."));
			ConjunctiveQuery query = ConjunctiveQueryFactory.instance().create(queryAtomSet);

			CloseableIterator<Substitution> subReader;
			Substitution sub;

			subReader = StaticHomomorphism.executeQuery(query, store);

			Assert.assertFalse(subReader.hasNext());
			subReader.close();
		} catch (Exception e) {
			Assert.assertTrue(e.getMessage(), false);
		}
	}

	/**
	 * Test a boolean query with variables
	 */
	@Theory
	public void booleanQueryWithVariablesTest(AtomSet store) {
		try {
			store.addAll(DlgpParser.parseAtomSet("p(a,b),p(d,e),p(e,c),p(f,d)."));

			ConjunctiveQuery query = DlgpParser.parseQuery("?(X,Y,Z,W) :- p(X,Y),p(Y,Z),p(Z,W).");

			CloseableIterator<Substitution> subReader = StaticHomomorphism.executeQuery(query, store);

			Assert.assertTrue(subReader.hasNext());
			Substitution sub = subReader.next();
			Assert.assertFalse(subReader.hasNext());
			subReader.close();
		} catch (Exception e) {
			Assert.assertTrue(e.getMessage(), false);
		}
	}

	@Theory
	public void basicQueryTest(AtomSet store) {
		try {
			store.addAll(DlgpParser.parseAtomSet("p(a,b).p(b,c)."));

			ConjunctiveQuery query = DlgpParser.parseQuery("?(X,Y) :- p(X,Y),p(Y,c).");

			CloseableIterator<Substitution> subReader;
			Substitution sub;

			subReader = StaticHomomorphism.executeQuery(query, store);

			Assert.assertTrue(subReader.hasNext());
			sub = subReader.next();
			Assert.assertEquals(2, sub.getTerms().size());
			Assert.assertEquals(
sub.createImageOf(DefaultTermFactory.instance()
					.createVariable("X")), DefaultTermFactory.instance()
					.createConstant("a"));
			Assert.assertEquals(
sub.createImageOf(DefaultTermFactory.instance()
					.createVariable("Y")), DefaultTermFactory.instance()
					.createConstant("b"));

			Assert.assertFalse(subReader.hasNext());
			subReader.close();
		} catch (Exception e) {
			Assert.assertTrue(e.getMessage(), false);
		}
	}
	
	public void variableFusionTest(AtomSet store) {
		try {
			store.addAll(DlgpParser.parseAtomSet("p(a,b),q(b,b)."));

			ConjunctiveQuery query = DlgpParser.parseQuery("?(X,Y) :- p(a,X),q(X,Y),q(Y,X).");

			CloseableIterator<Substitution> subReader;
			Substitution sub;

			subReader = StaticHomomorphism.executeQuery(query, store);

			Assert.assertTrue(subReader.hasNext());
			sub = subReader.next();
			Assert.assertEquals(2, sub.getTerms().size());
			Assert.assertEquals(
sub.createImageOf(DefaultTermFactory.instance()
					.createVariable("X")), DefaultTermFactory.instance()
					.createConstant("b"));
			Assert.assertEquals(
sub.createImageOf(DefaultTermFactory.instance()
					.createVariable("Y")), DefaultTermFactory.instance()
					.createConstant("b"));

			Assert.assertFalse(subReader.hasNext());
			subReader.close();
		} catch (Exception e) {
			Assert.assertTrue(e.getMessage(), false);
		}
		
	}

	/**
	 * Test a boolean query
	 */
	@Theory
	public void tttTrueQueryTest(AtomSet store) {
		try {
			store.addAll(DlgpParser.parseAtomSet("p(a,b),q(a,c,d),q(d,c,a)."));

			ConjunctiveQuery query = DlgpParser.parseQuery("?(X,Y) :- q(a,c,d),p(X,Y).");

			CloseableIterator<Substitution> subReader;
			Substitution sub;

			subReader = StaticHomomorphism.executeQuery(query, store);

			Assert.assertTrue(subReader.hasNext());
			sub = subReader.next();
			Assert.assertEquals(2, sub.getTerms().size());
			Assert.assertEquals(
sub.createImageOf(DefaultTermFactory.instance()
					.createVariable("X")), DefaultTermFactory.instance()
					.createConstant("a"));
			Assert.assertEquals(
sub.createImageOf(DefaultTermFactory.instance()
					.createVariable("Y")), DefaultTermFactory.instance()
					.createConstant("b"));

			Assert.assertFalse(subReader.hasNext());
			subReader.close();
		} catch (Exception e) {
			Assert.assertTrue(e.getMessage(), false);
		}
	}

	/**
	 * Test a boolean query
	 */
	@Theory
	public void tttFalseQueryTest(AtomSet store) {
		try {
			store.addAll(DlgpParser.parseAtomSet("p(a,b),p(b,c),q(a,c,d),q(d,c,a)."));

			ConjunctiveQuery query = DlgpParser.parseQuery("?(X,Y) :- q(a,f,d),p(X,Y).");

			CloseableIterator<Substitution> subReader;
			subReader = StaticHomomorphism.executeQuery(query, store);
			Assert.assertFalse(subReader.hasNext());
			subReader.close();
		} catch (Exception e) {
			Assert.assertTrue(e.getMessage(), false);
		}
	}

	/**
	 * Response variables Test
	 */
	@Theory
	public void responseVariablesTest(AtomSet store) {
		try {
			store.addAll(DlgpParser.parseAtomSet("p(a,b)."));

			ConjunctiveQuery query = DlgpParser.parseQuery("?(X) :- p(X,Y).");

			CloseableIterator<Substitution> subReader;
			Substitution sub;

			subReader = StaticHomomorphism.executeQuery(query, store);

			Assert.assertTrue(subReader.hasNext());
			sub = subReader.next();
			Assert.assertEquals(1, sub.getTerms().size());
			Assert.assertEquals(
					DefaultTermFactory.instance().createConstant("a"), sub
							.createImageOf(DefaultTermFactory.instance()
									.createVariable("X")));
			subReader.close();
		} catch (Exception e) {
			Assert.assertTrue(e.getMessage(), false);
		}
	}

	@Theory
	public void nonexistingPredicateQuery(AtomSet store) {
		try {
			store.addAll(DlgpParser.parseAtomSet("p(a,b)."));

			ConjunctiveQuery query = DlgpParser.parseQuery("?(X,Y) :- q(X,Y).");

			CloseableIterator<Substitution> subReader;
			subReader = StaticHomomorphism.executeQuery(query, store);
			Assert.assertFalse(subReader.hasNext());
			subReader.close();
		} catch (Exception e) {
			Assert.assertTrue(e.getMessage(), false);
		}
	}
	
	@Theory
	public void wrongArityQuery(AtomSet atomset) {
		try {
			atomset.add(DlgpParser.parseAtom("p(a,b)."));
			ConjunctiveQuery query = DlgpParser.parseQuery("? :- p(X).");
	
			CloseableIterator<Substitution> subReader;
			subReader = StaticHomomorphism.executeQuery(query, atomset);
			Assert.assertFalse(subReader.hasNext());
			subReader.close();
		} catch (Exception e) {
			Assert.assertTrue(e.getMessage(), false);
		}
	}
	
	@Theory
	public void wrongArityQuery2(AtomSet atomset) {
		try {
			atomset.add(DlgpParser.parseAtom("p(a,b)."));
			ConjunctiveQuery query = DlgpParser.parseQuery("? :- p(X,Y,Z).");
	
			CloseableIterator<Substitution> subReader;
			subReader = StaticHomomorphism.executeQuery(query, atomset);
			Assert.assertFalse(subReader.hasNext());
			subReader.close();
		} catch (Exception e) {
			Assert.assertTrue(e.getMessage(), false);
		}
	}
	
	@Theory
	public void diffLiteralQueryTest(AtomSet atomset) {
		try {
			atomset.add(DlgpParser.parseAtom("p(\"literal\")."));
			ConjunctiveQuery query = DlgpParser.parseQuery("? :- p(\"otherLiteral\").");
	
			CloseableIterator<Substitution> subReader;
			subReader = StaticHomomorphism.executeQuery(query, atomset);
			Assert.assertFalse("Error on " + atomset.getClass() ,subReader.hasNext());
			subReader.close();
		} catch (Exception e) {
			Assert.assertTrue(e.getMessage(), false);
		}
	}
	
	@Theory
	public void sameLiteralQueryTest(AtomSet atomset) {
		try {
			atomset.add(DlgpParser.parseAtom("p(\"literal\")."));
			ConjunctiveQuery query = DlgpParser.parseQuery("? :- p(\"literal\").");
	
			CloseableIterator<Substitution> subReader;
			subReader = StaticHomomorphism.executeQuery(query, atomset);
			Assert.assertTrue("Error on " + atomset.getClass() ,subReader.hasNext());
			subReader.close();
		} catch (Exception e) {
			Assert.assertTrue(e.getMessage(), false);
		}
	}
	

}
