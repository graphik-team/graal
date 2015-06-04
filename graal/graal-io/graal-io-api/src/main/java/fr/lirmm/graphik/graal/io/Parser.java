/**
 * 
 */
package fr.lirmm.graphik.graal.io;

import java.util.Iterator;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public interface Parser<T> extends Iterator<T>, Iterable<T> {
	
	@Override
	boolean hasNext();

	@Override
	T next();

	void close();
	
}
