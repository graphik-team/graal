/**
 * 
 */
package fr.lirmm.graphik.graal.solver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.core.Query;
import fr.lirmm.graphik.graal.core.atomset.ReadOnlyAtomSet;
import fr.lirmm.graphik.graal.core.stream.SubstitutionReader;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
public class StaticSolver {

	private static final Logger logger = LoggerFactory
			.getLogger(StaticSolver.class);
	
	public static SolverFactory getSolverFactory() {
		return DefaultSolverFactory.getInstance();
	}

	/**
	 * For boolean query, return a SubstitutionReader with an empty Substitution
	 * for true and no substitution for false.
	 * 
	 * @param query
	 * @param atomSet
	 * @return A substitution stream that represents homomorphisms.
	 * @throws SolverFactoryException
	 * @throws SolverException
	 */
	public static SubstitutionReader executeQuery(Query query,
			ReadOnlyAtomSet atomSet) throws SolverFactoryException,
			SolverException {
		if (logger.isDebugEnabled())
			logger.debug("Query : " + query);

		Solver solver = DefaultSolverFactory.getInstance().getSolver(query,
				atomSet);
		return solver.execute(query, atomSet);

	}
}
