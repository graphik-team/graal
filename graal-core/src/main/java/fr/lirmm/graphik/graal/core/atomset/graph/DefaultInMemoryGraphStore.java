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
package fr.lirmm.graphik.graal.core.atomset.graph;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Term.Type;
import fr.lirmm.graphik.graal.api.core.TermGenerator;
import fr.lirmm.graphik.graal.api.store.BatchProcessor;
import fr.lirmm.graphik.graal.api.store.Store;
import fr.lirmm.graphik.graal.core.AtomType;
import fr.lirmm.graphik.graal.core.DefaultVariableGenerator;
import fr.lirmm.graphik.graal.core.TypeFilter;
import fr.lirmm.graphik.graal.core.atomset.AbstractInMemoryAtomSet;
import fr.lirmm.graphik.graal.core.store.DefaultBatchProcessor;
import fr.lirmm.graphik.util.MethodNotImplementedError;
import fr.lirmm.graphik.util.stream.CloseableIteratorAdapter;
import fr.lirmm.graphik.util.stream.CloseableIteratorAggregatorWithoutExeception;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;
import fr.lirmm.graphik.util.stream.Iterators;
import fr.lirmm.graphik.util.stream.filter.Filter;
import fr.lirmm.graphik.util.stream.filter.FilterIteratorWithoutException;
import fr.lirmm.graphik.util.stream.filter.UniqFilter;

/**
 * Implementation of a graph in memory. Inherits directly from Fact.
 */
@SuppressWarnings("deprecation")
public class DefaultInMemoryGraphStore extends AbstractInMemoryAtomSet implements Store {

	private int size = 0;
	
	private Map<Term, TermVertex> terms;
	private Map<Predicate, PredicateVertex> predicates;

	private Map<Predicate, Set<Term>[]> termsByPredicatePosition;
	private TermGenerator freshSymbolGenerator = new DefaultVariableGenerator("EE");

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	public DefaultInMemoryGraphStore() {
		this.terms = CurrentIndexFactory.instance().<Term, TermVertex>createMap();
		this.predicates = CurrentIndexFactory.instance().<Predicate, PredicateVertex>createMap();
		this.termsByPredicatePosition = CurrentIndexFactory.instance().<Predicate, Set<Term>[]>createMap();
	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public Set<Predicate> getPredicates() {
		return Collections.unmodifiableSet(this.predicates.keySet());
	}

	@Override
	public CloseableIteratorWithoutException<Predicate> predicatesIterator() {
		return new CloseableIteratorAdapter<Predicate>(this.getPredicates().iterator());
	}

	@Override
	public CloseableIteratorWithoutException<Atom> iterator() {
		CloseableIteratorWithoutException<Predicate> predicatesIt = this.predicatesIterator();
		List<CloseableIteratorWithoutException<Atom>> list = new LinkedList<CloseableIteratorWithoutException<Atom>>();
		while (predicatesIt.hasNext()) {
			list.add(this.atomsByPredicate(predicatesIt.next()));
		}
		return new CloseableIteratorAggregatorWithoutExeception<Atom>(
				new CloseableIteratorAdapter<CloseableIteratorWithoutException<Atom>>(list.iterator()));

	}

	@Override
	public boolean remove(Atom atom) {
		// TODO implement this method
		throw new MethodNotImplementedError();
	}

	@Override
	public boolean contains(Atom atom) {
		PredicateVertex predicateVertex = this.predicates.get(atom.getPredicate());
		if (predicateVertex == null) {
			return false;
		}
		return predicateVertex.getNeighbors().contains(atom);
	}

	@Override
	public CloseableIteratorWithoutException<Atom> match(Atom atom, Substitution s) {
		CloseableIteratorWithoutException<Atom> it = null;
		final AtomType atomType = new AtomType(atom, s);
		if(atomType.isThereConstant()) {
			// find smallest iterator
			int i = -1;
			int size = Integer.MAX_VALUE;
			for (Term t : atom.getTerms()) {
				++i;
				if (t.isConstant() || s.getTerms().contains(t)) {
					TermVertex tv = this.getTermVertex(s.createImageOf(t));
					if (tv != null) {
						int tmpSize = tv.neighborhoodSize(atom.getPredicate(), i);
						if(tmpSize < size) {
							size = tmpSize;
							it = tv.getNeighbors(atom.getPredicate(), i);
						}
					} else {
						size = 0;
						it = Iterators.<Atom>emptyIterator();
					}
				}
			}
		} else {
			 it = this.atomsByPredicate(atom.getPredicate());
		}
		
		if(atomType.isThereConstraint()) {
			return new FilterIteratorWithoutException<Atom, Atom>(it, new TypeFilter(atomType, s.createImageOf(atom)));
		} else {
			return it;
		}
	}

	@Override
	public CloseableIteratorWithoutException<Atom> atomsByPredicate(Predicate p) {
		PredicateVertex pv = this.getPredicateVertex(p);
		if (pv == null) {
			return Iterators.<Atom>emptyIterator();
		}
		return new FilterIteratorWithoutException<Edge, Atom>(
				new CloseableIteratorAdapter<Edge>(pv.getNeighbors().iterator()), new Filter<Edge>() {
					@Override
					public boolean filter(Edge e) {
						return true;
					}
				});
	}

	@Override
	public int size(Predicate p) {
		PredicateVertex pred = this.getPredicateVertex(p);
		return (pred == null) ? 0 : pred.getNeighbors().size();
	}

	@Override
	public int getDomainSize() {
		return this.terms.size();
	}

	@Override
	public CloseableIteratorWithoutException<Term> termsByPredicatePosition(Predicate p, int position) {
		Set<Term>[] sets = this.termsByPredicatePosition.get(p);
		if (sets == null) {
			return new CloseableIteratorAdapter<Term>(Collections.<Term>emptyList().iterator());
		} else {
			return new CloseableIteratorAdapter<Term>(sets[position].iterator());
		}
	}

	@Override
	public Set<Term> getTerms() {
		return Collections.<Term>unmodifiableSet(this.terms.keySet());
	}

	@Override
	public CloseableIteratorWithoutException<Term> termsIterator() {
		return new CloseableIteratorAdapter<Term>(this.getTerms().iterator());
	}

	@Override
	@Deprecated
	public Set<Term> getTerms(Type type) {
		Set<Term> set = new HashSet<Term>();
		for (Term t : this.terms.keySet())
			if (type.equals(t.getType()))
				set.add(t);

		return set;
	}

	@Override
	@Deprecated
	public CloseableIteratorWithoutException<Term> termsIterator(Term.Type type) {
		return new CloseableIteratorAdapter<Term>(this.getTerms(type).iterator());
	}

	@Override
	public boolean add(Atom atom) {
		List<TermVertex> atomTerms = new LinkedList<TermVertex>();
		PredicateVertex atomPredicate;

		for (Term t : atom.getTerms()) {
			atomTerms.add(this.addTermVertex(TermVertexFactory.instance().createTerm(t)));
		}

		atomPredicate = this.addPredicateVertex(new PredicateVertex(atom.getPredicate()));
		AtomEdge atomEdge = new AtomEdge(atomPredicate, atomTerms);
		return this.addAtomEdge(atomEdge);
	}

	@Override
	public void clear() {
		this.terms.clear();
		this.predicates.clear();
		this.termsByPredicatePosition.clear();
		this.size = 0;
	}

	@Override
	public TermGenerator getFreshSymbolGenerator() {
		return freshSymbolGenerator;
	}
	
	public int size() {
		return this.size;
	}

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

	private TermVertex getTermVertex(Term term) {
		return this.terms.get(term);
	}

	private PredicateVertex getPredicateVertex(Predicate predicate) {
		return this.predicates.get(predicate);

	}

	private TermVertex addTermVertex(TermVertex term) {
		TermVertex t = this.terms.get(term);
		if (t == null) {
			t = term;
			this.terms.put(t, t);
		}
		return t;
	}

	private PredicateVertex addPredicateVertex(PredicateVertex predicate) {
		PredicateVertex p = this.predicates.get(predicate);
		if (p == null) {
			p = predicate;
			this.predicates.put(p, p);
			@SuppressWarnings("unchecked")
			Set<Term>[] array = new Set[predicate.getArity()];
			for (int i = 0; i < array.length; ++i) {
				array[i] = CurrentIndexFactory.instance().<Term>createSet();
			}
			this.termsByPredicatePosition.put(p, array);
		}
		return p;
	}

	private boolean addAtomEdge(AtomEdge atom) {
		PredicateVertex predicateVertex = this.predicates.get(atom.getPredicate());
		boolean val = predicateVertex.addNeighbor(atom);
		if (val) {
			++size;
			CloseableIteratorWithoutException<Term> it = new FilterIteratorWithoutException<Term, Term>(atom.getTerms().iterator(), new UniqFilter<Term>());
			while(it.hasNext()) {
				TermVertex term = (TermVertex) it.next();
				term.addNeighbor(atom);
			}
			it.close();

			Set<Term>[] sets = this.termsByPredicatePosition.get(atom.getPredicate());
			int i = -1;
			for (Term t : atom) {
				sets[++i].add(t);
			}
		}
		return val;
	}

	@Override
	public BatchProcessor createBatchProcessor() throws AtomSetException {
		return new DefaultBatchProcessor(this);
	}

	@Override
	public boolean isWriteable() {
		return true;
	}

	@Override
	public void close() {
	}

}
