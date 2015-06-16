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
 package fr.lirmm.graphik.graal.core;

import fr.lirmm.graphik.graal.core.term.Term;

/**
 * This interface represents a function for a {@link BuiltInPredicate}
 * 
 * @author Swan Rocher {@literal <swan.rocher@lirmm.fr>}
 *
 */
public interface PredicateFunction {

	public boolean evaluate(Term... t);

};

