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
package fr.lirmm.graphik.graal.store.test;

import org.junit.Assert;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.homomorphism.Homomorphism;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.util.stream.CloseableIterator;

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

	@DataPoints
	public static Homomorphism[] homomorphisms() {
		return TestUtil.getHomomorphisms();
	}

	@Theory
	public void tttTrueQueryTest(Homomorphism h, AtomSet store) {
		try {
			store.addAll(DlgpParser.parseAtomSet("<P>(a,b),<Q>(a,c,d),<Q>(d,c,a)."));

			ConjunctiveQuery query = DlgpParser.parseQuery("?(X,Y) :- <Q>(a,c,d),<P>(X,Y).");

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
	public void tttFalseQueryTest(Homomorphism h, AtomSet store) {
		try {
			store.addAll(DlgpParser.parseAtomSet("<P>(a,b),<P>(b,c),<Q>(a,c,d),<Q>(d,c,a)."));

			ConjunctiveQuery query = DlgpParser.parseQuery("?(X,Y) :- <Q>(a,f,d),<P>(X,Y).");

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
	public void responseVariablesTest(Homomorphism h, AtomSet store) {
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
	public void nonexistingPredicateQuery(Homomorphism h, AtomSet store) {
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
	public void wrongArityQuery(Homomorphism h, AtomSet store) {
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
	public void wrongArityQuery2(Homomorphism h, AtomSet store) {
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
	public void diffLiteralQueryTest(Homomorphism h, AtomSet store) {
		try {
			store.add(DlgpParser.parseAtom("<P>(\"literal\")."));
			ConjunctiveQuery query = DlgpParser.parseQuery("? :- <P>(\"otherLiteral\").");

			CloseableIterator<Substitution> subReader;
			subReader = h.execute(query, store);
			Assert.assertFalse("Error on " + store.getClass(), subReader.hasNext());
			subReader.close();
		} catch (Exception e) {
			Assert.assertTrue(e.getMessage(), false);
		}
	}

	@Theory
	public void sameLiteralQueryTest(Homomorphism h, AtomSet store) {
		try {
			store.add(DlgpParser.parseAtom("<P>(\"literal\")."));
			ConjunctiveQuery query = DlgpParser.parseQuery("? :- <P>(\"literal\").");

			CloseableIterator<Substitution> subReader;
			subReader = h.execute(query, store);
			Assert.assertTrue("Error on " + store.getClass(), subReader.hasNext());
			subReader.close();
		} catch (Exception e) {
			Assert.assertTrue(e.getMessage(), false);
		}
	}

	@Theory
	public void misc0(Homomorphism h, AtomSet store) {
		try {
			store.addAll(DlgpParser.parseAtomSet("<P>(a,b),<P>(d,e),<P>(e,c),<P>(f,d)."));

			ConjunctiveQuery query = DlgpParser.parseQuery("?(X,Y,Z,W) :- <P>(X,Y),<P>(Y,Z),<P>(Z,W).");

			CloseableIterator<Substitution> subReader = h.execute(query, store);

			Assert.assertTrue(subReader.hasNext());
			Substitution sub = subReader.next();
			Assert.assertFalse(subReader.hasNext());
			subReader.close();
		} catch (Exception e) {
			Assert.assertTrue(e.getMessage(), false);
		}
	}

	@Theory
	public void NFC2WithLimit8Test(Homomorphism h, AtomSet store) {
		try {

			store.addAll(DlgpParser.parseAtomSet(
			    "<Q>(k,a),<Q>(k,k),<P>(k,a),<P>(k,b),<P>(k,c),<P>(k,d),<P>(k,e),<P>(k,f),<P>(k,g),<P>(k,h),<P>(k,i)."));

			ConjunctiveQuery query = DlgpParser.parseQuery("?(X,Y,Z) :- <P>(X,Z),<Q>(Y,Z).");

			CloseableIterator<Substitution> subReader = h.execute(query, store);

			Assert.assertTrue(subReader.hasNext());
			Substitution sub = subReader.next();
			Assert.assertFalse(subReader.hasNext());
			subReader.close();
		} catch (Exception e) {
			Assert.assertTrue(e.getMessage(), false);
		}
	}

	@Theory
	public void NFC2Test(Homomorphism h, AtomSet store) {
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

}
