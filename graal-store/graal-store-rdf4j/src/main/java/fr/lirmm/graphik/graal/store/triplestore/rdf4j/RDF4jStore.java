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
package fr.lirmm.graphik.graal.store.triplestore.rdf4j;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Term.Type;
import fr.lirmm.graphik.graal.api.core.TermGenerator;
import fr.lirmm.graphik.graal.api.store.WrongArityException;
import fr.lirmm.graphik.graal.common.rdf4j.MalformedLangStringException;
import fr.lirmm.graphik.graal.common.rdf4j.RDF4jUtils;
import fr.lirmm.graphik.graal.core.DefaultConstantGenerator;
import fr.lirmm.graphik.graal.core.factory.DefaultConjunctiveQueryFactory;
import fr.lirmm.graphik.graal.core.store.AbstractTripleStore;
import fr.lirmm.graphik.graal.io.sparql.SparqlConjunctiveQueryWriter;
import fr.lirmm.graphik.util.Prefix;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.IteratorException;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
@SuppressWarnings("deprecation")
public class RDF4jStore extends AbstractTripleStore {

	static final Logger LOGGER = LoggerFactory.getLogger(RDF4jStore.class);

	private RepositoryConnection connection;

	private TupleQuery           predicatesQuery;
	private TupleQuery           termsQuery;
	
	private RDF4jUtils utils;
	
	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////
	
	/**
	 * Construct a RDF4jStore around a <a href="http://docs.rdf4j.org/javadoc/latest/org/eclipse/rdf4j/repository/Repository.html">RDF4J Repository</a>
	 * @param repo
	 * @throws AtomSetException
	 */
	public RDF4jStore(Repository repo) throws AtomSetException {
		try {
			repo.initialize();
			this.connection = repo.getConnection();
		} catch (RepositoryException e) {
			throw new AtomSetException("Error while creating SailStore", e);
		}		
		this.utils = new RDF4jUtils(new Prefix("rdf4j", "file://rdf4j/"),repo.getValueFactory());
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
	public boolean isWriteable() {
		return this.connection.getRepository().isWritable();
	}

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
			this.connection.add(utils.atomToStatement(atom));
		} catch (RepositoryException e) {
			throw new AtomSetException("Error while adding the atom " + atom, e);
		} catch (MalformedLangStringException e) {
			throw new AtomSetException("Error on the atom " + atom, e);
		}
		return true;
	}

	@Override
	public boolean addAll(CloseableIterator<? extends Atom> atom) throws AtomSetException {
		try {
			this.connection.add(new StatementIterator(utils, atom));
		} catch (RepositoryException e) {
			throw new AtomSetException("Error while adding the atom " + atom, e);
		} catch (RuntimeException e) {
			if(e.getCause() instanceof WrongArityException) {
				// StatementIterator implements an interface which does not allow to throw an implementation of Exception
				// so the Exception is throw through a RuntimeException
				throw (WrongArityException) e.getCause();
			}
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
			this.connection.remove(utils.atomToStatement(atom));
		} catch (RepositoryException e) {
			throw new AtomSetException("Error while removing the atoms.", e);
		} catch (MalformedLangStringException e) {
			throw new AtomSetException("Error on the atom " + atom, e);
		}
		return true;
	}

	@Override
	public boolean removeAll(CloseableIterator<? extends Atom> atom) throws AtomSetException {
		try {
			this.connection.remove(new StatementIterator(utils, atom));
		} catch (RepositoryException e) {
			throw new AtomSetException("Error while removing the atoms.", e);
		} catch (RuntimeException e) {
			if(e.getCause() instanceof WrongArityException) {
				// StatementIterator implements an interface which does not allow to throw an implementation of Exception
				// so the Exception is throw through a RuntimeException
				throw (WrongArityException) e.getCause();
			}
		}
		return true;
	}

	@Override
	public boolean contains(Atom atom) throws AtomSetException {
		Statement stat;
		try {
			stat = utils.atomToStatement(atom);
		} catch (MalformedLangStringException e) {
			throw new AtomSetException("Error on the atom " + atom, e);
		}
		try {
			return this.connection.hasStatement(stat, false);
		} catch (RepositoryException e) {
			throw new AtomSetException(e);
		}
	}

	@Override
	public CloseableIterator<Atom> match(Atom atom, Substitution initialSub) throws AtomSetException {
		try {
			// only to check validity
			utils.atomToStatement(atom);
		} catch (MalformedLangStringException e) {
			throw new AtomSetException("Error on the atom " + atom, e);
		}
		
		// check does not contains fixed variables
		for(Term t : initialSub.getValues()) {
			if(t.isVariable()) {
				throw new AtomSetException("We can't query specified blank node on TripleStore :" + t);
			}
		}
		
		ConjunctiveQuery query = DefaultConjunctiveQueryFactory.instance().create(atom);
		StringWriter s = new StringWriter();
		SparqlConjunctiveQueryWriter w = new SparqlConjunctiveQueryWriter(s, this.utils.getURIzer());
		try {
			w.write(query);
			w.close();
		} catch (IOException e1) {
			throw new AtomSetException("Error while converting to SPARQL " + atom, e1);
		}
		TupleQuery sparqlQuery = this.connection.prepareTupleQuery(s.toString());
		TupleQueryResult result = sparqlQuery.evaluate();
		return new TupleQueryResultAtomIterator(result, atom, utils);
	}

	@Override
	public CloseableIterator<Atom> atomsByPredicate(Predicate p) throws AtomSetException {
		if(p.getArity() != 2) {
			throw new WrongArityException("Error on " + p + ": arity " + p.getArity()
				+ " is not supported by this store. ");
		}
		try {
			return new AtomIterator(this.connection.getStatements(null, utils.createURI(p), null, false), utils);
		} catch (RepositoryException e) {
			throw new AtomSetException(e);
		}
	}

	@Override
	public CloseableIterator<Term> termsByPredicatePosition(Predicate p, int position) throws AtomSetException {
		if(p.getArity() != 2) {
			throw new WrongArityException("Error on " + p + ": arity " + p.getArity()
				+ " is not supported by this store. ");
		}
		TupleQuery query = null;
		TupleQueryResult results = null;
		try {
			if (position == 0) {
				query = this.connection.prepareTupleQuery(QueryLanguage.SPARQL,
 "SELECT DISTINCT ?x WHERE { ?x <"
				                                                                + utils.createURI(p)
				                                                                + "> ?y }");
			} else if (position == 1) {
				query = this.connection.prepareTupleQuery(QueryLanguage.SPARQL,
 "SELECT DISTINCT ?x WHERE { ?y <"
				                                                                + utils.createURI(p)
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

		return new TermsIterator(results, "x", this.utils);
	}

	@Override
	public CloseableIterator<Predicate> predicatesIterator() throws AtomSetException {
		TupleQueryResult result;
		try {
			result = this.getPredicatesQuery().evaluate();
			return new PredicatesIterator(result, utils);
		} catch (QueryEvaluationException e) {
			throw new AtomSetException(e);
		}
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
			throw new AtomSetException("An error occurs during iteration over predicates", e);
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
			return new TermsIterator(result, utils);
		} catch (QueryEvaluationException e) {
			throw new AtomSetException(e);
		}
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
			throw new AtomSetException("An error occurs during iteration over terms", e);
		}
		return set;
	}

	@Override
	@Deprecated
	public CloseableIterator<Term> termsIterator(Type type) throws AtomSetException {
		// TODO implements other type
		return this.termsIterator();
	}

	@Override
	@Deprecated
	public Set<Term> getTerms(Type type) throws AtomSetException {
		// TODO implements other type
		return this.getTerms();
	}

	private TermGenerator freshSymbolGenerator = new DefaultConstantGenerator("EE");

	@Override
	public TermGenerator getFreshSymbolGenerator() {
		return freshSymbolGenerator;
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
			return new AtomIterator(this.connection.getStatements(null, null, null, false), utils);
		} catch (RepositoryException e) {
			if (LOGGER.isErrorEnabled()) {
				LOGGER.error("Error during iterator creation", e);
			}
		}
		return null;
	}


}
