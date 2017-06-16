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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import fr.lirmm.graphik.util.string.AppendableToStringBuilder;

/**
 * This interface represents a logical atom. An <em>atom</em> is of the form p(e<sub>1</sub>…e<sub>k</sub>)
 * where <em>p</em> is a predicate, <em>k</em> >= 1 the arity of <em>p</em>, and each e<sub>i</sub> is a term.
 * 
 * @author Clément Sipieter (INRIA/CNRS) {@literal <clement@6pi.fr>}
 */
public interface Atom extends Comparable<Atom>, Iterable<Term>, AppendableToStringBuilder {

	/**
	 * Set the Predicate of this Atom.
	 * 
	 * @param predicate
	 */
	void setPredicate(Predicate predicate);

	/**
	 * Get the Predicate of this Atom.
	 * 
	 * @return the predicate of this atom.
	 */
	Predicate getPredicate();

	/**
	 * Set the n<sup>th</sup> term of this Atom. The first index is 0.
	 * 
	 * @param index
	 * @param term
	 */
	void setTerm(int index, Term term);

	/**
	 * Get the n<sup>th</sup> term of this Atom. The first index is 0.
	 * 
	 * @param index
	 * @return the n<sup>th</sup> term of this Atom.
	 */
	Term getTerm(int index);

	/**
	 * Returns the index of the first occurrence of the specified term in the
	 * term list of this atom, or -1 if this atom does not contain the term.
	 * 
	 * @param t
	 *            term to search for
	 * @return the index of the first occurrence of the specified term.
	 */
	int indexOf(Term t);

	/**
	 * Returns the indexes of a given term in the atom.
	 *
	 * @param term
	 * @return
	 */
	int[] indexesOf(Term term);
	 
	/**
	 * Returns true if the term list contains the specified term.
	 * 
	 * @param t
	 * @return true if the term list contains the specified term, false otherwise.
	 */
	boolean contains(Term t);

	/**
	 * Get an ordered List that represents the terms of this Atom.
	 * 
	 * @return an ordered List that represents the terms of this Atom.
	 */
	List<Term> getTerms();

	/**
	 * This method is deprecated since 1.3, use {@link #getConstants()} and {@link #getVariables()} instead. <br>
	 * <br>
	 * 
	 * Get all Term of the specified type.
	 * 
	 * @param type
	 * @return all Term of the specified type.
	 */
	@Deprecated
	Collection<Term> getTerms(Term.Type type);
	
	/**
	 * Get all variables that appear is this atom.
	 * @return all variables that appear is this atom.
	 */
	Set<Variable> getVariables();
	
	/**
	 * Get all constants that appear is this atom.
	 * @return all constants that appear is this atom.
	 */
	Set<Constant> getConstants();
	
	/**
	 * Get all literals that appear is this atom.
	 * @return all literals that appear is this atom.
	 */
	Set<Literal> getLiterals();
	
	/**
	 * Return an Iterator of Term.
	 */
	@Override
	Iterator<Term> iterator();

};
