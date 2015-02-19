/**
 * 
 */
package fr.lirmm.graphik.graal.io.owl.test;



import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.io.owl.OWLParser;
import fr.lirmm.graphik.util.Prefix;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class OWLParserTest {

	@Test
	public void testAssertionObjectProperty() {
		try {
			OWLParser parser = new OWLParser(
					"@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> . "
					+ "@prefix owl: <http://www.w3.org/2002/07/owl#> . "
					+ "@prefix test: <http://test.org/> . "
					+ "test:property rdf:type owl:ObjectProperty . "
					+ "test:a test:property test:b ."
					+ "");
			
			boolean found = false;
			for(Object o : parser) {
				if(!(o instanceof Prefix)) {
					System.out.println(o);
					Atom a = (Atom) o;
					Assert.assertEquals(new Predicate("test:property", 2), a.getPredicate());
					Iterator<Term> it = a.iterator();
					Assert.assertEquals(new Term("test:a", Term.Type.CONSTANT), it.next());
					Assert.assertEquals(new Term("test:b", Term.Type.CONSTANT), it.next());
					found = true;
				}
			}
			Assert.assertTrue("Assertion not found", found);
			
		} catch (Exception e) {
			Assert.assertFalse(e.getMessage(), true);
		}
	}
	
	@Test
	public void testAssertionDataProperty() {
		try {
			OWLParser parser = new OWLParser(
					"@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> . "
					+ "@prefix owl: <http://www.w3.org/2002/07/owl#> . "
					+ "@prefix test: <http://test.org/> . "
					+ "test:property rdf:type owl:DatatypeProperty . "
					+ "test:a test:property \"test\" ."
					+ "");
			
			boolean found = false;
			for(Object o : parser) {
				if(!(o instanceof Prefix)) {
					Atom a = (Atom) o;
					Assert.assertEquals(new Predicate("test:property", 2), a.getPredicate());
					Iterator<Term> it = a.iterator();
					Assert.assertEquals(new Term("test:a", Term.Type.CONSTANT), it.next());
					Assert.assertEquals(new Term("test" , Term.Type.LITERAL), it.next());
					found = true;
				}
			}
			
			Assert.assertTrue("Assertion not found", found);
		} catch (Exception e) {
			Assert.assertFalse(e.getMessage(), true);
		}
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// OBJECT PROPERTY AXIOMS
	// /////////////////////////////////////////////////////////////////////////
	
	@Test
	public void testSubObjectPropertyOf() {
		// q(X, Y) :- p(X, Y).
		try {
			OWLParser parser = new OWLParser(
					"@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> . "
					+ "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> ."
					+ "@prefix owl: <http://www.w3.org/2002/07/owl#> . "
					+ "@prefix test: <http://test.org/> . "
					+ "test:property rdf:type owl:ObjectProperty . "
					+ "test:subProperty rdf:type owl:ObjectProperty  . "
					+ "test:subProperty rdfs:subPropertyOf test:property ."
					+ "");
			
			boolean found = false;
			for(Object o : parser) {
				if(!(o instanceof Prefix)) {
					System.out.println(o);
					Rule r = (Rule) o;
					Atom subProperty = (Atom) r.getBody().iterator().next();
					Assert.assertEquals(new Predicate("test:subProperty", 2), subProperty.getPredicate());
					Atom property = (Atom) r.getHead().iterator().next();
					Assert.assertEquals(new Predicate("test:property", 2), property.getPredicate());

					Assert.assertEquals(property.getTerm(0), property.getTerm(0));
					Assert.assertEquals(property.getTerm(1), property.getTerm(1));
					found = true;
				}
			}
			
			Assert.assertTrue("Assertion not found", found);
		} catch (Exception e) {
			Assert.assertFalse(e.getMessage(), true);
		}
	}
	
	@Test
	public void testRangeObjectProperty() {
		// C(Y) :- p(X, Y).
		try {
			OWLParser parser = new OWLParser(
					"@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> . "
					+ "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> ."
					+ "@prefix owl: <http://www.w3.org/2002/07/owl#> . "
					+ "@prefix test: <http://test.org/> . "
					+ "test:property rdf:type owl:ObjectProperty . "
					+ "test:ClassA rdf:type owl:Class . "
					+ "test:property rdfs:range test:ClassA ."
					+ "");
			
			boolean found = false;
			for(Object o : parser) {
				if(!(o instanceof Prefix)) {
					System.out.println(o);
					Rule r = (Rule) o;
					Atom property = (Atom) r.getBody().iterator().next();
					Assert.assertEquals(new Predicate("test:property", 2), property.getPredicate());
					Atom classs = (Atom) r.getHead().iterator().next();
					Assert.assertEquals(new Predicate("test:ClassA", 1), classs.getPredicate());

					Assert.assertEquals(property.getTerm(1), classs.getTerm(0));
					found = true;
				}
			}
			
			Assert.assertTrue("Assertion not found", found);
		} catch (Exception e) {
			Assert.assertFalse(e.getMessage(), true);
		}
	}
	
	@Test
	public void testDomainObjectProperty() {
		// C(Y) :- p(X, Y).
		try {
			OWLParser parser = new OWLParser(
					"@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> . "
					+ "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> ."
					+ "@prefix owl: <http://www.w3.org/2002/07/owl#> . "
					+ "@prefix test: <http://test.org/> . "
					+ "test:property rdf:type owl:ObjectProperty . "
					+ "test:ClassA rdf:type owl:Class . "
					+ "test:property rdfs:domain test:ClassA ."
					+ "");
			
			boolean found = false;
			for(Object o : parser) {
				if(!(o instanceof Prefix)) {
					System.out.println(o);
					Rule r = (Rule) o;
					Atom property = (Atom) r.getBody().iterator().next();
					Assert.assertEquals(new Predicate("test:property", 2), property.getPredicate());
					Atom classs = (Atom) r.getHead().iterator().next();
					Assert.assertEquals(new Predicate("test:ClassA", 1), classs.getPredicate());

					Assert.assertEquals(property.getTerm(0), classs.getTerm(0));
					found = true;
				}
			}
			
			Assert.assertTrue("Assertion not found", found);
		} catch (Exception e) {
			Assert.assertFalse(e.getMessage(), true);
		}
	}
	
}
