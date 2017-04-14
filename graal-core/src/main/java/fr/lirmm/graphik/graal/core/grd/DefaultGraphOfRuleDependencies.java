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
package fr.lirmm.graphik.graal.core.grd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.DefaultDirectedGraph;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.GraphOfRuleDependencies;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.core.LabelRuleComparator;
import fr.lirmm.graphik.graal.core.Substitutions;
import fr.lirmm.graphik.graal.core.TreeMapSubstitution;
import fr.lirmm.graphik.graal.core.Unifier;
import fr.lirmm.graphik.util.LinkedSet;
import fr.lirmm.graphik.util.graph.scc.StronglyConnectedComponentsGraph;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;

/**
 * The graph of rule dependencies (GRD) is a directed graph built from a rule
 * set as follows: there is a vertex for each rule in the set and there is an
 * edge from a rule R1 to a rule R2 if R1 may lead to trigger R2, i.e., R2
 * depends on R1. R2 depends on R1 if and only if there is piece-unifier (for
 * this notion, see f.i. this paper (TODO)) between the body of R2 and the head
 * of R1.
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>} <br/>
 *         Swan Rocher {@literal <swan.rocher@lirmm.fr>}
 * 
 */
public class DefaultGraphOfRuleDependencies implements GraphOfRuleDependencies {

	private DirectedGraph<Rule, Integer> graph;
	private ArrayList<Set<Substitution>> edgesValue;
	private DependencyChecker checker;
	private boolean computingUnifiers;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	public DefaultGraphOfRuleDependencies(Iterable<Rule> rules, boolean withUnifiers, DependencyChecker checker) {
		this(rules.iterator(), withUnifiers, checker);
	}
	
	public DefaultGraphOfRuleDependencies(Iterator<Rule> rules, boolean withUnifiers, DependencyChecker checker) {
		this.graph = new DefaultDirectedGraph<Rule, Integer>(Integer.class);
		this.edgesValue = new ArrayList<Set<Substitution>>();
		this.computingUnifiers = withUnifiers;

		while(rules.hasNext()) {
			this.addRule(rules.next());
		}

		this.checker = checker;
		this.computeDependencies(checker);
	}

	public DefaultGraphOfRuleDependencies(Iterable<Rule> rules, boolean wu) {
		this(rules.iterator(),wu);
	}
	
	public DefaultGraphOfRuleDependencies(Iterator<Rule> rules, boolean wu) {
		this(rules,wu,DependencyChecker.DEFAULT);
	}

	public DefaultGraphOfRuleDependencies(Iterable<Rule> rules, DependencyChecker checker) {
		this(rules.iterator(),false,checker);
	}

	public DefaultGraphOfRuleDependencies(Iterable<Rule> rules) {
		this(rules,false);
	}
	
	public DefaultGraphOfRuleDependencies(Iterator<Rule> rules) {
		this(rules,false);
	}

	protected DefaultGraphOfRuleDependencies(boolean wu) {
		this.graph = new DefaultDirectedGraph<Rule, Integer>(Integer.class);
		this.edgesValue = new ArrayList<Set<Substitution>>();
		this.computingUnifiers = wu;
	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public boolean hasCircuit() {
		CycleDetector<Rule,Integer> cycle = new CycleDetector<Rule,Integer>(this.graph);
		return cycle.detectCycles();
	}

	public Set<Substitution> getUnifiers(Integer e) {
		if(this.computingUnifiers) {
			return Collections.unmodifiableSet(this.edgesValue.get(e));
		} else {
			return this.computeDependency(this.graph.getEdgeSource(e), this.graph.getEdgeTarget(e), this.checker);
		}
	}

	@Override
	public DefaultGraphOfRuleDependencies getSubGraph(Iterable<Rule> ruleSet) {
		DefaultGraphOfRuleDependencies subGRD = new DefaultGraphOfRuleDependencies(this.computingUnifiers);
		subGRD.addRuleSet(ruleSet);
		for (Rule src : ruleSet) {
			for (Rule target : ruleSet) {
				Integer e = this.graph.getEdge(src, target);
				if (e != null) { // there is an edge
					for (Substitution s : this.edgesValue.get(e)) {
						subGRD.addDependency(src, s, target);
					}
				}
			}
		}
		return subGRD;
	}

	@Override
	public Iterable<Rule> getRules() {
		return this.graph.vertexSet();
	}

	public Iterable<Integer> getOutgoingEdgesOf(Rule src) {
		return this.graph.outgoingEdgesOf(src);
	}
	
	@Override
	public Set<Rule> getTriggeredRules(Rule src) {
		Set<Rule> set = new HashSet<Rule>();
		for(Integer i : this.graph.outgoingEdgesOf(src)) {
			set.add(this.graph.getEdgeTarget(i));
		}
		return set;
	}
	
	public Set<Pair<Rule,Substitution>> getTriggeredRulesWithUnifiers(Rule src) {
		Set<Pair<Rule,Substitution>> set = new HashSet<Pair<Rule,Substitution>>();
		for(Integer i : this.graph.outgoingEdgesOf(src)) {
			Rule target = this.graph.getEdgeTarget(i);
			for (Substitution u : this.getUnifiers(i)) {
				Pair<Rule, Substitution> p = new ImmutablePair<Rule, Substitution>(target, u);
				set.add(p);
			}
		}
		return set;
	}


	public Rule getEdgeTarget(Integer i) {
		return this.graph.getEdgeTarget(i);
	}

	@Override
	public boolean existUnifier(Rule src, Rule dest) {
		Integer index = this.graph.getEdge(src, dest);
		return index != null;
	}
	
	@Override
	public Set<Substitution> getUnifiers(Rule src, Rule dest) {
		Integer index = this.graph.getEdge(src, dest);
		if(index != null) {
			return this.getUnifiers(index);
		} else {
			return Collections.<Substitution>emptySet();
		}
	}

	@Override
	public StronglyConnectedComponentsGraph<Rule> getStronglyConnectedComponentsGraph() {
		return new<Integer> StronglyConnectedComponentsGraph<Rule>(this.graph);
	}

	// /////////////////////////////////////////////////////////////////////////
	// OVERRIDE METHODS

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		TreeSet<Rule> rules = new TreeSet<Rule>(new LabelRuleComparator());
		for (Rule r : this.graph.vertexSet()) {
			rules.add(r);
		}
		for (Rule src : rules) {
			for (Integer e : this.graph.outgoingEdgesOf(src)) {
				Rule dest = this.graph.getEdgeTarget(e);

				s.append(src.getLabel());
				s.append("--");
				for (Substitution sub : this.edgesValue.get(this.graph.getEdge(
						src, dest))) {
					s.append(sub);
				}
				s.append("-->");
				s.append(dest.getLabel());
				s.append('\n');
			}

		}
		return s.toString();
	}

	// /////////////////////////////////////////////////////////////////////////
	// PROTECTED METHODS
	// /////////////////////////////////////////////////////////////////////////

	protected void addDependency(Rule src, Substitution sub, Rule dest) {
		Integer edgeIndex = this.graph.getEdge(src, dest);
		Set<Substitution> edge = null;
		if (edgeIndex != null) {
			edge = this.edgesValue.get(edgeIndex);
		} else {
			edgeIndex = this.edgesValue.size();
			edge = new LinkedSet<Substitution>();
			this.edgesValue.add(edgeIndex, edge);
			this.graph.addEdge(src, dest, edgeIndex);
		}
		edge.add(sub);
	}

	protected void setDependency(Rule src, Set<Substitution> subs, Rule dest) {
		Integer edgeIndex = this.graph.getEdge(src, dest);
		if (edgeIndex == null) {
			edgeIndex = this.edgesValue.size();
			this.edgesValue.add(edgeIndex, subs);
			this.graph.addEdge(src, dest, edgeIndex);
		} else {
			this.edgesValue.set(edgeIndex, subs);
		}
	}
	
	protected Set<Substitution> computeDependency(Rule r1, Rule r2, DependencyChecker checker) {
		checker.setRule1(r1);
		checker.setRule2(r2);
		if (this.computingUnifiers) {
			return Unifier.instance().computePieceUnifier(r1,r2.getBody(),checker);
		}
		else {
			return Collections.<Substitution>singleton(Substitutions.emptySubstitution());
		}
	}


	private static final String PREFIX = "R" + new Date().hashCode() + "-";
	private static int ruleIndex = -1;
	protected void addRule(Rule r) {
		if(r.getLabel().isEmpty()) {
			r.setLabel(PREFIX + ++ruleIndex);
		}
		this.graph.addVertex(r);
	}

	protected void addRuleSet(Iterable<Rule> ruleSet) {
		for (Rule r : ruleSet) {
			this.addRule(r);
		}
	}

	protected void computeDependencies(DependencyChecker checker) {
		// preprocess
		Map<Predicate, List<Rule>> index = new TreeMap<Predicate,List<Rule>>();
		for (Rule r : this.graph.vertexSet()) {
			CloseableIteratorWithoutException<Atom> it = r.getBody().iterator();
			while (it.hasNext()) {
				Atom a = it.next();
				if (index.get(a.getPredicate()) == null)
					index.put(a.getPredicate(),new LinkedList<Rule>());
				index.get(a.getPredicate()).add(r);
			}
		}

		List<Rule> candidates = null;
		Set<String> marked = new TreeSet<String>();
		for (Rule r1 : this.graph.vertexSet()) {
			marked.clear();
			CloseableIteratorWithoutException<Atom> it = r1.getHead().iterator();
			while (it.hasNext()) {
				Atom a = it.next();
				candidates = index.get(a.getPredicate());
				if (candidates != null) {
					for (Rule r2 : candidates) {
						if (marked.add(r2.getLabel())) {
							Set<Substitution> unifiers = computeDependency(r1,r2,checker);
							if (!unifiers.isEmpty()) {
								this.setDependency(r1, unifiers, r2);
							}
						}
					}
				}
			}
		}
	}

}
