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

import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.RulesCompilation;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.graal.homomorphism.BacktrackException;
import fr.lirmm.graphik.graal.homomorphism.Var;
import fr.lirmm.graphik.graal.homomorphism.VarSharedData;
import fr.lirmm.graphik.graal.homomorphism.backjumping.BackJumping;
import fr.lirmm.graphik.util.profiler.Profilable;
import fr.lirmm.graphik.util.stream.CloseableIterator;

/**
 * The ForwardChecking interface provides a way to restrict the domain of some
 * next variables based of the affectation of the current one.
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public interface ForwardChecking extends Profilable {

	void init(VarSharedData[] vars, Map<Variable, Integer> map);

	boolean isInit(int level);
	
	void setBackJumping(BackJumping bj);

	/**
	 * 
	 * @param v
	 * @param g
	 * @param map
	 * @param rc
	 * @return -1 if the current affectation is acceptable, the level of the detected issue otherwise.
	 * @throws BacktrackException
	 */
	boolean checkForward(Var v, AtomSet g, Substitution initialSubstitution, Map<Variable, Integer> map, Var[] varData, RulesCompilation rc) throws BacktrackException;

	/**
	 * @param var
	 * @return an iterator over possible candidates.
	 * @throws BacktrackException
	 */
	CloseableIterator<Term> getCandidatsIterator(AtomSet g, Var var, Substitution initialSubstitution, Map<Variable, Integer> map, Var[] varData,  RulesCompilation rc)
	    throws BacktrackException;

	/**
	 * @param sb
	 * @param level
	 */
	StringBuilder append(StringBuilder sb, int level);

	/**
	 * 
	 */
	void clear();
		
}
