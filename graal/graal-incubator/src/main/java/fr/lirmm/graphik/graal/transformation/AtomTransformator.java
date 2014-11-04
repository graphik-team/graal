/**
 * 
 */
package fr.lirmm.graphik.graal.transformation;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.atomset.ReadOnlyAtomSet;
import fr.lirmm.graphik.util.stream.ObjectReader;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public interface AtomTransformator {

	/**
     * Transform the specified atom.
     * 
     * @param atom
     * @return
     */
    ReadOnlyAtomSet transform(Atom atom);
    
    /**
     * Transform the specified atom stream
     * 
     * @param atoms
     * @return 
     */
    ObjectReader<Atom> transform(Iterable<Atom> atoms);

}
