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

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.api.core.AbstractAtomSet;
import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.homomorphism.DefaultHomomorphismFactory;
import fr.lirmm.graphik.graal.store.rdbms.driver.DriverException;
import fr.lirmm.graphik.graal.store.rdbms.driver.RdbmsDriver;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public abstract class AbstractRdbmsStore extends AbstractAtomSet implements
		RdbmsStore {

	static {
		DefaultHomomorphismFactory.instance().addChecker(
				new SqlHomomorphismChecker());
		DefaultHomomorphismFactory.instance().addChecker(
				new SqlUCQHomomorphismChecker());
	}

	private static final Logger LOGGER = LoggerFactory
			.getLogger(AbstractRdbmsStore.class);

	private final RdbmsDriver driver;

	private int unbatchedAtoms = 0;
	private Statement unbatchedStatement = null;

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
		try {
			statement = this.createStatement();
			this.add(statement, atom);
			int[] ret = statement.executeBatch();
			for(int i : ret) {
				if(i>0) {
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

	public void addUnbatched(Atom a) {
		try {
			if (this.unbatchedStatement == null) {
				this.unbatchedStatement = this.createStatement();
			}
			this.add(this.unbatchedStatement, a);
			++this.unbatchedAtoms;
			if (this.unbatchedAtoms >= MAX_BATCH_SIZE) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("batch commit, size=" + MAX_BATCH_SIZE);
				}
				this.commitAtoms();
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	public void commitAtoms() {
		try {
			if (this.unbatchedStatement != null) {
				this.unbatchedStatement.executeBatch();
				this.getConnection().commit();
				this.unbatchedAtoms = 0;
				this.unbatchedStatement.close();
				this.unbatchedStatement = null;
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
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
	public boolean addAll(Iterator<? extends Atom> stream) throws AtomSetException {
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
		} catch (SQLException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return true;
	}

	@Override
	public boolean removeAll(Iterator<? extends Atom> stream) throws AtomSetException {
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
		} catch (SQLException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return true;
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
		} catch(DriverException e) {
			throw new AtomSetException(e);
		}
	}

	protected abstract Statement add(Statement statement, Atom atom)
			throws AtomSetException;

	protected abstract Statement remove(Statement statement, Atom atom)
			throws AtomSetException;

	protected abstract boolean testDatabaseSchema() throws AtomSetException;

	protected abstract void createDatabaseSchema() throws AtomSetException;

}
