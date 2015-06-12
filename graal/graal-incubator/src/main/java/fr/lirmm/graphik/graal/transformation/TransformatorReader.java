/* Graal v0.7.4
 * Copyright (c) 2014-2015 Inria Sophia Antipolis - Méditerranée / LIRMM (Université de Montpellier & CNRS)
 * All rights reserved.
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * Author(s): Clément SIPIETER
 *            Mélanie KÖNIG
 *            Swan ROCHER
 *            Jean-François BAGET
 *            Michel LECLÈRE
 *            Marie-Laure MUGNIER
 */
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
    private Iterator<? extends Atom> atomIterator;
    private Iterator<Atom> tmpIterator;

    // /////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    // /////////////////////////////////////////////////////////////////////////

    public TransformatorReader(Iterable<? extends Atom> atoms,
            AtomTransformator transformator) {
        this.atomIterator = atoms.iterator();
        this.transformator = transformator;
        this.tmpIterator = null;
    }

    // /////////////////////////////////////////////////////////////////////////
    // METHODS
    // /////////////////////////////////////////////////////////////////////////

    @Override
    public void remove() {
        // TODO implement this method
        throw new MethodNotImplementedError();
    }

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

    @Override
    public Atom next() {
        if (!this.hasNextCallDone)
            this.hasNext();

        this.hasNextCallDone = false;

        return this.tmpIterator.next();
    }

    @Override
    public Iterator<Atom> iterator() {
        return this;
    }

}
