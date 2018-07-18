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
package fr.lirmm.graphik.graal.core;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.util.stream.filter.Filter;

public class TypeFilter implements Filter<Atom> {

	private AtomType type;
	private Atom atom;

	public TypeFilter(AtomType type, Atom atom) {
		this.type = type;
		this.atom = atom;
	}
	
	@Override
	public boolean filter(Atom e) {
		for(int i = 0; i < type.type.length; ++i) {
			if(type.type[i] >= 0) {
				if(!e.getTerm(i).equals(e.getTerm(type.type[i]))) {
					return false;
				}
			} else if (type.type[i] == AtomType.CONSTANT_OR_FROZEN_VAR) {
				if(!e.getTerm(i).equals(atom.getTerm(i))) {
					return false;
				}
			}
		}
		return true;
	}

}