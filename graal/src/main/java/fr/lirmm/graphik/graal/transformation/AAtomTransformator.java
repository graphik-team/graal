package fr.lirmm.graphik.graal.transformation;

import java.util.Iterator;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.atomset.ReadOnlyAtomSet;
import fr.lirmm.graphik.util.stream.ObjectReader;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public abstract class AAtomTransformator {

    /**
     * Transform the specified atom.
     * 
     * @param atom
     * @return
     */
    abstract public ReadOnlyAtomSet transform(Atom atom);
    
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
