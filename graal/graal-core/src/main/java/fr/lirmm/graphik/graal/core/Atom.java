package fr.lirmm.graphik.graal.core;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.core.term.Term;
import fr.lirmm.graphik.graal.core.term.Term.Type;

/**
 * This interface represents a logical atom like p(X,Y).
 */
public interface Atom extends Comparable<Atom>, Iterable<Term> {

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
	 * Set the n<sup>th</sup> term of this Atom.
	 * 
	 * @param index
	 * @param term
	 */
	void setTerm(int index, Term term);

	/**
	 * get the n<sup>th</sup> term of this Atom.
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
	 * Get all Term of Type type.
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
