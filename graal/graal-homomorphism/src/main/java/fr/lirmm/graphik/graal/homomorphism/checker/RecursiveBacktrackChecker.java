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
package fr.lirmm.graphik.graal.homomorphism.checker;

import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Query;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.homomorphism.RecursiveBacktrackHomomorphism;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class RecursiveBacktrackChecker extends AbstractChecker {
	
	@Override
	public RecursiveBacktrackHomomorphism getSolver() {
		return RecursiveBacktrackHomomorphism.getInstance();
	}
	
	@Override
	public boolean check(Query query,  AtomSet atomset) {
		return query instanceof ConjunctiveQuery;
	}

	@Override
	public int getDefaultPriority() {
		return 0;
	}

}
