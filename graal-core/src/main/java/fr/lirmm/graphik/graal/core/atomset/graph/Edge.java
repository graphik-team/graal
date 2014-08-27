/**
 * 
 */
package fr.lirmm.graphik.graal.core.atomset.graph;

import java.util.Set;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
interface Edge {

    abstract public Set<Vertex> getVertices();

}
