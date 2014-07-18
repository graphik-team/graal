/**
 * 
 */
package fr.lirmm.graphik.graal.solver.checker;

import fr.lirmm.graphik.graal.core.Query;
import fr.lirmm.graphik.graal.core.atomset.ReadOnlyAtomSet;
import fr.lirmm.graphik.graal.solver.Solver;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public interface SolverChecker extends Comparable<SolverChecker> {
	
	/**
	 * 
	 * @param query
	 * @param atomset
	 * @return
	 */
	boolean check(Query query, ReadOnlyAtomSet atomset);
	
	/**
	 * 
	 * @param query
	 * @param atomset
	 * @return
	 */
	Solver getSolver(Query query, ReadOnlyAtomSet atomset);
	
	/**
	 * 
	 * @return
	 */
	int getPriority();
	
	/**
	 * @param priority
	 */
	void setPriority(int priority);
}
