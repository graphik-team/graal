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
 package fr.lirmm.graphik.graal.api.core;

import java.util.Set;

import fr.lirmm.graphik.util.stream.CloseableIterableWithoutException;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;

/**
 * This interface represents an InMemory AtomSet. So, AtomSet methods are
 * redefined without the ability to throw exception.
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public interface InMemoryAtomSet extends AtomSet, CloseableIterableWithoutException<Atom> {
	
	@Override
	boolean contains(Atom atom);

	@Override
	CloseableIteratorWithoutException<Atom> match(Atom atom, Substitution s);

	@Override
	CloseableIteratorWithoutException<Atom> atomsByPredicate(Predicate p);

	@Override
	CloseableIteratorWithoutException<Term> termsByPredicatePosition(Predicate p, int position);

	@Override
	Set<Predicate> getPredicates();

	@Override
	CloseableIteratorWithoutException<Predicate> predicatesIterator();

	@Override
	Set<Term> getTerms();
	
	@Override
	Set<Variable> getVariables();
	
	@Override
	Set<Constant> getConstants();
	
	@Override
	Set<Literal> getLiterals();

	@Override
	CloseableIteratorWithoutException<Term> termsIterator();
	
	@Override
	CloseableIteratorWithoutException<Variable> variablesIterator();
	
	@Override
	CloseableIteratorWithoutException<Constant> constantsIterator();
	
	@Override
	CloseableIteratorWithoutException<Literal> literalsIterator();
	
	@Override
	@Deprecated
	Set<Term> getTerms(Term.Type type);

	@Override
	@Deprecated
	CloseableIteratorWithoutException<Term> termsIterator(Term.Type type);

	@Override
	CloseableIteratorWithoutException<Atom> iterator();

	@Override
	@Deprecated
	boolean isSubSetOf(AtomSet atomset);

	@Override
	boolean isEmpty();

	@Override
	boolean add(Atom atom);

	@Override
	boolean remove(Atom atom);

	@Override
	void clear();

	boolean removeAll(CloseableIteratorWithoutException<? extends Atom> atoms);
	
	boolean removeAll(InMemoryAtomSet atoms);
	
	boolean addAll(InMemoryAtomSet atoms);
	
	boolean addAll(CloseableIteratorWithoutException<? extends Atom> atoms);

}
