/* Graal v0.7.4
 * Copyright (c) 2014-2015 Inria Sophia Antipolis - Méditerranée / LIRMM (Université de Montpellier & CNRS)
 * All rights reserved.
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * Author(s): Clément SIPIETER
 *            Mélanie KÖNIG
 *            Swan ROCHER
 *            Jean-François BAGET
 *            Michel LECLÈRE
 *            Marie-Laure MUGNIER
 */
 /**
 * 
 */
package fr.lirmm.graphik.graal.core.atomset.graph;

import java.util.Set;
import java.util.TreeSet;

import fr.lirmm.graphik.graal.core.Predicate;


class PredicateVertex extends Predicate implements Vertex {

	private static final long serialVersionUID = 1607321754413212182L;
	private Set<Edge> edges = new TreeSet<Edge>();

    // /////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // /////////////////////////////////////////////////////////////////////////

    /**
     * @param predicate
     */
    public PredicateVertex(Predicate predicate) {
       super(predicate.getIdentifier(), predicate.getArity());
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
