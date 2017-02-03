/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2017)
 *
 * Contributors :
 *
 * Clément SIPIETER <clement.sipieter@inria.fr>
 * Mélanie KÖNIG
 * Swan ROCHER
 * Jean-François BAGET
 * Michel LECLÈRE
 * Marie-Laure MUGNIER <mugnier@lirmm.fr>
 *
 *
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
 /**
 * 
 */
package fr.lirmm.graphik.util.graph.scc;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.StrongConnectivityInspector;
import org.jgrapht.graph.DefaultDirectedGraph;

/**
 * The StronglyConnectedComponentsGraph represents a graph of strongly connected
 * components of an other graph.
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
public class StronglyConnectedComponentsGraph<V> extends
		DefaultDirectedGraph<Integer, Integer> {

	private static final long serialVersionUID = -2816870306827502776L;

	private int edgeMaxIndex = -1;
	private final Map<Integer, Set<V>> map = new TreeMap<Integer, Set<V>>();
	
	public StronglyConnectedComponentsGraph() {
		super(Integer.class);
	}

	/**
	 * Construct the StronglyConnectedComponentsGraph of the specified graph
	 * 
	 * @param graph
	 */
	public <E> StronglyConnectedComponentsGraph(DirectedGraph<V, E> graph) {
		this();
		List<Set<V>> stronglyConnectedSets = new StrongConnectivityInspector<V, E>(graph).stronglyConnectedSets();

		// add components
		int componentIndex = -1;
		for (Set<V> component : stronglyConnectedSets) {
			++componentIndex;
			this.addComponent(componentIndex, component);
		}

		// construct the graph
		for (int src : this.vertexSet()) {
			for (int target : this.vertexSet()) {
				if (src != target) {
					for (V s : this.getComponent(src)) {
						for (V t : this.getComponent(target)) {
							if (graph.getEdge(s, t) != null) {
								this.addEdge(src, target);
								break;
							}
						}
						if (this.getEdge(src, target) != null)
							break;
					}
				}
				// Actually we prefere to not have these edges so
				// that getSources work as we want it to work...
				/*else if (this.getComponent(src).size() > 1)
					this.addEdge(src,src);*/
			}
		}
	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	public void addEdge(int tail, int head) {
		this.addEdge(tail, head, ++edgeMaxIndex);
	}

	public void addComponent(int vertex, Set<V> vertices) {
		this.addVertex(vertex);
		this.map.put(vertex, vertices);
	}

	public Set<V> getComponent(int vertex) {
		return this.map.get(vertex);
	}

	public Set<Integer> getSources() {
		Set<Integer> sources = new TreeSet<Integer>();
		for (Integer i : this.vertexSet()) {
			Iterator<Integer> it = this.incomingEdgesOf(i).iterator();
			if (!it.hasNext()) {
				sources.add(i);
			}
		}
		return sources;
	}

	public Set<Integer> getSinks() {
		Set<Integer> sinks = new TreeSet<Integer>();
		for (Integer i : this.vertexSet()) {
			Iterator<Integer> it = this.outgoingEdgesOf(i).iterator();
			if (!it.hasNext()) {
				sinks.add(i);
			}
		}
		return sinks;
	}

	public int getNbrComponents() {
		return this.vertexSet().size();
	}

	/**
	 * @param sources
	 * @param direction
	 *            if true, following the direction of the edges, otherwise
	 *            follows the reverse direction.
	 * @return an array of int containing the layer number of each components of this graph.
	 */
	public int[] computeLayers(Iterable<Integer> sources,
			boolean direction) {
		Iterable<Integer> firstLayer = sources;
		int size = 0;
		Iterator<Integer> it = this.vertexSet().iterator();
		while (it.hasNext()) {
			it.next();
			++size;
		}

		int[] layers = new int[size];
		// init
		for (int i = 0; i < size; ++i) {
			layers[i] = -1;
		}
		for (int i : firstLayer) {
			layers[i] = 0;
			computeLayersRec(i, layers, 1, direction);
		}
		return layers;
	}

	private void computeLayersRec(int v, int[] layers, int actualLayer, boolean direction) {
		Iterable<Integer> it = null;
		if(direction)
			it = this.outgoingEdgesOf(v);
		else
			it = this.incomingEdgesOf(v);
		
		for (int s : it) {
			int succ;
			if (direction) succ = this.getEdgeTarget(s);
			else succ = this.getEdgeSource(s);
			if (layers[succ] < actualLayer && v != succ) {
				layers[succ] = actualLayer;
				computeLayersRec(succ, layers, actualLayer + 1, direction);
			}
		}
	}

}
