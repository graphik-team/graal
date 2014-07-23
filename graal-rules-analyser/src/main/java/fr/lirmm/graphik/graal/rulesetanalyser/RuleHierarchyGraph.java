/**
 * 
 */
package fr.lirmm.graphik.graal.rulesetanalyser;

import fr.lirmm.graphik.graal.rulesetanalyser.property.RuleProperty;
import grph.Grph;
import grph.in_memory.InMemoryGrph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import com.carrotsearch.hppc.cursors.IntCursor;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class RuleHierarchyGraph {

	private Grph graph;
	private ArrayList<RuleProperty> reverseIndex;
	private Map<String, Integer> index;
	
	public RuleHierarchyGraph() {
		this.graph = new InMemoryGrph();
		this.index = new TreeMap<String, Integer>();
		this.reverseIndex = new ArrayList<RuleProperty>();
	}

	/**
	 * @param label
	 * @param parentLabel
	 */
	public void addParent(String label, String parentLabel) {
		int index = this.index.get(label);
		int indexParent = this.index.get(parentLabel);
		this.graph.addDirectedSimpleEdge(index, indexParent);
	}

	/**
	 * @param property
	 */
	public void add(RuleProperty property) {
		int index = this.reverseIndex.size();
		this.index.put(property.getLabel(), index);
		this.reverseIndex.add(index, property);
		this.graph.addVertex(index);
	}
	
	public Collection<RuleProperty> getSources() {
		Collection<RuleProperty> list = new LinkedList<RuleProperty>();
		int i;
		for(IntCursor icursor : this.graph.getSources()) {
			i = icursor.value;
			list.add(this.reverseIndex.get(i));
		}
		return list;
	}
	
	
}
