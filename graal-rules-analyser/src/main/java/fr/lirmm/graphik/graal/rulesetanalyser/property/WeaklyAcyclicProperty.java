/**
 * 
 */
package fr.lirmm.graphik.graal.rulesetanalyser.property;

import java.util.LinkedList;
import java.util.List;

import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.rulesetanalyser.graph.GraphPositionDependencies;

/**
 * All predicate positions in the graph of position dependencies have finite
 * rank (i.e., there is no circuit with a special edge) (cf.
 * {@link GraphPositionDependencies}).
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * @author Swan Rocher
 * 
 */
public class WeaklyAcyclicProperty implements RuleProperty {

	private GraphPositionDependencies graph;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	private static WeaklyAcyclicProperty instance = null;

	private WeaklyAcyclicProperty() {
	}

	public static WeaklyAcyclicProperty getInstance() {
		if (instance == null) {
			instance = new WeaklyAcyclicProperty();
		}
		return instance;
	}

	public WeaklyAcyclicProperty(Iterable<Rule> rules) {
		this.graph = new GraphPositionDependencies(rules);
	}

	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.lirmm.graphik.graal.rulesetanalyser.UnitProperty#check(fr.lirmm.graphik
	 * .graal.core.Rule)
	 */
	@Override
	public boolean check(Rule rule) {
		List<Rule> rules = new LinkedList<Rule>();
		rules.add(rule);
		this.graph = new GraphPositionDependencies(rules);
		return this.graph.isWeaklyAcyclic();
	}

	public boolean check(Iterable<Rule> rules) {
		this.graph = new GraphPositionDependencies(rules);
		return this.graph.isWeaklyAcyclic();
	}

	public boolean check() {
		return this.graph.isWeaklyAcyclic();
	}

}
