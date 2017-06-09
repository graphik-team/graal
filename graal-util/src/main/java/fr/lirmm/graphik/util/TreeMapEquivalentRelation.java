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
 /**
 * 
 */
package fr.lirmm.graphik.util;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class TreeMapEquivalentRelation<T extends Comparable<T>> implements EquivalentRelation<T>{

	private Integer maxClassesValue = 0;
	private final TreeMap<T, Integer> classes = new TreeMap<T, Integer>();
	
	@Override
	public int addClasse(Iterable<T> elements) {
		Integer classe = ++maxClassesValue;
		for(T e : elements)
			this.classes.put(e, classe);
		return classe;
	}
	
	@SafeVarargs
	@Override
	public final int addClasse(T... elements) {
		Integer classe = ++maxClassesValue;
		for(T e : elements)
			this.classes.put(e, classe);
		return classe;
	}
	
	@Override
	public void mergeClasses(T o1, T o2) {
		Integer c1 = this.classes.get(o1);
		Integer c2 = this.classes.get(o2);
		if(c1 == null && c2 == null) {
			this.addClasse(o1, o2);
		} else if (c1 == null) {
			this.classes.put(o1, c2);
		} else if (c2 == null) {
			this.classes.put(o2, c1);
		} else {
			for (Map.Entry<T, Integer> e : this.classes.entrySet()) {
				if (e.getValue().equals(c1)) {
					this.classes.put(e.getKey(), c2);
				}
			}
		}
	}
	
	@Override
	public boolean compare(T o1, T o2) {
		boolean res = o1 == o2; // NOPMD
		if(!res) {
			Integer c1 = this.classes.get(o1);
			Integer c2 = this.classes.get(o2);
			if(c1 != null && c2 != null) {
				res = c1.equals(c2);
			}
		}
		return res;
	}

	@Override
	public int getIdClass(T o) {
		Integer id = this.classes.get(o);
		if(id == null)
			id = this.addClasse(o);
		return id;
		
	}

}
