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
package fr.lirmm.graphik.graal.store.rdbms;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Term.Type;
import fr.lirmm.graphik.graal.api.core.VariableGenerator;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.factory.ConjunctiveQueryFactory;
import fr.lirmm.graphik.graal.core.factory.DefaultAtomFactory;
import fr.lirmm.graphik.graal.core.stream.SubstitutionIterator2AtomIterator;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.store.rdbms.driver.RdbmsDriver;
import fr.lirmm.graphik.util.MethodNotImplementedError;
import fr.lirmm.graphik.util.stream.CloseableIterator;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class PlainTableRDBMSStore extends AbstractRdbmsStore {

	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(PlainTableRDBMSStore.class);
	
	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////
	
	/**
	 * 
	 * @param driver
	 * @throws SQLException
	 * @throws AtomSetException
	 */
	public PlainTableRDBMSStore(RdbmsDriver driver) throws AtomSetException {
		super(driver);
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	@Override
	public String transformToSQL(ConjunctiveQuery cquery) throws AtomSetException {
		// TODO implement this method
		throw new MethodNotImplementedError("This method isn't implemented");
	}

	@Override
	public Term getTerm(String label) throws AtomSetException {
		// TODO implement this method
		throw new MethodNotImplementedError("This method isn't implemented");
	}

	@Override
	public CloseableIterator<Atom> iterator() {
		// TODO implement this method
		throw new MethodNotImplementedError("This method isn't implemented");
	}

	@Override
	public VariableGenerator getFreeVarGen() {
		// TODO implement this method
		throw new MethodNotImplementedError("This method isn't implemented");
	}

	@Override
	public boolean contains(Atom atom) throws AtomSetException {
		Statement statement = this.createStatement();
		try {
			StringBuilder query = new StringBuilder("SELECT 1 FROM ");
			query.append(this.getPredicateTableName(atom.getPredicate()));
			query.append(" WHERE ");
			int termNumber = 0;
			for(Term t : atom) {
				query.append("term");
				query.append(termNumber);
				query.append(" = ");
				query.append('\'');
				query.append(t);
				query.append("' ");
			}
			query.append(';');
			return statement.execute(query.toString());
		} catch(SQLException e) {
		}
		return false;
	}
	
	@Override
	public CloseableIterator<Atom> match(Atom atom) throws AtomSetException {

		ConjunctiveQuery query = ConjunctiveQueryFactory.instance().create(new LinkedListAtomSet(atom));
		SqlHomomorphism solver = SqlHomomorphism.instance();

		try {
			return new SubstitutionIterator2AtomIterator(atom, solver.execute(query, this));
		} catch (HomomorphismException e) {
			throw new AtomSetException(e);
		}
	}

	@Override
	public CloseableIterator<Atom> atomsByPredicate(Predicate p) throws AtomSetException {
		List<Term> terms = new LinkedList<Term>();
		for (int i = 0; i < p.getArity(); ++i) {
			terms.add(DefaultTermFactory.instance().createVariable(i));
		}
		Atom atom = DefaultAtomFactory.instance().create(p, terms);
		ConjunctiveQuery query = ConjunctiveQueryFactory.instance().create(new LinkedListAtomSet(atom));
		SqlHomomorphism solver = SqlHomomorphism.instance();

		try {
			return new SubstitutionIterator2AtomIterator(atom, solver.execute(query, this));
		} catch (HomomorphismException e) {
			throw new AtomSetException(e);
		}
	}

	@Override
	public CloseableIterator<Term> termsByPredicatePosition(Predicate p, int position) throws AtomSetException {
		throw new MethodNotImplementedError();
	}

	@Override
	protected Statement add(Statement statement, Atom atom)
			throws AtomSetException {
		try {
			this.checkPredicateTable(atom.getPredicate());
			String tableName = this.getPredicateTableName(atom.getPredicate());
			Map<String, Object> data = new TreeMap<String, Object>();
			int i = -1;
			for (Term t : atom.getTerms()) {
				++i;
				data.put("term" + i, t);
			}
			String query = this.getDriver().getInsertOrIgnoreStatement(
					tableName, data);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(atom.toString() + " : " + query);
			}
			statement.addBatch(query);
		} catch (SQLException e) {
			throw new AtomSetException(e.getMessage(), e);
		}
		return statement;
	}

	/**
	 * @param predicate
	 * @throws AtomSetException 
	 */
	private void checkPredicateTable(Predicate predicate) throws AtomSetException {
		StringBuilder query = new StringBuilder("CREATE TABLE ");
		query.append(this.getPredicateTableName(predicate));
		query.append(" (");
		for(int i=0; i<predicate.getArity(); ++i) {
			if(i > 0) {
				query.append(", ");
			}
			query.append("term");
			query.append(i);
			query.append(" VARCHAR(256)");
		}
		query.append(");");
		try {
			this.createStatement().execute(query.toString());
		} catch (SQLException e) {
		}
	}

	/**
	 * @param predicate
	 * @return
	 */
	private String getPredicateTableName(Predicate predicate) {
		return predicate.getIdentifier().toString() + predicate.getArity();
	}

	@Override
	protected Statement remove(Statement statement, Atom atom)
			throws AtomSetException {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	protected boolean testDatabaseSchema() throws AtomSetException {
		return true;
	}

	@Override
	protected void createDatabaseSchema() throws AtomSetException {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public void clear() {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public Iterator<String> transformToSQL(Rule rangeRestrictedRule)
			throws AtomSetException {
		// TODO implement this method
		throw new MethodNotImplementedError();
	}

	@Override
	public CloseableIterator<Predicate> predicatesIterator() throws AtomSetException {
		// TODO implement this method
		throw new MethodNotImplementedError();
	}

	@Override
	public CloseableIterator<Term> termsIterator() throws AtomSetException {
		// TODO implement this method
		throw new MethodNotImplementedError();
	}

	@Override
	public CloseableIterator<Term> termsIterator(Type type) throws AtomSetException {
		// TODO implement this method
		throw new MethodNotImplementedError();
	}

}
