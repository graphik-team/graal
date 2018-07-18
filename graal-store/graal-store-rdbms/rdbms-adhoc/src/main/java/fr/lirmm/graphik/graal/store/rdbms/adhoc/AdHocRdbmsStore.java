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
package fr.lirmm.graphik.graal.store.rdbms.adhoc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Term.Type;
import fr.lirmm.graphik.graal.api.core.TermGenerator;
import fr.lirmm.graphik.graal.api.core.UnsupportedAtomTypeException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.api.store.BatchProcessor;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.factory.DefaultAtomFactory;
import fr.lirmm.graphik.graal.core.factory.DefaultConjunctiveQueryFactory;
import fr.lirmm.graphik.graal.core.stream.SubstitutionIterator2AtomIterator;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.store.rdbms.AbstractRdbmsStore;
import fr.lirmm.graphik.graal.store.rdbms.RdbmsConjunctiveQueryTranslator;
import fr.lirmm.graphik.graal.store.rdbms.RdbmsVariableGenenrator;
import fr.lirmm.graphik.graal.store.rdbms.driver.RdbmsDriver;
import fr.lirmm.graphik.graal.store.rdbms.homomorphism.SqlHomomorphism;
import fr.lirmm.graphik.graal.store.rdbms.util.DBColumn;
import fr.lirmm.graphik.graal.store.rdbms.util.DBTable;
import fr.lirmm.graphik.graal.store.rdbms.util.SQLQuery;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.IteratorException;
import fr.lirmm.graphik.util.stream.Iterators;
import fr.lirmm.graphik.util.string.StringUtils;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 *         This class represents an implementation of a store in a Relational
 *         Database System where each predicates is stored in a dedicated table.
 */
@SuppressWarnings("deprecation")
public class AdHocRdbmsStore extends AbstractRdbmsStore {
	private static final Logger        LOGGER                     = LoggerFactory.getLogger(AdHocRdbmsStore.class);


	private static final String        MAX_VARIABLE_ID_COUNTER    = "max_variable_id";
	private static final String        MAX_PREDICATE_ID_COUNTER   = "max_predicate_id";

	// tables names
	static final String                COUNTER_TABLE_NAME         = "counters";
	static final String                PREDICATE_TABLE_NAME       = "predicates";
	static final String                TERMS_TABLE_NAME            = "terms";
	static final DBTable TERMS_TABLE;
	static {
		List<DBColumn> columns = new ArrayList<DBColumn>();
		columns.add(new DBColumn("term", Types.VARCHAR));
		columns.add(new DBColumn("term_type", Types.VARCHAR));
		TERMS_TABLE = new DBTable(TERMS_TABLE_NAME, columns);
	}
	static final String EMPTY_TABLE_NAME = "empty";
	static final String TEST_TABLE_NAME = "test";

	// queries
	private static final String        GET_ALL_PREDICATES_QUERY   = "SELECT * FROM "
	                                                                + AdHocRdbmsStore.PREDICATE_TABLE_NAME
	                                                                + ";";

	private static final String        GET_PREDICATE_QUERY        = "SELECT * FROM "
	                                                                + PREDICATE_TABLE_NAME
	                                                                + " WHERE predicate_label = ? "
	                                                                + " AND predicate_arity = ?;";
	private static final String        INSERT_PREDICATE_QUERY     = "INSERT INTO "
	                                                                + PREDICATE_TABLE_NAME
	                                                                + " VALUES ( ?, ?, ?)";

	private static final String        GET_ALL_TERMS_QUERY        = "SELECT * FROM " + TERMS_TABLE_NAME + ";";

	private static final String        GET_TERMS_BY_TYPE          = "SELECT * FROM "
	                                                                + TERMS_TABLE_NAME
	                                                                + " WHERE term_type = ?;";

	private static final String        GET_TERM_QUERY             = "SELECT * FROM "
	                                                                + TERMS_TABLE_NAME
	                                                                + " WHERE term = ?;";

	//private static final String        GET_ATOMS_BY_PREDICATE     = "SELECT * FROM %s;";

	// counter queries
	private static final String        GET_COUNTER_VALUE_QUERY    = "SELECT value FROM "
	                                                                + COUNTER_TABLE_NAME
	                                                                + " WHERE counter_name = ?;";

	private static final String        UPDATE_COUNTER_VALUE_QUERY = "UPDATE "
	                                                                + COUNTER_TABLE_NAME
	                                                                + " SET value = ? WHERE counter_name = ?;";

	private static final String        TEST_SCHEMA_QUERY          = "SELECT 0 FROM "
	                                                                + PREDICATE_TABLE_NAME
	                                                                + " LIMIT 1";

	private static final String TRUNCATE_TABLE = "TRUNCATE TABLE %s;";

	private PreparedStatement          getPredicateTableStatement;
	private PreparedStatement          insertPredicateStatement;

	private PreparedStatement          getTermStatement;

	private PreparedStatement          getCounterValueStatement;
	private PreparedStatement          updateCounterValueStatement;

	private PreparedStatement          getTermsByTypeStatement;

	private final RdbmsConjunctiveQueryTranslator QUERY_TRANSLATOR;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * 
	 * @param driver
	 * @throws AtomSetException
	 */
	public AdHocRdbmsStore(RdbmsDriver driver) throws AtomSetException {
		super(driver);

		QUERY_TRANSLATOR = new AdHocConjunctiveQueryTranslator(this);
		try {
			this.getPredicateTableStatement = this.getConnection().prepareStatement(GET_PREDICATE_QUERY);
			this.insertPredicateStatement = this.getConnection().prepareStatement(INSERT_PREDICATE_QUERY);
			this.getCounterValueStatement = this.getConnection().prepareStatement(GET_COUNTER_VALUE_QUERY);
			this.updateCounterValueStatement = this.getConnection().prepareStatement(UPDATE_COUNTER_VALUE_QUERY);
			this.getTermStatement = this.getConnection().prepareStatement(GET_TERM_QUERY);
			this.getTermsByTypeStatement = this.getConnection().prepareStatement(GET_TERMS_BY_TYPE);

		} catch (SQLException e) {
			throw new AtomSetException(e.getMessage(), e);
		}

	}

	@Override
	protected boolean testDatabaseSchema() throws AtomSetException {
		Statement statement = null;
		try {
			statement = this.createStatement();
			ResultSet rs = statement.executeQuery(TEST_SCHEMA_QUERY);
			rs.close();
		} catch (SQLException e) {
			return false;
		} catch (AtomSetException e) {
			throw new AtomSetException(e.getMessage(), e);
		} finally {
			if (statement != null) {
				try {
					statement.close();
					this.getConnection().rollback();
				} catch (SQLException e) {
					throw new AtomSetException(e);
				}
			}
		}

		return true;
	}

	@Override
	protected void createDatabaseSchema() throws AtomSetException {
		final String createPredicateTableQuery = "CREATE TABLE "
		                                         + PREDICATE_TABLE_NAME
		                                         + "(predicate_label varchar("
		                                         + VARCHAR_SIZE
		                                         + "), predicate_arity int, "
		                                         + "predicate_table_name varchar("
		                                         + VARCHAR_SIZE
		                                         + "), PRIMARY KEY (predicate_label, predicate_arity));";

		final String createTermTableQuery = "CREATE TABLE "
		                                    + TERMS_TABLE_NAME
		                                    + " (term varchar("
		                                    + VARCHAR_SIZE
		                                    + "), term_type varchar("
		                                    + VARCHAR_SIZE
		                                    + "), PRIMARY KEY (term));";

		final String termTypeTableName = "term_type";
		final String createTermTypeTableQuery = "CREATE TABLE "
		                                        + termTypeTableName
		                                        + " (term_type varchar("
		                                        + VARCHAR_SIZE
		                                        + "), PRIMARY KEY (term_type));";

		final String insertTermTypeQuery = "INSERT INTO " + termTypeTableName + " values (?);";
		Statement statement = null;
		PreparedStatement pstat = null;
		try {
			statement = this.createStatement();
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("Create database schema");

			statement.executeUpdate("create table " + TEST_TABLE_NAME + " (i int)");
			statement.executeUpdate("insert into " + TEST_TABLE_NAME + " values (1)");

			statement.executeUpdate("create table " + EMPTY_TABLE_NAME + " (i int)");

			if (LOGGER.isDebugEnabled())
				LOGGER.debug(createPredicateTableQuery);
			statement.executeUpdate(createPredicateTableQuery);

			if (LOGGER.isDebugEnabled())
				LOGGER.debug(createTermTypeTableQuery);
			statement.executeUpdate(createTermTypeTableQuery);

			pstat = this.getConnection().prepareStatement(insertTermTypeQuery);
			String[] types = {"V", "C", "L"};
			for (int i = 0; i < types.length; ++i) {
				pstat.setString(1, types[i].toString());
				pstat.addBatch();
			}
			pstat.executeBatch();
			pstat.close();

			if (LOGGER.isDebugEnabled())
				LOGGER.debug(createTermTableQuery);
			statement.executeUpdate(createTermTableQuery);

			final String createCounterTableQuery = "CREATE TABLE "
			                                       + COUNTER_TABLE_NAME
			                                       + " (counter_name varchar(64), value BIGINT, PRIMARY KEY (counter_name));";

			if (LOGGER.isDebugEnabled())
				LOGGER.debug(createCounterTableQuery);
			statement.executeUpdate(createCounterTableQuery);
		} catch (SQLException e) {
			throw new AtomSetException(e.getMessage(), e);
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					throw new AtomSetException(e);
				}
			}
		}

		try {
			final String insertCounterTableQuery = "INSERT INTO " + COUNTER_TABLE_NAME + " values (?, -1);";
			final String[] counters = { MAX_PREDICATE_ID_COUNTER, MAX_VARIABLE_ID_COUNTER };
			pstat = this.getConnection().prepareStatement(insertCounterTableQuery);
			for (int i = 0; i < counters.length; ++i) {
				pstat.setString(1, counters[i]);
				pstat.addBatch();
			}
			pstat.executeBatch();
			this.getConnection().commit();
		} catch (SQLException e) {
			throw new AtomSetException(e.getMessage(), e);
		} finally {
			if (pstat != null) {
				try {
					pstat.close();
				} catch (SQLException e) {
					throw new AtomSetException(e);
				}
			}
		}

	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public BatchProcessor createBatchProcessor() throws AtomSetException {
		return new AdHocRdbmsBatchProcessor(this, this.getConnection());
	}

	@Override
	public TermGenerator getFreshSymbolGenerator() {
		return new RdbmsVariableGenenrator(this.getConnection(), MAX_VARIABLE_ID_COUNTER, GET_COUNTER_VALUE_QUERY,
		                                 UPDATE_COUNTER_VALUE_QUERY);
	}

	@Override
	public CloseableIterator<Atom> match(Atom atom, Substitution s) throws AtomSetException {

		ConjunctiveQuery query = DefaultConjunctiveQueryFactory.instance().create(new LinkedListAtomSet(atom));
		SqlHomomorphism solver = SqlHomomorphism.instance();

		try {
			return new SubstitutionIterator2AtomIterator(atom, solver.execute(query, this, s));
		} catch (HomomorphismException e) {
			throw new AtomSetException(e);
		}
	}

	@Override
	public CloseableIterator<Atom> atomsByPredicate(Predicate p) throws AtomSetException {
		List<Term> terms = new LinkedList<Term>();
		for (int i = 0; i < p.getArity(); ++i) {
			terms.add(DefaultTermFactory.instance().createVariable("X" + i));
		}
		Atom atom = DefaultAtomFactory.instance().create(p, terms);
		ConjunctiveQuery query = DefaultConjunctiveQueryFactory.instance().create(new LinkedListAtomSet(atom));
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
			SQLQuery query = this.getConjunctiveQueryTranslator().translateTermsByPredicatePositionQuery(p, position);
			if (query.hasSchemaError()) {
				return Iterators.<Term> emptyIterator();
			} else {
				return new AdHocResultSetTermIterator(this, query.toString());
			}
		} catch (SQLException e) {
			throw new AtomSetException(e);
		}
	}

	@Override
	public Term getTerm(String label) throws AtomSetException {
		ResultSet results;
		Term term = null;

		try {
			this.getTermStatement.setString(1, label);
			results = this.getTermStatement.executeQuery();
			if (results.next()) {
				String type = results.getString(2);
				if("V".equals(type)) {
					term = DefaultTermFactory.instance().createVariable(results.getString(1));
				} else if ("L".equals(type)) {
					term = DefaultTermFactory.instance().createLiteral(results.getString(1));
				} else if ("C".equals(type)) {
					term = DefaultTermFactory.instance().createConstant(results.getString(1));
				} else {			
					throw new AtomSetException("Unrecognized type: " + type);
				}
			}
			results.close();
		} catch (SQLException e) {
			throw new AtomSetException(e);
		}
		return term;

	}

	@Override
	public CloseableIterator<Term> termsIterator() throws AtomSetException {
		try {
			return new AdHocResultSetTermIterator(this, GET_ALL_TERMS_QUERY);
		} catch (SQLException e) {
			throw new AtomSetException("SQLException: ", e);
		}
	}

	@Override
	@Deprecated
	public CloseableIterator<Term> termsIterator(Type type) throws AtomSetException {
		try {
			this.getTermsByTypeStatement.setString(1, type.toString());
			return new AdHocResultSetTermIterator(this.getTermsByTypeStatement);
		} catch (SQLException e) {
			throw new AtomSetException("SQLException: ", e);
		}
	}

	@Override
	public void clear() throws AtomSetException {
		try {
			Statement stat = null;
			CloseableIterator<Predicate> it = this.predicatesIterator();
			try {
				stat = this.createStatement();
				while (it.hasNext()) {
					Predicate p = it.next();
					this.removePredicate(stat, p);
				}
				stat.execute(String.format(TRUNCATE_TABLE, PREDICATE_TABLE_NAME));
				stat.execute(String.format(TRUNCATE_TABLE, TERMS_TABLE));
				stat.close();
				this.getConnection().commit();
			} catch (IteratorException e) {
				this.getConnection().rollback();
				throw new AtomSetException(e);
			} catch (SQLException e) {
				this.getConnection().rollback();
				throw new AtomSetException(e);
			} finally {
				if (stat != null)
					stat.close();
			}
		} catch (SQLException e) {
			throw new AtomSetException(e);
		}
	}


	// /////////////////////////////////////////////////////////////////////////
	// PROTECTED METHODS
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * @param statement
	 * @param atom
	 * @throws AtomSetException
	 */
	@Override
	protected Statement add(Statement statement, Atom atom) throws AtomSetException {
		if (!this.check(atom)) {
			throw new UnsupportedAtomTypeException(""); // FIXME say why
		}
		try {
			for (Term t : atom.getTerms()) {
				if (this.getTerm(t.getLabel()) == null) { // FIXME Quick fix for
				                                          // VARIABLE and
				                                          // CONSTANT with same
				                                          // label conflict
					this.add(statement, t);
				}
			}
			DBTable table = this.createPredicateTableIfNotExist(atom.getPredicate());
			Map<String, String> data = new TreeMap<String, String>();
			int i = -1;
			for (Term t : atom.getTerms()) {
				++i;
				data.put("term" + i, '\'' + StringUtils.addSlashes(t.getIdentifier().toString()) + '\'');
			}
			String query = this.getDriver().getInsertOrIgnoreQuery(table, data);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(atom.toString() + " : " + query.toString());
			}
			statement.addBatch(query);
		} catch (SQLException e) {
			throw new AtomSetException(e);
		}
		return statement;
	}

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

	private void add(Statement statement, Term term) throws AtomSetException {
		try {
			List<DBColumn> cols = TERMS_TABLE.getColumns();
			Map<String, String> data = new TreeMap<String, String>();
			data.put(cols.get(0).getName(), '\'' + StringUtils.addSlashes(term.getIdentifier().toString()) + '\'');
			data.put(cols.get(1).getName(), '\'' + getType(term) + '\'');
			String query = this.getDriver().getInsertOrIgnoreQuery(TERMS_TABLE, data);
			statement.executeUpdate(query);
		} catch (SQLException e) {
			throw new AtomSetException("Error during insertion of a term: " + term, e);
		}
	}

	@Override
	protected String getFreshPredicateTableName(Predicate predicate) throws AtomSetException {
		try {
			return this.getDriver().formatIdentifier("pred" + this.getFreePredicateId());
		} catch (SQLException e) {
			throw new AtomSetException("Error during generation of a new predicate name", e);
		}
	}

	@Override
	protected DBTable createPredicateTable(Predicate predicate) throws AtomSetException {
		DBTable tableName = super.createPredicateTable(predicate);
		try {
			this.insertPredicate(tableName.getName(), predicate);
		} catch (SQLException e) {
			throw new AtomSetException("Error during insertion of new predicate table", e);
		}
		return tableName;
	}

	/**
	 * 
	 * @param tableName
	 * @param predicate
	 * @throws SQLException
	 */
	private void insertPredicate(String tableName, Predicate predicate) throws SQLException {
		this.insertPredicateStatement.setString(1, predicate.getIdentifier().toString());
		this.insertPredicateStatement.setInt(2, predicate.getArity());
		this.insertPredicateStatement.setString(3, tableName);
		this.insertPredicateStatement.execute();
	}

	@Override
	protected DBTable getPredicateTableIfExist(Predicate predicate) throws AtomSetException {
		DBTable table = null;

		try {
			this.getPredicateTableStatement.setString(1, predicate.getIdentifier().toString());
			this.getPredicateTableStatement.setInt(2, predicate.getArity());
			ResultSet results = this.getPredicateTableStatement.executeQuery();

			if (results.next()) {
				String predicateTableName = results.getString("predicate_table_name");
				table = this.getDriver().getTable(predicateTableName);
			}

			results.close();
		} catch (SQLException e) {
			throw new AtomSetException(e);
		}

		return table;
	}

	/**
	 * 
	 * @return a free predicate id.
	 * @throws SQLException
	 */
	private long getFreePredicateId() throws SQLException {
		long value;

		this.getCounterValueStatement.setString(1, MAX_PREDICATE_ID_COUNTER);
		ResultSet result = this.getCounterValueStatement.executeQuery();
		result.next();
		value = result.getLong("value") + 1;
		result.close();
		this.updateCounterValueStatement.setLong(1, value);
		this.updateCounterValueStatement.setString(2, MAX_PREDICATE_ID_COUNTER);
		this.updateCounterValueStatement.executeUpdate();

		return value;
	}

	@Override
	public CloseableIterator<Predicate> predicatesIterator() throws AtomSetException {
		try {
			return new AdHocResultSetPredicateIterator(this, GET_ALL_PREDICATES_QUERY);
		} catch (SQLException e) {
			throw new AtomSetException("Untreated exception", e);
		}
	}

	@Override
	public Set<Predicate> getPredicates() throws AtomSetException {
		TreeSet<Predicate> set = new TreeSet<Predicate>();
		try {
			CloseableIterator<Predicate> it = this.predicatesIterator();
			while (it.hasNext()) {
				set.add(it.next());
			}
		} catch (IteratorException e) {
			throw new AtomSetException(e);
		}
		return set;
	}

	@Override
	public RdbmsConjunctiveQueryTranslator getConjunctiveQueryTranslator() {
		return this.QUERY_TRANSLATOR;
	}

	@Override
	protected DBTable getPredicateTable(Predicate p) throws AtomSetException {
		return super.getPredicateTable(p); // for package visibility
	}
	
	private static String getType(Term t) {
		if (t.isVariable()) {
			return "V";
		} else if (t.isLiteral()) {
			return "L";
		} else {
			return "C";
		}
	}

}
