/**
 * 
 */
package fr.lirmm.graphik.graal.transformation;

import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.stream.SubstitutionReader;
import fr.lirmm.graphik.graal.solver.Solver;
import fr.lirmm.graphik.graal.solver.SolverException;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class TransformatorSolver implements Solver<ConjunctiveQuery, TransformAtomSet> {

	private static TransformatorSolver instance;

	private TransformatorSolver() {
	}

	public static TransformatorSolver getInstance() {
		if (instance == null)
			instance = new TransformatorSolver();

		return instance;
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public SubstitutionReader execute(ConjunctiveQuery query, TransformAtomSet atomSet) throws SolverException {
		//TODO transform query and pass it to encapsulated atomSet
		throw new Error("This method isn't implemented!");
	}

}
