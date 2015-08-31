/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2015)
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
package fr.lirmm.graphik.graal.core;

import java.util.Arrays;

import fr.lirmm.graphik.graal.core.impl.DefaultAtom;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.core.term.Term;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public final class TestUtils {

	public static Predicate p = new Predicate("p", 2);
	public static Predicate q = new Predicate("q", 1);
	public static Predicate r = new Predicate("r", 1);

	public static final Term X = DefaultTermFactory.instance().createVariable("X");
	public static final Term Y = DefaultTermFactory.instance().createVariable("Y");
	public static final Term Z = DefaultTermFactory.instance().createVariable("Z");
	public static final Term U = DefaultTermFactory.instance().createVariable("U");
	public static final Term V = DefaultTermFactory.instance().createVariable("V");
	public static final Term W = DefaultTermFactory.instance().createVariable("w");

	public static final Term A = DefaultTermFactory.instance().createConstant("a");
	public static final Term B = DefaultTermFactory.instance().createConstant("b");

	public static Atom pXY, pYZ, pUV, pVW, pAU, pXA, pXB;
	public static Atom qX, rX;

	static {
		Term[] terms = new Term[2];
		terms[0] = X;
		terms[1] = Y;
		pXY = new DefaultAtom(p, Arrays.asList(terms));

		terms = new Term[2];
		terms[0] = Y;
		terms[1] = Z;
		pYZ = new DefaultAtom(p, Arrays.asList(terms));

		terms = new Term[2];
		terms[0] = U;
		terms[1] = V;
		pUV = new DefaultAtom(p, Arrays.asList(terms));

		terms = new Term[2];
		terms[0] = V;
		terms[1] = W;
		pVW = new DefaultAtom(p, Arrays.asList(terms));

		terms = new Term[2];
		terms[0] = A;
		terms[1] = U;
		pAU = new DefaultAtom(p, Arrays.asList(terms));

		terms = new Term[2];
		terms[0] = X;
		terms[1] = A;
		pXA = new DefaultAtom(p, Arrays.asList(terms));

		terms = new Term[2];
		terms[0] = X;
		terms[1] = B;
		pXB = new DefaultAtom(p, Arrays.asList(terms));

		terms = new Term[1];
		terms[0] = X;
		qX = new DefaultAtom(q, Arrays.asList(terms));

		terms = new Term[1];
		terms[0] = X;
		rX = new DefaultAtom(r, Arrays.asList(terms));
	}

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	private TestUtils() {
	}

}
