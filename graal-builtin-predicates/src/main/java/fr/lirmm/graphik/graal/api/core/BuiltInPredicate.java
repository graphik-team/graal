package fr.lirmm.graphik.graal.api.core;

import java.util.List;

import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;

/**
 * Represents a built-in predicate which is a predicate who can validate a data
 * following is own semantic.
 *
 * @author Olivier Rodriguez
 */
abstract public class BuiltInPredicate extends Predicate {

	private static final long serialVersionUID = 1L;

	public BuiltInPredicate(Object identifier, int arity) {
		super(identifier, arity);
	}

	abstract public boolean validate(List<Term> terms) throws HomomorphismException;
}