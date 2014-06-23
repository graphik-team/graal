/**
 * 
 */
package fr.lirmm.graphik.kb.atomset.graph;

import java.util.Set;
import java.util.TreeSet;

import fr.lirmm.graphik.kb.core.Term;

public class TermVertex extends Term implements Vertex {

	private static final long serialVersionUID = -1087277093687686210L;
	private final TreeSet<Edge> edges = new TreeSet<Edge>();

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * @param t
	 */
	public TermVertex(Term t) {
		super(t.getValue(), t.getType());
	}

	/**
	 * 
	 * @param label
	 * @param type
	 */
	public TermVertex(String label, Type type) {
		super(label, type);
	}

	// /////////////////////////////////////////////////////////////////////////
	// VERTEX METHODS
	// /////////////////////////////////////////////////////////////////////////

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.lirmm.graphik.alaska.store.graph.Vertex#getEdges()
	 */
	@Override
	public Set<Edge> getEdges() {
		return this.edges;
	}
	
    @Override
    public boolean equals(Object o) {
    	return super.equals(o);
    }
    
    @Override 
    public int hashCode() {
    	return super.hashCode();
    }

}
