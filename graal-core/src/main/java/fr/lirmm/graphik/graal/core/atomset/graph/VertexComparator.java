/**
 * 
 */
package fr.lirmm.graphik.graal.core.atomset.graph;

import java.util.Comparator;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class VertexComparator implements Comparator<Vertex> {

	@Override
	public int compare(Vertex v1, Vertex v2) {
		return v1.toString().compareTo(v2.toString());
	}
	
}
