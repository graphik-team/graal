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
package fr.lirmm.graphik.graal.homomorphism.bootstrapper;

import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.lang3.tuple.Pair;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.RulesCompilation;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.homomorphism.BacktrackException;
import fr.lirmm.graphik.graal.homomorphism.VarSharedData;
import fr.lirmm.graphik.util.profiler.AbstractProfilable;
import fr.lirmm.graphik.util.stream.AbstractCloseableIterator;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.CloseableIteratorAggregator;
import fr.lirmm.graphik.util.stream.IteratorException;

/**
 * This implementation uses an randomly selected atom containing the variable v to
 * restrict the set of candidates.
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class DefaultBootstrapper extends AbstractProfilable implements Bootstrapper {

	private static DefaultBootstrapper instance;

	protected DefaultBootstrapper() {
		super();
	}

	public static synchronized DefaultBootstrapper instance() {
		if (instance == null)
			instance = new DefaultBootstrapper();

		return instance;
	}

	// /////////////////////////////////////////////////////////////////////////
	//
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public CloseableIterator<Term> exec(final VarSharedData v, Collection<Atom> preAtoms, Collection<Atom> postAtoms, final AtomSet data,
	    RulesCompilation compilation) throws BacktrackException {
		Iterator<Atom> it = postAtoms.iterator();
		if (it.hasNext()) {
			Atom a = it.next();
			final Iterator<Pair<Atom, Substitution>> rewritingOf = compilation.getRewritingOf(a).iterator();

			// TODO refactor the following code using converter Iterator or
			// create a private class?
			CloseableIterator<CloseableIterator<Term>> metaIt = new AbstractCloseableIterator<CloseableIterator<Term>>() {

				CloseableIterator<Term> next = null;

				@Override
				public void close() {
					if (next != null)
						this.next.close();
				}

				@Override
				public boolean hasNext() throws IteratorException {
					try {
						if (next == null && rewritingOf.hasNext()) {
							Pair<Atom, Substitution> rew = rewritingOf.next();
							Atom im = rew.getLeft();
							Predicate predicate = im.getPredicate();
							int pos = im.indexOf(rew.getRight().createImageOf(v.value));
							next = data.termsByPredicatePosition(predicate, pos);
						}
					} catch (AtomSetException e) {
						throw new IteratorException("An errors occurs while getting terms by predicate position", e);
					}

					return next != null;
				}

				@Override
				public CloseableIterator<Term> next() throws IteratorException {
					if (next == null)
						this.hasNext();

					CloseableIterator<Term> ret = next;
					next = null;
					return ret;
				}

			};

			return new CloseableIteratorAggregator<Term>(metaIt);
		} else {
			try {
				return data.termsIterator();
			} catch (AtomSetException e) {
				throw new BacktrackException(e);
			}
		}
	}

}
