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
package fr.lirmm.graphik.graal.backward_chaining.pure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.homomorphism.PureHomomorphism;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
class PureHomomorphismWithCompilation extends PureHomomorphism {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(PureHomomorphismWithCompilation.class);
	
	private static PureHomomorphismWithCompilation instance;

	protected PureHomomorphismWithCompilation() {
		super();
	}

	public static synchronized PureHomomorphismWithCompilation getInstance() {
		if (instance == null)
			instance = new PureHomomorphismWithCompilation();

		return instance;
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	/**
	 * return true iff exist an homomorphism from the query to the fact else
	 * return false
	 */
	public boolean exist(AtomSet source, AtomSet target, RulesCompilation compilation)
			throws HomomorphismException {

		Homomorphism homomorphism = new Homomorphism();
		homomorphism.compilation = compilation;

		// check if the query is empty
		if (source == null || !source.iterator().hasNext()) {
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("Empty query");
			}
			return true;
		}

		// /////////////////////////////////////////////////////////////////////
		// Initialisation
		if (!initialiseHomomorphism(homomorphism, source, target))
			return false;

		return backtrack(homomorphism);
	}
	
	@Override
	protected boolean isMappable(Atom a, Atom im, PureHomomorphism.Homomorphism homomorphism) {
		if(((Homomorphism) homomorphism).compilation != null){
			return ((Homomorphism) homomorphism).compilation.isMappable(a, im);
		}
		else {
			return a.getPredicate().equals(im.getPredicate());
		}
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE CLASS
	// /////////////////////////////////////////////////////////////////////////

	protected static class Homomorphism extends PureHomomorphism.Homomorphism {
		RulesCompilation compilation = null;
	}
}
