/**
 * 
 */
package fr.lirmm.graphik.graal.rulesetanalyser.graph;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.rulesetanalyser.util.PredicatePosition;
import grph.Grph;
import grph.in_memory.InMemoryGrph;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import toools.math.IntIterator;

import com.carrotsearch.hppc.cursors.IntCursor;

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

	private TreeSet<Integer> specialEdges;

	private int nbVertices = 0;

	private Map<PredicatePosition, Integer> graphEntryMap;
	private boolean[] isFiniteRank = null;

	private Grph graph = new InMemoryGrph();
	private Iterable<Rule> rules;

	public GraphPositionDependencies(Iterable<Rule> rules) {
		this.specialEdges = new TreeSet<Integer>();
		this.graphEntryMap = new HashMap<PredicatePosition, Integer>();
		this.rules = rules;
		init();
	}

	public boolean isWeaklyAcyclic() {
		for (int e : specialEdges) {
			int head = this.graph.getDirectedSimpleEdgeHead(e);
			int tail = this.graph.getDirectedSimpleEdgeTail(e);

			boolean[] markedVertex = new boolean[nbVertices];
			markedVertex[head] = true;
			markedVertex[tail] = true;

			if (this.findSpecialCycle(head, markedVertex, tail)) {
				return false;
			}

		}
		return true;
	}

	public boolean isFiniteRank(Predicate p, int position) {
		return isFiniteRank(new PredicatePosition(p, position));
	}

	public void initFiniteRank() {
		if (this.isFiniteRank == null) {
			this.isFiniteRank = new boolean[nbVertices];
			Collection<toools.set.IntSet> stronglyConnectedComponentCollection = this.graph
					.getStronglyConnectedComponents();
			for (toools.set.IntSet stronglyConnectedComponent : stronglyConnectedComponentCollection) {
				// TODO fix grph library
				IntIterator iIt = stronglyConnectedComponent
						.iteratorPrimitive();
				IntIterator jIt = stronglyConnectedComponent
						.iteratorPrimitive();
				int i, j;
				boolean componentIsFiniteRank = true;
				while (iIt.hasNext()) {
					i = iIt.next();
					while (jIt.hasNext()) {
						j = jIt.next();
						IntIterator edgesIt = this.graph.getEdgesConnecting(i,
								j).iteratorPrimitive();
						int edge;
						while (edgesIt.hasNext()) {
							edge = edgesIt.next();
							if (specialEdges.contains(new Integer(edge))) {
								componentIsFiniteRank = false;
							}
						}
					}
				}

				// store information
				iIt = stronglyConnectedComponent.iteratorPrimitive();
				while (iIt.hasNext()) {
					i = iIt.next();
					this.isFiniteRank[i] = componentIsFiniteRank;
				}

			}
		}
	}

	public boolean isFiniteRank(PredicatePosition p) {
		initFiniteRank();
		Integer integer = this.graphEntryMap.get(p);
		if (integer != null) {
			return this.isFiniteRank[integer];
		} else {
			return true;
		}
	}

	/**
	 * @param v
	 * @param markedVertex
	 * @return
	 */
	private boolean findSpecialCycle(int vertex, boolean[] markedVertex,
			int vertexCible) {

		for (IntCursor vcursor : this.graph.getOutNeighbors(vertex)) {
			int v = vcursor.value;
			if (v == vertexCible) {
				return true;
			} else if (!markedVertex[v]) {
				markedVertex[v] = true;
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

	private int getVertice(PredicatePosition predicatePosition) {
		Integer vertice = this.graphEntryMap.get(predicatePosition);
		if (vertice == null) {
			vertice = this.nbVertices++;
			this.graphEntryMap.put(predicatePosition, vertice);
		}
		return vertice;
	}

	/**
	 * @param predicatePosition
	 * @param predicatePosition2
	 */
	private void addSpecialEdge(PredicatePosition predicatePosition,
			PredicatePosition predicatePosition2) {
		int vertice1 = this.getVertice(predicatePosition);
		int vertice2 = this.getVertice(predicatePosition2);
		int edge = this.graph.addDirectedSimpleEdge(vertice1, vertice2);
		this.specialEdges.add(edge);
	}

	/**
	 * @param predicatePosition
	 * @param predicatePosition2
	 */
	private void addEdge(PredicatePosition predicatePosition,
			PredicatePosition predicatePosition2) {
		int vertice1 = this.getVertice(predicatePosition);
		int vertice2 = this.getVertice(predicatePosition2);
		this.graph.addDirectedSimpleEdge(vertice1, vertice2);
	}
}
