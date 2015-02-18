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
	private final Map<Integer, Set<V>> map = new TreeMap<Integer, Set<V>>();

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
	 * @param direction
	 *            if true, following the direction of the edges, otherwise
	 *            follows the reverse direction.
	 * @return
	 */
	public int[] computeLayers(Iterable<Integer> sources,
			boolean direction) {
		Iterable<Integer> firstLayer = sources;
		int size = 0;
		Iterator<Integer> it = this.getVertices().iterator();
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
			it = this.getOutbound(v);
		else
			it = this.getInbound(v);
		
		for (int succ : it) {
			if (layers[succ] < actualLayer && v != succ) {
				layers[succ] = actualLayer;
				computeLayersRec(succ, layers, actualLayer + 1, direction);
			}
		}
	}

}
