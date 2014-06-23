package fr.lirmm.graphik.alaska.transformation;

import fr.lirmm.graphik.alaska.store.Store;
import fr.lirmm.graphik.kb.core.Atom;
import fr.lirmm.graphik.kb.core.AtomSet;
import fr.lirmm.graphik.kb.exception.AtomSetException;
import fr.lirmm.graphik.util.stream.ObjectReader;

/**
 * 
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public class WriteableTransformAtomSet extends TransformStore implements
        AtomSet {

    // /////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    // /////////////////////////////////////////////////////////////////////////

    public WriteableTransformAtomSet(AtomSet store,
            AAtomTransformator transformator) {
        super(store, transformator);
    }

    // /////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    // /////////////////////////////////////////////////////////////////////////

    @Override
    public boolean add(Atom atom) {
        try {
            this.getStore().add(
                    this.getAtomTransformator().transform(atom).iterator());
        } catch (AtomSetException e) {
            // TODO treat this exception
            e.printStackTrace();
            throw new Error("Untreated exception");
        }
        return true;
    }

    @Override
    public boolean remove(Atom atom) {
        try {
            this.getStore().remove(
                    this.getAtomTransformator().transform(atom).iterator());
        } catch (AtomSetException e) {
            // TODO treat this exception
            e.printStackTrace();
            throw new Error("Untreated exception");
        }
        return true;
    }

    @Override
    public void add(Iterable<Atom> atoms) {
        try {
            this.getStore().add(this.getAtomTransformator().transform(atoms));
        } catch (AtomSetException e) {
            // TODO treat this exception
            e.printStackTrace();
            throw new Error("Untreated exception");

        }
    }

    @Override
    public void remove(ObjectReader stream) {
        try {
            this.getStore().remove(
                    this.getAtomTransformator().transform(stream));
        } catch (AtomSetException e) {
            // TODO treat this exception
            e.printStackTrace();
            throw new Error("Untreated exception");

        }
    }

    // /////////////////////////////////////////////////////////////////////////
    // PROTECTED METHODS
    // /////////////////////////////////////////////////////////////////////////

    protected AtomSet getStore() {
        return (AtomSet) super.getStore();
    }
}
