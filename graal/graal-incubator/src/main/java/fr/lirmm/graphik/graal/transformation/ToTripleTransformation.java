package fr.lirmm.graphik.graal.transformation;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.SymbolGenerator;
import fr.lirmm.graphik.graal.core.Util;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;

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
    public AtomSet transform(Atom atom) {
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
