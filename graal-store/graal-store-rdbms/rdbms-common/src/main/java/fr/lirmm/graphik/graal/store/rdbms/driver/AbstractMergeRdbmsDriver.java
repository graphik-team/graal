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

import fr.lirmm.graphik.graal.store.rdbms.util.DBColumn;
import fr.lirmm.graphik.graal.store.rdbms.util.DBTable;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public abstract class AbstractMergeRdbmsDriver extends AbstractRdbmsDriver {

	private static final String MERGE_FORMAT = "MERGE INTO %s as t USING (%s) AS %s  ON %s \n"
	                                           + "WHEN NOT MATCHED THEN \n"
	                                           + "   INSERT VALUES %s;";

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	public AbstractMergeRdbmsDriver(Connection connection) throws SQLException {
		super(connection);
	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public String getInsertOrIgnoreQuery(DBTable table, Map<String, String> data) {
		StringBuilder values = new StringBuilder();
		StringBuilder as = new StringBuilder();
		StringBuilder using = new StringBuilder();
		StringBuilder on = new StringBuilder();
		StringBuilder insert = new StringBuilder();

		int i = 0;
		values.append("VALUES(");
		as.append("data(");
		using.append("VALUES(");
		for (Map.Entry<String, String> e : data.entrySet()) {
			if (i > 0) {
				values.append(", ");
				as.append(", ");
				using.append(", ");
				on.append(" AND ");
				insert.append(", ");
			}
			values.append(e.getKey());
			as.append("term").append(i);
			using.append(e.getValue());
			on.append("t.").append(e.getKey()).append(" = ").append(e.getValue());
			insert.append("data.term").append(i);
			++i;
		}
		values.append(") ");
		as.append(") ");
		using.append(") ");

		return String.format(MERGE_FORMAT, table.getName(), using, as, on, insert);
	}

	@Override
	public String getInsertOrIgnoreQuery(DBTable table, String selectQuery) throws SQLException {
		StringBuilder values = new StringBuilder();
		StringBuilder as = new StringBuilder();
		String using = selectQuery;
		StringBuilder on = new StringBuilder();
		StringBuilder insert = new StringBuilder();

		values.append("VALUES(");
		as.append("data(");
		int i = 0;
		for (DBColumn col : table.getColumns()) {
			if (i > 0) {
				values.append(", ");
				as.append(", ");
				on.append(" AND ");
				insert.append(", ");
			}
			values.append("term").append(i);
			as.append("term").append(i);
			on.append("t.").append(col.getName()).append(" = ").append("term").append(i);
			insert.append("data.term").append(i);
			++i;
		}
		values.append(") ");
		as.append(") ");

		return String.format(MERGE_FORMAT, table.getName(), using, as, on, insert);
	}

	// /////////////////////////////////////////////////////////////////////////
	// OBJECT OVERRIDE METHODS
	// /////////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

}
