/**
 * 
 */
package fr.lirmm.graphik.graal.homomorphism.checker;

import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Query;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.homomorphism.RecursiveBacktrackHomomorphism;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class RecursiveBacktrackChecker extends AbstractChecker {
	
	@Override
	public RecursiveBacktrackHomomorphism getSolver() {
		return RecursiveBacktrackHomomorphism.getInstance();
	}
	
	@Override
	public boolean check(Query query,  AtomSet atomset) {
		return query instanceof ConjunctiveQuery;
	}

	@Override
	public int getDefaultPriority() {
		return 0;
	}

}
