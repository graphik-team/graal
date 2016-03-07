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
 package fr.lirmm.graphik.graal.trash;
import java.util.LinkedList;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Query;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.RuleSet;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismFactoryException;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.factory.ConjunctiveQueryFactory;
import fr.lirmm.graphik.graal.core.ruleset.LinkedListRuleSet;
import fr.lirmm.graphik.graal.forward_chaining.StaticChase;
import fr.lirmm.graphik.graal.homomorphism.StaticHomomorphism;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.GIterator;



/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class TestApplyRules {
	
	public static void main(String[] args) throws AtomSetException, HomomorphismFactoryException, HomomorphismException, ChaseException {
		
		AtomSet atomSet = new LinkedListAtomSet();
		
		atomSet.addAll(DlgpParser.parseAtomSet("p(X,a),q(a,a)."));

		RuleSet ruleSet = new LinkedListRuleSet();
		ruleSet.add(DlgpParser.parseRule("q(X,Y) :- p(X,Y)."));

		StaticChase.executeChase(atomSet, ruleSet);
	

		System.out.println("#####################");
		for(Atom a : atomSet) {
			System.out.println(a);
		}

		Query query = DlgpParser.parseQuery("?(X) :- p(X,Y),q(X,Y).");
		
		GIterator<Substitution> sub = StaticHomomorphism.instance().execute(query, atomSet);
		if(sub.hasNext()) {
			sub.next();
			System.out.println("ok");
		} else {
			System.out.println("nok");
		}
		
	}
	
	public static void applyRule(Rule rule, AtomSet atomSet) throws AtomSetException, HomomorphismFactoryException, HomomorphismException {
		Query query = ConjunctiveQueryFactory.instance().create(rule.getBody().iterator(),
		        rule.getFrontier().iterator());
		CloseableIterator<Substitution> reader = StaticHomomorphism.instance().execute(query, atomSet);
		while (reader.hasNext()) {
			Substitution s = reader.next();
			System.out.print(s);
			AtomSet tmp = substitute(s, rule.getHead());
			System.out.println(" -> " + tmp);
			atomSet.addAll(tmp);
		}
	}
	
	public static InMemoryAtomSet substitute(Substitution s, InMemoryAtomSet atomSet) {
		InMemoryAtomSet newAtomSet = new LinkedListAtomSet();
		for(Atom a : atomSet) {
			newAtomSet.add(s.createImageOf(a));
		}
		
		return newAtomSet;
	}
	
	public static void test() throws AtomSetException, HomomorphismFactoryException, HomomorphismException, ChaseException {
		AtomSet atomSet = new LinkedListAtomSet();
		atomSet.addAll(DlgpParser.parseAtomSet("p(X,a),q(a,a)."));

		LinkedList<Rule> ruleSet = new LinkedList<Rule>();
		ruleSet.add(DlgpParser.parseRule("q(X,Y) :- p(X,Y)."));

		StaticChase.executeChase(atomSet, ruleSet);

		
		System.out.println("#####################");
		for(Atom a : atomSet) {
			System.out.println(a);
		}
		
		Query query = DlgpParser.parseQuery("?(X,Y) :- p(X,Y).q(X,Y).");
		if (StaticHomomorphism.instance().execute(query, atomSet).hasNext()) {
			System.out.println("ok");
		} else {
			System.out.println("nok");
		}
	}
}

