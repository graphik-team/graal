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
package fr.lirmm.graphik.graal.core;

import java.util.Arrays;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.Constant;
import fr.lirmm.graphik.graal.api.core.Literal;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public final class TestUtils {

	public static final Predicate p = new Predicate("p", 2);
	public static final Predicate q = new Predicate("q", 2);
	public static final Predicate r = new Predicate("r", 1);
	public static final Predicate s = new Predicate("s", 1);

	public static final Variable T = DefaultTermFactory.instance().createVariable("T");
	public static final Variable U = DefaultTermFactory.instance().createVariable("U");
	public static final Variable V = DefaultTermFactory.instance().createVariable("V");
	public static final Variable W = DefaultTermFactory.instance().createVariable("W");
	public static final Variable X = DefaultTermFactory.instance().createVariable("X");
	public static final Variable Y = DefaultTermFactory.instance().createVariable("Y");
	public static final Variable Z = DefaultTermFactory.instance().createVariable("Z");

	public static final Constant A = DefaultTermFactory.instance().createConstant("a");
	public static final Constant B = DefaultTermFactory.instance().createConstant("b");
	
	public static final Literal ONE = DefaultTermFactory.instance().createLiteral(1);

	public static final Atom pAB, pBA;
	public static final Atom pTU, pUV, pVW, pWX, pXY, pYX, pYZ, pYW, pUU, pWV, pWT, pUW, pAU, pXX, pXZ;
	public static final Atom pXA, pXB, pXONE;
	
	public static final Atom qXY, qTW, qUV, qVW;
	public static final Atom sX, sU, sV, sY, rX;

	static {
		Term[] terms = new Term[2];
		terms[0] = A;
		terms[1] = B;
		pAB = new DefaultAtom(p, Arrays.asList(terms));
		
		terms = new Term[2];
		terms[0] = B;
		terms[1] = A;
		pBA = new DefaultAtom(p, Arrays.asList(terms));
		
		terms = new Term[2];
		terms[0] = T;
		terms[1] = U;
		pTU = new DefaultAtom(p, Arrays.asList(terms));
		
		terms = new Term[2];
		terms[0] = W;
		terms[1] = X;
		pWX = new DefaultAtom(p, Arrays.asList(terms));

		terms = new Term[2];
		terms[0] = X;
		terms[1] = Y;
		pXY = new DefaultAtom(p, Arrays.asList(terms));

		terms = new Term[2];
		terms[0] = Y;
		terms[1] = X;
		pYX = new DefaultAtom(p, Arrays.asList(terms));

		terms = new Term[2];
		terms[0] = Y;
		terms[1] = Z;
		pYZ = new DefaultAtom(p, Arrays.asList(terms));

		terms = new Term[2];
		terms[0] = Y;
		terms[1] = W;
		pYW = new DefaultAtom(p, Arrays.asList(terms));

		terms = new Term[2];
		terms[0] = U;
		terms[1] = U;
		pUU = new DefaultAtom(p, Arrays.asList(terms));

		terms = new Term[2];
		terms[0] = U;
		terms[1] = V;
		pUV = new DefaultAtom(p, Arrays.asList(terms));

		terms = new Term[2];
		terms[0] = V;
		terms[1] = W;
		pVW = new DefaultAtom(p, Arrays.asList(terms));
		
		terms = new Term[2];
		terms[0] = W;
		terms[1] = V;
		pWV = new DefaultAtom(p, Arrays.asList(terms));
		
		terms = new Term[2];
		terms[0] = W;
		terms[1] = T;
		pWT = new DefaultAtom(p, Arrays.asList(terms));
		
		terms = new Term[2];
		terms[0] = U;
		terms[1] = W;
		pUW = new DefaultAtom(p, Arrays.asList(terms));

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
		
		terms = new Term[2];
		terms[0] = X;
		terms[1] = X;
		pXX = new DefaultAtom(p, Arrays.asList(terms));
		
		terms = new Term[2];
		terms[0] = X;
		terms[1] = Z;
		pXZ = new DefaultAtom(p, Arrays.asList(terms));
		
		terms = new Term[2];
		terms[0] = X;
		terms[1] = ONE;
		pXONE = new DefaultAtom(p, Arrays.asList(terms));
		
		// q

		terms = new Term[2];
		terms[0] = X;
		terms[1] = Y;
		qXY = new DefaultAtom(q, Arrays.asList(terms));

		terms = new Term[2];
		terms[0] = U;
		terms[1] = V;
		qUV = new DefaultAtom(q, Arrays.asList(terms));

		terms = new Term[2];
		terms[0] = V;
		terms[1] = W;
		qVW = new DefaultAtom(q, Arrays.asList(terms));

		terms = new Term[2];
		terms[0] = T;
		terms[1] = W;
		qTW = new DefaultAtom(q, Arrays.asList(terms));
		
		// s

		terms = new Term[1];
		terms[0] = X;
		sX = new DefaultAtom(s, Arrays.asList(terms));
	
		terms = new Term[1];
		terms[0] = U;
		sU = new DefaultAtom(s, Arrays.asList(terms));
		
		terms = new Term[1];
		terms[0] = V;
		sV = new DefaultAtom(s, Arrays.asList(terms));
	
		terms = new Term[1];
		terms[0] = Y;
		sY = new DefaultAtom(s, Arrays.asList(terms));
		
		// r

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
