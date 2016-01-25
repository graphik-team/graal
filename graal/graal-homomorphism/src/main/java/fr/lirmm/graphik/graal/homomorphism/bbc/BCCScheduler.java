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
package fr.lirmm.graphik.graal.homomorphism.bbc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.graal.homomorphism.BacktrackHomomorphism;
import fr.lirmm.graphik.graal.homomorphism.Var;
import fr.lirmm.graphik.util.graph.DefaultDirectedEdge;
import fr.lirmm.graphik.util.graph.DefaultGraph;
import fr.lirmm.graphik.util.graph.DefaultHyperEdge;
import fr.lirmm.graphik.util.graph.DefaultHyperGraph;
import fr.lirmm.graphik.util.graph.DirectedEdge;
import fr.lirmm.graphik.util.graph.Graph;
import fr.lirmm.graphik.util.graph.HyperGraph;

/**
 * This BacktrackHomomorphism.Scheduler implementation provides an backtracking
 * order based on Biconnected Components (BCC-compatible ordering).
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class BCCScheduler implements BacktrackHomomorphism.Scheduler {

	private VarData[] data;
	private Term[]    inverseMap;
	private boolean   withForbiddenCandidate;

	public BCCScheduler() {
		this(false);
	}

	public BCCScheduler(boolean withForbiddenCandidate) {
		super();
		this.withForbiddenCandidate = withForbiddenCandidate;
	}

	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public Var[] execute(InMemoryAtomSet h, List<Term> ans) {

		Set<Term> variables = h.getTerms(Term.Type.VARIABLE);

		// BCC
		Map<Term, Integer> map = new TreeMap<Term, Integer>();
		inverseMap = new Term[variables.size() + 1];
		HyperGraph graph = constructHyperGraph(h, variables, inverseMap, map, ans);

		List<Integer> ansInt = new LinkedList<Integer>();
		for (Term t : ans) {
			ansInt.add(map.get(t));
		}
		TmpData d = biconnect(graph, ansInt.iterator());

		Var[] vars = new Var[variables.size() + 2];
		data = new VarData[variables.size() + 2];

		vars[0] = new Var(0);
		data[0] = new VarData();

		for (int i = 1; i < d.vars.length; ++i) {
			Var v = d.vars[i];
			vars[v.level] = v;
			data[v.level] = d.ext[i];
			v.value = (Variable) inverseMap[i];
			v.nextLevel = v.level + 1;
			v.previousLevel = v.level - 1;
			if (withForbiddenCandidate && data[v.level].isAccesseur) {
				data[v.level].forbidden = new TreeSet<Term>();
			}
		}

		int level = variables.size() + 1;
		vars[level] = new Var(level);
		data[level] = new VarData();
		vars[level].previousLevel = ans.size();

		return vars;
	}

	@Override
	public boolean isAllowed(Var var, Term image) {
		return (data[var.level].forbidden == null || !data[var.level].forbidden.contains(image));
	}

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE CLASSES
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * @param h
	 * @return
	 */
	private HyperGraph constructHyperGraph(InMemoryAtomSet h, Set<Term> variables, Term[] inverseMap,
	    Map<Term, Integer> map, Iterable<Term> ans) {

		int i = 0;
		for (Term t : variables) {
			inverseMap[++i] = t;
			map.put(t, i);
		}

		HyperGraph graph = new DefaultHyperGraph(variables.size() + 1);
		for (Atom a : h) {
			DefaultHyperEdge edge = new DefaultHyperEdge();
			for (Term t : a.getTerms(Term.Type.VARIABLE)) {
				edge.addVertice(map.get(t));
			}
			graph.add(edge);
		}

		// ANSWER VARIABLES
		DefaultHyperEdge edge = new DefaultHyperEdge();
		for (Term t : ans) {
			edge.addVertice(map.get(t));
		}
		graph.add(edge);

		return graph;
	}

	private static TmpData biconnect(HyperGraph g, Iterator<Integer> ans) {
		TmpData d = new TmpData(g.nbVertices());

		d.components = new ArrayList<Set<Integer>>();
		d.i = 0;
		d.stack = new LinkedList<DirectedEdge>();
		d.lowpt = new int[g.nbVertices()];

		if (ans.hasNext()) {
			biconnect(g, d, ans.next(), 0, ans);
		}
		for (int v = 1; v < g.nbVertices(); ++v) {
			if (d.vars[v].level == 0) {
				biconnect(g, d, v, 0, ans);
			}
		}

		return d;
	}

	static Deque<Integer> lastAccesseurs = new LinkedList<Integer>();
	static int            lastTerminal;

	private static void biconnect(HyperGraph g, TmpData d, int v, int u, Iterator<Integer> ans) {

		d.lowpt[v] = d.vars[v].level = ++d.i;
		d.vars[v].previousLevelFailure = d.vars[u].level;
		Iterator<Integer> adjacencyIt = g.adjacencyList(v);

		int w;
		while (adjacencyIt.hasNext()) {
			if (ans.hasNext()) {
				w = ans.next();
			} else {
				w = adjacencyIt.next();
			}
			if (d.vars[w].level == 0) {
				d.stack.push(new DefaultDirectedEdge(v, w));
				biconnect(g, d, w, v, ans);
				d.lowpt[v] = Math.min(d.lowpt[v], d.lowpt[w]);
				if (d.lowpt[w] >= d.vars[v].level) {
					// start new component
					d.currentComponent = new TreeSet<Integer>();
					d.components.add(d.currentComponent);
					while (d.vars[d.stack.peek().getTail()].level >= d.vars[w].level) {
						DirectedEdge e = d.stack.pop();
						d.currentComponent.add(e.getTail());
						d.currentComponent.add(e.getHead());
					}
					// should be (w, v)
					DirectedEdge e = d.stack.pop();
					d.currentComponent.add(e.getTail());
					d.currentComponent.add(e.getHead());

					int entry = w;
					int accesseur = (u == 0) ? u : v;
					int terminal = v;
					d.ext[accesseur].isAccesseur = true;
					for (int i : d.currentComponent) {
						if ((u == 0 || i != v) && d.vars[i].level < d.vars[entry].level) {
							entry = i;
						}
						if (d.vars[i].level > d.vars[terminal].level) {
							terminal = i;
						}
						if (i != v) {
							d.ext[i].accesseur = d.vars[accesseur].level;
						}
					}
					d.ext[entry].isEntry = true;

					int componentVertice = d.bccGraph.addComponent(d.currentComponent);
					int accesseurVertice = d.bccGraph.addAccesseur(accesseur);
					d.bccGraph.addEdge(componentVertice, accesseurVertice);

					if (!lastAccesseurs.isEmpty() && d.vars[lastAccesseurs.peek()].level >= d.vars[entry].level) {
						while (!lastAccesseurs.isEmpty() && d.vars[lastAccesseurs.peek()].level >= d.vars[entry].level) {
							int a = lastAccesseurs.pop();
							if (a != accesseur) {
								d.bccGraph.addEdge(componentVertice, d.bccGraph.addAccesseur(a));
							}
						}
					} else {
						d.ext[terminal].isTerminal = true;
						d.ext[terminal].compilateurs = new TreeSet<Integer>();
						lastTerminal = terminal;
					}

					d.ext[lastTerminal].compilateurs.add(d.vars[entry].level);

					if (lastAccesseurs.isEmpty() || lastAccesseurs.peek() != accesseur) {
						lastAccesseurs.push(accesseur);
					}

				}
			} else if (d.vars[w].level < d.vars[v].level && w != u) {
				d.stack.push(new DefaultDirectedEdge(v, w));
				d.lowpt[v] = Math.min(d.lowpt[v], d.vars[w].level);
			}
		}
	}

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE CLASS
	// /////////////////////////////////////////////////////////////////////////

	private static class TmpData {
		Var[]               vars;
		VarData[]           ext;
		int                 i;
		int                 lowpt[];
		Deque<DirectedEdge> stack;
		List<Set<Integer>>  components;
		Set<Integer>        currentComponent;
		BCCGraph            bccGraph = new BCCGraph();

		TmpData(int nbVertices) {
			vars = new Var[nbVertices];
			ext = new VarData[nbVertices];
			for (int i = 0; i < nbVertices; ++i) {
				vars[i] = new Var();
				ext[i] = new VarData();
			}
		}
	}

	private static class BCCGraph {

		public Graph    graph                       = new DefaultGraph();
		public Object[] bccGraphMap                 = new Object[40];
		public int[]    bccGraphEntryInverseMap     = new int[30];
		public int[]    bccGraphAccesseurInverseMap = new int[30];

		BCCGraph() {
			Arrays.fill(bccGraphEntryInverseMap, -1);
			Arrays.fill(bccGraphAccesseurInverseMap, -1);
		}

		int addAccesseur(int accesseur) {
			int v = bccGraphAccesseurInverseMap[accesseur];
			if (v == -1) {
				v = graph.addVertice();
				bccGraphMap[v] = accesseur;
				bccGraphAccesseurInverseMap[accesseur] = v;
			}
			return v;
		}

		int addComponent(Set<Integer> component) {
			int v = graph.addVertice();
			bccGraphMap[v] = component;
			return v;
		}

		Object getObject(int v) {
			return bccGraphMap[v];
		}

		void addEdge(int v1, int v2) {
			this.graph.addEdge(v1, v2);
		}

		public int nbVertices() {
			return this.graph.nbVertices();
		}

		public Iterator<Integer> adjacencyList(int v) {
			return this.graph.adjacencyList(v);
		}

		/**
		 * @param bccGraph
		 * @param inverseMap2
		 */
		void printBccGraph(BCCGraph bccGraph, Term[] inverseMap) {
			System.out.println("\n%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
			for (int i = 0; i < bccGraph.nbVertices(); ++i) {
				Iterator<Integer> adjacencyList = bccGraph.adjacencyList(i);
				printBccVertice(bccGraph, inverseMap, i);
				System.out.print(": ");
				while (adjacencyList.hasNext()) {
					int j = adjacencyList.next();
					printBccVertice(bccGraph, inverseMap, j);
					System.out.print(" ");
				}
				System.out.println();

			}
			System.out.println("\n%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		}

		void printBccVertice(BCCGraph g, Term[] inverseMap, int v) {
			Object o = g.getObject(v);
			if (o instanceof Integer) {
				System.out.print("(" + inverseMap[(Integer) o] + ")");
			} else if (o instanceof Set) {
				Set<Integer> set = (Set<Integer>) o;
				System.out.print("{");
				for (int j : set)
					System.out.print(inverseMap[j] + " ");
				System.out.print("}");
			}
		}
	}
}
