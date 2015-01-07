/**
 * 
 */
package fr.lirmm.graphik.graal.store.rdbms;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.SymbolGenerator;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.Term.Type;
import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
import fr.lirmm.graphik.graal.store.StoreException;
import fr.lirmm.graphik.graal.store.rdbms.driver.RdbmsDriver;
import fr.lirmm.graphik.util.MethodNotImplementedError;
import fr.lirmm.graphik.util.stream.ObjectReader;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class NoConstraintRdbmsStore extends AbstractRdbmsStore {

	/**
	 * @param driver
	 * @throws StoreException
	 */
	public NoConstraintRdbmsStore(RdbmsDriver driver)
			throws StoreException {
		super(driver);
	}

	/* (non-Javadoc)
	 * @see fr.lirmm.graphik.alaska.store.IWriteableStore#remove(fr.lirmm.graphik.kb.core.IAtom)
	 */
	@Override
	public boolean remove(Atom atom) {
		// TODO implement this method
		throw new MethodNotImplementedError("This method isn't implemented");
	}

	/* (non-Javadoc)
	 * @see fr.lirmm.graphik.alaska.store.IStore#iterator()
	 */
	@Override
	public ObjectReader<Atom> iterator() {
		// TODO implement this method
		throw new MethodNotImplementedError("This method isn't implemented");
	}

	/* (non-Javadoc)
	 * @see fr.lirmm.graphik.alaska.store.IStore#getFreeVarGen()
	 */
	@Override
	public SymbolGenerator getFreeVarGen() {
		// TODO implement this method
		throw new MethodNotImplementedError("This method isn't implemented");
	}

	/* (non-Javadoc)
	 * @see fr.lirmm.graphik.alaska.store.IStore#contains(fr.lirmm.graphik.kb.core.IAtom)
	 */
	@Override
	public boolean contains(Atom atom) {
		// TODO implement this method
		throw new MethodNotImplementedError("This method isn't implemented");
	}

	/* (non-Javadoc)
	 * @see fr.lirmm.graphik.alaska.store.IStore#getTerms()
	 */
	@Override
	public Set<Term> getTerms() {
		// TODO implement this method
		throw new MethodNotImplementedError("This method isn't implemented");
	}

	/* (non-Javadoc)
	 * @see fr.lirmm.graphik.kb.core.IAtomSet#getTerms(fr.lirmm.graphik.kb.core.ITerm.Type)
	 */
	@Override
	public Set<Term> getTerms(Type type) {
		// TODO implement this method
		throw new MethodNotImplementedError("This method isn't implemented");
	}
	
	 /* (non-Javadoc)
     * @see fr.lirmm.graphik.alaska.store.rdbms.representation.IRdbmsRepresentation#add(java.sql.Statement, fr.lirmm.graphik.kb.core.IAtom)
     */
    @Override
    public Statement add(Statement statement, Atom atom) throws StoreException {
        try {
            StringBuilder l = new StringBuilder("create table ");
            l.append(atom.getPredicate().getLabel());
            l.append("(term0 varchar(128), term1 varchar(128));");
            statement.execute(l.toString());
        }
        catch (Exception e) { }
        
        StringBuilder supersqlcommand = new StringBuilder("insert into ");

        supersqlcommand.append(atom.getPredicate().getLabel());
        supersqlcommand.append(" values ('");
        supersqlcommand.append(atom.getTerm(0));
        supersqlcommand.append("','");
        supersqlcommand.append(atom.getTerm(1));
        supersqlcommand.append("');");
        
        try {
			statement.addBatch(supersqlcommand.toString());
		} catch (SQLException e) {
			throw new StoreException(e.getMessage(), e);
		}
        return statement;
    }
    
    /* (non-Javadoc)
     * @see fr.lirmm.graphik.alaska.store.rdbms.representation.IRdbmsRepresentation#checkDatabaseStructure(java.sql.Connection)
     */
    @Override
    protected void createDatabaseSchema()
            throws StoreException {
        try {
            this.createStatement().execute("create table alaskavars (alaskavars1 varchar(128));");
            
        } catch (SQLException e) {
        	throw new StoreException(e.getMessage(), e);
        }
    }
    
    protected boolean testDatabaseSchema() throws StoreException {
    	try {
			ResultSet rs = this.createStatement().executeQuery("SELECT * FROM alaskavars limit 1;");
			rs.close();
		} catch (SQLException e) {
			return false;
		}
    	
    	return true;
    }

	/* (non-Javadoc)
	 * @see fr.lirmm.graphik.alaska.store.rdbms.IRdbmsStore#transformToSQL(fr.lirmm.graphik.kb.query.impl.ConjunctiveQuery)
	 */
	@Override
	public String transformToSQL(ConjunctiveQuery cquery) throws StoreException {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	/* (non-Javadoc)
	 * @see fr.lirmm.graphik.kb.core.AtomSet#getAllPredicate()
	 */
	@Override
	public ObjectReader<Predicate> getAllPredicates() throws AtomSetException {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	/* (non-Javadoc)
	 * @see fr.lirmm.graphik.alaska.store.rdbms.IRdbmsStore#getTerm(java.lang.String)
	 */
	@Override
	public Term getTerm(String label) throws StoreException {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	/* (non-Javadoc)
	 * @see fr.lirmm.graphik.graal.store.rdbms.AbstractRdbmsStore#remove(java.sql.Statement, fr.lirmm.graphik.graal.core.Atom)
	 */
	@Override
	protected Statement remove(Statement statement, Atom atom)
			throws StoreException {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public void clear() {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

}
