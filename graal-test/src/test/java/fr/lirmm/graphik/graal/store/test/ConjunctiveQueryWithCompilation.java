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
package fr.lirmm.graphik.graal.store.test;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.RuleSet;
import fr.lirmm.graphik.graal.api.core.RulesCompilation;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.homomorphism.Homomorphism;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismWithCompilation;
import fr.lirmm.graphik.graal.api.store.TripleStore;
import fr.lirmm.graphik.graal.core.DefaultConjunctiveQuery;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.ruleset.LinkedListRuleSet;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.forward_chaining.StaticChase;
import fr.lirmm.graphik.graal.homomorphism.AtomicQueryHomomorphism;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.graal.test.HierachicalCompilationFactory;
import fr.lirmm.graphik.graal.test.IDCompilationFactory;
import fr.lirmm.graphik.graal.test.RulesCompilationFactory;
import fr.lirmm.graphik.graal.test.TestUtil;
import fr.lirmm.graphik.util.stream.CloseableIterator;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
@RunWith(Theories.class)
public class ConjunctiveQueryWithCompilation {
	
	private static RulesCompilationFactory[] comps = {IDCompilationFactory.instance(), HierachicalCompilationFactory.instance()};

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	@DataPoints
	public static AtomSet[] atomset() {
		return TestUtil.getAtomSet();
	}

	@SuppressWarnings("rawtypes")
	@DataPoints
	public static Homomorphism[] homomorphisms() {
		return TestUtil.getHomomorphisms();
	}
	
	@DataPoints
	public static RulesCompilationFactory[] compilations() {
		return comps;
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// TEST CASES
	// /////////////////////////////////////////////////////////////////////////

	
	@Theory
	public void test1(Homomorphism<ConjunctiveQuery, AtomSet> hh, RulesCompilationFactory factory, AtomSet store) throws Exception {
		Assume.assumeFalse(store instanceof TripleStore);
		Assume.assumeTrue(hh instanceof HomomorphismWithCompilation);

		HomomorphismWithCompilation<ConjunctiveQuery, AtomSet> h = (HomomorphismWithCompilation<ConjunctiveQuery, AtomSet>) hh;
		
		store.add(DlgpParser.parseAtom("<P>(a)."));

		RuleSet rules = new LinkedListRuleSet();
		rules.add(DlgpParser.parseRule("<Q>(X) :- <P>(X)."));

		RulesCompilation comp = factory.create();
		comp.compile(rules.iterator());
		StaticChase.executeChase(store, rules);

		CloseableIterator<Substitution> results = h.execute(DlgpParser.parseQuery("?(X) :- <Q>(X)."), store, comp);
		Assert.assertTrue(results.hasNext());
		Substitution next = results.next();
		Assert.assertEquals(DefaultTermFactory.instance().createConstant("a"),
		    next.createImageOf(DefaultTermFactory.instance().createVariable("X")));
		Assert.assertFalse(results.hasNext());
		results.close();
	}


	@Theory
	public void test2(Homomorphism<ConjunctiveQuery, AtomSet> hh, RulesCompilationFactory factory, AtomSet store)
	    throws Exception {
		Assume.assumeTrue(hh instanceof HomomorphismWithCompilation);
		HomomorphismWithCompilation<ConjunctiveQuery, AtomSet> h = (HomomorphismWithCompilation<ConjunctiveQuery, AtomSet>) hh;
		
		store.add(DlgpParser.parseAtom("<P>(a,b)."));

		RuleSet rules = new LinkedListRuleSet();
		rules.add(DlgpParser.parseRule("<Q>(X,Y) :- <P>(Y,X)."));

		RulesCompilation comp = factory.create();
		comp.compile(rules.iterator());
		StaticChase.executeChase(store, rules);
		
		CloseableIterator<Substitution> results = h.execute(DlgpParser.parseQuery("?(X,Y) :- <Q>(X,Y)."), store, comp);

		Assert.assertTrue(results.hasNext());
		Substitution next = results.next();
		Assert.assertEquals(DefaultTermFactory.instance().createConstant("a"),
		    next.createImageOf(DefaultTermFactory.instance().createVariable("Y")));
		Assert.assertEquals(DefaultTermFactory.instance().createConstant("b"),
		    next.createImageOf(DefaultTermFactory.instance().createVariable("X")));
		Assert.assertFalse(results.hasNext());

		results.close();
	}
	
	@Theory
	public void backtrackHomomorphismBootstrapperTest1(Homomorphism<ConjunctiveQuery, AtomSet> hh, RulesCompilationFactory factory, AtomSet store) throws Exception {
		Assume.assumeFalse(store instanceof TripleStore);
		Assume.assumeTrue(hh instanceof HomomorphismWithCompilation);
		
		HomomorphismWithCompilation<ConjunctiveQuery, AtomSet> h = (HomomorphismWithCompilation<ConjunctiveQuery, AtomSet>) hh;
					
		store.addAll(DlgpParser.parseAtomSet("<P>(b,a)."));
		
		RuleSet rules = new LinkedListRuleSet();
		rules.add(DlgpParser.parseRule("<Q>(X,Y) :- <P>(X,Y)."));
		
		RulesCompilation comp = factory.create();
		comp.compile(rules.iterator());
		StaticChase.executeChase(store, rules);

		ConjunctiveQuery query = DlgpParser.parseQuery("?(X) :- <Q>(b,X).");

		CloseableIterator<Substitution> subReader;
		Substitution sub;

		subReader = h.execute(query, store, comp);

		Assert.assertTrue(subReader.hasNext());
		sub = subReader.next();
		Assert.assertEquals(DefaultTermFactory.instance().createConstant("a"), sub.createImageOf(DefaultTermFactory.instance().createVariable("X")));
		Assert.assertFalse(subReader.hasNext());
		subReader.close();
	}
	
	@Theory
	public void backtrackHomomorphismBootstrapperTest2(Homomorphism<ConjunctiveQuery, AtomSet> hh, RulesCompilationFactory factory, AtomSet store) throws Exception {
		Assume.assumeFalse(store instanceof TripleStore);
		Assume.assumeTrue(hh instanceof HomomorphismWithCompilation);
		
		HomomorphismWithCompilation<ConjunctiveQuery, AtomSet> h = (HomomorphismWithCompilation<ConjunctiveQuery, AtomSet>) hh;
		
		store.addAll(DlgpParser.parseAtomSet("<P>(b,a)."));
		
		RuleSet rules = new LinkedListRuleSet();
		rules.add(DlgpParser.parseRule("<Q>(X,Y) :- <P>(X,Y)."));
		
		RulesCompilation comp = factory.create();
		comp.compile(rules.iterator());
		StaticChase.executeChase(store, rules);

		ConjunctiveQuery query = DlgpParser.parseQuery("?(X) :- <Q>(X,a).");

		CloseableIterator<Substitution> subReader;
		Substitution sub;

		subReader = h.execute(query, store, comp);

		Assert.assertTrue(subReader.hasNext());
		sub = subReader.next();
		Assert.assertEquals(DefaultTermFactory.instance().createConstant("b"), sub.createImageOf(DefaultTermFactory.instance().createVariable("X")));
		Assert.assertFalse(subReader.hasNext());
		subReader.close();
	}
	

	@Theory
	public void issue34(Homomorphism<ConjunctiveQuery, AtomSet> hh, RulesCompilationFactory factory, AtomSet store)
	    throws Exception {
		Assume.assumeTrue(hh instanceof HomomorphismWithCompilation);
		HomomorphismWithCompilation<ConjunctiveQuery, AtomSet> h = (HomomorphismWithCompilation<ConjunctiveQuery, AtomSet>) hh;

		store.add(DlgpParser.parseAtom("<Q>(a,b)."));

		RuleSet rules = new LinkedListRuleSet();
		rules.add(DlgpParser.parseRule("<P>(X,Y) :- <Q>(Y,X)."));

		RulesCompilation comp = factory.create();
		comp.compile(rules.iterator());
		StaticChase.executeChase(store, rules);
		
		InMemoryAtomSet query1 = new LinkedListAtomSet();
		query1.add(DlgpParser.parseAtom("<P>(a,Y)."));

		CloseableIterator<Substitution> results = h.execute(new DefaultConjunctiveQuery(query1), store, comp);

		Assert.assertFalse(results.hasNext());
		results.close();
	}

	@Theory
	public void issue35(Homomorphism<ConjunctiveQuery, AtomSet> hh, RulesCompilationFactory factory, AtomSet store)
	    throws Exception {
		Assume.assumeFalse(store instanceof TripleStore);
		Assume.assumeTrue(hh instanceof HomomorphismWithCompilation);
		HomomorphismWithCompilation<ConjunctiveQuery, AtomSet> h = (HomomorphismWithCompilation<ConjunctiveQuery, AtomSet>) hh;
		
		store.addAll(DlgpParser.parseAtomSet("<P>(a,a), <R>(a,b,b)."));

		RuleSet rules = new LinkedListRuleSet();
		rules.add(DlgpParser.parseRule("<Q>(X,Y,X) :- <P>(X,Y)."));

		ConjunctiveQuery query = DlgpParser.parseQuery("? :- <Q>(X,Y,Y), <R>(X,Y,Z).");
		RulesCompilation comp = factory.create();
		comp.compile(rules.iterator());
		StaticChase.executeChase(store, rules);

		CloseableIterator<Substitution> results = h.execute(new DefaultConjunctiveQuery(query), store, comp);

		Assert.assertFalse(results.hasNext());
		results.close();

	}
	
	@Theory
	public void issueWithAtom2SubstitutionConverter(RulesCompilationFactory factory, AtomSet store)
	    throws Exception {
		Assume.assumeFalse(store instanceof TripleStore);
		HomomorphismWithCompilation<ConjunctiveQuery, AtomSet> h = AtomicQueryHomomorphism.instance();
		
		store.addAll(DlgpParser.parseAtomSet("<P>(a,a)."));

		RuleSet rules = new LinkedListRuleSet();
		rules.add(DlgpParser.parseRule("<Q>(X,Y,X) :- <P>(X,Y)."));

		RulesCompilation comp = factory.create();
		comp.compile(rules.iterator());
		StaticChase.executeChase(store, rules);

		ConjunctiveQuery query = DlgpParser.parseQuery("?(X) :- <Q>(X,Y,Y).");
		CloseableIterator<Substitution> results = h.execute(new DefaultConjunctiveQuery(query), store, comp);
		Assert.assertTrue(results.hasNext());
		results.close();
		
		query = DlgpParser.parseQuery("?(Y) :- <Q>(X,Y,Y).");
		results = h.execute(new DefaultConjunctiveQuery(query), store, comp);
		Assert.assertTrue(results.hasNext());
		results.close();
		
	}

}
