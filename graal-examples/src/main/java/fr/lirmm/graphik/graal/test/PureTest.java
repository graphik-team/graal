/**
 * 
 */
package fr.lirmm.graphik.graal.test;

import java.util.Collection;
import fr.lirmm.graphik.graal.backward_chaining.pure.QREAggregSingleRule;
import fr.lirmm.graphik.graal.backward_chaining.pure.QueryRewritingEngine;
import fr.lirmm.graphik.graal.backward_chaining.pure.queries.PureQuery;
import fr.lirmm.graphik.graal.backward_chaining.pure.rules.IDCompilation;
import fr.lirmm.graphik.graal.backward_chaining.pure.rules.RulesCompilation;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.ruleset.LinkedListRuleSet;
import fr.lirmm.graphik.graal.core.ruleset.RuleSet;
import fr.lirmm.graphik.graal.io.dlgp.DlgpParser;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class PureTest {

	public static void main(String args[]) throws Exception {
		
		// Query
		PureQuery query = new PureQuery(DlgpParser.parseQuery("?(X) :- r(a)."));
		
		// RuleSet
		RuleSet rules = new LinkedListRuleSet();
		rules.add(DlgpParser.parseRule("r(Y), q(X) :- p(X)."));

		
		RulesCompilation comp = new IDCompilation(rules);
		
		QueryRewritingEngine qre = new QREAggregSingleRule(query, rules, comp);
		
		Collection<ConjunctiveQuery> rewrites = null;
		
		
		rewrites = qre.computeRewritings();
		
		for(ConjunctiveQuery q : rewrites) {
			System.out.println(q);
		}
	}
}
