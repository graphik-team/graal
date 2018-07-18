/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2017)
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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.Constant;
import fr.lirmm.graphik.graal.api.core.Literal;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Term.Type;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.graal.core.Substitutions;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.CloseableIteratorAdapter;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
@SuppressWarnings("deprecation")
public abstract class AbstractAtomSet implements AtomSet {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractAtomSet.class);

	@Override
	public boolean contains(Atom atom) throws AtomSetException {
		CloseableIterator<Atom> it = this.iterator();
		try {
			while (it.hasNext()) {
				Atom a = it.next();
				if (Objects.equals(atom, a))
					return true;
			}
		} catch (Exception e) {
			throw new AtomSetException(e);
		}
		return false;
	}

	@Override
	public boolean addAll(CloseableIterator<? extends Atom> it) throws AtomSetException {
		boolean isChanged = false;
		try {
			while (it.hasNext()) {
				isChanged = this.add(it.next()) || isChanged;
			}
		} catch (Exception e) {
			throw new AtomSetException(e);
		}
		return isChanged;
	}

	@Override
	public boolean addAll(AtomSet atomset) throws AtomSetException {
		return this.addAll(atomset.iterator());
	}

	@Override
	public boolean removeAll(CloseableIterator<? extends Atom> it) throws AtomSetException {
		boolean isChanged = false;
		try {
			while (it.hasNext()) {
				isChanged = this.remove(it.next()) || isChanged;
			}
		} catch (Exception e) {
			throw new AtomSetException(e);
		}
		return isChanged;
	}
	
	@Override
	public boolean removeAll(AtomSet atomset) throws AtomSetException {
		return this.removeAll(atomset.iterator());
	}
	

	@Override
	public CloseableIterator<Atom> match(Atom atom) throws AtomSetException {
		return this.match(atom, Substitutions.emptySubstitution());
	}

	@Override
	public Set<Term> getTerms() throws AtomSetException {
		Set<Term> terms = new HashSet<Term>();
		CloseableIterator<Atom> atomIt = this.iterator();
		try {
			while(atomIt.hasNext()) {
				Iterator<Term> termIt = atomIt.next().iterator();
				while(termIt.hasNext()) {
					terms.add(termIt.next());
				}
			}
		} catch (Exception e) {
			throw new AtomSetException(e);
		}
		return terms;
	}
	
	@Override
	public Set<Variable> getVariables() throws AtomSetException {
		Set<Variable> terms = new HashSet<Variable>();
		CloseableIterator<Atom> atomIt = this.iterator();
		try {
			while(atomIt.hasNext()) {
				Iterator<Term> termIt = atomIt.next().iterator();
				while(termIt.hasNext()) {
					Term t = termIt.next();
					if(t.isVariable()) {
						terms.add((Variable) t);
					}
				}
			}
		} catch (Exception e) {
			throw new AtomSetException(e);
		}
		return terms;
	}
	
	@Override
	public Set<Constant> getConstants() throws AtomSetException {
		Set<Constant> terms = new HashSet<Constant>();
		CloseableIterator<Atom> atomIt = this.iterator();
		try {
			while(atomIt.hasNext()) {
				Iterator<Term> termIt = atomIt.next().iterator();
				while(termIt.hasNext()) {
					Term t = termIt.next();
					if(t.isConstant()) {
						terms.add((Constant) t);
					}
				}
			}
		} catch (Exception e) {
			throw new AtomSetException(e);
		}
		return terms;
	}
	
	@Override
	public Set<Literal> getLiterals() throws AtomSetException {
		Set<Literal> terms = new HashSet<Literal>();
		CloseableIterator<Atom> atomIt = this.iterator();
		try {
			while(atomIt.hasNext()) {
				Iterator<Term> termIt = atomIt.next().iterator();
				while(termIt.hasNext()) {
					Term t = termIt.next();
					if(t.isLiteral()) {
						terms.add((Literal) t);
					}
				}
			}
		} catch (Exception e) {
			throw new AtomSetException(e);
		}
		return terms;
	}
	
	@Override
	public CloseableIterator<Variable> variablesIterator() throws AtomSetException {
		return new CloseableIteratorAdapter<Variable>(this.getVariables().iterator());
	}
	
	@Override
	public CloseableIterator<Constant> constantsIterator() throws AtomSetException {
		return new CloseableIteratorAdapter<Constant>(this.getConstants().iterator());
	}
	
	@Override
	public CloseableIterator<Literal> literalsIterator() throws AtomSetException {
		return new CloseableIteratorAdapter<Literal>(this.getLiterals().iterator());
	}

	@Override
	@Deprecated
	public Set<Term> getTerms(Type type) throws AtomSetException {
		Set<Term> terms = new HashSet<Term>();
		try {
			CloseableIterator<Term> it = this.termsIterator(type);
			while (it.hasNext()) {
				terms.add(it.next());
			}
		} catch (Exception e) {
			throw new AtomSetException(e);
		}
		return terms;
	}

	@Override
	public Set<Predicate> getPredicates() throws AtomSetException {
		Set<Predicate> predicates = new HashSet<Predicate>();
		CloseableIterator<Predicate> it = this.predicatesIterator();
		try {
			while (it.hasNext()) {
				predicates.add(it.next());
			}
		} catch (Exception e) {
			throw new AtomSetException(e);
		}
		return predicates;
	}

	@Override
	@Deprecated
	public boolean isSubSetOf(AtomSet atomset) throws AtomSetException {
		CloseableIterator<Atom> it = atomset.iterator();
		try {
			while (it.hasNext()) {
				Atom a = it.next();
				if (!atomset.contains(a)) {
					return false;
				}
			}
		} catch (Exception e) {
			throw new AtomSetException(e);
		}
		return true;
	}

	@Override
	public boolean isEmpty() throws AtomSetException {
		try {
			return !this.iterator().hasNext();
		} catch (Exception e) {
			throw new AtomSetException(e);
		}
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
			CloseableIterator<Atom> it = this.iterator();
			while (it.hasNext()) {
				Atom a = it.next();
				if(!other.contains(a)) {
					return false;
				}
			}
			it = other.iterator();
			while (it.hasNext()) {
				Atom a = it.next();
				if(!this.contains(a)) {
					return false;
				}
			}
		} catch (Exception e) {
			LOGGER.error("An error occured during equality check: ", e);
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append('[');

		CloseableIterator<Atom> it = this.iterator();
		try {
			if (it.hasNext()) {
				s.append(it.next().toString());
			}
			while (it.hasNext()) {
				s.append(", ");
				s.append(it.next().toString());
			}
		} catch (Exception e) {
			s.append("ERROR: " + e.toString());
		}
		s.append(']');

		return s.toString();
	}

}
