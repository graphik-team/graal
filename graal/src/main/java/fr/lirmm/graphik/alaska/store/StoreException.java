/**
 * 
 */
package fr.lirmm.graphik.alaska.store;

import fr.lirmm.graphik.kb.exception.AtomSetException;


/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class StoreException extends AtomSetException {

	private static final long serialVersionUID = -5979052600210288338L;
	
	/**
	 * @param message
	 */
	public StoreException(String message) {
		super(message);
	}
	
	/**
	 * @param message
	 * @param t
	 */
	public StoreException(String message, Throwable t) {
		super(message, t);
	}
	
	/**
	 * @param e
	 */
	public StoreException(Exception e) {
		super(e.getMessage(), e);
	}

}
