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
	public String createInsertOrIgnoreStatement(String tableName, Map<String, Object> data) {
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
		sb.append(INSERT_IGNORE).append(" ").append(tableName).append(fields).append(" VALUES (").append(values)
		        .append(");");
		return sb.toString();
	}

	@Override
	public String getInsertOrIgnoreStatement(String tableName, String selectQuery) {
		StringBuilder sb = new StringBuilder();
		sb.append(INSERT_IGNORE).append(' ').append(tableName).append(' ').append(selectQuery);
		return sb.toString();
	}
}
