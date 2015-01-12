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
import fr.lirmm.graphik.graal.io.dlp.DlpParser;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class PureRewritingExample {

	public static void main(String args[]) throws Exception {
		
		// Query
		ConjunctiveQuery query = new DefaultConjunctiveQuery(DlpParser.parseQuery("?(X) :- f(X)."));
		
		// RuleSet
		RuleSet rules = new LinkedListRuleSet();
		rules.add(DlpParser.parseRule("b(X) :- a(X)."));
		rules.add(DlpParser.parseRule("c(X) :- b(X)."));
		rules.add(DlpParser.parseRule("d(X) :- c(X)."));
		rules.add(DlpParser.parseRule("e(X) :- d(X)."));
		rules.add(DlpParser.parseRule("f(X) :- e(X)."));
		rules.add(DlpParser.parseRule("f(X) :- p(X,Y), q(X)."));
		rules.add(DlpParser.parseRule("q(X) :- r(X)."));
		rules.add(DlpParser.parseRule("r(X) :- s(X)."));
		
		BackwardChainer bc = new PureRewriter(query, rules);
		
		while(bc.hasNext()) {
			System.out.println(bc.next());
		}
	}
}
