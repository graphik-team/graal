/* Graal v0.7.4
 * Copyright (c) 2014-2015 Inria Sophia Antipolis - Méditerranée / LIRMM (Université de Montpellier & CNRS)
 * All rights reserved.
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * Author(s): Clément SIPIETER
 *            Mélanie KÖNIG
 *            Swan ROCHER
 *            Jean-François BAGET
 *            Michel LECLÈRE
 *            Marie-Laure MUGNIER
 */
 /**
 * 
 */
package fr.lirmm.graphik.graal.store.rdbms.driver;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Map;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public interface RdbmsDriver {

	Connection getConnection();
	Statement createStatement() throws DriverException;
	void close();
	
	/**
	 * Generate an INSERT OR IGNORE SQL statement.
	 * 
	 * @param tableName
	 * @param values
	 * @return
	 */
	String getInsertOrIgnoreStatement(String tableName, Map<String, Object> data);

	/**
	 * Generate an INSERT OR IGNORE SQL statement.
	 * 
	 * @param tableName
	 * @param selectQuery
	 * @return
	 */
	String getInsertOrIgnoreStatement(String tableName, String selectQuery);

}
