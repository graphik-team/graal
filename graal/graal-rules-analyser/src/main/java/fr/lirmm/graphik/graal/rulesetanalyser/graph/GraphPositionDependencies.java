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

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.term.Term;
import fr.lirmm.graphik.graal.rulesetanalyser.util.PredicatePosition;


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
 * @param <E>
 * @param <V>
 * 
 */
public class GraphPositionDependencies {

	private static class SpecialEdge extends DefaultEdge {
		private static final long serialVersionUID = 3660050932528046714L;
	}

	private Set<PredicatePosition> isFiniteRank = null;

	private DirectedGraph<PredicatePosition, DefaultEdge> graph = new DefaultDirectedGraph<PredicatePosition, DefaultEdge>(DefaultEdge.class);
	private Iterable<Rule> rules;

	public GraphPositionDependencies(Iterable<Rule> rules) {
		this.rules = rules;
		init();
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
	 * @param v
	 * @param markedVertex
	 * @return
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
		Set<Term> existentials;

		for (Rule r : this.rules) {
			existentials = r.getExistentials();

			for (Atom bodyAtom : r.getBody()) {
				bodyTermIndex = -1;
				for (Term bodyTerm : bodyAtom) {
					++bodyTermIndex;
					for (Atom headAtom : r.getHead()) {
						headTermIndex = -1;
						for (Term headTerm : headAtom) {
							++headTermIndex;
							if (bodyTerm.equals(headTerm)) {
								this.addEdge(
										new PredicatePosition(bodyAtom
												.getPredicate(), bodyTermIndex),
										new PredicatePosition(headAtom
												.getPredicate(), headTermIndex));
							} else if (existentials.contains(headTerm)) {
								this.addSpecialEdge(
										new PredicatePosition(bodyAtom
												.getPredicate(), bodyTermIndex),
										new PredicatePosition(headAtom
												.getPredicate(), headTermIndex));
							}
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
