/**
 * 
 */
package fr.lirmm.graphik.graal.apps;

import java.io.FileNotFoundException;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class PureException extends Exception {
	
	private static final long serialVersionUID = -1997725285866124335L;

	/**
	 * @param message
	 */
	PureException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param e
	 */
	public PureException(String message, FileNotFoundException e) {
		super(message, e);
	}

}
