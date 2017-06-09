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
 package fr.lirmm.graphik.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * A partition of terms. A partition can be associated with a substition : two
 * terms are in the same class if one is the image of the other ex: the
 * partition associated with {(a,b)(c,b)(d,e)(e,f)} is {{a,b,c}{d,e,f}}
 */

public class Partition<E> implements Iterable<ArrayList<E>> {

	protected List<ArrayList<E>> partition;

	// //////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// //////////////////////////////////////////////////////////////////////////

	public Partition() {
		this.partition = new ArrayList<ArrayList<E>>();
	}

	/**
	 * Copy constructor
	 * 
	 * @param partition
	 */
	public Partition(Partition<E> partition) {
		this();
		for (Collection<E> classs : partition) {
			this.partition.add(new ArrayList<E>(classs));
		}
	}

	/**
	 * Create a partition based on the position of elements in the two lists.
	 * 
	 * @param list1
	 * @param list2
	 */
	public Partition(List<E> list1, List<E> list2) {
		this();
		Iterator<E> it1 = list1.iterator();
		Iterator<E> it2 = list2.iterator();

		while (it1.hasNext()) {
			E e1 = it1.next();
			if (it2.hasNext()) {
				this.add(e1, it2.next());
			}
		}
	}

	// //////////////////////////////////////////////////////////////////////////
	// METHODS
	// //////////////////////////////////////////////////////////////////////////

	public E getRepresentant(E e) {
		for (List<E> classs : this) {
			if (classs.contains(e)) {
				return classs.get(0);
			}
		}
		return e;
	}

	public ArrayList<E> getClass(E e) {
		for (ArrayList<E> classs : this) {
			if (classs.contains(e)) {
				return classs;
			}
		}
		return null;
	}

	/**
	 * Add the couple to the partition
	 */
	public void add(E t, E im) {
		if (!t.equals(im)) {
			ArrayList<E> tset = null;
			ArrayList<E> imset = null;
			// we look for the equivalence set of t and im if exists
			Iterator<ArrayList<E>> ip = partition.iterator();
			while ((tset == null || imset == null) && ip.hasNext()) {
				ArrayList<E> s = ip.next();
				Iterator<E> is = s.iterator();
				while ((tset == null || imset == null) && is.hasNext()) {
					E e = is.next();
					if (e.equals(t))
						tset = s;
					if (e.equals(im))
						imset = s;
				}
			}
			// im and t have not equivalence set so we create a new one for its
			if (tset == null && imset == null) {
				ArrayList<E> s = new ArrayList<E>();
				s.add(t);
				s.add(im);
				partition.add(s);
			}
			// im has not an equivalence set but t has one so we add im in t's
			// equivalence set
			else if (imset == null) {
				tset.add(im);
			}
			// t has not an equivalence set but im has one so we add t in im's
			// equivalence set
			else if (tset == null) {
				imset.add(t);
			}
			// t and im have different equivalence sets so we append the two
			// equivalence set
			else if (tset != imset) {
				tset.addAll(imset);
				partition.remove(imset);
			}
		}
	}

	/**
	 * @param toAdd
	 *            (const)
	 */
	public void addClass(ArrayList<E> toAdd) {
		Iterator<ArrayList<E>> i = partition.iterator();
		ArrayList<E> fusion = null;
		while (i.hasNext()) {
			ArrayList<E> cl = i.next();
			Iterator<E> it = cl.iterator();
			boolean contain = false;
			while (!contain && it.hasNext()) {
				E t = it.next();
				if (toAdd.contains(t)) {
					contain = true;
					if (fusion == null) {
						fusion = cl;
						cl.addAll(toAdd);
					} else {
						fusion.addAll(cl);
						i.remove();
					}
				}
			}
		}
		if (fusion == null)
			partition.add(new ArrayList<E>(toAdd));
	}

	/**
	 * Return the join of this and the given partition p the join of two
	 * partition is obtained by making the union of their non-disjoint classes
	 * until stability ex: the join of {{a,b,c},{d,e}} and {{e,g},{k,l}} is
	 * {{a,b,c},{d,e,g},{k,l}}
	 */
	public Partition<E> join(Partition<E> p) {
		Partition<E> res = new Partition<E>();
		for (ArrayList<E> cl : partition) {
			res.partition.add(new ArrayList<E>(cl));
		}
		for (ArrayList<E> cl : p.partition)
			res.addClass(cl);
		return res;
	}

	@Override
	public Iterator<ArrayList<E>> iterator() {
		return partition.iterator();
	}

	// //////////////////////////////////////////////////////////////////////////
	// OBJECT METHODS
	// //////////////////////////////////////////////////////////////////////////

	@Override
	public String toString() {
		return partition.toString();
	}
	
	@Override
	public int hashCode() {
		int hashCode = 0;
		for (ArrayList<E> list : this) {
			for (E e : list) {
				hashCode = 31 * hashCode + e.hashCode();
			}
		}
		return hashCode * 31 + this.partition.size();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || !(obj instanceof Partition)) {
			return false;
		}

		@SuppressWarnings("unchecked")
		Partition<Object> other = (Partition<Object>) obj;
		@SuppressWarnings("unchecked")
		Partition<Object> me = (Partition<Object>) this;

		for (ArrayList<Object> list : me) {
			for(Object e1 : list) {
				for(Object e2 : list) {
					ArrayList<Object> l1 = other.getClass(e1);
					ArrayList<Object> l2 = other.getClass(e2);
					if (l1 != l2 || l1 == null) {
						return false;
					}
				}
			}
		}
		for (ArrayList<Object> list : other) {
			for (Object e1 : list) {
				for (Object e2 : list) {
					ArrayList<Object> l1 = me.getClass(e1);
					ArrayList<Object> l2 = me.getClass(e2);
					if (l1 != l2 || l1 == null) {
						return false;
					}
				}
			}
		}
		return true;
	}

}
