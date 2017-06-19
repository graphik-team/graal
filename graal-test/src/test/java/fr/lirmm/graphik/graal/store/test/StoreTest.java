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

import java.sql.SQLException;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.forward_chaining.RuleApplicationException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.api.io.ParseException;
import fr.lirmm.graphik.graal.api.store.Store;
import fr.lirmm.graphik.graal.api.store.TripleStore;
import fr.lirmm.graphik.graal.forward_chaining.rule_applier.DefaultRuleApplier;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.graal.store.rdbms.RdbmsStore;
import fr.lirmm.graphik.graal.test.TestUtil;
import fr.lirmm.graphik.util.MethodNotImplementedError;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.IteratorException;
import fr.lirmm.graphik.util.stream.Iterators;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
@RunWith(Theories.class)
public class StoreTest {

	@DataPoints
	public static AtomSet[] getAtomset() {
		return TestUtil.getAtomSet();
	}

	@Theory
	public void atomUnicity(AtomSet store) throws AtomSetException, IteratorException, ParseException {
		store.add(DlgpParser.parseAtom("<R>(a,b)."));
		store.add(DlgpParser.parseAtom("<R>(a,b)."));

		int i = Iterators.count(store.iterator());
		Assert.assertEquals(1, i);
	}

	@Theory
	public void getPredicates(AtomSet store) throws AtomSetException, IteratorException, ParseException {
		store.add(DlgpParser.parseAtom("<R>(a,b)."));
		store.add(DlgpParser.parseAtom("<S>(a,b)."));
		store.add(DlgpParser.parseAtom("<S>(a,c)."));

		int i = 0;
		for (CloseableIterator<Predicate> it = store.predicatesIterator(); it.hasNext(); it
				.next()) {
			++i;
		}

		Assert.assertEquals(2, i);
	}

	@Theory
	public void addAndContains(AtomSet store) throws AtomSetException, IteratorException, ParseException {
		store.add(DlgpParser.parseAtom("<P>(a,b)."));
		store.add(DlgpParser.parseAtom("<Q>(b,c)."));

		int i = 0;
		for (CloseableIterator<Atom> it = store.iterator(); it.hasNext(); it.next()) {
			++i;
		}

		Assert.assertEquals("Store does not contains exactly 2 atoms", 2, i);

		Assert.assertTrue("Store does not contains <P>(a,b)", store.contains(DlgpParser.parseAtom("<P>(a,b).")));
		Assert.assertTrue("Store does not contains <Q>(b,c)", store.contains(DlgpParser.parseAtom("<Q>(b,c).")));

		Assert.assertFalse("Store contains <Q>(c, b)", store.contains(DlgpParser.parseAtom("<Q>(c,b).")));
	}

	@Theory
	public void isEmpty(AtomSet store) throws AtomSetException, ParseException {
		Assert.assertTrue("Store is empty but isEmpty return false",
				store.isEmpty());
		store.add(DlgpParser.parseAtom("<P>(a,b)."));
		Assert.assertFalse("Store is not empty but isEmpty return true",
				store.isEmpty());
	}

	@Theory
	public void match(AtomSet store) throws AtomSetException, IteratorException, ParseException {
		Atom a1 = DlgpParser.parseAtom("<P>(a,a).");
		Atom a2 = DlgpParser.parseAtom("<P>(a,c).");
		
		store.add(DlgpParser.parseAtom("<P>(b,a)."));
		store.add(a1);
		store.add(DlgpParser.parseAtom("<P>(b,b)."));
		store.add(a2);
		store.add(DlgpParser.parseAtom("<Q>(a,a)."));
		store.add(DlgpParser.parseAtom("<Q>(a,b)."));

		Atom q = DlgpParser.parseAtom("<P>(a,X).");
		CloseableIterator<Atom> it = store.match(q);
		
		int cpt = 0;
		while (it.hasNext()) {
			++cpt;
			Atom a = it.next();
			Assert.assertTrue(a.equals(a1) || a.equals(a2));
		}

		Assert.assertEquals(2, cpt);
	}
	
	@Theory
	public void match2(AtomSet store) throws AtomSetException, IteratorException, ParseException {
		Atom a1 = DlgpParser.parseAtom("<P>(a,a).");
		Atom a2 = DlgpParser.parseAtom("<P>(b,b).");
		
		store.add(DlgpParser.parseAtom("<P>(b,a)."));
		store.add(a1);
		store.add(a2);
		store.add(DlgpParser.parseAtom("<P>(a,c)."));
		store.add(DlgpParser.parseAtom("<Q>(a,a)."));
		store.add(DlgpParser.parseAtom("<Q>(a,b)."));

		Atom q = DlgpParser.parseAtom("<P>(X,X).");
		CloseableIterator<Atom> it = store.match(q);
		
		int cpt = 0;
		while (it.hasNext()) {
			++cpt;
			Atom a = it.next();
			Assert.assertTrue(a.equals(a1) || a.equals(a2));
		}

		Assert.assertEquals(2, cpt);
	}
	
	@Theory
	public void match3(AtomSet store) throws AtomSetException, IteratorException, ParseException, HomomorphismException {
		Assume.assumeFalse(store instanceof TripleStore);
		
		Atom a1 = DlgpParser.parseAtom("<P>(a,a,a).");
		Atom a2 = DlgpParser.parseAtom("<P>(a,c,c).");

		store.add(DlgpParser.parseAtom("<P>(b,a,a)."));
		store.add(DlgpParser.parseAtom("<P>(a,a,a)."));
		store.add(DlgpParser.parseAtom("<P>(a,c,b)."));
		store.add(DlgpParser.parseAtom("<P>(a,c,c)."));
		store.add(DlgpParser.parseAtom("<Q>(b,a,c)."));
		store.add(DlgpParser.parseAtom("<Q>(a,b,b)."));

		Atom q = DlgpParser.parseAtom("<P>(a,X,X).");
		CloseableIterator<Atom> it = store.match(q);
		
		int cpt = 0;
		while (it.hasNext()) {
			++cpt;
			Atom a = it.next();
			Assert.assertTrue(a.equals(a1) || a.equals(a2));
		}
		Assert.assertEquals(2, cpt);
	}

	@Theory
	public void atomByPredicate(AtomSet store) throws AtomSetException, IteratorException, ParseException {
		store.add(DlgpParser.parseAtom("<P>(b,a)."));
		store.add(DlgpParser.parseAtom("<P>(a,a)."));
		store.add(DlgpParser.parseAtom("<P>(b,b)."));
		store.add(DlgpParser.parseAtom("<P>(a,c)."));
		store.add(DlgpParser.parseAtom("<Q>(a,a)."));
		store.add(DlgpParser.parseAtom("<Q>(a,b)."));

		CloseableIterator<Atom> it = store.atomsByPredicate(new Predicate("P", 2));
		int count = Iterators.count(it);

		Assert.assertEquals(4, count);
	}

	@Theory
	public void termsByPredicatePosition(AtomSet store) throws AtomSetException, IteratorException, ParseException {
		store.add(DlgpParser.parseAtom("<P>(d,a)."));
		store.add(DlgpParser.parseAtom("<P>(d,a)."));
		store.add(DlgpParser.parseAtom("<P>(d,b)."));
		store.add(DlgpParser.parseAtom("<P>(d,c)."));
		store.add(DlgpParser.parseAtom("<Q>(e,e)."));

		CloseableIterator<?> it = store.termsByPredicatePosition(new Predicate("P", 2), 1);
		int cpt = 0;
		while (it.hasNext()) {
			++cpt;
			it.next();
		}

		Assert.assertEquals(3, cpt);
	}

	@Theory
	public void contains(AtomSet store) throws AtomSetException, ParseException {
		store.add(DlgpParser.parseAtom("<P>(b,a)."));
		store.add(DlgpParser.parseAtom("<P>(a,a)."));
		store.add(DlgpParser.parseAtom("<P>(b,b)."));
		store.add(DlgpParser.parseAtom("<P>(a,c)."));
		store.add(DlgpParser.parseAtom("<Q>(a,a)."));
		store.add(DlgpParser.parseAtom("<Q>(a,b)."));

		Atom a = DlgpParser.parseAtom("<P>(a,X).");
		Assert.assertFalse(store.contains(a));

		a = DlgpParser.parseAtom("<P>(a,a).");
		Assert.assertTrue(store.contains(a));
	}

	@Theory
	public void caseSensitivityTest(AtomSet store) throws AtomSetException, IteratorException, ParseException, SQLException {
		Assume.assumeTrue(!(store instanceof RdbmsStore) || ((RdbmsStore) store).getDriver().isCaseSensitive());
		
		Atom toAdd = DlgpParser.parseAtom("<P>(a,b).");
		Atom toCheck = DlgpParser.parseAtom("<p>(a,b).");
		Predicate predicateToCheck = new Predicate("p", 2);

		store.add(toAdd);

		Assert.assertTrue(store.contains(toAdd));
		Assert.assertFalse(store.contains(toCheck));

		CloseableIterator<?> it = store.termsByPredicatePosition(predicateToCheck, 0);
		Assert.assertFalse(it.hasNext());

		it = store.atomsByPredicate(predicateToCheck);
		Assert.assertFalse(it.hasNext());

		it = store.match(DlgpParser.parseAtom("<p>(X,Y)."));
		Assert.assertFalse(it.hasNext());
	}
	
	
	@Theory
	public void sizeTest(AtomSet atomset) throws ParseException, AtomSetException {
		Assume.assumeTrue(atomset instanceof Store);
		Store store = (Store) atomset;
		
		// given
		store.add(DlgpParser.parseAtom("<P>(b,a)."));
		store.add(DlgpParser.parseAtom("<P>(a,a)."));
		store.add(DlgpParser.parseAtom("<P>(b,b)."));
		store.add(DlgpParser.parseAtom("<P>(a,c)."));
		store.add(DlgpParser.parseAtom("<Q>(a,a)."));
		store.add(DlgpParser.parseAtom("<Q>(a,b)."));

		// when
		int sizeP = store.size(new Predicate("P",2));
		int sizeQ = store.size(new Predicate("Q",2));

		// then
		Assert.assertTrue(4 <= sizeP);
		Assert.assertTrue(2 <= sizeQ);
	}
	
	@Theory
	public void getDomainSizeTest(AtomSet atomset) throws ParseException, AtomSetException {
		Assume.assumeTrue(atomset instanceof Store);
		Store store = (Store) atomset;
		
		// given
		store.add(DlgpParser.parseAtom("<P>(b,a)."));
		store.add(DlgpParser.parseAtom("<P>(a,a)."));
		store.add(DlgpParser.parseAtom("<P>(b,b)."));
		store.add(DlgpParser.parseAtom("<P>(a,c)."));
		store.add(DlgpParser.parseAtom("<Q>(a,a)."));
		store.add(DlgpParser.parseAtom("<Q>(a,b)."));

		// when
		int domainSize = store.getDomainSize();

		// then
		Assert.assertTrue(3 <= domainSize);
	}
	
	@Theory
	public void removeTest(AtomSet store) throws AtomSetException, IteratorException {
		// given
		store.add(DlgpParser.parseAtom("<P>(b,a)."));
		store.add(DlgpParser.parseAtom("<P>(a,a)."));
		store.add(DlgpParser.parseAtom("<P>(b,b)."));
		store.add(DlgpParser.parseAtom("<P>(a,c)."));
		store.add(DlgpParser.parseAtom("<Q>(a,a)."));
		store.add(DlgpParser.parseAtom("<Q>(a,b)."));

		// when
		try {
			store.remove(DlgpParser.parseAtom("<P>(a,c)."));
		} catch (MethodNotImplementedError e) {
			return;
		}

		// then
		Assert.assertFalse(store.contains(DlgpParser.parseAtom("<P>(a,c).")));
		Assert.assertEquals(3, Iterators.count(store.atomsByPredicate(new Predicate("P",2))));
		Assert.assertEquals(2, Iterators.count(store.atomsByPredicate(new Predicate("Q",2))));
	}
	
	@Theory
	public void removeAllTest(AtomSet store) throws AtomSetException, IteratorException {
		// given
		store.add(DlgpParser.parseAtom("<P>(b,a)."));
		store.add(DlgpParser.parseAtom("<P>(a,a)."));
		store.add(DlgpParser.parseAtom("<P>(b,b)."));
		store.add(DlgpParser.parseAtom("<P>(a,c)."));
		store.add(DlgpParser.parseAtom("<Q>(a,a)."));
		store.add(DlgpParser.parseAtom("<Q>(a,b)."));

		// when
		try {
			store.removeAll(DlgpParser.parseAtomSet("<P>(a,c), <Q>(a,a)."));
		} catch (MethodNotImplementedError e) {
			return;
		}

		// then
		Assert.assertFalse(store.contains(DlgpParser.parseAtom("<P>(a,c).")));
		Assert.assertFalse(store.contains(DlgpParser.parseAtom("<Q>(a,a).")));
		Assert.assertEquals(3, Iterators.count(store.atomsByPredicate(new Predicate("P",2))));
		Assert.assertEquals(1, Iterators.count(store.atomsByPredicate(new Predicate("Q",2))));

	}
	
	@Theory
	public void bugConcurrentModificationException(InMemoryAtomSet store)
	    throws IteratorException, RuleApplicationException, AtomSetException {
		Assume.assumeTrue(store instanceof Store);
		
		Rule r = DlgpParser.parseRule("<T>(Z,W), <P>(Y,W) :- <P>(X,Y).");
		store.addAll(DlgpParser.parseAtomSet("<P>(a,a)."));

		try {
			DefaultRuleApplier<InMemoryAtomSet> applier = new DefaultRuleApplier<InMemoryAtomSet>();
			applier.apply(r, store);
			applier.apply(r, store);
		} catch (Exception e) {
			Assert.fail();
		}
	}

}
