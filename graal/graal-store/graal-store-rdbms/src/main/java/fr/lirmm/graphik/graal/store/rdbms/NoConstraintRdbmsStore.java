/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2015)
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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.VariableGenerator;
import fr.lirmm.graphik.graal.api.core.Term.Type;
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
	public VariableGenerator getFreeVarGen() {
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
