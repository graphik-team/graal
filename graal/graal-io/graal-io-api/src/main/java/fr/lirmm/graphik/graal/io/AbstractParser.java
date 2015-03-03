/**
 * 
 */
package fr.lirmm.graphik.graal.io;

import java.util.Iterator;


/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public abstract class AbstractParser<T> implements Parser<T> {

	public void remove() {
		throw new UnsupportedOperationException();
	}
	
	public Iterator<T> iterator() {
		return this;
	}
	
	public abstract void close();
}
