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
package fr.lirmm.graphik.graal.store.gdb;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
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

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Term.Type;
import fr.lirmm.graphik.graal.api.core.TermGenerator;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.DefaultVariableGenerator;
import fr.lirmm.graphik.graal.core.store.GraphDBStore;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.util.MethodNotImplementedError;
import fr.lirmm.graphik.util.stream.AbstractCloseableIterator;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.IteratorException;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
@SuppressWarnings("deprecation")
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
	private Map<Thread, Transaction> transactions;

	// //////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// //////////////////////////////////////////////////////////////////////////

	/**
	 * 
	 * @param graph
	 */
	public Neo4jStore(GraphDatabaseService graph) {
		transactions = new HashMap<Thread, Transaction>();
		this.graph = graph;
		this.checkTransaction();
		try {
			if (!graph.schema().getConstraints().iterator().hasNext()) {
				graph.schema().indexFor(NodeType.TERM);
				graph.schema().indexFor(NodeType.PREDICATE);
				// TODO manage constraints
				/*graph.schema().constraintFor(NodeType.TERM)
						.assertPropertyIsUnique("value").create();
				graph.schema().constraintFor(NodeType.PREDICATE)
						.assertPropertyIsUnique("value").create();*/
			}

			cypherEngine = new ExecutionEngine(graph);
			this.successTransaction();
		} finally {
			this.reloadTransaction();
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
		this.checkTransaction();
		try {
			isAdded = add(atom, null);
			this.successTransaction();
		} finally {
			this.reloadTransaction();
		}
		return isAdded;
	}
	
	@Override
	public boolean addAll(CloseableIterator<? extends Atom> it) throws AtomSetException {
		boolean isChanged = false;
		this.checkTransaction();
		try {
			while (it.hasNext()) {
				isChanged = this.add(it.next(), null) || isChanged;
			}
			this.successTransaction();
		} catch (IteratorException e) {
			throw new AtomSetException("An errors occurs while iterating atoms to add", e);
		} finally {
			this.reloadTransaction();
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
		this.checkTransaction();
		try {
			isRemoved = remove(atom, null);
			this.successTransaction();
		} finally {
			this.reloadTransaction();
		}
		return isRemoved;
	}
	
	@Override
	public boolean removeAll(CloseableIterator<? extends Atom> it) throws AtomSetException {
		boolean isChanged = false;
		this.checkTransaction();
		try {
			while(it.hasNext()) {
				isChanged = this.remove(it.next(), null) || isChanged;
			}
			this.successTransaction();
		} catch (IteratorException e) {
			throw new AtomSetException("An errors occurs while iterating atoms to remove", e);
		} finally {
			this.reloadTransaction();
		}
		return isChanged;
	}
	
	private boolean remove(Atom atom, Transaction transaction) {
		throw new MethodNotImplementedError();
		/*String query = deleteAtomIntoCypherQuery(atom);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(query);
		}
		ResourceIterator<Node> result = this.cypherEngine.execute(query).columnAs("atom");
		boolean contains = result.hasNext();
		result.close();
		return contains;*/
	}

	@Override
	public CloseableIterator<Atom> iterator() {
		this.checkTransaction();
		return new Neo4jAtomIterator(this.getTransaction(), this.cypherEngine.execute(
				"match (atom:ATOM) return atom").iterator());
	}

	@Override
	public CloseableIterator<Term> termsIterator() throws AtomSetException {
		this.checkTransaction();
		return new Neo4jTermIterator(this.getTransaction(), this.cypherEngine.execute(
				"match (term:TERM) return term").iterator());
	}

	@Override
	public Set<Term> getTerms() throws AtomSetException {
		TreeSet<Term> set = new TreeSet<Term>();
		CloseableIterator<Term> it = this.termsIterator();
		try {
			while (it.hasNext()) {
				set.add(it.next());
			}
		} catch (IteratorException e) {
			throw new AtomSetException("An errors occurs while iterating terms", e);
		}
		it.close();
		return set;
	}

	@Override
	@Deprecated
	public CloseableIterator<Term> termsIterator(Type type) throws AtomSetException {
		Map<String, Object> params = new TreeMap<String, Object>();
		params.put("type", type.toString());
		this.checkTransaction();
		return new Neo4jTermIterator(this.getTransaction(), this.cypherEngine.execute(
				"match (term:TERM {type : { type }}) return term", params)
				.iterator());
	}

	@Override
	@Deprecated
	public Set<Term> getTerms(Type type) throws AtomSetException {
		TreeSet<Term> set = new TreeSet<Term>();
		CloseableIterator<Term> it = this.termsIterator(type);
		try {
			while (it.hasNext()) {
				set.add(it.next());
			}
		} catch (IteratorException e) {
			throw new AtomSetException("An errors occurs while iterating terms", e);
		}
		return set;
	}

	@Override
	public CloseableIterator<Predicate> predicatesIterator() throws AtomSetException {
		this.checkTransaction();
		return new Neo4jPredicateIterator(this.getTransaction(), this.cypherEngine
				.execute("match (predicate:PREDICATE) return predicate")
				.iterator());
	}

	@Override
	public Set<Predicate> getPredicates() throws AtomSetException {
		TreeSet<Predicate> set = new TreeSet<Predicate>();
		CloseableIterator<Predicate> it = this.predicatesIterator();
		try {
			while (it.hasNext()) {
				set.add(it.next());
			}
		} catch (IteratorException e) {
			throw new AtomSetException("An errors occurs while iterating predicates", e);
		}
		return set;
	}

	@Override
	public void clear() {
		this.checkTransaction();
		this.cypherEngine
				.execute("MATCH (n) OPTIONAL MATCH (n)-[r]-() DELETE n, r");
		this.successTransaction();
		this.reloadTransaction();
	}

	@Override
	public boolean contains(Atom atom) {
		this.checkTransaction();
		return this.contains(atom, null);
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
	public CloseableIterator<Atom> match(Atom atom, Substitution s) {
		String query = matchAtomIntoCypherQuery(atom, s);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(query);
		}
		ResourceIterator<Map<String, Object>> result = this.cypherEngine.execute(query).iterator();

		return new Neo4jAtomIterator(this.getTransaction(), result);
	}

	@Override
	public CloseableIterator<Atom> atomsByPredicate(Predicate p) throws AtomSetException {
		StringBuilder sb = new StringBuilder();

		sb.append("MATCH ");
		predicateToCypher(sb, p);
		sb.append("<-[rel_predicate:PREDICATE]-(atom)");
		sb.append("RETURN atom");
		String query = sb.toString();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(query);
		}
		ResourceIterator<Map<String, Object>> result = this.cypherEngine.execute(query).iterator();

		return new Neo4jAtomIterator(this.getTransaction(), result);
	}

	@Override
	public CloseableIterator<Term> termsByPredicatePosition(Predicate p, int position) throws AtomSetException {
		StringBuilder sb = new StringBuilder();

		sb.append("MATCH ");
		predicateToCypher(sb, p);
		sb.append("<-[rel_predicate:PREDICATE]-(atom)-[:TERM { index: ").append(position).append(" }]->(term) ");
		sb.append("RETURN DISTINCT term");
		String query = sb.toString();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(query);
		}
		ResourceIterator<Map<String, Object>> result = this.cypherEngine.execute(query).iterator();

		return new Neo4jTermIterator(this.getTransaction(), result);
	}

	@Override
	public void close() {

		Transaction tx = this.transactions.get(Thread.currentThread());
		tx.close();
		this.transactions.put(Thread.currentThread(), null);
		this.graph.shutdown();
	}

	private TermGenerator freshSymbolGenerator = new DefaultVariableGenerator("EE");

	@Override
	public TermGenerator getFreshSymbolGenerator() {
		return freshSymbolGenerator;
	}

	// //////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// //////////////////////////////////////////////////////////////////////////

	private Node getTerm(Term term) {
		Node node = null;
		ResourceIterator<Node> it = this.graph.findNodesByLabelAndProperty(
				NodeType.TERM, "value", term.getIdentifier()).iterator();
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
			node.setProperty("value", term.getIdentifier().toString());
			node.setProperty("type", getType(term));
		}
		return node;
	}

	private Term nodeToTerm(Node node) throws AtomSetException {
		String type = node.getProperty("type").toString();
		if("V".equals(type)) {
			return DefaultTermFactory.instance().createVariable(node.getProperty("value"));
		} else if ("L".equals(type)) {
			return DefaultTermFactory.instance().createLiteral(node.getProperty("value"));
		} else if ("C".equals(type)) {
			return DefaultTermFactory.instance().createConstant(node.getProperty("value"));
		} else {			
			throw new AtomSetException("Unrecognized type: " + type);
		}
	}

	private Node getPredicate(Predicate predicate) {
		Node node = null;
		ResourceIterator<Node> it = this.graph.findNodesByLabelAndProperty(
				NodeType.PREDICATE, "value", predicate.getIdentifier()).iterator();
		while (node == null && it.hasNext()) {
			Node tmp = it.next();
			if (tmp.getProperty("arity").equals(predicate.getArity())) {
				node = tmp;
			}
		}
		it.close();
		return node;
	}

	private Node createPredicateIfNotExist(Predicate predicate) {
		Node node = this.getPredicate(predicate);
		if (node == null) {
			node = this.graph.createNode(NodeType.PREDICATE);
			node.setProperty("value", predicate.getIdentifier());
			node.setProperty("arity", predicate.getArity());
		}
		return node;
	}

	private static Predicate nodeToPredicate(Node node) {
		return new Predicate(node.getProperty("value").toString(),
				(Integer) node.getProperty("arity"));
	}

	// //////////////////////////////////////////////////////////////////////////
	// CYPHER QUERIES GENERATION
	// //////////////////////////////////////////////////////////////////////////

	/*private static String deleteAtomIntoCypherQuery(Atom a) {
		StringBuilder sb = new StringBuilder();

		sb.append("MATCH ");
		atomToCypher(sb, a, true, true);
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
	}*/

	private static String containsAtomIntoCypherQuery(Atom a) {
		StringBuilder sb = new StringBuilder();

		sb.append("MATCH ");
		atomToCypher(sb, a, a.getVariables(), false);
		sb.append("RETURN atom");

		return sb.toString();
	}
	
	private static String matchAtomIntoCypherQuery(Atom a, Substitution s) {
		StringBuilder sb = new StringBuilder();
		Set<Variable> fixedVars = new HashSet<>();
		for(Term t : s.getValues()) {
			if(t.isVariable()) {
				fixedVars.add((Variable) t);
			}
		}
		sb.append("MATCH ");
		atomToCypher(sb, s.createImageOf(a), fixedVars, false);
		sb.append("RETURN atom");

		return sb.toString();
	}

	private static void atomToCypher(StringBuilder sb, Atom a, Set<Variable> fixedVars, boolean checkType) {
		sb.append("(atom:ATOM),");
		predicateToCypher(sb, a.getPredicate());
		sb.append(",");

		int i = -1;
		for (Term t : a) {
			// (term?:TERM {value: '?', arity: ?}),
			// (atom)-[:TERM { index: ? }]->(term?)
			++i;
			if (t.isConstant() || fixedVars.contains(t)) {
				sb.append("(term").append(i).append(":TERM {value: '").append(t.getIdentifier().toString());
				if(checkType) {
					sb.append("', type: '").append(getType(t));
				}
				sb.append("' }), (atom)-[rel_term").append(i)
				  .append(":TERM { index: ").append(i).append(" }]->(term").append(i).append("), ");
			} else {
				String id = t.getIdentifier().toString();
				sb.append("(term").append(id).append(":TERM), (atom)-[rel_term")
				  .append(i).append(":TERM { index: ").append(i).append(" }]->(term").append(id).append("), ");
			}
		}

		sb.append("(atom)-[rel_predicate:PREDICATE]->(predicate) ");
	}


	private static void predicateToCypher(StringBuilder sb, Predicate p) {
		// (predicate:PREDICATE { value: <ID>, arity: <ARITY> })
		sb.append("(predicate:PREDICATE { value: '").append(p.getIdentifier()).append("', arity: ")
		  .append(p.getArity()).append(" })");
	}

	private synchronized void reloadTransaction() {
		Transaction tx = this.transactions.get(Thread.currentThread());
		tx.close();
		this.transactions.put(Thread.currentThread(), graph.beginTx());
	}

	private synchronized void checkTransaction() {
		if (this.transactions.get(Thread.currentThread()) == null) {
			this.transactions.put(Thread.currentThread(), graph.beginTx());
		}
	}

	private synchronized Transaction getTransaction() {
		Transaction tx = this.transactions.get(Thread.currentThread());
		if (tx == null) {
			tx = graph.beginTx();
			this.transactions.put(Thread.currentThread(), tx);
		}
		return tx;
	}

	private synchronized void successTransaction() {
		this.transactions.get(Thread.currentThread()).success();
	}

	// //////////////////////////////////////////////////////////////////////////
	// PRIVATE STATIC CLASS
	// //////////////////////////////////////////////////////////////////////////

	private abstract class Neo4jElementIterator<E> extends AbstractCloseableIterator<E> {
		ResourceIterator<Map<String, Object>> iterator;
		boolean isOpen = true;

		/**
		 * @param iterator
		 */
		public Neo4jElementIterator(Transaction transaction,
				ResourceIterator<Map<String, Object>> iterator) {
			this.iterator = iterator;
		}

		@Override
		public void close() {
			if(this.isOpen) {
				this.isOpen = false;
    			this.iterator.close();
			}
		}

		@Override
		public boolean hasNext() {
			if (this.isOpen && iterator.hasNext()) {
				return true;
			} else {
				this.close();
				return false;
			}
		}

	}

	private class Neo4jAtomIterator extends Neo4jElementIterator<Atom> {

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
		public Atom next() throws IteratorException {
			Node atomNode = (Node) this.iterator.next().get("atom");
			Node predicateNode = atomNode.getSingleRelationship(
					RelationshipType.PREDICATE, Direction.OUTGOING)
					.getEndNode();
			Predicate predicate = nodeToPredicate(predicateNode);

			Term[] terms = new Term[predicate.getArity()];

			for (Relationship rel : atomNode.getRelationships(
					Direction.OUTGOING, RelationshipType.TERM)) {
				Node termNode = rel.getEndNode();

				try {
					terms[(Integer) rel.getProperty("index")] = nodeToTerm(termNode);
				} catch (AtomSetException e) {
					throw new IteratorException(e);
				}
			}

			return new DefaultAtom(predicate, terms);
		}

	}

	private class Neo4jTermIterator extends Neo4jElementIterator<Term> {

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
		public Term next() throws IteratorException {
			try {
				return nodeToTerm((Node) iterator.next().get("term"));
			} catch (AtomSetException e) {
				throw new IteratorException(e);
			}
		}

	}

	private class Neo4jPredicateIterator extends
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
	
	private static String getType(Term t) {
		if (t.isVariable()) {
			return "V";
		} else if (t.isLiteral()) {
			return "L";
		} else {
			return "C";
		}
	}
	
	@Override
	public boolean isWriteable() {
		return true;
	}

}
