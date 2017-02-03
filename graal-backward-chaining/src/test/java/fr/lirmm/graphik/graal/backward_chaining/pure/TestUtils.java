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
package fr.lirmm.graphik.graal.backward_chaining.pure;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
public final class TestUtils {

	private TestUtils() {
	}

	public static final Term W = DefaultTermFactory.instance().createVariable(
			"W");
	public static final Term X = DefaultTermFactory.instance().createVariable(
			"X");
	public static final Term Y = DefaultTermFactory.instance().createVariable(
			"Y");
	public static final Term Z = DefaultTermFactory.instance().createVariable(
			"Z");
 
	public static final Term A = DefaultTermFactory.instance().createConstant(
			"A");
	public static final Term B = DefaultTermFactory.instance().createConstant(
			"B");
	public static final Term C = DefaultTermFactory.instance().createConstant(
			"C");
 
	public static final Predicate P1 = new Predicate("p", 1);
	public static final Predicate P2 = new Predicate("p", 2);
	public static final Predicate P3 = new Predicate("p", 3);
	public static final Predicate P4 = new Predicate("p", 4);
 
	public static final Predicate Q1 = new Predicate("q", 1);
	public static final Predicate Q2 = new Predicate("q", 2);
	public static final Predicate Q3 = new Predicate("q", 3);
	public static final Predicate Q4 = new Predicate("q", 4);

	public static final Predicate R1 = new Predicate("r", 1);
	public static final Predicate R2 = new Predicate("r", 2);
	public static final Predicate R3 = new Predicate("r", 3);
	public static final Predicate R4 = new Predicate("r", 4);

	public static final Predicate S1 = new Predicate("s", 1);
	public static final Predicate S2 = new Predicate("s", 2);
	public static final Predicate S3 = new Predicate("s", 3);
	public static final Predicate S4 = new Predicate("s", 4);

	public static final Atom PX = new DefaultAtom(P1, X);
	public static final Atom PY = new DefaultAtom(P1, Y);
	public static final Atom PZ = new DefaultAtom(P1, Z);

	public static final Atom QX = new DefaultAtom(Q1, X);
	public static final Atom QY = new DefaultAtom(Q1, Y);
	public static final Atom QZ = new DefaultAtom(Q1, Z);

	public static final Atom RX = new DefaultAtom(R1, X);
	public static final Atom RY = new DefaultAtom(R1, Y);
	public static final Atom RZ = new DefaultAtom(R1, Z);

	public static final Atom SX = new DefaultAtom(S1, X);
	public static final Atom SY = new DefaultAtom(S1, Y);
	public static final Atom SZ = new DefaultAtom(S1, Z);

	public static final Atom PXX = new DefaultAtom(P2, X, X);
	public static final Atom PXY = new DefaultAtom(P2, X, Y);
	public static final Atom PYX = new DefaultAtom(P2, Y, X);
	public static final Atom PZX = new DefaultAtom(P2, Z, X);
	public static final Atom PXZ = new DefaultAtom(P2, X, Z);

	public static final Atom QXX = new DefaultAtom(Q2, X, X);
	public static final Atom QXY = new DefaultAtom(Q2, X, Y);
	public static final Atom QYX = new DefaultAtom(Q2, Y, X);
	public static final Atom QZX = new DefaultAtom(Q2, Z, X);

	public static final Atom RXX = new DefaultAtom(R2, X, X);
	public static final Atom RXY = new DefaultAtom(P2, X, Y);
	public static final Atom RYX = new DefaultAtom(R2, Y, X);
	public static final Atom RZX = new DefaultAtom(R2, Z, X);

	public static final Atom SXX = new DefaultAtom(S2, X, X);
	public static final Atom SXY = new DefaultAtom(P2, X, Y);
	public static final Atom SYX = new DefaultAtom(S2, Y, X);
	public static final Atom SZX = new DefaultAtom(S2, Z, X);

	public static final Atom PXXY = new DefaultAtom(P3, X, X, Y);
	public static final Atom PYXY = new DefaultAtom(P3, Y, X, Y);
	public static final Atom PZXY = new DefaultAtom(P3, Z, X, Y);

	public static final Atom QXXY = new DefaultAtom(Q3, X, X, Y);
	public static final Atom QYXY = new DefaultAtom(Q3, Y, X, Y);
	public static final Atom QZXY = new DefaultAtom(Q3, Z, X, Y);

	public static final Atom RXXY = new DefaultAtom(R3, X, X, Y);
	public static final Atom RYXY = new DefaultAtom(R3, Y, X, Y);
	public static final Atom RZXY = new DefaultAtom(R3, Z, X, Y);

	public static final Atom SXXY = new DefaultAtom(S3, X, X, Y);
	public static final Atom SYXY = new DefaultAtom(S3, Y, X, Y);
	public static final Atom SZXY = new DefaultAtom(S3, Z, X, Y);
	
}
