/**
 * 
 */
package fr.lirmm.graphik.alaska.transformation;

import fr.lirmm.graphik.alaska.solver.Solver;
import fr.lirmm.graphik.alaska.solver.SolverException;
import fr.lirmm.graphik.kb.core.ConjunctiveQuery;
import fr.lirmm.graphik.kb.core.ReadOnlyAtomSet;
import fr.lirmm.graphik.kb.stream.SubstitutionReader;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class TransformatorSolver implements Solver {

	/**
	 * @param query
	 * @param atomSet
	 */
	public TransformatorSolver(ConjunctiveQuery query, ReadOnlyAtomSet atomSet) {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see fr.lirmm.graphik.alaska.solver.ISolver#execute()
	 */
	@Override
	public SubstitutionReader execute() throws SolverException {
		//TODO transform query and pass it to encapsulated atomSet
		throw new Error("This method isn't implemented!");
	}

}
