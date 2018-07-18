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
package fr.lirmm.graphik.graal.homomorphism;

import java.util.SortedSet;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.RulesCompilation;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.homomorphism.Homomorphism;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismChecker;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismWithCompilation;
import fr.lirmm.graphik.graal.core.compilation.NoCompilation;
import fr.lirmm.graphik.graal.homomorphism.checker.AtomicQueryHomomorphismChecker;
import fr.lirmm.graphik.graal.homomorphism.checker.AtomicQueryHomomorphismWithNegatedPartsChecker;
import fr.lirmm.graphik.graal.homomorphism.checker.BacktrackChecker;
import fr.lirmm.graphik.graal.homomorphism.checker.BacktrackWithNegatedPartsChecker;
import fr.lirmm.graphik.graal.homomorphism.checker.DefaultUnionConjunctiveQueriesChecker;
import fr.lirmm.graphik.graal.homomorphism.checker.FullyInstantiatedQueryHomomorphismChecker;
import fr.lirmm.graphik.util.profiler.AbstractProfilable;
import fr.lirmm.graphik.util.stream.CloseableIterator;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
public class SmartHomomorphism extends AbstractProfilable implements HomomorphismWithCompilation<Object, AtomSet> {

	private static final Logger LOGGER = LoggerFactory.getLogger(SmartHomomorphism.class);

	private SortedSet<HomomorphismChecker> elements;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////

	private static SmartHomomorphism instance;

	public SmartHomomorphism(boolean loadDefaultHomomorphism) {
		this.elements = new TreeSet<HomomorphismChecker>();
		if (loadDefaultHomomorphism) {
			this.elements.add(BacktrackChecker.instance());
			this.elements.add(DefaultUnionConjunctiveQueriesChecker.instance());
			this.elements.add(FullyInstantiatedQueryHomomorphismChecker.instance());
			this.elements.add(AtomicQueryHomomorphismChecker.instance());
			this.elements.add(BacktrackWithNegatedPartsChecker.instance());
			this.elements.add(AtomicQueryHomomorphismWithNegatedPartsChecker.instance());
		}
	}

	public static synchronized SmartHomomorphism instance() {
		if (instance == null)
			instance = new SmartHomomorphism(true);

		return instance;
	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * 
	 * @param checker
	 * @return true if this checker is not already added, false otherwise.
	 */
	public boolean addChecker(HomomorphismChecker checker) {
		return this.elements.add(checker);
	}

	@Override
	public CloseableIterator<Substitution> execute(Object query, AtomSet atomSet) throws HomomorphismException {
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Execute query: {}", query);

		for (HomomorphismChecker e : elements) {
			if (e.check(query, atomSet)) {
				@SuppressWarnings("unchecked")
				Homomorphism<Object, AtomSet> solver = (Homomorphism<Object, AtomSet>) e.getSolver();
				if (LOGGER.isDebugEnabled())
					LOGGER.debug("Solver: {}", solver.getClass());
				return solver.execute(query, atomSet);
			}
		}
		throw new HomomorphismException("Solver not found");
	}

	@Override
	public CloseableIterator<Substitution> execute(Object query, AtomSet atomSet, Substitution s)
	    throws HomomorphismException {
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Execute query: {}", query);

		for (HomomorphismChecker e : elements) {
			if (e.check(query, atomSet)) {
				@SuppressWarnings("unchecked")
				Homomorphism<Object, AtomSet> solver = (Homomorphism<Object, AtomSet>) e.getSolver();
				if (LOGGER.isDebugEnabled())
					LOGGER.debug("Solver: {}", solver.getClass());
				return solver.execute(query, atomSet, s);
			}
		}
		throw new HomomorphismException("Solver not found");
	}

	@Override
	public boolean exist(Object query, AtomSet atomSet) throws HomomorphismException {
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Exist query: {}", query);

		for (HomomorphismChecker e : elements) {
			if (e.check(query, atomSet)) {
				@SuppressWarnings("unchecked")
				Homomorphism<Object, AtomSet> solver = (Homomorphism<Object, AtomSet>) e.getSolver();
				if (LOGGER.isDebugEnabled())
					LOGGER.debug("Solver: {}", solver.getClass());
				return solver.exist(query, atomSet);
			}
		}
		throw new HomomorphismException("Solver not found");
	}

	@Override
	public boolean exist(Object query, AtomSet atomSet, Substitution s) throws HomomorphismException {
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Exist query: {}", query);

		for (HomomorphismChecker e : elements) {
			if (e.check(query, atomSet)) {
				@SuppressWarnings("unchecked")
				Homomorphism<Object, AtomSet> solver = (Homomorphism<Object, AtomSet>) e.getSolver();
				if (LOGGER.isDebugEnabled())
					LOGGER.debug("Solver: {}", solver.getClass());
				return solver.exist(query, atomSet, s);
			}
		}
		throw new HomomorphismException("Solver not found");
	}

	@Override
	public CloseableIterator<Substitution> execute(Object query, AtomSet atomSet, RulesCompilation compilation)
	    throws HomomorphismException {
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Execute query with compilation: {}", query);

		// is there really a compilation?
		if (compilation == null || compilation == NoCompilation.instance()) {
			return this.execute(query, atomSet);
		}

		for (HomomorphismChecker e : elements) {
			if (e.getSolver() instanceof HomomorphismWithCompilation && e.check(query, atomSet)) {
				@SuppressWarnings("unchecked")
				HomomorphismWithCompilation<Object, AtomSet> solver = (HomomorphismWithCompilation<Object, AtomSet>) e.getSolver();
				if (LOGGER.isDebugEnabled())
					LOGGER.debug("Solver: {}", solver.getClass());
				return solver.execute(query, atomSet, compilation);
			}
		}
		throw new HomomorphismException("Solver not found");
	}

	@Override
	public CloseableIterator<Substitution> execute(Object query, AtomSet atomSet, RulesCompilation compilation,
	    Substitution s)
	    throws HomomorphismException {
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Execute query with compilation: {}", query);

		// is there really a compilation?
		if (compilation == null || compilation == NoCompilation.instance()) {
			return this.execute(query, atomSet);
		}

		for (HomomorphismChecker e : elements) {
			if (e.getSolver() instanceof HomomorphismWithCompilation && e.check(query, atomSet)) {
				@SuppressWarnings("unchecked")
				HomomorphismWithCompilation<Object, AtomSet> solver = (HomomorphismWithCompilation<Object, AtomSet>) e.getSolver();
				if (LOGGER.isDebugEnabled())
					LOGGER.debug("Solver: {}", solver.getClass());
				return solver.execute(query, atomSet, compilation, s);
			}
		}
		throw new HomomorphismException("Solver not found");
	}

	@Override
	public boolean exist(Object query, AtomSet atomSet, RulesCompilation compilation) throws HomomorphismException {
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Exist query with compilation: {}", query);

		// is there really a compilation?
		if (compilation == null || compilation == NoCompilation.instance()) {
			return this.exist(query, atomSet);
		}

		for (HomomorphismChecker e : elements) {
			if (e.getSolver() instanceof HomomorphismWithCompilation && e.check(query, atomSet)) {
				@SuppressWarnings("unchecked")
				HomomorphismWithCompilation<Object, AtomSet> solver = (HomomorphismWithCompilation<Object, AtomSet>) e.getSolver();
				if (LOGGER.isDebugEnabled())
					LOGGER.debug("Solver: {}", solver.getClass());
				return solver.exist(query, atomSet, compilation);
			}
		}
		throw new HomomorphismException("Solver not found");
	}

	@Override
	public boolean exist(Object query, AtomSet atomSet, RulesCompilation compilation, Substitution s)
	    throws HomomorphismException {
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Exist query with compilation: {}", query);

		// is there really a compilation?
		if (compilation == null || compilation == NoCompilation.instance()) {
			return this.exist(query, atomSet);
		}

		for (HomomorphismChecker e : elements) {
			if (e.getSolver() instanceof HomomorphismWithCompilation && e.check(query, atomSet)) {
				@SuppressWarnings("unchecked")
				HomomorphismWithCompilation<Object, AtomSet> solver = (HomomorphismWithCompilation<Object, AtomSet>) e.getSolver();
				if (LOGGER.isDebugEnabled())
					LOGGER.debug("Solver: {}", solver.getClass());
				return solver.exist(query, atomSet, compilation, s);
			}
		}
		throw new HomomorphismException("Solver not found");
	}
}
