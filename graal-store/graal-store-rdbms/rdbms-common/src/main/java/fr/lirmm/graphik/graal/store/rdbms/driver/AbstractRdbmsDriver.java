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
package fr.lirmm.graphik.graal.store.rdbms.driver;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.store.rdbms.util.DBColumn;
import fr.lirmm.graphik.graal.store.rdbms.util.DBTable;
import fr.lirmm.graphik.graal.store.rdbms.util.ResultSetCloseableIterator;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.converter.ConversionException;
import fr.lirmm.graphik.util.stream.converter.ConverterCloseableIterator;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public abstract class AbstractRdbmsDriver implements RdbmsDriver {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractRdbmsDriver.class);

	private static final String[] TABLE_TYPE = { "TABLE" };

	private final Connection DB_CONNECTION;

	private final DatabaseMetaData METADATA;

	private final ResultSet2DBTableConverter converter;

	public AbstractRdbmsDriver(Connection connection) throws SQLException {
		this.DB_CONNECTION = connection;
		this.METADATA = this.DB_CONNECTION.getMetaData();
		this.converter = new ResultSet2DBTableConverter(this);
	}

	@Override
	public Connection getConnection() {
		return this.DB_CONNECTION;
	}

	@Override
	public DatabaseMetaData getMetaData() {
		return this.METADATA;
	}

	@Override
	public Statement createStatement() throws SQLException {
		return this.getConnection().createStatement();
	}

	@Override
	public CloseableIterator<DBTable> getTables() throws SQLException {
		ResultSet tables = this.getMetaData().getTables(null, null, null, TABLE_TYPE);
		return new ConverterCloseableIterator<ResultSet, DBTable>(new ResultSetCloseableIterator(tables),
		                                                          this.converter);
	}

	@Override
	public DBTable getTable(String tableName) throws SQLException {
		tableName = this.formatIdentifier(tableName);
		ResultSet tables = this.getMetaData().getTables(null, null, tableName, TABLE_TYPE);
		if (tables.next()) {
			try {
				return this.converter.convert(tables);
			} catch (ConversionException e) {
				throw new SQLException(e);
			}
		}
		return null;
	}

	@Override
	public List<DBColumn> getColumns(String tableName) throws SQLException {
		tableName = this.formatIdentifier(tableName);
		ArrayList<DBColumn> list = new ArrayList<DBColumn>();
		DatabaseMetaData metaData = this.getConnection().getMetaData();
		ResultSet res = metaData.getColumns(null, null, tableName, null);
		while (res.next()) {
			String name = res.getString("COLUMN_NAME");
			int type = res.getInt("DATA_TYPE");
			list.add(new DBColumn(name, type));
		}
		return list;
	}

	@Override
	public String formatIdentifier(String identifier) throws SQLException {
		if (this.isCaseSensitive())
			return identifier;

		if (this.getMetaData().storesLowerCaseIdentifiers())
			return identifier.toLowerCase();

		// if (this.getMetaData().storesUpperCaseIdentifiers() ||
		// this.getMetaData().storesMixedCaseIdentifiers())
		return identifier.toUpperCase();
	}

	@Override
	public boolean isCaseSensitive() throws SQLException {
		return this.getMetaData().supportsMixedCaseIdentifiers();
	}

	@Override
	public String getExtraIdentifierCharacters() throws SQLException {
		return this.getMetaData().getExtraNameCharacters();
	}

	@Override
	public int getMaxTableNameLength() throws SQLException {
		return this.getMetaData().getMaxSchemaNameLength();
	}

	@Override
	public boolean checkIdentifierName(String identifier) throws SQLException {
		String extraChars = this.getExtraIdentifierCharacters();

		int maxLength = this.getMaxTableNameLength();
		if (maxLength > 0 && identifier.length() > maxLength) {
			return false;
		}

		// begin with an capital letter
		char c = identifier.charAt(0);
		if (c < 'A' || c > 'Z') {
			if (!this.isCaseSensitive() || c < 'a' || c > 'z') {
				return false;
			}
		}
		// followed with capital letters, digits, underscore or extra
		// characters
		for (int i = 1; i < identifier.length(); ++i) {
			c = identifier.charAt(i);
			if (c != '_' && (c < '0' || c > '9') && (c < 'A' || c > 'Z') && (extraChars.indexOf(c) == -1)) {
				if (!this.isCaseSensitive() || c < 'a' || c > 'z') {
					return false;
				}
			}
		}
		return true;

	}

	@Override
	protected void finalize() throws Throwable {
		this.close();
		super.finalize();
	}

	@Override
	public void close() {
		if (this.DB_CONNECTION != null) {
			try {
				this.DB_CONNECTION.rollback();
				this.DB_CONNECTION.close();
			} catch (SQLException e) {
				if (LOGGER.isWarnEnabled()) {
					LOGGER.warn("Error during closing DB connection", e);
				}
			}

		}
	}

}
