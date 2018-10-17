package fr.lirmm.graphik.graal.api.core;

/**
 * @author Olivier Rodriguez
 */
public interface ConjunctiveQueryWithBuiltInPredicates extends Query {

	public ConjunctiveQuery getBaseQuery();

	public ConjunctiveQuery getBuiltInQuery();

	public void setBuiltInPredicates(BuiltInPredicateSet btpredicatesSet);

	public BuiltInPredicateSet getBuiltInPredicates();
}