/**
 * 
 */
package fr.lirmm.graphik.alaska.incubator.graph;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class BasicVertex<T> implements Vertex<T> {

	private T object;
	private Collection<Edge> edges = new LinkedList<Edge>();

	/* (non-Javadoc)
	 * @see fr.lirmm.graphik.alaska.incubator.graph.Vertex#setObject(java.lang.Object)
	 */
	@Override
	public void setObject(T object) {
		this.object = object;
	}

	/* (non-Javadoc)
	 * @see fr.lirmm.graphik.alaska.incubator.graph.Vertex#getObject()
	 */
	@Override
	public T getObject() {
		return this.object;
	}

	/* (non-Javadoc)
	 * @see fr.lirmm.graphik.alaska.incubator.graph.Vertex#getEdges()
	 */
	@Override
	public Collection<Edge> getEdges() {
		return this.edges ;
	}

	
	
}
