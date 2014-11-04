/**
 * 
 */
package fr.lirmm.graphik.graal.rulesetanalyser;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;

import fr.lirmm.graphik.graal.rulesetanalyser.property.RuleProperty;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class RuleHierarchyGraph {

	private DirectedGraph<String, DefaultEdge> graph;
	private Map<String, RuleProperty> index;
	
	public RuleHierarchyGraph() {
		this.graph = new SimpleDirectedGraph<String, DefaultEdge>(DefaultEdge.class);
		this.index = new TreeMap<String, RuleProperty>();
	}

	/**
	 * @param label
	 * @param parentLabel
	 */
	public void addParent(String label, String parentLabel) {
		this.graph.addEdge(label, parentLabel);
	}

	/**
	 * @param property
	 */
	public void add(RuleProperty property) {
		this.index.put(property.getLabel(), property);
		this.graph.addVertex(property.getLabel());
	}
	
	/**
	 * Return a Collection of sources of this graph.
	 * @return
	 */
	public Collection<RuleProperty> getSources() {
		Collection<RuleProperty> list = new LinkedList<RuleProperty>();
		for(String vertex : this.graph.vertexSet()) {
			if(this.graph.inDegreeOf(vertex) == 0) {
				list.add(this.index.get(vertex));
			}
		}
		return list;
	}
	
	
}
