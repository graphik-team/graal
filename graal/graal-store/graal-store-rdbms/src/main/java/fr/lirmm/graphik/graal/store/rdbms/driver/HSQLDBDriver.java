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
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
import fr.lirmm.graphik.util.MethodNotImplementedError;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class HSQLDBDriver extends AbstractRdbmsDriver {

	private static final Logger LOGGER = LoggerFactory
            .getLogger(HSQLDBDriver.class);
	

	/**
	 * 
	 * @param host
	 * @param dbName
	 * @param user
	 * @param password
	 * @throws AtomSetException
	 */
	public HSQLDBDriver(String alias, Map<String, String> properties)
			throws AtomSetException {
		super(openConnection(alias, properties), "");
	}
	
	public HSQLDBDriver(String uri) throws AtomSetException {
		super(openConnection(uri), "");
	}

	private static Connection openConnection(String alias,
			Map<String, String> properties)
			throws AtomSetException {
		StringBuilder uri = new StringBuilder();
		uri.append("jdbc:hsqldb:mem:").append(alias);
		if (properties != null) {
			for (Map.Entry<String, String> e : properties.entrySet()) {
				uri.append(';').append(e.getKey()).append('=')
						.append(e.getValue());
			}
		}
		return openConnection(uri.toString());
	}
	
	private static Connection openConnection(String uri) throws AtomSetException {
		Connection connection;
		try {
			Class.forName("org.hsqldb.jdbc.JDBCDriver").newInstance();
		} catch (InstantiationException e) {
			LOGGER.error(e.getMessage(), e);
			throw new AtomSetException(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			LOGGER.error(e.getMessage(), e);
			throw new AtomSetException(e.getMessage(), e);
		} catch (ClassNotFoundException e) {
			LOGGER.error(e.getMessage(), e);
			throw new AtomSetException(e.getMessage(), e);
		}
		
		try {
			connection = DriverManager.getConnection(uri);
		} catch (SQLException e) {
			LOGGER.error(e.getMessage(), e);
			throw new AtomSetException(e.getMessage(), e);
		}
		return connection;
	}
	
	@Override
	public String getInsertOrIgnoreStatement(String tableName,
			Map<String, Object> data) {
		StringBuilder fields = new StringBuilder();
		StringBuilder values = new StringBuilder();
		StringBuilder whereClause = new StringBuilder();

		boolean first = true;
		fields.append(" (");
		for (Map.Entry<String, Object> e : data.entrySet()) {
			if (!first) {
				fields.append(", ");
				values.append(", ");
				whereClause.append(" AND ");
			}
			fields.append(e.getKey());
			values.append('\'').append(e.getValue()).append('\'');
			whereClause.append(e.getKey()).append(" = ").append('\'')
					.append(e.getValue()).append('\'');
			first = false;
		}
		fields.append(") ");

		StringBuilder query = new StringBuilder();
		query.append("INSERT INTO ").append(tableName).append(fields)
				.append(" (SELECT ").append(values);

		// Where not exist
		query.append(" FROM test ")
				.append(" AS t WHERE NOT EXISTS (SELECT 1 FROM ")
				.append(tableName)
				.append(" WHERE ")
				.append(whereClause)
				.append(")); ");

		return query.toString();
	}

	@Override
	public String getInsertOrIgnoreStatement(String tableName,
			String selectQuery) {
		throw new MethodNotImplementedError();
	}

}
