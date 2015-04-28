/**
 * 
 */
package fr.lirmm.graphik.util;

import java.util.Iterator;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public final class Iterators {

	private Iterators(){}
	
	public static int count(Iterator<?> it) {
		int i = 0;
		while(it.hasNext()) {
			++i;
			it.next();
		}
		return i;
	}
}
