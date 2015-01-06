/**
 * 
 */
package fr.lirmm.graphik.graal.homomorphism;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.core.Query;
import fr.lirmm.graphik.graal.core.atomset.ReadOnlyAtomSet;
import fr.lirmm.graphik.graal.core.stream.SubstitutionReader;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
public class StaticHomomorphism {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(StaticHomomorphism.class);
	
	public static HomomorphismFactory getSolverFactory() {
		return DefaultHomomorphismFactory.getInstance();
	}

	/**
	 * For boolean query, return a SubstitutionReader with an empty Substitution
	 * for true and no substitution for false.
	 * 
	 * @param query
	 * @param atomSet
	 * @return A substitution stream that represents homomorphisms.
	 * @throws HomomorphismFactoryException
	 * @throws HomomorphismException
	 */
	public static SubstitutionReader executeQuery(Query query,
			ReadOnlyAtomSet atomSet) throws HomomorphismFactoryException,
			HomomorphismException {
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Query : " + query);

		Homomorphism solver = DefaultHomomorphismFactory.getInstance().getSolver(query,
				atomSet);
		return solver.execute(query, atomSet);

	}
}
