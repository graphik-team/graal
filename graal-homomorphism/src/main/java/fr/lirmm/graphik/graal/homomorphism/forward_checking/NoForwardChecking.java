package fr.lirmm.graphik.graal.homomorphism.forward_checking;

import java.util.Map;

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
import fr.lirmm.graphik.graal.homomorphism.utils.HomomorphismIteratorChecker;
import fr.lirmm.graphik.util.profiler.AbstractProfilable;
import fr.lirmm.graphik.util.stream.CloseableIterator;

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
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class NoForwardChecking extends AbstractProfilable implements ForwardChecking {

	private static NoForwardChecking instance;

	protected NoForwardChecking() {
		super();
	}

	public static synchronized NoForwardChecking instance() {
		if (instance == null)
			instance = new NoForwardChecking();

		return instance;
	}

	@Override
	public void init(VarSharedData[] vars, Map<Variable, Integer> map) {
	}

	@Override
	public boolean isInit(int level) {
		return false;
	}
	
	@Override
	public void clear() {
	}

	@Override
	public boolean checkForward(Var v, AtomSet g,  Substitution initialSubstitution, Map<Variable, Integer> map, Var[] varData, RulesCompilation rc) {
		return true;
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
		return sb.append("NOFC");
	}
	
	public ForwardChecking clone() {
		return this;
	}


}
