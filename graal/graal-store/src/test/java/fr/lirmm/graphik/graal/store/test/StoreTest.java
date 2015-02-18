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
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
import fr.lirmm.graphik.graal.io.dlp.DlpParser;
import fr.lirmm.graphik.graal.store.Store;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
@RunWith(Theories.class)
public class StoreTest {

	@DataPoints
	public static Store[] writeableStore() {
		return TestUtil.writeableStore();
	}

	@Theory
	public void getPredicates(Store store) throws AtomSetException {
		store.add(DlpParser.parseAtom("r(a,b)."));
		store.add(DlpParser.parseAtom("s(a,b)."));
		store.add(DlpParser.parseAtom("s(a,c)."));

		int i = 0;
		for (Iterator<Predicate> it = store.getAllPredicates().iterator(); it
				.hasNext(); it.next()) {
			++i;
		}

		Assert.assertEquals(2, i);
	}

	@Theory
	public void predicateArityTest(Store store) throws AtomSetException {
		store.add(DlpParser.parseAtom("p(a)."));
		store.add(DlpParser.parseAtom("p(a,b)."));
		store.add(DlpParser.parseAtom("p(a,c)."));

		int i = 0;
		for (Iterator<Predicate> it = store.getAllPredicates().iterator(); it
				.hasNext(); it.next()) {
			++i;
		}

		Assert.assertEquals(2, i);
	}

	@Theory
	public void add(Store store) throws AtomSetException {
		store.add(DlpParser.parseAtom("p(a,b)."));
		store.add(DlpParser.parseAtom("q(b,c)."));

		int i = 0;
		for (Iterator<Atom> it = store.iterator(); it.hasNext(); it.next()) {
			++i;
		}

		Assert.assertEquals("Store does not contains exactly 2 atoms", 2, i);

		Assert.assertTrue("Store does not contains p(a,b)",
				store.contains(DlpParser.parseAtom("p(a,b).")));
		Assert.assertTrue("Store does not contains q(b,c)",
				store.contains(DlpParser.parseAtom("q(b,c).")));

		Assert.assertFalse("Store contains q(c, b)",
				store.contains(DlpParser.parseAtom("q(c,b).")));
	}

	@Theory
	public void getTerms(Store store) throws AtomSetException {
		store.add(DlpParser.parseAtom("p(a,b)."));
		store.add(DlpParser.parseAtom("p(b,c)."));
		store.add(DlpParser.parseAtom("p(b,c,X,Y)."));

		int i = 0;
		for (Iterator<Term> it = store.getTerms().iterator(); it.hasNext(); it
				.next()) {
			++i;
		}

		Assert.assertEquals("Wrong number of terms", 5, i);

		i = 0;
		for (Iterator<Term> it = store.getTerms(Term.Type.CONSTANT).iterator(); it
				.hasNext(); it.next()) {
			++i;
		}

		Assert.assertEquals("Wrong number of constant", 3, i);

		i = 0;
		for (Iterator<Term> it = store.getTerms(Term.Type.VARIABLE).iterator(); it
				.hasNext(); it.next()) {
			++i;
		}
		Assert.assertEquals("Wrong number of variable", 2, i);
	}

	@Theory
	public void isEmpty(Store store) {
		Assert.assertTrue("Store is empty but isEmpty return false",
				store.isEmpty());
		store.add(DlpParser.parseAtom("p(a,b)."));
		Assert.assertFalse("Store is not empty but isEmpty return true",
				store.isEmpty());
	}

}
