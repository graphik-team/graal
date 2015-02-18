package fr.lirmm.graphik.graal.core;

import java.util.Collection;
import java.util.Set;

import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.InMemoryAtomSet;

/**
 * A Rule is a pair (B,H) of atom set such as "B -> H".
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 */
public interface Rule extends Comparable<Rule> {

	/**
	 * The label (the name) for this rule.
	 * 
	 * @return
	 */
	String getLabel();

	/**
	 * @param label
	 */
	void setLabel(String label);

	/**
	 * The body (the hypothesis) of the rule.
	 * 
	 * @return
	 */
	InMemoryAtomSet getBody();

	/**
	 * The head (the conclusion) of the rule.
	 * 
	 * @return
	 */
	InMemoryAtomSet getHead();

	/**
	 * Compute and return the set of frontier variables of the rule.
	 * 
	 * @return
	 */
	Set<Term> getFrontier();

	/**
	 * Compute and return the set of existential variables of the rule.
	 * 
	 * @return
	 */
	Set<Term> getExistentials();

	/**
	 * 
	 * @return
	 */
	Set<Term> getTerms(Term.Type type);

	/**
	 * 
	 * @return
	 */
	Set<Term> getTerms();

	/**
	 * Compute and return the set of pieces of the head according to the
	 * frontier. On Rules with Existential Variables: Walking the Decidability
	 * Line Jean-François Baget, Michel Leclère, Marie-Laure Mugnier, Eric
	 * Salvat
	 * 
	 * @return
	 */
	Collection<AtomSet> getPieces();
	
	void appendTo(StringBuilder sb);

};
