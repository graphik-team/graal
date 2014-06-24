/**
 * 
 */
package fr.lirmm.graphik.graal.incubator.graph;

import java.util.Collection;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public interface Vertex<T> {

	void setObject(T object);
	T getObject();
	
	Collection<Edge> getEdges();
}
