/**
 * 
 */
package fr.lirmm.graphik.util.stream;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public interface Filter<E> {

	boolean filter(E e);
}
