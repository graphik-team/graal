/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2016)
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

import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.Query;
import fr.lirmm.graphik.graal.api.core.RulesCompilation;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.UnionOfConjunctiveQueries;
import fr.lirmm.graphik.graal.api.homomorphism.Homomorphism;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismWithCompilation;
import fr.lirmm.graphik.util.AbstractProfilable;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.GIterator;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class UnionConjunctiveQueriesSubstitutionIterator extends AbstractProfilable implements
                                                                                   CloseableIterator<Substitution> {

	private AtomSet                                 atomSet;
	private GIterator<ConjunctiveQuery>             cqueryIterator;
	private CloseableIterator<Substitution>         tmpIt;
	private boolean                                 hasNextCallDone = false;
	private Homomorphism<ConjunctiveQuery, AtomSet> homomorphism;
	private RulesCompilation                        compilation;
	private int                                     i               = 0;

	public UnionConjunctiveQueriesSubstitutionIterator(UnionOfConjunctiveQueries queries, AtomSet atomSet) {
		this(queries, atomSet, null, null);
	}

	public UnionConjunctiveQueriesSubstitutionIterator(UnionOfConjunctiveQueries queries, AtomSet atomSet,
	    Homomorphism<ConjunctiveQuery, AtomSet> homomorphism) {
		this(queries, atomSet, homomorphism, null);
	}

	public UnionConjunctiveQueriesSubstitutionIterator(UnionOfConjunctiveQueries queries, AtomSet atomSet,
	    Homomorphism<ConjunctiveQuery, AtomSet> homomorphism, RulesCompilation rc) {
		this.cqueryIterator = queries.iterator();
		this.atomSet = atomSet;
		this.tmpIt = null;
		this.homomorphism = homomorphism;
		this.compilation = rc;
	}

	@Override
	public boolean hasNext() {
		if (!this.hasNextCallDone) {
			this.hasNextCallDone = true;

			if (this.tmpIt != null && !this.tmpIt.hasNext()) {
				this.tmpIt.close();
				this.tmpIt = null;
				this.getProfiler().stop("SubQuery" + i++);
			}
			while (this.tmpIt == null && this.cqueryIterator.hasNext()) {
				Query q = this.cqueryIterator.next();
				this.getProfiler().start("SubQuery" + i);
				Homomorphism solver = this.homomorphism;
				try {
					if (solver == null) {
						solver = DefaultHomomorphismFactory.instance().getSolver(q, this.atomSet);
						if (solver == null) {
							throw new Error("Solver not found.");
						}
					}
					solver.setProfiler(this.getProfiler());

					if (this.compilation != null && solver instanceof HomomorphismWithCompilation) {
						this.tmpIt = ((HomomorphismWithCompilation) solver).execute(q, this.atomSet, this.compilation);
					} else {
						this.tmpIt = solver.execute(q, this.atomSet);
					}

				} catch (HomomorphismException e) {
					return false;
				}
			}
		}
		return this.tmpIt != null && this.tmpIt.hasNext();
	}

	@Override
	public Substitution next() {
		if (!this.hasNextCallDone)
			this.hasNext();

		this.hasNextCallDone = false;

		return this.tmpIt.next();
	}

	@Override
	public void close() {
		if (this.tmpIt != null) {
			this.tmpIt.close();
		}
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
