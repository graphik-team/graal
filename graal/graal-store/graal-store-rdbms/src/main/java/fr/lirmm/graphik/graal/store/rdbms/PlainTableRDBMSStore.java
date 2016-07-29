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
package fr.lirmm.graphik.graal.store.rdbms;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.ConstantGenerator;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Term.Type;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.core.DefaultConstantGenerator;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.factory.ConjunctiveQueryFactory;
import fr.lirmm.graphik.graal.core.factory.DefaultAtomFactory;
import fr.lirmm.graphik.graal.core.factory.DefaultPredicateFactory;
import fr.lirmm.graphik.graal.core.stream.SubstitutionIterator2AtomIterator;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.store.rdbms.driver.HSQLDBDriver;
import fr.lirmm.graphik.graal.store.rdbms.driver.RdbmsDriver;
import fr.lirmm.graphik.util.MethodNotImplementedError;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.CloseableIteratorAdapter;
import fr.lirmm.graphik.util.stream.IteratorException;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class PlainTableRDBMSStore extends AbstractRdbmsStore {

	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(PlainTableRDBMSStore.class);
	private final ConstantGenerator gen = new DefaultConstantGenerator("EE");
	
	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////
	
	/**
	 * 
	 * @param driver
	 * @throws SQLException
	 * @throws AtomSetException
	 */
	public PlainTableRDBMSStore(RdbmsDriver driver) throws AtomSetException {
		super(driver);
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	@Override
	public boolean contains(Atom atom) throws AtomSetException {
		Statement statement = this.createStatement();
		try {
			StringBuilder query = new StringBuilder("SELECT 1 FROM ");
			query.append(this.getPredicateTableName(atom.getPredicate()));
			query.append(" WHERE ");
			int termNumber = 0;
			for(Term t : atom) {
				query.append("term");
				query.append(termNumber);
				query.append(" = ");
				query.append('\'');
				query.append(t);
				query.append("' ");
			}
			query.append(';');
			return statement.execute(query.toString());
		} catch(SQLException e) {
			throw new AtomSetException("Error during check contains atom: " + atom, e);
		}
	}
	
	@Override
	public CloseableIterator<Atom> match(Atom atom) throws AtomSetException {
		ConjunctiveQuery query = ConjunctiveQueryFactory.instance().create(new LinkedListAtomSet(atom));
		SqlHomomorphism solver = SqlHomomorphism.instance();

		try {
			return new SubstitutionIterator2AtomIterator(atom, solver.execute(query, this));
		} catch (HomomorphismException e) {
			throw new AtomSetException(e);
		}
	}

	@Override
	public Term getTerm(String label) throws AtomSetException {
		return DefaultTermFactory.instance().createConstant(label);
	}

	@Override
	public ConstantGenerator getFreshSymbolGenerator() {
		return gen;
	}

	@Override
	public CloseableIterator<Atom> atomsByPredicate(Predicate p) throws AtomSetException {
		List<Term> terms = new LinkedList<Term>();
		for (int i = 0; i < p.getArity(); ++i) {
			terms.add(DefaultTermFactory.instance().createVariable(i));
		}
		Atom atom = DefaultAtomFactory.instance().create(p, terms);
		ConjunctiveQuery query = ConjunctiveQueryFactory.instance().create(new LinkedListAtomSet(atom));
		SqlHomomorphism solver = SqlHomomorphism.instance();

		try {
			return new SubstitutionIterator2AtomIterator(atom, solver.execute(query, this));
		} catch (HomomorphismException e) {
			throw new AtomSetException(e);
		}
	}

	@Override
	public CloseableIterator<Term> termsByPredicatePosition(Predicate p, int position) throws AtomSetException {
		try {
			String tableName = this.getPredicateTableName(p);
			ArrayList columns = this.getColumnsName(tableName);
			return new ResultSetTermIterator(this,
			                                 "SELECT DISTINCT p."
			                                       + columns.get(position)
			                                       + ", t.term_type FROM "
			                                       + tableName
			                                       + " AS p LEFT JOIN terms AS t ON p."
			                                       + columns.get(position)
			                                       + " = t.term;");
		} catch (SQLException e) {
			throw new AtomSetException(e);
		}
	}

	@Override
	protected Statement add(Statement statement, Atom atom)
			throws AtomSetException {
		try {
			String tableName = this.getPredicateTable(atom.getPredicate());
			Map<String, Object> data = new TreeMap<String, Object>();
			int i = -1;
			for (Term t : atom.getTerms()) {
				++i;
				data.put("term" + i, t);
			}
			String query = this.getDriver().getInsertOrIgnoreStatement(
					tableName, data);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(atom.toString() + " : " + query);
			}
			statement.addBatch(query);
		} catch (SQLException e) {
			throw new AtomSetException(e.getMessage(), e);
		}
		return statement;
	}

	/**
	 * @param predicate
	 * @throws AtomSetException 
	 */
	private void checkPredicateTable(Predicate predicate) throws AtomSetException {
		StringBuilder query = new StringBuilder("CREATE TABLE ");
		query.append(this.getPredicateTableName(predicate));
		query.append(" (");
		for(int i=0; i<predicate.getArity(); ++i) {
			if(i > 0) {
				query.append(", ");
			}
			query.append("term");
			query.append(i);
			query.append(" VARCHAR(256)");
		}
		query.append(");");
		try {
			this.createStatement().execute(query.toString());
		} catch (SQLException e) {
		}
	}

	@Override
	protected Statement remove(Statement statement, Atom atom)
			throws AtomSetException {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	protected boolean testDatabaseSchema() throws AtomSetException {
		return true;
	}

	@Override
	protected void createDatabaseSchema() throws AtomSetException {
		// no specific schema expected
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<String> transformToSQL(Rule rangeRestrictedRule)
			throws AtomSetException {
		// TODO implement this method
		throw new MethodNotImplementedError();
	}

	@Override
	public CloseableIterator<Predicate> predicatesIterator() throws AtomSetException {
		List<Predicate> res = new LinkedList<Predicate>();
		try {
			DatabaseMetaData metaData = this.getConnection().getMetaData();
			ResultSet tables = metaData.getTables(null, null, null, null);
			while (tables.next()) {
				int arity = 0;
				String tableName = tables.getString("TABLE_NAME");
				ResultSet columns = metaData.getColumns(null, null, tableName, null);
				while (columns.next()) {
					++arity;
				}
				res.add(DefaultPredicateFactory.instance().create(tableName, arity));
			}
			return new CloseableIteratorAdapter<Predicate>(res.iterator());
		} catch (SQLException e) {
			throw new AtomSetException("Error during querying for table names", e);
		}
	}

	@Override
	public CloseableIterator<Term> termsIterator() throws AtomSetException {
		throw new UnsupportedOperationException();
	}

	@Override
	public CloseableIterator<Term> termsIterator(Type type) throws AtomSetException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Transforms the fact into a SQL statement.
	 */
	@Override
	public String transformToSQL(ConjunctiveQuery cquery) throws AtomSetException {
		AtomSet atomSet = cquery.getAtomSet();

		StringBuilder fields = new StringBuilder();
		StringBuilder tables = new StringBuilder();
		StringBuilder where = new StringBuilder();

		HashMap<Atom, String> tableNames = new HashMap<Atom, String>();
		HashMap<Atom, ArrayList<String>> tableColumnsName = new HashMap<Atom, ArrayList<String>>();
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
				try {
					tableColumnsName.put(atom, this.getColumnsName(this.predicateTableExist(atom.getPredicate())));
				} catch (SQLException e) {
					throw new AtomSetException("Error during retriving columns name", e);
				}
			}

			// Create WHERE clause
			it = atomSet.iterator();
			while (it.hasNext()) {
				Atom atom = it.next();
				String currentAtom = tableNames.get(atom) + ".";
				ArrayList<String> columnsName = tableColumnsName.get(atom);
				int position = 0;
				for (Term term : atom.getTerms()) {
					String thisTerm = currentAtom + columnsName.get(position);
					if (term.isConstant()) {
						constants.add(thisTerm + " = '" + term + "'");
					} else {
						if (lastOccurrence.containsKey(term.toString())) {
							equivalences.add(lastOccurrence.get(term.toString()) + " = " + thisTerm);
						}
						lastOccurrence.put(term.toString(), thisTerm);
						if (cquery.getAnswerVariables().contains(term))
							columns.put(term, thisTerm + " as " + term);
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
			String tableName = null;
			for (Map.Entry<Atom, String> entries : tableNames.entrySet()) {
				if (tables.length() != 0)
					tables.append(", ");

				tableName = this.predicateTableExist(entries.getKey().getPredicate());
				if (tableName == null)
					return null;// this.createEmptyQuery(cquery.getAnswerVariables());
				else
					tables.append(tableName);

				tables.append(" as ");
				tables.append(entries.getValue());
			}

			// Create SELECT clause
			for (Term t : cquery.getAnswerVariables()) {
				if (fields.length() != 0)
					fields.append(", ");

				fields.append(columns.get(t));
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

			query.append(';');

			if (LOGGER.isDebugEnabled())
				LOGGER.debug("Generated SQL query :" + cquery + " --> " + query.toString());
			System.err.println(query.toString());
			return query.toString();
		} catch (IteratorException e) {
			throw new AtomSetException(e);
		}
	}

	@Override
	protected String predicateTableExist(Predicate predicate) throws AtomSetException {
		try {
			String query = "select * from " + predicate.getIdentifier() + " LIMIT 1;";
			ResultSet res = this.getConnection().createStatement().executeQuery(query);
			res.close();
			return predicate.getIdentifier().toString();
		} catch (SQLException e) {
			return null;
		}
	}

	@Override
	protected String getFreshPredicateTableName(Predicate predicate) throws AtomSetException {
		return predicate.getIdentifier().toString();
	}

	private ArrayList<String> getColumnsName(String tableName) throws SQLException {
		ArrayList<String> array = new ArrayList<String>();
		DatabaseMetaData metaData = this.getConnection().getMetaData();
		ResultSet res = metaData.getColumns(null, null, tableName, null);
		while (res.next())
			array.add(res.getString("COLUMN_NAME"));

		return array;
	}

	public static void main(String args[]) throws AtomSetException, SQLException, IteratorException {

		PlainTableRDBMSStore store = new PlainTableRDBMSStore(new HSQLDBDriver("test", null));
		Statement st = store.createStatement();
		st.execute("CREATE table test (first varchar(128), second varchar(128));");
		System.out.println(st.executeUpdate("INSERT INTO test VALUES ('a', 'b');"));
		System.out.println(st.executeUpdate("INSERT INTO test VALUES ('a', 'b');"));
		// store.add(DlgpParser.parseAtom("s(a,b)."));
		CloseableIterator<Atom> it = store.iterator();
		while (it.hasNext()) {
			Atom a = it.next();
			System.out.println(a);
		}
		store.close();
	}

}
