package fr.lirmm.graphik.graal.transformation;

import fr.lirmm.graphik.graal.core.Atom;

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
    public Iterable<Atom> transform(Iterable<? extends Atom> atoms) {
        return new TransformatorReader(atoms, this);
    }

}
