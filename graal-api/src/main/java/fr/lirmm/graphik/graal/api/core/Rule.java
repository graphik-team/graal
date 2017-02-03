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
 package fr.lirmm.graphik.graal.api.core;

import java.util.Set;

import fr.lirmm.graphik.util.string.AppendableToStringBuilder;

/**
 * This interface represents an existential rule. An existential rule is a positive rule of the form B -> H,
 * where B and H are conjunctions of atoms; it is interpreted as the formula ∀X(∃Y B[X,Y] -> ∃Z H[X,Z]), or equivalently
 * ∀X∀Y(B[X,Y] -> ∃Z H[X,Z]), where X are the variables shared by B and H, Y are the variables that occur only in B and Z
 * the ones that occur only in H. Note that Z are existentially quantified.
 *  
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 */
public interface Rule extends Comparable<Rule>, AppendableToStringBuilder {

	/**
	 * Get the label (the name) of this rule.
	 * 
	 * @return the label of this rule.
	 */
	String getLabel();

	/**
	 * Set the label (the name) of this rule.
	 * 
	 * @param label
	 */
	void setLabel(String label);

	/**
	 * Get the body (the hypothesis) of this rule.
	 * 
	 * @return the body of this rule.
	 */
	InMemoryAtomSet getBody();

	/**
	 * Get the head (the conclusion) of this rule.
	 * 
	 * @return the head of this rule.
	 */
	InMemoryAtomSet getHead();

	/**
	 * Compute and return the set of frontier variables of this rule.
	 * 
	 * @return a Set containing the frontier variables of this rule.
	 */
	Set<Variable> getFrontier();

	/**
	 * Compute and return the set of existential variables of this rule.
	 * 
	 * @return a Set containing the existential variables of this rule.
	 */
	Set<Variable> getExistentials();

	/**
	 * Get terms by Type.
	 * 
	 * @return a Set of all Term of the specified type related to this Rule.  
	 */
	@Deprecated
	Set<Term> getTerms(Term.Type type);

	/**
	 * Get all variables of this rule.
	 * 
	 * @return a Set of all variables related to this Rule.
	 */
	Set<Variable> getVariables();
	
	/**
	 * Get all constants of this rule.
	 * 
	 * @return a Set of all constants related to this Rule.
	 */
	Set<Constant> getConstants();
	
	/**
	 * Get all literals of this rule.
	 * 
	 * @return a Set of all literals related to this Rule.
	 */
	Set<Literal> getLiterals();
	
	/**
	 * Get all terms of this rule.
	 * 
	 * @return a Set of all Term related to this Rule.
	 */
	Set<Term> getTerms();

};
