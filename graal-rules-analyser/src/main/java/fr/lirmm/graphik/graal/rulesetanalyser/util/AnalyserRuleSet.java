/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2016)
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
package fr.lirmm.graphik.graal.rulesetanalyser.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.RuleSet;
import fr.lirmm.graphik.graal.core.ruleset.LinkedListRuleSet;
import fr.lirmm.graphik.graal.grd.AtomErasingFilter;
import fr.lirmm.graphik.graal.grd.GraphOfRuleDependencies;
import fr.lirmm.graphik.graal.rulesetanalyser.graph.AffectedPositionSet;
import fr.lirmm.graphik.graal.rulesetanalyser.graph.GraphPositionDependencies;
import fr.lirmm.graphik.graal.rulesetanalyser.graph.MarkedVariableSet;
import fr.lirmm.graphik.util.MethodNotImplementedError;
import fr.lirmm.graphik.util.graph.scc.StronglyConnectedComponentsGraph;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class AnalyserRuleSet implements RuleSet {

	private Collection<Rule> ruleset;
	private GraphOfRuleDependencies grd;
	private AffectedPositionSet affectedPositionSet;
	private GraphPositionDependencies graphPositionDependencies;
	private MarkedVariableSet markedVariableSet;
	private StronglyConnectedComponentsGraph<Rule> sccGraph;
	private List<AnalyserRuleSet> scc;
	private GraphOfRuleDependencies.DependencyChecker dependencyChecker;
	private boolean withUnifiers = false;
	
	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////
	
	public AnalyserRuleSet(Rule rule) {
		LinkedListRuleSet list = new LinkedListRuleSet();
		list.add(rule);
		this.ruleset = Collections.unmodifiableCollection(list);
		this.dependencyChecker = new AtomErasingFilter();
	}

	public AnalyserRuleSet(Iterable<Rule> rules) {
		this.ruleset = Collections.unmodifiableCollection(new LinkedListRuleSet(rules));
		this.dependencyChecker = new AtomErasingFilter();
	}

	public AnalyserRuleSet(Iterable<Rule> rules, GraphOfRuleDependencies.DependencyChecker checker) {
		this.ruleset = Collections.unmodifiableCollection(new LinkedListRuleSet(rules));
		this.dependencyChecker = checker;
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

	public void setDependencyChecker(GraphOfRuleDependencies.DependencyChecker c) {
		this.dependencyChecker = c;
	}
	public void enableUnifiers(boolean wu) {
		this.withUnifiers = wu;
	}
	/**
	 * @return the grd
	 */
	public GraphOfRuleDependencies getGraphOfRuleDependencies() {
		if(this.grd == null)
			this.computeGRD();
		
		return this.grd;
	}
	
	/**
	 * @param grd
	 */
	public void setGraphOfRuleDependencies(GraphOfRuleDependencies grd) {
		this.grd = grd;
		this.sccGraph = null;
	}

	/**
	 * @return the affectedPositionSet
	 */
	public AffectedPositionSet getAffectedPositionSet() {
		if(this.affectedPositionSet == null)
			this.computeAffectedPositionSet();
		
		return this.affectedPositionSet;
	}
	
	/**
	 * @return the graphPositionDependencies
	 */
	public GraphPositionDependencies getGraphPositionDependencies() {
		if(this.graphPositionDependencies == null)
			this.computeGraphPositionDependencies();
		
		return this.graphPositionDependencies;
	}
	
	/**
	 * @return the markedVariableSet
	 */
	public MarkedVariableSet getMarkedVariableSet() {
		if(this.markedVariableSet == null)
			this.computeMarkedVariableSet();
		
		return this.markedVariableSet;
	}
	
	public StronglyConnectedComponentsGraph<Rule> getStronglyConnectedComponentsGraph() {
		if (this.sccGraph == null)
			this.sccGraph =  this.getGraphOfRuleDependencies().getStronglyConnectedComponentsGraph();
		return this.sccGraph;
	}
	
	public AnalyserRuleSet getSubRuleSetAnalyser(Iterable<Rule> rules) {
		return new AnalyserRuleSet(this.getGraphOfRuleDependencies().getSubGraph(rules));
	}

	public List<AnalyserRuleSet> getSCC() {
		if (this.scc == null) {
			computeSCC();
		}
		return this.scc;
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	@Override
	public boolean contains(Rule rule) {
		return this.ruleset.contains(rule);
	}

	@Override
	public Iterator<Rule> iterator() {
		return this.ruleset.iterator();
	}
	
	private void computeGRD() {
		this.grd = new GraphOfRuleDependencies(ruleset, this.withUnifiers, this.dependencyChecker);
	}

	private void computeSCC() {
		this.scc = new LinkedList<AnalyserRuleSet>();
		for (int s : this.getStronglyConnectedComponentsGraph().vertexSet())
			this.scc.add(this.getSubRuleSetAnalyser(this.getStronglyConnectedComponentsGraph().getComponent(s)));
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

	@Override
	public boolean add(Rule rule) {
		// TODO implement this method
		throw new MethodNotImplementedError();
	}

	@Override
	public boolean addAll(Iterator<Rule> ruleIterator) {
		// TODO implement this method
		throw new MethodNotImplementedError();
	}

	@Override
	public boolean remove(Rule rule) {
		// TODO implement this method
		throw new MethodNotImplementedError();
	}

	@Override
	public boolean removeAll(Iterator<Rule> ruleIterator) {
		// TODO implement this method
		throw new MethodNotImplementedError();
	}

	@Override
	public int size() {
		return this.ruleset.size();
	}

}
