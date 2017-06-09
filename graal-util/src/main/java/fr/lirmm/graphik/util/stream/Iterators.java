/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2017)
 *
 * Contributors :
 *
 * Clément SIPIETER <clement.sipieter@inria.fr>
 * Mélanie KÖNIG
 * Swan ROCHER
 * Jean-François BAGET
 * Michel LECLÈRE
 * Marie-Laure MUGNIER <mugnier@lirmm.fr>
 *
 *
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
package fr.lirmm.graphik.util.stream;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public final class Iterators {

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	private Iterators() {
	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Construct and return an empty iterator. The hasNext method will always
	 * return false and the next method will always return null.
	 * 
	 * @return An instance of a {@link CloseableIteratorWithoutException}
	 */
	public static <T> CloseableIteratorWithoutException<T> emptyIterator() {
		return new EmptyCloseableIteratorWithoutException<T>();
	}

	public static <E> CloseableIteratorWithoutException<E> singletonIterator(E e) {
		return new SingletonCloseableIteratorWithoutException<E>(e);
	}

	public static int count(CloseableIterator<?> it) throws IteratorException {
		int i = 0;
		while (it.hasNext()) {
			++i;
			it.next();
		}
		return i;
	}

	public static int count(CloseableIteratorWithoutException<?> it) {
		try {
			return Iterators.count((CloseableIterator<?>) it);
		} catch (IteratorException e) {
			throw new Error("Should never happen");
		}
	}

	/**
	 * Remove adjacent equals elements.
	 * 
	 * @param it
	 * @return a CloseableIterator.
	 */
	public static <T> CloseableIterator<T> uniqLocaly(CloseableIterator<T> it) {
		return new UniqIterator<T>(it);
	}

	/**
	 * Return an iterator over sorted elements from the specified iterator.
	 * 
	 * @param it
	 * @return a sorted CloseableIterator over elements from the specified Iterator.
	 * @throws IteratorException
	 */
	public static <T extends Comparable<T>> CloseableIterator<T> sort(CloseableIterator<T> it)
	    throws IteratorException {
		List<T> substitutionList = new LinkedList<T>();
		while (it.hasNext()) {
			substitutionList.add(it.next());
		}
		it.close();
		Collections.sort(substitutionList);
		return new CloseableIteratorAdapter<T>(substitutionList.iterator());
	}

	/**
	 * Return an iterator over sorted elements from the specified iterator.
	 * 
	 * @param it
	 * @return a sorted Iterator over elements from the specified Iterator.
	 */
	public static <T extends Comparable<T>> Iterator<T> sort(Iterator<T> it) {
		List<T> substitutionList = new LinkedList<T>();
		while (it.hasNext()) {
			substitutionList.add(it.next());
		}
		Collections.sort(substitutionList);
		return substitutionList.iterator();
	}

	/**
	 * Remove all equals elements.
	 * 
	 * @param it
	 * @return a CloseableIterator over elements from the specified it without duplicate.
	 * @throws IteratorException
	 */
	public static <T extends Comparable<T>> CloseableIterator<T> uniq(CloseableIterator<T> it)
	    throws IteratorException {
		Set<T> substitutionSet = new TreeSet<T>();
		while (it.hasNext()) {
			substitutionSet.add(it.next());
		}
		it.close();
		return new CloseableIteratorAdapter<T>(substitutionSet.iterator());
	}

	/**
	 * Remove all equals elements.
	 * 
	 * @param it
	 * @return an Iterator over elements from the specified it without duplicate.
	 */
	public static <T extends Comparable<T>> Iterator<T> uniq(Iterator<T> it) {
		Set<T> substitutionSet = new TreeSet<T>();
		while (it.hasNext()) {
			substitutionSet.add(it.next());
		}
		return substitutionSet.iterator();
	}

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE CLASSES
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * @param it
	 * @return a List containing all elements accessible by the specified iterator.
	 * @throws IteratorException 
	 */
	public static <T> List<T> toList(CloseableIterator<T> it) throws IteratorException {
		List<T> list = new LinkedList<T>();
		while(it.hasNext()) {
			list.add(it.next());
		}
		it.close();
		return list;
	}
	
	/**
	 * @param it
	 * @return a List containing all elements accessible by the specified iterator.

	 */
	public static <T> List<T> toList(CloseableIteratorWithoutException<T> it) {
		List<T> list = new LinkedList<T>();
		while(it.hasNext()) {
			list.add(it.next());
		}
		it.close();
		return list;
	}
	
	
	/**
	 * @param it
	 * @return a Set containing all elements accessible by the specified iterator.
	 * @throws IteratorException 
	 */
	public static <T> Set<T> toSet(CloseableIterator<T> it) throws IteratorException {
		Set<T> list = new HashSet<T>();
		while(it.hasNext()) {
			list.add(it.next());
		}
		it.close();
		return list;
	}
	
	/**
	 * @param it
	 * @return a Set containing all elements accessible by the specified iterator.
	 */
	public static <T> Set<T> toSet(CloseableIteratorWithoutException<T> it) {
		Set<T> list = new HashSet<T>();
		while(it.hasNext()) {
			list.add(it.next());
		}
		it.close();
		return list;
	}


}
