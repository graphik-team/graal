/**
 * 
 */
package fr.lirmm.graphik.graal.solver;

import fr.lirmm.graphik.graal.core.ConjunctiveQueriesUnion;
import fr.lirmm.graphik.graal.core.Query;
import fr.lirmm.graphik.graal.core.atomset.ReadOnlyAtomSet;
import fr.lirmm.graphik.graal.solver.checker.AbstractSolverChecker;
import fr.lirmm.graphik.graal.store.rdbms.RdbmsStore;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
public class SqlConjunctiveQueriesUnionSolverChecker extends
		AbstractSolverChecker {

	@Override
	public boolean check(Query query, ReadOnlyAtomSet atomset) {
		return query instanceof ConjunctiveQueriesUnion
				&& atomset instanceof RdbmsStore;
	}

	@Override
	public SqlConjunctiveQueriesUnionSolver getSolver() {
		return SqlConjunctiveQueriesUnionSolver.getInstance();
	}

	@Override
	public int getDefaultPriority() {
		return 100;
	}

}
