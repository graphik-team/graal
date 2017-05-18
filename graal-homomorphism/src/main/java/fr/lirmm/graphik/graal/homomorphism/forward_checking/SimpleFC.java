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
package fr.lirmm.graphik.graal.homomorphism.forward_checking;

import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.RulesCompilation;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.graal.homomorphism.BacktrackException;
import fr.lirmm.graphik.graal.homomorphism.Var;
import fr.lirmm.graphik.graal.homomorphism.VarSharedData;
import fr.lirmm.graphik.graal.homomorphism.backjumping.BackJumping;
import fr.lirmm.graphik.graal.homomorphism.utils.BacktrackUtils;
import fr.lirmm.graphik.graal.homomorphism.utils.HomomorphismIteratorChecker;
import fr.lirmm.graphik.util.profiler.AbstractProfilable;
import fr.lirmm.graphik.util.profiler.Profiler;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.IteratorException;

/**
 * SimpleFC is a simple ForwardChecking implementation for HyperGraph with
 * immediate local checking in one step. It is simple because it does not
 * maintain a list of possible candidats for each variables.
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class SimpleFC extends AbstractProfilable implements ForwardChecking {

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public void init(VarSharedData[] vars, Map<Variable, Integer> map) {
	}

	@Override
	public boolean checkForward(Var v, AtomSet g,  Substitution initialSubstitution, Map<Variable, Integer> map, Var[] varData, RulesCompilation rc)
	    throws BacktrackException {

		Profiler profiler = this.getProfiler();
		for (Atom atom : v.shared.postAtoms) {
			boolean contains = false;
			Atom im = BacktrackUtils.createImageOf(atom, initialSubstitution, map, varData);

			if (profiler != null) {
				profiler.incr("#selectOne", 1);
				profiler.start("selectOneTime");
			}
			for (Pair<Atom, Substitution> rew : rc.getRewritingOf(im)) {
				Atom a = rew.getLeft();
				CloseableIterator<Atom> matchIt = null;
				try {
					matchIt = g.match(a);
					if (matchIt.hasNext()) {
						contains = true;
						break;
					}
				} catch (IteratorException e) {
					throw new BacktrackException(e);
				} catch (AtomSetException e) {
					throw new BacktrackException(e);
				} finally {
					if(matchIt != null) {
						matchIt.close();
					}
				}
			}
			if (profiler != null) {
				profiler.stop("selectOneTime");
			}

			if (!contains) {
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean isInit(int level) {
		return false;
	}

	@Override
	public CloseableIterator<Term> getCandidatsIterator(AtomSet g, Var var, Substitution initialSubstitution, Map<Variable, Integer> map, Var[] varData, RulesCompilation rc)
	    throws BacktrackException {
		HomomorphismIteratorChecker tmp;
		try {
			tmp = new HomomorphismIteratorChecker(var, g.termsIterator(), var.shared.preAtoms, g, initialSubstitution, map, varData, rc);
		} catch (AtomSetException e) {
			throw new BacktrackException(e);
		}
		tmp.setProfiler(this.getProfiler());
		return tmp;
	}

	@Override
	public void setBackJumping(BackJumping bj) {
		
	}

	@Override
	public StringBuilder append(StringBuilder sb, int level) {
		return sb.append("SimpleFC");
	}

	@Override
	public void clear() {
	}
	// /////////////////////////////////////////////////////////////////////////
	// OBJECT OVERRIDE METHODS
	// /////////////////////////////////////////////////////////////////////////


	
	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

}
