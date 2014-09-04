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
	
	protected Statement createStatement() throws StoreException {
		return this.driver.createStatement();
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
		boolean res = true;
		Statement statement = null;
		try {
			statement = this.createStatement();
			this.add(statement, atom);
			statement.executeBatch();
			this.getConnection().commit();
		} catch (SQLException e) {
			res = false;
		} catch (AtomSetException e) {
			res = false;
		} finally {
			if(statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					res = false;
				}
			}
		}
		return res;
	}
	
	/* (non-Javadoc)
	 * @see fr.lirmm.graphik.graal.store.Store#remove(fr.lirmm.graphik.graal.core.Atom)
	 */
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
			if(statement != null) {
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
			Statement statement = null;
			
			for(Atom a : stream) {
				statement = this.createStatement();
				this.add(statement, a);
				if((++c % MAX_BATCH_SIZE) == 0) {
					if(logger.isDebugEnabled()) {
						logger.debug("batch commit, size=" + MAX_BATCH_SIZE);
					}
					statement.executeBatch();
					statement.close();
				}
			} 
			
			if(!statement.isClosed()) {
				statement.executeBatch();
				statement.close();
			}
			
			this.getConnection().commit();
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	public void remove(Iterable<Atom> stream) throws AtomSetException {
		try {
			int c = 0;
			Statement statement = this.createStatement();
			for(Atom a : stream) {
				this.remove(statement, a);
				if((++c % MAX_BATCH_SIZE) == 0) {
					if(logger.isDebugEnabled()) {
						logger.debug("batch commit, size=" + MAX_BATCH_SIZE);
					}
					statement.executeBatch();
					statement.close();
				}
			}
			
			if(!statement.isClosed()) {
				statement.executeBatch();
				statement.close();
			}
			
			this.getConnection().commit();
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	
}
