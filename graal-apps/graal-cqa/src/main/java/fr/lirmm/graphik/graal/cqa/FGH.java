/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2015)
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
 package fr.lirmm.graphik.graal.cqa;

import java.io.File;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.LinkedList;

public class FGH implements Iterable<FGH.Edge> {

	public static class Edge implements Comparable<Edge>, Iterable<Integer> {

		public Edge(LinkedList<Integer> f, int t) {
			for (Integer i : f) {
				if (!this.from.contains(i)) this.from.add(i);
			}
			this.to = t;
		}

		@Override
        public String toString() {
			StringBuilder s = new StringBuilder();
			s.append('{');
			boolean first = true;
			for (Integer i : this.from) {
				if (first) first = false;
				else s.append(',');
				s.append(i.intValue());
			}
			s.append('}');
			s.append(" --> ");
			s.append(to);
			return s.toString();
		}

		@Override
        public int compareTo(Edge e) {
			return 1;
		}
		public boolean isSubset(Edge e) {
			if (e.to != this.to)
				return false;
			for (Integer i : this.from)
				if (!e.from.contains(i))
					return false;
			return true;
		}

		public int to() { return this.to; }

		@Override
        public Iterator iterator() { return this.from.iterator(); }

		private LinkedList<Integer> from = new LinkedList<Integer>();
		private int to;
	};

	public void add(LinkedList<Integer> causes, int consequence) {
		if (!causes.contains(new Integer(consequence))) {
			Edge e = new Edge(causes,consequence);
			this.edges.add(e);
			// Perhaps too expensive and quite useless...
			filterSupsets();
		}
	}

	public void filterSupsets() {
		LinkedList<Edge> toRemove = new LinkedList<Edge>();
		for (Edge e : this.edges) {
			for (Edge e2 : this.edges) {
				if (e != e2) {
					if (e.isSubset(e2))
						toRemove.add(e2);
				}
			}
		}
		this.edges.removeAll(toRemove);
	}

	@Override
	public Iterator iterator() {
		return this.edges.iterator();
	}

	@Override
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

			out.write(""+cpt);
			out.write("\n");

			for (Edge e : this) {
				cpt = 0;
				for (Integer i : e) // count edge size
					++cpt;
				out.write(""+cpt);
				out.write(' ');
				for (Integer i : e) { // print elements
					out.write(""+i);
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

	private LinkedList<Edge> edges = new LinkedList<Edge>();

};

