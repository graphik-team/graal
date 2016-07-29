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
package fr.lirmm.graphik.graal.store.rdbms;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.IteratorException;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
abstract class AbstractResultSetIterator<T> implements CloseableIterator<T> {

	protected ResultSetMetaData metaData;
	private Statement statement;
	protected RdbmsStore store;

	protected boolean hasNextCallDone = false;
	protected boolean hasNext;
	protected ResultSet results;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * @param store
	 * @param sqlQuery
	 * @throws SQLException
	 * @throws StoreException
	 */
	public AbstractResultSetIterator(RdbmsStore store, String sqlQuery) throws SQLException {
		this.store = store;
		this.statement = store.getDriver().getConnection().createStatement();
		this.results = statement.executeQuery(sqlQuery);
		this.metaData = results.getMetaData();
	}

	public AbstractResultSetIterator(RdbmsStore store, PreparedStatement st) throws SQLException {
		this.store = store;
		this.results = st.executeQuery();
		this.metaData = results.getMetaData();
	}

	// /////////////////////////////////////////////////////////////////////////
	// ABSTRACT METHODS
	// /////////////////////////////////////////////////////////////////////////

	protected abstract T computeNext() throws IteratorException;

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public boolean hasNext() throws IteratorException {
		if (!this.hasNextCallDone) {
			this.hasNextCallDone = true;

			try {
				this.hasNext = this.results.next();
			} catch (SQLException e) {
				throw new IteratorException(e);
			}
		}

		return this.hasNext;
	}

	@Override
	public T next() throws IteratorException {
		if (!this.hasNextCallDone)
			this.hasNext();

		this.hasNextCallDone = false;

		return computeNext();
	}

	@Override
	public void close() {
		try {
			this.results.close();
			if (statement != null)
				this.statement.close();
		} catch (SQLException e) {
			throw new Error("Untreated exception");
		}
	}

	// /////////////////////////////////////////////////////////////////////////
	// OBJECT OVERRIDE METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	protected void finalize() throws Throwable {
		this.close();
		super.finalize();
	}

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

}
