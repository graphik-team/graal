/**
 * 
 */
package fr.lirmm.graphik.graal.store.rdbms;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.atomset.AbstractAtomSet;
import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
import fr.lirmm.graphik.graal.homomorphism.DefaultHomomorphismFactory;
import fr.lirmm.graphik.graal.store.homomorphism.SqlHomomorphismChecker;
import fr.lirmm.graphik.graal.store.homomorphism.SqlUCQHomomorphismChecker;
import fr.lirmm.graphik.graal.store.rdbms.driver.DriverException;
import fr.lirmm.graphik.graal.store.rdbms.driver.RdbmsDriver;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public abstract class AbstractRdbmsStore extends AbstractAtomSet implements
		RdbmsStore {

	static {
		DefaultHomomorphismFactory.getInstance().addChecker(
				new SqlHomomorphismChecker());
		DefaultHomomorphismFactory.getInstance().addChecker(
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

	public RdbmsDriver getDriver() {
		return this.driver;
	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public boolean add(Atom atom) {
		boolean res = true;
		Statement statement = null;
		try {
			statement = this.createStatement();
			this.add(statement, atom);
			statement.executeBatch();
		} catch (SQLException e) {
			res = false;
		} catch (AtomSetException e) {
			res = false;
		} finally {
			if (statement != null) {
				try {
					this.getConnection().commit();
					statement.close();
				} catch (SQLException e) {
					res = false;
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
	public boolean addAll(Iterable<? extends Atom> stream) throws AtomSetException {
		try {
			int c = 0;
			Statement statement = this.createStatement();

			for (Atom a : stream) {
				this.add(statement, a);
				if ((++c % MAX_BATCH_SIZE) == 0) {
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
	public boolean removeAll(Iterable<? extends Atom> stream) throws AtomSetException {
		try {
			int c = 0;
			Statement statement = this.createStatement();
			for (Atom a : stream) {
				this.remove(statement, a);
				if ((++c % MAX_BATCH_SIZE) == 0) {
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
