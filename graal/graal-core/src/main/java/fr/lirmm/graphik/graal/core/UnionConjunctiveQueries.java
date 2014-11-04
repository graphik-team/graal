/**
 * 
 */
package fr.lirmm.graphik.graal.core;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;


/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class UnionConjunctiveQueries implements Query, Collection<ConjunctiveQuery> {

    private Collection<ConjunctiveQuery> queries;
    
    // /////////////////////////////////////////////////////////////////////////
    //	CONSTRUCTORS
    // /////////////////////////////////////////////////////////////////////////

    public UnionConjunctiveQueries() {
        this.queries = new LinkedList<ConjunctiveQuery>();
    }
    
    public UnionConjunctiveQueries(Collection<ConjunctiveQuery> queries) {
        this.queries = queries;
    }
    
    public UnionConjunctiveQueries(ConjunctiveQuery... queries) {
        this.queries = new LinkedList<ConjunctiveQuery>();
        for(ConjunctiveQuery cq : queries)
            this.queries.add(cq);
    }

    // /////////////////////////////////////////////////////////////////////////
    //	PUBLIC METHODS
    // /////////////////////////////////////////////////////////////////////////

    @Override
    public boolean add(ConjunctiveQuery cquery) {
        return this.queries.add(cquery);
    }
    
    @Override
    public boolean addAll(Collection<? extends ConjunctiveQuery> queries) {
        return this.queries.addAll(queries);
    }
    
    @Override
    public void clear() {
        this.queries.clear();
    }
    
    @Override
    public Iterator<ConjunctiveQuery> iterator() {
        return this.queries.iterator();
    }

    /* (non-Javadoc)
     * @see java.util.Collection#isEmpty()
     */
    @Override
    public boolean isEmpty() {
        return this.queries.isEmpty();
    }

    /* (non-Javadoc)
     * @see java.util.Collection#remove(java.lang.Object)
     */
    @Override
    public boolean remove(Object o) {
        return this.queries.remove(o);
    }

    /* (non-Javadoc)
     * @see java.util.Collection#removeAll(java.util.Collection)
     */
    @Override
    public boolean removeAll(Collection<?> c) {
        return this.queries.removeAll(c);
    }

    /* (non-Javadoc)
     * @see java.util.Collection#retainAll(java.util.Collection)
     */
    @Override
    public boolean retainAll(Collection<?> c) {
        return this.queries.retainAll(c);
    }

    /* (non-Javadoc)
     * @see java.util.Collection#size()
     */
    @Override
    public int size() {
        return this.queries.size();
    }

    /* (non-Javadoc)
     * @see java.util.Collection#toArray()
     */
    @Override
    public Object[] toArray() {
        return this.queries.toArray();
    }

    /* (non-Javadoc)
     * @see java.util.Collection#toArray(T[])
     */
    @Override
    public <T> T[] toArray(T[] a) {
        return this.queries.toArray(a);
    }

    /* (non-Javadoc)
     * @see java.util.Collection#contains(java.lang.Object)
     */
    @Override
    public boolean contains(Object o) {
        return this.queries.contains(o);
    }

    /* (non-Javadoc)
     * @see java.util.Collection#containsAll(java.util.Collection)
     */
    @Override
    public boolean containsAll(Collection<?> c) {
        return this.queries.containsAll(c);
    }

	/* (non-Javadoc)
	 * @see fr.lirmm.graphik.kb.query.IQuery#isBoolean()
	 */
	@Override
	public boolean isBoolean() {
		return this.queries.isEmpty() || this.queries.iterator().next().isBoolean();
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// OBJECT METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		for(Query q : this.queries) {
			s.append(q);
			s.append(" | ");
		}
		return s.toString();
	}
    
}
