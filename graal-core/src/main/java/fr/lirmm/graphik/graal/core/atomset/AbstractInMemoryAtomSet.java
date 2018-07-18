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

import java.util.Set;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.Constant;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Literal;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Term.Type;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.CloseableIteratorAdapter;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
// TODO add cause to Errors
@SuppressWarnings("deprecation")
public abstract class AbstractInMemoryAtomSet extends AbstractAtomSet implements InMemoryAtomSet {

	@Override
	public boolean contains(Atom atom) {
		try {
			return super.contains(atom);
		} catch (AtomSetException e) {
			throw new Error("It should never happen.");
		}
	}

	@Override
	public boolean addAll(CloseableIteratorWithoutException<? extends Atom> atoms) {
		try {
			return super.addAll(atoms);
		} catch (AtomSetException e) {
			throw new Error("It should never happen.");
		}
	}

	@Override
	public boolean addAll(InMemoryAtomSet atoms) {
		try {
			return super.addAll(atoms);
		} catch (AtomSetException e) {
			throw new Error("It should never happen.");
		}
	}

	@Override
	public boolean removeAll(CloseableIteratorWithoutException<? extends Atom> atoms) {
		try {
			return super.removeAll(atoms);
		} catch (AtomSetException e) {
			throw new Error("It should never happen.");
		}
	}

	@Override
	public boolean removeAll(InMemoryAtomSet atoms) {
		try {
			return super.removeAll(atoms);
		} catch (AtomSetException e) {
			throw new Error("It should never happen.");
		}
	}
	
	@Override
	public CloseableIterator<Atom> match(Atom atom) throws AtomSetException {
		try {
			return super.match(atom);
    	} catch (AtomSetException e) {
    		throw new Error("It should never happen.");
    	}
	}

	@Override
	public Set<Term> getTerms() {
		try {
			return super.getTerms();
		} catch (AtomSetException e) {
			throw new Error("It should never happen.");
		}
	}
	
	@Override
	public Set<Variable> getVariables() {
		try {
			return super.getVariables();
		} catch (AtomSetException e) {
			throw new Error("It should never happen.");
		}
	}
	
	@Override
	public Set<Constant> getConstants() {
		try {
			return super.getConstants();
		} catch (AtomSetException e) {
			throw new Error("It should never happen.");
		}
	}
	
	@Override
	public Set<Literal> getLiterals() {
		try {
			return super.getLiterals();
		} catch (AtomSetException e) {
			throw new Error("It should never happen.");
		}
	}

	@Override
	@Deprecated
	public Set<Term> getTerms(Type type) {
		try {
			return super.getTerms(type);
		} catch (AtomSetException e) {
			throw new Error("It should never happen.");
		}
	}
	
	@Override
	public CloseableIteratorWithoutException<Variable> variablesIterator() {
		return new CloseableIteratorAdapter<Variable>(this.getVariables().iterator());
	}
	
	@Override
	public CloseableIteratorWithoutException<Constant> constantsIterator() {
		return new CloseableIteratorAdapter<Constant>(this.getConstants().iterator());
		
	}
	
	@Override
	public CloseableIteratorWithoutException<Literal> literalsIterator() {
		return new CloseableIteratorAdapter<Literal>(this.getLiterals().iterator());
	}

	@Override
	public Set<Predicate> getPredicates() {
		try {
			return super.getPredicates();
		} catch (AtomSetException e) {
			throw new Error("It should never happen.");
		}
	}

	@Override
	@Deprecated
	public boolean isSubSetOf(AtomSet atomset) {
		try {
			return super.isSubSetOf(atomset);
		} catch (AtomSetException e) {
			throw new Error("It should never happen.");
		}
	}

	@Override
	public boolean isEmpty() {
		try {
			return super.isEmpty();
		} catch (AtomSetException e) {
			throw new Error("It should never happen.");
		}
	}

}
