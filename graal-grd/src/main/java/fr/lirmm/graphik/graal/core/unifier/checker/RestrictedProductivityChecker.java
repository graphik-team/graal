package fr.lirmm.graphik.graal.core.unifier.checker;
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

import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.graal.api.core.unifier.DependencyChecker;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.core.HashMapSubstitution;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.homomorphism.PureHomomorphism;

public class RestrictedProductivityChecker implements DependencyChecker {

	// /////////////////////////////////////////////////////////////////////////
	// SINGLETON
	// /////////////////////////////////////////////////////////////////////////

	private static RestrictedProductivityChecker instance;

	protected RestrictedProductivityChecker() {
		super();
	}

	public static synchronized RestrictedProductivityChecker instance() {
		if (instance == null)
			instance = new RestrictedProductivityChecker();

		return instance;
	}

	// /////////////////////////////////////////////////////////////////////////
	//
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public boolean isValidDependency(Rule r1, Rule r2, Substitution s) {
		Substitution sub = new HashMapSubstitution(s);
		for(Variable v : r2.getFrontier()) {
			if(!sub.getTerms().contains(v)) {
				sub.put(v, v);
			}
		}
		InMemoryAtomSet b1 = sub.createImageOf(r1.getBody());
		InMemoryAtomSet h1 = sub.createImageOf(r1.getHead());
		InMemoryAtomSet b2 = sub.createImageOf(r2.getBody());

		InMemoryAtomSet f = new LinkedListAtomSet();
		f.addAll(b1.iterator());
		f.addAll(h1.iterator());
		f.addAll(b2.iterator());

		try {
			return !PureHomomorphism.instance().exist(r2.getHead(), f, sub);
		} catch (HomomorphismException e) {
			// TODO treat this exception
			e.printStackTrace();
			throw new Error("Untreated exception");
		}
	}

};
