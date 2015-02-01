/**
 * 
 */
package fr.lirmm.graphik.graal.transformation;

import java.util.Iterator;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.util.MethodNotImplementedError;
import fr.lirmm.graphik.util.stream.AbstractReader;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public class TransformatorReader extends AbstractReader<Atom> {

    private boolean hasNextCallDone;
    private AtomTransformator transformator;
    private Iterator<Atom> atomIterator;
    private Iterator<Atom> tmpIterator;

    // /////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    // /////////////////////////////////////////////////////////////////////////

    public TransformatorReader(Iterable<Atom> atoms,
            AtomTransformator transformator) {
        this.atomIterator = atoms.iterator();
        this.transformator = transformator;
        this.tmpIterator = null;
    }

    // /////////////////////////////////////////////////////////////////////////
    // METHODS
    // /////////////////////////////////////////////////////////////////////////

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Iterator#remove()
     */
    @Override
    public void remove() {
        // TODO implement this method
        throw new MethodNotImplementedError();
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.lirmm.graphik.kb.stream.IAtomReader#hasNext()
     */
    @Override
    public boolean hasNext() {
        if (!this.hasNextCallDone) {
            this.hasNextCallDone = true;

            while ((this.tmpIterator == null || !this.tmpIterator.hasNext())
                    && this.atomIterator.hasNext())
                this.tmpIterator = this.transformator
                        .transform(atomIterator.next()).iterator();
        }
        return this.tmpIterator != null && this.tmpIterator.hasNext();
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.lirmm.graphik.kb.stream.IAtomReader#next()
     */
    @Override
    public Atom next() {
        if (!this.hasNextCallDone)
            this.hasNext();

        this.hasNextCallDone = false;

        return this.tmpIterator.next();
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.lirmm.graphik.kb.stream.IAtomReader#iterator()
     */
    @Override
    public Iterator<Atom> iterator() {
        return this;
    }

}
