/**
 * 
 */
package fr.lirmm.graphik.graal.store.rdbms;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.SymbolGenerator;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.Term.Type;
import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
import fr.lirmm.graphik.graal.core.atomset.ReadOnlyAtomSet;
import fr.lirmm.graphik.graal.store.StoreException;
import fr.lirmm.graphik.graal.store.rdbms.driver.RdbmsDriver;
import fr.lirmm.graphik.util.stream.ObjectReader;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 *         This class represents an implementation of a store in a Relational
 *         Database System where each predicates is stored in a dedicated table.
 */
public class DefaultRdbmsStore extends AbstractRdbmsStore {
	private static final Logger logger = LoggerFactory
			.getLogger(DefaultRdbmsStore.class);

	private static final int VARCHAR_SIZE = 128;

	private static final String MAX_VARIABLE_ID_COUNTER = "max_variable_id";
	private static final String MAX_PREDICATE_ID_COUNTER = "max_predicate_id";

	// tables names
	static final String counterTableName = "counters";
	static final String predicateTableName = "predicates";
	static final String termTableName = "terms";

	// table fields name
	static final String PREFIX_TERM_FIELD = "term";

	// queries
	private static final String getPredicateQuery = "SELECT * FROM "
													+ predicateTableName
													+ " WHERE predicate_label = ? " 
													+ " AND predicate_arity = ?;";
	private static final String insertPredicateQuery = "INSERT INTO "
													   + predicateTableName
													   + " VALUES ( ?, ?, ?)";

	private static final String getAllTermsQuery = "SELECT * FROM "
												   + termTableName
												   + ";";
	private static final String getTermQuery = "SELECT * FROM "
											   + termTableName
											   + " WHERE term = ?;";

	// counter queries
	private static final String getCounterValueQuery = "SELECT value FROM "
													   + counterTableName
													   + " WHERE counter_name = ?;";

	private static final String updateCounterValueQuery = "UPDATE "
														  + counterTableName
														  + " SET value = ? WHERE counter_name = ?;";

	// misc queries
	private static final String EMPTY_QUERY = "select 0 from (select 0) as t where 0;";
	private static final String TEST_SCHEMA_QUERY = "SELECT 0 FROM "
													+ predicateTableName
													+ " LIMIT 1";

	private PreparedStatement getPredicateTableStatement;
	private PreparedStatement insertPredicateStatement;

	private PreparedStatement insertTermStatement;
	private PreparedStatement getTermStatement;

	private PreparedStatement getCounterValueStatement;
	private PreparedStatement updateCounterValueStatement;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * 
	 * @param driver
	 * @throws SQLException
	 * @throws StoreException
	 */
	public DefaultRdbmsStore(RdbmsDriver driver) throws StoreException {
		super(driver);

		try {
			this.getPredicateTableStatement = this.getConnection()
					.prepareStatement(getPredicateQuery);
			this.insertPredicateStatement = this.getConnection()
					.prepareStatement(insertPredicateQuery);
			this.getCounterValueStatement = this.getConnection()
					.prepareStatement(getCounterValueQuery);
			this.updateCounterValueStatement = this.getConnection()
					.prepareStatement(updateCounterValueQuery);
			this.insertTermStatement = this.getConnection().prepareStatement(
					this.getInsertTermQuery());
			this.getTermStatement = this.getConnection().prepareStatement(
					getTermQuery);
		} catch (SQLException e) {
			throw new StoreException(e.getMessage(), e);
		}

	}

	@Override
	protected boolean testDatabaseSchema() throws StoreException {
		Statement statement = null;
		try {
			statement = this.createStatement();
			statement.execute(TEST_SCHEMA_QUERY);
		} catch (SQLException e) {
			return false;
		} catch (StoreException e) {
			throw new StoreException(e.getMessage(), e);
		} finally {
			if(statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					throw new StoreException(e);
				}
			}
		}

		return true;
	}

	@Override
	protected void createDatabaseSchema() throws StoreException {
		final String createPredicateTableQuery = "CREATE TABLE IF NOT EXISTS "
												 + predicateTableName
												 + "(predicate_label varchar("
												 + VARCHAR_SIZE
												 + "), predicate_arity int, "
												 + "predicate_table_name varchar("
												 + VARCHAR_SIZE
												 + "), PRIMARY KEY (predicate_label, predicate_arity));";

		final String createTermTableQuery = "CREATE TABLE IF NOT EXISTS "
											+ termTableName
											+ " (term varchar("
											+ VARCHAR_SIZE
											+ "), term_type varchar("
											+ VARCHAR_SIZE
											+ "), PRIMARY KEY (term));";

		final String termTypeTableName = "term_type";
		final String createTermTypeTableQuery = "CREATE TABLE IF NOT EXISTS "
												+ termTypeTableName
												+ " (term_type varchar("
												+ VARCHAR_SIZE
												+ "), PRIMARY KEY (term_type));";

		final String insertTermTypeQuery = "INSERT INTO "
										   + termTypeTableName
										   + " values (?);";
		Statement statement = null;
		PreparedStatement pstat = null;
		try {
			statement = this.createStatement();
			if (logger.isDebugEnabled())
				logger.debug("Create database schema");

			if (logger.isDebugEnabled())
				logger.debug(createPredicateTableQuery);
			statement.executeUpdate(createPredicateTableQuery);

			if (logger.isDebugEnabled())
				logger.debug(createTermTypeTableQuery);
			statement.executeUpdate(createTermTypeTableQuery);

			pstat = this.getConnection().prepareStatement(insertTermTypeQuery);
			Term.Type[] types = Term.Type.values();
			for (int i = 0; i < types.length; ++i) {
				pstat.setString(1, types[i].toString());
				pstat.addBatch();
			}
			pstat.executeBatch();
			pstat.close();

			if (logger.isDebugEnabled())
				logger.debug(createTermTableQuery);
			statement.executeUpdate(createTermTableQuery);

			final String createCounterTableQuery = "CREATE TABLE IF NOT EXISTS "
												   + counterTableName
												   + " (counter_name varchar(64), value long, PRIMARY KEY (counter_name));";

			if (logger.isDebugEnabled())
				logger.debug(createCounterTableQuery);
			statement.executeUpdate(createCounterTableQuery);
		} catch (SQLException e) {
			throw new StoreException(e.getMessage(), e);
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					throw new StoreException(e);
				}
			}
		}
		
		try {
			final String insertCounterTableQuery = "INSERT INTO "
					   + counterTableName
					   + " values (?, -1);";
			final String[] counters = { MAX_PREDICATE_ID_COUNTER,
					MAX_VARIABLE_ID_COUNTER };
			pstat = this.getConnection().prepareStatement(
					insertCounterTableQuery);
			for (int i = 0; i < counters.length; ++i) {
				pstat.setString(1, counters[i]);
				pstat.addBatch();
			}
			pstat.executeBatch();
			this.getConnection().commit();
		} catch (SQLException e) {
			throw new StoreException(e.getMessage(), e);
		} finally {
			if (pstat != null) {
				try {
					pstat.close();
				} catch (SQLException e) {
					throw new StoreException(e);
				}
			}
		}

	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.lirmm.graphik.alaska.store.IStore#iterator()
	 */
	@Override
	public ObjectReader<Atom> iterator() {
		try {
			return new DefaultRdbmsIterator(this);
		} catch (AtomSetException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.lirmm.graphik.alaska.store.IStore#getFreeVarGen()
	 */
	@Override
	public SymbolGenerator getFreeVarGen() {
		return new RdbmsSymbolGenenrator(this.getConnection(),
				MAX_VARIABLE_ID_COUNTER, getCounterValueQuery,
				updateCounterValueQuery);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.lirmm.graphik.alaska.store.IStore#contains(fr.lirmm.graphik.kb.core
	 * .IAtom)
	 */
	@Override
	public boolean contains(Atom atom) throws StoreException {
		boolean res = false;
		Term term;
		Statement statement = null;
		int termIndex = -1;
		String tableName = this.predicateTableExist(atom.getPredicate());
		if (logger.isDebugEnabled()) {
			logger.debug(atom.getPredicate() + " -- > " + tableName);
		}
		if (tableName != null) {

			StringBuilder query = new StringBuilder("SELECT * FROM ");
			query.append(tableName);
			query.append(" WHERE ");

			Iterator<Term> terms = atom.getTerms().iterator();

			term = terms.next();            // TODO: FIX THIS => if arity = 0 -> crash ?!
			++termIndex;
			query.append("term").append(termIndex).append(" = \'").append(term)
					.append('\'');

			while (terms.hasNext()) {
				term = terms.next();
				++termIndex;
				query.append(" and ").append(PREFIX_TERM_FIELD)
						.append(termIndex).append(" = \'").append(term)
						.append('\'');
			}
			query.append(" LIMIT 1;");

			if (logger.isDebugEnabled()) {
				logger.debug(atom.toString() + " : " + query.toString());
			}
			ResultSet results;
			try {
				statement = this.createStatement();
				results = statement.executeQuery(query.toString());
				if (results.next()) {
					res = true;
				}
				results.close();
			} catch (SQLException e) {
				if(statement != null) {
					try {
						statement.close();
					} catch (SQLException sqlEx) {
						throw new StoreException(sqlEx);
					}
				}
				throw new StoreException(e);
			}
		}

		return res;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.lirmm.graphik.alaska.store.IStore#getTerms()
	 */
	@Override
	public Set<Term> getTerms() throws StoreException {
		Statement statement = this.createStatement();
		ResultSet results = null;
		Set<Term> terms;
		
		try {
			results = statement.executeQuery(getAllTermsQuery);
			terms = new TreeSet<Term>();

			while (results.next()) {
				terms.add(new Term(results.getString(1), Term.Type
						.valueOf(results.getString(2))));
			}
			
			results.close();
		} catch (SQLException e) {
			throw new StoreException(e);
		} finally {
			if(statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					throw new StoreException(e);
				}
			}
		}
		
		
		
		return terms;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.lirmm.graphik.kb.core.IAtomSet#getTerms(fr.lirmm.graphik.kb.core.ITerm
	 * .Type)
	 */
	@Override
	public Set<Term> getTerms(Type type) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	/**
	 * Get a term by its label
	 * 
	 * @param label
	 * @return
	 * @throws StoreException
	 */
	@Override
	public Term getTerm(String label) throws StoreException {
		ResultSet results;
		Term term = null;

		try {
			this.getTermStatement.setString(1, label);
			results = this.getTermStatement.executeQuery();
			if (results.next()) {
				term = new Term(results.getString(1), Term.Type.valueOf(results
						.getString(2)));
			}
			results.close();
		} catch (SQLException e) {
			throw new StoreException(e);
		}
		return term;

	}

	/**
	 * Transforms the fact into a SQL statement.
	 */
	@Override
	public String transformToSQL(ConjunctiveQuery cquery) throws StoreException {

		ReadOnlyAtomSet atomSet = cquery.getAtomSet();

		StringBuilder fields = new StringBuilder();
		StringBuilder tables = new StringBuilder();
		StringBuilder where = new StringBuilder();

		HashMap<Atom, String> tableNames = new HashMap<Atom, String>();
		HashMap<Predicate, Integer> predicateCount = new HashMap<Predicate, Integer>();
		HashMap<String, String> lastOccurrence = new HashMap<String, String>();

		ArrayList<String> constants = new ArrayList<String>();
		ArrayList<String> equivalences = new ArrayList<String>();
		TreeMap<Term, String> columns = new TreeMap<Term, String>();

		for (Atom atom : atomSet) {
			int count = 1;
			if (predicateCount.containsKey(atom.getPredicate())) {
				count = predicateCount.get(atom.getPredicate()) + 1;
			}
			predicateCount.put(atom.getPredicate(), count);
			String tableName = atom.getPredicate().getLabel() + count;
			tableNames.put(atom, tableName);
		}
		
		//
		for (Atom atom : atomSet) {
			String currentAtom = tableNames.get(atom) + ".";

			int position = 0;
			for (Term term : atom.getTerms()) {
				String thisTerm = currentAtom + PREFIX_TERM_FIELD + position;
				if (term.isConstant()) {
					constants.add(thisTerm + " = '" + term + "'");
				} else {
					if (lastOccurrence.containsKey(term.toString())) {
						equivalences.add(lastOccurrence.get(term.toString())
										 + " = "
										 + thisTerm);
					}
					lastOccurrence.put(term.toString(), thisTerm);
					if (cquery.getAnswerVariables().contains(term))
						columns.put(term, thisTerm + " as " + term);
				}
				++position;
			}
		}

		// Create FROM clause
		String tableName = null;
		for (Map.Entry<Atom, String> entries : tableNames.entrySet()) {
			if (tables.length() != 0)
				tables.append(", ");

			tableName = this.predicateTableExist(entries.getKey()
					.getPredicate());
			if (tableName == null)
				return this.createEmptyQuery(columns.size());
			else
				tables.append(tableName);

			tables.append(" as ");
			tables.append(entries.getValue());
		}

		

		// Create WHERE clause
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

		// Create SELECT clause
		while (!columns.isEmpty()) {
			Term term = columns.firstKey();
			String column = columns.remove(term);
			if (fields.length() != 0)
				fields.append(", ");

			fields.append(column);
		}

		StringBuilder query = new StringBuilder("SELECT DISTINCT ");
		if (fields.length() > 0)
			query.append(fields);
		else
			query.append("1");

		if (tables.length() > 0)
			query.append(" FROM ").append(tables);

		if (where.length() > 0)
			query.append(" WHERE ").append(where);

		query.append(';');

		if (logger.isDebugEnabled())
			logger.debug("Generated SQL query :"
						 + cquery
						 + " --> "
						 + query.toString());

		return query.toString();
	}

	// /////////////////////////////////////////////////////////////////////////
	// PROTECTED METHODS
	// /////////////////////////////////////////////////////////////////////////

	protected String getInsertTermQuery() {
		return this.getDriver().getInsertOrIgnoreStatement()
			   + termTableName
			   + " values (?, ?);";
	}

	/**
	 * @param statement
	 * @param atom
	 * @throws AtomSetException
	 * @throws SQLException
	 */
	protected Statement add(Statement statement, Atom atom)
														   throws StoreException {
		Term term;
		try {
			StringBuilder query = new StringBuilder(this.getDriver()
					.getInsertOrIgnoreStatement());
			query.append(this.getPredicateTable(atom.getPredicate()));
			query.append(" VALUES (");

			Iterator<Term> terms = atom.getTerms().iterator();

			term = terms.next();
			query.append('\'').append(term).append('\'');
			this.add(statement, term);
			while (terms.hasNext()) {
				term = terms.next();
				query.append(", '").append(term).append('\'');
				this.add(statement, term);
			}
			query.append(");");

			if (logger.isDebugEnabled()) {
				logger.debug(atom.toString() + " : " + query.toString());
			}
			statement.addBatch(query.toString());
		} catch (SQLException e) {
			throw new StoreException(e.getMessage(), e);
		}
		return statement;
	}
	
	/**
	 * 
	 * @param atom
	 * @return
	 */
	protected Statement remove(Statement statement, Atom atom) throws StoreException {
		try {
			String tableName = this.predicateTableExist(atom.getPredicate());
			if (tableName == null) 
				return statement;
			StringBuilder query = new StringBuilder("DELETE FROM ");
			query.append(tableName);
			query.append(" WHERE ");

			int termIndex = 0;
			for (Term t : atom.getTerms()) {
				if (termIndex != 0) {
					query.append(" and ");
				}
				query.append(PREFIX_TERM_FIELD).append(termIndex).append(" = '").append(t).append("'");
				++termIndex;
			}
			query.append(";");

			if (logger.isDebugEnabled()) {
				logger.debug("Removing " + atom.toString() + " : " + query.toString());
			}
			statement.addBatch(query.toString());
		} catch (SQLException e) {
			throw new StoreException(e.getMessage(), e);
		}
		return statement;
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

	private void add(Statement statement, Term term) throws SQLException {
		this.insertTermStatement.setString(1, term.toString());
		this.insertTermStatement.setString(2, term.getType().toString());
		this.insertTermStatement.execute();
	}

	private String getPredicateTable(Predicate predicate) throws SQLException,
														 StoreException {
		String tableName = this.predicateTableExist(predicate);
		if (tableName == null)
			tableName = this.createPredicateTable(predicate);
		return tableName;
	}

	/**
	 * 
	 * @param predicate
	 * @return
	 * @throws AtomSetException
	 * @throws SQLException
	 */
	private String createPredicateTable(Predicate predicate)
															throws SQLException,
															StoreException {
		String tableName = "pred" + this.getFreePredicateId();
		if (predicate.getArity() >= 1) {
			Statement stat = this.createStatement();
			stat.executeUpdate(generateCreateTablePredicateQuery(tableName,
					predicate));
			if(stat != null) {
				try {
					stat.close();
				} catch (SQLException e) {
					throw new StoreException(e);
				}
			}
			insertPredicate(tableName, predicate);
		} else {
			throw new StoreException("Unsupported arity 0"); // TODO Why ?!
		}
		return tableName;
	}

	private static String generateCreateTablePredicateQuery(
															String tableName,
															Predicate predicate) {
		StringBuilder primaryKey = new StringBuilder("PRIMARY KEY (");
		StringBuilder query = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
		query.append(tableName);

		query.append('(').append(PREFIX_TERM_FIELD).append('0');
		query.append(" varchar(").append(VARCHAR_SIZE).append(")");
		primaryKey.append("term0");
		for (int i = 1; i < predicate.getArity(); i++) {
			query.append(", ").append(PREFIX_TERM_FIELD).append(i)
					.append(" varchar(" + VARCHAR_SIZE + ")");
			primaryKey.append(", term" + i);
		}
		primaryKey.append(")");

		query.append(',');
		query.append(primaryKey);
		query.append(");");
		return query.toString();
	}

	/**
	 * 
	 * @param tableName
	 * @param predicate
	 * @throws SQLException
	 */
	private void insertPredicate(String tableName, Predicate predicate)
																	   throws SQLException {
		this.insertPredicateStatement.setString(1, predicate.getLabel());
		this.insertPredicateStatement.setInt(2, predicate.getArity());
		this.insertPredicateStatement.setString(3, tableName);
		this.insertPredicateStatement.execute();
	}

	/**
	 * 
	 * @param dbConnection
	 * @param predicate
	 * @return the table name corresponding to this predicate or null if this
	 *         predicate doesn't exist.
	 * @throws SQLException
	 */
	private String predicateTableExist(Predicate predicate)
														   throws StoreException {
		String predicateTableName = null;

		try {
			this.getPredicateTableStatement.setString(1, predicate.getLabel());
			this.getPredicateTableStatement.setInt(2, predicate.getArity());
			ResultSet results = this.getPredicateTableStatement.executeQuery();

			if (results.next())
				predicateTableName = results.getString("predicate_table_name");
			
			results.close();
		} catch (SQLException e) {
			throw new StoreException(e);
		}

		return predicateTableName;
	}

	/**
	 * 
	 * @param dbConnection
	 * @return
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.lirmm.graphik.kb.core.AtomSet#getAllPredicate()
	 */
	@Override
	public ObjectReader<Predicate> getAllPredicate() throws StoreException {
		return new DefaultRdbmsPredicateReader(this.getDriver());
	}
	
	private String createEmptyQuery(int nbAnswerVars) {
		StringBuilder s = new StringBuilder("select 0");
		
		for(int i=1; i<nbAnswerVars; ++i)
			s.append(", 0");
		
		s.append(" from (select 0) as t where 0;");
		return s.toString();

	}

}
