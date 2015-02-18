/**
 * 
 */
package fr.lirmm.graphik.graal.homomorphism.checker;

import fr.lirmm.graphik.graal.core.Query;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.homomorphism.Homomorphism;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public interface HomomorphismChecker extends Comparable<HomomorphismChecker> {
	
	/**
	 * 
	 * @param query
	 * @param atomset
	 * @return
	 */
	boolean check(Query query, AtomSet atomset);
	
	/**
	 * 
	 * @param query
	 * @param atomset
	 * @return
	 */
	Homomorphism<? extends Query, ? extends AtomSet> getSolver();
	
	/**
	 * 
	 * @return
	 */
	int getPriority();
	
	/**
	 * @param priority
	 */
	void setPriority(int priority);
}
