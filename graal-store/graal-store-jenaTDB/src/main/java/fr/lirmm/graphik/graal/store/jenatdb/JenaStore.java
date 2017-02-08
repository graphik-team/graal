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
package fr.lirmm.graphik.graal.store.jenatdb;

import java.io.File;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.update.GraphStore;
import com.hp.hpl.jena.update.GraphStoreFactory;
import com.hp.hpl.jena.update.UpdateExecutionFactory;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateProcessor;
import com.hp.hpl.jena.update.UpdateRequest;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.ConstantGenerator;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Term.Type;
import fr.lirmm.graphik.graal.api.store.WrongArityException;
import fr.lirmm.graphik.graal.core.DefaultConstantGenerator;
import fr.lirmm.graphik.graal.core.store.AbstractTripleStore;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.CloseableIteratorAdapter;
import fr.lirmm.graphik.util.stream.IteratorException;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
public class JenaStore extends AbstractTripleStore {

	private static final Logger LOGGER = LoggerFactory.getLogger(JenaStore.class);

	Dataset                     dataset;
	String                      directory;

	private static final String INSERT_QUERY = PREFIX + " INSERT DATA { " + " %s %s %s " + " } ";

	private static final String DELETE_QUERY = PREFIX + " DELETE WHERE { %s %s %s } ";
	
	private static final String SELECT_FILTERED_QUERY = PREFIX + "SELECT ?s ?p ?o WHERE { %s %s %s }";

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
	public boolean addAll(CloseableIterator<? extends Atom> atoms) throws AtomSetException {
		dataset.begin(ReadWrite.WRITE);
		try {
			GraphStore graphStore = GraphStoreFactory.create(dataset);
			UpdateRequest request = UpdateFactory.create();
			try {
				while (atoms.hasNext()) {
					add(request, atoms.next());
				}
			} catch (IteratorException e) {
				throw new AtomSetException("An errors occurs while iterating atoms to add", e);
			}
			UpdateProcessor proc = UpdateExecutionFactory.create(request, graphStore);
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
			UpdateProcessor proc = UpdateExecutionFactory.create(request, graphStore);
			proc.execute();
			dataset.commit();
		} finally {
			dataset.end();
		}

		return true;
	}

	private static boolean add(UpdateRequest request, Atom atom) {
		String insert = String.format(INSERT_QUERY, Utils.termToString(atom.getTerm(0)),
		    Utils.predicateToString(atom.getPredicate()), Utils.termToString(atom.getTerm(1)));
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(insert);
		}
		request.add(insert);
		return true;
	}

	@Override
	public boolean removeAll(CloseableIterator<? extends Atom> atoms) throws AtomSetException {
		dataset.begin(ReadWrite.WRITE);
		try {
			GraphStore graphStore = GraphStoreFactory.create(dataset);
			UpdateRequest request = UpdateFactory.create();
			try {
				while (atoms.hasNext()) {
					remove(request, atoms.next());
				}
			} catch (IteratorException e) {
				throw new AtomSetException("An errors occurs while iterating atoms to remove", e);

			}
			UpdateProcessor proc = UpdateExecutionFactory.create(request, graphStore);
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
			UpdateProcessor proc = UpdateExecutionFactory.create(request, graphStore);
			proc.execute();
			dataset.commit();
		} finally {
			dataset.end();
		}

		return true;
	}

	private static boolean remove(UpdateRequest request, Atom atom) {
		String delete = String.format(DELETE_QUERY, atom.getTerm(0), atom.getPredicate(), atom.getTerm(1));
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(delete);
		}
		request.add(delete);
		return true;
	}

	@Override
	public CloseableIterator<Atom> iterator() {
		return new AtomIterator(this.directory, SELECT_ALL);
	}

	@Override
	public boolean contains(Atom atom) throws AtomSetException {
		boolean contains = false;
		String select = String.format(SELECT_QUERY, Utils.termToString(atom.getTerm(0)),
		    Utils.predicateToString(atom.getPredicate()), Utils.termToString(atom.getTerm(1)));

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
	public CloseableIterator<Atom> match(Atom atom) throws AtomSetException {
		Term subject = atom.getTerm(0);
		Term object = atom.getTerm(1);
		String select = String.format(SELECT_QUERY, Utils.termToString(subject, "?" + subject.getLabel()),
		    Utils.predicateToString(atom.getPredicate()), Utils.termToString(object, "?" + object.getLabel()));

		return new AtomIterator(this.directory, select, subject, atom.getPredicate(), object);
	}

	@Override
	public CloseableIterator<Atom> atomsByPredicate(Predicate p) throws AtomSetException {
		String select = String.format(SELECT_QUERY, "?s", Utils.predicateToString(p), "?o");

		return new AtomIterator(this.directory, select, null, p, null);

	}

	@Override
	public CloseableIterator<Term> termsByPredicatePosition(Predicate p, int position) throws AtomSetException {
		if(position == 0) {
			return new TermIterator(this.directory, "SELECT DISTINCT ?x WHERE { ?x " + Utils.predicateToString(p) + " ?y }");
		} else if (position == 1) {
			return new TermIterator(this.directory, "SELECT DISTINCT ?x WHERE { ?y " + Utils.predicateToString(p) + " ?x }");
		} else {
			throw new WrongArityException("Position should be 0 for subject or 1 for object.");
		}
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
				terms.add(Utils.createTerm(rs.next().get("?term")));
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
	public CloseableIterator<Term> termsIterator() {
		// TODO use a ResultSetIterator
		return new CloseableIteratorAdapter<Term>(this.getTerms().iterator());
	}

	@Override
	@Deprecated
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
	@Deprecated
	public CloseableIterator<Term> termsIterator(Term.Type type) {
		// TODO use a ResultSetIterator
		return new CloseableIteratorAdapter<Term>(this.getTerms(type).iterator());
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
	public CloseableIterator<Predicate> predicatesIterator() {
		return new CloseableIteratorAdapter<Predicate>(this.getPredicates().iterator());
	}

	@Override
	public void clear() {
		dataset.begin(ReadWrite.WRITE);
		try {
			GraphStore graphStore = GraphStoreFactory.create(dataset);
			UpdateRequest request = UpdateFactory.create("CLEAR DEFAULT");
			UpdateProcessor proc = UpdateExecutionFactory.create(request, graphStore);
			proc.execute();
			dataset.commit();
		} finally {
			dataset.end();
		}
	}

	private ConstantGenerator freshSymbolGenerator = new DefaultConstantGenerator("EE");

	@Override
	public ConstantGenerator getFreshSymbolGenerator() {
		return freshSymbolGenerator;
	}

}
