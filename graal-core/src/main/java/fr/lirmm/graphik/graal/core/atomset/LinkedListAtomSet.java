package fr.lirmm.graphik.graal.core.atomset;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.AtomComparator;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.stream.IteratorAtomReader;
import fr.lirmm.graphik.util.stream.ObjectReader;

/**
 * 
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 */
public class LinkedListAtomSet extends AbstractReadOnlyAtomSet implements AtomSet, Collection<Atom> {

    private LinkedList<Atom> linkedList;

    // /////////////////////////////////////////////////////////////////////////
    //	CONSTRUCTORS
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
    
    // /////////////////////////////////////////////////////////////////////////
    //	PUBLIC METHODS
    // /////////////////////////////////////////////////////////////////////////

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
    public void add(Iterable<Atom> atoms) {
        for(Atom a : atoms)
            this.linkedList.add(a);
    }

    @Override
    public void remove(Iterable<Atom> atoms) {
        for(Atom a : atoms)
            this.linkedList.remove(a);
        
    }

    @Override
    public boolean remove(Atom atom) {
        return this.linkedList.remove(atom);
    }

    @Override
    public boolean contains(Atom atom) {
    	Comparator<Atom> cmp = new AtomComparator();
    	for(Atom a : this.linkedList) {
    		if(cmp.compare(atom, a) == 0)
    			return true;
    	}
        return false;
    }

    /* (non-Javadoc)
     * @see fr.lirmm.graphik.kb.IAtomSet#iterator()
     */
    @Override
    public ObjectReader<Atom> iterator() {
        return new IteratorAtomReader(this.linkedList.iterator());
    }

    /* (non-Javadoc)
     * @see fr.lirmm.graphik.kb.IWriteableAtomSet#add(fr.lirmm.graphik.kb.IAtom)
     */
    @Override
    public boolean add(Atom atom) {
        return this.linkedList.add(atom);
    }

    @Override
    public String toString() {        
       return this.linkedList.toString();
    }
    
    public boolean isEmpty() {
        return this.linkedList.isEmpty();
    }
    
    public int size() {
        return this.linkedList.size();
    }

    /**
     * @return
     */
    public Atom poll() {
        return this.linkedList.poll();
    }

	/* (non-Javadoc)
	 * @see fr.lirmm.graphik.kb.core.AtomSet#getAllPredicate()
	 */
	@Override
	public ObjectReader<Predicate> getAllPredicate() throws AtomSetException {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#addAll(java.util.Collection)
	 */
	@Override
	public boolean addAll(Collection<? extends Atom> c) {
		return this.linkedList.addAll(c);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#clear()
	 */
	@Override
	public void clear() {
		this.linkedList = new LinkedList<Atom>();
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(Object o) {
		return this.linkedList.contains(o);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#containsAll(java.util.Collection)
	 */
	@Override
	public boolean containsAll(Collection<?> c) {
		return this.linkedList.containsAll(c);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#remove(java.lang.Object)
	 */
	@Override
	public boolean remove(Object o) {
		return this.remove(o);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#removeAll(java.util.Collection)
	 */
	@Override
	public boolean removeAll(Collection<?> c) {
		return this.linkedList.removeAll(c);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#retainAll(java.util.Collection)
	 */
	@Override
	public boolean retainAll(Collection<?> c) {
		return this.linkedList.retainAll(c);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#toArray()
	 */
	@Override
	public Object[] toArray() {
		return this.linkedList.toArray();
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#toArray(T[])
	 */
	@Override
	public <T> T[] toArray(T[] t) {
		return this.linkedList.toArray(t);
	}

};
