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
package fr.lirmm.graphik.graal.api.store;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.util.stream.CloseableIterator;

/**
 * This interface represents a persistent AtomSet.
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public interface Store extends AtomSet {
	
	BatchProcessor createBatchProcessor() throws AtomSetException;
	
	boolean isWriteable() throws AtomSetException;
	
	/**
	 * Returns the number of atoms related to the specified predicate or a
	 * greater estimation of it. In the worst case, this method can return
	 * {@link Integer.MAX_VALUE}.
	 * 
	 * @param p
	 *            a predicate
	 * @return the number of atoms related to the specified predicate in this store.
	 */
	int size(Predicate p);

	/**
	 * Returns the numbers of different terms referenced by atoms in this store
	 * or a greater estimation of it. In the worst case, this method can return
	 * {@link Integer.MAX_VALUE}.
	 * 
	 * @return the numbers of different terms referenced by atoms in this store.
	 */
	int getDomainSize();

	@Override
	CloseableIterator<Atom> iterator();

	@Override
	CloseableIterator<Atom> match(Atom atom, Substitution s) throws AtomSetException;

	@Override
	CloseableIterator<Atom> atomsByPredicate(Predicate p) throws AtomSetException;

	@Override
	CloseableIterator<Term> termsByPredicatePosition(Predicate p, int position) throws AtomSetException;

	@Override
	CloseableIterator<Predicate> predicatesIterator() throws AtomSetException;

	@Override
	CloseableIterator<Term> termsIterator() throws AtomSetException;

	@Override
	@Deprecated
	CloseableIterator<Term> termsIterator(Term.Type type) throws AtomSetException;

	void close();

}
