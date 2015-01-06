/**
 * 
 */
package fr.lirmm.graphik.graal.backward_chaining.test;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.Term;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
public final class TestUtils {

	private TestUtils() {
	}

	public static final Term W = new Term("W", Term.Type.VARIABLE);
	public static final Term X = new Term("X", Term.Type.VARIABLE);
	public static final Term Y = new Term("Y", Term.Type.VARIABLE);
	public static final Term Z = new Term("Z", Term.Type.VARIABLE);
 
	public static final Term A = new Term("A", Term.Type.CONSTANT);
	public static final Term B = new Term("B", Term.Type.CONSTANT);
	public static final Term C = new Term("C", Term.Type.CONSTANT);
 
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
