package fr.lirmm.graphik.graal.homomorphism;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.RuleSet;
import fr.lirmm.graphik.graal.api.forward_chaining.RuleApplier;
import fr.lirmm.graphik.graal.api.util.ClassicBuiltInPredicates;
import fr.lirmm.graphik.graal.converter.Object2RuleWithBuiltInPredicateConverter;
import fr.lirmm.graphik.graal.core.DefaultBuiltInPredicateSet;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.ruleset.LinkedListRuleSet;
import fr.lirmm.graphik.graal.forward_chaining.BreadthFirstChase;
import fr.lirmm.graphik.graal.forward_chaining.rule_applier.RuleWithBuiltInPredicateApplier;
import fr.lirmm.graphik.graal.homomorphism.checker.ConjunctiveQueryWithBuiltInPredicatesChecker;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;

public class ConjunctiveQueryWBPHomomorphismTest {
	final static DefaultBuiltInPredicateSet btpredicates = new DefaultBuiltInPredicateSet(ClassicBuiltInPredicates.defaultPredicates());
	final static RuleApplier<Rule, AtomSet> applier = new RuleWithBuiltInPredicateApplier<AtomSet>();

	private AtomSet facts;
	private RuleSet rules;
	private AtomSet result;
	private AtomSet expected;

	@BeforeClass
	public static void setUp() {
		SmartHomomorphism.instance().addChecker(ConjunctiveQueryWithBuiltInPredicatesChecker.instance());
	}

	@Before
	public void setup() {
		facts = new LinkedListAtomSet();
		rules = new LinkedListRuleSet();
		result = new LinkedListAtomSet();
		expected = new LinkedListAtomSet();
	}

	public void expected(String... atoms) throws Exception {

		for (String atom : atoms)
			expected.add(DlgpParser.parseAtom(atom));
	}

	public void rules(String... queries) throws Exception {
		Object2RuleWithBuiltInPredicateConverter converter = new Object2RuleWithBuiltInPredicateConverter(btpredicates);

		for (String query : queries)
			rules.add((Rule)converter.convert(DlgpParser.parseRule(query)));
	}

	public void facts(String... atoms) throws Exception {

		for (String atom : atoms)
			facts.add(DlgpParser.parseAtom(atom));
	}

	private void checkResult() throws Exception {
		boolean res = expected.equals(result);
		assertTrue("Expected result:\n" + expected + "\nHave:\n" + result, res);
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
}