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
package fr.lirmm.graphik.graal.store.rdbms;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.core.store.AbstractStore;
import fr.lirmm.graphik.graal.homomorphism.SmartHomomorphism;
import fr.lirmm.graphik.graal.store.rdbms.driver.RdbmsDriver;
import fr.lirmm.graphik.graal.store.rdbms.homomorphism.SqlHomomorphismChecker;
import fr.lirmm.graphik.graal.store.rdbms.homomorphism.SqlUCQHomomorphismChecker;
import fr.lirmm.graphik.graal.store.rdbms.util.DBColumn;
import fr.lirmm.graphik.graal.store.rdbms.util.DBTable;
import fr.lirmm.graphik.graal.store.rdbms.util.SQLQuery;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.IteratorException;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public abstract class AbstractRdbmsStore extends AbstractStore implements RdbmsStore {

	protected static final int VARCHAR_SIZE = 128;

	private TreeMap<Predicate, DBTable> predicateMap = new TreeMap<Predicate, DBTable>();

	static {
		SmartHomomorphism.instance().addChecker(new SqlHomomorphismChecker());
		SmartHomomorphism.instance().addChecker(new SqlUCQHomomorphismChecker());
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractRdbmsStore.class);

	protected static final int MAX_BATCH_SIZE = 1024;

	private final RdbmsDriver driver;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public RdbmsDriver getDriver() {
		return this.driver;
	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public boolean add(Atom atom) throws AtomSetException {
		boolean res = false;
		Statement statement = null;
		try {
			statement = this.createStatement();
			this.add(statement, atom);
			int[] ret = statement.executeBatch();
			for (int i : ret) {
				if (i > 0) {
					res = true;
					break;
				}
			}
		} catch (SQLException e) {
			throw new AtomSetException("Error while adding an atom", e);
		} finally {
			if (statement != null) {
				try {
					this.getConnection().commit();
					statement.close();
				} catch (SQLException e) {
					throw new AtomSetException("Error while adding an atom", e);
				}
			}
		}
		return res;
	}

	@Override
	public boolean contains(Atom atom) throws AtomSetException {
		if (!this.check(atom)) {
			return false;
		}
		Statement statement = this.createStatement();
		SQLQuery query = this.getConjunctiveQueryTranslator().translateContainsQuery(atom);
		boolean res = false;
		if (!query.hasSchemaError()) {
			ResultSet results;
			try {
				statement = this.createStatement();
				results = statement.executeQuery(query.toString());
				if (results.next()) {
					res = true;
				}
				results.close();
			} catch (SQLException e) {
				if (statement != null) {
					try {
						statement.close();
					} catch (SQLException sqlEx) {
					}
				}
				throw new AtomSetException("Error during check contains atom: " + atom, e);
			}
		}
		return res;
	}

	@Override
	public boolean remove(Atom atom) {
		boolean res = true;
		Statement statement = null;
		try {
			statement = this.createStatement();
			this.remove(statement, atom);
			statement.executeBatch();
			this.getConnection().commit();
		} catch (SQLException e) {
			res = false;
		} catch (AtomSetException e) {
			res = false;
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					res = false;
				}
			}
		}
		return res;
	}

	@Override
	public void clear() throws AtomSetException {
		CloseableIterator<Predicate> it = this.predicatesIterator();
		Statement stat = null;
		try {
			try {
				stat = this.createStatement();
				while (it.hasNext()) {
					Predicate p = it.next();
					this.removePredicate(stat, p);
				}
				this.getConnection().commit();
			} catch (IteratorException e) {
				this.getConnection().rollback();
				throw new AtomSetException(e);
			} catch (SQLException e) {
				this.getConnection().rollback();
				throw new AtomSetException(e);
			} finally {
				if (stat != null)
					stat.close();
			}
		} catch (SQLException e) {
			throw new AtomSetException(e);
		}
	}

	/**
	 * 
	 * @param driver
	 * @throws AtomSetException
	 */
	public AbstractRdbmsStore(RdbmsDriver driver) throws AtomSetException {
		this.driver = driver;
		try {
			this.driver.getConnection().setAutoCommit(false);
		} catch (SQLException e) {
			throw new AtomSetException("ACID transaction required", e);
		}

		if (!this.testDatabaseSchema())
			this.createDatabaseSchema();

	}

	@Override
	public boolean addAll(CloseableIterator<? extends Atom> stream) throws AtomSetException {
		try {
			int c = 0;
			Statement statement = this.createStatement();

			while (stream.hasNext()) {
				this.add(statement, stream.next());
				if (++c % MAX_BATCH_SIZE == 0) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("batch commit, size=" + MAX_BATCH_SIZE);
					}
					statement.executeBatch();
					statement.close();
					statement = this.createStatement();
				}
			}

			// if(statement != null) {// && !statement.isClosed()) {
			statement.executeBatch();
			statement.close();
			// }

			this.getConnection().commit();
		} catch (Exception e) {
			throw new AtomSetException(e);
		}
		return true;
	}

	@Override
	public boolean removeAll(CloseableIterator<? extends Atom> stream) throws AtomSetException {
		try {
			int c = 0;
			Statement statement = this.createStatement();
			while (stream.hasNext()) {
				this.remove(statement, stream.next());
				if (++c % MAX_BATCH_SIZE == 0) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("batch commit, size=" + MAX_BATCH_SIZE);
					}
					statement.executeBatch();
					statement.close();
				}
			}

			if (!statement.isClosed()) {
				statement.executeBatch();
				statement.close();
			}

			this.getConnection().commit();
		} catch (Exception e) {
			throw new AtomSetException(e);
		}
		return true;
	}

	@Override
	public boolean check(Atom a) throws AtomSetException {
		return true;
	}

	@Override
	public boolean check(Predicate p) throws AtomSetException {
		return true;
	}

	@Override
	public CloseableIterator<Atom> iterator() {
		try {
			return new RdbmsAtomIterator(this);
		} catch (AtomSetException e) {
			if (LOGGER.isErrorEnabled()) {
				LOGGER.error(e.getMessage(), e);
			}
			return null;
		}
	}
	
	@Override
	public boolean isWriteable() throws AtomSetException {
		try {
			return !this.getConnection().isReadOnly();
		} catch (SQLException e) {
			throw new AtomSetException(e);
		}
	}

	/**
	 * This method close the associated driver instance.
	 */
	@Override
	public void close() {
		this.driver.close();
	}

	// /////////////////////////////////////////////////////////////////////////
	// PROTECTED METHODS
	// /////////////////////////////////////////////////////////////////////////

	protected Connection getConnection() {
		return this.driver.getConnection();
	}

	protected Statement createStatement() throws AtomSetException {
		try {
			return this.driver.createStatement();
		} catch (SQLException e) {
			throw new AtomSetException(e);
		}
	}

	protected abstract Statement add(Statement statement, Atom atom) throws AtomSetException;

	protected Statement remove(Statement statement, Atom atom) throws AtomSetException {
		if (!this.check(atom)) {
			return statement;
		}
		SQLQuery query = this.getConjunctiveQueryTranslator().translateRemove(atom);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Removing " + atom.toString() + " : " + query.toString());
		}
		try {
			if (!query.hasSchemaError()) {
				statement.addBatch(query.toString());
			}
		} catch (SQLException e) {
			throw new AtomSetException(e);
		}
		return statement;
	}

	/**
	 * Get the table informations associated to the specified predicate. If
	 * there is no table for it, a new table is created.
	 * 
	 * @param predicate
	 * @return the table informations associated to the specified predicate.
	 * @throws AtomSetException
	 */
	protected DBTable createPredicateTableIfNotExist(Predicate predicate) throws AtomSetException {

		DBTable tableName = this.getPredicateTable(predicate);
		if (tableName == null) {
			tableName = this.createPredicateTable(predicate);
		}

		return tableName;
	}

	/**
	 * Get the table informations associated to the specified predicate. If
	 * there is no table for it, return null.
	 * 
	 * @param predicate
	 * @return the table informations associated to the specified predicate.
	 * @throws AtomSetException
	 */
	protected DBTable getPredicateTable(Predicate predicate) throws AtomSetException {
		// look in the local map
		DBTable table = this.predicateMap.get(predicate);
		if (table == null) {
			// look in the database
			table = this.getPredicateTableIfExist(predicate);
			this.predicateMap.put(predicate, table);
		}
		return table;
	}

	/**
	 * Create a table associated to the specified predicate and return the table
	 * informations.
	 * 
	 * @param predicate
	 * @return the table informations.
	 * @throws AtomSetException
	 */
	protected DBTable createPredicateTable(Predicate predicate) throws AtomSetException {
		String tableName = this.getFreshPredicateTableName(predicate);
		DBTable table = null;
		if (predicate.getArity() >= 1) {
			Statement stat = this.createStatement();
			table = generateNewDBTableData(tableName, predicate.getArity());
			String query = this.getConjunctiveQueryTranslator().translateCreateTable(table);
			try {
				stat.executeUpdate(query);
			} catch (SQLException e) {
				throw new AtomSetException("Error during table creation: " + query, e);
			}
			if (stat != null) {
				try {
					stat.close();
				} catch (SQLException e) {
					throw new AtomSetException(e);
				}
			}

			// add to the local map
			this.predicateMap.put(predicate, table);
		} else {
			throw new AtomSetException("Unsupported arity 0");
		}
		return table;
	}

	protected void removePredicate(Statement stat, Predicate p) throws AtomSetException {
		try {
			DBTable table = this.getPredicateTable(p);
			String query = String.format("DROP TABLE %s", table.getName());
			stat.execute(query);
			this.predicateMap.remove(p);
		} catch (SQLException e) {
			throw new AtomSetException(e);
		}
	}

	private static DBTable generateNewDBTableData(String tableName, int arity) {
		List<DBColumn> columns = new ArrayList<DBColumn>();
		for (int i = 0; i < arity; ++i) {
			columns.add(new DBColumn(AbstractRdbmsConjunctiveQueryTranslator.PREFIX_TERM_FIELD + i, Types.VARCHAR));
		}
		return new DBTable(tableName, columns);
	}

	// /////////////////////////////////////////////////////////////////////////
	// ABSTRACT METHODS
	// /////////////////////////////////////////////////////////////////////////

	protected abstract String getFreshPredicateTableName(Predicate predicate) throws AtomSetException;

	protected abstract boolean testDatabaseSchema() throws AtomSetException;

	protected abstract void createDatabaseSchema() throws AtomSetException;

	/**
	 * Ask the database for table informations associated to the specified
	 * predicate. If there is no table for it, return null.
	 * 
	 * @param predicate
	 * @return the table informations associated to the specified predicate if exist, null otherwise.
	 * @throws AtomSetException
	 */
	protected abstract DBTable getPredicateTableIfExist(Predicate predicate) throws AtomSetException;

}
