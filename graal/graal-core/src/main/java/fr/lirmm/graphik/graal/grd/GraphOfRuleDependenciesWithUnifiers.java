/**
 * 
 */
package fr.lirmm.graphik.graal.grd;

import fr.lirmm.graphik.graal.core.Rule;

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
@Deprecated
public class GraphOfRuleDependenciesWithUnifiers extends GraphOfRuleDependencies {

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	@Deprecated
	public GraphOfRuleDependenciesWithUnifiers() {
		super();
	}
	public GraphOfRuleDependenciesWithUnifiers(Iterable<Rule> rules) {
		super(rules,true);
	}

};

