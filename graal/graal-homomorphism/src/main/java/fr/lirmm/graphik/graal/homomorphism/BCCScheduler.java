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
package fr.lirmm.graphik.graal.homomorphism;

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
import fr.lirmm.graphik.graal.homomorphism.BacktrackHomomorphism.Var;
import fr.lirmm.graphik.util.graph.DefaultDirectedEdge;
import fr.lirmm.graphik.util.graph.DefaultHyperEdge;
import fr.lirmm.graphik.util.graph.DefaultHyperGraph;
import fr.lirmm.graphik.util.graph.DirectedEdge;
import fr.lirmm.graphik.util.graph.HyperGraph;

/**
 * This BacktrackHomomorphism.Scheduler implementation provides an backtracking
 * order based on Biconnected Components (BCC-compatible ordering).
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class BCCScheduler implements BacktrackHomomorphism.Scheduler {

	static Term[] inverseMap;

	private static BCCScheduler instance;

	protected BCCScheduler() {
		super();
	}

	public static synchronized BCCScheduler instance() {
		if (instance == null)
			instance = new BCCScheduler();

		return instance;
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
		Var[] tmp = biconnect(graph, ansInt.iterator());

		Var[] vars = new Var[variables.size() + 2];

		vars[0] = new Var(0);

		for (int i = 1; i < tmp.length; ++i) {
			Var v = tmp[i];
			vars[v.level] = v;
			v.value = (Variable) inverseMap[i];
			v.nextLevel = v.level + 1;
			v.previousLevelSuccess = v.level - 1;
		}

		int level = variables.size() + 1;
		vars[level] = new Var(level);
		vars[level].previousLevelSuccess = ans.size();

		return vars;
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

		HyperGraph graph = new DefaultHyperGraph(variables.size());
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

	private static Var[] biconnect(HyperGraph g, Iterator<Integer> ans) {
		Data d = new Data();
		d.vars = new Var[g.nbVertices() + 1];
		for (int i = 0; i < d.vars.length; ++i) {
			d.vars[i] = new Var();
		}
		d.components = new LinkedList<Set<Integer>>();
		d.i = 0;
		d.stack = new LinkedList<DirectedEdge>();
		d.lowpt = new int[g.nbVertices() + 1];

		if (ans.hasNext()) {
			biconnect(g, d, ans.next(), 0, ans);
		}
		for (int v = 1; v <= g.nbVertices(); ++v) {
			// System.out.println(inverseMap[v]);
			if (d.vars[v].level == 0) {
				biconnect(g, d, v, 0, ans);
			}
		}

		return d.vars;
	}

	private static void biconnect(HyperGraph g, Data d, int v, int u, Iterator<Integer> ans) {

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
					// d.vars[v].isAccesseur = true;
					// d.vars[v].isEntry = true;
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
				}
			} else if (d.vars[w].level < d.vars[v].level && w != u) {
				d.stack.push(new DefaultDirectedEdge(v, w));
				d.lowpt[v] = Math.min(d.lowpt[v], d.vars[w].level);
			}
		}
	}

	private static class Data {
		Var[]               vars;
		int                 i;
		int                 lowpt[];
		Deque<DirectedEdge> stack;
		List<Set<Integer>>  components;
		Set<Integer>        currentComponent;
	}
}
