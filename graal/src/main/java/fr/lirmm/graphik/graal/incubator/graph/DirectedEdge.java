/**
 * 
 */
package fr.lirmm.graphik.graal.incubator.graph;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public interface DirectedEdge<T> extends Edge<T> {

	Vertex getSource();
	Vertex getDestination();
}
