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
 package fr.lirmm.graphik.graal.core.atomset.graph;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.AtomComparator;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.TermValueComparator;
import fr.lirmm.graphik.graal.core.atomset.AbstractInMemoryAtomSet;
import fr.lirmm.graphik.graal.core.atomset.InMemoryAtomSet;
import fr.lirmm.graphik.graal.core.stream.IteratorAtomReader;
import fr.lirmm.graphik.graal.core.term.Term;
import fr.lirmm.graphik.graal.core.term.Term.Type;
import fr.lirmm.graphik.util.MethodNotImplementedError;

/**
 * Implementation of a graph in memory. Inherits directly from Fact.
 */
public class DefaultInMemoryGraphAtomSet extends AbstractInMemoryAtomSet implements GraphAtomSet, InMemoryAtomSet {

    private TreeSet<TermVertex> terms;
    private TreeSet<PredicateVertex> predicates;
    private TreeSet<AtomEdge> atoms;

    // /////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // /////////////////////////////////////////////////////////////////////////

    public DefaultInMemoryGraphAtomSet() {
        this.terms = new TreeSet<TermVertex>(new TermValueComparator());
        this.predicates = new TreeSet<PredicateVertex>();
        this.atoms = new TreeSet<AtomEdge>(new AtomComparator());
    }

    // /////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    // /////////////////////////////////////////////////////////////////////////

	@Override
	public Iterator<AtomEdge> getAtoms(Predicate p) {
		// TODO
		throw new MethodNotImplementedError();
	}

	@Override
	public Iterator<AtomEdge> getAtoms(Term t) {
		// TODO
		throw new MethodNotImplementedError();
	}

    @Override
	public Set<Predicate> getPredicates() {
		Set<Predicate> predicates = new TreeSet<Predicate>();
		for (Atom a : this) {
			predicates.add(a.getPredicate());
		}
		return predicates;
	}
    
    @Override
    public Iterator<Atom> iterator() {
        return new IteratorAtomReader(new TreeSet<Atom>(this.atoms).iterator());
    }

    /**
     * (super-Javadoc)
     * 
     * @see fr.lirmm.graphik.kb.core.IWriteableAtomSet#remove(fr.lirmm.graphik.kb.core.IAtom)
     */
    @Override
    public boolean remove(Atom atom) {
        // TODO implement this method
        throw new Error("This method isn't implemented");
    }

    @Override
    public boolean removeAll(Iterable<? extends Atom> atoms) {
        // TODO implement this method
        throw new MethodNotImplementedError();
    }

    @Override
    public boolean contains(Atom atom) {
        return this.atoms.contains(atom);
    }

    @Override
    public TreeSet<Term> getTerms() {
        return new TreeSet<Term>(this.terms);
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
    public boolean addAll(Iterable<? extends Atom> atoms)  {
    	boolean isChanged = false;
        for (Atom a : atoms) {
            isChanged = this.add(a) || isChanged ;
        }
        return isChanged;
    }

    /**
     * @see fr.lirmm.graphik.alaska.store.Store#write(fr.lirmm.graphik.kb.core.IAtom)
     */
    @Override
    public boolean add(Atom atom) {
        List<TermVertex> atomTerms = new LinkedList<TermVertex>();
        PredicateVertex atomPredicate;

        for (Term t : atom.getTerms())
			atomTerms.add(this.addTermVertex(TermVertexFactory.instance()
					.createTerm(t)));

        atomPredicate = this.addPredicateVertex(new PredicateVertex(atom
                .getPredicate()));
        AtomEdge atomEdge = new AtomEdge(atomPredicate, atomTerms);
        return this.addAtomEdge(atomEdge);
    }

    // /////////////////////////////////////////////////////////////////////////
    // PRIVATE METHODS
    // /////////////////////////////////////////////////////////////////////////

    TermVertex getTermVertex(Term term) {
		TermVertex t = this.terms.tailSet(
				TermVertexFactory.instance().createTerm(term))
				.first();
        return (term.equals(t)) ? t : null;
    }

    PredicateVertex getPredicateVertex(PredicateVertex predicate) {
        PredicateVertex p = this.predicates.tailSet(predicate).first();
        return (predicate.equals(p)) ? p : null;
    }

    TermVertex addTermVertex(TermVertex term) {
        if (this.terms.add(term))
            return term;
        else
            return this.terms.tailSet(term).first();
    }

    PredicateVertex addPredicateVertex(PredicateVertex predicate) {
        if (this.predicates.add(predicate))
            return predicate;
        else
            return this.predicates.tailSet(predicate).first();
    }

    boolean addAtomEdge(AtomEdge atom) {
        boolean val = this.atoms.add(atom);
        if (val) {
            for (Vertex term : atom.getVertices()) {
                term.getEdges().add(atom);
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
