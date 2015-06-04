/**
 * 
 */
package fr.lirmm.graphik.graal.transformation;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.atomset.InMemoryAtomSet;
import fr.lirmm.graphik.util.stream.transformator.Transformator;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public interface AtomTransformator extends Transformator<Atom, InMemoryAtomSet> {

	/**
     * Transform the specified atom.
     * 
     * @param atom
     * @return
     */
    InMemoryAtomSet transform(Atom atom);
    
    /**
     * Transform the specified atom stream
     * 
     * @param atoms
     * @return 
     */
    Iterable<Atom> transform(Iterable<? extends Atom> atoms);

}
