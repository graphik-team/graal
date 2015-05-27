/**
 * 
 */
package fr.lirmm.graphik.graal.test;

import java.io.IOException;

import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.graal.io.dlp.DlgpWriter;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class Dlgp2ParserTest {

	public static void main(String args[]) throws IOException {
		DlgpParser p = new DlgpParser("\n" + ""
				+ "@prefix ns: <http://ns.ns/> @prefix xsd: <http://xsd.org/> "
				+ "@top thing \n" + "@una " + "ns:p(ns:a,ns:b) ."
				// + "! :- b = a. "
				+ "p(X,Y) :- q(<http://toto/titi>,Z)."
				+ "toto(\"1\"^^xsd:integer)." + "toto(2).");

		DlgpWriter w = new DlgpWriter();
		for (Object o : p) {
			System.out.println(o);

			w.write(o);
			System.out.println();
		}
		System.out.println("---");

		LinkedListAtomSet atomset = new LinkedListAtomSet();
		atomset.add(DlgpParser.parseAtom("p(a)."));
		atomset.add(DlgpParser.parseAtom("p(b)."));
		atomset.add(DlgpParser.parseAtom("b = a."));

		w.write(atomset);

		w.close();
	}

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////////
	// OBJECT OVERRIDE METHODS
	// /////////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

}
