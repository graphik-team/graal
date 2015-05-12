package fr.lirmm.graphik.graal.core;

import java.util.Set;

import fr.lirmm.graphik.graal.core.atomset.InMemoryAtomSet;
import fr.lirmm.graphik.graal.core.term.Term;

/**
 * A substitution represents a set of transformation of a variable into a term.
 * To apply a substitution to a logical expression replace each variable symbols
 * by its substitute.
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 */
public interface Substitution {

	/** 
	 * Get all terms that have a substitute.
	 * @return
	 */
	Set<Term> getTerms();
	
	/**
	 * Get all substitutes of this substitution.
	 * @return
	 */
	Set<Term> getValues();

	/**
	 * Get the substitute of the given term.
	 * @param term
	 * @return the substitute.
	 */
	Term getSubstitute(Term term);

	/**
	 * Add a term substitution.
	 * @param term the term to substitute.
	 * @param substitut its substitute.
	 * @return false if a constant term is substituted by another constant term,
	 *         true otherwise.
	 */
	boolean put(Term term, Term substitut);

	/**
	 * Add all term substitution of an other substitution instance.
	 * @param s
	 */
	void put(Substitution s);

	/**
	 * Apply this substitution on an atom.
	 * @param atom (const)
	 * @return
	 */
	Atom getSubstitut(Atom atom);

	/**
	 * Apply this substitution on an atom set.
	 * @param src (const)
	 * @return
	 */
	InMemoryAtomSet getSubstitut(InMemoryAtomSet src);
	
	/**
	 * Apply this substitution on the given rule.
	 * @param rule (const)
	 * @return
	 */
	Rule getSubstitut(Rule rule);

	/**
	 * Insert the application of this substitution on the src atom set into the
	 * target atom set.
	 * 
	 * @param src (const)
	 * @param dest
	 */
	void substitut(InMemoryAtomSet src, InMemoryAtomSet target);

	/**
	 * The composition of a substitution is more complex that just put an other
	 * term substitution. If the current substitution is {X -> Y} and you add
	 * a term substitution {Y -> 'a'}, the result is {X -> 'a', Y -> 'a'}.
	 * 
	 * An other example is {X -> 'a'} + {X -> Y} => {Y -> 'a', X -> 'a'}.
	 * 
	 * @param term
	 * @param substitut
	 * @return false if the composition imply a substitution of a constant term
	 *         by another constant term, true otherwise.
	 * @throws SubstitutionException
	 */
	boolean compose(Term term, Term substitut);

	/**
	 * (const)
	 * 
	 * @param s
	 * @return null if the composition imply a substitution of a constant term
	 *         by another constant term, the result of the composition
	 *         otherwise.
	 * @throws SubstitutionException
	 */
	Substitution compose(Substitution s);

};
