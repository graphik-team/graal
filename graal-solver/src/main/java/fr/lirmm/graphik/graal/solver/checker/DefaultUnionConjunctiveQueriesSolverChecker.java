/**
 * 
 */
package fr.lirmm.graphik.graal.solver.checker;

import fr.lirmm.graphik.graal.core.ConjunctiveQueriesUnion;
import fr.lirmm.graphik.graal.core.Query;
import fr.lirmm.graphik.graal.core.atomset.ReadOnlyAtomSet;
import fr.lirmm.graphik.graal.solver.DefaultConjunctiveQueriesUnionSolver;
import fr.lirmm.graphik.graal.solver.Solver;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class DefaultUnionConjunctiveQueriesSolverChecker extends AbstractSolverChecker {

	@Override
	public boolean check(Query query, ReadOnlyAtomSet atomset) {
		return query instanceof ConjunctiveQueriesUnion;
	}

	@Override
	public Solver<? extends Query, ? extends ReadOnlyAtomSet> getSolver() {
		 return DefaultConjunctiveQueriesUnionSolver.getInstance();
	}

	@Override
	public int getDefaultPriority() {
		return 0;
	}

}
