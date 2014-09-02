package fr.lirmm.graphik.graal.core;

/**
 * Represents a generic query.
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
public interface Query {

	/**
	 * @return true if the expected answer is boolean, false otherwise.
	 */
	public boolean isBoolean();

};
