/**
 * 
 */
package fr.lirmm.graphik.graal.incubator.graph;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author  Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class BasicDirectedEdge<T> implements DirectedEdge<T> {

	private T object;
	private Vertex[] vertices = new Vertex[2];
	
	//
	
	public BasicDirectedEdge(T object, Vertex v1, Vertex v2) {
		this.object = object;
		this.vertices[0] = v1;
		this.vertices[1] = v2;
	}
	//

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
		
		return Arrays.asList(this.vertices);
	}

	/* (non-Javadoc)
	 * @see fr.lirmm.graphik.alaska.incubator.graph.DirectedEdge#getSource()
	 */
	@Override
	public Vertex getSource() {
		return this.vertices[0];
	}

	/* (non-Javadoc)
	 * @see fr.lirmm.graphik.alaska.incubator.graph.DirectedEdge#getDestination()
	 */
	@Override
	public Vertex getDestination() {
		return this.vertices[1];
	}

}
