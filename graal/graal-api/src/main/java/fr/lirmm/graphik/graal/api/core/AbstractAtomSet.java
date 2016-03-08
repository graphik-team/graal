/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2016)
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
package fr.lirmm.graphik.graal.api.core;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import fr.lirmm.graphik.graal.api.core.Term.Type;
import fr.lirmm.graphik.util.stream.GIterator;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public abstract class AbstractAtomSet implements AtomSet {
	
	@Override
	public boolean contains(Atom atom) throws AtomSetException {
		for (Atom a : this) {
			if (AtomComparator.instance().compare(atom, a) == 0)
				return true;
		}
		return false;
	}

	@Override
	public boolean addAll(Iterator<? extends Atom> it) throws AtomSetException {
		boolean isChanged = false;
		while (it.hasNext()) {
			isChanged = this.add(it.next()) || isChanged;
		}
		return isChanged;
	}

	@Override
	public boolean addAll(AtomSet atomset) throws AtomSetException {
		return this.addAll(atomset.iterator());
	}

	@Override
	public boolean removeAll(Iterator<? extends Atom> it) throws AtomSetException {
		boolean isChanged = false;
		while (it.hasNext()) {
			isChanged = this.remove(it.next()) || isChanged;
		}
		return isChanged;
	}
	
	@Override
	public boolean removeAll(AtomSet atomset) throws AtomSetException {
		return this.removeAll(atomset.iterator());
	}

	@Override
	public Set<Term> getTerms() throws AtomSetException {
		Set<Term> terms = new TreeSet<Term>();
		Iterator<Term> it = this.termsIterator();
		while (it.hasNext()) {
			terms.add(it.next());
		}
		return terms;
	}

	@Override
	public Set<Term> getTerms(Type type) throws AtomSetException {
		Set<Term> terms = new TreeSet<Term>();
		Iterator<Term> it = this.termsIterator(type);
		while (it.hasNext()) {
			terms.add(it.next());
		}
		return terms;
	}

	@Override
	public Set<Predicate> getPredicates() throws AtomSetException {
		Set<Predicate> predicates = new TreeSet<Predicate>();
		Iterator<Predicate> it = this.predicatesIterator();
		while (it.hasNext()) {
			predicates.add(it.next());
		}
		return predicates;
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
	
	// /////////////////////////////////////////////////////////////////////////
	//
	// /////////////////////////////////////////////////////////////////////////

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

		GIterator<Atom> it = this.iterator();
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
	public int count(Predicate p) {
		return Integer.MAX_VALUE;
	}

	@Override
	public int getDomainSize() {
		return Integer.MAX_VALUE;
	}

}
