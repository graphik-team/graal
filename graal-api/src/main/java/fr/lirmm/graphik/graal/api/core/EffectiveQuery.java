package fr.lirmm.graphik.graal.api.core;

/**
 * This interface represents a query associated with a partial substitution.
 * 
 * @author Olivier Rodriguez
 */
public interface EffectiveQuery<Q extends Query, S extends Substitution> {

	Q getQuery();

	S getSubstitution();

};
