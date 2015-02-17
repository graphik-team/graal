/**
 * 
 */
package fr.lirmm.graphik.graal.store.test;

import java.util.Iterator;

import org.junit.Assert;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
import fr.lirmm.graphik.graal.store.triplestore.TripleStore;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
@RunWith(Theories.class)
public class TripleStoreTest {

	@DataPoints
	public static TripleStore[] getTriplesStores() {
		return TestUtil.getTripleStores();
	}

	@Theory
	public void simpleTest(TripleStore store) throws AtomSetException {
		Term t1 = new Term("http://to.to/b", Term.Type.CONSTANT);
		Term t2 = new Term("http://to.to/a", Term.Type.CONSTANT);
		Predicate p = new Predicate("http://to.to/p", 2);
		Atom atom1 = new DefaultAtom(p, t1, t2);
		
		store.add(atom1);
		
		int i = 0;
		for(Iterator<Atom> it = store.iterator(); it.hasNext(); it.next()) {
			++i;
		}
		
		Assert.assertEquals(1, i);
	}
}
