/**
 * 
 */
package fr.lirmm.graphik.alaska.chase;

import java.util.Set;

import fr.lirmm.graphik.alaska.solver.SolverException;
import fr.lirmm.graphik.alaska.solver.SolverFactoryException;
import fr.lirmm.graphik.kb.core.ReadOnlyAtomSet;
import fr.lirmm.graphik.kb.core.Term;

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
