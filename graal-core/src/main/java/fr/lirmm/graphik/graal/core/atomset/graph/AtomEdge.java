/**
 * 
 */
package fr.lirmm.graphik.graal.core.atomset.graph;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import fr.lirmm.graphik.graal.core.AbstractAtom;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.Term.Type;

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

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.lirmm.graphik.kb.core.IAtom#setPredicate(fr.lirmm.graphik.kb.core.
     * IPredicate)
     */
    @Override
    public void setPredicate(Predicate predicate) {
        this.predicate = new PredicateVertex(predicate);
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.lirmm.graphik.kb.core.IAtom#getPredicate()
     */
    @Override
    public Predicate getPredicate() {
        return this.predicate;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.lirmm.graphik.kb.core.IAtom#setTerm(int,
     * fr.lirmm.graphik.kb.core.ITerm)
     */
    @Override
    public void setTerm(int index, Term term) {
        this.terms.set(index, new TermVertex(term));
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.lirmm.graphik.kb.core.IAtom#getTerm(int)
     */
    @Override
    public TermVertex getTerm(int index) {
        return this.terms.get(index);
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.lirmm.graphik.kb.core.IAtom#getTerms()
     */
    @Override
    public List<Term> getTerms() {
        return new LinkedList<Term>(this.terms);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.lirmm.graphik.kb.core.IAtom#getTerms(fr.lirmm.graphik.kb.core.ITerm
     * .Type)
     */
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

    /*
     * (non-Javadoc)
     * 
     * @see fr.lirmm.graphik.alaska.store.graph.Edge#getVertices()
     */
    @Override
    public Set<Vertex> getVertices() {
        Set<Vertex> set = new TreeSet<Vertex>(new VertexComparator());
        set.addAll(terms);
        set.add(predicate);
        return set;
    }

}
