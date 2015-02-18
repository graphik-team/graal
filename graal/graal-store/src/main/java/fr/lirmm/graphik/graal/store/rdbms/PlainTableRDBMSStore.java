/**
 * 
 */
package fr.lirmm.graphik.graal.store.rdbms;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Set;
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
import fr.lirmm.graphik.graal.store.rdbms.driver.RdbmsDriver;
import fr.lirmm.graphik.util.MethodNotImplementedError;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class PlainTableRDBMSStore extends AbstractRdbmsStore {

	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(PlainTableRDBMSStore.class);
	
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
	public String transformToSQL(ConjunctiveQuery cquery) throws AtomSetException {
		// TODO implement this method
		throw new MethodNotImplementedError("This method isn't implemented");
	}

	@Override
	public Term getTerm(String label) throws AtomSetException {
		// TODO implement this method
		throw new MethodNotImplementedError("This method isn't implemented");
	}

	@Override
	public Iterator<Atom> iterator() {
		// TODO implement this method
		throw new MethodNotImplementedError("This method isn't implemented");
	}

	@Override
	public SymbolGenerator getFreeVarGen() {
		// TODO implement this method
		throw new MethodNotImplementedError("This method isn't implemented");
	}

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
		}
		return false;
	}

	@Override
	public Set<Term> getTerms() throws AtomSetException {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public Iterator<Predicate> predicatesIterator() {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}
	
	@Override
	public Set<Predicate> getPredicates() throws AtomSetException {
		TreeSet<Predicate> set = new TreeSet<Predicate>();
		Iterator<Predicate> it = this.predicatesIterator();
		while(it.hasNext()) {
			set.add(it.next());
		}
		return set;
	}

	@Override
	public Set<Term> getTerms(Type type) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	protected Statement add(Statement statement, Atom atom)
			throws AtomSetException {
		try {
			this.checkPredicateTable(atom.getPredicate());
			String tableName = this.getPredicateTableName(atom.getPredicate());
			String query = this.getDriver().getInsertOrIgnoreStatement(tableName, atom.getTerms());

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

	/**
	 * @param predicate
	 * @return
	 */
	private String getPredicateTableName(Predicate predicate) {
		return predicate.getLabel() + predicate.getArity();
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
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public void clear() {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

}
