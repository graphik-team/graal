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

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.stream.SubstitutionReader;
import fr.lirmm.graphik.graal.core.HashMapSubstitution;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
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
					Term term = DefaultTermFactory.instance().createVariable(
							this.metaData.getColumnLabel(i));
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
