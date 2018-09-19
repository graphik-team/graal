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
package fr.lirmm.graphik.graal.store.rdbms.natural;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.store.rdbms.AbstractRdbmsConjunctiveQueryTranslator;
import fr.lirmm.graphik.graal.store.rdbms.util.DBColumn;
import fr.lirmm.graphik.graal.store.rdbms.util.DBTable;
import fr.lirmm.graphik.graal.store.rdbms.util.SQLQuery;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.IteratorException;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
class NaturalConjunctiveQueryTranslator extends AbstractRdbmsConjunctiveQueryTranslator {

	private static final Logger LOGGER = LoggerFactory.getLogger(NaturalConjunctiveQueryTranslator.class);
	private NaturalRDBMSStore store;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	public NaturalConjunctiveQueryTranslator(NaturalRDBMSStore store) {
		super(store);
		this.store = store;
	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public SQLQuery translate(ConjunctiveQuery cquery, Substitution s) throws AtomSetException {
		if (cquery.getAtomSet().isEmpty()) {
			return SQLQuery.emptyInstance();
		}
		AtomSet atomSet = cquery.getAtomSet();

		StringBuilder fields = new StringBuilder();
		StringBuilder tables = new StringBuilder();
		StringBuilder where = new StringBuilder();

		HashMap<Atom, String> tableAsNames = new HashMap<Atom, String>();
		HashMap<Atom, String> tableNames = new HashMap<Atom, String>();
		HashMap<Atom, List<DBColumn>> tableColumns = new HashMap<Atom, List<DBColumn>>();
		HashMap<String, String> lastOccurrence = new HashMap<String, String>();

		ArrayList<String> constants = new ArrayList<String>();
		ArrayList<String> equivalences = new ArrayList<String>();
		TreeMap<Term, String> columns = new TreeMap<Term, String>();

		int count = -1;
		CloseableIterator<Atom> it = atomSet.iterator();
		try {
			while (it.hasNext()) {
				Atom atom = it.next();
				DBTable table = this.store.getPredicateTableIfExist(atom.getPredicate());
				if (table != null) {
					String tableName = "atom" + ++count;

					tableAsNames.put(atom, tableName);
					tableNames.put(atom, table.getName());
					tableColumns.put(atom, table.getColumns());
				} else {
					return SQLQuery.hasSchemaErrorInstance();
				}
			}

			// Create WHERE clause
			it = atomSet.iterator();
			while (it.hasNext()) {
				Atom atom = it.next();
				String currentAtom = tableAsNames.get(atom) + ".";
				List<DBColumn> columnsInfo = tableColumns.get(atom);
				int position = 0;
				for (Term term : atom.getTerms()) {
					DBColumn column = columnsInfo.get(position);
					String thisTerm = currentAtom + column.getName();
					if (term.isConstant() || s.getTerms().contains(term)) {
						constants.add(thisTerm + " = " + this.formatFromColumnType(column, term));
					} else {
						if (lastOccurrence.containsKey(term.getIdentifier().toString())) {
							equivalences.add(lastOccurrence.get(term.getIdentifier().toString()) + " = " + thisTerm);
						}
						lastOccurrence.put(term.getIdentifier().toString(), thisTerm);
						if (cquery.getAnswerVariables().contains(term))
							columns.put(term, thisTerm + " as " + term.getIdentifier().toString());
					}
					++position;
				}
			}

			for (String equivalence : equivalences) {
				if (where.length() != 0)
					where.append(" AND ");

				where.append(equivalence);
			}

			for (String constant : constants) {
				if (where.length() != 0)
					where.append(" AND ");

				where.append(constant);
			}

			// Create FROM clause
			for (Map.Entry<Atom, String> entries : tableAsNames.entrySet()) {
				if (tables.length() != 0)
					tables.append(", ");

				tables.append(tableNames.get(entries.getKey()));
				tables.append(" as ");
				tables.append(entries.getValue());
			}

			// Create SELECT clause
			for (Term t : cquery.getAnswerVariables()) {
				if (fields.length() != 0)
					fields.append(", ");

				if (t.isConstant()) {
					fields.append("'");
					fields.append(t.getIdentifier());
					fields.append("'");
				} else {
					fields.append(columns.get(t));
				}
			}

			StringBuilder query = new StringBuilder("SELECT DISTINCT ");
			if (fields.length() > 0)
				query.append(fields);
			else
				query.append("1");

			query.append(" FROM ");
			if (tables.length() > 0)
				query.append(tables);
			/*
			 * else query.append(TEST_TABLE_NAME);
			 */

			if (where.length() > 0)
				query.append(" WHERE ").append(where);


			if (LOGGER.isDebugEnabled())
				LOGGER.debug("Generated SQL query :" + cquery + " --> " + query.toString());
			return new SQLQuery(query.toString());
		} catch (IteratorException e) {
			throw new AtomSetException(e);
		}
	}

	private static final String TERM_BY_PREDICATE_POSTION_FORMAT = "SELECT DISTINCT p.%s FROM %s AS p;";

	@Override
	public SQLQuery translateTermsByPredicatePositionQuery(Predicate p, int position) throws AtomSetException {
		DBTable table = this.store.getPredicateTable(p);
		if (table == null) {
			return SQLQuery.hasSchemaErrorInstance();
		}
		DBColumn col = table.getColumns().get(position);
		String query = String.format(TERM_BY_PREDICATE_POSTION_FORMAT, col.getName(), table.getName());
		return new SQLQuery(query);
	}

	@Override
	public SQLQuery translateContainsQuery(Atom atom) throws AtomSetException {
		DBTable table = this.store.getPredicateTable(atom.getPredicate());
		if (table == null) {
			return SQLQuery.hasSchemaErrorInstance();
		}
		List<DBColumn> columns = table.getColumns();

		StringBuilder query = new StringBuilder("SELECT 1 FROM ");
		query.append(table.getName());
		query.append(" WHERE ");

		int i = 0;
		for (Term t : atom) {
			if (i != 0) {
				query.append(" AND ");
			}
			DBColumn col = columns.get(i);
			query.append(col.getName());
			query.append(" = ");
			query.append(this.formatFromColumnType(col, t));
			++i;
		}
		query.append(';');
		return new SQLQuery(query.toString());
	}

	// /////////////////////////////////////////////////////////////////////////
	// OBJECT OVERRIDE METHODS
	// /////////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

}
