/**
 * 
 */
package fr.lirmm.graphik.graal.forward_chaining;

import java.util.Set;

import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.atomset.ReadOnlyAtomSet;
import fr.lirmm.graphik.graal.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.homomorphism.HomomorphismFactoryException;

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
	 * @throws HomomorphismFactoryException
	 * @throws HomomorphismException
	 */
	boolean canIAdd(ReadOnlyAtomSet atomSet, Set<Term> fixedTerm, 
	                ReadOnlyAtomSet from, ReadOnlyAtomSet base)
		throws HomomorphismFactoryException, HomomorphismException;
}
