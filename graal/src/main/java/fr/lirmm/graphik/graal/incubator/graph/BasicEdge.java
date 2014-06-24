/**
 * 
 */
package fr.lirmm.graphik.graal.incubator.graph;

import java.util.Collection;
import java.util.LinkedList;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class BasicEdge<T> implements Edge<T> {

	private T object;
	private Collection<Vertex> vertices = new LinkedList<Vertex>();

	/* (non-Javadoc)
	 * @see fr.lirmm.graphik.alaska.incubator.graph.Edge#setObject(java.lang.Object)
	 */
	@Override
	public void setObject(T object) {
		this.object = object;
	}

	/* (non-Javadoc)
	 * @see fr.lirmm.graphik.alaska.incubator.graph.Edge#getObject()
	 */
	@Override
	public T getObject() {
		return this.object;
	}

	/* (non-Javadoc)
	 * @see fr.lirmm.graphik.alaska.incubator.graph.Edge#getVertices()
	 */
	@Override
	public Collection<Vertex> getVertices() {
		return this.vertices;
	}

}
