/**
 * 
 */
package fr.lirmm.graphik.graal.forward_chaining.halting_condition;

import java.util.Set;

import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.term.Term;
import fr.lirmm.graphik.graal.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.homomorphism.HomomorphismFactoryException;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public interface ChaseHaltingCondition {


	/**
	 * @param atomSet
	 * @param fixedTerm
	 * @param base
	 * @return
	 * @throws HomomorphismFactoryException
	 * @throws HomomorphismException
	 */
	boolean canIAdd(AtomSet atomSet, Set<Term> fixedTerm, 
	                AtomSet from, AtomSet base)
		throws HomomorphismFactoryException, HomomorphismException;
}
