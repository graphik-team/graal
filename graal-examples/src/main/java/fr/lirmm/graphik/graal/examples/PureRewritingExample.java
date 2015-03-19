/**
 * 
 */
package fr.lirmm.graphik.graal.examples;

import fr.lirmm.graphik.graal.backward_chaining.BackwardChainer;
import fr.lirmm.graphik.graal.backward_chaining.PureRewriter;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.DefaultConjunctiveQuery;
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
		ConjunctiveQuery query = new DefaultConjunctiveQuery(DlgpParser.parseQuery("?(X) :- f(X)."));
		
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
