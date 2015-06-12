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
 package fr.lirmm.graphik.graal.transformation;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.SymbolGenerator;
import fr.lirmm.graphik.graal.core.atomset.InMemoryAtomSet;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public class ToTripleTransformation extends AAtomTransformator {

    private SymbolGenerator freeVarGen;

    // /////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    // /////////////////////////////////////////////////////////////////////////

    public ToTripleTransformation(SymbolGenerator freeVarGen) {
        this.freeVarGen = freeVarGen;
    }

    @Override
    public InMemoryAtomSet transform(Atom atom) {
        return Util.reification(atom, this.freeVarGen);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.lirmm.graphik.alaska.transformation.AAtomTransformator#transform(fr
     * .lirmm.graphik.kb.IAtomSet)
     */
    /*@Override
    public AtomSet transform(AtomSet atomSet) {
        WriteableAtomSet atomSetTransformed = new LinkedListAtomSet();
        for (Atom atom : atomSet) {
            for (Atom a : Util.reification(atom, freeVarGen)) {
                atomSetTransformed.add(a);
            }
        }
        return atomSetTransformed;
    }*/
}
