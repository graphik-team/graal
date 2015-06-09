/* Graal v0.7.4
 * Copyright (c) 2014-2015 Inria Sophia Antipolis - Méditerranée / LIRMM (Université de Montpellier & CNRS)
 * All rights reserved.
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * Author(s): Clément SIPIETER
 *            Mélanie KÖNIG
 *            Swan ROCHER
 *            Jean-François BAGET
 *            Michel LECLÈRE
 *            Marie-Laure MUGNIER
 */
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
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.core.term.Term;
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
					Assert.assertEquals(DefaultTermFactory.instance()
							.createConstant("test:a"), it.next());
					Assert.assertEquals(DefaultTermFactory.instance()
							.createConstant("test:b"), it.next());
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
					Assert.assertEquals(DefaultTermFactory.instance()
							.createConstant("test:a"), it.next());
					Assert.assertEquals(DefaultTermFactory.instance()
							.createLiteral("test"), it.next());
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
	
	// /////////////////////////////////////////////////////////////////////////
	// MORE COMPLEX TEST
	// /////////////////////////////////////////////////////////////////////////

	@Test
	public void test1() {
		// !a(x) v b(x) v !c(x).
		try {
			OWLParser parser = new OWLParser(
					"@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> . "
							+ "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> ."
							+ "@prefix owl: <http://www.w3.org/2002/07/owl#> . "
							+ "@prefix test: <http://test.org/> . "
							+ "test:ClassA rdf:type owl:Class . "
							+ "test:ClassB rdf:type owl:Class . "
							+ "test:ClassC rdf:type owl:Class . "
							+ "test:ClassA rdfs:subClassOf [ "
							+ "			owl:unionOf ( test:ClassB [ "
							+ "					owl:complementOf test:ClassC ] ) ] ."
							+ "");

			boolean found = false;
			Predicate classA = new Predicate("test:ClassA", 1);
			Predicate classB = new Predicate("test:ClassB", 1);
			Predicate classC = new Predicate("test:ClassC", 1);

			for (Object o : parser) {
				if (!(o instanceof Prefix)) {
					System.out.println(o);
					Rule r = (Rule) o;
					Iterator<Atom> it = r.getBody().iterator();
					Atom body = it.next();
					Assert.assertTrue(classA.equals(body.getPredicate())
							|| classC.equals(body.getPredicate()));
					body = it.next();
					Assert.assertTrue(classA.equals(body.getPredicate())
							|| classC.equals(body.getPredicate()));

					Atom classs = (Atom) r.getHead().iterator().next();
					Assert.assertEquals(classB,
							classs.getPredicate());

					Assert.assertEquals(body.getTerm(0), classs.getTerm(0));
					found = true;
				}
			}

			Assert.assertTrue("Assertion not found", found);
		} catch (Exception e) {
			Assert.assertFalse(e.getMessage(), true);
		}
	}

}
