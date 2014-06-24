package fr.lirmm.graphik.graal.core;

import java.util.Set;

public interface Rule {
	
	/**
	 * @return a label (a name) for this rule.
	 */
	String getLabel();

	AtomSet getBody();

	AtomSet getHead();

	Set<Term> getFrontier();

	/**
	 * @return
	 */
	Set<Term> getExistentials();
};
