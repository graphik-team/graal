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
