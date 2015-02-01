package fr.lirmm.graphik.graal.apps;

import java.util.TreeMap;
import fr.lirmm.graphik.graal.core.Atom;

// TODO: use external DB
public class AtomIndex {

	public int get(Atom a) {
		Integer i = _atomToIndex.get(a);
		if (i == null) { 
			add(a); 
			return get(a);
		}
		return _atomToIndex.get(a).intValue();
	}

	public Atom get(int i) {
		return _indexToAtom.get(new Integer(i));
	}

	public void add(Atom a, Integer i) {
		_atomToIndex.put(a,i);
		_indexToAtom.put(i,a);
	}

	public void add(Atom a) {
		add(a,new Integer(++_currentIndex));
	}

	public String toString() {
		StringBuilder s = new StringBuilder();
		int i = 1;
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

	private TreeMap<Atom,Integer> _atomToIndex = new TreeMap<Atom,Integer>();
	private TreeMap<Integer,Atom> _indexToAtom = new TreeMap<Integer,Atom>();

	private int _currentIndex = 0;
};

