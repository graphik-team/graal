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
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import fr.lirmm.graphik.graal.store.rdbms.util.DBColumn;
import fr.lirmm.graphik.graal.store.rdbms.util.DBTable;
import fr.lirmm.graphik.util.stream.CloseableIterator;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public interface RdbmsDriver {

	Connection getConnection();

	Statement createStatement() throws SQLException;
	void close();

	/**
	 * Returns database metadata
	 * 
	 * @return the DatabaseMetaData.
	 */
	DatabaseMetaData getMetaData() throws SQLException;

	/**
	 * Generates an INSERT OR IGNORE SQL statement.
	 * 
	 * @param tableName
	 * @param data
	 * @return a String representing an insert or ignore query statement.
	 */
	String getInsertOrIgnoreQuery(DBTable tableName, Map<String, String> data) throws SQLException;

	/**
	 * 
	 * @param tableName
	 * @param selectQuery
	 * @return a String representing an insert or ignore SQL statement.
	 * @throws SQLException
	 */
	String getInsertOrIgnoreQuery(DBTable tableName, String selectQuery) throws SQLException;

	/**
	 * Formats identifier (table and column name) to fulfill database storage
	 * format (upper or lower case). This method does not remove any unavailable
	 * characters.
	 * 
	 * @param identifier
	 * @return a String.
	 */
	String formatIdentifier(String identifier) throws SQLException;

	/**
	 * Returns an iterator over the database tables.
	 * 
	 * @return an iterator over the database tables.
	 * @throws SQLException
	 */
	CloseableIterator<DBTable> getTables() throws SQLException;

	/**
	 * Returns the table information for the specified table name or null if
	 * there is no table with this name.
	 * 
	 * @param tableName
	 *            The table name correctly formated (upper or lower case).
	 * @return the table information for the specified table name or null if there is no table with this name.
	 * @throws SQLException
	 */
	DBTable getTable(String tableName) throws SQLException;

	/**
	 * Return columns informations for the specified table name.
	 * 
	 * @param tableName
	 *            The table name correctly formated (upper or lower case).
	 * 
	 * @return columns informations for the specified table name.
	 * @throws SQLException
	 */
	List<DBColumn> getColumns(String tableName) throws SQLException;

	/**
	 * Return true if this database instance is case sensitive, false otherwise.
	 * 
	 * @return true if this database instance is case sensitive, false otherwise.
	 * @throws SQLException
	 */
	boolean isCaseSensitive() throws SQLException;

	/**
	 * Return a String containing extra characters available for table and
	 * column name (those beyond a-z, A-Z, 0-9 and _).
	 * 
	 * @return a String containing extra characters available.
	 * @throws SQLException
	 */
	String getExtraIdentifierCharacters() throws SQLException;

	/**
	 * Returns the maximum number of characters that this database allows in a
	 * table name.
	 * 
	 * @return the maximum number of characters allowed in a procedure name; a
	 *         result of zero means that there is no limit or the limit is not
	 *         known
	 * @throws SQLException
	 */
	int getMaxTableNameLength() throws SQLException;

	boolean checkIdentifierName(String identifier) throws SQLException;



}
