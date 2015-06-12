/* Graal v0.7.4
 * Copyright (c) 2014-2015 Inria Sophia Antipolis - Méditerranée / LIRMM (Université de Montpellier & CNRS)
 * All rights reserved.
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * Author(s): Clément SIPIETER
 *            Mélanie KÖNIG
 *            Swan ROCHER
 *            Jean-François BAGET
 *            Michel LECLÈRE
 *            Marie-Laure MUGNIER
 */
 /**
 * 
 */
package fr.lirmm.graphik.graal.store.rdbms;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.SymbolGenerator;
import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
import fr.lirmm.graphik.graal.core.term.Term;
import fr.lirmm.graphik.graal.core.term.Term.Type;
import fr.lirmm.graphik.graal.store.rdbms.driver.RdbmsDriver;
import fr.lirmm.graphik.util.MethodNotImplementedError;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class NoConstraintRdbmsStore extends AbstractRdbmsStore {

	/**
	 * @param driver
	 * @throws AtomSetException
	 */
	public NoConstraintRdbmsStore(RdbmsDriver driver)
			throws AtomSetException {
		super(driver);
	}

	@Override
	public boolean remove(Atom atom) {
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
	public boolean contains(Atom atom) {
		// TODO implement this method
		throw new MethodNotImplementedError("This method isn't implemented");
	}

	@Override
	public Set<Term> getTerms() {
		// TODO implement this method
		throw new MethodNotImplementedError("This method isn't implemented");
	}

	@Override
	public Set<Term> getTerms(Type type) {
		// TODO implement this method
		throw new MethodNotImplementedError("This method isn't implemented");
	}
	
    @Override
    public Statement add(Statement statement, Atom atom) throws AtomSetException {
        try {
            StringBuilder l = new StringBuilder("create table ");
            l.append(atom.getPredicate().getIdentifier());
            l.append("(term0 varchar(128), term1 varchar(128));");
            statement.execute(l.toString());
        }
        catch (Exception e) { }
        
        StringBuilder supersqlcommand = new StringBuilder("insert into ");

        supersqlcommand.append(atom.getPredicate().getIdentifier());
        supersqlcommand.append(" values ('");
        supersqlcommand.append(atom.getTerm(0));
        supersqlcommand.append("','");
        supersqlcommand.append(atom.getTerm(1));
        supersqlcommand.append("');");
        
        try {
			statement.addBatch(supersqlcommand.toString());
		} catch (SQLException e) {
			throw new AtomSetException(e.getMessage(), e);
		}
        return statement;
    }
    
    @Override
    protected void createDatabaseSchema()
            throws AtomSetException {
        try {
            this.createStatement().execute("create table alaskavars (alaskavars1 varchar(128));");
            
        } catch (SQLException e) {
        	throw new AtomSetException(e.getMessage(), e);
        }
    }
    
    @Override
	protected boolean testDatabaseSchema() throws AtomSetException {
    	try {
			ResultSet rs = this.createStatement().executeQuery("SELECT * FROM alaskavars limit 1;");
			rs.close();
		} catch (SQLException e) {
			return false;
		}
    	
    	return true;
    }

	@Override
	public String transformToSQL(ConjunctiveQuery cquery) throws AtomSetException {
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
	public Term getTerm(String label) throws AtomSetException {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	protected Statement remove(Statement statement, Atom atom)
			throws AtomSetException {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public void clear() {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public Iterator<String> transformToSQL(Rule rangeRestrictedRule)
			throws AtomSetException {
		// TODO implement this method
		throw new MethodNotImplementedError();
	}

}
