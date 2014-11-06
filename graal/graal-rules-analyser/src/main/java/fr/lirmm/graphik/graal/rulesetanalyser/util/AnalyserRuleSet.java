/**
 * 
 */
package fr.lirmm.graphik.graal.rulesetanalyser.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.ruleset.ImmutableRuleSet;
import fr.lirmm.graphik.graal.core.ruleset.LinkedListRuleSet;
import fr.lirmm.graphik.graal.grd.GraphOfRuleDependencies;
import fr.lirmm.graphik.graal.rulesetanalyser.graph.AffectedPositionSet;
import fr.lirmm.graphik.graal.rulesetanalyser.graph.GraphPositionDependencies;
import fr.lirmm.graphik.graal.rulesetanalyser.graph.MarkedVariableSet;
import fr.lirmm.graphik.util.graph.scc.StronglyConnectedComponentsGraph;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class AnalyserRuleSet implements ImmutableRuleSet {
	
	Collection<Rule> ruleset;
	GraphOfRuleDependencies grd;
	AffectedPositionSet affectedPositionSet;
	GraphPositionDependencies graphPositionDependencies;
	MarkedVariableSet markedVariableSet;
	
	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////
	
	public AnalyserRuleSet(Iterable<Rule> rules) {
		this.ruleset = Collections.unmodifiableCollection(new LinkedListRuleSet(rules));
	}
	
	public AnalyserRuleSet(GraphOfRuleDependencies grd) {
		Collection<Rule> c = new LinkedList<Rule>();
		for(Rule r : grd.getRules()) {
			c.add(r);
		}
		this.ruleset = Collections.unmodifiableCollection(c);
		this.grd = grd;
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// GETTERS
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * @return the grd
	 */
	public GraphOfRuleDependencies getGraphOfRuleDependencies() {
		if(this.grd == null)
			this.computeGRD();
		
		return grd;
	}
	
	/**
	 * @param grd
	 */
	public void setGraphOfRuleDependencies(GraphOfRuleDependencies grd) {
		this.grd = grd;
	}

	/**
	 * @return the affectedPositionSet
	 */
	public AffectedPositionSet getAffectedPositionSet() {
		if(this.affectedPositionSet == null)
			this.computeAffectedPositionSet();
		
		return affectedPositionSet;
	}
	
	/**
	 * @return the graphPositionDependencies
	 */
	public GraphPositionDependencies getGraphPositionDependencies() {
		if(this.graphPositionDependencies == null)
			this.computeGraphPositionDependencies();
		
		return graphPositionDependencies;
	}
	
	/**
	 * @return the markedVariableSet
	 */
	public MarkedVariableSet getMarkedVariableSet() {
		if(this.markedVariableSet == null)
			this.computeMarkedVariableSet();
		
		return markedVariableSet;
	}
	
	public StronglyConnectedComponentsGraph<Rule> getStronglyConnectedComponentsGraph() {
		return this.getGraphOfRuleDependencies().getStronglyConnectedComponentsGraph();
	}
	
	public AnalyserRuleSet getSubRuleSetAnalyser(Iterable<Rule> rules) {
		return new AnalyserRuleSet(this.getGraphOfRuleDependencies().getSubGraph(rules));
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	@Override
	public boolean contains(Rule rule) {
		return ruleset.contains(rule);
	}

	@Override
	public Iterator<Rule> iterator() {
		return ruleset.iterator();
	}
	
	private void computeGRD() {
		this.grd = new GraphOfRuleDependencies(ruleset);
	}
	
	private void computeAffectedPositionSet() {
		this.affectedPositionSet = new AffectedPositionSet(this);
	}
	
	private void computeGraphPositionDependencies() {
		this.graphPositionDependencies = new GraphPositionDependencies(this);
	}
	
	private void computeMarkedVariableSet() {
		this.markedVariableSet = new MarkedVariableSet(this);
	}


}
