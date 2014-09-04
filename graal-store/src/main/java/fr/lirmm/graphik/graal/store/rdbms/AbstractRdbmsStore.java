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
import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
import fr.lirmm.graphik.graal.store.AbstractStore;
import fr.lirmm.graphik.graal.store.StoreException;
import fr.lirmm.graphik.graal.store.rdbms.driver.RdbmsDriver;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public abstract class AbstractRdbmsStore extends AbstractStore implements
RdbmsStore {
	
	private static final Logger logger = LoggerFactory
			.getLogger(AbstractRdbmsStore.class);
	
	private final RdbmsDriver driver;
	
	public RdbmsDriver getDriver() {
		return this.driver;
	}
	
	protected Connection getConnection() {
		return this.driver.getConnection();
	}
	
	protected Statement getStatement() throws StoreException {
		return this.driver.getStatement();
	}

	protected abstract Statement add(Statement statement, Atom atom) throws StoreException;
	
	protected abstract Statement remove(Statement statement, Atom atom) throws StoreException;
	
	protected abstract boolean testDatabaseSchema() throws StoreException;
	
	protected abstract void createDatabaseSchema() throws StoreException;	
	
	protected static final int MAX_BATCH_SIZE = 1024;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.lirmm.graphik.alaska.store.IWriteableStore#add(fr.lirmm.graphik.kb
	 * .core.IAtom)
	 */
	@Override
	public boolean add(Atom atom) {
		try {
			Statement statement = this.getStatement();
			this.add(statement, atom);
			statement.executeBatch();
			this.getConnection().commit();
		} catch (SQLException e) {
			return false;
		} catch (AtomSetException e) {
			return false;
		}
		return true;
	}

	private int unbatchedAtoms = 0;
	public void addUnbatched(Atom a) {
		try {
			Statement statement = this.getStatement();
			this.add(statement, a);
			if((++this.unbatchedAtoms % MAX_BATCH_SIZE) == 0) {
				if(logger.isDebugEnabled()) {
					logger.debug("batch commit, size=" + MAX_BATCH_SIZE);
				}
				this.commitAtoms();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	public void commitAtoms() {
		try {
			this.getStatement().executeBatch();
			this.getConnection().commit();
			this.unbatchedAtoms = 0;
		}
		catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}
	
	/* (non-Javadoc)
	 * @see fr.lirmm.graphik.graal.store.Store#remove(fr.lirmm.graphik.graal.core.Atom)
	 */
	@Override
	public boolean remove(Atom atom) {
		try {
			Statement statement = this.getStatement();
			this.remove(statement, atom);
			statement.executeBatch();
			this.getConnection().commit();
		} catch (SQLException e) {
			return false;
		} catch (AtomSetException e) {
			return false;
		}
		return true;
	}


	
	/**
	 * 
	 * @param driver
	 * @throws SQLException
	 */
	public AbstractRdbmsStore(RdbmsDriver driver) throws StoreException {
		this.driver = driver;
		try {
			this.driver.getConnection().setAutoCommit(false);
		} catch (SQLException e) {
			throw new StoreException("ACID transaction required", e);
		}
		
		if(!this.testDatabaseSchema())
			this.createDatabaseSchema();

	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.lirmm.graphik.alaska.store.IWriteableStore#add(fr.lirmm.graphik.kb
	 * .stream.AtomReader)
	 */
	@Override
	public void addAll(Iterable<Atom> stream) throws AtomSetException {
		try {
			int c = 0;
			Statement statement = this.getStatement();
			for(Atom a : stream) {
				this.add(statement, a);
				if((++c % MAX_BATCH_SIZE) == 0) {
					if(logger.isDebugEnabled()) {
						logger.debug("batch commit, size=" + MAX_BATCH_SIZE);
					}
					statement.executeBatch();
				}
			}
			statement.executeBatch();
			this.getConnection().commit();
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	public void remove(Iterable<Atom> stream) throws AtomSetException {
		try {
			int c = 0;
			Statement statement = this.getStatement();
			for(Atom a : stream) {
				this.remove(statement, a);
				if((++c % MAX_BATCH_SIZE) == 0) {
					if(logger.isDebugEnabled()) {
						logger.debug("batch commit, size=" + MAX_BATCH_SIZE);
					}
					statement.executeBatch();
				}
			}
			statement.executeBatch();
			this.getConnection().commit();
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	
}
