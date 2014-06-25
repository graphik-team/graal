/**
 * 
 */
package fr.lirmm.graphik.graal.store.rdbms;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.SubstitutionReader2AtomReader;
import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.DefaultConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.solver.Solver;
import fr.lirmm.graphik.graal.solver.SolverException;
import fr.lirmm.graphik.graal.solver.SqlSolver;
import fr.lirmm.graphik.graal.store.StoreException;
import fr.lirmm.graphik.util.stream.AbstractReader;
import fr.lirmm.graphik.util.stream.ObjectReader;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
class DefaultRdbmsIterator extends AbstractReader<Atom> {

	private static final Logger logger = LoggerFactory
			.getLogger(DefaultRdbmsIterator.class);
	
	private DefaultRdbmsStore store;
	private boolean hasNextCallDone = false;
	private ObjectReader<Predicate> predicateStream;
	private ObjectReader<Atom> atomReader;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////

	DefaultRdbmsIterator(DefaultRdbmsStore store) throws StoreException {
		this.store = store;
		this.init();
	}

	private void init() throws StoreException {
		this.predicateStream = store.getAllPredicate();
	}

	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.lirmm.graphik.util.stream.ObjectReader#hasNext()
	 */
	@Override
	public boolean hasNext() {
		if (!this.hasNextCallDone) {
			this.hasNextCallDone = true;
			while (this.predicateStream.hasNext()
				   && (this.atomReader == null || !this.atomReader.hasNext())) {
				Predicate p = predicateStream.next();
				List<Term> terms = new LinkedList<Term>();
				for(int i=0; i<p.getArity(); ++i) {
					terms.add(new Term("X"+i, Term.Type.VARIABLE));
				}
				
				AtomSet atomSet = new LinkedListAtomSet();
				Atom atom = new DefaultAtom(p, terms);
				atomSet.add(atom);
				
				DefaultConjunctiveQuery query = new DefaultConjunctiveQuery(atomSet);
				
				Solver solver = new SqlSolver(query, this.store);
				try {
					this.atomReader = new SubstitutionReader2AtomReader(atom, solver.execute());
				} catch (SolverException e) {
					logger.error(e.getMessage(), e);
					return false;
				}
			}
		}
		return this.atomReader != null && this.atomReader.hasNext();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.lirmm.graphik.util.stream.ObjectReader#next()
	 */
	@Override
	public Atom next() {
		if (!this.hasNextCallDone)
			this.hasNext();
		this.hasNextCallDone = false;

		return this.atomReader.next();
	}

}
