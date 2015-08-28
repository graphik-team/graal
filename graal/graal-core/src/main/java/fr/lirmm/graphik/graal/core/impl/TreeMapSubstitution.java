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
package fr.lirmm.graphik.graal.core.impl;

import java.util.Map;
import java.util.TreeMap;

import fr.lirmm.graphik.graal.core.Substitution;
import fr.lirmm.graphik.graal.core.term.Term;

/**
 * An implementation of Susbstitution using a {@link TreeMap}
 * 
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public class TreeMapSubstitution extends AbstractSubstitution {

	private TreeMap<Term, Term> map = new TreeMap<Term, Term>();

	public TreeMapSubstitution() {
		super();
	}

	public TreeMapSubstitution(Substitution substitution) {
		super();
		for (Term term : substitution.getTerms())
			this.map.put(term, substitution.createImageOf(term));
	}

	@Override
	protected Map<Term, Term> getMap() {
		return this.map;
	}

};
