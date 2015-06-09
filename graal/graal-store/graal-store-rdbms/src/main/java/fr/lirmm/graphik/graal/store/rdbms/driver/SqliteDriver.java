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

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class SqliteDriver extends AbstractRdbmsDriver {
	
	private static final Logger LOGGER = LoggerFactory
            .getLogger(SqliteDriver.class);
	
	private static final String INSERT_IGNORE = "INSERT OR IGNORE INTO ";
	
	 /**
	  * 
	  * @param file
	  * @throws StoreException
	  */
	public SqliteDriver(File file)
			throws DriverException {
		super(openConnection(file), INSERT_IGNORE);
	}
	 
	private static Connection openConnection(File file) throws DriverException {
		Connection connection;
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			LOGGER.error(e.getMessage(), e);
			throw new DriverException(e.getMessage(), e);
		}
		
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + file);
		} catch (SQLException e) {
			LOGGER.error(e.getMessage(), e);
			throw new DriverException(e.getMessage(), e);
		}
		
		return connection;
	}

}
