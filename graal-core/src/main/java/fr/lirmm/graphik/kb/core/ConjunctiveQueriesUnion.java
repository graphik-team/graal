/**
 * 
 */
package fr.lirmm.graphik.kb.core;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;


/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class ConjunctiveQueriesUnion implements Query, Collection<ConjunctiveQuery> {

    private Collection<ConjunctiveQuery> queries;
    
    // /////////////////////////////////////////////////////////////////////////
    //	CONSTRUCTORS
    // /////////////////////////////////////////////////////////////////////////

    public ConjunctiveQueriesUnion() {
        this.queries = new LinkedList<ConjunctiveQuery>();
    }
    
    public ConjunctiveQueriesUnion(Collection<ConjunctiveQuery> queries) {
        this.queries = queries;
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
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}
    
}
