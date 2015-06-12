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
package fr.lirmm.graphik.graal.rulesetanalyser.util;

import java.util.Collection;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.term.Term;
import fr.lirmm.graphik.graal.core.term.Term.Type;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public final class RuleUtil {

	private RuleUtil() {}
	
	public static boolean thereIsOneAtomThatContainsAllVars(Iterable<Atom> atomset, Collection<Term> terms) {
		for(Atom atom : atomset) {
			if(atom.getTerms(Type.VARIABLE).containsAll(terms)) {
				return true;
			}
		}
		return false;
	}
}
