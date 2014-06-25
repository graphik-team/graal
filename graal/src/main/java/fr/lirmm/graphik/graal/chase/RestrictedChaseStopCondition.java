/**
 * 
 */
package fr.lirmm.graphik.graal.chase;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.Graal;
import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.DefaultConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Query;
import fr.lirmm.graphik.graal.core.Substitution;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.Term.Type;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.ReadOnlyAtomSet;
import fr.lirmm.graphik.graal.query.ConjunctiveQueryWithFixedVariables;
import fr.lirmm.graphik.graal.solver.SolverException;
import fr.lirmm.graphik.graal.solver.SolverFactoryException;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class RestrictedChaseStopCondition implements ChaseStopCondition {

	private static final Logger logger = LoggerFactory
			.getLogger(RestrictedChaseStopCondition.class);
	
	/* (non-Javadoc)
	 * @see fr.lirmm.graphik.alaska.chase.ChaseStopCondition#canIAdd(fr.lirmm.graphik.kb.core.AtomSet)
	 */
	@Override
	public boolean canIAdd(ReadOnlyAtomSet atomSet, Set<Term> fixedTerms, ReadOnlyAtomSet base) throws SolverFactoryException, SolverException {
		
		Query query = new ConjunctiveQueryWithFixedVariables(atomSet, fixedTerms);
		if(logger.isDebugEnabled()) {
			logger.debug("Fixed Query:" + query);
		}
		if (Graal.executeQuery(query, base).hasNext()) {
			return false;
		}
		return true;
	}

}
