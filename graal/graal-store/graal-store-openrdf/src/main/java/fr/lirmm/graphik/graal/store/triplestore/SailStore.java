/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2016)
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

import info.aduna.iteration.Iteration;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.Literal;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Term.Type;
import fr.lirmm.graphik.graal.api.store.AbstractTripleStore;
import fr.lirmm.graphik.graal.api.store.WrongArityException;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.util.Prefix;
import fr.lirmm.graphik.util.URIUtils;
import fr.lirmm.graphik.util.stream.AbstractCloseableIterator;
import fr.lirmm.graphik.util.stream.CloseableIterator;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class SailStore extends AbstractTripleStore {

	private static final Logger LOGGER = LoggerFactory.getLogger(SailStore.class);

	private static class URIzer {
		private static URIzer instance;

		protected URIzer() {
			super();
		}

		public static synchronized URIzer instance() {
			if (instance == null)
				instance = new URIzer();

			return instance;
		}

		Prefix defaultPrefix = new Prefix("sail", "file:///sail/");

		/**
		 * Add default prefix if necessary
		 * 
		 * @param s
		 * @return
		 */
		String input(String s) {
			return URIUtils.createURI(s, defaultPrefix).toString();
		}

		/**
		 * Remove default prefix if it is present
		 * 
		 * @param s
		 * @return
		 */
		String output(String s) {
			if (s.startsWith(defaultPrefix.getPrefix())) {
				return s.substring(defaultPrefix.getPrefix().length());
			}
			return s;
		}
	}

	private RepositoryConnection connection;
	private ValueFactory         valueFactory;

	private TupleQuery           predicatesQuery;
	private TupleQuery           termsQuery;

	public SailStore() throws AtomSetException {
		Repository repo = new SailRepository(new MemoryStore());
		try {
			repo.initialize();
			this.connection = repo.getConnection();
		} catch (RepositoryException e) {
			throw new AtomSetException("Error while creating SailStore", e);
		}

		this.valueFactory = repo.getValueFactory();

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
		try {
			this.connection.close();
		} catch (RepositoryException e) {
			if (LOGGER.isErrorEnabled()) {
				LOGGER.error("Error while trying to close sail repository connection.", e);
			}
		}
	}

	@Override
	public boolean add(Atom atom) throws AtomSetException {
		try {
			this.connection.add(this.atomToStatement(atom));
		} catch (RepositoryException e) {
			throw new AtomSetException("Error while adding the atom " + atom, e);
		}
		return true;
	}

	@Override
	public boolean addAll(Iterator<? extends Atom> atom) throws AtomSetException {
		try {
			this.connection.add(new StatementIterator(atom));
		} catch (RepositoryException e) {
			throw new AtomSetException("Error while adding the atom " + atom, e);
		}
		return true;
	}

	/**
	 * 
	 * @param atom
	 * @return always return true.
	 * @throws AtomSetException
	 */
	@Override
	public boolean remove(Atom atom) throws AtomSetException {
		try {
			this.connection.remove(this.atomToStatement(atom));
		} catch (RepositoryException e) {
			throw new AtomSetException("Error while adding the atoms.", e);
		}
		return true;
	}

	@Override
	public boolean removeAll(Iterator<? extends Atom> atom) throws AtomSetException {
		try {
			this.connection.remove(new StatementIterator(atom));
		} catch (RepositoryException e) {
			throw new AtomSetException("Error while removing the atoms.", e);
		}
		return true;
	}

	@Override
	public boolean contains(Atom atom) throws AtomSetException {
		Statement stat = this.atomToStatement(atom);
		try {
			return this.connection.hasStatement(stat, false);
		} catch (RepositoryException e) {
			throw new AtomSetException(e);
		}
	}

	@Override
	public CloseableIterator<Atom> match(Atom atom) throws AtomSetException {
		Statement stat = this.atomToStatement(atom);
		URI subject = (URI) stat.getSubject();
		if (subject.getNamespace().equals("_:")) {
			subject = null;
		}
		Value object = stat.getObject();
		if (object instanceof URI && ((URI) object).getNamespace().equals("_:")) {
			object = null;
		}

		try {
			return new AtomIterator(this.connection.getStatements(subject, stat.getPredicate(), object, false));
		} catch (RepositoryException e) {
			throw new AtomSetException(e);
		}
	}

	@Override
	public CloseableIterator<Atom> atomsByPredicate(Predicate p) throws AtomSetException {
		try {
			return new AtomIterator(this.connection.getStatements(null, this.createURI(p), null, false));
		} catch (RepositoryException e) {
			throw new AtomSetException(e);
		}
	}

	@Override
	public CloseableIterator<Term> termsByPredicatePosition(Predicate p, int position) throws AtomSetException {
		TupleQuery query = null;
		TupleQueryResult results = null;
		try {
			if (position == 0) {
				query = this.connection.prepareTupleQuery(QueryLanguage.SPARQL,
 "SELECT DISTINCT ?x WHERE { ?x <"
				                                                                + this.createURI(p)
				                                                                + "> ?y }");
			} else if (position == 1) {
				query = this.connection.prepareTupleQuery(QueryLanguage.SPARQL,
 "SELECT DISTINCT ?x WHERE { ?y <"
				                                                                + this.createURI(p)
				                                                                + "> ?x }");
			} else {
				throw new WrongArityException("Position should be 0 for subject or 1 for object.");
			}
			results = query.evaluate();
		} catch (RepositoryException e) {
			throw new AtomSetException(e);
		} catch (MalformedQueryException e) {
			throw new AtomSetException(e);
		} catch (QueryEvaluationException e) {
			throw new AtomSetException(e);
		}

		return new TermsIterator(results, "x");
	}

	@Override
	public CloseableIterator<Predicate> predicatesIterator() throws AtomSetException {
		TupleQueryResult result;
		try {
			result = this.getPredicatesQuery().evaluate();
			return new PredicatesIterator(result);
		} catch (QueryEvaluationException e) {
			throw new AtomSetException(e);
		}
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

	private TupleQuery getPredicatesQuery() throws AtomSetException {
		if (this.predicatesQuery == null) {
			try {
				this.predicatesQuery = this.connection.prepareTupleQuery(QueryLanguage.SPARQL, SELECT_PREDICATES_QUERY);
			} catch (RepositoryException e) {
				throw new AtomSetException(e);
			} catch (MalformedQueryException e) {
				if (LOGGER.isErrorEnabled()) {
					LOGGER.error("Error on SPARQL query syntax", e);
				}
			}
		}
		return this.predicatesQuery;
	}

	@Override
	public CloseableIterator<Term> termsIterator() throws AtomSetException {
		TupleQueryResult result;
		try {
			result = this.getTermsQuery().evaluate();
			return new TermsIterator(result);
		} catch (QueryEvaluationException e) {
			throw new AtomSetException(e);
		}
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
	public CloseableIterator<Term> termsIterator(Type type) throws AtomSetException {
		// TODO implements other type
		return this.termsIterator();
	}

	@Override
	public Set<Term> getTerms(Type type) throws AtomSetException {
		// TODO implements other type
		return this.getTerms();
	}

	private TupleQuery getTermsQuery() throws AtomSetException {
		if (this.termsQuery == null) {
			try {
				this.termsQuery = this.connection.prepareTupleQuery(QueryLanguage.SPARQL, SELECT_TERMS_QUERY);
			} catch (RepositoryException e) {
				throw new AtomSetException(e);
			} catch (MalformedQueryException e) {
				if (LOGGER.isErrorEnabled()) {
					LOGGER.error("Error on SPARQL query syntax", e);
				}
			}
		}
		return this.termsQuery;
	}

	@Override
	public void clear() throws AtomSetException {
		try {
			this.connection.clear();
		} catch (RepositoryException e) {
			throw new AtomSetException("Error during cleaning this atomSet", e);
		}
	}

	@Override
	public CloseableIterator<Atom> iterator() {
		try {
			return new AtomIterator(this.connection.getStatements(null, null, null, false));
		} catch (RepositoryException e) {
			if (LOGGER.isErrorEnabled()) {
				LOGGER.error("Error during iterator creation", e);
			}
		}
		return null;
	}

	// //////////////////////////////////////////////////////////////////////////
	// PRIVATE
	// //////////////////////////////////////////////////////////////////////////

	private Statement atomToStatement(Atom atom) throws WrongArityException {
		if (atom.getPredicate().getArity() != 2) {
			throw new WrongArityException("Arity "
			                              + atom.getPredicate().getArity()
			                              + " is not supported by this store.");
		}
		URI predicate = this.createURI(atom.getPredicate());
		URI term0 = this.createURI(atom.getTerm(0));
		Value term1 = this.createValue(atom.getTerm(1));
		return valueFactory.createStatement(term0, predicate, term1);
	}

	private URI createURI(Predicate p) {
		return createURI(URIzer.instance().input(p.getIdentifier().toString()));
	}

	private URI createURI(Term t) {
		if (t.isConstant()) {
			return createURI(URIzer.instance().input(t.getIdentifier().toString()));
		} else {
			return createURI("_:" + t.getIdentifier().toString());
		}
	}

	/**
	 * Create URI from string. If the specified string is not a valid URI, the
	 * method add a default prefix to the string.
	 */
	private URI createURI(String string) {
		return valueFactory.createURI(string);
	}

	private Value createValue(Term t) {
		if (t instanceof Literal) {
			Literal l = (Literal) t;
			return valueFactory.createLiteral(l.getValue().toString(),
			    valueFactory.createURI(l.getDatatype().toString()));
		} else {
			return createURI(t);
		}
	}

	private static Atom statementToAtom(Statement stat) {
		Predicate predicate = valueToPredicate(stat.getPredicate());
		Term term0 = valueToTerm(stat.getSubject());
		Term term1 = valueToTerm(stat.getObject());
		return new DefaultAtom(predicate, term0, term1);
	}

	private static Predicate valueToPredicate(Value value) {
		return new Predicate(URIzer.instance().output(value.toString()), 2);
	}

	private static Term valueToTerm(Value value) {
		if (value instanceof Resource) {
			return DefaultTermFactory.instance().createConstant(URIzer.instance().output(value.toString()));
		} else { //  Literal
			org.openrdf.model.Literal l = (org.openrdf.model.Literal) value;
			return DefaultTermFactory.instance().createLiteral(URIUtils.createURI(l.getDatatype().toString()),
			    l.getLabel());
		}
	}

	// //////////////////////////////////////////////////////////////////////////
	// PRIVATE CLASSES
	// //////////////////////////////////////////////////////////////////////////

	private class StatementIterator implements Iteration<Statement, RepositoryException> {

		private Iterator<? extends Atom> it;

		public StatementIterator(Iterator<? extends Atom> iterator) {
			this.it = iterator;
		}

		@Override
		public boolean hasNext() {
			return it.hasNext();
		}

		@Override
		public Statement next() {
			try {
				return atomToStatement(it.next());
			} catch (WrongArityException e) {
				return null;
			}
		}

		@Override
		public void remove() {
			it.remove();
		}

	}

	private class AtomIterator extends AbstractCloseableIterator<Atom> {
		RepositoryResult<Statement> it;

		AtomIterator(RepositoryResult<Statement> it) {
			this.it = it;
		}

		@Override
		public void close() {
			try {
				this.it.close();
			} catch (RepositoryException e) {
				LOGGER.error("Error when closing SailStore iterator.");
			}
		}

		@Override
		public boolean hasNext() {
			try {
				if (it.hasNext()) {
					return true;
				} else {
					this.it.close();
					return false;
				}
			} catch (RepositoryException e) {
				// TODO manage this Exception
				LOGGER.error("Error on SailStore iterator.");
				return false;
			}
		}

		@Override
		public Atom next() {
			try {
				return statementToAtom(this.it.next());
			} catch (RepositoryException e) {
				// TODO manage this Exception
				LOGGER.error("Error on SailStore iterator.");
				return null;
			}
		}

		@Override
		public void remove() {
			try {
				this.it.remove();
			} catch (RepositoryException e) {
				// TODO manage this Exception
				LOGGER.error("Error on SailStore iterator.");
			}
		}
	}

	private abstract class TupleQueryResultIterator<E> extends AbstractCloseableIterator<E> {

		protected TupleQueryResult it;

		@Override
		public void close() {
			try {
				this.it.close();
			} catch (QueryEvaluationException e) {
				if (LOGGER.isErrorEnabled()) {
					LOGGER.error("Error during iteration closing", e);
				}
			}
		}

		@Override
		public boolean hasNext() {
			try {
				return this.it.hasNext();
			} catch (QueryEvaluationException e) {
				if (LOGGER.isErrorEnabled()) {
					// TODO manage this Exception
					LOGGER.error("Error during iteration", e);
				}
			}
			return false;
		}

		@Override
		public void remove() {
			try {
				this.it.remove();
			} catch (QueryEvaluationException e) {
				if (LOGGER.isErrorEnabled()) {
					// TODO manage this Exception
					LOGGER.error("Error during iteration", e);
				}
			}
		}

	}

	private class PredicatesIterator extends TupleQueryResultIterator<Predicate> {

		PredicatesIterator(TupleQueryResult results) {
			super.it = results;
		}

		@Override
		public Predicate next() {
			try {
				return valueToPredicate(this.it.next().getValue("p"));
			} catch (QueryEvaluationException e) {
				if (LOGGER.isErrorEnabled()) {
					// TODO manage this Exception
					LOGGER.error("Error during iteration", e);
				}
			}
			return null;
		}

	}

	private class TermsIterator extends TupleQueryResultIterator<Term> {

		String value = "term";

		TermsIterator(TupleQueryResult results) {
			super.it = results;
		}

		TermsIterator(TupleQueryResult results, String value) {
			super.it = results;
			this.value = value;
		}

		@Override
		public Term next() {
			try {
				return valueToTerm(this.it.next().getValue(value));
			} catch (QueryEvaluationException e) {
				if (LOGGER.isErrorEnabled()) {
					// TODO manage this Exception
					LOGGER.error("Error during iteration", e);
				}
			}
			return null;
		}

	}

}
