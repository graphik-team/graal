/**
 * 
 */
package fr.lirmm.graphik.graal.parser;

import java.io.IOException;


/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class ParseException extends IOException {

	private static final long serialVersionUID = -4455111019098315998L;
	
	/**
	 * @param message
	 * @param e
	 */
	public ParseException(String message, Throwable e) {
		super(message, e);
	}

	/**
	 * @param message
	 */
	public ParseException(String message) {
		super(message);
	}

}
