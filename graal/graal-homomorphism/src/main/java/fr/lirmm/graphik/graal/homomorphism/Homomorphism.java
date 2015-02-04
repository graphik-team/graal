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

	 SubstitutionReader execute(T1 q, T2 a) throws HomomorphismException;

};

