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
/**
 * 
 */
package fr.lirmm.graphik.graal.store.test;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.homomorphism.Homomorphism;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.api.io.ParseException;
import fr.lirmm.graphik.graal.api.store.TripleStore;
import fr.lirmm.graphik.graal.api.store.WrongArityException;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.graal.test.TestUtil;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.IteratorException;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
@RunWith(Theories.class)
public class ConjunctiveQuery2Test {

	@DataPoints
	public static AtomSet[] atomset() {
		return TestUtil.getAtomSet();
	}

	@SuppressWarnings("rawtypes")
	@DataPoints
	public static Homomorphism[] homomorphisms() {
		return TestUtil.getHomomorphisms();
	}

	@Theory
	public void tttTrueQueryTest(Homomorphism<ConjunctiveQuery, AtomSet> h, AtomSet store) {
		try {
			store.addAll(DlgpParser.parseAtomSet("<P>(a,b),<Q>(a,c),<Q>(d,c)."));

			ConjunctiveQuery query = DlgpParser.parseQuery("?(X,Y) :- <Q>(a,c),<P>(X,Y).");

			CloseableIterator<Substitution> subReader;
			Substitution sub;

			subReader = h.execute(query, store);

			Assert.assertTrue(subReader.hasNext());
			sub = subReader.next();
			Assert.assertEquals(2, sub.getTerms().size());
			Assert.assertEquals(sub.createImageOf(DefaultTermFactory.instance().createVariable("X")),
			    DefaultTermFactory.instance().createConstant("a"));
			Assert.assertEquals(sub.createImageOf(DefaultTermFactory.instance().createVariable("Y")),
			    DefaultTermFactory.instance().createConstant("b"));

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
	public void tttFalseQueryTest(Homomorphism<ConjunctiveQuery, AtomSet> h, AtomSet store) {
		try {
			store.addAll(DlgpParser.parseAtomSet("<P>(a,b),<P>(b,c),<Q>(a,c),<Q>(d,c)."));

			ConjunctiveQuery query = DlgpParser.parseQuery("?(X,Y) :- <Q>(a,f),<P>(X,Y).");

			CloseableIterator<Substitution> subReader;
			subReader = h.execute(query, store);
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
	public void responseVariablesTest(Homomorphism<ConjunctiveQuery, AtomSet> h, AtomSet store) {
		try {
			store.addAll(DlgpParser.parseAtomSet("<P>(a,b)."));

			ConjunctiveQuery query = DlgpParser.parseQuery("?(X) :- <P>(X,Y).");

			CloseableIterator<Substitution> subReader;
			Substitution sub;

			subReader = h.execute(query, store);

			Assert.assertTrue(subReader.hasNext());
			sub = subReader.next();
			Assert.assertEquals(1, sub.getTerms().size());
			Assert.assertEquals(DefaultTermFactory.instance().createConstant("a"),
			    sub.createImageOf(DefaultTermFactory.instance().createVariable("X")));
			subReader.close();
		} catch (Exception e) {
			Assert.assertTrue(e.getMessage(), false);
		}
	}

	@Theory
	public void nonexistingPredicateQuery(Homomorphism<ConjunctiveQuery, AtomSet> h, AtomSet store) {
		try {
			store.addAll(DlgpParser.parseAtomSet("<P>(a,b)."));

			ConjunctiveQuery query = DlgpParser.parseQuery("?(X,Y) :- q(X,Y).");

			CloseableIterator<Substitution> subReader;
			subReader = h.execute(query, store);
			Assert.assertFalse(subReader.hasNext());
			subReader.close();
		} catch (Exception e) {
			Assert.assertTrue(e.getMessage(), false);
		}
	}

	@Theory
	public void wrongArityQuery(Homomorphism<ConjunctiveQuery, AtomSet> h, AtomSet store) {
		Assume.assumeFalse(store instanceof TripleStore);

		try {
			store.add(DlgpParser.parseAtom("<P>(a,b)."));
			ConjunctiveQuery query = DlgpParser.parseQuery("? :- <P>(X).");

			CloseableIterator<Substitution> subReader;
			subReader = h.execute(query, store);
			Assert.assertFalse(subReader.hasNext());
			subReader.close();
		} catch (Exception e) {
			Assert.assertTrue(e.getMessage(), false);
		}
	}

	@Theory
	public void wrongArityQuery2(Homomorphism<ConjunctiveQuery, AtomSet> h, AtomSet store) {
		Assume.assumeFalse(store instanceof TripleStore);

		try {
			store.add(DlgpParser.parseAtom("<P>(a,b)."));
			ConjunctiveQuery query = DlgpParser.parseQuery("? :- <P>(X,Y,Z).");

			CloseableIterator<Substitution> subReader;
			subReader = h.execute(query, store);
			Assert.assertFalse(subReader.hasNext());
			subReader.close();
		} catch (Exception e) {
			Assert.assertTrue(e.getMessage(), false);
		}
	}

	@Theory
	public void diffLiteralQueryTest(Homomorphism<ConjunctiveQuery, AtomSet> h, AtomSet store) {
		try {
			store.add(DlgpParser.parseAtom("<P>(a,\"literal\")."));
			ConjunctiveQuery query = DlgpParser.parseQuery("? :- <P>(a,\"otherLiteral\").");

			CloseableIterator<Substitution> subReader;
			subReader = h.execute(query, store);
			Assert.assertFalse("Error on " + store.getClass(), subReader.hasNext());
			subReader.close();
		} catch (Exception e) {
			Assert.assertTrue(e.getMessage(), false);
		}
	}

	@Theory
	public void sameLiteralQueryTest(Homomorphism<ConjunctiveQuery, AtomSet> h, AtomSet store) {
		try {
			store.add(DlgpParser.parseAtom("<P>(a,\"literal\")."));
			ConjunctiveQuery query = DlgpParser.parseQuery("? :- <P>(a,\"literal\").");

			CloseableIterator<Substitution> subReader;
			subReader = h.execute(query, store);
			Assert.assertTrue("Error on " + store.getClass(), subReader.hasNext());
			subReader.close();
		} catch (Exception e) {
			Assert.assertTrue(e.getMessage(), false);
		}
	}

	@Theory
	public void misc0(Homomorphism<ConjunctiveQuery, AtomSet> h, AtomSet store) {
		try {
			store.addAll(DlgpParser.parseAtomSet("<P>(a,b),<P>(d,e),<P>(e,c),<P>(f,d)."));

			ConjunctiveQuery query = DlgpParser.parseQuery("?(X,Y,Z,W) :- <P>(X,Y),<P>(Y,Z),<P>(Z,W).");

			CloseableIterator<Substitution> subReader = h.execute(query, store);

			Assert.assertTrue(subReader.hasNext());
			subReader.next();
			Assert.assertFalse(subReader.hasNext());
			subReader.close();
		} catch (Exception e) {
			Assert.assertTrue(e.getMessage(), false);
		}
	}

	@Theory
	public void NFC2WithLimit8Test(Homomorphism<ConjunctiveQuery, AtomSet> h, AtomSet store) {
		try {

			store.addAll(DlgpParser.parseAtomSet(
			    "<Q>(k,a),<Q>(k,k),<P>(k,a),<P>(k,b),<P>(k,c),<P>(k,d),<P>(k,e),<P>(k,f),<P>(k,g),<P>(k,h),<P>(k,i)."));

			ConjunctiveQuery query = DlgpParser.parseQuery("?(X,Y,Z) :- <P>(X,Z),<Q>(Y,Z).");

			CloseableIterator<Substitution> subReader = h.execute(query, store);

			Assert.assertTrue(subReader.hasNext());
			subReader.next();
			Assert.assertFalse(subReader.hasNext());
			subReader.close();
		} catch (Exception e) {
			Assert.assertTrue(e.getMessage(), false);
		}
	}

	@Theory
	public void NFC2Test(Homomorphism<ConjunctiveQuery, AtomSet> h, AtomSet store) {
		Assume.assumeFalse(store instanceof TripleStore);

		try {

			store.addAll(DlgpParser.parseAtomSet("<P>(a,b,c)."));

			ConjunctiveQuery query = DlgpParser.parseQuery("?(X,Y) :- <P>(X,Y,Y).");

			CloseableIterator<Substitution> subReader = h.execute(query, store);

			Assert.assertFalse(subReader.hasNext());
			subReader.close();
		} catch (Exception e) {
			Assert.assertTrue(e.getMessage(), false);
		}
	}
	
	/**
	 * Check if the GraphBaseBackjumping jump to the last neighbor (in the homomorphism level order)
	 * @param h
	 * @param store
	 */
	@Theory
	public void BackJumpingTest1(Homomorphism<ConjunctiveQuery, AtomSet> h, AtomSet store) {
		try {
			store.addAll(DlgpParser.parseAtomSet("<P0>(a,c), <P1>(a,z), <P1>(b,c)."));
			ConjunctiveQuery query = DlgpParser.parseQuery("? :- <P0>(X0,X2), <P1>(X1,X2).");

			Assert.assertTrue(h.exist(query, store));
		} catch (Exception e) {
			Assert.assertTrue(e.getMessage(), false);
		}
	}
	
	@Theory
	public void testWrongArityOnTripleStoreWithHomomorphism(Homomorphism<ConjunctiveQuery, AtomSet> h, AtomSet store) throws HomomorphismException, IteratorException, AtomSetException {
		Assume.assumeTrue(store instanceof TripleStore);
		store.add(DlgpParser.parseAtom("p(a,b)."));
		ConjunctiveQuery query = DlgpParser.parseQuery("? :- p(X,Y), q(Y).");
		boolean wrongArityExceptionFound = false;
		try {
    		CloseableIterator<Substitution> execute = h.execute(query, store);
    		while(execute.hasNext()) {
    			execute.next();
    		}
    		execute.close();
		} catch(IteratorException | HomomorphismException e) {
			// look for WrongArityException in cause stack
			Throwable cause = e;
			while(cause != null && !(cause instanceof WrongArityException)) {
				cause = cause.getCause();
			}
			wrongArityExceptionFound = cause != null;
		}
		Assert.assertTrue(wrongArityExceptionFound);
	}

}
