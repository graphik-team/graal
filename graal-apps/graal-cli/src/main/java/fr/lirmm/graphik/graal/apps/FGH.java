package fr.lirmm.graphik.graal.apps;

import java.util.Iterator;
import java.util.LinkedList;
// TODO external DB
public class FGH implements Iterable<FGH.Edge> {

	public class Edge {

		public Edge(LinkedList<Integer> f, int t) {
			_from = f;
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

		private LinkedList<Integer> _from;
		private int _to;
	};

	public void add(LinkedList<Integer> causes, int consequence) {
		_edges.add(new Edge(causes,consequence));
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

	private LinkedList<Edge> _edges = new LinkedList<Edge>();

};

