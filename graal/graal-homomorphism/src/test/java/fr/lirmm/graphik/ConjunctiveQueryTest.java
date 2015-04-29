/**
 * 
 */
package fr.lirmm.graphik;

import org.junit.Assert;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import fr.lirmm.graphik.graal.core.DefaultConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Substitution;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.InMemoryAtomSet;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.stream.SubstitutionReader;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.homomorphism.StaticHomomorphism;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;

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
	 * Test an empty query with an empty atomSet that must have an empty substitution 
	 */
	@Theory
	public void emptyQueryAndEmptyAtomSetTest(AtomSet store) {
		try {
			InMemoryAtomSet queryAtomSet = new LinkedListAtomSet();
			DefaultConjunctiveQuery query = new DefaultConjunctiveQuery(queryAtomSet);

			SubstitutionReader subReader;
			Substitution sub;

			subReader = StaticHomomorphism.executeQuery(query, store);

			Assert.assertTrue(subReader.hasNext());
			sub = subReader.next();
			Assert.assertEquals(0, sub.getTerms().size());

			Assert.assertFalse(subReader.hasNext());

		} catch (Exception e) {
			Assert.assertTrue(e.getMessage(), false);
		}
	}
	
	/**
	 * Test an empty query that must have an empty substitution
	 */
	@Theory
	public void emptyQueryTest(AtomSet store) {
		try {
			store.addAll(DlgpParser.parseAtomSet("p(a,b), p(b,c), q(a,c,d)."));

			InMemoryAtomSet queryAtomSet = new LinkedListAtomSet();
			DefaultConjunctiveQuery query = new DefaultConjunctiveQuery(queryAtomSet);

			SubstitutionReader subReader;
			Substitution sub;

			subReader = StaticHomomorphism.executeQuery(query, store);

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
			store.addAll(DlgpParser.parseAtomSet("p(a,b),p(b,c),q(a,c,d)."));

			DefaultConjunctiveQuery query = DlgpParser.parseQuery("?(X) :- p(c,X).");

			SubstitutionReader subReader;

			subReader = StaticHomomorphism.executeQuery(query, store);
			Assert.assertFalse(subReader.hasNext());
		} catch (Exception e) {
			Assert.assertTrue(e.getMessage(), false);
		}
	}

	/**
	 * Test a query without answer
	 */
	@Theory
	public void noAnswerQueryTest2(AtomSet store) {
		try {
			DefaultConjunctiveQuery query = DlgpParser.parseQuery("?(Y,X) :- p(Y,X).");

			SubstitutionReader subReader;

			subReader = StaticHomomorphism.executeQuery(query, store);
			Assert.assertFalse(subReader.hasNext());
		} catch (Exception e) {
			Assert.assertTrue(e.getMessage(), false);
		}
	}
	
	/**
	 * Test a query without answer
	 */
	@Theory
	public void noAnswerQueryTest3(AtomSet store) {
		try {
			store.addAll(DlgpParser.parseAtomSet("p(a,b), r(c,c)."));
			DefaultConjunctiveQuery query = DlgpParser.parseQuery("?(Y,X) :- p(a,X), q(X,Y).");

			SubstitutionReader subReader;

			subReader = StaticHomomorphism.executeQuery(query, store);
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
			store.addAll(DlgpParser.parseAtomSet("p(a,b).p(b,c).q(a,c,d).q(d,c,a)."));

			InMemoryAtomSet queryAtomSet = new LinkedListAtomSet();
			queryAtomSet.add(DlgpParser.parseAtom("q(a,c,d)."));
			DefaultConjunctiveQuery query = new DefaultConjunctiveQuery(queryAtomSet);

			SubstitutionReader subReader;
			Substitution sub;

			subReader = StaticHomomorphism.executeQuery(query, store);

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
			store.addAll(DlgpParser.parseAtomSet("p(a,b).p(b,c)."));

			DefaultConjunctiveQuery query = DlgpParser.parseQuery("?(X,Y) :- p(X,Y),p(Y,c).");

			SubstitutionReader subReader;
			Substitution sub;

			subReader = StaticHomomorphism.executeQuery(query, store);

			Assert.assertTrue(subReader.hasNext());
			sub = subReader.next();
			Assert.assertEquals(2, sub.getTerms().size());
			Assert.assertEquals(
sub.getSubstitute(DefaultTermFactory.instance()
					.createVariable("X")), DefaultTermFactory.instance()
					.createConstant("a"));
			Assert.assertEquals(
sub.getSubstitute(DefaultTermFactory.instance()
					.createVariable("Y")), DefaultTermFactory.instance()
					.createConstant("b"));

			Assert.assertFalse(subReader.hasNext());

		} catch (Exception e) {
			Assert.assertTrue(e.getMessage(), false);
		}
	}
	
	public void variableFusionTest(AtomSet store) {
		try {
			store.addAll(DlgpParser.parseAtomSet("p(a,b),q(b,b)."));

			DefaultConjunctiveQuery query = DlgpParser.parseQuery("?(X,Y) :- p(a,X),q(X,Y),q(Y,X).");

			SubstitutionReader subReader;
			Substitution sub;

			subReader = StaticHomomorphism.executeQuery(query, store);

			Assert.assertTrue(subReader.hasNext());
			sub = subReader.next();
			Assert.assertEquals(2, sub.getTerms().size());
			Assert.assertEquals(
sub.getSubstitute(DefaultTermFactory.instance()
					.createVariable("X")), DefaultTermFactory.instance()
					.createConstant("b"));
			Assert.assertEquals(
sub.getSubstitute(DefaultTermFactory.instance()
					.createVariable("Y")), DefaultTermFactory.instance()
					.createConstant("b"));

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
			store.addAll(DlgpParser.parseAtomSet("p(a,b),q(a,c,d),q(d,c,a)."));

			DefaultConjunctiveQuery query = DlgpParser.parseQuery("?(X,Y) :- q(a,c,d),p(X,Y).");

			SubstitutionReader subReader;
			Substitution sub;

			subReader = StaticHomomorphism.executeQuery(query, store);

			Assert.assertTrue(subReader.hasNext());
			sub = subReader.next();
			Assert.assertEquals(2, sub.getTerms().size());
			Assert.assertEquals(
sub.getSubstitute(DefaultTermFactory.instance()
					.createVariable("X")), DefaultTermFactory.instance()
					.createConstant("a"));
			Assert.assertEquals(
sub.getSubstitute(DefaultTermFactory.instance()
					.createVariable("Y")), DefaultTermFactory.instance()
					.createConstant("b"));

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
			store.addAll(DlgpParser.parseAtomSet("p(a,b),p(b,c),q(a,c,d),q(d,c,a)."));

			DefaultConjunctiveQuery query = DlgpParser.parseQuery("?(X,Y) :- q(a,f,d),p(X,Y).");

			SubstitutionReader subReader;
			subReader = StaticHomomorphism.executeQuery(query, store);
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
			store.addAll(DlgpParser.parseAtomSet("p(a,b)."));

			DefaultConjunctiveQuery query = DlgpParser.parseQuery("?(X) :- p(X,Y).");

			SubstitutionReader subReader;
			Substitution sub;

			subReader = StaticHomomorphism.executeQuery(query, store);

			Assert.assertTrue(subReader.hasNext());
			sub = subReader.next();
			Assert.assertEquals(1, sub.getTerms().size());
			Assert.assertEquals(
					DefaultTermFactory.instance().createConstant("a"), sub
							.getSubstitute(DefaultTermFactory.instance()
									.createVariable("X")));
		} catch (Exception e) {
			Assert.assertTrue(e.getMessage(), false);
		}
	}

	@Theory
	public void nonexistingPredicateQuery(AtomSet store) {
		try {
			store.addAll(DlgpParser.parseAtomSet("p(a,b)."));

			DefaultConjunctiveQuery query = DlgpParser.parseQuery("?(X,Y) :- q(X,Y).");

			SubstitutionReader subReader;
			subReader = StaticHomomorphism.executeQuery(query, store);
			Assert.assertFalse(subReader.hasNext());
		} catch (Exception e) {
			Assert.assertTrue(e.getMessage(), false);
		}
	}
	
	@Theory
	public void wrongArityQuery(AtomSet atomset) {
		try {
			atomset.add(DlgpParser.parseAtom("p(a,b)."));
			DefaultConjunctiveQuery query = DlgpParser.parseQuery("? :- p(X).");
	
			SubstitutionReader subReader;
			subReader = StaticHomomorphism.executeQuery(query, atomset);
			Assert.assertFalse(subReader.hasNext());
		} catch (Exception e) {
			Assert.assertTrue(e.getMessage(), false);
		}
	}
	
	@Theory
	public void wrongArityQuery2(AtomSet atomset) {
		try {
			atomset.add(DlgpParser.parseAtom("p(a,b)."));
			DefaultConjunctiveQuery query = DlgpParser.parseQuery("? :- p(X,Y,Z).");
	
			SubstitutionReader subReader;
			subReader = StaticHomomorphism.executeQuery(query, atomset);
			Assert.assertFalse(subReader.hasNext());
		} catch (Exception e) {
			Assert.assertTrue(e.getMessage(), false);
		}
	}
	
	@Theory
	public void diffLiteralQueryTest(AtomSet atomset) {
		try {
			atomset.add(DlgpParser.parseAtom("p(\"literal\")."));
			DefaultConjunctiveQuery query = DlgpParser.parseQuery("? :- p(\"otherLiteral\").");
	
			SubstitutionReader subReader;
			subReader = StaticHomomorphism.executeQuery(query, atomset);
			Assert.assertFalse("Error on " + atomset.getClass() ,subReader.hasNext());
		} catch (Exception e) {
			Assert.assertTrue(e.getMessage(), false);
		}
	}
	
	@Theory
	public void sameLiteralQueryTest(AtomSet atomset) {
		try {
			atomset.add(DlgpParser.parseAtom("p(\"literal\")."));
			DefaultConjunctiveQuery query = DlgpParser.parseQuery("? :- p(\"literal\").");
	
			SubstitutionReader subReader;
			subReader = StaticHomomorphism.executeQuery(query, atomset);
			Assert.assertTrue("Error on " + atomset.getClass() ,subReader.hasNext());
		} catch (Exception e) {
			Assert.assertTrue(e.getMessage(), false);
		}
	}
	

}
