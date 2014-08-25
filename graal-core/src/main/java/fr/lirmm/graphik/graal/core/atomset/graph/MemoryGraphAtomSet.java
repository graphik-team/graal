package fr.lirmm.graphik.graal.core.atomset.graph;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.AtomComparator;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.Term.Type;
import fr.lirmm.graphik.graal.core.TermValueComparator;
import fr.lirmm.graphik.graal.core.atomset.AbstractReadOnlyAtomSet;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
import fr.lirmm.graphik.graal.core.stream.IteratorAtomReader;
import fr.lirmm.graphik.util.stream.ObjectReader;

/**
 * Implementation of a graph in memory. Inherits directly from Fact.
 */
public class MemoryGraphAtomSet extends AbstractReadOnlyAtomSet implements AtomSet {

    private TreeSet<TermVertex> terms;
    private TreeSet<PredicateVertex> predicates;
    private TreeSet<AtomEdge> atoms;

    // /////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // /////////////////////////////////////////////////////////////////////////

    public MemoryGraphAtomSet() {
        this.terms = new TreeSet<TermVertex>(new TermValueComparator());
        this.predicates = new TreeSet<PredicateVertex>();
        this.atoms = new TreeSet<AtomEdge>(new AtomComparator());
    }

    // /////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    // /////////////////////////////////////////////////////////////////////////

    /*
     * (non-Javadoc)
     * 
     * @see fr.lirmm.graphik.kb.core.IAtomSet#iterator()
     */
    @Override
    public ObjectReader<Atom> iterator() {
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

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.lirmm.graphik.kb.core.IWriteableAtomSet#remove(fr.lirmm.graphik.kb
     * .stream.IAtomReader)
     */
    @Override
    public void remove(Iterable<Atom> atoms) {
        // TODO implement this method
        throw new Error("This method isn't implemented");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.lirmm.graphik.kb.core.IAtomSet#contains(fr.lirmm.graphik.kb.core.IAtom
     * )
     */
    @Override
    public boolean contains(Atom atom) throws AtomSetException {
        return this.atoms.contains(atom);
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.lirmm.graphik.kb.core.IAtomSet#getTerms()
     */
    @Override
    public TreeSet<Term> getTerms() {
        return new TreeSet<Term>(this.terms);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.lirmm.graphik.kb.core.IAtomSet#getTerms(fr.lirmm.graphik.kb.core.ITerm
     * .Type)
     */
    @Override
    public Set<Term> getTerms(Type type) {
        TreeSet<Term> set = new TreeSet<Term>();
        for (Term t : this.terms)
            if (type.equals(t.getType()))
                set.add(t);

        return set;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.lirmm.graphik.alaska.store.IWriteableStore#write(fr.lirmm.graphik.
     * kb.stream.IAtomReader)
     */
    @Override
    public void addAll(Iterable<Atom> atoms) throws AtomSetException {
        for (Atom a : atoms) {
            this.add(a);
        }
    }

    /**
     * @see fr.lirmm.graphik.alaska.store.Store#write(fr.lirmm.graphik.kb.core.IAtom)
     */
    @Override
    public boolean add(Atom atom) {
        List<TermVertex> atomTerms = new LinkedList<TermVertex>();
        PredicateVertex atomPredicate;

        for (Term t : atom.getTerms())
            atomTerms.add(this.addTermVertex(new TermVertex(t)));

        atomPredicate = this.addPredicateVertex(new PredicateVertex(atom
                .getPredicate()));
        AtomEdge atomEdge = new AtomEdge(atomPredicate, atomTerms);
        return this.addAtomEdge(atomEdge);
    }

    // /////////////////////////////////////////////////////////////////////////
    // PRIVATE METHODS
    // /////////////////////////////////////////////////////////////////////////

    TermVertex getTermVertex(Term term) {
        TermVertex t = this.terms.tailSet(new TermVertex(term)).first();
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

	/* (non-Javadoc)
	 * @see fr.lirmm.graphik.kb.core.AtomSet#getAllPredicate()
	 */
	@Override
	public ObjectReader<Predicate> getAllPredicate() throws AtomSetException {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

}
