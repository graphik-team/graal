/**
 * 
 */
package fr.lirmm.graphik.graal.test;

import java.util.Iterator;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.DefaultConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Query;
import fr.lirmm.graphik.graal.core.Substitution;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.homomorphism.HomomorphismFactoryException;
import fr.lirmm.graphik.graal.homomorphism.StaticHomomorphism;
import fr.lirmm.graphik.graal.io.dlgp.DlgpParser;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class Test {

	public static void main(String args[]) throws HomomorphismFactoryException, HomomorphismException {
		AtomSet atomset = new LinkedListAtomSet();

		atomset.add(DlgpParser.parseAtom("p(A,B)."));
		
		Query query = new DefaultConjunctiveQuery(atomset);
		Iterator<Substitution> subIt = StaticHomomorphism.executeQuery(query, atomset);
		
		System.out.println(subIt.hasNext());
	}
}
