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
package fr.lirmm.graphik.graal.core.stream;

import java.util.Iterator;

import fr.lirmm.graphik.graal.core.Substitution;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class IteratorSubstitutionReader implements SubstitutionReader {
    
    private Iterator<Substitution> iterator;
    
    public IteratorSubstitutionReader(Iterator<Substitution>  iterator) {
        this.iterator = iterator;
    }

    @Override
    public void remove() {
        this.iterator.remove();
    }

    @Override
    public boolean hasNext() {
        return this.iterator.hasNext();
    }

    @Override
    public Substitution next() {
        return this.iterator.next();
    }

    @Override
    public Iterator<Substitution> iterator() {
        return this;
    }

    /* (non-Javadoc)
     * @see fr.lirmm.graphik.kb.stream.ISubstitutionReader#close()
     */
    @Override
    public void close() {
    }
    
    

}
