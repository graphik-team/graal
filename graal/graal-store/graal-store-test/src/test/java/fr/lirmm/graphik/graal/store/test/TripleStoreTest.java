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
package fr.lirmm.graphik.graal.store.test;

import java.util.Iterator;

import org.junit.Assert;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theory;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.core.term.Term;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.graal.store.TripleStore;


/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class TripleStoreTest {

	@DataPoints
	public static TripleStore[] getTriplesStores() {
		return TestUtil.getTripleStores();
	}

	@Theory
	public void simpleTest(TripleStore store) throws AtomSetException {
		Term t1 = DefaultTermFactory.instance()
				.createConstant("http://to.to/b");
		Term t2 = DefaultTermFactory.instance()
				.createConstant("http://to.to/a");
		Predicate p = new Predicate("http://to.to/p", 2);
		Atom atom1 = new DefaultAtom(p, t1, t2);

		store.add(atom1);

		int i = 0;
		for (Iterator<Atom> it = store.iterator(); it.hasNext(); it.next()) {
			++i;
		}

		Assert.assertEquals(1, i);
	}

	@Theory
	public void getPredicates(TripleStore store) throws AtomSetException {
		store.add(DlgpParser.parseAtom("r(a,b)."));
		store.add(DlgpParser.parseAtom("s(a,b)."));
		store.add(DlgpParser.parseAtom("s(a,c)."));

		int i = 0;
		for (Iterator<Predicate> it = store.predicatesIterator(); it.hasNext(); it
				.next()) {
			++i;
		}

		Assert.assertEquals(2, i);
	}

	@Theory
	public void addAndContains(TripleStore store) throws AtomSetException {
		store.add(DlgpParser.parseAtom("p(a,b)."));
		store.add(DlgpParser.parseAtom("q(b,c)."));

		int i = 0;
		for (Iterator<Atom> it = store.iterator(); it.hasNext(); it.next()) {
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
	public void getTerms(TripleStore store) throws AtomSetException {
		store.add(DlgpParser.parseAtom("p(a,b)."));
		store.add(DlgpParser.parseAtom("p(b,c)."));
		store.add(DlgpParser.parseAtom("p(e,f)."));

		int i = 0;
		for (Iterator<Term> it = store.getTerms().iterator(); it.hasNext(); it
				.next()) {
			++i;
		}

		Assert.assertEquals("Wrong number of terms", 5, i);

		i = 0;
		for (Iterator<Term> it = store.getTerms(Term.Type.CONSTANT).iterator(); it
				.hasNext(); it.next()) {
			++i;
		}

		Assert.assertEquals("Wrong number of constant", 5, i);

	}

	@Theory
	public void isEmpty(TripleStore store) throws AtomSetException {
		Assert.assertTrue("Store is empty but isEmpty return false",
				store.isEmpty());
		store.add(DlgpParser.parseAtom("p(a,b)."));
		Assert.assertFalse("Store is not empty but isEmpty return true",
				store.isEmpty());
	}

}
