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
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.io.dlp.DlpParser;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
@RunWith(Theories.class)
public class StoreTest {

	@DataPoints
	public static AtomSet[] writeableStore() {
		return TestUtil.writeableStore();
	}
	
	@Theory
	public void add(AtomSet store) {		
		store.add(DlpParser.parseAtom("p(a,b)."));
		store.add(DlpParser.parseAtom("q(b,c)."));
		
		Iterator<Atom> it = store.iterator();
		int i = 0;
		while(it.hasNext()) {
			++i;
			it.next();
		}
		System.out.println(i);
		Assert.assertEquals(2, i);
	}
	
	
}
