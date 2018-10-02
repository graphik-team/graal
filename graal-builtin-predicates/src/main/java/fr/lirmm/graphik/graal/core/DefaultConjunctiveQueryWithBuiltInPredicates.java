package fr.lirmm.graphik.graal.core;

import java.util.ArrayList;

import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQueryWithBuiltInPredicates;
import fr.lirmm.graphik.graal.api.core.BuiltInPredicateSet;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.core.factory.DefaultConjunctiveQueryFactory;
import fr.lirmm.graphik.util.stream.IteratorException;

/**
 * Represents a conjunctive query which contains atoms with predefined built-in
 * predicates.
 * 
 * @author Olivier Rodriguez
 */
public class DefaultConjunctiveQueryWithBuiltInPredicates implements ConjunctiveQueryWithBuiltInPredicates {
	/**
	 * The query without builtInPredicate
	 */
	ConjunctiveQuery baseQuery;
	/**
	 * This query must be send to the appropriate filter at the end to takes care of
	 * the built-in's behaviour.
	 */
	ConjunctiveQuery btquery;
	BuiltInPredicateSet btpredicates;

	public DefaultConjunctiveQueryWithBuiltInPredicates() {
		super();
	}

	public DefaultConjunctiveQueryWithBuiltInPredicates(ConjunctiveQuery q, BuiltInPredicateSet btpredicates)
			throws IteratorException, AtomSetException {
		baseQuery = new DefaultConjunctiveQuery(q);
		setBuiltInPredicates(btpredicates);

		BuiltInAtomSetBuilder btbuilder = new BuiltInAtomSetBuilder(q.getAtomSet(), btpredicates);
		btbuilder.build();

		/*
		 * Set the instance to an executable query. This contains non built-in atoms.
		 */
		{
			AtomSet a = baseQuery.getAtomSet();
			a.clear();
			a.addAll(btbuilder.getSimpleAtoms());
			baseQuery.setAnswerVariables(new ArrayList<Term>(a.getVariables()));
		}
		/*
		 * Set the btquery instance
		 */
		btquery = DefaultConjunctiveQueryFactory.instance().create();
		btquery.getAtomSet().addAll(btbuilder.getBuiltInAtoms());
		btquery.setAnswerVariables(q.getAnswerVariables());
	}

	public ConjunctiveQuery getBaseQuery() {
		return baseQuery;
	}

	public ConjunctiveQuery getBuiltInQuery() {
		return btquery;
	}

	public void setBuiltInPredicates(BuiltInPredicateSet btpredicatesSet) {
		btpredicates = btpredicatesSet;
	}

	public BuiltInPredicateSet getBuiltInPredicates() {
		return btpredicates;
	}

	@Override
	public String toString() {
		return baseQuery.toString() + " + " + btquery.toString();
	}

	@Override
	public void appendTo(StringBuilder sb) {
		baseQuery.appendTo(sb);
	}

	@Override
	public boolean isBoolean() {
		return baseQuery.isBoolean();
	}

	@Override
	public String getLabel() {
		return baseQuery.getLabel();
	}

	@Override
	public void setLabel(String label) {
		baseQuery.setLabel(label);
	}
}