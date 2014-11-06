/**
 * 
 */
package fr.lirmm.graphik.graal.forward_chaining;

import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.grd.GraphOfRuleDependenciesWithUnifiers;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class StaticChase {
	public static void executeChase(AtomSet atomSet, Iterable<Rule> ruleSet)
			throws ChaseException {
		Chase chase = new DefaultChase(ruleSet, atomSet);
		chase.execute();
	}

	public static void executeChase(AtomSet atomSet, GraphOfRuleDependenciesWithUnifiers grd)
			throws ChaseException {
		Chase chase = new ChaseWithGRD(grd, atomSet);
		chase.execute();
	}

	public static void executeOneStepChase(AtomSet atomSet,
			Iterable<Rule> ruleSet) throws ChaseException {
		Chase chase = new DefaultChase(ruleSet, atomSet);
		chase.next();
	}

	public static void executeOneStepChase(AtomSet atomSet,
			GraphOfRuleDependenciesWithUnifiers grd) throws ChaseException {
		Chase chase = new ChaseWithGRD(grd, atomSet);
		chase.next();
	}
}
