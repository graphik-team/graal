/**
 * 
 */
package fr.lirmm.graphik.graal.transformation;

import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Query;
import fr.lirmm.graphik.graal.core.atomset.ReadOnlyAtomSet;
import fr.lirmm.graphik.graal.homomorphism.checker.AbstractChecker;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class TransformatorSolverChecker extends AbstractChecker {

	@Override
	public boolean check(Query query, ReadOnlyAtomSet atomset) {
		return query instanceof ConjunctiveQuery && atomset instanceof ReadOnlyTransformStore;
	}

	@Override
	public TransformatorSolver getSolver() {
		return TransformatorSolver.getInstance();
	}

	@Override
	public int getDefaultPriority() {
		return 101;
	}

}
