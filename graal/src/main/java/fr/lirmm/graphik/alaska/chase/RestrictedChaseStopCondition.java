/**
 * 
 */
package fr.lirmm.graphik.alaska.chase;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.alaska.Alaska;
import fr.lirmm.graphik.alaska.query.ConjunctiveQueryWithFixedVariables;
import fr.lirmm.graphik.alaska.solver.SolverException;
import fr.lirmm.graphik.alaska.solver.SolverFactoryException;
import fr.lirmm.graphik.kb.core.Atom;
import fr.lirmm.graphik.kb.core.AtomSet;
import fr.lirmm.graphik.kb.core.DefaultConjunctiveQuery;
import fr.lirmm.graphik.kb.core.Query;
import fr.lirmm.graphik.kb.core.ReadOnlyAtomSet;
import fr.lirmm.graphik.kb.core.Substitution;
import fr.lirmm.graphik.kb.core.Term;
import fr.lirmm.graphik.kb.core.Term.Type;

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
		if (Alaska.execute(query, base).hasNext()) {
			return false;
		}
		return true;
	}

}
