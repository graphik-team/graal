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
package fr.lirmm.graphik.util.stream;

import java.io.IOException;
import java.util.Iterator;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
@Deprecated
public abstract class AbstractReader<T> implements ObjectReader<T> {

    /* (non-Javadoc)
     * @see java.util.Iterator#remove()
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
    
    /* (non-Javadoc)
     * @see fr.lirmm.graphik.kb.stream.IAtomReader#iterator()
     */
    @Override
    public Iterator<T> iterator() {
        return this;
    }

    /* (non-Javadoc)
     * @see fr.lirmm.graphik.kb.stream.AtomReader#read(fr.lirmm.graphik.kb.stream.AtomWriter)
     */
    @Override
    public void read(ObjectWriter<T> writer) throws IOException {
        for(T object : this)
            writer.write(object);
    }
}
