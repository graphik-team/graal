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
 package fr.lirmm.graphik.graal.trash;
import java.util.LinkedList;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.Query;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.Substitution;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
import fr.lirmm.graphik.graal.core.atomset.InMemoryAtomSet;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.factory.ConjunctiveQueryFactory;
import fr.lirmm.graphik.graal.core.ruleset.LinkedListRuleSet;
import fr.lirmm.graphik.graal.core.ruleset.RuleSet;
import fr.lirmm.graphik.graal.core.stream.SubstitutionReader;
import fr.lirmm.graphik.graal.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.forward_chaining.StaticChase;
import fr.lirmm.graphik.graal.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.homomorphism.HomomorphismFactoryException;
import fr.lirmm.graphik.graal.homomorphism.StaticHomomorphism;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;



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
		
		SubstitutionReader sub = StaticHomomorphism.executeQuery(query, atomSet);
		if(sub.hasNext()) {
			sub.next();
			System.out.println("ok");
		} else {
			System.out.println("nok");
		}
		
	}
	
	public static void applyRule(Rule rule, AtomSet atomSet) throws AtomSetException, HomomorphismFactoryException, HomomorphismException {
		Query query = ConjunctiveQueryFactory.instance().create(rule.getBody(), rule.getFrontier());
		SubstitutionReader reader = StaticHomomorphism.executeQuery(query, atomSet);
		for(Substitution s : reader) {
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
		if(StaticHomomorphism.executeQuery(query, atomSet).hasNext()) {
			System.out.println("ok");
		} else {
			System.out.println("nok");
		}
	}
}

