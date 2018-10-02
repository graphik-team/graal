package fr.lirmm.graphik.graal.homomorphism.checker;

import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQueryWithBuiltInPredicates;
import fr.lirmm.graphik.graal.api.core.Query;
import fr.lirmm.graphik.graal.api.homomorphism.AbstractChecker;
import fr.lirmm.graphik.graal.api.homomorphism.Homomorphism;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismChecker;
import fr.lirmm.graphik.graal.homomorphism.ConjunctiveQueryWithBuiltInPredicatesHomomorphism;

/**
 * @author Olivier Rodriguez
 */
public class ConjunctiveQueryWithBuiltInPredicatesChecker extends AbstractChecker implements HomomorphismChecker {
	private static final ConjunctiveQueryWithBuiltInPredicatesChecker INSTANCE = new ConjunctiveQueryWithBuiltInPredicatesChecker();

	public static ConjunctiveQueryWithBuiltInPredicatesChecker instance() {
		return INSTANCE;
	}

	private ConjunctiveQueryWithBuiltInPredicatesChecker() {
	}

	@Override
	public boolean check(Object query, AtomSet atomset) {
		return query instanceof ConjunctiveQueryWithBuiltInPredicates;
	}

	@Override
	public Homomorphism<? extends Query, ? extends AtomSet> getSolver() {
		return ConjunctiveQueryWithBuiltInPredicatesHomomorphism.instance();
	}

	@Override
	public int getDefaultPriority() {
		return 0;
	}
}