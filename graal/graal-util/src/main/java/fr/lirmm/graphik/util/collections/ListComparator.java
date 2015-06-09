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
package fr.lirmm.graphik.util.collections;

import java.util.Comparator;
import java.util.List;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class ListComparator<E extends Comparable<E>> implements
		Comparator<List<E>> {

	@Override
	public int compare(List<E> o1, List<E> o2) {
		int cmp = o1.size() - o2.size();
		if (cmp == 0) {
			int i = 0;
			while (i < o1.size() && cmp == 0) {
				cmp = o1.get(i).compareTo(o2.get(i));
				++i;
			}
		}
		return cmp;
	}

}
