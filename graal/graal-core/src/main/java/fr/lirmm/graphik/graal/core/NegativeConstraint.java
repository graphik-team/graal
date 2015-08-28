/**
 * 
 */
package fr.lirmm.graphik.graal.core;

import java.util.Set;

import fr.lirmm.graphik.graal.core.atomset.InMemoryAtomSet;
import fr.lirmm.graphik.graal.core.term.Term;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public interface NegativeConstraint {

	/**
	 * Get the label (the name) for this constraint.
	 * 
	 * @return
	 */
	String getLabel();

	/**
	 * Set the label (the name) for this constraint.
	 * 
	 * @param label
	 */
	void setLabel(String label);

	/**
	 * Get the body (the hypothesis) of this constraint.
	 * 
	 * @return
	 */
	InMemoryAtomSet getBody();

	/**
	 * Get terms by Type.
	 * 
	 * @return
	 */
	Set<Term> getTerms(Term.Type type);

	/**
	 * Get all terms of this constraint.
	 * 
	 * @return
	 */
	Set<Term> getTerms();

	void appendTo(StringBuilder sb);
}
