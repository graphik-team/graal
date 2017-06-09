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
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.store.rdbms.util.DBTable;
import fr.lirmm.graphik.util.MethodNotImplementedError;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class PostgreSQLDriver extends AbstractRdbmsDriver {

	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(PostgreSQLDriver.class);
	
	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////
	
	/**
	 * 
	 * @param host
	 * @param dbName
	 * @param user
	 * @param password
	 */
	public PostgreSQLDriver(String host, String dbName, String user,
			String password)
	    throws SQLException {
		super(openConnection(host, dbName, user, password, false));
	}
	
	public PostgreSQLDriver(String host, String dbName, String user,
			String password, boolean create)  throws SQLException {
		super(openConnection(host, dbName, user, password, create));
	}

	public PostgreSQLDriver(String uri) throws SQLException {
		super(openConnection(uri));
	}

	private static Connection openConnection(String host, String dbName, String user,
	    String password, boolean create) throws SQLException {
		if (create) {
			Connection con = null;
			Statement stmt = null;
			try {
				con = openConnection("jdbc:postgresql://" + host
						+ "?user=" + user + "&password=" + password);
				stmt = con.createStatement();
				try{
				    stmt.executeUpdate("CREATE DATABASE " + dbName);
				}catch(SQLException e){
			    }
			}finally{
		      try{
		         if(stmt!=null)
		            stmt.close();
		      }catch(SQLException e){
		      }
		      try{
		         if(con!=null)
		            con.close();
		      }catch(SQLException e){
		         e.printStackTrace();
		      }
			}
		}
		return openConnection("jdbc:postgresql://" + host
				+ "/" + dbName + "?user=" + user + "&password=" + password);
	}
	
	private static Connection openConnection(String uri) throws SQLException {
		Connection connection;
		try {
			Class.forName("org.postgresql.Driver").newInstance();
		} catch (InstantiationException e) {
			LOGGER.error(e.getMessage(), e);
			throw new SQLException(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			LOGGER.error(e.getMessage(), e);
			throw new SQLException(e.getMessage(), e);
		} catch (ClassNotFoundException e) {
			LOGGER.error(e.getMessage(), e);
			throw new SQLException(e.getMessage(), e);
		}
		
		connection = DriverManager.getConnection(uri);
		return connection;
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	@Override
	public String getInsertOrIgnoreQuery(DBTable tableName,
	    Map<String, String> data) {
		StringBuilder fields = new StringBuilder("(");
		StringBuilder values = new StringBuilder("");

		boolean first = true;
		for (Map.Entry<String, String> e : data.entrySet()) {
			if (!first) {
				fields.append(", ");
				values.append(", ");
			}
			fields.append(e.getKey());
			values.append(e.getValue());
			first = false;
		}
		fields.append(") ");

		StringBuilder query = new StringBuilder();
		query.append("INSERT INTO ");
		query.append(tableName);
		query.append(fields);
		query.append(" SELECT ");
		query.append(values);
		
		// Where not exist
		query.append(" FROM (SELECT 0) AS t WHERE NOT EXISTS (SELECT 1 FROM ");
		query.append(tableName);
		query.append(" WHERE ");
		
		int i = 0;
		for (Map.Entry<String, String> e : data.entrySet()) {
			if (++i > 1) {
				query.append(" and ");
			}
			query.append(e.getKey()).append(" = ");
			query.append(e.getValue());
		}
		query.append("); ");
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(query.toString());
		}
		return query.toString();
	}

	@Override
	public String getInsertOrIgnoreQuery(DBTable tableName, String selectQuery) throws SQLException {
		// TODO implement this method
		throw new MethodNotImplementedError();
	}

}
