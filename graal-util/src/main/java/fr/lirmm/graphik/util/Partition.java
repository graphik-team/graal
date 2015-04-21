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

	public Partition(Partition<E> partition) {
		this();
		for (Collection<E> classs : partition) {
			this.partition.add(new ArrayList<E>(classs));
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
			partition.add(toAdd);
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
			res.partition.add(cl);
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
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || !(obj instanceof Partition)) {
			return false;
		}
		return this.equals((Partition<E>) obj);
	}

	public boolean equals(Partition<E> other) { // NOPMD
		for (ArrayList<E> list : this) {
			for(E e1 : list) {
				for(E e2 : list) {
					ArrayList<E> l1 = other.getClass(e1);
					ArrayList<E> l2 = other.getClass(e2);
					if (l1 != l2 || l1 == null) {
						return false;
					}
				}
			}
		}
		for (ArrayList<E> list : other) {
			for (E e1 : list) {
				for (E e2 : list) {
					ArrayList<E> l1 = this.getClass(e1);
					ArrayList<E> l2 = this.getClass(e2);
					if (l1 != l2 || l1 == null) {
						return false;
					}
				}
			}
		}
		return true;
	}

}
