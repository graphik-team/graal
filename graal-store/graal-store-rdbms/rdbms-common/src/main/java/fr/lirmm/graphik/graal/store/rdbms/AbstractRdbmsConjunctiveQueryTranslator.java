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
package fr.lirmm.graphik.graal.store.rdbms;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Literal;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.factory.TermFactory;
import fr.lirmm.graphik.graal.core.Substitutions;
import fr.lirmm.graphik.graal.core.factory.DefaultConjunctiveQueryFactory;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.store.rdbms.util.DBColumn;
import fr.lirmm.graphik.graal.store.rdbms.util.DBTable;
import fr.lirmm.graphik.graal.store.rdbms.util.SQLQuery;
import fr.lirmm.graphik.util.URI;
import fr.lirmm.graphik.util.URIUtils;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.IteratorException;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public abstract class AbstractRdbmsConjunctiveQueryTranslator implements RdbmsConjunctiveQueryTranslator {

	public static final String PREFIX_TERM_FIELD = "TERM";

	private AbstractRdbmsStore store;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	public AbstractRdbmsConjunctiveQueryTranslator(AbstractRdbmsStore store) {
		this.store = store;
	}
	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public SQLQuery translateRemove(Atom atom) throws AtomSetException {
		DBTable table = this.store.getPredicateTableIfExist(atom.getPredicate());
		if (table == null)
			return SQLQuery.hasSchemaErrorInstance();

		StringBuilder query = new StringBuilder("DELETE FROM ");
		query.append(table);
		query.append(" WHERE ");

		List<DBColumn> columns = table.getColumns();
		int termIndex = 0;
		for (Term t : atom.getTerms()) {
			if (termIndex != 0) {
				query.append(" and ");
			}
			query.append(columns.get(termIndex).getName()).append(" = '")
			     .append(t.getLabel()).append("'");
			++termIndex;
		}
		return new SQLQuery(query.toString());
	}

	@Override
	public Iterator<SQLQuery> translate(Rule rangeRestrictedRule) throws AtomSetException {
		Collection<SQLQuery> queries = new LinkedList<SQLQuery>();
		InMemoryAtomSet body = rangeRestrictedRule.getBody();
		CloseableIterator<Atom> it = rangeRestrictedRule.getHead().iterator();
		try {
			while (it.hasNext()) {
				Atom headAtom = it.next();
				DBTable table = this.store.createPredicateTableIfNotExist(headAtom.getPredicate());
				List<Term> terms = headAtom.getTerms();
				ConjunctiveQuery query = DefaultConjunctiveQueryFactory.instance().create(body, terms);
				SQLQuery selectQuery = this.translate(query, Substitutions.emptySubstitution());
				if (!selectQuery.hasSchemaError()) {
					queries.add(new SQLQuery(store.getDriver().getInsertOrIgnoreQuery(table, selectQuery.toString())));
				}
			}
		} catch (IteratorException e) {
			throw new AtomSetException(e);
		} catch (SQLException e) {
			throw new AtomSetException(e);
		}
		return queries.iterator();
	}

	@Override
	public String translateCreateTable(DBTable table) {
		StringBuilder primaryKey = new StringBuilder("PRIMARY KEY (");
		StringBuilder query = new StringBuilder("CREATE TABLE ");
		query.append(table.getName());
		query.append(" (");

		boolean first = true;
		for (DBColumn col : table.getColumns()) {
			if (!first) {
				query.append(", ");
				primaryKey.append(", ");
			}
			query.append(col.getName()).append(" varchar(" + AbstractRdbmsStore.VARCHAR_SIZE + ")");
			primaryKey.append(col.getName());
			first = false;
		}
		primaryKey.append(")");

		query.append(',');
		query.append(primaryKey);
		query.append(");");
		return query.toString();
	}

	@Override
	public String formatFromColumnType(DBColumn col, Term term) throws AtomSetException {
		switch (col.getType()) {
			case Types.BOOLEAN:
			case Types.INTEGER:
			case Types.BIGINT:
			case Types.SMALLINT:
			case Types.TINYINT:
			case Types.DECIMAL:
			case Types.DOUBLE:
				if (term instanceof Literal) {
					Literal l = (Literal) term;
					URI datatype = l.getDatatype();
					switch (col.getType()) {
						case Types.BOOLEAN:
							if (URIUtils.XSD_BOOLEAN.equals(l.getDatatype()) || l.getValue() instanceof Boolean) {
								return term.getLabel();
							}
							throw new AtomSetException("No correspondance between the database field (Boolean) and the Term type ("
							                           + datatype.toString()
							                           + ")");
						case Types.INTEGER:
						case Types.BIGINT:
						case Types.SMALLINT:
						case Types.TINYINT:
							if (URIUtils.XSD_INTEGER.equals(l.getDatatype())
							    || l.getValue() instanceof Integer
							    || l.getValue() instanceof BigInteger) {
								return term.getLabel();
							}
							throw new AtomSetException("No correspondance between the database field (Boolean) and the Term type ("
							                           + datatype.toString()
							                           + ")");
						case Types.DECIMAL:
							if (URIUtils.XSD_DECIMAL.equals(l.getDatatype())
							    || l.getValue() instanceof Float
							    || l.getValue() instanceof BigDecimal) {
								return term.getLabel();
							}
							throw new AtomSetException("No correspondance between the database field (Boolean) and the Term type ("
							                           + datatype.toString()
							                           + ")");
						case Types.DOUBLE:
							if (URIUtils.XSD_DOUBLE.equals(l.getDatatype()) || l.getValue() instanceof Double) {
								return term.getLabel();
							}
							throw new AtomSetException("No correspondance between the database field (Boolean) and the Term type ("
							                           + datatype.toString()
							                           + ")");
					}
				} else {
					throw new AtomSetException("No correspondance between the database field (Boolean) and the Term type.");
				}
			case Types.VARCHAR:
			case Types.NVARCHAR:
			case Types.LONGVARCHAR:
			case Types.LONGNVARCHAR:
				return '\'' + term.getIdentifier().toString() + '\'';
			default:
				throw new AtomSetException("Column type not supported (see java.sql.Types): " + col.getType());
		}
	}

	@Override
	public Term createTermFromColumnType(int sqlType, String value) throws AtomSetException {

		TermFactory factory = DefaultTermFactory.instance();

		switch (sqlType) {
			case Types.BOOLEAN:
				return factory.createLiteral(URIUtils.XSD_BOOLEAN, value);
			case Types.INTEGER:
			case Types.BIGINT:
			case Types.SMALLINT:
			case Types.TINYINT:
				return factory.createLiteral(URIUtils.XSD_INTEGER, value);
			case Types.DECIMAL:
				return factory.createLiteral(URIUtils.XSD_DECIMAL, value);
			case Types.DOUBLE:
				return factory.createLiteral(URIUtils.XSD_INTEGER, value);
			case Types.VARCHAR:
			case Types.NVARCHAR:
			case Types.LONGVARCHAR:
			case Types.LONGNVARCHAR:
				Matcher m = URIUtils.LITERAL_PATTERN.matcher((String) value);
				if (m.matches()) {
					return DefaultTermFactory.instance().createLiteral(value);
				} else {
					return factory.createConstant(value);
				}
			default:
				throw new AtomSetException("No correspondance for this column type (see java.sql.Types): " + sqlType);

		}
	}

	// /////////////////////////////////////////////////////////////////////////
	// OBJECT OVERRIDE METHODS
	// /////////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

	protected AbstractRdbmsStore getStore() {
		return this.store;
	}

}
