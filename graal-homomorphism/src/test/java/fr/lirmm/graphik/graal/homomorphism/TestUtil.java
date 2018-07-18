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

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.homomorphism.Homomorphism;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismWithCompilation;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.homomorphism.bbc.BCC;
import fr.lirmm.graphik.graal.homomorphism.bootstrapper.StarBootstrapper;
import fr.lirmm.graphik.graal.homomorphism.forward_checking.NFC0;
import fr.lirmm.graphik.graal.homomorphism.forward_checking.NFC2;
import fr.lirmm.graphik.graal.homomorphism.forward_checking.SimpleFC;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
final class TestUtil {

	private TestUtil() {
	}

	@SuppressWarnings("rawtypes")
	public static HomomorphismWithCompilation[] getHomomorphismsWithCompilation() {

		BCC bcc = new BCC();

		return new HomomorphismWithCompilation[] { new BacktrackHomomorphism(),
		        new BacktrackHomomorphism(bcc.getBCCScheduler(), bcc.getBCCBackJumping()),
		        new BacktrackHomomorphism(new NFC0()), new BacktrackHomomorphism(new NFC2()),
		        new BacktrackHomomorphism(bcc.getBCCScheduler(), StarBootstrapper.instance(), new NFC2(),
		                                  bcc.getBCCBackJumping()) };

	}

	@SuppressWarnings({ "rawtypes", "deprecation" })
	public static Homomorphism[] getHomomorphisms() {

		BCC bcc = new BCC();

		return new Homomorphism[] {
		        new BacktrackHomomorphism(),
		        new BacktrackHomomorphism(bcc.getBCCScheduler(), bcc.getBCCBackJumping()),
		        new BacktrackHomomorphism(new NFC0()), new BacktrackHomomorphism(new NFC2()),
		        new BacktrackHomomorphism(new SimpleFC()),
		        new BacktrackHomomorphism(bcc.getBCCScheduler(), StarBootstrapper.instance(), new SimpleFC(),
		                                  bcc.getBCCBackJumping()),
		        new BacktrackHomomorphism(bcc.getBCCScheduler(), StarBootstrapper.instance(), new NFC2(),
		                                  bcc.getBCCBackJumping()) };

	}
	
	public static void addNAtoms(InMemoryAtomSet to, int n, Predicate[] predicates, int domainSize, Random rand) {
		for (int i = 0; i < n; ++i) {
			int p = rand.nextInt(predicates.length);
			List<Term> terms = new LinkedList<Term>();
			for (int j = 0; j < predicates[p].getArity(); ++j) {
				terms.add(DefaultTermFactory.instance().createConstant(rand.nextInt(domainSize)));
			}
			to.add(new DefaultAtom(predicates[p], terms));
		}
	}

}
