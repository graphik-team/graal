package fr.lirmm.graphik.graal.backward_chaining.test;

/**
 * 
 */

import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import fr.lirmm.graphik.graal.backward_chaining.PureRewriter;
import fr.lirmm.graphik.graal.backward_chaining.pure.AggregAllRulesOperator;
import fr.lirmm.graphik.graal.backward_chaining.pure.AggregSingleRuleOperator;
import fr.lirmm.graphik.graal.backward_chaining.pure.BasicAggregAllRulesOperator;
import fr.lirmm.graphik.graal.backward_chaining.pure.RewritingOperator;
import fr.lirmm.graphik.graal.backward_chaining.pure.rules.HierarchicalCompilation;
import fr.lirmm.graphik.graal.backward_chaining.pure.rules.IDCompilation;
import fr.lirmm.graphik.graal.backward_chaining.pure.rules.NoCompilation;
import fr.lirmm.graphik.graal.backward_chaining.pure.rules.RulesCompilation;
import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.ruleset.LinkedListRuleSet;
import fr.lirmm.graphik.graal.core.ruleset.RuleSet;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.util.Iterators;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
@RunWith(Theories.class)
public class BackwardChainingTest {

	@DataPoints
	public static RulesCompilation[] compilations() {
		return new RulesCompilation[] { new NoCompilation(),
				new HierarchicalCompilation(), new IDCompilation() };
	}
	
	@DataPoints
	public static RewritingOperator[] operators() {
		return new RewritingOperator[] { new AggregAllRulesOperator(),
				new AggregSingleRuleOperator(),
				new BasicAggregAllRulesOperator() };
	}
	
	/**
	 * Test 1
	 */
	@Theory
	public void Test1(RulesCompilation compilation, RewritingOperator operator) {
		RuleSet rules = new LinkedListRuleSet();
		rules.add(DlgpParser.parseRule("q(X1,X2), ppp(X2) :- r(X1)."));
		rules.add(DlgpParser.parseRule("pp(X) :- ppp(X)."));
		rules.add(DlgpParser.parseRule("p(X) :- pp(X)."));

		ConjunctiveQuery query = DlgpParser
				.parseQuery("?(X) :- q(X, Y), p(Y).");

		compilation.compile(rules.iterator());
		PureRewriter bc = new PureRewriter(query, rules, compilation, operator);
		bc.enableUnfolding(true);
		
		int i = Iterators.count(bc);
		Assert.assertEquals(4, i);
	}
	
	/**
	 * Test 2
	 */
	@Theory
	public void Test2(RulesCompilation compilation, RewritingOperator operator) {
		RuleSet rules = new LinkedListRuleSet();
		rules.add(DlgpParser.parseRule("p(X,Y) :- q(X,Y)."));
		rules.add(DlgpParser.parseRule("q(X,Y) :- p(Y,X)."));

		ConjunctiveQuery query = DlgpParser.parseQuery("?(X,Y) :- p(X,Y).");

		compilation.compile(rules.iterator());
		PureRewriter bc = new PureRewriter(query, rules, compilation, operator);
		bc.enableUnfolding(true);

		int i = Iterators.count(bc);
		Assert.assertEquals(4, i);
	}

	/**
	 * folding on answer variables
	 */
	@Theory
	public void forbiddenFoldingTest(RulesCompilation compilation, RewritingOperator operator) {
		RuleSet rules = new LinkedListRuleSet();
		rules.add(DlgpParser.parseRule("q(Y,X) :- p(X,Y)."));
		rules.add(DlgpParser.parseRule("p(X,Y) :- q(Y,X)."));

		ConjunctiveQuery query = DlgpParser.parseQuery("?(X,Y,Z) :- p(X, Y), q(Y,Z).");

		compilation.compile(rules.iterator());
		PureRewriter bc = new PureRewriter(query, rules, compilation, operator);
		bc.enableUnfolding(true);
		
		int i = Iterators.count(bc);
		Assert.assertEquals(4, i);
	}
	
	@Theory
	public void queriesCover(RulesCompilation compilation, RewritingOperator operator) {
		RuleSet rules = new LinkedListRuleSet();
		rules.add(DlgpParser.parseRule("p(X) :- r(Y, X)."));

		ConjunctiveQuery query = DlgpParser.parseQuery("?(X) :- p(X), r(a,X).");

		compilation.compile(rules.iterator());
		PureRewriter bc = new PureRewriter(query, rules, compilation, operator);
		bc.enableUnfolding(true);
		
		int i = Iterators.count(bc);
		Assert.assertEquals(1, i);
	}

	/**
	 * c(X) :- b(X,Y,Y).
	 *
	 * getBody([X]) => b(X, Y, Y).
	 */
	@Theory
	public void getBody1(RulesCompilation compilation,
			RewritingOperator operator) {
		RuleSet rules = new LinkedListRuleSet();
		rules.add(DlgpParser.parseRule("p(X) :- q(X,Y,Y)."));

		ConjunctiveQuery query = DlgpParser.parseQuery("?(X) :- p(X).");

		compilation.compile(rules.iterator());
		PureRewriter bc = new PureRewriter(query, rules, compilation, operator);
		bc.enableUnfolding(true);

		boolean isFound = false;
		while(bc.hasNext()) {
			ConjunctiveQuery rew = bc.next();
			Iterator<Atom> it = rew.getAtomSet().iterator();
			if(it.hasNext()) {
				Atom a = it.next();
				if(a.getPredicate().equals(new Predicate("q", 3))) {
					isFound = true;
					List<Term> terms = a.getTerms();
					Assert.assertEquals(terms.get(1), terms.get(2));
				}
			}
		}

		Assert.assertTrue("Rewrite not found", isFound);
	}

	/**
	 * c(X) :- b(X,X).
	 *
	 * getBody([X]) => b(X, X).
	 */
	@Theory
	public void getBody2(RulesCompilation compilation,
			RewritingOperator operator) {
		RuleSet rules = new LinkedListRuleSet();
		rules.add(DlgpParser.parseRule("p(X) :- q(X,X)."));

		ConjunctiveQuery query = DlgpParser.parseQuery("?(X) :- p(X).");

		compilation.compile(rules.iterator());
		PureRewriter bc = new PureRewriter(query, rules, compilation, operator);
		bc.enableUnfolding(true);

		boolean isFound = false;
		while (bc.hasNext()) {
			ConjunctiveQuery rew = bc.next();
			Iterator<Atom> it = rew.getAtomSet().iterator();
			if (it.hasNext()) {
				Atom a = it.next();
				if (a.getPredicate().equals(new Predicate("q", 2))) {
					isFound = true;
					List<Term> terms = a.getTerms();
					Assert.assertEquals(terms.get(0), terms.get(1));
				}
			}
		}

		Assert.assertTrue("Rewrite not found", isFound);

	}

	/**
	 * c(X) :- p(X,Y,X). p(X,Y,X) :- a(X).
	 *
	 * ?(X) :- c(X).
	 */
	@Theory
	public void getUnification(RulesCompilation compilation,
			RewritingOperator operator) {
		RuleSet rules = new LinkedListRuleSet();
		rules.add(DlgpParser.parseRule("p(X) :- q(X,Y,X)."));
		rules.add(DlgpParser.parseRule("q(X,Y,X) :- s(X)."));

		ConjunctiveQuery query = DlgpParser.parseQuery("?(X) :- p(X).");

		compilation.compile(rules.iterator());
		PureRewriter bc = new PureRewriter(query, rules, compilation, operator);
		bc.enableUnfolding(true);

		int i = Iterators.count(bc);
		Assert.assertEquals(3, i);
	}

}
