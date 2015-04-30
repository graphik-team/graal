/**
 * 
 */
package fr.lirmm.graphik.graal.core;

import java.io.Serializable;
import java.util.Comparator;

/**
 * 
 * This class implements a comparator of Term that doesn't make difference
 * on Term Type.
 *
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class TermValueComparator implements Comparator<Term>, Serializable {

	private static final long serialVersionUID = -4231328676676157296L;

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Term term0, Term term1) { // TODO are you sure?
		return term0.getIdentifier().toString().compareTo(term1.getIdentifier().toString());
	}
};

