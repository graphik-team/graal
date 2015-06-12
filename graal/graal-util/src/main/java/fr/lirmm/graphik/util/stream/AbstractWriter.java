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


/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
@Deprecated
public abstract class AbstractWriter<T> implements ObjectWriter<T> {

    /* (non-Javadoc)
     * @see fr.lirmm.graphik.kb.stream.AtomWriter#write(fr.lirmm.graphik.kb.stream.AtomReader)
     */
    @Override
    public void write(Iterable<T> inputStream) throws IOException {
        for(T object : inputStream)
            this.write(object);
    }

}
