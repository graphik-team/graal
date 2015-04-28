/**
 * 
 */
package fr.lirmm.graphik.util;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class MethodNotImplementedError extends Error {

	private static final long serialVersionUID = 2844152323438292015L;

	public MethodNotImplementedError() {
		super("This method isn't implemented");
	}
	/**
	 * @param string
	 */
	public MethodNotImplementedError(String message) {
		super(message);
	}

}
