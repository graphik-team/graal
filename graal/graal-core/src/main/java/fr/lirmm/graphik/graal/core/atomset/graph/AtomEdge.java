/* Graal v0.7.4
 * Copyright (c) 2014-2015 Inria Sophia Antipolis - Méditerranée / LIRMM (Université de Montpellier & CNRS)
 * All rights reserved.
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * Author(s): Clément SIPIETER
 *            Mélanie KÖNIG
 *            Swan ROCHER
 *            Jean-François BAGET
 *            Michel LECLÈRE
 *            Marie-Laure MUGNIER
 */
 /**
 * 
 */
package fr.lirmm.graphik.graal.core.atomset.graph;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.impl.AbstractAtom;
import fr.lirmm.graphik.graal.core.term.Term;
import fr.lirmm.graphik.graal.core.term.Term.Type;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
class AtomEdge extends AbstractAtom implements Edge {

    private PredicateVertex predicate;
    private List<TermVertex> terms;

    // /////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // /////////////////////////////////////////////////////////////////////////

    /**
     * @param predicate
     * @param terms
     */
    public AtomEdge(PredicateVertex predicate, List<TermVertex> terms) {
        this.predicate = predicate;
        this.terms = terms;
    }

    // /////////////////////////////////////////////////////////////////////////
    // IATOM METHODS
    // /////////////////////////////////////////////////////////////////////////

    @Override
    public void setPredicate(Predicate predicate) {
        this.predicate = new PredicateVertex(predicate);
    }

    @Override
    public Predicate getPredicate() {
        return this.predicate;
    }

    @Override
    public void setTerm(int index, Term term) {
		this.terms.set(index, TermVertexFactory.instance().createTerm(term));
    }

    @Override
    public TermVertex getTerm(int index) {
        return this.terms.get(index);
    }

    @Override
    public List<Term> getTerms() {
        return new LinkedList<Term>(this.terms);
    }

    @Override
    public Collection<Term> getTerms(Type type) {
        Collection<Term> typedTerms = new LinkedList<Term>();
        for (Term term : this.terms)
            if (type.equals(term.getType()))
                typedTerms.add(term);

        return typedTerms;
    }

    // /////////////////////////////////////////////////////////////////////////
    // EDGE METHODS
    // /////////////////////////////////////////////////////////////////////////

    @Override
    public Set<Vertex> getVertices() {
        Set<Vertex> set = new TreeSet<Vertex>(new VertexComparator());
        set.addAll(terms);
        set.add(predicate);
        return set;
    }

}
