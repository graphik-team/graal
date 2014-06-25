/**
 * 
 */
package fr.lirmm.graphik;

import org.junit.Assert;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import fr.lirmm.graphik.graal.Graal;
import fr.lirmm.graphik.graal.core.DefaultConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Substitution;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.stream.SubstitutionReader;
import fr.lirmm.graphik.graal.io.basic.BasicParser;
import fr.lirmm.graphik.graal.io.dlgp.DlgpParser;
import fr.lirmm.graphik.graal.parser.misc.BasicStringFormat;
import fr.lirmm.graphik.graal.parser.misc.StringAtomReader;
import fr.lirmm.graphik.graal.parser.misc.StringFormat;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
@RunWith(Theories.class)
public class ConjunctiveQueryTest {

	@DataPoints
	public static AtomSet[] writeableStore() {
		return TestUtil.writeableStore();
	}

	/**
	 * Test an empty query that must have an empty substitution
	 */
	@Theory
	public void emptyQueryTest(AtomSet store) {
		try {
			store.add(BasicParser.parse("p(a,b).p(b,c).q(a,c,d)"));

			AtomSet queryAtomSet = new LinkedListAtomSet();
			DefaultConjunctiveQuery query = new DefaultConjunctiveQuery(queryAtomSet);

			SubstitutionReader subReader;
			Substitution sub;

			subReader = Graal.executeQuery(query, store);

			Assert.assertTrue(subReader.hasNext());
			sub = subReader.next();
			Assert.assertEquals(0, sub.getTerms().size());

			Assert.assertFalse(subReader.hasNext());

		} catch (Exception e) {
			Assert.assertTrue(e.getMessage(), false);
		}
	}

	/**
	 * Test a query without answer
	 */
	@Theory
	public void noAnswerQueryTest(AtomSet store) {
		try {
			store.add(BasicParser.parse("p(a,b).p(b,c).q(a,c,d)"));

			DefaultConjunctiveQuery query = DlgpParser.parseQuery("?(X) :- p(c,X).");

			SubstitutionReader subReader;

			subReader = Graal.executeQuery(query, store);
			Assert.assertFalse(subReader.hasNext());
		} catch (Exception e) {
			Assert.assertTrue(e.getMessage(), false);
		}
	}

	/**
	 * Test a boolean query
	 */
	@Theory
	public void booleanQueryTest(AtomSet store) {
		try {
			StringFormat stringRepresentation = new BasicStringFormat();
			store.add(new StringAtomReader("p(a,b).p(b,c).q(a,c,d).q(d,c,a)",
					stringRepresentation));

			AtomSet queryAtomSet = new LinkedListAtomSet();
			queryAtomSet.add(new StringAtomReader("q(a,c,d)",
					stringRepresentation));
			DefaultConjunctiveQuery query = new DefaultConjunctiveQuery(queryAtomSet);

			SubstitutionReader subReader;
			Substitution sub;

			subReader = Graal.executeQuery(query, store);

			Assert.assertTrue(subReader.hasNext());
			sub = subReader.next();
			System.out.println(sub.getTerms());
			Assert.assertEquals(0, sub.getTerms().size());

			Assert.assertFalse(subReader.hasNext());
		} catch (Exception e) {
			Assert.assertTrue(e.getMessage(), false);
		}
	}

	@Theory
	public void basicQueryTest(AtomSet store) {
		try {
			store.add(BasicParser.parse("p(a,b).p(b,c)"));

			DefaultConjunctiveQuery query = DlgpParser.parseQuery("?(X,Y) :- p(X,Y),p(Y,c).");

			SubstitutionReader subReader;
			Substitution sub;

			subReader = Graal.executeQuery(query, store);

			Assert.assertTrue(subReader.hasNext());
			sub = subReader.next();
			Assert.assertEquals(2, sub.getTerms().size());
			Assert.assertEquals(
					sub.getSubstitut(new Term("X", Term.Type.VARIABLE)),
					new Term("a", Term.Type.CONSTANT));
			Assert.assertEquals(
					sub.getSubstitut(new Term("Y", Term.Type.VARIABLE)),
					new Term("b", Term.Type.CONSTANT));

			Assert.assertFalse(subReader.hasNext());

		} catch (Exception e) {
			Assert.assertTrue(e.getMessage(), false);
		}
	}

	/**
	 * Test a boolean query
	 */
	@Theory
	public void tttTrueQueryTest(AtomSet store) {
		try {
			store.add(BasicParser.parse("p(a,b).q(a,c,d).q(d,c,a)"));

			DefaultConjunctiveQuery query = DlgpParser.parseQuery("?(X,Y) :- q(a,c,d),p(X,Y).");

			SubstitutionReader subReader;
			Substitution sub;

			subReader = Graal.executeQuery(query, store);

			Assert.assertTrue(subReader.hasNext());
			sub = subReader.next();
			Assert.assertEquals(2, sub.getTerms().size());
			Assert.assertEquals(
					sub.getSubstitut(new Term("X", Term.Type.VARIABLE)),
					new Term("a", Term.Type.CONSTANT));
			Assert.assertEquals(
					sub.getSubstitut(new Term("Y", Term.Type.VARIABLE)),
					new Term("b", Term.Type.CONSTANT));

			Assert.assertFalse(subReader.hasNext());
		} catch (Exception e) {
			Assert.assertTrue(e.getMessage(), false);
		}
	}

	/**
	 * Test a boolean query
	 */
	@Theory
	public void tttFalseQueryTest(AtomSet store) {
		try {
			store.add(BasicParser.parse("p(a,b).p(b,c).q(a,c,d).q(d,c,a)"));

			DefaultConjunctiveQuery query = DlgpParser.parseQuery("?(X,Y) :- q(a,f,d),p(X,Y).");

			SubstitutionReader subReader;
			subReader = Graal.executeQuery(query, store);
			Assert.assertFalse(subReader.hasNext());
		} catch (Exception e) {
			Assert.assertTrue(e.getMessage(), false);
		}
	}

	/**
	 * Response variables Test
	 */
	@Theory
	public void responseVariablesTest(AtomSet store) {
		try {
			store.add(BasicParser.parse("p(a,b)"));

			DefaultConjunctiveQuery query = DlgpParser.parseQuery("?(X) :- p(X,Y).");

			SubstitutionReader subReader;
			Substitution sub;

			subReader = Graal.executeQuery(query, store);

			Assert.assertTrue(subReader.hasNext());
			sub = subReader.next();
			Assert.assertEquals(1, sub.getTerms().size());
			Assert.assertEquals(
					sub.getSubstitut(new Term("X", Term.Type.VARIABLE)),
					new Term("a", Term.Type.CONSTANT));
		} catch (Exception e) {
			Assert.assertTrue(e.getMessage(), false);
		}
	}

	@Theory
	public void nonexistingPredicateQuery(AtomSet store) {
		try {
			store.add(BasicParser.parse("p(a,b)"));

			DefaultConjunctiveQuery query = DlgpParser.parseQuery("?(X,Y) :- q(X,Y).");

			SubstitutionReader subReader;
			subReader = Graal.executeQuery(query, store);
			Assert.assertFalse(subReader.hasNext());
		} catch (Exception e) {
			Assert.assertTrue(e.getMessage(), false);
		}
	}
	
//	@Theory 
//	public void universalQuery(WriteableAtomSet store) {
//		try {
//			store.add(BasicParser.parse("p(a,b)"));
//
//			WriteableAtomSet queryAtomSet = new LinkedListAtomSet();
//			queryAtomSet.add(BasicParser.parse("p(a,Y)"));
//			ConjunctiveQuery query = new ConjunctiveQuery(queryAtomSet);
//
//			SubstitutionReader subReader;
//			subReader = Util.execute(query, store);
//			Assert.assertFalse(subReader.hasNext());
//		} catch (Exception e) {
//			Assert.assertTrue(e.getMessage(), false);
//		}
//	}
}
