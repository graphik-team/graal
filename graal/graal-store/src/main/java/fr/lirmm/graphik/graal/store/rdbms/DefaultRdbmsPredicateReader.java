/**
 * 
 */
package fr.lirmm.graphik.graal.store.rdbms;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.store.StoreException;
import fr.lirmm.graphik.graal.store.rdbms.driver.RdbmsDriver;
import fr.lirmm.graphik.util.stream.AbstractReader;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
class DefaultRdbmsPredicateReader extends AbstractReader<Predicate> {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(DefaultRdbmsPredicateReader.class);
	
	private static final String GET_ALL_PREDICATES_QUERY = "SELECT * FROM "
														+ DefaultRdbmsStore.PREDICATE_TABLE_NAME
														+ ";";
	private boolean hasNextCallDone = false;
	private boolean hasNext; 

	private ResultSet results;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////

	DefaultRdbmsPredicateReader(RdbmsDriver driver) throws StoreException {
		Statement stat;
		try {
			stat = driver.createStatement();
			results = stat.executeQuery(GET_ALL_PREDICATES_QUERY);
		} catch (SQLException e) {
			throw new StoreException(e);
		}
		
	}

	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.lirmm.graphik.util.stream.ObjectReader#hasNext()
	 */
	@Override
	public boolean hasNext() {
		if (!this.hasNextCallDone) {
			this.hasNextCallDone = true;
			try {
				this.hasNext = this.results.next();
			} catch (SQLException e) {
				LOGGER.error(e.getMessage(), e);
				this.hasNext = false;
			}
		}
		return this.hasNext;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.lirmm.graphik.util.stream.ObjectReader#next()
	 */
	@Override
	public Predicate next() {
		if (!this.hasNextCallDone)
			this.hasNext();
		this.hasNextCallDone = false;
		try {
			return this.readPredicate(this.results);
		} catch (SQLException e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
	}

	/**
	 * @param results2
	 * @return
	 * @throws SQLException 
	 */
	private Predicate readPredicate(ResultSet results2) throws SQLException {
		return new Predicate(results.getString(1), results.getInt(2));
	}

}
