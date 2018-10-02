package fr.lirmm.graphik.graal.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import fr.lirmm.graphik.graal.api.core.BuiltInPredicate;
import fr.lirmm.graphik.graal.api.core.BuiltInPredicateSet;

/**
 * @author Olivier Rodriguez
 */
public class DefaultBuiltInPredicateSet extends ArrayList<BuiltInPredicate> implements BuiltInPredicateSet {

	private static final long serialVersionUID = 1L;

	public DefaultBuiltInPredicateSet() {
		super();
	}

	public DefaultBuiltInPredicateSet(Collection<BuiltInPredicate> asList) {
		super(asList);
	}

	public DefaultBuiltInPredicateSet(BuiltInPredicate... asList) {
		super(Arrays.asList(asList));
	}
}