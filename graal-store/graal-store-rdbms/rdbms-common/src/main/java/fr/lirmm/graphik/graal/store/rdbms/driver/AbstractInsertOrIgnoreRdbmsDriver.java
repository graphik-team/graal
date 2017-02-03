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
package fr.lirmm.graphik.graal.store.rdbms.driver;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import fr.lirmm.graphik.graal.store.rdbms.util.DBTable;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public abstract class AbstractInsertOrIgnoreRdbmsDriver extends AbstractRdbmsDriver {

	protected final String INSERT_IGNORE;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	public AbstractInsertOrIgnoreRdbmsDriver(Connection connection, String insertORignore) throws SQLException {
		super(connection);
		this.INSERT_IGNORE = insertORignore;
	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public String getInsertOrIgnoreQuery(DBTable table, Map<String, String> data) {
		StringBuilder fields = new StringBuilder(" (");
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

		StringBuilder sb = new StringBuilder();
		sb.append(INSERT_IGNORE).append(" ").append(table.getName()).append(fields).append(" VALUES (").append(values)
		  .append(");");
		return sb.toString();
	}

	@Override
	public String getInsertOrIgnoreQuery(DBTable table, String selectQuery) throws SQLException {
		StringBuilder sb = new StringBuilder();
		sb.append(INSERT_IGNORE).append(' ').append(table.getName()).append(' ').append(selectQuery);
		return sb.toString();
	}

	// /////////////////////////////////////////////////////////////////////////
	// OBJECT OVERRIDE METHODS
	// /////////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

}
