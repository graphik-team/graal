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
		List<Set<V>> stronglyConnectedSets = new StrongConnectivityInspector<V, E>(
				graph).stronglyConnectedSets();

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
			}
		}
	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	public void addEdge(int tail, int head) {
		this.addEdge(tail, head, ++edgeMaxIndex);
	}

	public void addToComponent(int vertex, V v) {
		Set<V> set = this.map.get(vertex);
		if (set == null) {
			set = new TreeSet<V>();
			this.map.put(vertex, set);
		}
		set.add(v);
	}

	public void addComponent(int vertex, Set<V> vertices) {
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
	 * @param scc
	 * @param direction
	 *            if true, following the direction of the edges, otherwise
	 *            follows the reverse direction.
	 * @return
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
		
		for (int succ : it) {
			if (layers[succ] < actualLayer && v != succ) {
				layers[succ] = actualLayer;
				computeLayersRec(succ, layers, actualLayer + 1, direction);
			}
		}
	}

}
