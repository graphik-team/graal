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

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;

import fr.lirmm.graphik.graal.api.core.AbstractAtom;
import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.Constant;
import fr.lirmm.graphik.graal.api.core.Literal;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.util.ArrayIterator;

/**
 * Class that implements atoms.
 */
public class DefaultAtom extends AbstractAtom implements Serializable {

	private static final long serialVersionUID = -5889218407173357933L;

	private Predicate predicate;
	private Term[]            terms;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	public DefaultAtom(Predicate predicate) {
		this.predicate = predicate;
		int n = predicate.getArity();
		this.terms = new Term[n];
	}

	public DefaultAtom(Predicate predicate, List<Term> terms) {
		this.predicate = predicate;
		int n = predicate.getArity();
		this.terms = new Term[n];
		int i = 0;
		for (Term t : terms) {
			this.terms[i++] = t;
			if (i == n)
				break;
		}
	}
	
	public DefaultAtom(Predicate predicate, Term... terms) {
		this.predicate = predicate;
		this.terms = terms;
	}

	/**
	 * @param atom
	 */
	public DefaultAtom(Atom atom) {
		this(atom.getPredicate(), atom.getTerms());
	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	@Deprecated
	public Collection<Term> getTerms(Term.Type type) {
		Collection<Term> typedTerms = new LinkedList<Term>();
		for (Term term : this.terms)
			if (type.equals(term.getType()))
				typedTerms.add(term);

		return typedTerms;
	}
	
	@Override
	public Set<Constant> getConstants() {
		Set<Constant> typedTerms = new HashSet<Constant>();
		for (Term term : this.terms)
			if (term.isConstant())
				typedTerms.add((Constant) term);

		return typedTerms;
	}
	
	@Override
	public Set<Variable> getVariables() {
		Set<Variable> typedTerms = new HashSet<Variable>();
		for (Term term : this.terms)
			if (term.isVariable())
				typedTerms.add((Variable)term);

		return typedTerms;
	}
	
	@Override
	public Set<Literal> getLiterals() {
		Set<Literal> typedTerms = new HashSet<Literal>();
		for (Term term : this.terms)
			if (term.isLiteral())
				typedTerms.add((Literal)term);

		return typedTerms;
	}

	@Override
	public boolean contains(Term term) {
		return this.indexOf(term) >= 0;
	}
	
	@Override
	public int indexOf(Term t) {
		return ArrayUtils.indexOf(terms, t);
	}
	
	@Override
	public int[] indexesOf(Term term) {
		int[] result = null;
		int resultCounter = 0;
		int termsSize = terms.length;
		for (int i = 0; i < termsSize; i++) {
			if (terms[i].equals(term)) {
				resultCounter++;
			}
		}
		result = new int[resultCounter];
		if (resultCounter != 0) {
			int pos = 0;
			for (int i = 0; i < termsSize; i++) {
				if (terms[i].equals(term)) {
					result[pos] = i;
					pos++;
				}
			}
		}
		return result;
	}

	// /////////////////////////////////////////////////////////////////////////
	// GETTERS/SETTERS
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Set the predicate.
	 */
	@Override
	public void setPredicate(Predicate predicate) {
		this.predicate = predicate;
	}

	/**
	 * Get the predicate.
	 */
	@Override
	public Predicate getPredicate() {
		return predicate;
	}

	/**
	 * Set the Term with the specified index.
	 */
	@Override
	public void setTerm(int index, Term term) {
		this.terms[index] = term;

	}

	/**
	 * Returns the Term with the specified index.
	 */
	@Override
	public Term getTerm(int index) {
		return this.terms[index];
	}

	/**
	 * Set the List of Term.
	 * 
	 * @param terms
	 */
	public void setTerms(List<Term> terms) {
		int i = 0;
		for (Term t : terms) {
			this.terms[i++] = t;
			if (i == this.terms.length)
				break;
		}
	}

	/**
	 * Returns the List of Term.
	 */
	@Override
	public List<Term> getTerms() {
		return Arrays.asList(this.terms);
	}

	@Override
	public Iterator<Term> iterator() {
		return new ArrayIterator<Term>(terms);
	}
	
};
