/**
 * 
 */
package fr.lirmm.graphik.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public final class MathUtils {

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	private MathUtils() {
	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Comput the cartesian product of the specified set with itself.
	 * input: { A, B, C }
	 * output : { (A,A), (A,B), (B,B) } 
	 * @param set
	 * @return
	 */
	public static <T> Iterable<Pair<T, T>> selfCartesianProduct(
			Iterable<T> set) {
		Collection<Pair<T, T>> pairs = new LinkedList<Pair<T, T>>();
		
		Iterator<T> it = set.iterator();
		while (it.hasNext()) {
			T a = it.next();
			for (T b : set) {
				pairs.add(new ImmutablePair<T, T>(a, b));
			}

			if (it.hasNext()) { //  FIX for singleton implementation
				it.remove();
			}
		}
		return pairs;
	}

	// /////////////////////////////////////////////////////////////////////////
	// OBJECT OVERRIDE METHODS
	// /////////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

}
