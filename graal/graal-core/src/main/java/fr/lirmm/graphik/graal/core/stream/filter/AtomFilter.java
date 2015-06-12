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
package fr.lirmm.graphik.graal.core.stream.filter;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.util.stream.filter.Filter;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class AtomFilter implements Filter {
	
	private static AtomFilter instance;

	protected AtomFilter() {
		super();
	}

	public static synchronized AtomFilter getInstance() {
		if (instance == null)
			instance = new AtomFilter();

		return instance;
	}

	@Override
	public boolean filter(Object o) {
		return o instanceof Atom;
	}
};
