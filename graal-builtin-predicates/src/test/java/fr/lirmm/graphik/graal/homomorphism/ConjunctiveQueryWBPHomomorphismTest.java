package fr.lirmm.graphik.graal.homomorphism;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.RuleSet;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.factory.TermFactory;
import fr.lirmm.graphik.graal.api.forward_chaining.RuleApplier;
import fr.lirmm.graphik.graal.api.util.ClassicBuiltInPredicates;
import fr.lirmm.graphik.graal.converter.Object2RuleWithBuiltInPredicateConverter;
import fr.lirmm.graphik.graal.core.DefaultBuiltInPredicateSet;
import fr.lirmm.graphik.graal.core.DefaultConjunctiveQueryWithBuiltInPredicates;
import fr.lirmm.graphik.graal.core.HashMapSubstitution;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.ruleset.LinkedListRuleSet;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.forward_chaining.BreadthFirstChase;
import fr.lirmm.graphik.graal.forward_chaining.rule_applier.RuleWithBuiltInPredicateApplier;
import fr.lirmm.graphik.graal.homomorphism.checker.ConjunctiveQueryWithBuiltInPredicatesChecker;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.CloseableIteratorAccumulator;

public class ConjunctiveQueryWBPHomomorphismTest {
	final static DefaultBuiltInPredicateSet btpredicates = new DefaultBuiltInPredicateSet(ClassicBuiltInPredicates.defaultPredicates());
	final static RuleApplier<Rule, AtomSet> applier = new RuleWithBuiltInPredicateApplier<AtomSet>();
	final static TermFactory termFactory = DefaultTermFactory.instance();
	
	private DefaultConjunctiveQueryWithBuiltInPredicates query;
	private AtomSet facts;
	private RuleSet rules;
	private AtomSet result;
	private AtomSet expected;
	private List<Substitution> queryResult;
	private List<Substitution> queryExpected;

	@BeforeClass
	public static void setUp() {
		SmartHomomorphism.instance().addChecker(ConjunctiveQueryWithBuiltInPredicatesChecker.instance());
	}

	@Before
	public void setup() {
		query = null;
		facts = new LinkedListAtomSet();
		rules = new LinkedListRuleSet();
		result = new LinkedListAtomSet();
		expected = new LinkedListAtomSet();
		queryResult = new ArrayList<>();
		queryExpected = new ArrayList<>();
	}

	// ========================================================================
	public void expected(String... atoms) throws Exception {

		for (String atom : atoms)
			expected.add(DlgpParser.parseAtom(atom));
	}

	public void rules(String... queries) throws Exception {
		Object2RuleWithBuiltInPredicateConverter converter = new Object2RuleWithBuiltInPredicateConverter(btpredicates);

		for (String query : queries)
			rules.add((Rule) converter.convert(DlgpParser.parseRule(query)));
	}

	public void facts(String... atoms) throws Exception {

		for (String atom : atoms)
			facts.add(DlgpParser.parseAtom(atom));
	}

	private void checkResult() throws Exception {
		boolean res = expected.equals(result);
		assertTrue("Expected result:\n" + expected + "\nHave:\n" + result, res);
	}

	// ========================================================================

	private void query(String query) throws Exception {
		this.query = new DefaultConjunctiveQueryWithBuiltInPredicates(DlgpParser.parseQuery(query), btpredicates);
	}

	private void queryExpected(Substitution... subs) throws Exception {

		for (Substitution s : subs)
			queryExpected.add(s);
	}

	private void checkQueryResult() throws Exception {
		boolean res = queryExpected.equals(queryResult);
		assertTrue("Expected result:\n" + queryExpected + "\nHave:\n" + queryResult, res);
	}

	private Substitution makeSubstitution(Object... keyVal) {
		Substitution ret = new HashMapSubstitution();
		Iterator<Object> it = Arrays.asList(keyVal).iterator();

		// A key MUST be followed by a value
		while (it.hasNext())
			//For now we assume that the objects are both String, and conserve the Object typing for future evolution.
			ret.put(termFactory.createVariable(it.next()), termFactory.createConstant(it.next()));

		return ret;
	}

	@Test
	public void saturation_0() throws Exception {
		rules( //
				"human(X) :- person(X).", //
				"person(X) :- type(X,person).", //
				"diff(X,Y) :- person(X), person(Y), bt__neq(X,Y).", //
				"eq(X,Y) :- person(X), person(Y), bt__eq(X,Y)." //
		);
		facts( //
				"type(alice,person).\r\n", //
				"type(bob,person)." //
		);
		expected( //
				"type(alice,person).", //
				"type(bob,person).", //
				"person(alice).", //
				"person(bob).", //
				"human(alice).", //
				"human(bob).", //
				"diff(alice,bob).", //
				"diff(bob,alice).", //
				"eq(alice,alice).", //
				"eq(bob,bob)." //
		);

		result = new LinkedListAtomSet(facts);
		BreadthFirstChase bf = new BreadthFirstChase(rules.iterator(), result, applier);
		bf.execute();

		checkResult();
	}

	@SuppressWarnings("resource")
	@Test
	public void goodQuery_0() throws Exception {
		facts( //
				"p(a).", //
				"p(b).", //
				"h(a,b).", //
				"h(a,a).", //
				"h(b,a)." //
		);
		queryExpected( //
				makeSubstitution("X", "a") //
		);
		query("?(X) :- h(X,Y), bt__eq(X,Y).");

		CloseableIterator<Substitution> res = SmartHomomorphism.instance().execute(query, facts);
		queryResult = new CloseableIteratorAccumulator<Substitution>(res).consumeAll().getList();
		checkQueryResult();
	}

	@SuppressWarnings("resource")
	@Test
	public void goodQuery_1() throws Exception {
		facts( //
				"h(a,b).", //
				"x(b,c).", //
				"h(c,d).", //
				"x(d,d)." //
		);
		queryExpected( //
				makeSubstitution("X", "c") //
		);
		query("?(X) :- h(X,Y), x(Y,Z), bt__eq(Y,Z).");

		CloseableIterator<Substitution> res = SmartHomomorphism.instance().execute(query, facts);
		queryResult = new CloseableIteratorAccumulator<Substitution>(res).consumeAll().getList();
		checkQueryResult();
	}
}