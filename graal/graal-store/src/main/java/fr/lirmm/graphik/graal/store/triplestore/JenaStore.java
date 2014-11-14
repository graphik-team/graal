/**
 * 
 */
package fr.lirmm.graphik.graal.store.triplestore;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.update.GraphStore;
import com.hp.hpl.jena.update.GraphStoreFactory;
import com.hp.hpl.jena.update.UpdateExecutionFactory;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateProcessor;
import com.hp.hpl.jena.update.UpdateRequest;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.Term.Type;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
import fr.lirmm.graphik.util.stream.AbstractReader;
import fr.lirmm.graphik.util.stream.ObjectReader;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
public class JenaStore extends AbstractTripleStore {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(JenaStore.class);

	Dataset dataset;
	String directory;

	private static final String PREFIX = "PREFIX : ";

	private static final String INSERT_QUERY = PREFIX + " INSERT DATA { "
			+ " %s %s %s " + " } ";

	private static final String DELETE_QUERY = PREFIX
			+ " DELETE WHERE { %s %s %s } ";

	private static final String SELECT_QUERY = PREFIX + "SELECT ?x "
			+ " WHERE { %s %s %s } ";

	private static final String SELECT_TERMS_QUERY = PREFIX + "SELECT ?term "
			+ " WHERE { { ?term  ?p  ?o } " + " UNION { ?s ?p ?term } } ";

	private static final String SELECT_PREDICATES_QUERY = PREFIX + "SELECT ?p "
			+ " WHERE { ?s ?p ?o }";

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	public JenaStore(String directory) {
		this.directory = directory;
		this.dataset = TDBFactory.createDataset(directory);
	}

	@Override
	protected void finalize() throws Throwable {
		this.close();
	}

	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public void close() {
		if (this.dataset != null) {
			this.dataset.close();
		}
	}
	
	@Override
	public boolean addAll(Iterable<? extends Atom> atoms)
			throws AtomSetException {
		dataset.begin(ReadWrite.WRITE);
		try {
			GraphStore graphStore = GraphStoreFactory.create(dataset);
			UpdateRequest request = UpdateFactory.create();
			for (Atom atom : atoms) {
				add(request, atom);
			}
			UpdateProcessor proc = UpdateExecutionFactory.create(request,
					graphStore);
			proc.execute();
			dataset.commit();
		} finally {
			dataset.end();
		}

		return true;
	}

	@Override
	public boolean add(Atom atom) {
		dataset.begin(ReadWrite.WRITE);
		try {
			GraphStore graphStore = GraphStoreFactory.create(dataset);
			UpdateRequest request = UpdateFactory.create();
			add(request, atom);
			UpdateProcessor proc = UpdateExecutionFactory.create(request,
					graphStore);
			proc.execute();
			dataset.commit();
		} finally {
			dataset.end();
		}

		return true;
	}

	private static boolean add(UpdateRequest request, Atom atom) {
		String insert = String.format(INSERT_QUERY, atom.getTerm(0),
				atom.getPredicate(), atom.getTerm(1));
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(insert);
		}
		request.add(insert);
		return true;
	}

	@Override
	public boolean removeAll(Iterable<? extends Atom> atoms)
			throws AtomSetException {
		dataset.begin(ReadWrite.WRITE);
		try {
			GraphStore graphStore = GraphStoreFactory.create(dataset);
			UpdateRequest request = UpdateFactory.create();
			for (Atom atom : atoms) {
				remove(request, atom);
			}
			UpdateProcessor proc = UpdateExecutionFactory.create(request,
					graphStore);
			proc.execute();
			dataset.commit();
		} finally {
			dataset.end();
		}

		return true;
	}

	@Override
	public boolean remove(Atom atom) {
		dataset.begin(ReadWrite.WRITE);
		try {
			GraphStore graphStore = GraphStoreFactory.create(dataset);
			UpdateRequest request = UpdateFactory.create();
			remove(request, atom);
			UpdateProcessor proc = UpdateExecutionFactory.create(request,
					graphStore);
			proc.execute();
			dataset.commit();
		} finally {
			dataset.end();
		}

		return true;
	}

	private static boolean remove(UpdateRequest request, Atom atom) {
		String delete = String.format(DELETE_QUERY, atom.getTerm(0),
				atom.getPredicate(), atom.getTerm(1));
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(delete);
		}
		request.add(delete);
		return true;
	}

	@Override
	public ObjectReader<Atom> iterator() {
		return new AtomIterator(this.directory);
	}

	@Override
	public boolean contains(Atom atom) throws AtomSetException {
		boolean contains = false;
		String select = String.format(SELECT_QUERY, atom.getTerm(0),
				atom.getPredicate(), atom.getTerm(1));

		dataset.begin(ReadWrite.READ);
		QueryExecution qExec = null;
		try {
			qExec = QueryExecutionFactory.create(select, dataset);
			ResultSet rs = qExec.execSelect();
			if (rs.hasNext()) {
				contains = true;
			}
		} finally {
			if (qExec != null) {
				qExec.close();
			}
			dataset.end();
		}

		return contains;
	}

	@Override
	public Set<Term> getTerms() {
		Set<Term> terms = new TreeSet<Term>();
		dataset.begin(ReadWrite.READ);
		QueryExecution qExec = null;
		try {
			qExec = QueryExecutionFactory.create(SELECT_TERMS_QUERY, dataset);
			ResultSet rs = qExec.execSelect();
			while (rs.hasNext()) {
				terms.add(createTerm(rs.next().get("?term")));
			}
		} finally {
			if (qExec != null) {
				qExec.close();
			}
			dataset.end();
		}

		return terms;
	}

	@Override
	public Set<Term> getTerms(Type type) {
		Set<Term> terms = this.getTerms();
		Iterator<Term> it = terms.iterator();
		while (it.hasNext()) {
			Term t = it.next();
			if (!t.getType().equals(type)) {
				it.remove();
			}
		}
		return terms;
	}

	@Override
	public Iterable<Predicate> getAllPredicates() {
		Set<Predicate> predicates = new TreeSet<Predicate>();
		dataset.begin(ReadWrite.READ);
		QueryExecution qExec = null;
		try {
			qExec = QueryExecutionFactory.create(SELECT_TERMS_QUERY, dataset);
			ResultSet rs = qExec.execSelect();
			while (rs.hasNext()) {
				predicates.add(new Predicate(rs.next().toString(), 2));
			}
		} finally {
			if (qExec != null) {
				qExec.close();
			}
			dataset.end();
		}

		return predicates;
	}

	@Override
	public boolean isSubSetOf(AtomSet atomset) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public boolean isEmpty() {
		return !this.iterator().hasNext();
	}

	@Override
	public void clear() {
		dataset.begin(ReadWrite.WRITE);
		try {
			GraphStore graphStore = GraphStoreFactory.create(dataset);
			UpdateRequest request = UpdateFactory.create("CLEAR DEFAULT");
			UpdateProcessor proc = UpdateExecutionFactory.create(request,
					graphStore);
			proc.execute();
			dataset.commit();
		} finally {
			dataset.end();
		}
	}

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE CLASS
	// /////////////////////////////////////////////////////////////////////////

	private static class AtomIterator extends AbstractReader<Atom> {

		Dataset dataset;
		static String SELECT = "PREFIX graal: <http://team.inria.fr/graphik/graal/> "
				+ "SELECT ?s ?p ?o WHERE { ?s ?p ?o } ";
		ResultSet rs;
		QueryExecution qExec;

		// /////////////////////////////////////////////////////////////////////////
		// CONSTRUCTOR
		// /////////////////////////////////////////////////////////////////////////

		/**
		 * @param dataset
		 */
		public AtomIterator(String directory) {
			this.dataset = TDBFactory.createDataset(directory);
			this.dataset.begin(ReadWrite.READ);
			this.qExec = QueryExecutionFactory.create(SELECT, this.dataset);
			this.rs = qExec.execSelect();
		}

		@Override
		protected void finalize() {
			if (this.qExec != null) {
				this.qExec.close();
			}
			this.dataset.end();
		}

		// /////////////////////////////////////////////////////////////////////////
		// METHODS
		// /////////////////////////////////////////////////////////////////////////

		@Override
		public void remove() {
			this.rs.remove();
		}

		@Override
		public boolean hasNext() {
			if (this.rs.hasNext()) {
				return true;
			} else {
				this.finalize();
				return false;
			}
		}

		@Override
		public Atom next() {
			QuerySolution next = this.rs.next();

			Predicate predicate = new Predicate(next.get("?p").toString(), 2);
			Term subject = new Term(next.get("?s").toString(),
					Term.Type.CONSTANT);

			RDFNode o = next.get("?o");
			Term object = createTerm(o);

			Atom atom = new DefaultAtom(predicate, subject, object);
			return atom;
		}

		@Override
		public Iterator<Atom> iterator() {
			return this;
		}

	}

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE STATIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	private static Term createTerm(RDFNode node) {
		Term term = null;
		if (node.isLiteral()) {
			term = new Term(node.toString(), Term.Type.LITERAL);
		} else {
			term = new Term(node.toString(), Term.Type.CONSTANT);
		}
		return term;
	}

}
