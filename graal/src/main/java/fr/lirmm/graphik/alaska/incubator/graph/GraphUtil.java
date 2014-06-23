/**
 * 
 */
package fr.lirmm.graphik.alaska.incubator.graph;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class GraphUtil<V,E> {
	
	public Edge<E> addEdge(E edgeObject, Vertex<V> v1, Vertex<V> v2) {
		Edge<E> edge = new BasicEdge<E>();
		edge.setObject(edgeObject);
		edge.getVertices().add(v1);
		edge.getVertices().add(v2);
		
		v1.getEdges().add(edge);
		v2.getEdges().add(edge);

		return edge;
	}
	
	public Edge<E> addDirectedEdge(E edgeObject, Vertex<V> v1, Vertex<V> v2) {
		Edge<E> edge = new BasicDirectedEdge<E>(edgeObject, v1, v2);
		
		v1.getEdges().add(edge);
		v2.getEdges().add(edge);

		return edge;
	}
}
