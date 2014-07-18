/**
 * 
 */
package fr.lirmm.graphik.graal.solver.checker;

import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Query;
import fr.lirmm.graphik.graal.core.atomset.ReadOnlyAtomSet;
import fr.lirmm.graphik.graal.solver.RecursiveBacktrackSolver;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class RecursiveBacktrackSolverChecker extends AbstractSolverChecker {
	
	@Override
	public RecursiveBacktrackSolver getSolver() {
		return RecursiveBacktrackSolver.getInstance();
	}
	
	@Override
	public boolean check(Query query,  ReadOnlyAtomSet atomset) {
		return query instanceof ConjunctiveQuery;
	}

	@Override
	public int getDefaultPriority() {
		return 0;
	}

}
