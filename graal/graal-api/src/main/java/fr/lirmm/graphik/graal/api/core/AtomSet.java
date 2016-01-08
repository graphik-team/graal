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
 package fr.lirmm.graphik.graal.api.core;

import java.util.Iterator;
import java.util.Set;

import fr.lirmm.graphik.util.stream.GIterable;
import fr.lirmm.graphik.util.stream.GIterator;

/**
 * This interface represents a set of atoms.
 * 
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public interface AtomSet extends GIterable<Atom> {

	/**
	 * Returns true if this atom set contains the specified atom.
	 * 
	 * @param atom
	 * @return true if this atom set contains the specified atom.
	 * @throws AtomSetException
	 */
	boolean contains(Atom atom) throws AtomSetException;
	
	/**
	 * Returns an iterator over atoms that match predicate and constants from
	 * the specified atom.
	 * 
	 * @param atom
	 * @return
	 * @throws AtomSetException
	 */
	GIterator<Atom> match(Atom atom) throws AtomSetException;

	/**
	 * Returns a Set of all predicates in this atom set.
	 * 
	 * @return
	 * @throws AtomSetException
	 */
	Set<Predicate> getPredicates() throws AtomSetException;

	/**
	 * Returns an iterator of all predicates in this atom set. Each predicate is
	 * iterated only once time.
	 * 
	 * @return
	 * @throws AtomSetException
	 */
	GIterator<Predicate> predicatesIterator() throws AtomSetException;

	/**
	 * Returns a Set of all terms in this atom set.
	 * 
	 * @return
	 * @throws IAtomSetException
	 */
	Set<Term> getTerms() throws AtomSetException;

	/**
	 * Returns an iterator of all terms in this atom set. Each term is iterated
	 * only once time.
	 * 
	 * @return
	 * @throws AtomSetException
	 */
	GIterator<Term> termsIterator() throws AtomSetException;
	
	/**
	 * Returns a Set of all terms of the specified type in this atom set.
	 * 
	 * @param type
	 * @return a collection of all terms of the specified type in this atom set.
	 * @throws AtomSetException
	 */
	Set<Term> getTerms(Term.Type type) throws AtomSetException;


	/**
	 * Retuns on iterator of all terms of the specified type in this atom set.
	 * Each term is iterated only once time.
	 * 
	 * @param type
	 * @return
	 * @throws AtomSetException
	 */
	GIterator<Term> termsIterator(Term.Type type) throws AtomSetException;

	/**
	 * Use AtomSets.contains instead.
	 * 
	 * Check if all atoms of this AtomSet are also contained in the specified
	 * AtomSet.
	 * 
	 * @param atomset
	 * @return
	 */
	@Deprecated 
	boolean isSubSetOf(AtomSet atomset) throws AtomSetException;

	/**
	 * 
	 * @return true if this atom set is empty, false otherwise.
	 */
	boolean isEmpty() throws AtomSetException;

	/**
	 * Add the specified atom to this atom set if is not already present.
	 * 
	 * @param atom
	 *            - atom to be added to this atom set
	 * @return true if this atom set did not already contain the specified atom
	 */
	boolean add(Atom atom) throws AtomSetException;

	/**
	 * Add the specified atom stream to this atom set.
	 * 
	 * @param atoms
	 * @return true if this atomset changed as a result of the call
	 * @throws
	 */
	boolean addAll(Iterator<? extends Atom> atoms) throws AtomSetException;

	/**
	 * @param atoms
	 * @return
	 */
	boolean addAll(AtomSet atoms) throws AtomSetException;

	/**
	 * Remove the specified atom from this this atom set.
	 * 
	 * @param atom
	 *            - the atom to be removed
	 * @return true if this atom set contained the specified atom.
	 */
	boolean remove(Atom atom) throws AtomSetException;

	/**
	 * Remove the specified atom stream from this atom set.
	 * 
	 * @param stream
	 *            - the atom stream to be removed.
	 * @return true if this atomset changed as a result of the call
	 * @throws AtomSetException
	 */
	boolean removeAll(Iterator<? extends Atom> atoms) throws AtomSetException;

	boolean removeAll(AtomSet atoms) throws AtomSetException;

	/**
	 * 
	 */
	void clear() throws AtomSetException;

	/**
	 * Returns an iterator over the atoms in this atom set.
	 * 
	 * @return an iterator over the atoms in this atom set.
	 */
	@Override
	GIterator<Atom> iterator();

}
