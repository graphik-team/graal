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
package fr.lirmm.graphik.graal.core.atomset;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.ConstantGenerator;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.core.AtomType;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.DefaultConstantGenerator;
import fr.lirmm.graphik.graal.core.TypeFilter;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.CloseableIteratorAdapter;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;
import fr.lirmm.graphik.util.stream.IteratorException;
import fr.lirmm.graphik.util.stream.converter.Converter;
import fr.lirmm.graphik.util.stream.converter.ConverterIteratorWithoutException;
import fr.lirmm.graphik.util.stream.filter.Filter;
import fr.lirmm.graphik.util.stream.filter.FilterIteratorWithoutException;

/**
 * 
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 */
public class LinkedListAtomSet extends AbstractInMemoryAtomSet implements InMemoryAtomSet {

	private LinkedList<Atom> linkedList;
	private ConstantGenerator freshSymbolGenerator = new DefaultConstantGenerator("EE");

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	public LinkedListAtomSet() {
		this.linkedList = new LinkedList<Atom>();
	}

	@Override
	public CloseableIteratorWithoutException<Atom> match(Atom atom, Substitution s) {
		final AtomType atomType = new AtomType(atom, s);
		return new FilterIteratorWithoutException<Atom, Atom>(this.atomsByPredicate(atom.getPredicate()), new TypeFilter(atomType, s.createImageOf(atom)));
	}

	@Override
	public CloseableIteratorWithoutException<Atom> atomsByPredicate(final Predicate p) {
		return new FilterIteratorWithoutException<Atom, Atom>(this.iterator(), new Filter<Atom>() {
			@Override
			public boolean filter(Atom a) {
				return a.getPredicate().equals(p);
			}
		});
	}

	@Override
	public CloseableIteratorWithoutException<Term> termsByPredicatePosition(Predicate p, final int position) {
		Set<Term> terms = new HashSet<Term>();
		CloseableIteratorWithoutException<Term> it = new ConverterIteratorWithoutException<Atom, Term>(this.atomsByPredicate(
		    p), new Converter<Atom, Term>() {
			@Override
			public Term convert(Atom atom) {
				return atom.getTerm(position);
			}
		});
		while (it.hasNext()) {
			terms.add(it.next());
		}
		it.close();
		return new CloseableIteratorAdapter<Term>(terms.iterator());
	}

	public LinkedListAtomSet(LinkedList<Atom> list) {
		this.linkedList = list;
	}

	public LinkedListAtomSet(Atom... atoms) {
		this();
		for (Atom a : atoms)
			this.linkedList.add(a);
	}

	public LinkedListAtomSet(CloseableIterator<Atom> it) throws IteratorException {
		this();
		while (it.hasNext()) {
			this.linkedList.add(it.next());
		}
	}

	public LinkedListAtomSet(CloseableIteratorWithoutException<Atom> it) {
		this();
		while (it.hasNext()) {
			this.linkedList.add(it.next());
		}
	}

	/**
	 * copy constructor
	 * 
	 * @throws IteratorException
	 */
	public LinkedListAtomSet(AtomSet atomset) throws IteratorException {
		this();
		CloseableIterator<Atom> it = atomset.iterator();
		while (it.hasNext()) {
			this.add(new DefaultAtom(it.next()));
		}
	}

	/**
	 * copy constructor
	 * 
	 */
	public LinkedListAtomSet(InMemoryAtomSet atomset) {
		this();
		CloseableIteratorWithoutException<Atom> it = atomset.iterator();
		while (it.hasNext()) {
			this.add(new DefaultAtom(it.next()));
		}
	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public Set<Predicate> getPredicates() {
		Set<Predicate> predicates = new HashSet<Predicate>();
		CloseableIteratorWithoutException<Atom> it = this.iterator();
		while (it.hasNext()) {
			Atom a = it.next();
			predicates.add(a.getPredicate());
		}
		return predicates;
	}

	@Override
	public CloseableIteratorWithoutException<Predicate> predicatesIterator() {
		return new CloseableIteratorAdapter<Predicate>(this.getPredicates().iterator());
	}

	@Override
	public boolean add(Atom atom) {
		if (this.linkedList.contains(atom))
			return false;

		return this.linkedList.add(atom);
	}

	@Override
	public Set<Term> getTerms() {
		Set<Term> terms = new HashSet<Term>();
		for (Atom a : this.linkedList) {
			terms.addAll(a.getTerms());
		}
		return terms;
	}

	@Override
	public ConstantGenerator getFreshSymbolGenerator() {
		return freshSymbolGenerator;
	}

	@Override
	public CloseableIteratorWithoutException<Term> termsIterator() {
		return new CloseableIteratorAdapter<Term>(this.getTerms().iterator());
	}

	@Override
	@Deprecated
	public Set<Term> getTerms(Term.Type type) {
		Set<Term> terms = new HashSet<Term>();
		for (Atom a : this.linkedList) {
			terms.addAll(a.getTerms(type));
		}
		return terms;
	}

	@Override
	@Deprecated
	public CloseableIteratorWithoutException<Term> termsIterator(Term.Type type) {
		return new CloseableIteratorAdapter<Term>(this.getTerms(type).iterator());
	}

	@Override
	public boolean remove(Atom atom) {
		return this.linkedList.remove(atom);
	}

	@Override
	public CloseableIteratorWithoutException<Atom> iterator() {
		return new CloseableIteratorAdapter<Atom>(this.linkedList.iterator());
	}

	@Override
	public boolean isEmpty() {
		return this.linkedList.isEmpty();
	}

	public int size() {
		return this.linkedList.size();
	}

	@Override
	public void clear() {
		this.linkedList.clear();
	}

};
