/**
 * 
 */
package fr.lirmm.graphik.graal.core.stream.filter;

import java.util.Iterator;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.util.stream.filter.FilterIterator;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class AtomFilterIterator extends FilterIterator<Object, Atom> {
	
	/**
	 * @param it
	 * @param filter
	 */
	public AtomFilterIterator(Iterator<Object> it) {
		super(it, AtomFilter.getInstance());
	}

}
