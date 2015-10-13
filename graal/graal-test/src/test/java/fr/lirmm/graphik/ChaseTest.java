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
package fr.lirmm.graphik;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import org.junit.Assert;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Query;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.RuleSet;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.forward_chaining.Chase;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismFactoryException;
import fr.lirmm.graphik.graal.api.io.ParseException;
import fr.lirmm.graphik.graal.core.ruleset.LinkedListRuleSet;
import fr.lirmm.graphik.graal.forward_chaining.ChaseWithGRDAndUnfiers;
import fr.lirmm.graphik.graal.forward_chaining.NaiveChase;
import fr.lirmm.graphik.graal.forward_chaining.StaticChase;
import fr.lirmm.graphik.graal.grd.GraphOfRuleDependencies;
import fr.lirmm.graphik.graal.homomorphism.StaticHomomorphism;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
@RunWith(Theories.class)
public class ChaseTest {

	@DataPoints
	public static AtomSet[] writeableStore() {
		return TestUtil.writeableStore();
	}
	
	@Theory
	public void test1(AtomSet atomSet) throws AtomSetException, HomomorphismFactoryException, HomomorphismException, ChaseException {
		atomSet.addAll(DlgpParser.parseAtomSet("p(X,a),q(a,a)."));

		LinkedList<Rule> ruleSet = new LinkedList<Rule>();
		ruleSet.add(DlgpParser.parseRule("q(X,Y) :- p(X,Y)."));

		Chase chase = new NaiveChase(ruleSet, atomSet);
		chase.execute();
		
		Query query = DlgpParser.parseQuery("? :- p(X,Y),q(X,Y).");
		Assert.assertTrue(StaticHomomorphism.executeQuery(query, atomSet).hasNext());
	}
	
	@Theory
	public void restrictedChaseTest(AtomSet atomSet) throws AtomSetException, HomomorphismFactoryException, HomomorphismException, ChaseException {
		atomSet.addAll(DlgpParser.parseAtomSet("p(a)."));
		
		LinkedList<Rule> ruleSet = new LinkedList<Rule>();
		ruleSet.add(DlgpParser.parseRule("q(X,Z) :- p(X)."));
		ruleSet.add(DlgpParser.parseRule("r(X,Z) :- q(X,Y)."));
		ruleSet.add(DlgpParser.parseRule("q(X,Z) :- r(X,Y)."));
		ruleSet.add(DlgpParser.parseRule("s(X) :- q(Y,X)."));

		Chase chase = new NaiveChase(ruleSet, atomSet);
		chase.execute();
		
		int size = 0;
		for(Iterator<Atom> it = atomSet.iterator(); it.hasNext(); it.next()) {
			++size;
		}
		
		Assert.assertEquals(4, size);
	}
	
	@Theory
	public void restrictedChaseTestWithGrd(InMemoryAtomSet atomSet) throws IOException, ChaseException, ParseException,
	                                                               AtomSetException {
		atomSet.addAll(DlgpParser.parseAtomSet("p(a)."));

		LinkedList<Rule> ruleSet = new LinkedList<Rule>();
		ruleSet.add(DlgpParser.parseRule("q(X,Z) :- p(X)."));
		ruleSet.add(DlgpParser.parseRule("r(X,Z) :- q(X,Y)."));
		ruleSet.add(DlgpParser.parseRule("q(X,Z) :- r(X,Y)."));
		ruleSet.add(DlgpParser.parseRule("s(X) :- q(Y,X)."));

		GraphOfRuleDependencies grd = new GraphOfRuleDependencies(ruleSet);
		Chase chase = new ChaseWithGRDAndUnfiers(grd, atomSet);
		chase.execute();
		
		int size = 0;
		for(Iterator<Atom> it = atomSet.iterator(); it.hasNext(); it.next()) {
			++size;
		}
		
		Assert.assertEquals(4, size);
	}
	
	@Theory
	public void test2(InMemoryAtomSet atomSet) throws ChaseException, HomomorphismFactoryException, HomomorphismException {

		// add assertions into this atom set
		atomSet.add(DlgpParser.parseAtom("p(a)."));
		atomSet.add(DlgpParser.parseAtom("p(c)."));
		atomSet.add(DlgpParser.parseAtom("q(b)."));
		atomSet.add(DlgpParser.parseAtom("q(c)."));
		atomSet.add(DlgpParser.parseAtom("s(z,z)."));
		
		// /////////////////////////////////////////////////////////////////////
		// create a rule set
		RuleSet ruleSet = new LinkedListRuleSet();
		
		// add a rule into this rule set
		ruleSet.add(DlgpParser.parseRule("r(X) :- p(X), q(X)."));
		ruleSet.add(DlgpParser.parseRule("s(X, Y) :- p(X), q(Y)."));
		
		// /////////////////////////////////////////////////////////////////////
		// run saturation
		StaticChase.executeChase(atomSet, ruleSet);
		
		// /////////////////////////////////////////////////////////////////////
		// execute query
		Query query = DlgpParser.parseQuery("?(X,Y) :- s(X, Y), p(X), q(Y).");
		Iterable<Substitution> subReader = StaticHomomorphism.executeQuery(query, atomSet);
		Assert.assertTrue(subReader.iterator().hasNext());
	}
	
//	@Theory
//	public void test2(WriteableAtomSet atomSet) throws AtomSetException {
//		atomSet.add(BasicParser.parse("p(a,Z).q(a,b)"));
//		
//		LinkedList<Rule> ruleSet = new LinkedList<Rule>();
//		ruleSet.add(new BasicRule(BasicParser.parse("p(X,Y)"), BasicParser.parse("q(X,Y)")));
//
//		Util.applyRuleSet(ruleSet, atomSet);
//
//		int size = 0;
//		System.out.println("##################");
//		for(Atom a : atomSet) {
//			++size;
//			System.out.println(a);
//			if(a.getPredicate().getArity() == 2) {
//				System.out.println(a.getTerm(1).getType());
//			}
//		}
//		
//		//Assert.assertEquals(3, size);
//	}
	
	
}
