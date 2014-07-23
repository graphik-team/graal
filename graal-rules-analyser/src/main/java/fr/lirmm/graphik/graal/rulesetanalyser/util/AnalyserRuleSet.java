/**
 * 
 */
package fr.lirmm.graphik.graal.rulesetanalyser.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import fr.lirmm.graphik.graal.core.ImmutableRuleSet;
import fr.lirmm.graphik.graal.core.LinkedListRuleSet;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.rulesetanalyser.graph.AffectedPositionSet;
import fr.lirmm.graphik.graal.rulesetanalyser.graph.GraphPositionDependencies;
import fr.lirmm.graphik.graal.rulesetanalyser.graph.MarkedVariableSet;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class AnalyserRuleSet implements ImmutableRuleSet {
	
	Collection<Rule> ruleset;
	GraphPositionDependencies grd;
	AffectedPositionSet affectedPositionSet;
	GraphPositionDependencies graphPositionDependencies;
	MarkedVariableSet markedVariableSet;
	
	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////
	
	public AnalyserRuleSet(Iterable<Rule> rules) {
		this.ruleset = Collections.unmodifiableCollection(new LinkedListRuleSet(rules));
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// GETTERS
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * @return the grd
	 */
	public GraphPositionDependencies getGrd() {
		if(this.grd == null)
			this.computeGRD();
		
		return grd;
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
		// TODO implement this method
		throw new Error("This method isn't implemented");
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
