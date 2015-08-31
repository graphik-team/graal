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
