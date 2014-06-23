/**
 * 
 */
package fr.lirmm.graphik.alaska.solver;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class SolverException extends Exception {

	private static final long serialVersionUID = -961880040919174316L;

	/**
     * @param message
     * @param e
     */
    public SolverException(String message, Exception e) {
        super(message, e);
    }

}
