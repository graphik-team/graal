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

	private String label = "";
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
    
    public UnionConjunctiveQueries(Iterator<ConjunctiveQuery> queries) {
        this.queries = new LinkedList<ConjunctiveQuery>();
        while(queries.hasNext()) {
            this.queries.add(queries.next());
        }
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

    @Override
    public boolean isEmpty() {
        return this.queries.isEmpty();
    }

    @Override
    public boolean remove(Object o) {
        return this.queries.remove(o);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return this.queries.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return this.queries.retainAll(c);
    }

    @Override
    public int size() {
        return this.queries.size();
    }

    @Override
    public Object[] toArray() {
        return this.queries.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return this.queries.toArray(a);
    }

    @Override
    public boolean contains(Object o) {
        return this.queries.contains(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return this.queries.containsAll(c);
    }

	@Override
	public boolean isBoolean() {
		return this.queries.isEmpty() || this.queries.iterator().next().isBoolean();
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public String getLabel() {
		return this.label;
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
