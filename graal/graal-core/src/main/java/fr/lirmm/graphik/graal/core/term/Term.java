/**
 * 
 */
package fr.lirmm.graphik.graal.core.term;

import java.io.Serializable;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public interface Term extends Comparable<Term>, Serializable {

	/**
	 * The enumeration of term types.
	 */
	public static enum Type {
		CONSTANT, VARIABLE, LITERAL
	}

	boolean isConstant();

	String getLabel();

	Object getIdentifier();

	Type getType();


}
