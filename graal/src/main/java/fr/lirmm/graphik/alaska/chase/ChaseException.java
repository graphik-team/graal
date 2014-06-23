/**
 * 
 */
package fr.lirmm.graphik.alaska.chase;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class ChaseException extends Exception {

	private static final long serialVersionUID = -8123025266971025431L;

	/**
	 * @param message
	 * @param e
	 */
	public ChaseException(String message, Exception e) {
		super(message, e);
	}

}
