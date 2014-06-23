/**
 * 
 */
package fr.lirmm.graphik.kb;

import fr.lirmm.graphik.kb.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.kb.core.AtomSet;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public abstract class AtomSetFactory {

    public static AtomSet createWriteableAtomSet() {
        return new LinkedListAtomSet();
    }
}
