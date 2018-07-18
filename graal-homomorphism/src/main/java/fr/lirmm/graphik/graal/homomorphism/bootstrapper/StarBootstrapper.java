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
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.tuple.Pair;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.Constant;
import fr.lirmm.graphik.graal.api.core.RulesCompilation;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.homomorphism.BacktrackException;
import fr.lirmm.graphik.graal.homomorphism.VarSharedData;
import fr.lirmm.graphik.util.profiler.AbstractProfilable;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.CloseableIteratorAdapter;
import fr.lirmm.graphik.util.stream.IteratorException;

/**
 * This implementation uses the star query around the variable v to restrict the set of candidates.
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class StarBootstrapper extends AbstractProfilable implements Bootstrapper {

	private static StarBootstrapper instance;

	protected StarBootstrapper() {
		super();
	}

	public static synchronized StarBootstrapper instance() {
		if (instance == null)
			instance = new StarBootstrapper();

		return instance;
	}

	// /////////////////////////////////////////////////////////////////////////
	//
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public CloseableIterator<Term> exec(final VarSharedData v, Collection<Atom> preAtoms, Collection<Atom> postAtoms, final AtomSet data,
	    RulesCompilation compilation) throws BacktrackException {
		Set<Term> terms = null;
		
		if(this.getProfiler() != null) {
			this.getProfiler().start("BootstrapTime");
			this.getProfiler().start("BootstrapTimeFirstPart");
		}
		Iterator<Atom> it;

		Collection<Constant> constants = null;
		Atom aa = null;
		it = postAtoms.iterator();
		while (it.hasNext()) {
			Atom a = it.next();
			if (constants == null || constants.isEmpty()) {
				constants = a.getConstants();
				aa = a;
			}
		}
		it = preAtoms.iterator();
		while (it.hasNext()) {
			Atom a = it.next();
			if (constants == null || constants.isEmpty()) {
				constants = a.getConstants();
				aa = a;
			}
		}
		
		try {
			if (constants != null && !constants.isEmpty()) {
				terms = new TreeSet<Term>();
				for (Pair<Atom, Substitution> im : compilation.getRewritingOf(aa)) {
					int pos = im.getLeft().indexOf(im.getRight().createImageOf(v.value));
					CloseableIterator<Atom> match = data.match(im.getLeft());
					while (match.hasNext()) {
						terms.add(match.next().getTerm(pos));
					}
				}
			}

			if (this.getProfiler() != null) {
				this.getProfiler().stop("BootstrapTimeFirstPart");
			}

			if (terms == null) {
				it = postAtoms.iterator();
				while (it.hasNext()) {
					if (terms == null) {
						terms = BootstrapperUtils.computeCandidatesOverRewritings(it.next(), v, data, compilation);
					} else {
						terms.retainAll(
						    BootstrapperUtils.computeCandidatesOverRewritings(it.next(), v, data, compilation));
					}
				}

				it = preAtoms.iterator();
				while (it.hasNext()) {
					if (terms == null) {
						terms = BootstrapperUtils.computeCandidatesOverRewritings(it.next(), v, data, compilation);
					} else {
						terms.retainAll(
						    BootstrapperUtils.computeCandidatesOverRewritings(it.next(), v, data, compilation));
					}
				}
			}

			if (this.getProfiler() != null) {
				this.getProfiler().stop("BootstrapTime");
			}

			if (terms == null) {
				return data.termsIterator();
			} else {
				return new CloseableIteratorAdapter<Term>(terms.iterator());
			}
		} catch (AtomSetException e) {
			throw new BacktrackException(e);
		} catch (IteratorException e) {
			throw new BacktrackException(e);
		}
	}



}
