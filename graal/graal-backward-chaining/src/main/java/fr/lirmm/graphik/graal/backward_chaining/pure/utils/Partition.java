package fr.lirmm.graphik.graal.backward_chaining.pure.utils;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * A partition of terms. A partition can be associated with a substition : two
 * terms are in the same class if one is the image of the other ex: the
 * partition associated with {(a,b)(c,b)(d,e)(e,f)} is {{a,b,c}{d,e,f}}
 */

public class Partition<E> implements Iterable<ArrayList<E>> {

	protected ArrayList<ArrayList<E>> partition;

	public Partition() {
		partition = new ArrayList<ArrayList<E>>();
	}

	public ArrayList<ArrayList<E>> getClasses() {
		return partition;
	}

	/**
	 * Add the couple to the partition
	 */
	public void add(E t, E im) {
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
		else if (tset != null && imset == null) {
			tset.add(im);
		}
		// t has not an equivalence set but im has one so we add t in im's
		// equivalence set
		else if (tset == null && imset != null) {
			imset.add(t);
		}
		// t and im have different equivalence sets so we append the two
		// equivalence set
		else if (tset != imset) {
			tset.addAll(imset);
			partition.remove(imset);
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
					fusion.remove(t);
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
	public String toString() {
		return partition.toString();
	}

	@Override
	public Iterator<ArrayList<E>> iterator() {
		return partition.iterator();
	}

}
