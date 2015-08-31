/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2015)
 *
 * Contributors :
 *
 * Clément SIPIETER <clement.sipieter@inria.fr>
 * Mélanie KÖNIG
 * Swan ROCHER
 * Jean-François BAGET
 * Michel LECLÈRE
 * Marie-Laure MUGNIER <mugnier@lirmm.fr>
 *
 *
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
 /**
 * 
 */
package fr.lirmm.graphik.graal.core.atomset;

import java.util.Iterator;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.term.Term;

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
