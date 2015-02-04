/**
 * 
 */
package fr.lirmm.graphik.graal.forward_chaining;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.core.Query;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.homomorphism.HomomorphismFactoryException;
import fr.lirmm.graphik.graal.homomorphism.StaticHomomorphism;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class RestrictedChaseStopCondition implements ChaseHaltingCondition {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(RestrictedChaseStopCondition.class);
	
	@Override
	public boolean canIAdd(AtomSet atomSet, Set<Term> fixedTerms, AtomSet from, AtomSet base) throws HomomorphismFactoryException, HomomorphismException {
		
		Query query = new ConjunctiveQueryWithFixedVariables(atomSet, fixedTerms);
		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("Fixed Query:" + query);
		}
		if (StaticHomomorphism.executeQuery(query, base).hasNext()) {
			return false;
		}
		return true;
	}

}
