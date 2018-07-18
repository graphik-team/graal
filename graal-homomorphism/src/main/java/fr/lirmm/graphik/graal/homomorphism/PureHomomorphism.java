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
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.RulesCompilation;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.homomorphism.ExistentialHomomorphismWithCompilation;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.core.Substitutions;
import fr.lirmm.graphik.graal.core.compilation.NoCompilation;
import fr.lirmm.graphik.util.profiler.AbstractProfilable;

/**
 * A simple implementation of an algorithm to find if there exist an
 * homomorphism between two facts Backtrack algorithm that look for an
 * association of atoms that correspond to a substitution of terms efficient for
 * simple facts of small size
 * 
 * @author Mélanie KÖNIG
 * 
 */
public class PureHomomorphism extends AbstractProfilable
		implements ExistentialHomomorphismWithCompilation<InMemoryAtomSet, AtomSet> {

	private static PureHomomorphism instance;

	protected PureHomomorphism() {
	}

	public static synchronized PureHomomorphism instance() {
		if (instance == null)
			instance = new PureHomomorphism();

		return instance;
	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public boolean exist(InMemoryAtomSet source, AtomSet target, RulesCompilation compilation, Substitution s)
			throws HomomorphismException {

		PureHomomorphismImpl homomorphism = new PureHomomorphismImpl(source, target, compilation, s);
		homomorphism.setProfiler(this.getProfiler());
		return homomorphism.exist();
	}

	@Override
	public boolean exist(InMemoryAtomSet q, AtomSet a) throws HomomorphismException {
		return this.exist(q, a, NoCompilation.instance(), Substitutions.emptySubstitution());
	}

	@Override
	public boolean exist(InMemoryAtomSet q, AtomSet a, Substitution s) throws HomomorphismException {
		return this.exist(q, a, NoCompilation.instance(), s);

	}

	@Override
	public boolean exist(InMemoryAtomSet q, AtomSet a, RulesCompilation compilation) throws HomomorphismException {
		return this.exist(q, a, compilation, Substitutions.emptySubstitution());
	}

}
