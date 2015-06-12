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
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public abstract class AbstractRdbmsDriver implements RdbmsDriver {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(AbstractRdbmsDriver.class);
	
	private final Connection DB_CONNECTION;
	protected final String INSERT_IGNORE;
	
	public AbstractRdbmsDriver(Connection connection, String insertORignore) {
		this.INSERT_IGNORE = insertORignore;
		this.DB_CONNECTION = connection;
	}
	
	@Override
	public Connection getConnection() {
		return this.DB_CONNECTION;
	}
	
	@Override
	public Statement createStatement() throws DriverException {
		try {
			return this.getConnection().createStatement();
		} catch (SQLException e) {
			throw new DriverException(e.getMessage(), e);
		}		
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
				if(LOGGER.isWarnEnabled()) {
					LOGGER.warn("Error during closing DB connection", e);
				}
			}
			
		}
	}

	// /////////////////////////////////////////////////////////////////////////
	//
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public String getInsertOrIgnoreStatement(String tableName,
			Map<String, Object> data) {
		StringBuilder fields = new StringBuilder(" (");
		StringBuilder values = new StringBuilder("");

		boolean first = true;
		for (Map.Entry<String, Object> e : data.entrySet()) {
			if (!first) {
				fields.append(", ");
				values.append(", ");
			}
			fields.append(e.getKey());
			values.append('\'').append(e.getValue()).append('\'');
			first = false;
		}
		fields.append(") ");

		StringBuilder sb = new StringBuilder();
		sb.append(INSERT_IGNORE).append(" ").append(tableName).append(fields)
				.append(" VALUES (").append(values).append(");");
		return sb.toString();
	}

	@Override
	public String getInsertOrIgnoreStatement(String tableName,
			String selectQuery) {
		StringBuilder sb = new StringBuilder();
		sb.append(INSERT_IGNORE).append(' ').append(tableName).append(' ')
				.append(selectQuery);
		return sb.toString();
	}
}
