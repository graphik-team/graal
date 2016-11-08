/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2016)
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
package fr.lirmm.graphik.graal.homomorphism;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.Query;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.homomorphism.Homomorphism;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismFactory;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismFactoryException;
import fr.lirmm.graphik.util.AbstractProfilable;
import fr.lirmm.graphik.util.stream.CloseableIterator;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
public class StaticHomomorphism extends AbstractProfilable implements Homomorphism<Query, AtomSet> {

	private static final Logger LOGGER = LoggerFactory.getLogger(StaticHomomorphism.class);

	public static HomomorphismFactory getSolverFactory() {
		return DefaultHomomorphismFactory.instance();
	}

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////

	private static StaticHomomorphism instance;

	protected StaticHomomorphism() {
		super();
	}

	public static synchronized StaticHomomorphism instance() {
		if (instance == null)
			instance = new StaticHomomorphism();

		return instance;
	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * For boolean query, return a SubstitutionReader with an empty Substitution
	 * for true and no substitution for false.
	 * 
	 * @param query
	 * @param atomSet
	 * @return A substitution stream that represents homomorphisms.
	 * @throws HomomorphismFactoryException
	 * @throws HomomorphismException
	 */
	@Override
	public <T1 extends Query, U2 extends AtomSet> CloseableIterator<Substitution> execute(T1 query, U2 atomSet)
	    throws HomomorphismException {
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Query : " + query);

		Homomorphism solver = getSolverFactory().getSolver(query, atomSet);
		solver.setProfiler(this.getProfiler());
		return solver.execute(query, atomSet);
	}

	@Override
	public <U1 extends Query, U2 extends AtomSet> boolean exist(U1 query, U2 atomset) throws HomomorphismException {
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Query : " + query);

		Homomorphism solver = getSolverFactory().getSolver(query, atomset);
		solver.setProfiler(this.getProfiler());
		return solver.exist(query, atomset);
	}

}
