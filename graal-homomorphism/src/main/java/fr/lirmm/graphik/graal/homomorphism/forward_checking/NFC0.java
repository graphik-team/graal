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

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.RulesCompilation;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.graal.homomorphism.BacktrackException;
import fr.lirmm.graphik.graal.homomorphism.Var;
import fr.lirmm.graphik.graal.homomorphism.VarSharedData;
import fr.lirmm.graphik.util.stream.IteratorException;

/**
 * NFC0 is a ForwardChecking implementation for HyperGraph with delayed local
 * propagation. <br/>
 * 
 * delayed: check atoms with only one variable not assigned (after current
 * assignment). <br/>
 * local: check atoms containing at least one atoms from the set of post
 * variables of the current variable. <br/>
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class NFC0 extends AbstractNFC implements ForwardChecking {

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	public NFC0() {
	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public boolean checkForward(Var v, AtomSet g, Substitution initialSubstitution, Map<Variable, Integer> map, Var[] varData, RulesCompilation rc) throws BacktrackException {

		// clear all computed candidats for post variables
		for (VarSharedData z : v.shared.postVars) {
			this.clear(v.shared, z);
		}

		for (Atom atom : v.shared.postAtoms) {
			if (mustBeChecked(atom, map, varData)) {
				try {
					if(!select(atom, v, g, initialSubstitution, map, varData, rc)) {
						return false;
					}
				} catch (IteratorException e) {
					throw new BacktrackException("An error occurs while selecting candidates for next steps ", e);
				} catch (AtomSetException e) {
					throw new BacktrackException("An error occurs while selecting candidates for next steps ", e);
				}
			}
		}

		return true;
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	/**
	 * Check delayed property
	 * @param atom
	 * @param map
	 * @return true if the specified atom must be checked now, false otherwise.
	 */
	protected boolean mustBeChecked(Atom atom, Map<Variable, Integer> map, Var[] varData) {
		int i = 0;
		for (Variable t : atom.getVariables()) {
			Integer idx = map.get(t);
			if(idx != null) {
				Var z = varData[idx];
    			if (z.image != null) {
    				if (++i > 1) {
    					return false;
    				}
    			}
			}
		}
		return true;
	}

}
