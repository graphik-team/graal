package fr.lirmm.graphik.graal.cqa;

import java.util.Iterator;
import java.util.LinkedList;

import java.io.File;
import java.io.FileWriter;

public class FGH implements Iterable<FGH.Edge> {

	public class Edge implements Comparable<Edge>, Iterable<Integer> {

		public Edge(LinkedList<Integer> f, int t) {
			for (Integer i : f) {
				if (!_from.contains(i)) _from.add(i);
			}
			_to = t;
		}

		public String toString() {
			StringBuilder s = new StringBuilder();
			s.append('{');
			boolean first = true;
			for (Integer i : _from) {
				if (first) first = false;
				else s.append(',');
				s.append(i.intValue());
			}
			s.append('}');
			s.append(" --> ");
			s.append(_to);
			return s.toString();
		}

		public int compareTo(Edge e) {
			return 1;
		}
		public boolean isSubset(Edge e) {
			if (e._to != _to)
				return false;
			for (Integer i : _from)
				if (!e._from.contains(i))
					return false;
			return true;
		}

		public int to() { return _to; }

		public Iterator iterator() { return _from.iterator(); }

		private LinkedList<Integer> _from = new LinkedList<Integer>();
		private int _to;
	};

	public void add(LinkedList<Integer> causes, int consequence) {
		if (!causes.contains(new Integer(consequence))) {
			Edge e = new Edge(causes,consequence);
			_edges.add(e);
			// Perhaps too expensive and quite useless...
			filterSupsets();
		}
	}

	public void filterSupsets() {
		for (Edge e : _edges) {
			for (Edge e2 : _edges) {
				if (e != e2) {
					if (e.isSubset(e2))
						_edges.remove(e2);
				}
			}
		}
	}

	@Override
	public Iterator iterator() {
		return _edges.iterator();
	}

	public String toString() {
		StringBuilder s = new StringBuilder();
		for (Edge e : this) {
			s.append(e);
			s.append("\n");
		}
		return s.toString();
	}

	public void writeToFile(String filepath) {
		try {
			File f = new File(filepath);
			FileWriter out = new FileWriter(f);

			// first count nb edges
			int cpt = 0;
			for (Edge e : this)
				++cpt;

			out.write(cpt);
			out.write("\n");

			for (Edge e : this) {
				cpt = 0;
				for (Integer i : e) // count edge size
					++cpt;
				out.write(cpt);
				out.write(' ');
				for (Integer i : e) { // print elements
					out.write(i);
					out.write(' ');
				}
				out.write("\n");
			}
		}
		catch (Exception e) {
			System.err.println(e);
			e.printStackTrace();
		}
	}

	private LinkedList<Edge> _edges = new LinkedList<Edge>();

};

