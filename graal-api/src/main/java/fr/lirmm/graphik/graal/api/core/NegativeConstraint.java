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
package fr.lirmm.graphik.graal.api.core;

import java.util.Set;

import fr.lirmm.graphik.util.string.AppendableToStringBuilder;

/**
 * This interface represents a negative constraint. A negative constraint C is a conjunction of atoms
 * interpreted as the negation of its existential closure ∄X C[X] where X denotes a set of variables.
 * Equivalently, it can be seen as a rule 
 * of the form C -> ⊥, where ⊥ denotes the absurd symbol (which is always false).
 *  
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public interface NegativeConstraint extends Rule, AppendableToStringBuilder {

	/**
	 * Get the label (the name) of this constraint.
	 *  
	 * @return the label of this constraint.
	 */
	String getLabel();

	/**
	 * Set the label (the name) for this constraint.
	 * 
	 * @param label
	 */
	void setLabel(String label);

	/**
	 * Get the body (the hypothesis) of this constraint.
	 * 
	 * @return the body of this constraint.
	 */
	InMemoryAtomSet getBody();

	/**
	 * Get terms by Type.
	 * 
	 * @return a Set of all Term of the specified type related to this NegativeConstraint.  
	 */
	@Deprecated
	Set<Term> getTerms(Term.Type type);

	/**
	 * Get all terms of this constraint.
	 * 
	 * @return a Set of all Term related to this NegativeConstraint.
	 */
	Set<Term> getTerms();

}
