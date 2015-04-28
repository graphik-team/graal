/**
 * 
 */
package fr.lirmm.graphik.graal.store.test;

import java.util.Iterator;

import org.junit.Assert;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theory;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.graal.store.Store;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class StoreTest {

	@DataPoints
	public static Store[] writeableStore() {
		return TestUtil.getStores();
	}

	@Theory
	public void getPredicates(Store store) throws AtomSetException {
		store.add(DlgpParser.parseAtom("r(a,b)."));
		store.add(DlgpParser.parseAtom("s(a,b)."));
		store.add(DlgpParser.parseAtom("s(a,c)."));

		int i = 0;
		for (Iterator<Predicate> it = store.predicatesIterator(); it.hasNext(); it
				.next()) {
			++i;
		}

		Assert.assertEquals(2, i);
	}

	@Theory
	public void predicateArityTest(Store store) throws AtomSetException {
		store.add(DlgpParser.parseAtom("p(a)."));
		store.add(DlgpParser.parseAtom("p(a,b)."));
		store.add(DlgpParser.parseAtom("p(a,c)."));

		int i = 0;
		for (Iterator<Predicate> it = store.predicatesIterator(); it.hasNext(); it
				.next()) {
			++i;
		}

		Assert.assertEquals(2, i);
	}

	@Theory
	public void addAndContains(Store store) throws AtomSetException {
		store.add(DlgpParser.parseAtom("p(a,b)."));
		store.add(DlgpParser.parseAtom("q(b,c)."));

		int i = 0;
		for (Iterator<Atom> it = store.iterator(); it.hasNext(); it.next()) {
			++i;
		}

		Assert.assertEquals("Store does not contains exactly 2 atoms", 2, i);

		Assert.assertTrue("Store does not contains p(a,b)",
				store.contains(DlgpParser.parseAtom("p(a,b).")));
		Assert.assertTrue("Store does not contains q(b,c)",
				store.contains(DlgpParser.parseAtom("q(b,c).")));

		Assert.assertFalse("Store contains q(c, b)",
				store.contains(DlgpParser.parseAtom("q(c,b).")));
	}

	@Theory
	public void getTerms(Store store) throws AtomSetException {
		store.add(DlgpParser.parseAtom("p(a,b)."));
		store.add(DlgpParser.parseAtom("p(b,c)."));
		store.add(DlgpParser.parseAtom("p(b,c,X,Y)."));

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
	public void isEmpty(Store store) throws AtomSetException {
		Assert.assertTrue("Store is empty but isEmpty return false",
				store.isEmpty());
		store.add(DlgpParser.parseAtom("p(a,b)."));
		Assert.assertFalse("Store is not empty but isEmpty return true",
				store.isEmpty());
	}

	@Theory
	public void termsOrder(Store store) throws AtomSetException {
		Atom a1 = DlgpParser.parseAtom("p(a,b,c,d,e,f).");
		Atom a2 = DlgpParser.parseAtom("p(f,e,d,c,b,a).");

		store.add(a1);
		store.add(a2);

		for (Atom a : store) {
			Assert.assertTrue(a.equals(a1) || a.equals(a2));
		}
	}

}
