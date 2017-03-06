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
package fr.lirmm.graphik.graal.backward_chaining;

import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.RuleSet;
import fr.lirmm.graphik.graal.api.core.RulesCompilation;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.io.ParseException;
import fr.lirmm.graphik.graal.backward_chaining.pure.AggregAllRulesOperator;
import fr.lirmm.graphik.graal.backward_chaining.pure.AggregSingleRuleOperator;
import fr.lirmm.graphik.graal.backward_chaining.pure.BasicAggregAllRulesOperator;
import fr.lirmm.graphik.graal.backward_chaining.pure.PureRewriter;
import fr.lirmm.graphik.graal.backward_chaining.pure.RewritingOperator;
import fr.lirmm.graphik.graal.core.compilation.HierarchicalCompilation;
import fr.lirmm.graphik.graal.core.compilation.IDCompilation;
import fr.lirmm.graphik.graal.core.compilation.NoCompilation;
import fr.lirmm.graphik.graal.core.ruleset.LinkedListRuleSet;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;
import fr.lirmm.graphik.util.stream.IteratorException;
import fr.lirmm.graphik.util.stream.Iterators;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
@RunWith(Theories.class)
public class BackwardChainingTest {

	private static final boolean DEBUG = false;

	@DataPoints
	public static RulesCompilation[] compilations() {
		return new RulesCompilation[] { NoCompilation.instance(),
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
	 * 
	 * @throws IteratorException
	 * @throws ParseException
	 */
	@Theory
	public void Test1(RulesCompilation compilation, RewritingOperator operator)
	    throws IteratorException, ParseException {
		RuleSet rules = new LinkedListRuleSet();
		rules.add(DlgpParser.parseRule("q(X1,X2), ppp(X2) :- r(X1)."));
		rules.add(DlgpParser.parseRule("pp(X) :- ppp(X)."));
		rules.add(DlgpParser.parseRule("p(X) :- pp(X)."));

		ConjunctiveQuery query = DlgpParser
				.parseQuery("?(X) :- q(X, Y), p(Y).");

		compilation.compile(rules.iterator());
		PureRewriter bc = new PureRewriter(operator, true);
		CloseableIterator<? extends ConjunctiveQuery> it = bc.execute(query, rules, compilation);
		
		int i = Iterators.count(it);
		Assert.assertEquals(4, i);
	}
	
	/**
	 * Test 2
	 * 
	 * @throws IteratorException
	 * @throws ParseException
	 */
	@Theory
	public void Test2(RulesCompilation compilation, RewritingOperator operator)
	    throws IteratorException, ParseException {
		RuleSet rules = new LinkedListRuleSet();
		rules.add(DlgpParser.parseRule("p(X,Y) :- q(X,Y)."));
		rules.add(DlgpParser.parseRule("q(X,Y) :- p(Y,X)."));

		ConjunctiveQuery query = DlgpParser.parseQuery("?(X,Y) :- p(X,Y).");

		compilation.compile(rules.iterator());
		PureRewriter bc = new PureRewriter(operator, true);
		CloseableIterator<? extends ConjunctiveQuery> it = bc.execute(query, rules, compilation);
		
		int i = Iterators.count(it);
		Assert.assertEquals(4, i);
	}

	/**
	 * folding on answer variables
	 * 
	 * @throws IteratorException
	 * @throws ParseException
	 */
	@Theory
	public void forbiddenFoldingTest(RulesCompilation compilation, RewritingOperator operator)
	    throws IteratorException, ParseException {
		RuleSet rules = new LinkedListRuleSet();
		rules.add(DlgpParser.parseRule("q(Y,X) :- p(X,Y)."));
		rules.add(DlgpParser.parseRule("p(X,Y) :- q(Y,X)."));

		ConjunctiveQuery query = DlgpParser.parseQuery("?(X,Y,Z) :- p(X,Y), q(Y,Z).");

		compilation.compile(rules.iterator());
		PureRewriter bc = new PureRewriter(operator, true);
		CloseableIterator<? extends ConjunctiveQuery> it = bc.execute(query, rules, compilation);
		int i = count(it);
		Assert.assertEquals(4, i);
	}

	@Theory
	public void queriesCover(RulesCompilation compilation, RewritingOperator operator)
	    throws IteratorException, ParseException {
		RuleSet rules = new LinkedListRuleSet();
		rules.add(DlgpParser.parseRule("p(X) :- r(Y, X)."));

		ConjunctiveQuery query = DlgpParser.parseQuery("?(X) :- p(X), r(a,X).");

		compilation.compile(rules.iterator());
		PureRewriter bc = new PureRewriter(operator, true);
		CloseableIterator<? extends ConjunctiveQuery> it = bc.execute(query, rules, compilation);
		
		int i = Iterators.count(it);
		Assert.assertEquals(1, i);
	}

	/**
	 * c(X) :- b(X,Y,Y).
	 *
	 * getBody([X]) => b(X, Y, Y).
	 * 
	 * @throws ParseException
	 */
	@Theory
	public void getBody1(RulesCompilation compilation,
	    RewritingOperator operator) throws ParseException {
		RuleSet rules = new LinkedListRuleSet();
		rules.add(DlgpParser.parseRule("p(X) :- q(X,Y,Y)."));

		ConjunctiveQuery query = DlgpParser.parseQuery("?(X) :- p(X).");

		compilation.compile(rules.iterator());
		PureRewriter bc = new PureRewriter(operator, true);
		CloseableIteratorWithoutException<? extends ConjunctiveQuery> rewIt = bc.execute(query, rules, compilation);
		
		boolean isFound = false;
		while(rewIt.hasNext()) {
			ConjunctiveQuery rew = rewIt.next();
			CloseableIteratorWithoutException<Atom> it = rew.getAtomSet().iterator();
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
	 * 
	 * @throws ParseException
	 */
	@Theory
	public void getBody2(RulesCompilation compilation,
	    RewritingOperator operator) throws ParseException {
		RuleSet rules = new LinkedListRuleSet();
		rules.add(DlgpParser.parseRule("p(X) :- q(X,X)."));

		ConjunctiveQuery query = DlgpParser.parseQuery("?(X) :- p(X).");

		compilation.compile(rules.iterator());
		PureRewriter bc = new PureRewriter(operator, true);
		CloseableIteratorWithoutException<? extends ConjunctiveQuery> rewIt = bc.execute(query, rules, compilation);

		boolean isFound = false;
		while (rewIt.hasNext()) {
			ConjunctiveQuery rew = rewIt.next();
			CloseableIteratorWithoutException<Atom> it = rew.getAtomSet().iterator();
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
	 * 
	 * @throws IteratorException
	 * @throws ParseException
	 */
	@Theory
	public void getUnification(RulesCompilation compilation,
	    RewritingOperator operator) throws IteratorException, ParseException {
		RuleSet rules = new LinkedListRuleSet();
		rules.add(DlgpParser.parseRule("p(X) :- q(X,Y,X)."));
		rules.add(DlgpParser.parseRule("q(X,Y,X) :- s(X)."));

		ConjunctiveQuery query = DlgpParser.parseQuery("?(X) :- p(X).");

		compilation.compile(rules.iterator());
		PureRewriter bc = new PureRewriter(operator, true);
		CloseableIterator<? extends ConjunctiveQuery> it = bc.execute(query, rules, compilation);

		int i = Iterators.count(it);
		Assert.assertEquals(3, i);
	}

	/**
	 * Given p(X,Y) :- q(X,Y). q(X,Y) :- a(X), p(X,Y). Then rewrite(?(X) :-
	 * q(X,Y), p(Y,Z).) Return ?(X) :- q(X,Y), p(Y,Z). ?(X) :- a(X), p(X,Y),
	 * p(Y,Z). ?(X) :- q(X,Y), q(Y,Z). ?(X) :- a(X), p(X,Y), q(Y,Z).
	 * 
	 * @param compilation
	 * @param operator
	 * @throws IteratorException
	 * @throws ParseException
	 */
	@Theory
	public void issue22(RulesCompilation compilation, RewritingOperator operator)
	    throws IteratorException, ParseException {
		RuleSet rules = new LinkedListRuleSet();
		rules.add(DlgpParser.parseRule("p(X,Y) :- q(X,Y)."));
		rules.add(DlgpParser.parseRule("q(X,Y) :- a(X), p(X,Y)."));

		ConjunctiveQuery query = DlgpParser.parseQuery("?(X) :- q(X,Y), p(Y,Z).");

		compilation.compile(rules.iterator());
		PureRewriter bc = new PureRewriter(operator, true);
		CloseableIterator<? extends ConjunctiveQuery> it = bc.execute(query, rules, compilation);

		int i = count(it);
		Assert.assertEquals(4, i);
	}

	@Theory
	public void issue34_1(RulesCompilation compilation, RewritingOperator operator) throws IteratorException {
		try {
			RuleSet rules = new LinkedListRuleSet();
			rules.add(DlgpParser.parseRule("p(X,Y) :- q(Y,X)."));
			rules.add(DlgpParser.parseRule("r(X,Z) :- p(X,Y)."));
			rules.add(DlgpParser.parseRule("r(X,Z) :- q(X,Y)."));

			ConjunctiveQuery query = DlgpParser.parseQuery("? :- r(a,Y).");

			compilation.compile(rules.iterator());
			PureRewriter bc = new PureRewriter(operator, true);
			CloseableIterator<? extends ConjunctiveQuery> it = bc.execute(query, rules, compilation);

			int i = Iterators.count(it);
			Assert.assertEquals(4, i);
		} catch (Throwable t) {
			Assert.assertFalse("There is an error.", true);
		}
	}

	@Theory
	public void issue34_2(RulesCompilation compilation, RewritingOperator operator) throws IteratorException {
		try {
			RuleSet rules = new LinkedListRuleSet();
			rules.add(DlgpParser.parseRule("q(X,Y,X) :- p(X,Y)."));
			rules.add(DlgpParser.parseRule("r(Z,T) :- q(X,Y,Z)."));

			ConjunctiveQuery query = DlgpParser.parseQuery("? :- r(a,X),p(a,b).");

			compilation.compile(rules.iterator());
			PureRewriter bc = new PureRewriter(operator, true);
			CloseableIterator<? extends ConjunctiveQuery> it = bc.execute(query, rules, compilation);

			int i = Iterators.count(it);
			Assert.assertEquals(1, i);
		} catch (Throwable t) {
			Assert.assertFalse("There is an error.", true);
		}
	}

	@Theory
	public void issue35(RulesCompilation compilation, RewritingOperator operator) throws IteratorException {
		try {
			RuleSet rules = new LinkedListRuleSet();
			Predicate p = new Predicate("p", 2);
			Predicate q = new Predicate("q", 3);
			Predicate r = new Predicate("r", 2);

			rules.add(DlgpParser.parseRule("q(X,Y,X) :- p(X,Y)."));

			ConjunctiveQuery query = DlgpParser.parseQuery("? :- q(X,Y,Y), r(X,Y).");

			compilation.compile(rules.iterator());
			PureRewriter bc = new PureRewriter(operator, true);
			CloseableIterator<? extends ConjunctiveQuery> it = bc.execute(query, rules, compilation);

			int i = 0;
			while (it.hasNext()) {
				ConjunctiveQuery next = it.next();
				InMemoryAtomSet atomSet = next.getAtomSet();
				Set<Predicate> predicates = atomSet.getPredicates();
				Assert.assertTrue(predicates.contains(r));
				if (predicates.contains(p)) {
					Assert.assertEquals(1, atomSet.getTerms().size());
				} else if (predicates.contains(q)) {
					Assert.assertEquals(query, next);
				} else {
					Assert.assertFalse(true);
				}
				++i;
			}
			Assert.assertEquals(2, i);
		} catch (Exception e) {
			Assert.assertFalse("There is an error.", true);
		}
	}
	
	@Theory
	public void constantInRulesIssue62(RulesCompilation compilation,
	    RewritingOperator operator) throws IteratorException, ParseException {
		RuleSet rules = new LinkedListRuleSet();
		rules.add(DlgpParser.parseRule("p(X,a) :- q(X)."));

		ConjunctiveQuery query = DlgpParser.parseQuery("?(X,Y) :- p(X,Y).");

		compilation.compile(rules.iterator());
		PureRewriter bc = new PureRewriter(operator, true);
		CloseableIterator<? extends ConjunctiveQuery> it = bc.execute(query, rules, compilation);

		boolean found = false;
		int i = 0;
		while (it.hasNext()) {
			ConjunctiveQuery next = it.next();
			CloseableIteratorWithoutException<Atom> atomIt = next.getAtomSet().iterator();
			while(atomIt.hasNext()) {
				if(atomIt.next().getPredicate().equals(Predicate.EQUALITY)) {
					found = true;
				}
			}
			++i;
		}
		Assert.assertTrue(found);
		Assert.assertEquals(2, i);
	}

	public static int count(CloseableIterator<?> it) throws IteratorException {
		if (DEBUG) {
			System.out.println("###############################################");
		}
		int i = 0;
		while (it.hasNext()) {
			Object next = it.next();
			if (DEBUG) {
				System.out.println(next);
			}
			++i;
		}
		return i;
	}

}
