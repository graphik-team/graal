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

import java.util.Iterator;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.store.rdbms.util.DBColumn;
import fr.lirmm.graphik.graal.store.rdbms.util.DBTable;
import fr.lirmm.graphik.graal.store.rdbms.util.SQLQuery;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public interface RdbmsConjunctiveQueryTranslator {

	/**
	 * Translates a ConjunctiveQuery into an SQL query
	 * 
	 * @param query
	 * @param s a Substitution of Variable from query into Term from database.
	 * @return the String representation of an SQL query which is equivalent to
	 *         the specified {@link ConjunctiveQuery}.
	 * @throws AtomSetException
	 */
	SQLQuery translate(ConjunctiveQuery query, Substitution s) throws AtomSetException;

	/**
	 * Translates a rule into an "INSERT ... SELECT ..." SQL statement.
	 * 
	 * @param rangeRestrictedRule
	 *            a range restricted rule (i.e. all variables that appear in the
	 *            head also occur in the body).
	 * @return a string representing the generated SQL statement. If the rule
	 *         does not fulfill the range restricted condition the behavior is
	 *         undefined.
	 * @throws AtomSetException
	 */
	Iterator<SQLQuery> translate(Rule rangeRestrictedRule) throws AtomSetException;

	/**
	 * Produces a SQL contains Query. The produced query should return one tuple
	 * if the specified atom is present, no tuple otherwise.
	 * 
	 * @param atom
	 * @return a SQLQuery encapsuling the SQL string which represents the contains query.
	 * @throws AtomSetException
	 */
	SQLQuery translateContainsQuery(Atom atom) throws AtomSetException;


	/**
	 * Produces a SQL Query which returns all term labels present in the
	 * specified position of the specified predicate.
	 * 
	 * @param p
	 * @param position
	 * @return a SQLQuery.
	 * @throws AtomSetException
	 */
	SQLQuery translateTermsByPredicatePositionQuery(Predicate p, int position) throws AtomSetException;

	/**
	 * Produces a SQL Query which remove the specified atom.
	 * 
	 * @param atom
	 * @return a SQLQuery.
	 * @throws AtomSetException
	 */
	SQLQuery translateRemove(Atom atom) throws AtomSetException;

	/**
	 * Produces a CREATE TABLE Query.
	 * 
	 * @param table
	 * @return a String representing the SQL create table statement.
	 */
	String translateCreateTable(DBTable table);

	/**
	 * Format a {@link Term} to fulfill the database column type.
	 * 
	 * @param col
	 *            the column informations
	 * @param term
	 *            the term to format
	 * @return a String representing the term
	 * @throws AtomSetException
	 */
	String formatFromColumnType(DBColumn col, Term term) throws AtomSetException;

	/**
	 * Create a {@link Term} from a String representation and a column type.
	 * 
	 * @param sqlType
	 *            the sql column type
	 * @param value
	 *            the String representation of the term to create
	 * @return the corresponding {@link Term}
	 * @throws AtomSetException
	 */
	Term createTermFromColumnType(int sqlType, String value) throws AtomSetException;

}
