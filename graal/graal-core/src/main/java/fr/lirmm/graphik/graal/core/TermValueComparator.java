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
package fr.lirmm.graphik.graal.core;

import java.io.Serializable;
import java.util.Comparator;

import fr.lirmm.graphik.graal.core.term.Term;

/**
 * 
 * This class implements a comparator of Term that doesn't make difference
 * on Term Type.
 *
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class TermValueComparator implements Comparator<Term>, Serializable {

	private static final long serialVersionUID = -4231328676676157296L;

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Term term0, Term term1) { // TODO are you sure?
		return term0.getIdentifier().toString().compareTo(term1.getIdentifier().toString());
	}
};

