/**
 * 
 */
package fr.lirmm.graphik.graal.chase;

import java.util.Set;

import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.atomset.ReadOnlyAtomSet;
import fr.lirmm.graphik.graal.solver.SolverException;
import fr.lirmm.graphik.graal.solver.SolverFactoryException;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public interface ChaseStopCondition {


	/**
	 * @param atomSet
	 * @param fixedTerm
	 * @param base
	 * @return
	 * @throws SolverFactoryException
	 * @throws SolverException
	 */
	boolean canIAdd(ReadOnlyAtomSet atomSet, Set<Term> fixedTerm,
			ReadOnlyAtomSet base) throws SolverFactoryException,
			SolverException;
}
