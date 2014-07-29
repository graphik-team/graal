package org.apache.commons.graph.scc;

/*                                                                              |                                                  
 * Licensed to the Apache Software Foundation (ASF) under one                   |                                                  
 * or more contributor license agreements.  See the NOTICE file                 |                                                  
 * distributed with this work for additional information                        |                                                  
 * regarding copyright ownership.  The ASF licenses this file                   |                                                  
 * to you under the Apache License, Version 2.0 (the                            |                                                  
 * "License"); you may not use this file except in compliance                   |                                                  
 * with the License.  You may obtain a copy of the License at                   |                                                  
 *                                                                              |                                                  
 *   http://www.apache.org/licenses/LICENSE-2.0                                 |                                                  
 *                                                                              |                                                  
 * Unless required by applicable law or agreed to in writing,                   |                                                  
 * software distributed under the License is distributed on an                  |                                                  
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY                       |                                                  
 * KIND, either express or implied.  See the License for the                    |                                                  
 * specific language governing permissions and limitations                      |                                                  
 * under the License.                                                           |                                                  
 */

import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

import org.apache.commons.graph.DirectedGraph;
import org.apache.commons.lang.mutable.MutableInt;

/**
 * Implements Tarjan's algorithm is a variation (slightly faster) on
 * KosarajuSharir's algorithm for finding strongly-connected components in a
 * directed graph.
 * 
 * @param <V> the Graph vertices type.
 */
public class TarjanAlgorithm2<V> 
{

    private final DirectedGraph<V, ?> graph;
    private StronglyConnectedComponentsGraph<V> sccGraph;
    private int sccMaxIndex = -1;
    
    /**
     * 
     */
    public TarjanAlgorithm2(DirectedGraph<V, ?> graph) 
    {
        this.graph = graph;
    }

    /**
     * @return the input graph strongly connected component.
     */
    public StronglyConnectedComponentsGraph<V> perform() 
    {
    	if(sccGraph == null) {
    		MutableInt currentIndex = new MutableInt(0);
	        this.sccGraph = new StronglyConnectedComponentsGraph<V>();
	        Map<V, Integer> index = new TreeMap<V, Integer>();
	        Map<V, Integer> lowLink = new TreeMap<V, Integer>();
	        Stack<V> stack = new Stack<V>();

	        for ( V v : this.graph.getVertices() ) 
	        {
	            if ( index.get( v ) == null ) 
	            {
	                strongConnect( v, currentIndex, index, lowLink, stack );
	            }
	        }
	        
	        constructGraph();

    	}
    	return sccGraph;
       
    }

	/**
     * @param v
     * @param index
     */
    private void strongConnect( V v, MutableInt currentIndex, Map<V, Integer> index,
            Map<V, Integer> lowLink, Stack<V> stack ) 
    {
        // Set the depth index for v to the smallest unused index
        index.put( v, currentIndex.toInteger() );
        lowLink.put( v, currentIndex.toInteger() );
        currentIndex.increment();
        stack.push( v );

        // Consider successors of v
        for ( V w : this.graph.getOutbound( v ) ) 
        {
            if ( index.get( w ) == null ) 
            {
                // Successor w has not yet been visited; recurse on it
                strongConnect( w, currentIndex, index, lowLink, stack );
                lowLink.put( v, Math.min( lowLink.get( v ), lowLink.get( w ) ) );
            } 
            else if ( stack.contains( w ) ) 
            {
                // Successor w is in stack S and hence in the current SCC
                lowLink.put( v, Math.min( lowLink.get( v ), index.get( w ) ) );
            }
        }

        // If v is a root node, pop the stack and generate an SCC
        if ( lowLink.get( v ).equals(index.get( v )) ) 
        {
        	int sccIndex = ++sccMaxIndex;
        	this.sccGraph.addVertex( sccIndex );
            V w;
            do {
                w = stack.pop();
                sccGraph.addToComponent( sccIndex, w );
            } while ( !w.equals( v ) );
        }
    }
    
    /**
	 * 
	 */
	private void constructGraph() {
		for(int src : this.sccGraph.getVertices()) {
			for(int target : this.sccGraph.getVertices()) {
				if(src != target) {
					for(V s : this.sccGraph.getComponent(src)) {
						for(V t : this.sccGraph.getComponent(target)) {
							if(this.graph.getEdge(s, t) != null) {
								this.sccGraph.addEdge(src, target);
								break;
							}
						}
						if(this.sccGraph.getEdge(src, target) != null)
							break;
					}
				}
			}
		}
	}
}
