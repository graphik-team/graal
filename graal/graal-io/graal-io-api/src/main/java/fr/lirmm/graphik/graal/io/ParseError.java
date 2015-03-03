/**
 * 
 */
package fr.lirmm.graphik.graal.io;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class ParseError extends Error {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6399295619031902779L;

	public ParseError(String msg, Throwable t) {
		super(msg,t);
	}
}
