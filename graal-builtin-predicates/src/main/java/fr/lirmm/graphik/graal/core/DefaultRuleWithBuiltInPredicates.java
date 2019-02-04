package fr.lirmm.graphik.graal.core;

import java.util.Set;

import fr.lirmm.graphik.graal.api.core.BuiltInPredicateSet;
import fr.lirmm.graphik.graal.api.core.Constant;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Literal;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.RuleWithBuiltInPredicate;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Term.Type;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;

/**
 * @author Olivier Rodriguez
 */
public class DefaultRuleWithBuiltInPredicates extends AbstractRule implements RuleWithBuiltInPredicate {

	/**
	 * Here the rule contains both simple predicates and built-in predicates.
	 */
	private Rule baseRule;

	private BuiltInPredicateSet btpredicates;

	public DefaultRuleWithBuiltInPredicates() {
		baseRule = new DefaultRule("", new LinkedListAtomSet(), new LinkedListAtomSet());
		setBuiltInPredicates(new DefaultBuiltInPredicateSet());
	}

	// copy constructor
	public DefaultRuleWithBuiltInPredicates(Rule rule) {
		baseRule = new DefaultRule(rule);
		setBuiltInPredicates(new DefaultBuiltInPredicateSet());
	}

	public DefaultRuleWithBuiltInPredicates(Rule rule, BuiltInPredicateSet btpredicates) {
		baseRule = new DefaultRule(rule);
		setBuiltInPredicates(btpredicates);
	}

	public void setBuiltInPredicates(BuiltInPredicateSet btpredicates) {
		this.btpredicates = btpredicates;
	}

	public BuiltInPredicateSet getBuiltInPredicates() {
		return btpredicates;
	}

	@Override
	public String getLabel() {
		return baseRule.getLabel();
	}

	@Override
	public void setLabel(String label) {
		baseRule.setLabel(label);
	}

	@Override
	public InMemoryAtomSet getBody() {
		return baseRule.getBody();
	}

	@Override
	public InMemoryAtomSet getHead() {
		return baseRule.getHead();
	}

	@Override
	public Set<Variable> getFrontier() {
		return baseRule.getFrontier();
	}

	@Override
	public Set<Variable> getExistentials() {
		return baseRule.getExistentials();
	}

	@Override
	public Set<Term> getTerms(Type type) {
		return baseRule.getTerms(type);
	}

	@Override
	public Set<Variable> getVariables() {
		return baseRule.getVariables();
	}

	@Override
	public Set<Constant> getConstants() {
		return baseRule.getConstants();
	}

	@Override
	public Set<Literal> getLiterals() {
		return baseRule.getLiterals();
	}

	@Override
	public Set<Term> getTerms() {
		return baseRule.getTerms();
	}
}