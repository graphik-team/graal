/**
 * 
 */
package fr.lirmm.graphik.graal.core.atomset;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.Predicate;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public abstract class AbstractReadOnlyAtomSet implements ReadOnlyAtomSet {

	@Override
	public boolean isSubSetOf(AtomSet atomset) {
		for (Atom a : this) {
			try {
				if (!atomset.contains(a)) {
					return false;
				}
			} catch (AtomSetException e) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean isEmpty() {
		return !this.iterator().hasNext();
	}

	@Override
	public Iterable<Predicate> getAllPredicates() throws AtomSetException {
		Set<Predicate> predicates = new TreeSet<Predicate>();
		for (Atom a : this) {
			predicates.add(a.getPredicate());
		}
		return predicates;
	}

	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append('[');

		Iterator<Atom> it = this.iterator();
		if (it.hasNext()) {
			s.append(it.next().toString());
		}
		while (it.hasNext()) {
			s.append(", ");
			s.append(it.next().toString());
		}
		s.append(']');

		return s.toString();
	}

}
