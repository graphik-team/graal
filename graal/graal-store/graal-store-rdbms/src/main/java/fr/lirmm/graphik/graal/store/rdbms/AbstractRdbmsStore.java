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
package fr.lirmm.graphik.graal.store.rdbms;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.store.AbstractStore;
import fr.lirmm.graphik.graal.homomorphism.DefaultHomomorphismFactory;
import fr.lirmm.graphik.graal.store.rdbms.driver.DriverException;
import fr.lirmm.graphik.graal.store.rdbms.driver.RdbmsDriver;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.CloseableIterator;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public abstract class AbstractRdbmsStore extends AbstractStore implements RdbmsStore {

	protected static final int VARCHAR_SIZE = 128;
	// new table fields name
	protected static final String PREFIX_TERM_FIELD = "term";

	private TreeMap<Predicate, String> predicateMap = new TreeMap<Predicate, String>();

	static {
		DefaultHomomorphismFactory.instance().addChecker(new SqlHomomorphismChecker());
		DefaultHomomorphismFactory.instance().addChecker(new SqlUCQHomomorphismChecker());
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractRdbmsStore.class);

	private final RdbmsDriver driver;

	protected static final int MAX_BATCH_SIZE = 1024;

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

	/**
	 * This method close the associated driver instance.
	 */
	@Override
	public void close() {
		this.driver.close();
	}

	@Override
	public boolean add(Atom atom) throws AtomSetException {
		boolean res = false;
		Statement statement = null;
		System.out.println(atom);
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

	/**
	 * 
	 * @param driver
	 * @throws SQLException
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
	public CloseableIterator<Atom> iterator() {
		try {
			return new DefaultRdbmsAtomIterator(this);
		} catch (AtomSetException e) {
			if (LOGGER.isErrorEnabled()) {
				LOGGER.error(e.getMessage(), e);
			}
			return null;
		}
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
		} catch (DriverException e) {
			throw new AtomSetException(e);
		}
	}

	protected abstract Statement add(Statement statement, Atom atom) throws AtomSetException;

	protected abstract Statement remove(Statement statement, Atom atom) throws AtomSetException;

	protected abstract boolean testDatabaseSchema() throws AtomSetException;

	protected abstract void createDatabaseSchema() throws AtomSetException;

	/**
	 * Get the table name of this predicate. If there is no table for it, a new
	 * table is created.
	 * 
	 * @param predicate
	 * @return
	 * @throws SQLException
	 * @throws AtomSetException
	 */
	protected String getPredicateTable(Predicate predicate) throws AtomSetException {

		String tableName = this.getPredicateTableName(predicate);
		if (tableName == null) {
			if (tableName == null) {
				tableName = this.createPredicateTable(predicate);
			}
		}

		return tableName;
	}

	/**
	 * @param predicate
	 * @return the table name corresponding to this predicate or null if this
	 *         predicate doesn't exist.
	 * @throws SQLException
	 */
	protected abstract String predicateTableExist(Predicate predicate) throws AtomSetException;


	/**
	 * 
	 * @param predicate
	 * @return
	 * @throws AtomSetException
	 * @throws SQLException
	 */
	protected String createPredicateTable(Predicate predicate) throws AtomSetException {
		String tableName = this.getFreshPredicateTableName(predicate);
		if (predicate.getArity() >= 1) {
			Statement stat = this.createStatement();
			String query = generateCreateTablePredicateQuery(tableName, predicate.getArity());
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
			this.predicateMap.put(predicate, tableName);
		} else {
			throw new AtomSetException("Unsupported arity 0"); // TODO Why ?!
		}
		return tableName;
	}

	protected abstract String getFreshPredicateTableName(Predicate predicate) throws AtomSetException;


	protected String getPredicateTableName(Predicate predicate) throws AtomSetException {
		// look in the local map
		String tableName = this.predicateMap.get(predicate);
		if (tableName == null) {
			// look in the database
			tableName = this.predicateTableExist(predicate);
			this.predicateMap.put(predicate, tableName);
		}
		return tableName;
	}


	protected static String generateCreateTablePredicateQuery(String tableName, int arity) {
		StringBuilder primaryKey = new StringBuilder("PRIMARY KEY (");
		StringBuilder query = new StringBuilder("CREATE TABLE ");
		query.append(tableName);

		query.append('(').append(PREFIX_TERM_FIELD).append('0');
		query.append(" varchar(").append(VARCHAR_SIZE).append(")");
		primaryKey.append("term0");
		for (int i = 1; i < arity; i++) {
			query.append(", ").append(PREFIX_TERM_FIELD).append(i).append(" varchar(" + VARCHAR_SIZE + ")");
			primaryKey.append(", term" + i);
		}
		primaryKey.append(")");

		query.append(',');
		query.append(primaryKey);
		query.append(");");
		return query.toString();
	}


}
