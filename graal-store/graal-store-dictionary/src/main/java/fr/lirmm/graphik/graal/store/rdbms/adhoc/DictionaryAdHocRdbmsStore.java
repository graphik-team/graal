package fr.lirmm.graphik.graal.store.rdbms.adhoc;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.graal.store.dictionary.DictionaryMapper;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.TermGenerator;
import fr.lirmm.graphik.graal.api.core.UnsupportedAtomTypeException;
import fr.lirmm.graphik.graal.core.StoredVariableGenerator;
import fr.lirmm.graphik.graal.store.rdbms.AbstractRdbmsConjunctiveQueryTranslator;
import fr.lirmm.graphik.graal.store.rdbms.RdbmsConjunctiveQueryTranslator;
import fr.lirmm.graphik.graal.store.rdbms.adhoc.AdHocRdbmsStore;
import fr.lirmm.graphik.graal.store.rdbms.driver.RdbmsDriver;
import fr.lirmm.graphik.graal.store.rdbms.util.DBColumn;
import fr.lirmm.graphik.graal.store.rdbms.util.DBTable;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.IteratorException;

/**
 * 
 * @author mathieu dodard
 * @author renaud colin
 *
 */
public class DictionaryAdHocRdbmsStore extends AdHocRdbmsStore{

	/**
	 * 
	 */
	protected DictionaryMapper dictionaryMapper;
	
	/**
	 * 
	 */
	private final StoredVariableGenerator freshSymbolGenerator = new StoredVariableGenerator("EE");
	
	
	private final RdbmsConjunctiveQueryTranslator QUERY_TRANSLATOR;
	
	static {
		/**
		 * Update predicate table creation with label as integer 
		 */
		createPredicateTableQuery = "CREATE TABLE "
	            + PREDICATE_TABLE_NAME
	            + "(predicate_label int,"
	            + "predicate_arity int, "
	            + "predicate_table_name varchar("
	            + VARCHAR_SIZE
	            + "), PRIMARY KEY (predicate_label, predicate_arity));";
	 
	}
	 
	public DictionaryAdHocRdbmsStore(RdbmsDriver driver, DictionaryMapper mapper) throws AtomSetException {
		super(driver);
		dictionaryMapper = mapper;
		QUERY_TRANSLATOR = new DictionaryAdHocConjectiveQueryTranslator(this,dictionaryMapper);
	}
	
//	@Override
//	public CloseableIterator<Term> termsIterator() throws AtomSetException {
//		return dictionaryMapper.termIterator();
//	}
//	
//	@Override
//	public CloseableIterator<Predicate> predicatesIterator() throws AtomSetException {
//		return dictionaryMapper.predicateIterator();
//	}
//	
//	@Override
//	public Set<Predicate> getPredicates() throws AtomSetException {
//		return new TreeSet<Predicate>(dictionaryMapper.getAllPredicates());
//	}
//	
//	@Override
//	public Set<Term> getTerms() throws AtomSetException{
//		return new TreeSet<Term>(dictionaryMapper.getTerms());
//	}
	
	@Override
	public TermGenerator getFreshSymbolGenerator() {
		return freshSymbolGenerator;
	}
	
	@Override
	public RdbmsConjunctiveQueryTranslator getConjunctiveQueryTranslator() {
		return QUERY_TRANSLATOR;
	}
	
	/**
	 * Save the dictionary which is in memory into the database
	 */
	public void saveDictionary() {
		
	}
	
	@Override
	protected Statement add(Statement statement, Atom atom) throws AtomSetException {
		if (!this.check(atom)) {
			throw new UnsupportedAtomTypeException(""); // FIXME say why
		}
		try {
			updateExistentialTermIntoDictionary();
			DBTable table = this.createPredicateTableIfNotExist(atom.getPredicate());
			Iterator<DBColumn> cols = table.getColumns().iterator();

			Map<String, String> data = new TreeMap<String, String>();
			for (Term t : atom.getTerms()) {
				DBColumn col = cols.next();
				data.put(col.getName(), this.getConjunctiveQueryTranslator().formatFromColumnType(col, t));
			}
			String query = this.getDriver().getInsertOrIgnoreQuery(table, data);
			statement.addBatch(query);
		} catch (SQLException e) {
			throw new AtomSetException(e);
		}
		return statement;
	}
	
	/**
	 * 
	 */
	private void updateExistentialTermIntoDictionary() {
		Collection<Term> newExistentialTerms = freshSymbolGenerator.getNewGeneratedSymbol();	
		if(! newExistentialTerms.isEmpty()){
			dictionaryMapper.addAllExistentialVariables(newExistentialTerms); // update the dictionary
			freshSymbolGenerator.resetNewGeneratedSymbol();
		}
	}

	
	@Override
	/**
	 * 
	 */
	public boolean addAll(CloseableIterator<? extends Atom> stream) throws AtomSetException {
		
		updateExistentialTermIntoDictionary(); 

		final int BF_SIZE = 8192 * 16;
		HashMap<Predicate, StringBuilder> atomsByPredicate = new HashMap<>(); // associate to each predicate the corresponding insert query
		HashSet<Predicate> predicatesPresents = new HashSet<>();
		int i = 0;

		try {
			while (stream.hasNext()) { // read the stream by block of BF_SIZE atoms
				Object obj = stream.next();
				if (obj instanceof Atom) { 
					Atom atom = (Atom) obj;
					
					if (!atomsByPredicate.containsKey(atom.getPredicate())) { // write INSERT INTO predicate_name VALUES 
						String predicateName = createPredicateTableIfNotExist(atom.getPredicate()).getName();
						atomsByPredicate.put(atom.getPredicate(), new StringBuilder("INSERT INTO "
								+predicateName + " VALUES "));
					}
					predicatesPresents.add(atom.getPredicate());
					StringBuilder predQuery = atomsByPredicate.get(atom.getPredicate()); // get the predicate insert query and update it
					predQuery.append('(');
					
					boolean beginQuery = false;
					for (Term term : atom.getTerms()) { // for each term 
						if (beginQuery)
							predQuery.append(',');
						predQuery.append(this.getConjunctiveQueryTranslator().formatFromColumnType(null, term));
						beginQuery = true;
					}
					predQuery.append("),");

					if (++i == BF_SIZE) { // if buffer size is reached then load data into POSTGRES with copy command
						fillTables(atomsByPredicate, predicatesPresents); // fill the tables
						i = 0;
						atomsByPredicate.clear();
					}
					// }

				}
			}
			if (i > 0) { // if the buffer has not been flush then do it a last time
				fillTables(atomsByPredicate, predicatesPresents);
			}
			return true;
		} catch (IteratorException | SQLException e) {
			stream.close();
			throw new AtomSetException(e);
		}
	}

	/**
	 * Fill the database 
	 * @param atomsByPredicate
	 * @param predicatesPresents
	 * @param sqlConnection
	 * @throws SQLException
	 */
	private void fillTables(HashMap<Predicate, StringBuilder> atomsByPredicate, HashSet<Predicate> predicatesPresents) throws SQLException {

		for (Predicate pred : atomsByPredicate.keySet()) { // for each predicate,
			if (predicatesPresents.contains(pred)) {
				StringBuilder query = atomsByPredicate.get(pred); // get the insert query 
				query.setCharAt(query.length() - 1, ' ');
				query.append(" ON CONFLICT DO NOTHING;"); // if duplicate key then ignore 
				Statement statement = this.getConnection().createStatement();
				statement.execute(query.toString());
			}
		}
	}
	
	@Override
	/**
	 * Update predicate insertion with integer as first parameter instead of varchar 
	 */
	protected void insertPredicate(String tableName, Predicate predicate) throws SQLException {
		this.insertPredicateStatement.setInt(1, (Integer) predicate.getIdentifier());
		this.insertPredicateStatement.setInt(2, predicate.getArity());
		this.insertPredicateStatement.setString(3, tableName);
		this.insertPredicateStatement.execute();
	}
	
	
	protected static DBTable generateNewDBTableData(String tableName, int arity) {
		List<DBColumn> columns = new ArrayList<DBColumn>();
		for (int i = 0; i < arity; ++i) {	
			columns.add(new DBColumn(AbstractRdbmsConjunctiveQueryTranslator.PREFIX_TERM_FIELD + i, Types.INTEGER)); // use integer column type 
		}
		return new DBTable(tableName, columns);
	}
	
	

}
