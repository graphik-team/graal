/**
 * 
 */
package fr.lirmm.graphik.graal.io;

import java.io.IOException;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class WriterException extends IOException {

	private static final long serialVersionUID = -1719359432056325781L;

	/**
	 * @param message
	 */
	public WriterException(String message) {
		super(message);
	}

}
