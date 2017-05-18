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
package fr.lirmm.graphik.graal.homomorphism.utils;

import java.util.Map;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.RulesCompilation;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.graal.homomorphism.Var;
import fr.lirmm.graphik.util.profiler.Profilable;
import fr.lirmm.graphik.util.profiler.Profiler;
import fr.lirmm.graphik.util.stream.AbstractCloseableIterator;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.IteratorException;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class HomomorphismIteratorChecker extends AbstractCloseableIterator<Term> implements Profilable {

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	private CloseableIterator<Term>	it;
	private Term					next;
	private Var						var;
	private Iterable<Atom>			h;
	private AtomSet					g;
	private Map<Variable, Integer>	map;
	private RulesCompilation		rc;
	private Profiler				profiler;
	private Substitution			initialSubstitution;
	private Var[]					varData;

	/**
	 * Check over it, the images for var such that there exists an homomorphism
	 * from h to g.
	 */
	public HomomorphismIteratorChecker(Var var, CloseableIterator<Term> it, Iterable<Atom> h, AtomSet g,
	    Substitution initialSubstitution, Map<Variable, Integer> map, Var[] varData, RulesCompilation rc) {
		this.var = var;
		this.it = it;
		this.h = h;
		this.g = g;
		this.map = map;
		this.varData = varData;
		this.rc = rc;
		this.initialSubstitution = initialSubstitution;
	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public boolean hasNext() throws IteratorException {
		try {
			while (this.next == null && this.it.hasNext()) {
				Term t = this.it.next();
				if (this.check(t, varData)) {
					this.next = t;
				}
			}
		} catch (AtomSetException e) {
			throw new IteratorException(e);
		}
		return this.next != null;
	}

	@Override
	public Term next() throws IteratorException {
		this.hasNext();
		Term t = this.next;
		this.next = null;
		return t;
	}

	@Override
	public void close() {
		this.it.close();
	}

	@Override
	public void setProfiler(Profiler profiler) {
		this.profiler = profiler;
	}

	@Override
	public Profiler getProfiler() {
		return this.profiler;
	}

	// /////////////////////////////////////////////////////////////////////////
	// OBJECT OVERRIDE METHODS
	// /////////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

	private boolean check(Term t, Var[] varData) throws AtomSetException {
		this.var.image = t;
		Profiler profiler = this.getProfiler();
		if (profiler != null) {
			profiler.incr("#isHomomorphism", 1);
			profiler.start("isHomomorphismTime");
		}
		boolean res = BacktrackUtils.isHomomorphism(h, g, initialSubstitution, map, varData, rc);
		if (profiler != null) {
			profiler.stop("isHomomorphismTime");
		}
		return res;
	}

}
