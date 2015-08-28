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
package fr.lirmm.graphik.graal.core.factory;

import fr.lirmm.graphik.graal.core.Substitution;
import fr.lirmm.graphik.graal.core.impl.HashMapSubstitution;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public final class SubstitutionFactory {

	private static SubstitutionFactory instance = new SubstitutionFactory();
	
	private SubstitutionFactory() {
	}

	public static SubstitutionFactory instance() {
		return instance;
	}

	public Substitution createSubstitution() {
		return new HashMapSubstitution();
	}
}
