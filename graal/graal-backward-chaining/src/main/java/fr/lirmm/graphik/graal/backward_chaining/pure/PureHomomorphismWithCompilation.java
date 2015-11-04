/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2015)
 *
 * Contributors :
 *
 * Clément SIPIETER <clement.sipieter@inria.fr>
 * Mélanie KÖNIG
 * Swan ROCHER
 * Jean-François BAGET
 * Michel LECLÈRE
 * Marie-Laure MUGNIER <mugnier@lirmm.fr>
 *
 *
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
 /**
 * 
 */
package fr.lirmm.graphik.graal.backward_chaining.pure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.RulesCompilation;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
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

	public static synchronized PureHomomorphismWithCompilation instance() {
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
	protected boolean isMappable(Predicate a, Predicate im, PureHomomorphism.Homomorphism homomorphism) {
		if(((Homomorphism) homomorphism).compilation != null){
			return ((Homomorphism) homomorphism).compilation.isMappable(a, im);
		}
		else {
			return super.isMappable(a, im, homomorphism);
		}
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE CLASS
	// /////////////////////////////////////////////////////////////////////////

	protected static class Homomorphism extends PureHomomorphism.Homomorphism {
		RulesCompilation compilation = null;
	}
}
