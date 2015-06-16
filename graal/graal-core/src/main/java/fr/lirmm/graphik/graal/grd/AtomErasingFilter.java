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
 package fr.lirmm.graphik.graal.grd;

import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.Substitution;
import fr.lirmm.graphik.graal.core.atomset.AtomSetUtils;
import fr.lirmm.graphik.graal.core.atomset.InMemoryAtomSet;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;

public class AtomErasingFilter extends GraphOfRuleDependencies.DependencyChecker {

	@Override
	protected boolean isValidDependency(Rule r1, Rule r2, Substitution s) {
		InMemoryAtomSet b1 = s.createImageOf(r1.getBody());
		InMemoryAtomSet h1 = s.createImageOf(r1.getHead());
		InMemoryAtomSet b2 = s.createImageOf(r2.getBody());
		InMemoryAtomSet h2 = s.createImageOf(r2.getHead());

		InMemoryAtomSet f = new LinkedListAtomSet();
		f.addAll(b1);
		f.addAll(h1);
		f.addAll(b2);

		// mu(B2) not subset of mu(B1) 
		// (R2 could not be applied on F)
		//if (isSubsetEq(B2,B1)) return false;


		// mu(H2) not subset of mu(B1) cup mu(B2) cup mu(H1)
		// (mu may lead to a *new* application of R2)
		// if (isSubsetEq(H2,f)) return false;

		return !AtomSetUtils.contains(b1,b2) && !AtomSetUtils.contains(f,h2);
	}

};

