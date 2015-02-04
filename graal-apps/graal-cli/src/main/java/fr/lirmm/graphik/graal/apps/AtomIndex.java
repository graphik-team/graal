package fr.lirmm.graphik.graal.apps;

import java.util.TreeMap;
import fr.lirmm.graphik.graal.core.Atom;

// TODO: use external DB
public class AtomIndex {

	public int get(Atom a) {
		Integer i = this.atomToIndex.get(a);
		if (i == null) { 
			add(a); 
			return get(a);
		}
		return this.atomToIndex.get(a).intValue();
	}

	public Atom get(int i) {
		return this.indexToAtom.get(new Integer(i));
	}

	public void add(Atom a, Integer i) {
		this.atomToIndex.put(a,i);
		this.indexToAtom.put(i,a);
	}

	public void add(Atom a) {
		add(a,new Integer(this.currentIndex));
		++this.currentIndex;
	}

	public String toString() {
		StringBuilder s = new StringBuilder();
		int i = 0;
		Atom a = get(i);
		while (a != null) {
			s.append(i);
			s.append("\t");
			s.append(a);
			s.append("\n");
			a = get(++i);
		}
		return s.toString();
	}

	private TreeMap<Atom,Integer> atomToIndex = new TreeMap<Atom,Integer>();
	private TreeMap<Integer,Atom> indexToAtom = new TreeMap<Integer,Atom>();

	private int currentIndex = 0;
};

