/**
 * 
 */
package fr.lirmm.graphik.util;

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
	
	@Override
	public int addClasse(T... elements) {
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
			for(T t : this.classes.keySet()) {
				if(this.classes.get(t).equals(c1)) {
					this.classes.put(t, c2);
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
