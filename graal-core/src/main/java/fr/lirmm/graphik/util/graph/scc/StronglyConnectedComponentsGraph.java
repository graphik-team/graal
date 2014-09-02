/**
 * 
 */
package fr.lirmm.graphik.util.graph.scc;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.graph.model.DirectedMutableGraph;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
public class StronglyConnectedComponentsGraph<V> extends
		DirectedMutableGraph<Integer, Integer> {

	private static final long serialVersionUID = -2816870306827502776L;

	private int edgeMaxIndex = -1;
	private Map<Integer, Set<V>> map = new TreeMap<Integer, Set<V>>();

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	public void addEdge(int tail, int head) {
		this.addEdge(tail, ++edgeMaxIndex, head);
	}

	public void addToComponent(int vertex, V v) {
		Set<V> set = this.map.get(vertex);
		if (set == null) {
			set = new TreeSet<V>();
			this.map.put(vertex, set);
		}
		set.add(v);
	}

	public Set<V> getComponent(int vertex) {
		return this.map.get(vertex);
	}

	public Set<Integer> getSources() {
		Set<Integer> sources = new TreeSet<Integer>();
		for (Integer i : this.getVertices()) {
			Iterator<Integer> it = this.getInbound(i).iterator();
			if (!it.hasNext()) {
				sources.add(i);
			}
		}
		return sources;
	}

	public Set<Integer> getSinks() {
		Set<Integer> sinks = new TreeSet<Integer>();
		for (Integer i : this.getVertices()) {
			Iterator<Integer> it = this.getOutbound(i).iterator();
			if (!it.hasNext()) {
				sinks.add(i);
			}
		}
		return sinks;
	}

	public int getNbrComponents() {
		return this.getAdjacencyList().size();
	}
	
	/**
	 * @param scc
	 * @return
	 */
	public int[] computeLayers() {
		Iterable<Integer> firstLayer = this.getSources();
		int size = 0;
		Iterator<Integer> it = this.getVertices().iterator();
		while (it.hasNext()) {
			it.next();
			++size;
		}
		
		int[] layers = new int[size];
		//init
		for(int i = 0; i < size; ++i) {
			layers[i] = -1;
		}
		for(int i : firstLayer) {
			layers[i] = 0;
			layers = computeLayersRec( i, layers, 1);
		}
		return layers;
	}
	
	private int[] computeLayersRec(int v, int[] layers, int actualLayer) {
		for(int succ : this.getOutbound(v)) {
			if(layers[succ] < actualLayer && v != succ) {
				layers[succ] = actualLayer;
				layers = computeLayersRec(succ, layers, actualLayer + 1);
			}
		}
		return layers;
	}

}
