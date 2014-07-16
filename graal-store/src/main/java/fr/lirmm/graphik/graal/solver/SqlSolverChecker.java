/**
 * 
 */
package fr.lirmm.graphik.graal.solver;

import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Query;
import fr.lirmm.graphik.graal.core.atomset.ReadOnlyAtomSet;
import fr.lirmm.graphik.graal.solver.checker.AbstractSolverChecker;
import fr.lirmm.graphik.graal.store.rdbms.RdbmsStore;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class SqlSolverChecker extends AbstractSolverChecker {

	@Override
	public boolean check(Query query, ReadOnlyAtomSet atomset) {
		 return query instanceof ConjunctiveQuery && atomset instanceof RdbmsStore;
	}

	@Override
	public SqlSolver getSolver() {
         return SqlSolver.getInstance();
	}

	@Override
	public int getDefaultPriority() {
		return 100;
	}

}
