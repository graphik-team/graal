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
package fr.lirmm.graphik.util.graph.algorithm;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import fr.lirmm.graphik.util.graph.DefaultDirectedEdge;
import fr.lirmm.graphik.util.graph.DefaultHyperGraph;
import fr.lirmm.graphik.util.graph.DirectedEdge;
import fr.lirmm.graphik.util.graph.HyperGraph;

/**
 * From Tarjan 1972, depth-first search and linear graph algorithms
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public final class BiconnectedComponentsForHyperGraph {

	private BiconnectedComponentsForHyperGraph() {
	}

	public static Data execute(HyperGraph g) {
		Data d = new Data();
		d.components = new LinkedList<Set<Integer>>();
		d.g = g;
		d.i = 0;
		d.stack = new LinkedList<DirectedEdge>();
		d.number = new int[g.nbVertices() + 1];
		d.access = new int[g.nbVertices() + 1];
		d.isAccesseur = new boolean[g.nbVertices() + 1];
		d.isEntry = new boolean[g.nbVertices() + 1];
		d.lowpt = new int[g.nbVertices() + 1];

		for (int v = 1; v <= g.nbVertices(); ++v) {
			if (d.number[v] == 0) {
				// d.stack.push(v);
				biconnect(d, v, 0);
			}
		}

		return d;
	}

	private static void biconnect(Data d, int v, int u) {

		d.lowpt[v] = d.number[v] = ++d.i;
		d.access[v] = u;
		Iterator<Integer> adjacencyIt = d.g.adjacencyList(v);

		int w;
		while (adjacencyIt.hasNext()) {
			w = adjacencyIt.next();
			if (d.number[w] == 0) {
				d.stack.push(new DefaultDirectedEdge(v, w));
				biconnect(d, w, v);
				d.lowpt[v] = Math.min(d.lowpt[v], d.lowpt[w]);
				if (d.lowpt[w] >= d.number[v]) {
					// start new component
					d.isAccesseur[v] = true;
					d.isEntry[w] = true;
					d.currentComponent = new TreeSet<Integer>();
					d.components.add(d.currentComponent);
					while (d.number[d.stack.peek().getTail()] >= d.number[w]) {
						DirectedEdge e = d.stack.pop();
						d.currentComponent.add(e.getTail());
						d.currentComponent.add(e.getHead());
					}
					// should be (w, v)
					DirectedEdge e = d.stack.pop();
					d.currentComponent.add(e.getTail());
					d.currentComponent.add(e.getHead());
				}
			} else if (d.number[w] < d.number[v] && w != u) {
				d.stack.push(new DefaultDirectedEdge(v, w));
				d.lowpt[v] = Math.min(d.lowpt[v], d.number[w]);

			}
		}
	}

	public static class Data {
		public boolean[]    isEntry;
		public boolean[]    isAccesseur;
		int[]               numberinv;
		LinkedList<Integer> order;
		public int          access[];
		HyperGraph g;
		int i;
		public int          number[];
		int lowpt[];
		Deque<DirectedEdge>        stack;
		List<Set<Integer>> components;
		Set<Integer>        currentComponent;

		@Override
        public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("")
			.append("\t{ vertices : ");
			for (int v = 0; v <= g.nbVertices(); ++v) {
				sb.append("\t")
.append(v);
			}
			sb.append(" }\n")
			.append("\t{   number : ");
			for (int v = 0; v <= g.nbVertices(); ++v) {
				sb.append("\t")
				.append(number[v]);
			}
			sb.append(" }\n")
			.append("\t{    lowpt : ");
			for (int v = 0; v <= g.nbVertices(); ++v) {
				sb.append("\t")
.append(lowpt[v]);
			}
			// sb.append(" }\n")
			// .append("\t{  access2 : ");
			// for (int v = 0; v <= g.nbVertices(); ++v) {
			// sb.append("\t")
			// .append(access2[v]);
			// }
			sb.append(" }\n").append("\t{   access : ");
			for (int v = 0; v <= g.nbVertices(); ++v) {
				sb.append("\t").append(access[v]);
			}
			sb.append(" }\n")
.append("\t{   stack :");
			for (DirectedEdge e : stack) {
				sb.append(e);
			}
			sb.append(" }\n")
			.append("\t{ components :");
			for (Set<Integer> c : components) {
				sb.append(c);
			}
			sb.append(" }\n")
			.append("\t{ current component :")
			.append(currentComponent)
			.append(" }\n}");
			return sb.toString();
		}
	}

	public static void main(String args[]) {
		DefaultHyperGraph g = new DefaultHyperGraph(20);
		g.addEdge(1, 2, 3);
		g.addEdge(3, 4);
		g.addEdge(14, 10);
		g.addEdge(10, 12, 13);
		g.addEdge(10, 7, 11);
		g.addEdge(7, 11);
		g.addEdge(5, 6, 7, 8);
		g.addEdge(7, 8, 9);
		g.addEdge(8, 19, 20);
		g.addEdge(9, 18);
		g.addEdge(9, 15);
		g.addEdge(15, 16, 17);

		// List<Set<Integer>> components =
		// BiconnectedComponentsForHyperGraph.execute(g);
		// for (Set<Integer> c : components) {
		// System.out.println(c);
		// }
	}

}
