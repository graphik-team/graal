/**
 * 
 */
package fr.lirmm.graphik.graal.store.gdb;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.Term.Type;
import fr.lirmm.graphik.graal.core.atomset.AtomSetException;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class Neo4jStore extends GraphDBStore {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Neo4jStore.class);

	private static enum NodeType implements Label {
		TERM, ATOM, PREDICATE
	};

	private static enum RelationshipType implements
			org.neo4j.graphdb.RelationshipType {
		PREDICATE, TERM
	}

	private GraphDatabaseService graph;
	private ExecutionEngine cypherEngine;

	// //////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// //////////////////////////////////////////////////////////////////////////

	/**
	 * 
	 * @param graph
	 */
	public Neo4jStore(GraphDatabaseService graph) {
		this.graph = graph;
		Transaction tx = graph.beginTx();
		try {
			if (!graph.schema().getConstraints().iterator().hasNext()) {
				graph.schema().constraintFor(NodeType.TERM)
						.assertPropertyIsUnique("value").create();
				graph.schema().constraintFor(NodeType.PREDICATE)
						.assertPropertyIsUnique("label").create();
			}

			cypherEngine = new ExecutionEngine(graph);
			tx.success();
		} finally {
			tx.close();
		}
	}

	/**
	 * 
	 * @param filepath
	 */
	public Neo4jStore(String filepath) {
		this(new GraphDatabaseFactory().newEmbeddedDatabase(filepath));
	}

	/**
	 * @param neo4jDirectory
	 */
	public Neo4jStore(File neo4jDirectory) {
		this(neo4jDirectory.getAbsolutePath());
	}

	// //////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// //////////////////////////////////////////////////////////////////////////

	@Override
	public boolean add(Atom atom) {
		boolean isAdded = false;
		Transaction tx = graph.beginTx();
		try {
			isAdded = add(atom,tx);
			tx.success();
		} finally {
			tx.close();
		}
		return isAdded;
	}
	
	@Override
	public boolean addAll(Iterator<? extends Atom> it) {
		boolean isChanged = false;
		Transaction tx = graph.beginTx();
		try {
			while(it.hasNext()) {
				isChanged = this.add(it.next(), tx) || isChanged;
			}
			tx.success();
		} finally {
			tx.close();
		}
		return isChanged;
	}
	
	private boolean add(Atom atom, Transaction transaction) {
		if (!this.contains(atom, transaction)) {
			Node atomNode = this.graph.createNode(NodeType.ATOM);

			Node predicateNode = createPredicateIfNotExist(atom
					.getPredicate());
			atomNode.createRelationshipTo(predicateNode,
					RelationshipType.PREDICATE);

			int i = 0;
			for (Term term : atom) {
				Relationship r = atomNode.createRelationshipTo(
						createTermIfNotExist(term), RelationshipType.TERM);
				r.setProperty("index", i++);
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean remove(Atom atom) {
		boolean isRemoved = false;
		Transaction tx = graph.beginTx();
		try {
			isRemoved = remove(atom,tx);
			tx.success();
		} finally {
			tx.close();
		}
		return isRemoved;
	}
	
	@Override
	public boolean removeAll(Iterator<? extends Atom> it) {
		boolean isChanged = false;
		Transaction tx = graph.beginTx();
		try {
			while(it.hasNext()) {
				isChanged = this.remove(it.next(), tx) || isChanged;
			}
			tx.success();
		} finally {
			tx.close();
		}
		return isChanged;
	}
	
	private boolean remove(Atom atom, Transaction transaction) {
		String query = deleteAtomIntoCypherQuery(atom);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(query);
		}
		ResourceIterator<Node> result = this.cypherEngine.execute(query).columnAs("atom");
		boolean contains = result.hasNext();
		result.close();
		return contains;
	}

	@Override
	public Iterator<Atom> iterator() {
		Transaction transaction = graph.beginTx();
		return new Neo4jAtomIterator(transaction, this.cypherEngine.execute(
				"match (atom:ATOM) return atom").iterator());
	}

	@Override
	public Iterator<Term> termsIterator() throws AtomSetException {
		Transaction transaction = graph.beginTx();
		return new Neo4jTermIterator(transaction, this.cypherEngine.execute(
				"match (term:TERM) return term").iterator());
	}

	@Override
	public Set<Term> getTerms() throws AtomSetException {
		TreeSet<Term> set = new TreeSet<Term>();
		Iterator<Term> it = this.termsIterator();
		while (it.hasNext()) {
			set.add(it.next());
		}
		return set;
	}

	@Override
	public Iterator<Term> termsIterator(Type type) throws AtomSetException {
		Map<String, Object> params = new TreeMap<String, Object>();
		params.put("type", type.toString());
		Transaction transaction = graph.beginTx();

		return new Neo4jTermIterator(transaction, this.cypherEngine.execute(
				"match (term:TERM {type : { type }}) return term", params)
				.iterator());
	}

	@Override
	public Set<Term> getTerms(Type type) throws AtomSetException {
		TreeSet<Term> set = new TreeSet<Term>();
		Iterator<Term> it = this.termsIterator(type);
		while (it.hasNext()) {
			set.add(it.next());
		}
		return set;
	}

	@Override
	public Iterator<Predicate> predicatesIterator() throws AtomSetException {
		Transaction transaction = graph.beginTx();
		return new Neo4jPredicateIterator(transaction, this.cypherEngine
				.execute("match (predicate:PREDICATE) return predicate")
				.iterator());
	}

	@Override
	public Set<Predicate> getPredicates() throws AtomSetException {
		TreeSet<Predicate> set = new TreeSet<Predicate>();
		Iterator<Predicate> it = this.predicatesIterator();
		while (it.hasNext()) {
			set.add(it.next());
		}
		return set;
	}

	@Override
	public void clear() {
		Transaction transaction = graph.beginTx();
		this.cypherEngine
				.execute("MATCH (n) OPTIONAL MATCH (n)-[r]-() DELETE n, r");
		transaction.success();
		transaction.close();
	}

	@Override
	public boolean contains(Atom atom) {
		Transaction transaction = graph.beginTx();
		boolean contains = false;
		try {
			contains = this.contains(atom, transaction);
		} finally {
			transaction.close();
		}
		return contains;
	}

	private boolean contains(Atom atom, Transaction transaction) {
		String query = containsAtomIntoCypherQuery(atom);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(query);
		}
		ResourceIterator<Node> result = this.cypherEngine.execute(query).columnAs("atom");
		boolean res = result.hasNext();
		result.close();
		return res;
	}

	@Override
	public void close() {
		this.graph.shutdown();
	}

	// //////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// //////////////////////////////////////////////////////////////////////////

	/**
	 * @param label
	 * @return
	 * @throws StoreException
	 */
	private Node getTerm(Term term) {
		Node node = null;
		ResourceIterator<Node> it = this.graph.findNodesByLabelAndProperty(
				NodeType.TERM, "value", term.getValue()).iterator();
		if (it.hasNext()) {
			node = it.next();
		}
		it.close();
		return node;
	}

	private Node createTermIfNotExist(Term term) {
		Node node = this.getTerm(term);
		if (node == null) {
			node = this.graph.createNode(NodeType.TERM);
			node.setProperty("value", term.getValue().toString());
			node.setProperty("type", term.getType().toString());
		}
		return node;
	}

	private static Term nodeToTerm(Node node) {
		return new Term(node.getProperty("value"), Term.Type.valueOf(node
				.getProperty("type").toString()));
	}

	private Node getPredicate(Predicate predicate) {
		Node node = null;
		ResourceIterator<Node> it = this.graph.findNodesByLabelAndProperty(
				NodeType.PREDICATE, "value", predicate.getLabel()).iterator();
		while (node == null && it.hasNext()) {
			Node tmp = it.next();
			if (tmp.getProperty("arity").equals(predicate.getArity())) {
				node = tmp;
			}
		}
		it.close();
		return node;
	}

	/**
	 * @param predicate
	 * @return
	 */
	private Node createPredicateIfNotExist(Predicate predicate) {
		Node node = this.getPredicate(predicate);
		if (node == null) {
			node = this.graph.createNode(NodeType.PREDICATE);
			node.setProperty("value", predicate.getLabel());
			node.setProperty("arity", predicate.getArity());
		}
		return node;
	}

	private static Predicate nodeToPredicate(Node node) {
		return new Predicate(node.getProperty("value").toString(),
				(int) node.getProperty("arity"));
	}

	// //////////////////////////////////////////////////////////////////////////
	// CYPHER QUERIES GENERATION
	// //////////////////////////////////////////////////////////////////////////

	private static String deleteAtomIntoCypherQuery(Atom a) {
		StringBuilder sb = new StringBuilder();

		sb.append("MATCH ");
		atomToCypher(sb, a);
		sb.append("DELETE atom, rel_predicate");
		int i = -1;
		Iterator<Term> it = a.iterator();
		while (it.hasNext()) {
			++i;
			it.next();
			sb.append(", term").append(i).append(", rel_term").append(i);

		}

		sb.append(" RETURN atom");

		return sb.toString();
	}

	private static String containsAtomIntoCypherQuery(Atom a) {
		StringBuilder sb = new StringBuilder();

		sb.append("MATCH ");
		atomToCypher(sb, a);
		sb.append("RETURN atom");

		return sb.toString();
	}

	private static void atomToCypher(StringBuilder sb, Atom a) {
		Predicate p = a.getPredicate();
		sb.append("(atom:ATOM), (predicate:PREDICATE { value: '")
				.append(p.getLabel()).append("', arity: ").append(p.getArity())
				.append(" }), ");

		int i = -1;
		for (Term t : a) {
			// (term?:TERM {value: '?', arity: ?}),
			// (atom)-[:TERM { index: ? }->(term?)
			++i;
			sb.append("(term").append(i).append(":TERM {value: '")
					.append(t.getValue().toString()).append("', type: '")
					.append(t.getType().toString())
					.append("' }), (atom)-[rel_term").append(i)
					.append(":TERM { index: ").append(i).append(" }]->(term")
					.append(i).append("), ");
		}

		sb.append("(atom)-[rel_predicate:PREDICATE]->(predicate) ");
	}

	// //////////////////////////////////////////////////////////////////////////
	// PRIVATE STATIC CLASS
	// //////////////////////////////////////////////////////////////////////////

	private static abstract class Neo4jElementIterator<E> implements
			Iterator<E> {
		ResourceIterator<Map<String, Object>> iterator;
		Transaction transaction;

		/**
		 * @param iterator
		 */
		public Neo4jElementIterator(Transaction transaction,
				ResourceIterator<Map<String, Object>> iterator) {
			this.transaction = transaction;
			this.iterator = iterator;
		}

		@Override
		protected void finalize() throws Throwable {
			this.close();
			super.finalize();
		}

		private void close() {
			this.iterator.close();
			this.transaction.success();
			this.transaction.close();
		}

		@Override
		public boolean hasNext() {
			if (iterator.hasNext()) {
				return true;
			} else {
				this.close();
				return false;
			}
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	private static class Neo4jAtomIterator extends Neo4jElementIterator<Atom> {

		/**
		 * 
		 * @param transaction
		 * @param iterator
		 */
		public Neo4jAtomIterator(Transaction transaction,
				ResourceIterator<Map<String, Object>> iterator) {
			super(transaction, iterator);
		}

		@Override
		public Atom next() {
			Node atomNode = (Node) this.iterator.next().get("atom");
			Node predicateNode = atomNode.getSingleRelationship(
					RelationshipType.PREDICATE, Direction.OUTGOING)
					.getEndNode();
			Predicate predicate = nodeToPredicate(predicateNode);

			Term[] terms = new Term[predicate.getArity()];

			for (Relationship rel : atomNode.getRelationships(
					Direction.OUTGOING, RelationshipType.TERM)) {
				Node termNode = rel.getEndNode();

				terms[(int) rel.getProperty("index")] = nodeToTerm(termNode);
			}

			return new DefaultAtom(predicate, terms);
		}

	}

	private static class Neo4jTermIterator extends Neo4jElementIterator<Term> {

		/**
		 * 
		 * @param transaction
		 * @param iterator
		 */
		public Neo4jTermIterator(Transaction transaction,
				ResourceIterator<Map<String, Object>> iterator) {
			super(transaction, iterator);
		}

		@Override
		public Term next() {
			return nodeToTerm((Node) iterator.next().get("term"));
		}

	}

	private static class Neo4jPredicateIterator extends
			Neo4jElementIterator<Predicate> {

		/**
		 * 
		 * @param transaction
		 * @param iterator
		 */
		public Neo4jPredicateIterator(Transaction transaction,
				ResourceIterator<Map<String, Object>> iterator) {
			super(transaction, iterator);
		}

		@Override
		public Predicate next() {
			return nodeToPredicate((Node) iterator.next().get("predicate"));
		}

	}

}
