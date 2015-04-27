/**
 * 
 */
package fr.lirmm.graphik.graal.store.rdbms;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.core.HashMapSubstitution;
import fr.lirmm.graphik.graal.core.Substitution;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
import fr.lirmm.graphik.graal.core.stream.SubstitutionReader;
import fr.lirmm.graphik.util.MethodNotImplementedError;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public class ResultSetSubstitutionReader implements SubstitutionReader {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(ResultSetSubstitutionReader.class);
    private ResultSet results;
    private ResultSetMetaData metaData;
    private Statement statement;

    private boolean hasNextCallDone = false;
    private boolean hasNext;
	private boolean isBooleanQuery;
	private RdbmsStore store;

    // /////////////////////////////////////////////////////////////////////////
    //  CONSTRUCTORS
    // /////////////////////////////////////////////////////////////////////////

    /**
     * @param store
     * @param sqlQuery
     * @throws SQLException
     * @throws StoreException 
     */
    public ResultSetSubstitutionReader(RdbmsStore store, String sqlQuery) throws SQLException, AtomSetException {
    	this.store = store;
		this.statement = store.getDriver().getConnection().createStatement();
        this.results = statement.executeQuery(sqlQuery);
        this.metaData = results.getMetaData();
        this.isBooleanQuery = false;
    }
    
    /**
     * 
     * @param store
     * @param sqlQuery
     * @param isBooleanQuery
     * @throws SQLException
     * @throws StoreException
     */
	public ResultSetSubstitutionReader(RdbmsStore store, String sqlQuery,
			boolean isBooleanQuery) throws SQLException, AtomSetException {
		this.store = store;
		this.statement = store.getDriver().getConnection().createStatement();
        this.results = statement.executeQuery(sqlQuery);
        this.metaData = results.getMetaData();
		this.isBooleanQuery = isBooleanQuery;
	}

	@Override
	protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }

    // /////////////////////////////////////////////////////////////////////////
    //  METHODS
    // /////////////////////////////////////////////////////////////////////////

    @Override
    public void remove() {
        // TODO implement this method
        throw new MethodNotImplementedError();
    }

    @Override
    public boolean hasNext() {
        if (!this.hasNextCallDone) {
            this.hasNextCallDone = true;

            try {
                this.hasNext = this.results.next();
            } catch (SQLException e) {
                LOGGER.error("Error during atom reading", e);
                this.hasNext = false;
            }
        }

        return this.hasNext;
    }

    @Override
    public Substitution next() {
        if (!this.hasNextCallDone)
            this.hasNext();

        this.hasNextCallDone = false;

        try {
            Substitution substitution = new HashMapSubstitution();
            if(!isBooleanQuery) {
	            for (int i = 1; i <= this.metaData.getColumnCount(); ++i) {
					Term term = new Term(this.metaData.getColumnLabel(i),
	                        Term.Type.VARIABLE);
	                Term substitut = this.store.getTerm(this.results.getString(i));
	                substitution.put(term, substitut);
	            }
            }
            return substitution;
        } catch (Exception e) {
        	LOGGER.error("Error while reading the next substitution", e);
            return null;
        }
    }

    @Override
    public Iterator<Substitution> iterator() {
        return this;
    }

    @Override
    public void close() {
        try {
            this.results.close();
            this.statement.close();
        } catch (SQLException e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }
    
}
