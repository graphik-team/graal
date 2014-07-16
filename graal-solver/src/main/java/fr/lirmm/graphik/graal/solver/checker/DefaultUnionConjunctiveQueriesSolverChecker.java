/**
 * 
 */
package fr.lirmm.graphik.graal.solver.checker;

import fr.lirmm.graphik.graal.core.UnionConjunctiveQueries;
import fr.lirmm.graphik.graal.core.Query;
import fr.lirmm.graphik.graal.core.atomset.ReadOnlyAtomSet;
import fr.lirmm.graphik.graal.solver.DefaultUnionConjunctiveQueriesSolver;
import fr.lirmm.graphik.graal.solver.Solver;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class DefaultUnionConjunctiveQueriesSolverChecker extends AbstractSolverChecker {

	@Override
	public boolean check(Query query, ReadOnlyAtomSet atomset) {
		return query instanceof UnionConjunctiveQueries;
	}

	@Override
	public Solver<? extends Query, ? extends ReadOnlyAtomSet> getSolver() {
		 return DefaultUnionConjunctiveQueriesSolver.getInstance();
	}

	@Override
	public int getDefaultPriority() {
		return 0;
	}

}
