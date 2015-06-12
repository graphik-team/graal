/* Graal v0.7.4
 * Copyright (c) 2014-2015 Inria Sophia Antipolis - Méditerranée / LIRMM (Université de Montpellier & CNRS)
 * All rights reserved.
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * Author(s): Clément SIPIETER
 *            Mélanie KÖNIG
 *            Swan ROCHER
 *            Jean-François BAGET
 *            Michel LECLÈRE
 *            Marie-Laure MUGNIER
 */
 /**
 * 
 */
package fr.lirmm.graphik.graal.forward_chaining;

import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.grd.GraphOfRuleDependencies;

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

	public static void executeChaseWithGRDAndUnfiers(AtomSet atomSet,
			GraphOfRuleDependencies grd)
			throws ChaseException {
		Chase chase = new ChaseWithGRDAndUnfiers(grd, atomSet);
		chase.execute();
	}

	public static void executeOneStepChase(AtomSet atomSet,
			Iterable<Rule> ruleSet) throws ChaseException {
		Chase chase = new DefaultChase(ruleSet, atomSet);
		chase.next();
	}

	public static void executeOneStepChaseWithGRDAndUnifiers(AtomSet atomSet,
			GraphOfRuleDependencies grd) throws ChaseException {
		Chase chase = new ChaseWithGRDAndUnfiers(grd, atomSet);
		chase.next();
	}
}
