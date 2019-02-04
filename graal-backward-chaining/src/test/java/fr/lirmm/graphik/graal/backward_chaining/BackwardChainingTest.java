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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.IteratorUtils;
import org.junit.Before;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.EffectiveConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.RuleSet;
import fr.lirmm.graphik.graal.api.core.RulesCompilation;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
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
import fr.lirmm.graphik.graal.homomorphism.PureHomomorphism;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;
import fr.lirmm.graphik.util.stream.IteratorAdapter;
import fr.lirmm.graphik.util.stream.IteratorException;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
@RunWith(Theories.class)
public class BackwardChainingTest {

	@DataPoints
	public static RulesCompilation[] compilations() {
		return new RulesCompilation[] {
				NoCompilation.instance(), new HierarchicalCompilation(), new IDCompilation()
		};
	}

	@DataPoints
	public static RewritingOperator[] operators() {
		return new RewritingOperator[] {
				new AggregAllRulesOperator(), new AggregSingleRuleOperator(), new BasicAggregAllRulesOperator()
		};
	}

	private ConjunctiveQuery query;
	private RuleSet rules;
	private List<EffectiveConjunctiveQuery> result;
	private List<EffectiveConjunctiveQuery> expected;

	@Before
	public void setup() {
		query = null;
		rules = new LinkedListRuleSet();
		result = new ArrayList<>();
		expected = new ArrayList<>();
	}

	public void expected(String... queries) throws ParseException {

		for (String query : queries)
			expected.add(DlgpParser.parseEffectiveQuery(query));
	}

	public void rules(String... queries) throws ParseException {

		for (String query : queries)
			rules.add(DlgpParser.parseRule(query));
	}

	public void query(String query) throws ParseException {
		this.query = DlgpParser.parseQuery(query);
	}

	private static void checkResult(List<EffectiveConjunctiveQuery> result, List<EffectiveConjunctiveQuery> expected) throws AtomSetException, HomomorphismException {
		assertEquals(result.size(), expected.size());

		PureHomomorphism pure = PureHomomorphism.instance();
		boolean res = true;
		EffectiveConjunctiveQuery ecqNotFound = null;

		Iterator<EffectiveConjunctiveQuery> resultIterator = result.iterator();

		// Stores the expected queries that passed the internal loop check
		Set<EffectiveConjunctiveQuery> expectedValidated = new HashSet<>();

		/*
		 * Here we check that all the result's queries are in the expected set.
		 */
		while (resultIterator.hasNext()) {
			EffectiveConjunctiveQuery resultECQ = resultIterator.next();
			ConjunctiveQuery resultCQuery = resultECQ.getQuery();
			List<Term> resultAns = resultCQuery.getAnswerVariables();
			Substitution resultSubstitution = resultECQ.getSubstitution();

			Iterator<EffectiveConjunctiveQuery> expectedIterator = expected.iterator();
			boolean found = false;

			while (expectedIterator.hasNext()) {
				EffectiveConjunctiveQuery expectedECQ = expectedIterator.next();
				ConjunctiveQuery expectedCQuery = expectedECQ.getQuery();
				List<Term> expectedAns = expectedCQuery.getAnswerVariables();
				Substitution expectedSubstitution = expectedECQ.getSubstitution();

				/*
				 * Found the expected query in the result.
				 */
				if (true //
						&& expectedCQuery.getAtomSet().size() >= resultCQuery.getAtomSet().size() //
						&& expectedAns.size() == resultAns.size() //
						&& pure.exist(expectedCQuery.getAtomSet(), resultCQuery.getAtomSet()) //
						&& resultAns.equals(expectedAns) //
						&& expectedSubstitution.equals(resultSubstitution) //
				) {
					found = true;
					expectedValidated.add(expectedECQ);
				}
			}

			if (!found) {
				res = false;
				ecqNotFound = resultECQ;
			}
		}

		if (!res)
			assertTrue(String.format("Waiting for\n%s\nResult is\n%s\nThis result query not found in the expected set:\n%s", expected, result, ecqNotFound), res);

		if (expectedValidated.size() != expected.size()) {
			List<EffectiveConjunctiveQuery> diff = new ArrayList<>(expected);
			diff.removeAll(expectedValidated);
			assertTrue(String.format("All the expected query could'nt be validate\nAll expected are:\n%s\nValidated are:\n%sNot validated are:%s", expected, expectedValidated, diff), false);
		}
	}

	/**
	 * Test 1
	 * 
	 * @throws IteratorException
	 * @throws ParseException
	 */
	@Theory
	public void Test1(RulesCompilation compilation, RewritingOperator operator) throws Exception {
		rules( //
				"q(X1,X2), ppp(X2) :- r(X1).", //
				"pp(X) :- ppp(X).", //
				"p(X) :- pp(X)." //
		);
		query("?(X) :- q(X,Y), p(Y).");

		compilation.compile(rules.iterator());
		PureRewriter bc = new PureRewriter(operator, true);
		CloseableIterator<EffectiveConjunctiveQuery> it = bc.execute(query, rules, compilation);

		result = IteratorUtils.toList(new IteratorAdapter<>(it));
		expected( //
				"?(X) :- q(X,Y), p(Y).", //
				"?(X) :- q(X,Y), pp(Y).", //
				"?(X) :- q(X,Y), ppp(Y).", //
				"?(X) :- r(X)." //
		);
		checkResult(result, IteratorUtils.toList(expected.iterator()));
	}

	/**
	 * Test 2
	 * 
	 * @throws IteratorException
	 * @throws ParseException
	 */
	@Theory
	public void Test2(RulesCompilation compilation, RewritingOperator operator) throws Exception {
		rules( //
				"p(X,Y) :- q(X,Y).", //
				"q(X,Y) :- p(Y,X)." //
		);
		query("?(X,Y) :- p(X,Y).");

		compilation.compile(rules.iterator());
		PureRewriter bc = new PureRewriter(operator, true);
		CloseableIterator<EffectiveConjunctiveQuery> it = bc.execute(query, rules, compilation);

		result = IteratorUtils.toList(new IteratorAdapter<>(it));
		expected( //
				"?(X,Y) :- p(X,Y).", //
				"?(X,Y) :- p(Y,X).", //
				"?(X,Y) :- q(X,Y).", //
				"?(X,Y) :- q(Y,X)." //
		);
		checkResult(result, IteratorUtils.toList(expected.iterator()));
	}

	/**
	 * folding on answer variables
	 * 
	 * @throws IteratorException
	 * @throws ParseException
	 */
	@Theory
	public void forbiddenFoldingTest(RulesCompilation compilation, RewritingOperator operator) throws Exception {
		rules( //
				"q(Y,X) :- p(X,Y).", //
				"p(Y,X) :- q(X,Y)." //
		);
		query("?(X,Y,Z) :- p(X,Y), q(Y,Z).");

		compilation.compile(rules.iterator());
		PureRewriter bc = new PureRewriter(operator, true);
		CloseableIterator<EffectiveConjunctiveQuery> it = bc.execute(query, rules, compilation);

		result = IteratorUtils.toList(new IteratorAdapter<>(it));
		expected( //
				"?(X,Y,Z) :- p(X,Y), q(Y,Z).", //
				"?(X,Y,Z) :- q(Y,X), q(Y,Z).", //
				"?(X,Y,Z) :- p(X,Y), p(Z,Y).", //
				"?(X,Y,Z) :- q(Y,X), p(Z,Y)." //
		);
		checkResult(result, IteratorUtils.toList(expected.iterator()));
	}

	@Theory
	public void queriesCover(RulesCompilation compilation, RewritingOperator operator) throws Exception {
		rules("p(X) :- r(Y,X).");
		query("?(X) :- p(X), r(a,X).");

		compilation.compile(rules.iterator());
		PureRewriter bc = new PureRewriter(operator, true);
		CloseableIterator<EffectiveConjunctiveQuery> it = bc.execute(query, rules, compilation);

		result = IteratorUtils.toList(new IteratorAdapter<>(it));
		expected("?(X) :- r(E0,X), r(a,X).");
		checkResult(result, IteratorUtils.toList(expected.iterator()));
	}

	/**
	 * c(X) :- b(X,Y,Y).
	 *
	 * getBody([X]) => b(X, Y, Y).
	 * 
	 * @throws ParseException
	 */
	@Theory
	public void getBody1(RulesCompilation compilation, RewritingOperator operator) throws Exception {
		rules("p(X) :- q(X,Y,Y).");
		query("?(X) :- p(X).");

		compilation.compile(rules.iterator());
		PureRewriter bc = new PureRewriter(operator, true);
		CloseableIteratorWithoutException<EffectiveConjunctiveQuery> it = bc.execute(query, rules, compilation);

		result = IteratorUtils.toList(new IteratorAdapter<>(it));
		expected( //
				"?(X) :- p(X).", //
				"?(X) :- q(X,E0,E0)." //
		);
		checkResult(result, IteratorUtils.toList(expected.iterator()));
	}

	/**
	 * c(X) :- b(X,X).
	 *
	 * getBody([X]) => b(X, X).
	 * 
	 * @throws ParseException
	 */
	@Theory
	public void getBody2(RulesCompilation compilation, RewritingOperator operator) throws Exception {
		rules("p(X) :- q(X,X).");
		query("?(X) :- p(X).");

		compilation.compile(rules.iterator());
		PureRewriter bc = new PureRewriter(operator, true);
		CloseableIteratorWithoutException<EffectiveConjunctiveQuery> it = bc.execute(query, rules, compilation);

		result = IteratorUtils.toList(new IteratorAdapter<>(it));
		expected( //
				"?(X) :- p(X).", //
				"?(X) :- q(X,X)." //
		);
		checkResult(result, IteratorUtils.toList(expected.iterator()));
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
	public void getUnification(RulesCompilation compilation, RewritingOperator operator) throws Exception {
		rules( //
				"p(X) :- q(X,Y,X).", //
				"q(X,Y,X) :- s(X)." //
		);
		query("?(X) :- p(X).");

		compilation.compile(rules.iterator());
		PureRewriter bc = new PureRewriter(operator, true);
		CloseableIterator<EffectiveConjunctiveQuery> it = bc.execute(query, rules, compilation);

		result = IteratorUtils.toList(new IteratorAdapter<>(it));
		expected( //
				"?(X) :- p(X).", //
				"?(X) :- q(X,E0,X).", //
				"?(X) :- s(X)." //
		);
		checkResult(result, IteratorUtils.toList(expected.iterator()));
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
	public void issue22(RulesCompilation compilation, RewritingOperator operator) throws Exception {
		rules( //
				"p(X,Y) :- q(X,Y).", //
				"q(X,Y) :- a(X), p(X,Y)." //
		);
		query("?(X) :- q(X,Y), p(Y,Z).");

		compilation.compile(rules.iterator());
		PureRewriter bc = new PureRewriter(operator, true);
		CloseableIterator<EffectiveConjunctiveQuery> it = bc.execute(query, rules, compilation);

		result = IteratorUtils.toList(new IteratorAdapter<>(it));
		expected( //
				"?(X) :- q(X,Y), p(Y,Z).", //
				"?(X) :- q(X,Y), q(Y,Z).", //
				"?(X) :- a(X), p(X,Y), p(Y,Z).", //
				"?(X) :- a(X), p(X,Y), q(Y,Z)." //
		);
		checkResult(result, IteratorUtils.toList(expected.iterator()));
	}

	@Theory
	public void issue34_1(RulesCompilation compilation, RewritingOperator operator) throws Exception {
		rules( //
				"p(X, Y) :- q(Y,X).", //
				"r(X,E0) :- p(X,Y).", //
				"r(X,E0) :- q(X,Y)." //
		);
		query("? :- r(a,X).");

		compilation.compile(rules.iterator());
		PureRewriter bc = new PureRewriter(operator, true);
		CloseableIterator<EffectiveConjunctiveQuery> it = bc.execute(query, rules, compilation);

		result = IteratorUtils.toList(new IteratorAdapter<>(it));
		expected( //
				"? :- r(a,X).", //
				"? :- q(a,X).", //
				"? :- p(a,X).", //
				"? :- q(X,a)." //
		);
		checkResult(result, IteratorUtils.toList(expected.iterator()));
	}

	@Theory
	public void issue34_2(RulesCompilation compilation, RewritingOperator operator) throws Exception {
		rules( //
				"q(X,Y,X) :- p(X,Y).", //
				"r(Z,E0)  :- q(X,Y,Z)." //
		);
		query("? :- r(a,X), p(a,b).");

		compilation.compile(rules.iterator());
		PureRewriter bc = new PureRewriter(operator, true);
		CloseableIterator<EffectiveConjunctiveQuery> it = bc.execute(query, rules, compilation);

		result = IteratorUtils.toList(new IteratorAdapter<>(it));
		expected("? :- p(a,E0), p(a,b).");
		checkResult(result, IteratorUtils.toList(expected.iterator()));
	}

	@Theory
	public void issue35(RulesCompilation compilation, RewritingOperator operator) throws Exception {
		rules("q(X,Y,X) :- p(X,Y).");
		query("? :- q(X,Y,Y), r(X,Y).");

		compilation.compile(rules.iterator());
		PureRewriter bc = new PureRewriter(operator, true);
		CloseableIterator<EffectiveConjunctiveQuery> it = bc.execute(query, rules, compilation);

		List<EffectiveConjunctiveQuery> result = IteratorUtils.toList(new IteratorAdapter<>(it));
		expected( //
				"? :- q(X,Y,Y), r(X,Y).", //
				"? :- p(X,X), r(X,X)." //
		);
		checkResult(result, IteratorUtils.toList(expected.iterator()));
	}

	@Theory
	public void constantInRulesIssue62(RulesCompilation compilation, RewritingOperator operator) throws Exception {
		rules("p(X,a) :- q(X).");
		query("?(X,Y) :- p(X,Y).");

		compilation.compile(rules.iterator());
		PureRewriter bc = new PureRewriter(operator, true);
		CloseableIterator<EffectiveConjunctiveQuery> it = bc.execute(query, rules, compilation);

		result = IteratorUtils.toList(new IteratorAdapter<>(it));
		expected( //
				"?(X,Y) :- p(X,Y).", //
				"?(X,Y) :- q(X), Y=a." //
		);
		checkResult(result, IteratorUtils.toList(expected.iterator()));
	}

	@Theory
	public void issue97variableNameConflict(RulesCompilation compilation, RewritingOperator operator) throws Exception {
		rules( //
				"p(X,Y) :- s(X,Z,Y).", //
				"q(Y) :- t(Y,X)." //
		);
		query("?(X,Y) :- p(X,Y), q(Y).");

		compilation.compile(rules.iterator());
		PureRewriter bc = new PureRewriter(operator, true);
		CloseableIterator<EffectiveConjunctiveQuery> it = bc.execute(query, rules, compilation);

		result = IteratorUtils.toList(new IteratorAdapter<>(it));
		expected( //
				"?(X,Y) :- p(X,Y), q(Y).", //
				"?(X,Y) :- p(X,Y), t(Y,E0).", //
				"?(X,Y) :- s(X,E0,Y), q(Y).", //
				"?(X,Y) :- s(X,E0,Y), t(Y,E1)." //
		);
		checkResult(result, IteratorUtils.toList(expected.iterator()));
	}
}
