/**
 * 
 */
package fr.lirmm.graphik.graal.io;

import java.util.Iterator;


/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public abstract class Parser implements Iterator<Object>, Iterable<Object> {

	public void remove() {
		throw new UnsupportedOperationException();
	}
	
	public Iterator<Object> iterator() {
		return this;
	}
	
	public abstract void close();
}
