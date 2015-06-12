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
