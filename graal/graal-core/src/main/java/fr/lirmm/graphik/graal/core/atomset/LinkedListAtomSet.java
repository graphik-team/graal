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
 package fr.lirmm.graphik.graal.core.atomset;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.stream.IteratorAtomReader;

/**
 * 
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 */
public class LinkedListAtomSet extends AbstractInMemoryAtomSet implements
		InMemoryAtomSet, Collection<Atom> {

	private LinkedList<Atom> linkedList;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	public LinkedListAtomSet() {
		this.linkedList = new LinkedList<Atom>();
	}

	public LinkedListAtomSet(LinkedList<Atom> list) {
		this.linkedList = list;
	}

	public LinkedListAtomSet(Atom... atoms) {
		this();
		for (Atom a : atoms)
			this.linkedList.add(a);
	}

	public LinkedListAtomSet(Iterable<Atom> it) {
		this();
		for (Atom a : it)
			this.linkedList.add(a);
	}

	public LinkedListAtomSet(Iterator<Atom> it) {
		this();
		while (it.hasNext()) {
			this.linkedList.add(it.next());
		}
	}

	/**
	 *  copy constructor
	 */
	public LinkedListAtomSet(AtomSet atomset) {
		this();
		for (Atom atom : atomset) {
			this.add(new DefaultAtom(atom));
		}
	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////
	 
	@Override
	public Set<Predicate> getPredicates() {
		Set<Predicate> predicates = new TreeSet<Predicate>();
		for (Atom a : this) {
			predicates.add(a.getPredicate());
		}
		return predicates;
	}

	@Override
	public boolean add(Atom atom) {
		if (this.linkedList.contains(atom))
			return false;

		return this.linkedList.add(atom);
	}

	@Override
	public boolean addAll(Collection<? extends Atom> c) {
		return this.addAll((Iterable<? extends Atom>) c);
	}

	@Override
	public Set<Term> getTerms() {
		Set<Term> terms = new TreeSet<Term>();
		for (Atom a : this.linkedList) {
			terms.addAll(a.getTerms());
		}
		return terms;
	}

	@Override
	public Set<Term> getTerms(Term.Type type) {
		Set<Term> terms = new TreeSet<Term>();
		for (Atom a : this.linkedList) {
			terms.addAll(a.getTerms(type));
		}
		return terms;
	}

	@Override
	public boolean remove(Atom atom) {
		return this.linkedList.remove(atom);
	}

	@Override
	public boolean removeAll(Iterable<? extends Atom> atoms) {
		boolean isChanged = false;
		for (Atom a : atoms) {
			isChanged = this.linkedList.remove(a) || isChanged;
		}
		return isChanged;
	}

	@Override
	public Iterator<Atom> iterator() {
		return new IteratorAtomReader(this.linkedList.iterator());
	}

	@Override
	public String toString() {
		return this.linkedList.toString();
	}

	@Override
	public boolean isEmpty() {
		return this.linkedList.isEmpty();
	}

	@Override
	public int size() {
		return this.linkedList.size();
	}

	@Override
	public void clear() {
		this.linkedList.clear();
	}

	@Override
	public boolean contains(Object o) {
		if (o instanceof Atom)
			this.linkedList.contains((Atom) o);

		return false;
	}

	@Override
	public boolean contains(Atom atom) {
		return this.linkedList.contains(atom);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return this.linkedList.containsAll(c);
	}

	@Override
	public boolean remove(Object o) {
		return this.linkedList.remove(o);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return this.linkedList.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return this.linkedList.retainAll(c);
	}

	@Override
	public Object[] toArray() {
		return this.linkedList.toArray();
	}

	@Override
	public <T> T[] toArray(T[] t) {
		return this.linkedList.toArray(t);
	}

};
