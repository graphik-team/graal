/**
 * 
 */
package fr.lirmm.graphik.graal.grd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import org.apache.commons.graph.model.DirectedMutableGraph;

import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.Substitution;
import fr.lirmm.graphik.util.graph.scc.StronglyConnectedComponentsGraph;
import fr.lirmm.graphik.util.graph.scc.TarjanAlgorithm2;

/**
 * The graph of rule dependencies (GRD) is a directed graph built from a rule
 * set as follows: there is a vertex for each rule in the set and there is an
 * edge from a rule R1 to a rule R2 if R1 may lead to trigger R2, i.e., R2
 * depends on R1. R2 depends on R1 if and only if there is piece-unifier (for
 * this notion, see f.i. this paper) between the body of R2 and the head of R1.
 * 
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public class GraphOfRuleDependencies {

	private DirectedMutableGraph<Rule, Integer> graph;
	private ArrayList<Collection<Substitution>> edgesValue;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	public GraphOfRuleDependencies() {
		this.graph = new DirectedMutableGraph<Rule, Integer>();
		this.edgesValue = new ArrayList<Collection<Substitution>>();
	}
	
	public GraphOfRuleDependencies(Iterable<Rule> it) {
		this();
		for(Rule r : it)
			this.addRule(r);
		this.computeGRD();
	}

	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////

	public void addRule(Rule r) {
		this.graph.addVertex(r);
	}

	public void addRuleSet(Iterable<Rule> ruleSet) {
		for (Rule r : ruleSet) {
			this.addRule(r);
		}
	}

	public Iterable<Rule> getRules() {
		return this.graph.getVertices();
	}

	public void addDependency(Rule src, Substitution sub, Rule dest) {
	
		Collection<Substitution> edge  = this.getUnifier(src, dest);
		if(edge == null) {
			int edgeIndex = this.edgesValue.size();
			edge = new LinkedList<Substitution>();
			this.edgesValue.add(edgeIndex, edge);
			this.graph.addEdge(src, edgeIndex, dest);
		}
		edge.add(sub);
		
	}

	public Iterable<Rule> getOutEdges(Rule src) {
		return this.graph.getOutbound(src);
	}

	public Collection<Substitution> getUnifier(Rule src, Rule dest) {
		Integer index = this.graph.getEdge(src, dest);
		Collection<Substitution> res = null;
		if(index != null) {
			res = this.edgesValue.get(index);
		}
		return  res;
	}

	public StronglyConnectedComponentsGraph<Rule> getStronglyConnectedComponentsGraph() {
		StronglyConnectedComponentsGraph<Rule> scc = new TarjanAlgorithm2<Rule>(this.graph).perform();
		return scc;
	}
	
	/**
	 * @param ruleset
	 * @return
	 */
	public GraphOfRuleDependencies getSubGraph(Iterable<Rule> ruleSet) {
		GraphOfRuleDependencies subGRD = new GraphOfRuleDependencies();
		subGRD.addRuleSet(ruleSet);
		for (Rule src : ruleSet) {
			for (Rule target : ruleSet) {
				Integer e = this.graph.getEdge(src, target);
				if (e != null) { // there is an edge
					for(Substitution s : this.edgesValue.get(e)) {
						subGRD.addDependency(src, s, target);
					}
				}
			}
		}
		return subGRD;
	}

	// /////////////////////////////////////////////////////////////////////////
	// OVERRIDE METHODS
	// /////////////////////////////////////////////////////////////////////////

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		for (Rule src : this.graph.getVertices()) {
			for (Rule dest : this.graph.getOutbound(src)) {
				
				s.append(src.getLabel());
				s.append("--");
				for(Substitution sub : this.edgesValue.get(this.graph.getEdge(src,
						dest))) {
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
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

	private void computeGRD() {
		for(Rule r1 : this.graph.getVertices()) {
			for(Rule r2 : this.graph.getVertices()) {
			}
		}
	}
}
