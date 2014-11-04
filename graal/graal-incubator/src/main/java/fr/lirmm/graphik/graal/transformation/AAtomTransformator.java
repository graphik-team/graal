package fr.lirmm.graphik.graal.transformation;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.util.stream.ObjectReader;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public abstract class AAtomTransformator implements AtomTransformator {

    /**
     * Transform the specified atom stream
     * 
     * @param atoms
     * @return 
     */
    public ObjectReader<Atom> transform(Iterable<Atom> atoms) {
        return new TransformatorReader(atoms, this);
    }

}
