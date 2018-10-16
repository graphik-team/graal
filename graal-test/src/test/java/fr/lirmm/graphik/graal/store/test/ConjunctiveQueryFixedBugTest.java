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

import java.util.LinkedList;

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
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.api.homomorphism.Homomorphism;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.api.store.Store;
import fr.lirmm.graphik.graal.api.store.TripleStore;
import fr.lirmm.graphik.graal.core.DefaultConjunctiveQuery;
import fr.lirmm.graphik.graal.core.atomset.graph.DefaultInMemoryGraphStore;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.forward_chaining.StaticChase;
import fr.lirmm.graphik.graal.homomorphism.BacktrackHomomorphism;
import fr.lirmm.graphik.graal.homomorphism.backjumping.NoBackJumping;
import fr.lirmm.graphik.graal.homomorphism.bootstrapper.AllDomainBootstrapper;
import fr.lirmm.graphik.graal.homomorphism.bootstrapper.StarBootstrapper;
import fr.lirmm.graphik.graal.homomorphism.forward_checking.ForwardChecking;
import fr.lirmm.graphik.graal.homomorphism.forward_checking.NoForwardChecking;
import fr.lirmm.graphik.graal.homomorphism.scheduler.ComparableOrderScheduler;
import fr.lirmm.graphik.graal.homomorphism.scheduler.FixedOrderScheduler;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.graal.test.TestUtil;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.IteratorException;
import fr.lirmm.graphik.util.stream.Iterators;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
@RunWith(Theories.class)
public class ConjunctiveQueryFixedBugTest {

	@SuppressWarnings("rawtypes")
	@DataPoints
	public static Homomorphism[] homomorphisms() {
		return TestUtil.getHomomorphisms();
	}

	@DataPoints
	public static AtomSet[] atomset() {
		return TestUtil.getAtomSet();
	}
	
	@DataPoints
	public static ForwardChecking[] forwardChecking() {
		return TestUtil.getForwardChecking();
	}


	/**
	 * Overwriting of answer variable values before creating substitution to
	 * return.
	 */
	@Theory
	public void HomomorphismAnsVarOverwritingBug(Homomorphism<ConjunctiveQuery, AtomSet> h, AtomSet store) {
		try {
			store.addAll(DlgpParser.parseAtomSet("<P>(a,b)."));

			ConjunctiveQuery query = DlgpParser.parseQuery("?(Y) :- <P>(X,Y).");

			CloseableIterator<Substitution> subReader;
			Substitution sub;

			subReader = h.execute(query, store);

			Assert.assertTrue(subReader.hasNext());
			sub = subReader.next();
			Assert.assertEquals(1, sub.getTerms().size());
			Assert.assertEquals(DefaultTermFactory.instance().createConstant("b"),
			    sub.createImageOf(DefaultTermFactory.instance().createVariable("Y")));

			Assert.assertFalse(subReader.hasNext());
			subReader.close();
		} catch (Exception e) {
			Assert.assertTrue(e.getMessage(), false);
		}
	}

	/**
	 * Query using an DefaultInMemoryGraphAtomSet.
	 * 
	 * @param h
	 * @param store
	 */
	@Theory
	public void GraphAtomSetQuery(Homomorphism<ConjunctiveQuery, AtomSet> h, AtomSet store) {
		try {
			InMemoryAtomSet atomset = new DefaultInMemoryGraphStore();
			atomset.add(DlgpParser.parseAtom("<P>(X,Y)."));
			ConjunctiveQuery query = new DefaultConjunctiveQuery(atomset);

			CloseableIterator<Substitution> subReader;

			subReader = h.execute(query, store);

			Assert.assertFalse(subReader.hasNext());
			subReader.close();
		} catch (Exception e) {
			Assert.assertTrue(e.getMessage(), false);
		}
	}
	
	
	@Theory
	public void issue52(Homomorphism<ConjunctiveQuery, AtomSet> h, AtomSet store) {
		try {
			store.addAll(DlgpParser.parseAtomSet("<P0>(a,b),<P1>(b,c),<P2>(c,d),<P2>(a,a)."));

			ConjunctiveQuery query = DlgpParser.parseQuery("?(X0,X1,X2,X3) :- <P0>(X0,X2), <P1>(X2,X3), <P2>(X3,X1).");

			Assert.assertTrue(h.exist(query, store));		
		} catch (Exception e) {
			Assert.assertTrue(e.getMessage(), false);
		}
	}
	
	@Theory
	public void issue52Bis(Homomorphism<ConjunctiveQuery, AtomSet> h, AtomSet store) {
		Assume.assumeFalse(store instanceof TripleStore);

		try {
			store.addAll(DlgpParser.parseAtomSet("<P0>(a,b),<P1>(b,a),<P1>(d,b),<R>(b,c,c),<R>(b,d,d)."));

			ConjunctiveQuery query = DlgpParser.parseQuery("?(X0,X1,X2,X3,X4,X5) :- <P0>(X0,X3), <R>(X3,X4,X5), <P1>(X4,X2), <P1>(X5,X1).");

			Assert.assertTrue(h.exist(query, store));	
		} catch (Exception e) {
			Assert.assertTrue(e.getMessage(), false);
		}
	}
	
	@Theory 
	public void issue80(Homomorphism<ConjunctiveQuery, AtomSet> h, AtomSet store) throws AtomSetException, HomomorphismException, IteratorException {
		store.addAll(DlgpParser.parseAtomSet("<P>(a,b), <P>(b,c), <P>(c,d), <P>(e,c), <P>(f,e)."));
		
		ConjunctiveQuery query = DlgpParser.parseQuery("?(X0,X1,X2,X3) :- <P>(X0,X1), <P>(X1,X2), <P>(X2,X3).");

		CloseableIterator<Substitution> results = h.execute(query, store);
		int nbResults = 0;
		results = Iterators.uniq(results);
		while(results.hasNext()) {
			results.next();
			++nbResults;
		}
		Assert.assertEquals(2, nbResults);
	}
	
	/**
	 * Issue with scheduler hazard ordering, test fail in normal mode but not in debug mode
	 */
	@Test
	public void atomicQueryOverExistentialVar() {
		try {
			Store store = new DefaultInMemoryGraphStore();
			Variable x = DefaultTermFactory.instance().createVariable("X");
			Variable y = DefaultTermFactory.instance().createVariable("Y");
			Homomorphism<ConjunctiveQuery, AtomSet> h = new BacktrackHomomorphism(new FixedOrderScheduler(x, y), StarBootstrapper.instance(), NoForwardChecking.instance(), NoBackJumping.instance());

			store.addAll(DlgpParser.parseAtomSet("<P>(X0, a)."));
		
			ConjunctiveQuery query = DlgpParser.parseQuery("? :- <P>(X,Y).");
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
	public void existentialVariableInDataTest(ForwardChecking fc) throws AtomSetException, ChaseException, HomomorphismException, IteratorException {
		Store store = new DefaultInMemoryGraphStore();
        Homomorphism<ConjunctiveQuery, AtomSet> h = new BacktrackHomomorphism(ComparableOrderScheduler.instance(), AllDomainBootstrapper.instance(), fc, NoBackJumping.instance());

			
		store.addAll(DlgpParser.parseAtomSet("<P>(a,b)."));
		Rule r = DlgpParser.parseRule("<Q>(X,Z) :- <P>(X,Y).");
		LinkedList<Rule> rules = new LinkedList<Rule>();
		rules.add(r);
		StaticChase.executeChase(store, rules);

		ConjunctiveQuery query = DlgpParser.parseQuery("?(X,Y,Z) :- <Q>(X,Y), <P>(X,Z).");

		CloseableIterator<Substitution> subReader;
		Substitution sub;

		subReader = h.execute(query, store);

		Assert.assertTrue(subReader.hasNext());
		sub = subReader.next();
		Assert.assertEquals(3, sub.getTerms().size());
		Assert.assertEquals(sub.createImageOf(DefaultTermFactory.instance().createVariable("X")),
		    DefaultTermFactory.instance().createConstant("a"));
		Assert.assertEquals(sub.createImageOf(DefaultTermFactory.instance().createVariable("Z")),
		    DefaultTermFactory.instance().createConstant("b"));

		Assert.assertFalse(subReader.hasNext());
		subReader.close();

	}
}
