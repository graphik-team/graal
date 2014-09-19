/**
 * 
 */
package fr.lirmm.graphik.graal.backward_chaining;

import java.util.Iterator;

import fr.lirmm.graphik.graal.core.ConjunctiveQuery;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public interface BackwardChainer extends Iterator<ConjunctiveQuery> {
	
	@Override
	boolean hasNext();
	
	@Override
	ConjunctiveQuery next();

}
