/**
 * 
 */
package fr.lirmm.graphik.graal.transformation;

import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.stream.SubstitutionReader;
import fr.lirmm.graphik.graal.homomorphism.Homomorphism;
import fr.lirmm.graphik.graal.homomorphism.HomomorphismException;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class TransformatorSolver implements Homomorphism<ConjunctiveQuery, TransformAtomSet> {

	private static TransformatorSolver instance;

	private TransformatorSolver() {
	}

	public static synchronized TransformatorSolver getInstance() {
		if (instance == null)
			instance = new TransformatorSolver();

		return instance;
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public SubstitutionReader execute(ConjunctiveQuery query, TransformAtomSet atomSet) throws HomomorphismException {
		//TODO transform query and pass it to encapsulated atomSet
		throw new Error("This method isn't implemented!");
	}

}
