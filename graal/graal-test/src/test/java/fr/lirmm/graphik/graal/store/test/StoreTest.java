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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.IteratorException;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
@RunWith(Theories.class)
public class StoreTest {

	@DataPoints
	public static AtomSet[] getAtomset() {
		List<AtomSet> list = new LinkedList<AtomSet>();
		list.addAll(Arrays.asList(TestUtil.getAtomSet()));
		list.addAll(Arrays.asList(TestUtil.getTripleStores()));
		return list.toArray(new AtomSet[list.size()]);
	}

	@Theory
	public void getPredicates(AtomSet store) throws AtomSetException, IteratorException {
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
	public void addAndContains(AtomSet store) throws AtomSetException, IteratorException {
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
	public void isEmpty(AtomSet store) throws AtomSetException {
		Assert.assertTrue("Store is empty but isEmpty return false",
				store.isEmpty());
		store.add(DlgpParser.parseAtom("p(a,b)."));
		Assert.assertFalse("Store is not empty but isEmpty return true",
				store.isEmpty());
	}

	@Theory
	public void match(AtomSet store) throws AtomSetException, IteratorException {
		store.add(DlgpParser.parseAtom("p(b,a)."));
		store.add(DlgpParser.parseAtom("p(a,a)."));
		store.add(DlgpParser.parseAtom("p(b,b)."));
		store.add(DlgpParser.parseAtom("p(a,c)."));
		store.add(DlgpParser.parseAtom("q(a,a)."));
		store.add(DlgpParser.parseAtom("q(a,b)."));

		Atom a = DlgpParser.parseAtom("p(a,X).");

		CloseableIterator<?> it = store.match(a);
		int cpt = 0;
		while (it.hasNext()) {
			++cpt;
			it.next();
		}

		Assert.assertEquals(2, cpt);
	}

	// @Theory
	// public void atomByPredicate(AtomSet store) throws AtomSetException {
	// store.add(DlgpParser.parseAtom("p(b,a)."));
	// store.add(DlgpParser.parseAtom("p(a,a)."));
	// store.add(DlgpParser.parseAtom("p(b,b)."));
	// store.add(DlgpParser.parseAtom("p(a,c)."));
	// store.add(DlgpParser.parseAtom("q(a,a)."));
	// store.add(DlgpParser.parseAtom("q(a,b)."));
	//
	// Atom a = DlgpParser.parseAtom("p(a,X).");
	//
	// Iterator<?> it = store.atomsByPredicate(new Predicate("p", 2));
	// int cpt = 0;
	// while (it.hasNext()) {
	// ++cpt;
	// it.next();
	// }
	//
	// Assert.assertEquals(4, cpt);
	// }

	@Theory
	public void termsByPredicatePosition(AtomSet store) throws AtomSetException, IteratorException {
		store.add(DlgpParser.parseAtom("p(b,a)."));
		store.add(DlgpParser.parseAtom("p(a,a)."));
		store.add(DlgpParser.parseAtom("p(b,b)."));
		store.add(DlgpParser.parseAtom("p(d,c)."));
		store.add(DlgpParser.parseAtom("q(e,e)."));

		CloseableIterator<?> it = store.termsByPredicatePosition(new Predicate("p", 2), 1);
		int cpt = 0;
		while (it.hasNext()) {
			++cpt;
			it.next();
		}

		Assert.assertEquals(3, cpt);
	}

	@Theory
	public void contains(AtomSet store) throws AtomSetException {
		store.add(DlgpParser.parseAtom("p(b,a)."));
		store.add(DlgpParser.parseAtom("p(a,a)."));
		store.add(DlgpParser.parseAtom("p(b,b)."));
		store.add(DlgpParser.parseAtom("p(a,c)."));
		store.add(DlgpParser.parseAtom("q(a,a)."));
		store.add(DlgpParser.parseAtom("q(a,b)."));

		System.out.println(store.getClass());
		Atom a = DlgpParser.parseAtom("p(a,X).");
		Assert.assertFalse(store.contains(a));

		a = DlgpParser.parseAtom("p(a,a).");
		Assert.assertTrue(store.contains(a));
	}

}
