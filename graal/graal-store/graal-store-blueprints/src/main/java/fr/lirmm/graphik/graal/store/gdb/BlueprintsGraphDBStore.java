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
 /**
 * 
 */
package fr.lirmm.graphik.graal.store.gdb;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.GraphQuery;
import com.tinkerpop.blueprints.Vertex;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
import fr.lirmm.graphik.graal.core.impl.DefaultAtom;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.core.term.Term;
import fr.lirmm.graphik.graal.core.term.Term.Type;
import fr.lirmm.graphik.graal.store.GraphDBStore;
import fr.lirmm.graphik.util.MethodNotImplementedError;

/**
 * BlueprintsGraphDBStore wrap Blueprints API {@link http
 * ://blueprints.tinkerpop.com} into an AtomSet. Blueprints API allows you to
 * use many Graph Database like Neo4j, Sparksee, OrientDB, Titan...
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class BlueprintsGraphDBStore extends GraphDBStore {

	private final Graph graph;

	public BlueprintsGraphDBStore(Graph graph) {
		this.graph = graph;
		init();
	}

	private void init() {
		try {
			this.graph.getVertices("class", "");
		} catch (IllegalArgumentException e) {
			Vertex v = this.graph.addVertex(null);
			v.setProperty("class", "");
		}
	}

	@Override
	protected void finalize() throws Throwable {
		this.close();
		super.finalize();
	}

	// //////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// //////////////////////////////////////////////////////////////////////////

	@Override
	public void close() {
		this.graph.shutdown();
	}

	@Override
	public boolean contains(Atom atom) {
		GraphQuery query = this.graph.query();
		query.has("class", "atom");
		query.has("predicate", predicateToString(atom.getPredicate()));

		int i = 0;
		for (Term t : atom) {
			query.has("term" + i++, termToString(t));
		}

		return query.vertices().iterator().hasNext();
	}

	@Override
	public Iterator<Predicate> predicatesIterator() {
		return new PredicateIterable(graph.getVertices("class", "predicate"))
				.iterator();
	}

	@Override
	public Set<Predicate> getPredicates() {
		TreeSet<Predicate> set = new TreeSet<Predicate>();
		Iterator<Predicate> it = this.predicatesIterator();
		while (it.hasNext()) {
			set.add(it.next());
		}
		return set;
	}

	@Override
	public Set<Term> getTerms() {
		Set<Term> terms = new TreeSet<Term>();
		for (Vertex v : this.graph.getVertices("class", "term")) {
			terms.add(vertexToTerm(v));
		}
		return terms;
	}

	@Override
	public Set<Term> getTerms(Type type) {
		Set<Term> terms = new TreeSet<Term>();
		GraphQuery query = this.graph.query();
		query.has("class", "term");
		query.has("type", type.toString());

		for (Vertex v : query.vertices()) {
			terms.add(vertexToTerm(v));
		}
		return terms;
	}

	@Override
	public boolean add(Atom atom) {
		Vertex atomVertex = graph.addVertex(null);
		atomVertex.setProperty("class", "atom");
		atomVertex.setProperty("predicate",
				predicateToString(atom.getPredicate()));

		Vertex predicateVertex = this.add(atom.getPredicate());
		this.graph.addEdge(null, atomVertex, predicateVertex, "predicate");

		int i = 0;
		for (Term t : atom) {
			atomVertex.setProperty("term" + i, termToString(t));

			Vertex termVertex = this.add(t);
			Edge e = graph.addEdge(null, atomVertex, termVertex, "term");
			e.setProperty("index", i++);
		}

		return true;
	}

	private Vertex add(Predicate predicate) {
		Vertex v = null;

		GraphQuery query = this.graph.query();
		query.has("class", "predicate");
		query.has("value", predicate.getIdentifier());
		query.has("arity", predicate.getArity());
		Iterator<Vertex> it = query.vertices().iterator();

		if (it.hasNext()) {
			v = it.next();
		} else {
			v = graph.addVertex(null);
			v.setProperty("class", "predicate");
			v.setProperty("value", predicate.getIdentifier());
			v.setProperty("arity", predicate.getArity());
		}
		return v;
	}

	private Vertex add(Term term) {
		Vertex v = null;

		GraphQuery query = this.graph.query();
		query.has("class", "term");
		query.has("value", term.getIdentifier().toString());
		query.has("type", term.getType().toString());
		Iterator<Vertex> it = query.vertices().iterator();

		if (it.hasNext()) {
			v = it.next();
		} else {
			v = this.graph.addVertex(null);
			v.setProperty("class", "term");
			v.setProperty("value", term.getIdentifier().toString());
			v.setProperty("type", term.getType().toString());
		}
		return v;
	}

	@Override
	public boolean addAll(Iterable<? extends Atom> atoms) {
		for (Atom a : atoms) {
			this.add(a);
		}
		return true;
	}

	@Override
	public boolean remove(Atom atom) {
		// TODO implement this method
		throw new MethodNotImplementedError();
	}

	@Override
	public boolean removeAll(Iterable<? extends Atom> atoms)
			throws AtomSetException {
		// TODO implement this method
		throw new MethodNotImplementedError();
	}

	@Override
	public void clear() {
		// TODO implement this method
		throw new MethodNotImplementedError();
	}

	@Override
	public Iterator<Atom> iterator() {
		Iterator<Vertex> it = this.graph.getVertices("class", "atom")
				.iterator();
		return new AtomIterator(it);
	}

	// //////////////////////////////////////////////////////////////////////////
	// PRIVATE STATIC METHODS
	// //////////////////////////////////////////////////////////////////////////

	private static Predicate vertexToPredicate(Vertex vertex) {
		String label = vertex.getProperty("value");
		int arity = vertex.getProperty("arity");
		return new Predicate(label, arity);
	}

	private static Term vertexToTerm(Vertex vertex) {
		return DefaultTermFactory.instance().createTerm(
				vertex.getProperty("value"),
				Term.Type.valueOf(vertex
				.getProperty("type").toString()));
	}

	private static Atom vertexToAtom(Vertex vertex) {
		Iterator<Edge> it = vertex.getEdges(Direction.OUT, "predicate")
				.iterator();
		Vertex predicateVertex = it.next().getVertex(Direction.IN);
		Predicate p = vertexToPredicate(predicateVertex);

		Term[] terms = new Term[p.getArity()];

		for (Edge e : vertex.getEdges(Direction.OUT, "term")) {
			Vertex t = e.getVertex(Direction.IN);
			terms[(Integer) e.getProperty("index")] = vertexToTerm(t);
		}

		return new DefaultAtom(p, terms);
	}

	private static String predicateToString(Predicate p) {
		return p.getIdentifier() + "@" + p.getArity();
	}

	private static String termToString(Term t) {
		return t.getIdentifier().toString() + "@" + t.getType().toString();
	}

	// //////////////////////////////////////////////////////////////////////////
	// PRIVATE CLASSES
	// //////////////////////////////////////////////////////////////////////////

	private static class AtomIterator implements Iterator<Atom> {
		Iterator<Vertex> it;

		public AtomIterator(Iterator<Vertex> it) {
			this.it = it;
		}

		@Override
		public boolean hasNext() {
			return this.it.hasNext();
		}

		@Override
		public Atom next() {
			return vertexToAtom(this.it.next());
		}

		@Override
		public void remove() {
			this.it.remove();
		}
	}

	private static class PredicateIterable implements Iterable<Predicate> {

		Iterable<Vertex> iterable;

		public PredicateIterable(Iterable<Vertex> vertices) {
			this.iterable = vertices;
		}

		@Override
		public Iterator<Predicate> iterator() {
			return new PredicateIterator(this.iterable.iterator());
		}
	}

	private static class PredicateIterator implements Iterator<Predicate> {

		Iterator<Vertex> it;

		public PredicateIterator(Iterator<Vertex> iterator) {
			this.it = iterator;
		}

		@Override
		public boolean hasNext() {
			return this.it.hasNext();
		}

		@Override
		public Predicate next() {
			return vertexToPredicate(this.it.next());
		}

		@Override
		public void remove() {
			this.it.remove();
		}

	}

}
