/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2015)
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

import java.util.Set;

import fr.lirmm.graphik.graal.core.atomset.InMemoryAtomSet;
import fr.lirmm.graphik.graal.core.term.Term;
import fr.lirmm.graphik.util.string.AppendableToStringBuilder;

/**
 * This interface represents an existential rule.
 * A Rule is a pair (B,H) of atom set such as "B -> H".
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 */
public interface Rule extends Comparable<Rule>, AppendableToStringBuilder {

	/**
	 * Get the label (the name) for this rule.
	 * 
	 * @return
	 */
	String getLabel();

	/**
	 * Set the label (the name) for this rule.
	 * 
	 * @param label
	 */
	void setLabel(String label);

	/**
	 * Get the body (the hypothesis) of this rule.
	 * 
	 * @return
	 */
	InMemoryAtomSet getBody();

	/**
	 * Get the head (the conclusion) of this rule.
	 * 
	 * @return
	 */
	InMemoryAtomSet getHead();

	/**
	 * Compute and return the set of frontier variables of this rule.
	 * 
	 * @return
	 */
	Set<Term> getFrontier();

	/**
	 * Compute and return the set of existential variables of this rule.
	 * 
	 * @return
	 */
	Set<Term> getExistentials();

	/**
	 * Get terms by Type.
	 * 
	 * @return
	 */
	Set<Term> getTerms(Term.Type type);

	/**
	 * Get all terms of this rule.
	 * 
	 * @return
	 */
	Set<Term> getTerms();

};
