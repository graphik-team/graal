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
package fr.lirmm.graphik.graal.store.rdbms.adhoc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
import fr.lirmm.graphik.graal.store.rdbms.util.DBTable;
import fr.lirmm.graphik.graal.store.rdbms.util.SQLQuery;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.IteratorException;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
class AdHocConjunctiveQueryTranslator extends AbstractRdbmsConjunctiveQueryTranslator {

	private static final Logger LOGGER = LoggerFactory.getLogger(AdHocConjunctiveQueryTranslator.class);
	private AdHocRdbmsStore store;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	public AdHocConjunctiveQueryTranslator(AdHocRdbmsStore store) {
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

		HashMap<Atom, String> tableNames = new HashMap<Atom, String>();
		HashMap<String, String> lastOccurrence = new HashMap<String, String>();

		ArrayList<String> constants = new ArrayList<String>();
		ArrayList<String> equivalences = new ArrayList<String>();
		TreeMap<Term, String> columns = new TreeMap<Term, String>();

		int count = -1;
		CloseableIterator<Atom> it = atomSet.iterator();
		try {
			while (it.hasNext()) {
				Atom atom = it.next();
				String tableName = "atom" + ++count;
				tableNames.put(atom, tableName);
			}

			// Create WHERE clause
			it = atomSet.iterator();
			while (it.hasNext()) {
				Atom atom = it.next();
				String currentAtom = tableNames.get(atom) + ".";

				int position = 0;
				for (Term term : atom.getTerms()) {
					String thisTerm = currentAtom
					                  + AbstractRdbmsConjunctiveQueryTranslator.PREFIX_TERM_FIELD
					                  + position;
					if (term.isConstant() || s.getTerms().contains(term)) {
						constants.add(thisTerm + " = '" + s.createImageOf(term).getIdentifier().toString() + "'");
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
		} catch (IteratorException e) {
			throw new AtomSetException(e);
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
		DBTable table = null;
		for (Map.Entry<Atom, String> entries : tableNames.entrySet()) {
			if (tables.length() != 0)
				tables.append(", ");

			table = store.getPredicateTableIfExist(entries.getKey().getPredicate());
			if (table == null)
				return SQLQuery.hasSchemaErrorInstance();
			else
				tables.append(table.getName());

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
		else
			query.append(AdHocRdbmsStore.TEST_TABLE_NAME);

		if (where.length() > 0)
			query.append(" WHERE ").append(where);


		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Generated SQL query :" + cquery + " --> " + query.toString());

		return new SQLQuery(query.toString());
	}

	@Override
	public SQLQuery translateContainsQuery(Atom atom) throws AtomSetException {
		DBTable table = this.store.getPredicateTable(atom.getPredicate());
		if (table == null) {
			return SQLQuery.hasSchemaErrorInstance();
		}
		Term term;
		int termIndex = -1;
		StringBuilder query = new StringBuilder("SELECT 1 FROM ");
		query.append(table.getName());
		query.append(" WHERE ");

		Iterator<Term> terms = atom.getTerms().iterator();

		term = terms.next(); // TODO: FIX THIS => if arity = 0 -> crash ?!
		++termIndex;
		query.append("term").append(termIndex).append(" = \'").append(term.getIdentifier()).append('\'');

		while (terms.hasNext()) {
			term = terms.next();
			++termIndex;
			query.append(" AND ").append(AbstractRdbmsConjunctiveQueryTranslator.PREFIX_TERM_FIELD).append(termIndex)
			     .append(" = \'")
			     .append(term.getIdentifier()).append('\'');
		}
		query.append(" LIMIT 1;");
		return new SQLQuery(query.toString());
	}

	private static final String TERMS_BY_PREDICATE_POSITION_FORMAT = "SELECT DISTINCT p.term%s, t.term_type \n"
	                                                                 + " FROM %s AS p \n"
	                                                                 + " LEFT JOIN terms AS t ON p.term%s = t.term;";

	@Override
	public SQLQuery translateTermsByPredicatePositionQuery(Predicate p, int position) throws AtomSetException {
		DBTable table = this.store.getPredicateTable(p);
		if (table == null) {
			return SQLQuery.hasSchemaErrorInstance();
		}
		String query = String.format(TERMS_BY_PREDICATE_POSITION_FORMAT, position, table.getName(), position);
		return new SQLQuery(query);
	}

	// /////////////////////////////////////////////////////////////////////////
	// OBJECT OVERRIDE METHODS
	// /////////////////////////////////////////////////////////////////////////
}
