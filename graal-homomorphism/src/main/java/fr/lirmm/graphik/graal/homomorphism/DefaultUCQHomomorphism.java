/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2017)
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

import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.RulesCompilation;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.UnionOfConjunctiveQueries;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismWithCompilation;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.IteratorException;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public final class DefaultUCQHomomorphism extends
                                          AbstractHomomorphismWithCompilation<UnionOfConjunctiveQueries, AtomSet>
                                          implements HomomorphismWithCompilation<UnionOfConjunctiveQueries, AtomSet> {

	private HomomorphismWithCompilation<ConjunctiveQuery, AtomSet> homomorphism = null;

	// /////////////////////////////////////////////////////////////////////////
	// SINGLETON
	// /////////////////////////////////////////////////////////////////////////

	private static DefaultUCQHomomorphism instance;

	protected DefaultUCQHomomorphism() {
		super();
	}

	public static synchronized DefaultUCQHomomorphism instance() {
		if (instance == null)
			instance = new DefaultUCQHomomorphism();

		return instance;
	}

	public DefaultUCQHomomorphism(HomomorphismWithCompilation<ConjunctiveQuery, AtomSet> h) {
		this.homomorphism = h;
	}

	@Override
	public CloseableIterator<Substitution> execute(UnionOfConjunctiveQueries queries, AtomSet atomset,
	    RulesCompilation rc, Substitution s)
	    throws HomomorphismException {
		UnionConjunctiveQueriesSubstitutionIterator it = new UnionConjunctiveQueriesSubstitutionIterator(queries,
		                                                                                                 atomset,
		                                                                                                 s,
		                                                                                                 homomorphism,
		                                                                                                 rc);
		it.setProfiler(this.getProfiler());
		return it;
	}

	@Override
	public boolean exist(UnionOfConjunctiveQueries q, AtomSet a,
	    RulesCompilation compilation) throws HomomorphismException {
		try {
			CloseableIterator<Substitution> execute = this.execute(q, a, compilation);
			boolean res = execute.hasNext();
			execute.close();
			return res;
		} catch (IteratorException e) {
			throw new HomomorphismException(e);
		}
	}

}
