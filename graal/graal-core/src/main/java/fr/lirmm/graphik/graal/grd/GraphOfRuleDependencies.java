/**
 * 
 */
package fr.lirmm.graphik.graal.grd;

import java.util.Date;

import org.apache.commons.graph.model.DirectedMutableGraph;

import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.Unifier;
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

	protected DirectedMutableGraph<Rule, Integer> graph;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	protected GraphOfRuleDependencies() {
		this.graph = new DirectedMutableGraph<Rule, Integer>();
	}

	public GraphOfRuleDependencies(Iterable<Rule> rules) {
		this();
		for (Rule r : rules) {
			this.addRule(r);
		}
		for (Rule r1 : rules) {
			for (Rule r2 : rules) {
				if(Unifier.getInstance().existPieceUnifier(r1,
						r2.getBody())) {
					this.addDependency(r1, r2);
				}
			}
		}
	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	public Iterable<Rule> getRules() {
		return this.graph.getVertices();
	}

	public Iterable<Rule> getOutEdges(Rule src) {
		return this.graph.getOutbound(src);
	}

	public boolean existUnifier(Rule src, Rule dest) {
		Integer index = this.graph.getEdge(src, dest);
		if (index != null) {
			return true;
		}
		return false;
	}

	public StronglyConnectedComponentsGraph<Rule> getStronglyConnectedComponentsGraph() {
		StronglyConnectedComponentsGraph<Rule> scc = new TarjanAlgorithm2<Rule>(
				this.graph).perform();
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
					subGRD.addDependency(src, target);
				}
			}
		}
		return subGRD;
	}

	// /////////////////////////////////////////////////////////////////////////
	// OVERRIDE METHODS

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		for (Rule src : this.graph.getVertices()) {
			for (Rule dest : this.graph.getOutbound(src)) {

				s.append(src.getLabel());
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

	private static final String PREFIX = "R" + (new Date()).hashCode() + "-";
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

	private static int edgeIndex = 0;
	protected void addDependency(Rule src, Rule dest) {
		Integer edge = this.graph.getEdge(src, dest);
		if (edge == null) {
			this.graph.addEdge(src, ++edgeIndex, dest);
		}
	}
}
