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
package fr.lirmm.graphik.graal.homomorphism;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.RulesCompilation;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.api.homomorphism.PreparedHomomorphism;
import fr.lirmm.graphik.graal.homomorphism.backjumping.GraphBaseBackJumping;
import fr.lirmm.graphik.graal.homomorphism.backjumping.NoBackJumping;
import fr.lirmm.graphik.graal.homomorphism.bbc.BCC;
import fr.lirmm.graphik.graal.homomorphism.bootstrapper.Bootstrapper;
import fr.lirmm.graphik.graal.homomorphism.bootstrapper.StatBootstrapper;
import fr.lirmm.graphik.graal.homomorphism.forward_checking.NFC2;
import fr.lirmm.graphik.graal.homomorphism.forward_checking.NoForwardChecking;
import fr.lirmm.graphik.graal.homomorphism.scheduler.Scheduler;
import fr.lirmm.graphik.util.profiler.NoProfiler;
import fr.lirmm.graphik.util.profiler.Profiler;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.IteratorException;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
class PreparedBacktrackHomomorphism implements PreparedHomomorphism {

	BacktrackIteratorData data;
	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////
	
	public PreparedBacktrackHomomorphism(ConjunctiveQuery query, Set<Variable> variablesToParameterize, AtomSet data, RulesCompilation compilation) throws HomomorphismException {

		BCC bcc = new BCC(new GraphBaseBackJumping(), true);
		this.data = new BacktrackIteratorData(query.getAtomSet(), variablesToParameterize, Collections.<InMemoryAtomSet>emptySet(), 
				data, query.getAnswerVariables(), bcc.getBCCScheduler(), StatBootstrapper.instance(), new NFC2(), bcc.getBCCBackJumping(), compilation, NoProfiler.instance());

	}


	public PreparedBacktrackHomomorphism(ConjunctiveQuery query, Set<Variable> variablesToParameterize, Collection<InMemoryAtomSet> negParts, AtomSet data, Scheduler scheduler, Bootstrapper bootstrapper, RulesCompilation compilation, Profiler profiler) throws HomomorphismException {
		
		this.data = new BacktrackIteratorData(query.getAtomSet(), variablesToParameterize, negParts, data, query.getAnswerVariables(), scheduler, bootstrapper, NoForwardChecking.instance(), NoBackJumping.instance(), compilation, profiler);
	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public boolean exist(Substitution s) throws HomomorphismException {
		CloseableIterator<Substitution> results = this.execute(s);
		boolean val;
		try {
			val = results.hasNext();
		} catch (IteratorException e) {
			throw new HomomorphismException(e);
		}
		results.close();
		return val;
	}

	@Override
	public CloseableIterator<Substitution> execute(Substitution s)
			throws HomomorphismException {
		return new BacktrackIterator(this.data, s);
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// OBJECT OVERRIDE METHODS
	// /////////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

}
