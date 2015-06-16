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

import java.util.HashMap;
import java.util.Map;

import fr.lirmm.graphik.graal.core.term.Term;

/**
 * An implementation of Substitution using a {@link HashMap}.
 * 
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class HashMapSubstitution extends AbstractSubstitution {

	private HashMap<Term, Term> map = new HashMap<Term, Term>();

	public HashMapSubstitution() {
		super();
	}

	public HashMapSubstitution(Substitution substitution) {
		super();
		for (Term term : substitution.getTerms())
			this.map.put(term, substitution.createImageOf(term));
	}

	@Override
	protected Map<Term, Term> getMap() {
		return this.map;
	}

};
