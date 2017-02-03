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

import java.util.Arrays;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import fr.lirmm.graphik.util.graph.DefaultDirectedEdge;
import fr.lirmm.graphik.util.graph.DirectedEdge;
import fr.lirmm.graphik.util.graph.Graph;

/**
 * From Tarjan 1972, depth-first search and linear graph algorithms
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public final class BiconnectedComponents {

	private BiconnectedComponents() {
	}

	public static List<Set<Integer>> execute(Graph g) {
		Data d = new Data();
		d.components = new LinkedList<Set<Integer>>();
		d.g = g;
		d.i = 0;
		d.stack = new LinkedList<DirectedEdge>();
		d.number = new int[g.nbVertices()];
		Arrays.fill(d.number, -1);
		d.lowpt = new int[g.nbVertices()];
		Arrays.fill(d.lowpt, -1);

		for (int v = 0; v < g.nbVertices(); ++v) {
			if (d.number[v] == -1) {
				biconnect(d, v, 0);
			}
		}

		return d.components;
	}

	private static void biconnect(Data d, int v, int u) {

		d.lowpt[v] = d.number[v] = ++d.i;
		Iterator<Integer> adjacencyIt = d.g.adjacencyList(v);

		int w;
		while (adjacencyIt.hasNext()) {
			w = adjacencyIt.next();
			if (d.number[w] == -1) {
				d.stack.push(new DefaultDirectedEdge(v, w));
				biconnect(d, w, v);
				d.lowpt[v] = Math.min(d.lowpt[v], d.lowpt[w]);
				if(d.lowpt[w] >= d.number[v]) {
					// start new component
					d.currentComponent = new TreeSet<Integer>();
					d.components.add(d.currentComponent);
					while (d.number[d.stack.peek().getTail()] >= d.number[w]) {
						DirectedEdge e = d.stack.pop();
						d.currentComponent.add(e.getTail());
						d.currentComponent.add(e.getHead());
					}
					// should be (v,w)
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

	private static class Data {
		Graph               g;
		int i;
		int number[];
		int lowpt[];
		Deque<DirectedEdge> stack;
		List<Set<Integer>> components;
		Set<Integer>        currentComponent;
	}
}
