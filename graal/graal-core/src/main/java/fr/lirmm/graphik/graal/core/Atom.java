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
	static final Atom BOTTOM = new DefaultAtom(Predicate.BOTTOM,
			DefaultTermFactory.instance().createVariable("X"));
	static final Atom TOP = new DefaultAtom(Predicate.TOP,
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
