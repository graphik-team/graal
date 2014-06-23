package fr.lirmm.graphik.alaska.transformation;

import java.util.Iterator;

import fr.lirmm.graphik.kb.core.Atom;
import fr.lirmm.graphik.kb.core.ReadOnlyAtomSet;
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
