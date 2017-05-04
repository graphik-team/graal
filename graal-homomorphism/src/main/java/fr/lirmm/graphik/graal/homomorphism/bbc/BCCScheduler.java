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
package fr.lirmm.graphik.graal.homomorphism.bbc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.RulesCompilation;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.graal.core.ConjunctiveQueryWithFixedVariables;
import fr.lirmm.graphik.graal.homomorphism.Var;
import fr.lirmm.graphik.graal.homomorphism.scheduler.PatternScheduler;
import fr.lirmm.graphik.graal.homomorphism.utils.ProbaUtils;
import fr.lirmm.graphik.util.graph.DefaultDirectedEdge;
import fr.lirmm.graphik.util.graph.DefaultGraph;
import fr.lirmm.graphik.util.graph.DefaultHyperEdge;
import fr.lirmm.graphik.util.graph.DefaultHyperGraph;
import fr.lirmm.graphik.util.graph.DirectedEdge;
import fr.lirmm.graphik.util.graph.Graph;
import fr.lirmm.graphik.util.graph.HyperGraph;
import fr.lirmm.graphik.util.profiler.AbstractProfilable;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;

class BCCScheduler extends AbstractProfilable implements PatternScheduler {

	protected final BCC BCC;
	private Comparator<Integer> varComparator;
	private Term[] inverseMap;
	boolean withForbiddenCandidate;

	BCCScheduler(BCC BCC, boolean withForbiddenCandidate) {
		this.BCC = BCC;
		this.withForbiddenCandidate = withForbiddenCandidate;
	}

	@Override
	public Var[] execute(InMemoryAtomSet query, Set<Variable> preAffectedVars, List<Term> ans, AtomSet data,
			RulesCompilation rc) {
		InMemoryAtomSet fixedQuery = new ConjunctiveQueryWithFixedVariables(query, ans, preAffectedVars).getAtomSet();

		Set<Variable> variables = fixedQuery.getVariables();
		// Set<Variable> variables = query.getVariables().removeAll(preAffectedVars);

		// BCC
		Map<Term, Integer> map = new TreeMap<Term, Integer>();
		this.inverseMap = new Term[variables.size() + 1];
		HyperGraph graph = constructHyperGraph(query, variables, this.inverseMap, map, ans);

		double[] proba = this.computeProba(fixedQuery, data, variables.size(), map, rc);
		this.varComparator = new IntegerComparator(proba);

		TmpData d = biconnect(graph, this.varComparator);

		Var[] vars = new Var[variables.size() + 2];
		this.BCC.varData = new VarData[variables.size() + 2];

		vars[0] = new Var(0);
		this.BCC.varData[0] = new VarData();

		int lastAnswerVariable = -1;
		for (int i = 1; i < d.vars.length; ++i) {
			Var v = d.vars[i];
			vars[v.level] = v;
			this.BCC.varData[v.level] = d.ext[i];
			v.value = (Variable) this.inverseMap[i];
			v.nextLevel = v.level + 1;
			v.previousLevel = v.level - 1;
			if (this.withForbiddenCandidate && this.BCC.varData[v.level].isAccesseur) {
				this.BCC.varData[v.level].forbidden = new TreeSet<Term>();
			}

			if (ans.contains(v.value)) {
				if (v.level > lastAnswerVariable)
					lastAnswerVariable = v.level;
			}
		}

		int level = variables.size() + 1;
		vars[level] = new Var(level);
		this.BCC.varData[level] = new VarData();
		// if an homomorphism is found, go to the last answer variable
		vars[level].previousLevel = lastAnswerVariable;

		// Profiling
		if (this.getProfiler().isProfilingEnabled()) {
			StringBuilder sb = new StringBuilder();
			for (Var v : vars) {
				sb.append(v.value);
				sb.append(" > ");
			}
			this.getProfiler().put("BCCOrder", sb.toString());
		}

		return vars;

	}

	@Override
	public Var[] execute(InMemoryAtomSet h, List<Term> ans, AtomSet data, RulesCompilation rc) {
		return this.execute(h, Collections.<Variable>emptySet(), ans, data, rc);
	}

	@Override
	public boolean isAllowed(Var var, Term image) {
		return (this.BCC.varData[var.level].forbidden == null
				|| !this.BCC.varData[var.level].forbidden.contains(image));
	}

	/**
	 * @param h
	 * @param map
	 * @param nbVar
	 * @return the probability to have an image for each variables which appears
	 *         in h.
	 */
	protected double[] computeProba(InMemoryAtomSet h, AtomSet data, int nbVar, Map<Term, Integer> map,
			RulesCompilation rc) {
		this.getProfiler().start("probaComputingTime");
		final double[] proba = new double[nbVar + 1];
		for (int i = 0; i < proba.length; ++i) {
			proba[i] = -1.;
		}

		CloseableIteratorWithoutException<Atom> it = h.iterator();
		while (it.hasNext()) {
			Atom a = it.next();
			double probaA = ProbaUtils.computeProba(a, data, rc);

			for (Term t : a.getVariables()) {
				int i = map.get(t);
				if (proba[i] < 0) {
					proba[i] = probaA;
				} else {
					proba[i] *= probaA;
				}
			}
		}
		this.getProfiler().start("probaComputingTime");
		return proba;
	}

	protected static TmpData biconnect(HyperGraph g, Comparator<Integer> varComparator) {
		TmpData d = new TmpData(g.nbVertices());

		d.components = new ArrayList<Set<Integer>>();
		d.i = 0;
		d.stack = new LinkedList<DirectedEdge>();
		d.lowpt = new int[g.nbVertices()];

		List<Integer> vertices = new LinkedList<Integer>();
		for (int v = 1; v < g.nbVertices(); ++v) {
			vertices.add(v);
		}
		Collections.sort(vertices, varComparator);

		for (Integer v : vertices) {
			if (d.vars[v].level == 0) {
				biconnect(g, d, v, 0, varComparator);
			}
		}

		return d;
	}

	protected static Deque<Integer> lastAccesseurs = new LinkedList<Integer>();
	protected static int lastTerminal;

	protected static void biconnect(HyperGraph g, TmpData d, int v, int u, Comparator<Integer> varComparator) {

		d.lowpt[v] = d.vars[v].level = ++d.i;
		d.ext[v].previousLevelFailure = d.vars[u].level;
		Iterator<Integer> adjacencyIt = g.adjacencyList(v);

		LinkedList<Integer> list = new LinkedList<Integer>();
		while (adjacencyIt.hasNext()) {
			list.add(adjacencyIt.next());
		}
		Collections.sort(list, varComparator);
		adjacencyIt = list.iterator();

		int w;
		while (adjacencyIt.hasNext()) {
			w = adjacencyIt.next();

			if (d.vars[w].level == 0) {
				d.stack.push(new DefaultDirectedEdge(v, w));
				biconnect(g, d, w, v, varComparator);
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
						while (!lastAccesseurs.isEmpty()
								&& d.vars[lastAccesseurs.peek()].level >= d.vars[entry].level) {
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

	/**
	 * The HyperGraph of variables of h. There is an hyper edge between a set of
	 * variables if they appear in a same atom.
	 * 
	 * @param h
	 * @return the HyperGraph of variables of h.
	 */
	protected static HyperGraph constructHyperGraph(InMemoryAtomSet h, Set<Variable> variables, Term[] inverseMap,
			Map<Term, Integer> map, Iterable<Term> ans) {

		int i = 0;
		for (Variable t : variables) {
			inverseMap[++i] = t;
			map.put(t, i);
		}

		HyperGraph graph = new DefaultHyperGraph(variables.size() + 1);
		CloseableIteratorWithoutException<Atom> it = h.iterator();
		while (it.hasNext()) {
			Atom a = it.next();
			DefaultHyperEdge edge = new DefaultHyperEdge();
			for (Variable t : a.getVariables()) {
				if(variables.contains(t)) {
					edge.addVertice(map.get(t));
				}
			}
			graph.add(edge);
		}

		return graph;
	}

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE CLASS
	// /////////////////////////////////////////////////////////////////////////

	protected static class TmpData {
		Var[] vars;
		VarData[] ext;
		int i;
		int lowpt[];
		Deque<DirectedEdge> stack;
		List<Set<Integer>> components;
		Set<Integer> currentComponent;
		BCCGraph bccGraph = new BCCGraph();

		TmpData(int nbVertices) {
			vars = new Var[nbVertices];
			ext = new VarData[nbVertices];
			for (int i = 0; i < nbVertices; ++i) {
				vars[i] = new Var();
				ext[i] = new VarData();
			}
		}
	}

	protected static class BCCGraph {

		public Graph graph = new DefaultGraph();
		public Object[] bccGraphMap = new Object[40];
		public int[] bccGraphEntryInverseMap = new int[30];
		public int[] bccGraphAccesseurInverseMap = new int[30];

		BCCGraph() {
			Arrays.fill(bccGraphEntryInverseMap, -1);
			Arrays.fill(bccGraphAccesseurInverseMap, -1);
		}

		int addAccesseur(int accesseur) {
			int v = bccGraphAccesseurInverseMap[accesseur];
			if (v == -1) {
				v = graph.addVertex();
				bccGraphMap[v] = accesseur;
				bccGraphAccesseurInverseMap[accesseur] = v;
			}
			return v;
		}

		int addComponent(Set<Integer> component) {
			int v = graph.addVertex();
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
		 * @param inverseMap
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