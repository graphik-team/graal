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


import static org.junit.Assert.fail;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.Constant;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.io.ParseException;
import fr.lirmm.graphik.graal.api.store.TripleStore;
import fr.lirmm.graphik.graal.api.store.WrongArityException;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.stream.filter.AtomFilterIterator;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.graal.test.TestUtil;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.IteratorException;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
@RunWith(Theories.class)
public class TripleStoreTest {

	@DataPoints
	public static AtomSet[] getAtomSet() {
		return TestUtil.getAtomSet();
	}

	@Theory
	public void simpleTest(AtomSet store) throws AtomSetException, IteratorException {
		Assume.assumeTrue(store instanceof TripleStore);

		Term t1 = DefaultTermFactory.instance()
				.createConstant("http://to.to/b");
		Term t2 = DefaultTermFactory.instance()
				.createConstant("http://to.to/a");
		Predicate p = new Predicate("http://to.to/p", 2);
		Atom atom1 = new DefaultAtom(p, t1, t2);

		store.add(atom1);

		int i = 0;
		for (CloseableIterator<Atom> it = store.iterator(); it.hasNext(); it.next()) {
			++i;
		}

		Assert.assertEquals(1, i);
	}

	@Theory
	public void getPredicates(AtomSet store) throws AtomSetException, IteratorException, ParseException {
		Assume.assumeTrue(store instanceof TripleStore);

		store.add(DlgpParser.parseAtom("r(a,b)."));
		store.add(DlgpParser.parseAtom("s(a,b)."));
		store.add(DlgpParser.parseAtom("s(a,c)."));

		int i = 0;
		for (CloseableIterator<Predicate> it = store.predicatesIterator(); it.hasNext(); it
				.next()) {
			++i;
		}

		Assert.assertEquals(2, i);
	}

	@Theory
	public void addAndContains(AtomSet store) throws AtomSetException, IteratorException, ParseException {
		Assume.assumeTrue(store instanceof TripleStore);

		store.add(DlgpParser.parseAtom("p(a,b)."));
		store.add(DlgpParser.parseAtom("q(b,c)."));

		int i = 0;
		for (CloseableIterator<Atom> it = store.iterator(); it.hasNext(); it.next()) {
			++i;
		}

		Assert.assertEquals("Store does not contains exactly 2 atoms", 2, i);

		Assert.assertTrue("Store does not contains p(a,b)",
				store.contains(DlgpParser.parseAtom("p(a,b).")));
		Assert.assertTrue("Store does not contains q(b,c)",
				store.contains(DlgpParser.parseAtom("q(b,c).")));

		Assert.assertFalse("Store contains q(c, b)",
				store.contains(DlgpParser.parseAtom("q(c,b).")));
	}

	@Theory
	public void getTerms(AtomSet store) throws AtomSetException, IteratorException {
		Assume.assumeTrue(store instanceof TripleStore);

		store.add(DlgpParser.parseAtom("p(a,b)."));
		store.add(DlgpParser.parseAtom("p(b,c)."));
		store.add(DlgpParser.parseAtom("p(e,f)."));

		int i = 0;
		for (CloseableIterator<Term> it = store.termsIterator(); it.hasNext(); it
				.next()) {
			++i;
		}

		Assert.assertEquals("Wrong number of terms", 5, i);

		i = 0;
		for (CloseableIterator<Constant> it = store.constantsIterator(); it
				.hasNext(); it.next()) {
			++i;
		}

		Assert.assertEquals("Wrong number of constant", 5, i);

	}

	@Theory
	public void isEmpty(AtomSet store) throws AtomSetException, ParseException {
		Assume.assumeTrue(store instanceof TripleStore);

		Assert.assertTrue("Store is empty but isEmpty return false",
				store.isEmpty());
		store.add(DlgpParser.parseAtom("p(a,b)."));
		Assert.assertFalse("Store is not empty but isEmpty return true",
				store.isEmpty());
	}
	
	@Theory
	@Test(expected = WrongArityException.class)
	public void testContains(AtomSet store) throws ParseException, AtomSetException {
		Assume.assumeTrue(store instanceof TripleStore);
		store.contains(DlgpParser.parseAtom("p(a)."));
	}

	@Theory
	@Test(expected = WrongArityException.class)
	public void testAddAllCloseableIterator(AtomSet store) throws AtomSetException {
		Assume.assumeTrue(store instanceof TripleStore);
		store.addAll(new AtomFilterIterator(new DlgpParser("q(a,b). p(a). q(b,c).")));
	}

	@Theory
	@Test(expected = WrongArityException.class)
	public void testRemoveAllCloseableIteratorOfQextendsAtom(AtomSet store) throws AtomSetException {
		Assume.assumeTrue(store instanceof TripleStore);
		store.removeAll(new AtomFilterIterator(new DlgpParser("q(a,b). p(a). q(b,c).")));
	}

	@Theory
	@Test(expected = WrongArityException.class)
	public void testAdd(AtomSet store) throws ParseException, AtomSetException {
		Assume.assumeTrue(store instanceof TripleStore);
		store.add(DlgpParser.parseAtom("p(a)."));
	}

	@Theory
	@Test(expected = WrongArityException.class)
	public void testRemove(AtomSet store) throws ParseException, AtomSetException {
		Assume.assumeTrue(store instanceof TripleStore);
		store.remove(DlgpParser.parseAtom("p(a)."));
	}

	@Theory
	@Test(expected = WrongArityException.class)
	public void testMatch(AtomSet store) throws ParseException, AtomSetException {
		Assume.assumeTrue(store instanceof TripleStore);
		store.match(DlgpParser.parseAtom("p(X)."));
	}
	
	@Theory
	@Test(expected = WrongArityException.class)
	public void testAtomsByPredicate(AtomSet store) throws ParseException, AtomSetException {
		Assume.assumeTrue(store instanceof TripleStore);
		store.atomsByPredicate(new Predicate("p", 1));
	}

	@Theory
	@Test(expected = WrongArityException.class)
	public void testTermsByPredicatePosition(AtomSet store) throws ParseException, AtomSetException {
		Assume.assumeTrue(store instanceof TripleStore);
		store.termsByPredicatePosition(new Predicate("p",1), 1);
	}

}
