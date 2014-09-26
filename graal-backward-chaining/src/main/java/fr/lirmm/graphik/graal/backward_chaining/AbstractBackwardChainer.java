/**
 * 
 */
package fr.lirmm.graphik.graal.backward_chaining;

import fr.lirmm.graphik.graal.core.ConjunctiveQuery;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
public abstract class AbstractBackwardChainer implements BackwardChainer {

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
