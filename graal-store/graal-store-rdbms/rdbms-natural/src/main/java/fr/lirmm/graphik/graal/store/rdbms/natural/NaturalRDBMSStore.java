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
package fr.lirmm.graphik.graal.store.rdbms.natural;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
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
import fr.lirmm.graphik.graal.api.core.ConstantGenerator;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Term.Type;
import fr.lirmm.graphik.graal.api.core.UnsupportedAtomTypeException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.api.store.BatchProcessor;
import fr.lirmm.graphik.graal.core.DefaultConstantGenerator;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.factory.DefaultAtomFactory;
import fr.lirmm.graphik.graal.core.factory.DefaultConjunctiveQueryFactory;
import fr.lirmm.graphik.graal.core.stream.SubstitutionIterator2AtomIterator;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.store.rdbms.AbstractRdbmsStore;
import fr.lirmm.graphik.graal.store.rdbms.RdbmsConjunctiveQueryTranslator;
import fr.lirmm.graphik.graal.store.rdbms.driver.RdbmsDriver;
import fr.lirmm.graphik.graal.store.rdbms.homomorphism.SqlHomomorphism;
import fr.lirmm.graphik.graal.store.rdbms.util.DBColumn;
import fr.lirmm.graphik.graal.store.rdbms.util.DBTable;
import fr.lirmm.graphik.graal.store.rdbms.util.SQLQuery;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.CloseableIteratorAdapter;
import fr.lirmm.graphik.util.stream.IteratorException;
import fr.lirmm.graphik.util.stream.Iterators;
import fr.lirmm.graphik.util.stream.converter.ConverterCloseableIterator;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
@SuppressWarnings("deprecation")
public class NaturalRDBMSStore extends AbstractRdbmsStore {

	private static final Logger LOGGER = LoggerFactory.getLogger(NaturalRDBMSStore.class);
	private final ConstantGenerator gen = new DefaultConstantGenerator("EE");

	private final NaturalConjunctiveQueryTranslator translator = new NaturalConjunctiveQueryTranslator(this);

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * 
	 * @param driver
	 * @throws AtomSetException
	 */
	public NaturalRDBMSStore(RdbmsDriver driver) throws AtomSetException {
		super(driver);
	}

	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public CloseableIterator<Atom> match(Atom atom, Substitution s) throws AtomSetException {
		if (!this.check(atom)) {
			return Iterators.<Atom> emptyIterator();
		}
		
		// check does not contains fixed variables
		for(Term t : s.getValues()) {
			if(t.isVariable()) {
				throw new AtomSetException("We can't query specified blank node on TripleStore :" + t);
			}
		}
		
		ConjunctiveQuery query = DefaultConjunctiveQueryFactory.instance().create(new LinkedListAtomSet(atom));
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
		if (!this.check(p)) {
			return Iterators.<Atom> emptyIterator();
		}
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
		if (!this.check(p)) {
			return Iterators.<Term> emptyIterator();
		}
		try {
			SQLQuery query = this.getConjunctiveQueryTranslator().translateTermsByPredicatePositionQuery(p, position);
			if (query.hasSchemaError()) {
				return Iterators.<Term> emptyIterator();
			} else {
				DBTable table = this.getPredicateTable(p);
				if (table == null) {
					throw new AtomSetException("No table for this predicate: " + p);
				}
				DBColumn col = table.getColumns().get(position);

				int sqlType = col.getType();
				return new NaturalResultSetTermIterator(this, query.toString(), sqlType);
			}
		} catch (SQLException e) {
			throw new AtomSetException(e);
		}
	}

	@Override
	public CloseableIterator<Predicate> predicatesIterator() throws AtomSetException {
		try {
			CloseableIterator<DBTable> tables = this.getDriver().getTables();
			return new ConverterCloseableIterator<DBTable, Predicate>(tables, DBTable2PredicateConverter.instance());
		} catch (SQLException e) {
			throw new AtomSetException("Error during querying for table names", e);
		}
	}

	@Override
	public CloseableIterator<Term> termsIterator() throws AtomSetException {
		Set<Term> terms = new TreeSet<Term>();
		CloseableIterator<Predicate> predIt = this.predicatesIterator();
		try {
			while (predIt.hasNext()) {
				Predicate p = predIt.next();
				for (int i = 0; i < p.getArity(); ++i) {
					CloseableIterator<Term> termIt = this.termsByPredicatePosition(p, i);
					while (termIt.hasNext()) {
						terms.add(termIt.next());
					}
				}
			}
		} catch (IteratorException e) {
			throw new AtomSetException(e);
		}
		return new CloseableIteratorAdapter<Term>(terms.iterator());
	}

	@Override
	@Deprecated
	public CloseableIterator<Term> termsIterator(Type type) throws AtomSetException {
		Set<Term> terms = new TreeSet<Term>();
		CloseableIterator<Predicate> predIt = this.predicatesIterator();
		try {
			while (predIt.hasNext()) {
				Predicate p = predIt.next();
				for (int i = 0; i < p.getArity(); ++i) {
					CloseableIterator<Term> termIt = this.termsByPredicatePosition(p, i);
					while (termIt.hasNext()) {
						Term t = termIt.next();
						if (type.equals(t.getType())) {
							terms.add(t);
						}
					}
				}
			}
		} catch (IteratorException e) {
			throw new AtomSetException(e);
		}
		return new CloseableIteratorAdapter<Term>(terms.iterator());
	}

	@Override
	public RdbmsConjunctiveQueryTranslator getConjunctiveQueryTranslator() {
		return translator;
	}

	@Override
	public boolean check(Atom a) throws AtomSetException {
		return this.check(a.getPredicate());
	}

	@Override
	public boolean check(Predicate p) throws AtomSetException {
		try {
			Object identifier = p.getIdentifier();
			if (identifier instanceof String) {
				return this.getDriver().checkIdentifierName((String) identifier);
			} else {
				return false;
			}
		} catch (SQLException e) {
			throw new AtomSetException(e);
		}
	}

	@Override
	public BatchProcessor createBatchProcessor() throws AtomSetException {
		return new NaturalBatchProcessor(this);
	}

	// /////////////////////////////////////////////////////////////////////////
	// PROTECTED AND PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	protected Statement add(Statement statement, Atom atom) throws AtomSetException {
		if (!this.check(atom)) {
			throw new UnsupportedAtomTypeException(""); // FIXME say why
		}
		try {
			DBTable table = this.createPredicateTableIfNotExist(atom.getPredicate());
			Iterator<DBColumn> cols = table.getColumns().iterator();

			Map<String, String> data = new TreeMap<String, String>();
			for (Term t : atom.getTerms()) {
				DBColumn col = cols.next();
				data.put(col.getName(), this.getConjunctiveQueryTranslator().formatFromColumnType(col, t));
			}
			String query = this.getDriver().getInsertOrIgnoreQuery(table, data);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(atom.toString() + " : " + query);
			}
			statement.addBatch(query);
		} catch (SQLException e) {
			throw new AtomSetException(e);
		}
		return statement;
	}

	@Override
	protected boolean testDatabaseSchema() throws AtomSetException {
		// no specific schema expected
		return true;
	}

	@Override
	protected void createDatabaseSchema() throws AtomSetException {
		// no specific schema expected
	}

	@Override
	protected DBTable getPredicateTable(Predicate p) throws AtomSetException {
		return super.getPredicateTable(p); // for package visibility
	}

	@Override
	protected DBTable createPredicateTable(Predicate predicate) throws AtomSetException {
		return super.createPredicateTable(predicate); // for package visibility
	}

	@Override
	protected DBTable getPredicateTableIfExist(Predicate predicate) throws AtomSetException {
		try {
			String tableName = predicate.getIdentifier().toString();
			DBTable table = this.getDriver().getTable(tableName);
			if (table != null && predicate.getArity() == table.getColumns().size()) {
				return table;
			} else {
				return null;
			}
		} catch (SQLException e) {
			throw new AtomSetException(e);
		}
	}


	@Override
	protected String getFreshPredicateTableName(Predicate predicate) throws AtomSetException {
		try {
			return this.getDriver().formatIdentifier(predicate.getIdentifier().toString());
		} catch (SQLException e) {
			throw new AtomSetException(e);
		}
	}

}
