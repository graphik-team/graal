/* Graal v0.7.4
 * Copyright (c) 2014-2015 Inria Sophia Antipolis - Méditerranée / LIRMM (Université de Montpellier & CNRS)
 * All rights reserved.
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * Author(s): Clément SIPIETER
 *            Mélanie KÖNIG
 *            Swan ROCHER
 *            Jean-François BAGET
 *            Michel LECLÈRE
 *            Marie-Laure MUGNIER
 */
 package fr.lirmm.graphik.graal.core;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import fr.lirmm.graphik.graal.core.impl.DefaultAtom;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.core.term.Term;
import fr.lirmm.graphik.graal.core.term.Term.Type;
import fr.lirmm.graphik.util.string.AppendableToStringBuilder;

/**
 * This interface represents a logical atom like p(X,Y).
 */
public interface Atom extends Comparable<Atom>, Iterable<Term>, AppendableToStringBuilder {

	/**
	 * This instance of Atom represents Bottom, it is always interpreted as
	 * false.
	 */
	public static final Atom BOTTOM = new DefaultAtom(
Predicate.BOTTOM,
			DefaultTermFactory.instance().createVariable("X"));
	public static final Atom TOP = new DefaultAtom(Predicate.TOP,
			DefaultTermFactory.instance().createVariable("X"));


	/**
	 * Set the Predicate of this Atom.
	 * 
	 * @param predicate
	 */
	void setPredicate(Predicate predicate);

	/**
	 * Get the Predicate of this Atom.
	 * 
	 * @return
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
	 * @return
	 */
	Term getTerm(int index);

	/**
	 * Get an ordered List that represents the terms of this Atom.
	 * 
	 * @return
	 */
	List<Term> getTerms();

	/**
	 * Get all Term of the specified type.
	 * 
	 * @param type
	 * @return
	 */
	Collection<Term> getTerms(Type type);

	/**
	 * Return an Iterator of Term.
	 */
	@Override
	Iterator<Term> iterator();

};
