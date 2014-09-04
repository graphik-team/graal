/**
 * 
 */
package fr.lirmm.graphik.graal.store.rdbms;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.SymbolGenerator;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.Term.Type;
import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
import fr.lirmm.graphik.graal.store.StoreException;
import fr.lirmm.graphik.graal.store.rdbms.driver.RdbmsDriver;
import fr.lirmm.graphik.util.stream.ObjectReader;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class PlainTableRDBMSStore extends AbstractRdbmsStore {

	
	private static final Logger logger = LoggerFactory
			.getLogger(PlainTableRDBMSStore.class);
	
	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////
	
	/**
	 * 
	 * @param driver
	 * @throws SQLException
	 * @throws StoreException
	 */
	public PlainTableRDBMSStore(RdbmsDriver driver) throws StoreException {
		super(driver);
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	@Override
	public String transformToSQL(ConjunctiveQuery cquery) throws StoreException {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public Term getTerm(String label) throws StoreException {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public ObjectReader<Atom> iterator() {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public SymbolGenerator getFreeVarGen() {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public boolean contains(Atom atom) throws StoreException {
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
	public Set<Term> getTerms() throws StoreException {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public ObjectReader<Predicate> getAllPredicate() throws AtomSetException {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public Set<Term> getTerms(Type type) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	protected Statement add(Statement statement, Atom atom)
			throws StoreException {
		Term term;
		try {
			this.checkPredicateTable(atom.getPredicate());
			StringBuilder query = new StringBuilder(this.getDriver()
					.getInsertOrIgnoreStatement());
			query.append(this.getPredicateTableName(atom.getPredicate()));
			query.append(" VALUES (");

			Iterator<Term> terms = atom.getTerms().iterator();

			term = terms.next();
			query.append('\'').append(term).append('\'');
			while (terms.hasNext()) {
				term = terms.next();
				query.append(", '").append(term).append('\'');
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
	 * @param predicate
	 * @throws StoreException 
	 */
	private void checkPredicateTable(Predicate predicate) throws StoreException {
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
			throws StoreException {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	protected boolean testDatabaseSchema() throws StoreException {
		return true;
	}

	@Override
	protected void createDatabaseSchema() throws StoreException {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

}
