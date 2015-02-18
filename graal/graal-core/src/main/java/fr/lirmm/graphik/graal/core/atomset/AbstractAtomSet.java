/**
 * 
 */
package fr.lirmm.graphik.graal.core.atomset;

import java.util.Iterator;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.Term;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public abstract class AbstractAtomSet implements AtomSet {
	
	@Override
	public boolean addAll(Iterator<? extends Atom> atoms) throws AtomSetException {
		boolean isChanged = false;
		while(atoms.hasNext()) {
			isChanged = this.add(atoms.next()) || isChanged;
		}
		return isChanged;
	}
	

	@Override
	public boolean addAll(Iterable<? extends Atom> atoms) throws AtomSetException {
		return this.addAll(atoms.iterator());
	}
	
	@Override
	public boolean removeAll(Iterator<? extends Atom> atoms) throws AtomSetException {
		boolean isChanged = false;
		while(atoms.hasNext()) {
			isChanged = this.remove(atoms.next()) || isChanged;
		}
		return isChanged;
	}
	
	@Override
	public boolean removeAll(Iterable<? extends Atom> atoms) throws AtomSetException {
		return this.removeAll(atoms.iterator());
	}
	
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
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof AtomSet)) {
			return false;
		}
		return this.equals((AtomSet) obj);
	}

	public boolean equals(AtomSet other) { // NOPMD
		try {
			for(Atom a : this) {
				if(!other.contains(a)) {
					return false;
				}
			}
			for(Atom a : other) {
				if(!this.contains(a)) {
					return false;
				}
			}
		} catch (AtomSetException e) {
			return false;
		}
		return true;
	}

	@Override
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
	
	@Override
	public Iterator<Predicate> predicatesIterator() throws AtomSetException {
		return this.getPredicates().iterator();
	}
	
	@Override
	public Iterator<Term> termsIterator() throws AtomSetException {
		return this.getTerms().iterator();
	}
	
	@Override
	public Iterator<Term> termsIterator(Term.Type type) throws AtomSetException {
		return this.getTerms(type).iterator();
	}
	

}
