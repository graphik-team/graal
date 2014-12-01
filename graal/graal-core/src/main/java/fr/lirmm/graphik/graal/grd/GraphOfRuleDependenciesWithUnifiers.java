/**
 * 
 */
package fr.lirmm.graphik.graal.grd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import fr.lirmm.graphik.graal.core.LabelRuleComparator;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.Substitution;
import fr.lirmm.graphik.graal.core.Unifier;
import fr.lirmm.graphik.util.LinkedSet;

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
public class GraphOfRuleDependenciesWithUnifiers extends GraphOfRuleDependencies {

	private ArrayList<Set<Substitution>> edgesValue;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	protected GraphOfRuleDependenciesWithUnifiers() {
		super();
		this.edgesValue = new ArrayList<Set<Substitution>>();
	}

	public GraphOfRuleDependenciesWithUnifiers(Iterable<Rule> rules) {
		this();
		for (Rule r : rules) {
			this.addRule(r);
		}
		for (Rule r1 : rules) {
			for (Rule r2 : rules) {
				Set<Substitution> unifiers = Unifier.getInstance().computePieceUnifier(r1,
						r2.getBody());
				if (!unifiers.isEmpty()) {
					this.setDependency(r1, unifiers, r2);
				}
			}
		}
	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	public Set<Substitution> getUnifiers(Rule src, Rule dest) {
		Integer index = this.graph.getEdge(src, dest);
		Set<Substitution> res = null;
		if (index != null) {
			res = this.edgesValue.get(index);
		}
		return Collections.unmodifiableSet(res);
	}

	/**
	 * @param ruleset
	 * @return
	 */
	public GraphOfRuleDependenciesWithUnifiers getSubGraph(Iterable<Rule> ruleSet) {
		GraphOfRuleDependenciesWithUnifiers subGRD = new GraphOfRuleDependenciesWithUnifiers();
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

	// /////////////////////////////////////////////////////////////////////////
	// OVERRIDE METHODS

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		TreeSet<Rule> rules = new TreeSet<Rule>(new LabelRuleComparator());
		for (Rule r : this.graph.getVertices()) {
			rules.add(r);
		}
		for (Rule src : rules) {
			for (Rule dest : this.graph.getOutbound(src)) {

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
			this.graph.addEdge(src, edgeIndex, dest);
		}
		edge.add(sub);
	}

	protected void setDependency(Rule src, Set<Substitution> subs, Rule dest) {
		Integer edgeIndex = this.graph.getEdge(src, dest);
		if (edgeIndex == null) {
			edgeIndex = this.edgesValue.size();
			this.edgesValue.add(edgeIndex, subs);
			this.graph.addEdge(src, edgeIndex, dest);
		} else {
			this.edgesValue.set(edgeIndex, subs);
		}
	}
}
