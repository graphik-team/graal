/**
 * 
 */
package fr.lirmm.graphik.util.stream.transformator;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public interface Transformator<U, T> {

	/**
	 * Transform an instance of U into an instance of T.
	 * 
	 * @param u
	 *            the instance to transform.
	 * @return an instance of T.
	 */
	T transform(U u);

}
