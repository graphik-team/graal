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
package fr.lirmm.graphik.graal.forward_chaining;

import java.io.IOException;
import java.util.LinkedList;

import org.junit.Assert;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.GraphOfRuleDependencies;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.RuleSet;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.forward_chaining.Chase;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismFactoryException;
import fr.lirmm.graphik.graal.api.io.ParseException;
import fr.lirmm.graphik.graal.core.grd.DefaultGraphOfRuleDependencies;
import fr.lirmm.graphik.graal.core.ruleset.LinkedListRuleSet;
import fr.lirmm.graphik.graal.homomorphism.SmartHomomorphism;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.graal.test.TestUtil;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.IteratorException;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
@RunWith(Theories.class)
public class ChaseTest {

	@DataPoints
	public static AtomSet[] getAtomSet() {
		return TestUtil.getAtomSet();
	}
	
	@Theory
	public void test1(AtomSet atomSet)
	    throws AtomSetException, HomomorphismFactoryException, HomomorphismException, ChaseException,
	    IteratorException, ParseException {

		atomSet.addAll(DlgpParser.parseAtomSet("<P>(X,a),<Q>(a,a)."));

		LinkedList<Rule> ruleSet = new LinkedList<>();
		ruleSet.add(DlgpParser.parseRule("<Q>(X,Y) :- <P>(X,Y)."));

		Chase chase = new BreadthFirstChase(ruleSet, atomSet);
		chase.execute();
		
		ConjunctiveQuery query = DlgpParser.parseQuery("? :- <P>(X,Y),<Q>(X,Y).");
		Assert.assertTrue(SmartHomomorphism.instance().execute(query, atomSet).hasNext());
	}
	
	@Theory
	public void restrictedChaseTest0(AtomSet atomSet)
	    throws AtomSetException, HomomorphismFactoryException, HomomorphismException, ChaseException,
	    IteratorException, ParseException {
		atomSet.addAll(DlgpParser.parseAtomSet("<P>(a,a)."));

		LinkedList<Rule> ruleSet = new LinkedList<>();
		ruleSet.add(DlgpParser.parseRule("<Q>(X,Z) :- <P>(X,X)."));

		Chase chase = new BreadthFirstChase(ruleSet, atomSet);
		chase.execute();

		int size = 0;
		for (CloseableIterator<Atom> it = atomSet.iterator(); it.hasNext(); it.next()) {
			++size;
		}

		Assert.assertEquals(2, size);
	}

	@Theory
	public void restrictedChaseTest(AtomSet atomSet)
	    throws AtomSetException, HomomorphismFactoryException, HomomorphismException, ChaseException,
	    IteratorException, ParseException {
		atomSet.addAll(DlgpParser.parseAtomSet("<P>(a,a)."));
		
		LinkedList<Rule> ruleSet = new LinkedList<>();
		ruleSet.add(DlgpParser.parseRule("<Q>(X,Z) :- <P>(X,X)."));
		ruleSet.add(DlgpParser.parseRule("<R>(X,Z) :- <Q>(X,Y)."));
		ruleSet.add(DlgpParser.parseRule("<Q>(X,Z) :- <R>(X,Y)."));
		ruleSet.add(DlgpParser.parseRule("<S>(X,X) :- <Q>(Y,X)."));

		Chase chase = new BreadthFirstChase(ruleSet, atomSet);
		chase.execute();
		
		int size = 0;
		for (CloseableIterator<Atom> it = atomSet.iterator(); it.hasNext(); it.next()) {
			++size;
		}
		
		Assert.assertEquals(4, size);
	}

	@Theory
	public void restrictedChaseTestWithGrd(InMemoryAtomSet atomSet) throws IOException, ChaseException, ParseException,
	    AtomSetException, IteratorException {
		atomSet.addAll(DlgpParser.parseAtomSet("<P>(a,a)."));

		LinkedList<Rule> ruleSet = new LinkedList<>();
		ruleSet.add(DlgpParser.parseRule("<Q>(X,Z) :- <P>(X,X)."));
		ruleSet.add(DlgpParser.parseRule("<R>(X,Z) :- <Q>(X,Y)."));
		ruleSet.add(DlgpParser.parseRule("<Q>(X,Z) :- <R>(X,Y)."));
		ruleSet.add(DlgpParser.parseRule("<S>(X,X) :- <Q>(Y,X)."));

		GraphOfRuleDependencies grd = new DefaultGraphOfRuleDependencies(ruleSet);
		Chase chase = new ChaseWithGRDAndUnfiers<AtomSet>(grd, atomSet);
		chase.execute();
		
		int size = 0;
		for (CloseableIterator<Atom> it = atomSet.iterator(); it.hasNext(); it.next()) {
			++size;
		}
		
		Assert.assertEquals(4, size);
	}
	
	// @Theory
	// public void coreChaseTest(AtomSet atomSet) throws AtomSetException,
	// HomomorphismFactoryException,
	// HomomorphismException, ChaseException {
	// atomSet.addAll(DlgpParser.parseAtomSet("e(X,Y), e(Y,X)."));
	//
	// LinkedList<Rule> ruleSet = new LinkedList<Rule>();
	// ruleSet.add(DlgpParser.parseRule("e(Z,Z) :- e(X,Y), e(Y,X)."));
	// ruleSet.add(DlgpParser.parseRule("e(X,Z), e(Z,Y) :- e(X,Y)."));
	//
	// Chase chase = new DefaultChase(ruleSet, atomSet,
	// RecursiveBacktrackHomomorphism.instance(),
	// new CoreChaseStopCondition());
	// chase.execute();
	//
	// int size = 0;
	// for (Iterator<Atom> it = atomSet.iterator(); it.hasNext(); it.next()) {
	// if (++size > 3)
	// Assert.assertFalse(true);
	// }
	//
	// Assert.assertTrue(true);
	// }
	//
	// @Theory
	// public void coreChaseTest2(AtomSet atomSet) throws AtomSetException,
	// HomomorphismFactoryException,
	// HomomorphismException, ChaseException {
	// atomSet.addAll(DlgpParser.parseAtomSet("e(X,Y), e(Y,Z)."));
	//
	// LinkedList<Rule> ruleSet = new LinkedList<Rule>();
	// ruleSet.add(DlgpParser.parseRule("e(X,Z) :- e(X,Y), e(Y,Z)."));
	//
	// Chase chase = new DefaultChase(ruleSet, atomSet,
	// RecursiveBacktrackHomomorphism.instance(),
	// new CoreChaseStopCondition());
	// chase.execute();
	//
	// ConjunctiveQuery query = DlgpParser.parseQuery("? :-
	// e(X,Y),e(Y,Z),e(X,Z).");
	// Assert.assertTrue(StaticHomomorphism.instance().execute(query,
	// atomSet).hasNext());
	// }

	@Theory
	public void test2(InMemoryAtomSet atomSet)
	    throws ChaseException, HomomorphismFactoryException, HomomorphismException, IteratorException, ParseException {

		// add assertions into this atom set
		atomSet.add(DlgpParser.parseAtom("<P>(a,a)."));
		atomSet.add(DlgpParser.parseAtom("<P>(c,c)."));
		atomSet.add(DlgpParser.parseAtom("<Q>(b,b)."));
		atomSet.add(DlgpParser.parseAtom("<Q>(c,c)."));
		atomSet.add(DlgpParser.parseAtom("<S>(z,z)."));
		
		// /////////////////////////////////////////////////////////////////////
		// create a rule set
		RuleSet ruleSet = new LinkedListRuleSet();
		
		// add a rule into this rule set
		ruleSet.add(DlgpParser.parseRule("<R>(X,X) :- <P>(X,X), <Q>(X,X)."));
		ruleSet.add(DlgpParser.parseRule("<S>(X, Y) :- <P>(X,X), <Q>(Y,Y)."));
		
		// /////////////////////////////////////////////////////////////////////
		// run saturation
		StaticChase.executeChase(atomSet, ruleSet);
		
		// /////////////////////////////////////////////////////////////////////
		// execute query
		ConjunctiveQuery query = DlgpParser.parseQuery("?(X,Y) :- <S>(X, Y), <P>(X,X), <Q>(Y,Y).");
		CloseableIterator<Substitution> subReader = SmartHomomorphism.instance().execute(query, atomSet);
		Assert.assertTrue(subReader.hasNext());
	}
	
}
