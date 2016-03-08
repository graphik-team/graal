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
package fr.lirmm.graphik.graal.core.atomset.graph;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomComparator;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Term.Type;
import fr.lirmm.graphik.graal.api.core.TermValueComparator;
import fr.lirmm.graphik.graal.core.AtomMatcher;
import fr.lirmm.graphik.graal.core.atomset.AbstractInMemoryAtomSet;
import fr.lirmm.graphik.util.stream.GIterator;
import fr.lirmm.graphik.util.stream.IteratorAdapter;
import fr.lirmm.graphik.util.stream.filter.Filter;
import fr.lirmm.graphik.util.stream.filter.FilterIterator;

/**
 * Implementation of a graph in memory. Inherits directly from Fact.
 */
public class DefaultInMemoryGraphAtomSet extends AbstractInMemoryAtomSet implements GraphAtomSet, InMemoryAtomSet {

	private TreeSet<TermVertex>      terms;
	private TreeSet<PredicateVertex> predicates;
	private TreeSet<AtomEdge>        atoms;

	private TreeMap<Predicate, Set<Term>[]> termsByPredicatePosition;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	public DefaultInMemoryGraphAtomSet() {
		this.terms = new TreeSet<TermVertex>(TermValueComparator.instance());
		this.predicates = new TreeSet<PredicateVertex>();
		this.atoms = new TreeSet<AtomEdge>(AtomComparator.instance());
		
		this.termsByPredicatePosition = new TreeMap<Predicate, Set<Term>[]>();
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
	public GIterator<Predicate> predicatesIterator() {
		return new IteratorAdapter<Predicate>(this.getPredicates().iterator());
	}

	@Override
	public GIterator<Atom> iterator() {
		return new IteratorAdapter<Atom>(new TreeSet<Atom>(this.atoms).iterator());
	}

	@Override
	public boolean remove(Atom atom) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public boolean contains(Atom atom) {
		return this.atoms.contains(atom);
	}

	@Override
	public GIterator<Atom> match(Atom atom) {
		final AtomMatcher matcher = new AtomMatcher(atom);
		GIterator<Atom> it = null;
		int i = -1;
		for (Term t : atom.getTerms()) {
			++i;
			if (t.isConstant()) {
				it = this.getTermVertex(t).getNeighbors(atom.getPredicate(), i);
			}
		}
		return new FilterIterator<Atom, Atom>(it, new Filter<Atom>() {
			@Override
			public boolean filter(Atom a) {
				return matcher.check((Atom) a);
			}
		});
	}

	@Override
	public GIterator<Atom> atomsByPredicate(Predicate p) {
		return new FilterIterator<Edge, Atom>(new IteratorAdapter<Edge>(this.getPredicateVertex(p).getEdges()
		                                                                    .iterator()), new Filter<Edge>() {
			@Override
			public boolean filter(Edge e) {
				return true;
			}
		});
	}

	@Override
	public int count(Predicate p) {
		PredicateVertex pred = this.getPredicateVertex(p);
		return (pred == null) ? 0 : pred.getEdges().size();
	}

	@Override
	public int getDomainSize() {
		return this.terms.size();
	}

	@Override
	public GIterator<Term> termsByPredicatePosition(Predicate p, int position) {
		Set<Term>[] sets = this.termsByPredicatePosition.get(p);
		if(sets == null) {
			return new IteratorAdapter<Term>(Collections.<Term>emptyIterator());
		} else {
			return new IteratorAdapter<Term>(sets[position].iterator());
		}
	}

	@Override
	public Set<Term> getTerms() {
		return new TreeSet<Term>(this.terms);
	}

	@Override
	public GIterator<Term> termsIterator() {
		return new IteratorAdapter<Term>(this.getTerms().iterator());
	}

	@Override
	public Set<Term> getTerms(Type type) {
		TreeSet<Term> set = new TreeSet<Term>();
		for (Term t : this.terms)
			if (type.equals(t.getType()))
				set.add(t);

		return set;
	}

	@Override
	public GIterator<Term> termsIterator(Term.Type type) {
		return new IteratorAdapter<Term>(this.getTerms(type).iterator());
	}

	/**
	 * @see fr.lirmm.graphik.alaska.store.Store#write(fr.lirmm.graphik.kb.core.IAtom)
	 */
	@Override
	public boolean add(Atom atom) {
		List<TermVertex> atomTerms = new LinkedList<TermVertex>();
		PredicateVertex atomPredicate;

		for (Term t : atom.getTerms()) {
			// if (t.isConstant()) {
			// t =
			// DefaultTermFactory.instance().createConstant(t.getIdentifier().hashCode());
			// }
			atomTerms.add(this.addTermVertex(TermVertexFactory.instance().createTerm(t)));
		}

		atomPredicate = this.addPredicateVertex(new PredicateVertex(atom.getPredicate().getIdentifier()/*
																									    * .
																									    * hashCode
																									    * (
																									    * )
																									    */,
		                                                            atom.getPredicate().getArity()));
		AtomEdge atomEdge = new AtomEdge(atomPredicate, atomTerms);
		return this.addAtomEdge(atomEdge);
	}

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

	TermVertex getTermVertex(Term term) {
		TermVertex t = this.terms.tailSet(TermVertexFactory.instance().createTerm(term)).first();
		return (TermValueComparator.instance().compare(term, t) == 0) ? t : null;
	}

	PredicateVertex getPredicateVertex(Predicate predicate) {
		PredicateVertex p = this.predicates.tailSet(new PredicateVertex(predicate)).first();
		return (predicate.equals(p)) ? p : null;
	}

	TermVertex addTermVertex(TermVertex term) {
		if (this.terms.add(term))
			return term;
		else
			return this.terms.tailSet(term).first();
	}

	PredicateVertex addPredicateVertex(PredicateVertex predicate) {
		if (this.predicates.add(predicate)) {
			Set<Term>[] array = new Set[predicate.getArity()];
			for(int i = 0; i < array.length; ++i) {
				array[i] = new TreeSet<Term>();
			}
			this.termsByPredicatePosition.put(predicate, array);
			return predicate;
		} else
			return this.predicates.tailSet(predicate).first();
	}

	boolean addAtomEdge(AtomEdge atom) {
		boolean val = this.atoms.add(atom);
		if (val) {
			for (Vertex v : atom.getVertices()) {
				v.getEdges().add(atom);
				if (v instanceof TermVertex) {
					TermVertex term = (TermVertex) v;
					term.add(atom);
				}
			}

			Set<Term>[] sets = this.termsByPredicatePosition.get(atom.getPredicate());
			int i = -1;
			for (Term t : atom) {
				sets[++i].add(t);
			}
		}
		return val;
	}

	@Override
	public void clear() {
		this.terms.clear();
		this.predicates.clear();
		this.atoms.clear();
	}

}
