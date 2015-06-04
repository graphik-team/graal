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

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Iterator<T> iterator() {
		return this;
	}

}
