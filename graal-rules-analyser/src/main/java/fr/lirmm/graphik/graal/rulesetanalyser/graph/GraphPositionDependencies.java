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
 /**
 * 
 */
package fr.lirmm.graphik.graal.rulesetanalyser.graph;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.StrongConnectivityInspector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.graal.rulesetanalyser.util.PredicatePosition;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;


/**
 * The graph of position dependencies noted Gpos is a directed graph built from
 * a rule set as follows: first, for each predicate p and for each of its
 * positions p[i] a vertex is added. Then, for each rule and for each variable x
 * that occurs at some position p[i] in the rule body: (1) for each position
 * r[j] where x also occurs in the rule head, a normal edge is added from p[i]
 * to r[j], and (2) for each position q[k] in the rule head where some
 * existentially quantified variable appears, a special edge is added from p[i]
 * to q[k]. In this graph, a vertex (hence a predicate position) is of finite
 * rank if there is no circuit containing a special edge and passing through
 * this vertex.
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * @author Swan Rocher
 * 
 */
public class GraphPositionDependencies {

	public static class SpecialEdge extends DefaultEdge {
		private static final long serialVersionUID = 3660050932528046714L;
	}

	private Set<PredicatePosition> isFiniteRank = null;

	private DirectedGraph<PredicatePosition, DefaultEdge> graph = new DefaultDirectedGraph<PredicatePosition, DefaultEdge>(DefaultEdge.class);
	private Iterable<Rule> rules;

	public GraphPositionDependencies(Iterable<Rule> rules) {
		this.rules = rules;
		init();
	}
	
	public Set<DefaultEdge> edgeSet() {
		return this.graph.edgeSet();
	}
	
	public PredicatePosition getEdgeTarget(DefaultEdge e) {
		return this.graph.getEdgeTarget(e);
	}
	
	public PredicatePosition getEdgeSource(DefaultEdge e) {
		return this.graph.getEdgeSource(e);
	}

	public boolean isWeaklyAcyclic() {
		for (DefaultEdge e : this.graph.edgeSet()) {
			if (e instanceof SpecialEdge) {
				PredicatePosition head = this.graph.getEdgeTarget(e);
				PredicatePosition tail = this.graph.getEdgeSource(e);

				Set<PredicatePosition> markedVertex = new TreeSet<PredicatePosition>();
				markedVertex.add(head);
				markedVertex.add(tail);

				if (this.findSpecialCycle(head, markedVertex, tail)) {
					return false;
				}
			}
		}
		return true;
	}

	public boolean isFiniteRank(Predicate p, int position) {
		return isFiniteRank(new PredicatePosition(p, position));
	}

	public void initFiniteRank() {
		if (this.isFiniteRank == null) {
			this.isFiniteRank = new TreeSet<PredicatePosition>();

			StrongConnectivityInspector<PredicatePosition, DefaultEdge> sccInspector = new StrongConnectivityInspector<PredicatePosition, DefaultEdge>(
					graph);
			List<Set<PredicatePosition>> sccList = sccInspector
					.stronglyConnectedSets();
			for (Set<PredicatePosition> scc : sccList) {
				boolean componentIsFiniteRank = true;
				for (PredicatePosition p1 : scc) {
					for (PredicatePosition p2 : scc) {
						for (DefaultEdge edge : graph.getAllEdges(p1, p2)) {
							if (edge instanceof SpecialEdge) {
								componentIsFiniteRank = false;
							}
						}
					}
				}

				// store information
				if (componentIsFiniteRank) {
					for (PredicatePosition p : scc) {
						this.isFiniteRank.add(p);
					}
				}
			}
		}
	}

	public boolean isFiniteRank(PredicatePosition p) {
		initFiniteRank();
		return this.isFiniteRank.contains(p);
	}

	/**
	 * @param vertex
	 * @param markedVertex
	 * @return true if there is a specialCycle, false otherwise.
	 */
	private boolean findSpecialCycle(PredicatePosition vertex, Set<PredicatePosition> markedVertex,
			PredicatePosition vertexCible) {

		for (DefaultEdge edge : this.graph.outgoingEdgesOf(vertex)) {
			PredicatePosition v = this.graph.getEdgeTarget(edge);
			if (v.equals(vertexCible)) {
				return true;
			} else if (!markedVertex.contains(v)) {
				markedVertex.add(v);
				if (this.findSpecialCycle(v, markedVertex, vertexCible)) {
					return true;
				}
			}
		}
		return false;
	}

	private void init() {
		int bodyTermIndex, headTermIndex;
		Set<Variable> existentials;

		for (Rule r : this.rules) {
			existentials = r.getExistentials();

			CloseableIteratorWithoutException<Atom> bodyIt = r.getBody().iterator();
			while (bodyIt.hasNext()) {
				Atom bodyAtom = bodyIt.next();
				bodyTermIndex = -1;
				for (Term bodyTerm : bodyAtom) {
					++bodyTermIndex;
					if (r.getHead().getTerms().contains(bodyTerm)) {
						CloseableIteratorWithoutException<Atom> headIt = r.getHead().iterator();
						while (headIt.hasNext()) {
							Atom headAtom = headIt.next();
							headTermIndex = -1;
							for (Term headTerm : headAtom) {
								++headTermIndex;
								if (bodyTerm.equals(headTerm)) {
									this.addEdge(
											new PredicatePosition(bodyAtom.getPredicate(), bodyTermIndex),
											new PredicatePosition(headAtom.getPredicate(), headTermIndex));
								} else if (existentials.contains(headTerm)) {
									this.addSpecialEdge(
											new PredicatePosition(bodyAtom.getPredicate(), bodyTermIndex),
											new PredicatePosition(headAtom.getPredicate(), headTermIndex));
								}
							}
						}
					} else {
						if (!this.graph.containsVertex(new PredicatePosition(bodyAtom.getPredicate(), bodyTermIndex))) {
							this.graph.addVertex(new PredicatePosition(bodyAtom.getPredicate(), bodyTermIndex));
						}
					}
				}
			}
		}
	}

	/**
	 * @param predicatePosition
	 * @param predicatePosition2
	 */
	private void addSpecialEdge(PredicatePosition predicatePosition,
			PredicatePosition predicatePosition2) {
		if(this.graph.containsEdge(predicatePosition, predicatePosition2)) {
			this.graph.removeEdge(predicatePosition, predicatePosition2);
		} else {
			if(!this.graph.containsVertex(predicatePosition)) {
				this.graph.addVertex(predicatePosition);
			}
			if(!this.graph.containsVertex(predicatePosition2)) {
				this.graph.addVertex(predicatePosition2);
			}
		}
		this.graph.addEdge(predicatePosition, predicatePosition2, new SpecialEdge());

	}

	/**
	 * @param predicatePosition
	 * @param predicatePosition2
	 */
	private void addEdge(PredicatePosition predicatePosition,
			PredicatePosition predicatePosition2) {
		if(!this.graph.containsEdge(predicatePosition, predicatePosition2)) {
			if(!this.graph.containsVertex(predicatePosition)) {
				this.graph.addVertex(predicatePosition);
			}
			if(!this.graph.containsVertex(predicatePosition2)) {
				this.graph.addVertex(predicatePosition2);
			}
			this.graph.addEdge(predicatePosition, predicatePosition2);
		}
	}
}
