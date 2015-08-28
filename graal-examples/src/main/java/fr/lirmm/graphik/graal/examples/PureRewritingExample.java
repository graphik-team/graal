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
package fr.lirmm.graphik.graal.examples;

import fr.lirmm.graphik.graal.backward_chaining.BackwardChainer;
import fr.lirmm.graphik.graal.backward_chaining.pure.PureRewriter;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.factory.ConjunctiveQueryFactory;
import fr.lirmm.graphik.graal.core.ruleset.LinkedListRuleSet;
import fr.lirmm.graphik.graal.core.ruleset.RuleSet;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class PureRewritingExample {

	public static void main(String args[]) throws Exception {
		
		// Query
		ConjunctiveQuery query = ConjunctiveQueryFactory.instance().create(DlgpParser.parseQuery("?(X) :- f(X)."));
		
		// RuleSet
		RuleSet rules = new LinkedListRuleSet();
		rules.add(DlgpParser.parseRule("b(X) :- a(X)."));
		rules.add(DlgpParser.parseRule("c(X) :- b(X)."));
		rules.add(DlgpParser.parseRule("d(X) :- c(X)."));
		rules.add(DlgpParser.parseRule("e(X) :- d(X)."));
		rules.add(DlgpParser.parseRule("f(X) :- e(X)."));
		rules.add(DlgpParser.parseRule("f(X) :- p(X,Y), q(X)."));
		rules.add(DlgpParser.parseRule("q(X) :- r(X)."));
		rules.add(DlgpParser.parseRule("r(X) :- s(X)."));
		
		BackwardChainer bc = new PureRewriter(query, rules);
		
		while(bc.hasNext()) {
			System.out.println(bc.next());
		}
	}
}
