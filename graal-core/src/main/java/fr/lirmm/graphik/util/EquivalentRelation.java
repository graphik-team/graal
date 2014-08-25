/**
 * 
 */
package fr.lirmm.graphik.util;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public interface EquivalentRelation<T> {

	public boolean compare(T o1, T o2);

	/**
	 * @param elements
	 * @return the affected class id
	 */
	int addClasse(Iterable<T> elements);

	/**
	 * @param elements
	 * @return the affected class id
	 */
	int addClasse(T[] elements);

	/**
	 * @param o1
	 * @param o2
	 */
	void mergeClasses(T o1, T o2);
	
	int getIdClass(T o);

}
