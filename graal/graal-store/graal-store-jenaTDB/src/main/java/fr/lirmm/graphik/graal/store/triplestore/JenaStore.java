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
package fr.lirmm.graphik.graal.store.triplestore;

import java.io.File;
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
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
import fr.lirmm.graphik.graal.core.impl.DefaultAtom;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.core.term.Term;
import fr.lirmm.graphik.graal.core.term.Term.Type;
import fr.lirmm.graphik.graal.store.AbstractTripleStore;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
public class JenaStore extends AbstractTripleStore {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(JenaStore.class);

	Dataset dataset;
	String directory;

	private static final String INSERT_QUERY = PREFIX + " INSERT DATA { "
			+ " %s %s %s " + " } ";

	private static final String DELETE_QUERY = PREFIX
			+ " DELETE WHERE { %s %s %s } ";

	private static final String SELECT_QUERY = PREFIX + "SELECT ?x "
			+ " WHERE { %s %s %s } ";

	

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	public JenaStore(String directory) {
		this.directory = directory;
		this.dataset = TDBFactory.createDataset(directory);
	}
	
	public JenaStore(File jenaDirectory) {
		this(jenaDirectory.getAbsolutePath());
	}

	@Override
	protected void finalize() throws Throwable {
		this.close();
		super.finalize();
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
		String insert = String.format(INSERT_QUERY, termToString(atom.getTerm(0)),
				predicateToString(atom.getPredicate()), termToString(atom.getTerm(1)));
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
	public Iterator<Atom> iterator() {
		return new AtomIterator(this.directory);
	}

	@Override
	public boolean contains(Atom atom) throws AtomSetException {
		boolean contains = false;
		String select = String.format(SELECT_QUERY, termToString(atom.getTerm(0)),
				predicateToString(atom.getPredicate()), termToString(atom.getTerm(1)));

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
	public Set<Predicate> getPredicates() {
		Set<Predicate> predicates = new TreeSet<Predicate>();
		dataset.begin(ReadWrite.READ);
		QueryExecution qExec = null;
		try {
			qExec = QueryExecutionFactory.create(SELECT_PREDICATES_QUERY, dataset);
			ResultSet rs = qExec.execSelect();
			while (rs.hasNext()) {
				predicates.add(new Predicate(rs.next().get("?p").toString(), 2));
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

	private static class AtomIterator implements Iterator<Atom> {

		Dataset dataset;
		static final String SELECT = "PREFIX graal: <http://team.inria.fr/graphik/graal/> "
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
		protected void finalize() throws Throwable {
			this.close();
			super.finalize();
		}

		// /////////////////////////////////////////////////////////////////////////
		// METHODS
		// /////////////////////////////////////////////////////////////////////////

		public void close() {
			if (this.qExec != null) {
				this.qExec.close();
			}
			this.dataset.end();
		}
		
		@Override
		public void remove() {
			this.rs.remove();
		}

		@Override
		public boolean hasNext() {
			if (this.rs.hasNext()) {
				return true;
			} else {
				this.close();
				return false;
			}
		}

		@Override
		public Atom next() {
			QuerySolution next = this.rs.next();

			Predicate predicate = new Predicate(next.get("?p").toString(), 2);
			Term subject = DefaultTermFactory.instance().createConstant(
					next.get("?s")
					.toString());

			RDFNode o = next.get("?o");
			Term object = createTerm(o);

			Atom atom = new DefaultAtom(predicate, subject, object);
			return atom;
		}

	}

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE STATIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	private static String predicateToString(Predicate p) {
		return "<" + p.getIdentifier() + ">";
	}
	
	private static String termToString(Term t) {
		if(Term.Type.CONSTANT.equals(t.getType())) {
			return "<" + t.getIdentifier().toString() + ">";
		} else if (Term.Type.LITERAL.equals(t.getType())) {
			return t.getIdentifier().toString();
		} else if (Term.Type.VARIABLE.equals(t.getType())) {
			return "?" + t.getIdentifier().toString();
		} else {
			return "";
		}
	}
	
	private static Term createTerm(RDFNode node) {
		Term term = null;
		if (node.isLiteral()) {
			term = DefaultTermFactory.instance().createLiteral(
					node.asLiteral().getValue());
		} else {
			term = DefaultTermFactory.instance()
					.createConstant(node.toString());
		}
		return term;
	}

}
