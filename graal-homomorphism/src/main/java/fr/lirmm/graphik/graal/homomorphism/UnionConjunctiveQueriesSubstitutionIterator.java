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

import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.RulesCompilation;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.UnionOfConjunctiveQueries;
import fr.lirmm.graphik.graal.api.homomorphism.Homomorphism;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismWithCompilation;
import fr.lirmm.graphik.graal.core.Substitutions;
import fr.lirmm.graphik.graal.core.compilation.NoCompilation;
import fr.lirmm.graphik.util.profiler.AbstractProfilable;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.IteratorException;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
class UnionConjunctiveQueriesSubstitutionIterator extends AbstractProfilable implements
                                                                                   CloseableIterator<Substitution> {
	
	private AtomSet                                 atomSet;
	private CloseableIterator<ConjunctiveQuery>     cqueryIterator;
	private CloseableIterator<Substitution>         tmpIt;
	private boolean                                 hasNextCallDone = false;
	private Homomorphism<ConjunctiveQuery, AtomSet> homomorphism;
	private RulesCompilation                        compilation;
	private Substitution                            initialSubstitution;

	private int                                     i = 1;

	private boolean isBooleanQuery;

	public UnionConjunctiveQueriesSubstitutionIterator(UnionOfConjunctiveQueries queries, AtomSet atomSet) {
		this(queries, atomSet, Substitutions.emptySubstitution(), null, null);
	}

	public UnionConjunctiveQueriesSubstitutionIterator(UnionOfConjunctiveQueries queries, AtomSet atomSet,
	    Substitution s, Homomorphism<ConjunctiveQuery, AtomSet> homomorphism) {
		this.cqueryIterator = queries.iterator();
		this.isBooleanQuery = queries.isBoolean();
		this.atomSet = atomSet;
		this.tmpIt = null;
		this.initialSubstitution = s;
		this.homomorphism = homomorphism;
		this.compilation = null;
	}

	public UnionConjunctiveQueriesSubstitutionIterator(UnionOfConjunctiveQueries queries, AtomSet atomSet,
	    Substitution s, HomomorphismWithCompilation<ConjunctiveQuery, AtomSet> homomorphism, RulesCompilation rc) {
		this.cqueryIterator = queries.iterator();
		this.isBooleanQuery = queries.isBoolean();
		this.atomSet = atomSet;
		this.tmpIt = null;
		this.initialSubstitution = s;
		this.homomorphism = homomorphism;
		this.compilation = rc;
	}

	@Override
	public boolean hasNext() throws IteratorException {
		if (!this.hasNextCallDone) {
			this.hasNextCallDone = true;
			
			if (this.tmpIt != null && !this.tmpIt.hasNext()) {
				this.tmpIt.close();
				this.tmpIt = null;
				this.getProfiler().stop("SubQuery" + i++);
			}
			while ((this.tmpIt == null || !this.tmpIt.hasNext()) && this.cqueryIterator.hasNext()) {
				ConjunctiveQuery q = this.cqueryIterator.next();
				this.getProfiler().start("SubQuery" + i);
				try {
					if(this.homomorphism == null) {
						this.tmpIt = SmartHomomorphism.instance().execute(q, this.atomSet, this.compilation, this.initialSubstitution);
					} else {
						if(this.compilation != null && !(this.compilation instanceof NoCompilation)) {
							if(this.homomorphism instanceof HomomorphismWithCompilation) {
								this.tmpIt = ((HomomorphismWithCompilation<ConjunctiveQuery,AtomSet>) this.homomorphism).execute(q, this.atomSet, this.compilation, this.initialSubstitution);
							} else {
								throw new IteratorException("There is a compilation and selected homomorphism can't handle it : " + this.homomorphism.getClass());
							}
						} else {
							this.tmpIt = this.homomorphism.execute(q, this.atomSet, this.initialSubstitution);
						}
					}
					if (this.isBooleanQuery && this.tmpIt.hasNext()) {
						this.cqueryIterator.close();
					}

				} catch (HomomorphismException e) {
					return false;
				}
			}
		}
		return this.tmpIt != null && this.tmpIt.hasNext();
	}

	@Override
	public Substitution next() throws IteratorException {
		if (!this.hasNextCallDone)
			this.hasNext();

		this.hasNextCallDone = false;

		return this.tmpIt.next();
	}
	
	@Override
	protected void finalize() throws Throwable {
		this.close();
		super.finalize();
	}

	@Override
	public void close() {
		if (this.tmpIt != null) {
			this.tmpIt.close();
		}
		if (this.cqueryIterator != null) {
			this.cqueryIterator.close();
		}
	}

}
