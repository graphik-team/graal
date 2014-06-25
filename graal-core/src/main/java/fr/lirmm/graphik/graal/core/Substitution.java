package fr.lirmm.graphik.graal.core;

import java.util.Collection;
import java.util.Set;

import fr.lirmm.graphik.graal.core.atomset.AtomSet;

public interface Substitution {

	Set<Term> getTerms();

	Term getSubstitut(Term term);

	/**
	 * 
	 * @param term
	 * @param substitut
	 * @return false if a constant term is substituted by another constant term,
	 *         true otherwise.
	 * @throws SubstitutionException
	 */
	boolean put(Term term, Term substitut);

	void put(Substitution s);

	Atom getSubstitut(Atom atom);

	/**
	 * @return
	 */
	Set<Term> getValues();

	/**
	 * @param src
	 * @return
	 */
	AtomSet getSubstitut(AtomSet src);

	/**
	 * Insert subsitution of src in dest
	 * 
	 * @param src
	 * @param dest
	 */
	void substitut(AtomSet src, AtomSet dest);

	/**
	 * @param rule
	 * @return
	 */
	Rule getSubstitut(Rule rule);

	/**
	 * 
	 * @param term
	 * @param substitut
	 * @return false if the composition imply a substitution of a constant term
	 *         by another constant term, true otherwise.
	 * @throws SubstitutionException
	 */
	boolean compose(Term term, Term substitut);

	/**
	 * Const method
	 * 
	 * 
	 * 
	 * @param s
	 * @return null if the composition imply a substitution of a constant term
	 *         by another constant term, the result of the composition otherwise.
	 * @throws SubstitutionException
	 */
	Substitution compose(Substitution s);

};
