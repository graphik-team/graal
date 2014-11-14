/**
 * 
 */
package fr.lirmm.graphik.graal.store;

import fr.lirmm.graphik.graal.core.atomset.AtomSet;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public interface Store extends AtomSet {
	
	public void close();

}
