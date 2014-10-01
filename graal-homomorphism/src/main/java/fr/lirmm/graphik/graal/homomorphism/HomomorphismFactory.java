/**
 * 
 */
package fr.lirmm.graphik.graal.homomorphism;

import fr.lirmm.graphik.graal.core.Query;
import fr.lirmm.graphik.graal.core.atomset.ReadOnlyAtomSet;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public interface HomomorphismFactory {
    
    public abstract Homomorphism getSolver(Query query, ReadOnlyAtomSet atomSet);

}
