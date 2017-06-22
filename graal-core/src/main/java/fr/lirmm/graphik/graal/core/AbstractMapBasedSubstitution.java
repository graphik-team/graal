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
package fr.lirmm.graphik.graal.core;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Variable;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public abstract class AbstractMapBasedSubstitution extends AbstractSubstitution implements Substitution {

	protected abstract Map<Variable, Term> getMap();

	@Override
	public Set<Variable> getTerms() {
		return this.getMap().keySet();
	}

	@Override
	public Set<Term> getValues() {
		return new HashSet<Term>(this.getMap().values());
	}

	@Override
	public Term createImageOf(Term term) {
		Term substitut = this.getMap().get(term);
		return (substitut == null) ? term : substitut;
	}

	@Override
	public boolean put(Variable term, Term substitute) {
		Term actualSubstitute = this.getMap().get(term);
		if (actualSubstitute != null && !actualSubstitute.equals(substitute)) {
			return false;
		}
		this.getMap().put(term, substitute);
		return true;
	}

	@Override
	public boolean remove(Variable term) {
		return this.getMap().remove(term) != null;
	}

	@Override
	public boolean aggregate(Variable term, Term substitut) {
		Term termSubstitut = this.createImageOf(term);
		Term substitutSubstitut = this.createImageOf(substitut);

		if (!termSubstitut.equals(substitutSubstitut)) {
			if (termSubstitut.isConstant()) {
				if (substitutSubstitut.isConstant()) {
					return substitutSubstitut.equals(termSubstitut);
				} else {
					Term tmp = termSubstitut;
					termSubstitut = substitutSubstitut;
					substitutSubstitut = tmp;
				}
			}

			for (Variable t : this.getTerms()) {
				Term image = this.createImageOf(t);
				if (termSubstitut.equals(image) && !t.equals(substitutSubstitut)) {
					this.getMap().put(t, substitutSubstitut);
				}
			}

			this.getMap().put((Variable) termSubstitut, substitutSubstitut);
		}
		return true;
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// OVERRIDE OBJECT METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	@Override
	public int hashCode() {
		int result = 1;
		for (Map.Entry<Variable, Term> e : this.getMap().entrySet()) {
			int a = 31 * e.getKey().hashCode();
			int b = 67 * e.getValue().hashCode();
			result += a + b + (a * b);
		}
		return result;
	}

};
