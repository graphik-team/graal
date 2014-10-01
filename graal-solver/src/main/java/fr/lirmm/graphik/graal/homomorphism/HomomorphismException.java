/**
 * 
 */
package fr.lirmm.graphik.graal.homomorphism;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class HomomorphismException extends Exception {

	private static final long serialVersionUID = -961880040919174316L;

	/**
     * @param message
     * @param e
     */
    public HomomorphismException(String message, Exception e) {
        super(message, e);
    }

}
