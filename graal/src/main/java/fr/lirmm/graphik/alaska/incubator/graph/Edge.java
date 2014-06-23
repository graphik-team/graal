/**
 * 
 */
package fr.lirmm.graphik.alaska.incubator.graph;

import java.util.Collection;


/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public interface Edge<T> {

	void setObject(T object);
	T getObject();
	
	Collection<Vertex> getVertices();
	
}
