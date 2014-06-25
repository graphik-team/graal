/**
 * 
 */
package fr.lirmm.graphik.graal.core.atomset;

import java.util.Iterator;

import fr.lirmm.graphik.graal.core.Atom;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public abstract class AbstractReadOnlyAtomSet implements ReadOnlyAtomSet {
	
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append('[');
		
		Iterator<Atom> it = this.iterator();
		if(it.hasNext()) {
			s.append(it.next().toString());
		}
		while(it.hasNext()) {
			s.append(", ");
			s.append(it.next().toString());
		}
		s.append(']');
		
		return s.toString();
	}

}
