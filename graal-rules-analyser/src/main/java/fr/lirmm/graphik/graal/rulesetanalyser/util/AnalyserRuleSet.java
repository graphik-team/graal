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
package fr.lirmm.graphik.graal.rulesetanalyser.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import fr.lirmm.graphik.graal.api.core.ImmutableRuleSet;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.RuleLabeler;
import fr.lirmm.graphik.graal.api.core.RuleSetException;
import fr.lirmm.graphik.graal.api.core.unifier.DependencyChecker;
import fr.lirmm.graphik.graal.core.DefaultRuleLabeler;
import fr.lirmm.graphik.graal.core.grd.DefaultGraphOfRuleDependencies;
import fr.lirmm.graphik.graal.core.ruleset.LinkedListRuleSet;
import fr.lirmm.graphik.graal.core.unifier.checker.AtomErasingChecker;
import fr.lirmm.graphik.graal.core.unifier.checker.ProductivityChecker;
import fr.lirmm.graphik.graal.rulesetanalyser.graph.AffectedPositionSet;
import fr.lirmm.graphik.graal.rulesetanalyser.graph.GraphPositionDependencies;
import fr.lirmm.graphik.graal.rulesetanalyser.graph.JointlyAffectedPositionSet;
import fr.lirmm.graphik.graal.rulesetanalyser.graph.MarkedVariableSet;
import fr.lirmm.graphik.util.graph.scc.StronglyConnectedComponentsGraph;
import fr.lirmm.graphik.util.stream.CloseableIterator;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class AnalyserRuleSet implements ImmutableRuleSet {

	private Collection<Rule> ruleset;
	private DefaultGraphOfRuleDependencies grd;
	private AffectedPositionSet affectedPositionSet;
	private JointlyAffectedPositionSet jointlyAffectedPositionSet;
	private GraphPositionDependencies graphPositionDependencies;
	private MarkedVariableSet markedVariableSet;
	private StronglyConnectedComponentsGraph<Rule> sccGraph;
	private List<AnalyserRuleSet> scc;
	private List<DependencyChecker> dependencyCheckerList;
	private boolean withUnifiers = false;
	private RuleLabeler labeler = new DefaultRuleLabeler();
	
	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////
	
	public AnalyserRuleSet(Rule rule) {
		LinkedListRuleSet list = new LinkedListRuleSet();
		// do not set the label of the rule when the rule set is a singleton. 
		// This constructor is used internally and then would override the previously defined label. 
		list.add(rule);
		this.ruleset = Collections.unmodifiableCollection(list);
		this.dependencyCheckerList = new LinkedList<DependencyChecker>();
		this.dependencyCheckerList.add(ProductivityChecker.instance());
		this.dependencyCheckerList.add(AtomErasingChecker.instance());

	}

	public AnalyserRuleSet(Iterable<Rule> rules) {
		this(rules.iterator());
	}
	
	public AnalyserRuleSet(Iterable<Rule> rules, DependencyChecker checker) {
		this(rules.iterator(), checker);
	}
	
	public AnalyserRuleSet(Iterator<Rule> rules) {
		this.ruleset = Collections.unmodifiableCollection(new LinkedListRuleSet(rules));
		setRuleLabels();
		this.dependencyCheckerList = new LinkedList<DependencyChecker>();
		this.dependencyCheckerList.add(ProductivityChecker.instance());
		this.dependencyCheckerList.add(AtomErasingChecker.instance());

	}

	public AnalyserRuleSet(Iterator<Rule> rules, DependencyChecker... checkers) {
		this.ruleset = Collections.unmodifiableCollection(new LinkedListRuleSet(rules));
		setRuleLabels();
		this.dependencyCheckerList = Arrays.asList(checkers);
	}
	
	public AnalyserRuleSet(CloseableIterator<Rule> rules) throws RuleSetException {
		this.ruleset = Collections.unmodifiableCollection(new LinkedListRuleSet(rules));
		setRuleLabels();
		this.dependencyCheckerList = new LinkedList<DependencyChecker>();
		this.dependencyCheckerList.add(ProductivityChecker.instance());
		this.dependencyCheckerList.add(AtomErasingChecker.instance());

	}

	public AnalyserRuleSet(CloseableIterator<Rule> rules, DependencyChecker... checkers) throws RuleSetException {
		this.ruleset = Collections.unmodifiableCollection(new LinkedListRuleSet(rules));
		setRuleLabels();
		this.dependencyCheckerList = Arrays.asList(checkers);
	}
	
	public AnalyserRuleSet(DefaultGraphOfRuleDependencies grd) {
		Collection<Rule> c = new LinkedList<Rule>();
		for(Rule r : grd.getRules()) {
			this.labeler.setLabel(r);
			c.add(r);
		}
		this.ruleset = Collections.unmodifiableCollection(c);
		this.grd = grd;
	}
	
	private final void setRuleLabels() {
		for(Rule r : this.ruleset) {
			this.labeler.setLabel(r);
		}
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// GETTERS
	// /////////////////////////////////////////////////////////////////////////

	public void addDependencyChecker(DependencyChecker checker) {
		this.dependencyCheckerList.add(checker);
	}
	
	public void removeDependencyChecker(DependencyChecker checker) {
		this.dependencyCheckerList.remove(checker);
	}
	
	public void clearDependencyChecker() {
		this.dependencyCheckerList.clear();
	}
	
	public void enableUnifiers(boolean wu) {
		this.withUnifiers = wu;
	}
	/**
	 * @return the grd
	 */
	public DefaultGraphOfRuleDependencies getGraphOfRuleDependencies() {
		if(this.grd == null)
			this.computeGRD();
		
		return this.grd;
	}
	
	/**
	 * @param grd
	 */
	public void setGraphOfRuleDependencies(DefaultGraphOfRuleDependencies grd) {
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
	 * @return the jointlyAffectedPositionSet
	 */
	public JointlyAffectedPositionSet getJointlyAffectedPositionSet() {
		if(this.jointlyAffectedPositionSet == null)
			this.computeJointlyAffectedPositionSet();
		
		return this.jointlyAffectedPositionSet;
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
		this.grd = new DefaultGraphOfRuleDependencies(ruleset, this.withUnifiers, this.dependencyCheckerList.toArray(new DependencyChecker[this.dependencyCheckerList.size()]));
	}

	private void computeSCC() {
		this.scc = new LinkedList<AnalyserRuleSet>();
		for (int s : this.getStronglyConnectedComponentsGraph().vertexSet())
			this.scc.add(this.getSubRuleSetAnalyser(this.getStronglyConnectedComponentsGraph().getComponent(s)));
	}
	
	private void computeAffectedPositionSet() {
		this.affectedPositionSet = new AffectedPositionSet(this);
	}
	
	private void computeJointlyAffectedPositionSet() {
		this.jointlyAffectedPositionSet = new JointlyAffectedPositionSet(this);
	}
	
	private void computeGraphPositionDependencies() {
		this.graphPositionDependencies = new GraphPositionDependencies(this);
	}
	
	private void computeMarkedVariableSet() {
		this.markedVariableSet = new MarkedVariableSet(this);
	}

	@Override
	public int size() {
		return this.ruleset.size();
	}

	@Override
	public boolean isEmpty() {
		return this.ruleset.isEmpty();
	}

}
