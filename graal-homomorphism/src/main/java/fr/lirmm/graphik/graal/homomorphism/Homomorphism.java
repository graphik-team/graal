/**
 * 
 */
package fr.lirmm.graphik.graal.homomorphism;

import fr.lirmm.graphik.graal.core.Query;
import fr.lirmm.graphik.graal.core.atomset.ReadOnlyAtomSet;
import fr.lirmm.graphik.graal.core.stream.SubstitutionReader;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public interface Homomorphism<T1 extends Query, T2 extends ReadOnlyAtomSet> {

	 SubstitutionReader execute(T1 q, T2 a) throws HomomorphismException;

};

