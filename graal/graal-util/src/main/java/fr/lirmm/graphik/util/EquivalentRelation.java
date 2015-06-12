/* Graal v0.7.4
 * Copyright (c) 2014-2015 Inria Sophia Antipolis - Méditerranée / LIRMM (Université de Montpellier & CNRS)
 * All rights reserved.
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * Author(s): Clément SIPIETER
 *            Mélanie KÖNIG
 *            Swan ROCHER
 *            Jean-François BAGET
 *            Michel LECLÈRE
 *            Marie-Laure MUGNIER
 */
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
	int addClasse(T... elements);

	/**
	 * @param o1
	 * @param o2
	 */
	void mergeClasses(T o1, T o2);
	
	int getIdClass(T o);

}
