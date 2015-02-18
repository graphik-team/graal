/**
 * 
 */
package fr.lirmm.graphik.graal.homomorphism.checker;

import fr.lirmm.graphik.graal.core.Query;
import fr.lirmm.graphik.graal.core.UnionConjunctiveQueries;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.homomorphism.DefaultUnionConjunctiveQueriesHomomorphism;
import fr.lirmm.graphik.graal.homomorphism.Homomorphism;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class DefaultUnionConjunctiveQueriesChecker extends AbstractChecker {

	@Override
	public boolean check(Query query, AtomSet atomset) {
		return query instanceof UnionConjunctiveQueries;
	}

	@Override
	public Homomorphism<? extends Query, ? extends AtomSet> getSolver() {
		 return DefaultUnionConjunctiveQueriesHomomorphism.getInstance();
	}

	@Override
	public int getDefaultPriority() {
		return 0;
	}

}
