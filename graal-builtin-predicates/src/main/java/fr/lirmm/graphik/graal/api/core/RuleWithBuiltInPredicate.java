package fr.lirmm.graphik.graal.api.core;

/**
 * 
 * @author Olivier Rodriguez
 *
 */
public interface RuleWithBuiltInPredicate extends Rule {

	public void setBuiltInPredicates(BuiltInPredicateSet btpredicates);

	public BuiltInPredicateSet getBuiltInPredicates();
}