/**
 * 
 */
package fr.lirmm.graphik.graal.transformation;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
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
    AtomSet transform(Atom atom);
    
    /**
     * Transform the specified atom stream
     * 
     * @param atoms
     * @return 
     */
    Iterable<Atom> transform(Iterable<? extends Atom> atoms);

}
