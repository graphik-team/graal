/**
 * 
 */
package fr.lirmm.graphik;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import fr.lirmm.graphik.alaska.store.impl.FileStore;
import fr.lirmm.graphik.kb.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.kb.core.Atom;
import fr.lirmm.graphik.kb.core.AtomSet;
import fr.lirmm.graphik.kb.core.DefaultAtom;
import fr.lirmm.graphik.kb.core.Predicate;
import fr.lirmm.graphik.kb.core.Term;
import fr.lirmm.graphik.kb.stream.IteratorAtomReader;
import fr.lirmm.graphik.obda.parser.misc.BasicStringFormat;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public class MiscTest {

    @Test
    public void testFileStore() throws Exception {
        List<Term> terms = new LinkedList<Term>();
        terms.add(new Term("a", Term.Type.CONSTANT));
        terms.add(new Term("b", Term.Type.CONSTANT));
        Atom atom = new DefaultAtom(new Predicate("p", 2), terms);

        FileStore store = new FileStore(new URI("file:///tmp/fol.store"),
                new BasicStringFormat());
        AtomSet atomSet = new LinkedListAtomSet();
        atomSet.add(atom);
        store.add(new IteratorAtomReader(atomSet.iterator()));
    }

}
