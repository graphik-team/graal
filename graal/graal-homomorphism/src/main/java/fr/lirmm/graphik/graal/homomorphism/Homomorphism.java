/**
 * 
 */
package fr.lirmm.graphik.graal.homomorphism;

import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.stream.SubstitutionReader;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public interface Homomorphism<T1 extends Object, T2 extends AtomSet> {

	<U1 extends T1, U2 extends T2> SubstitutionReader execute(U1 q, U2 a)
			throws HomomorphismException;

};

