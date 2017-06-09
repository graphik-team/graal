/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2017)
 *
 * Contributors :
 *
 * Clément SIPIETER <clement.sipieter@inria.fr>
 * Mélanie KÖNIG
 * Swan ROCHER
 * Jean-François BAGET
 * Michel LECLÈRE
 * Marie-Laure MUGNIER <mugnier@lirmm.fr>
 *
 *
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
/**
 * 
 */
package fr.lirmm.graphik.graal.io.owl.test;

import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.Constant;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Literal;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.core.DefaultNegativeConstraint;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.io.owl.OWL2Parser;
import fr.lirmm.graphik.graal.io.owl.OWL2ParserException;
import fr.lirmm.graphik.util.DefaultURI;
import fr.lirmm.graphik.util.Prefix;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;
import fr.lirmm.graphik.util.stream.IteratorException;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class OWL2ParserTest {

	private static final String PREFIXES = "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> . "
	                                       + "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> ."
	                                       + "@prefix owl: <http://www.w3.org/2002/07/owl#> . "
	                                       + "@prefix xsd: <http://www.w3.org/2001/XMLSchema#> ."
	                                       + "@prefix : <http://test.org/> . ";
	private static final String test = "http://test.org/";
	private static final Predicate A = new Predicate(new DefaultURI(test, "A"), 1);
	private static final Predicate B = new Predicate(new DefaultURI(test, "B"), 1);
	private static final Predicate C = new Predicate(new DefaultURI(test, "C"), 1);
	private static final Predicate D = new Predicate(new DefaultURI(test, "D"), 1);

	private static final Predicate P = new Predicate(new DefaultURI(test, "p"), 2);
	private static final Predicate Q = new Predicate(new DefaultURI(test, "q"), 2);
	private static final Predicate R = new Predicate(new DefaultURI(test, "r"), 2);
	private static final Predicate S = new Predicate(new DefaultURI(test, "s"), 2);

	private static final Constant I1 = DefaultTermFactory.instance().createConstant(new DefaultURI(test, "i1"));

	private static final Constant I2 = DefaultTermFactory.instance().createConstant(new DefaultURI(test, "i2"));

	private static final Constant I3 = DefaultTermFactory.instance().createConstant(new DefaultURI(test, "i3"));

	private static final Literal L1 = DefaultTermFactory.instance().createLiteral(
	    new DefaultURI(Prefix.XSD.getPrefix(), "integer"), 7);

	// /////////////////////////////////////////////////////////////////////////
	// Axioms
	// /////////////////////////////////////////////////////////////////////////

	@Test
	public void subClassOfAxiom() throws OWL2ParserException {
		OWL2Parser parser = new OWL2Parser(PREFIXES
		                                   + ":A rdf:type owl:Class . "
		                                   + ":B rdf:type owl:Class . "
		                                   + ":A rdfs:subClassOf :B. ");

		int nbRules = 0;

		while (parser.hasNext()) {
			Object o = parser.next();
			if (o instanceof Rule) {
				++nbRules;

				Rule r = (Rule) o;
				CloseableIteratorWithoutException<Atom> bodyIt = r.getBody().iterator();
				CloseableIteratorWithoutException<Atom> headIt = r.getHead().iterator();
				Assert.assertTrue(bodyIt.hasNext());
				Assert.assertTrue(headIt.hasNext());

				Atom body = bodyIt.next();
				Atom head = headIt.next();
				Assert.assertTrue(!body.getTerm(0).isConstant());
				Assert.assertTrue(body.getTerm(0).equals(head.getTerm(0)));
				Assert.assertEquals(A, body.getPredicate());
				Assert.assertEquals(B, head.getPredicate());

				Assert.assertFalse(bodyIt.hasNext());
				Assert.assertFalse(headIt.hasNext());
			}
		}
		parser.close();
		Assert.assertEquals("Number of assertions found:", 1, nbRules);
	}

	@Test
	public void equivalentClassAxiom() throws OWL2ParserException {
		OWL2Parser parser = new OWL2Parser(PREFIXES
		                                   + ":A rdf:type owl:Class . "
		                                   + ":B rdf:type owl:Class . "
		                                   + ":A owl:equivalentClass :B. ");

		int nbRules = 0;

		while (parser.hasNext()) {
			Object o = parser.next();
			if (o instanceof Rule) {
				++nbRules;
				Rule r = (Rule) o;
				CloseableIteratorWithoutException<Atom> bodyIt = r.getBody().iterator();
				CloseableIteratorWithoutException<Atom> headIt = r.getHead().iterator();
				Assert.assertTrue(bodyIt.hasNext());
				Assert.assertTrue(headIt.hasNext());

				Atom body = bodyIt.next();
				Atom head = headIt.next();
				Assert.assertTrue(!body.getTerm(0).isConstant());
				Assert.assertTrue(body.getTerm(0).equals(head.getTerm(0)));
				Assert.assertTrue(A.equals(body.getPredicate()) || B.equals(body.getPredicate()));
				Assert.assertTrue(A.equals(head.getPredicate()) || B.equals(head.getPredicate()));

				Assert.assertFalse(bodyIt.hasNext());
				Assert.assertFalse(headIt.hasNext());
			}
		}
		parser.close();
		Assert.assertEquals("Number of assertions found:", 2, nbRules);
	}

	@Test
	public void disjointClassAxiom() throws OWL2ParserException {
		OWL2Parser parser = new OWL2Parser(PREFIXES
		                                   + ":A rdf:type owl:Class . "
		                                   + ":B rdf:type owl:Class . "
		                                   + ":C rdf:type owl:Class . "
		                                   + " _:x rdf:type owl:AllDisjointClasses ; "
		                                   + " owl:members ( :A :B :C ) . ");

		int nbNegativeConstraint = 0;

		while (parser.hasNext()) {
			Object o = parser.next();
			if (o instanceof DefaultNegativeConstraint) {
				++nbNegativeConstraint;
				DefaultNegativeConstraint r = (DefaultNegativeConstraint) o;
				CloseableIteratorWithoutException<Atom> bodyIt = r.getBody().iterator();
				Assert.assertTrue(bodyIt.hasNext());
				Atom body1 = bodyIt.next();
				Assert.assertTrue(bodyIt.hasNext());
				Atom body2 = bodyIt.next();
				Assert.assertFalse(bodyIt.hasNext());

				Assert.assertTrue(!body1.getTerm(0).isConstant());
				Assert.assertTrue(body1.getTerm(0).equals(body2.getTerm(0)));

				Assert.assertTrue(
				    A.equals(body1.getPredicate()) || B.equals(body1.getPredicate()) || C.equals(body1.getPredicate()));
				Assert.assertTrue(
				    A.equals(body2.getPredicate()) || B.equals(body2.getPredicate()) || C.equals(body2.getPredicate()));
			}
		}
		parser.close();
		Assert.assertEquals("Number of assertions found:", 3, nbNegativeConstraint);
	}

	// /////////////////////////////////////////////////////////////////////////
	// EQUIV CLASS EXPRESSION
	// /////////////////////////////////////////////////////////////////////////

	@Test
	public void objectIntersectionOf() throws OWL2ParserException {
		OWL2Parser parser = new OWL2Parser(PREFIXES
		                                   + ":A rdf:type owl:Class . "
		                                   + ":B rdf:type owl:Class . "
		                                   + ":C rdf:type owl:Class . "
		                                   + ":D rdf:type owl:Class . "
		                                   + ":A rdfs:subClassOf [ owl:intersectionOf ( :B :C :D ) ] . ");

		int nbRules = 0;

		while (parser.hasNext()) {
			Object o = parser.next();
			if (o instanceof Rule) {
				++nbRules;

				Rule r = (Rule) o;
				CloseableIteratorWithoutException<Atom> bodyIt = r.getBody().iterator();
				CloseableIteratorWithoutException<Atom> headIt = r.getHead().iterator();
				Assert.assertTrue(bodyIt.hasNext());
				Assert.assertTrue(headIt.hasNext());

				Atom body = bodyIt.next();
				Atom head = headIt.next();
				Assert.assertTrue(!body.getTerm(0).isConstant());
				Assert.assertTrue(body.getTerm(0).equals(head.getTerm(0)));
				Assert.assertEquals(A, body.getPredicate());
				Assert.assertTrue(
				    B.equals(head.getPredicate()) || C.equals(head.getPredicate()) || D.equals(head.getPredicate()));

				Assert.assertFalse(bodyIt.hasNext());
				Assert.assertFalse(headIt.hasNext());
			}
		}
		parser.close();
		Assert.assertEquals("Number of assertions found:", 3, nbRules);
	}

	@Test
	public void objectSomeValuesFrom() throws OWL2ParserException {
		OWL2Parser parser = new OWL2Parser(PREFIXES
		                                   + ":A rdf:type owl:Class . "
		                                   + ":B rdf:type owl:Class . "
		                                   + ":p rdf:type owl:ObjectProperty . "
		                                   + "	[rdf:type owl:Restriction ;	owl:onProperty :p ;"
		                                   + "			owl:someValuesFrom :A]"
		                                   + "	rdfs:subClassOf :B . ");

		int nbRules = 0;

		while (parser.hasNext()) {
			Object o = parser.next();
			if (o instanceof Rule) {
				++nbRules;

				Rule r = (Rule) o;
				CloseableIteratorWithoutException<Atom> bodyIt = r.getBody().iterator();
				CloseableIteratorWithoutException<Atom> headIt = r.getHead().iterator();
				Assert.assertTrue(bodyIt.hasNext());
				Assert.assertTrue(headIt.hasNext());

				Atom body1 = bodyIt.next();
				Atom body2 = bodyIt.next();
				Atom head = headIt.next();
				Assert.assertFalse(bodyIt.hasNext());
				Assert.assertFalse(headIt.hasNext());

				Assert.assertTrue(A.equals(body1.getPredicate()) || A.equals(body2.getPredicate()));
				Assert.assertTrue(P.equals(body1.getPredicate()) || P.equals(body2.getPredicate()));
				Assert.assertTrue(A.equals(body1.getPredicate()) || P.equals(body1.getPredicate()));
				Assert.assertTrue(A.equals(body2.getPredicate()) || P.equals(body2.getPredicate()));

				Assert.assertEquals(B, head.getPredicate());

			}
		}
		parser.close();
		Assert.assertEquals("Number of assertions found:", 1, nbRules);
	}

	@Test
	public void objectHasValue() throws OWL2ParserException {
		OWL2Parser parser = new OWL2Parser(PREFIXES
		                                   + ":A rdf:type owl:Class . "
		                                   + ":p rdf:type owl:ObjectProperty . "
		                                   + "	[rdf:type owl:Restriction ;	owl:onProperty :p ;"
		                                   + "			owl:hasValue :i1]"
		                                   + "	rdfs:subClassOf :A . ");

		int nbRules = 0;

		while (parser.hasNext()) {
			Object o = parser.next();
			if (o instanceof Rule) {
				++nbRules;

				Rule r = (Rule) o;
				CloseableIteratorWithoutException<Atom> bodyIt = r.getBody().iterator();
				CloseableIteratorWithoutException<Atom> headIt = r.getHead().iterator();
				Assert.assertTrue(bodyIt.hasNext());
				Assert.assertTrue(headIt.hasNext());

				Atom body = bodyIt.next();
				Atom head = headIt.next();
				Assert.assertFalse(bodyIt.hasNext());
				Assert.assertFalse(headIt.hasNext());

				Assert.assertEquals(P, body.getPredicate());
				Assert.assertEquals(A, head.getPredicate());

				Assert.assertTrue(!body.getTerm(0).isConstant());
				Assert.assertEquals(body.getTerm(0), head.getTerm(0));
				Assert.assertEquals(I1, body.getTerm(1));
			}
		}
		parser.close();
		Assert.assertEquals("Number of assertions found:", 1, nbRules);
	}

	@Test
	public void objectHasSelf() throws OWL2ParserException {
		OWL2Parser parser = new OWL2Parser(PREFIXES
		                                   + ":A rdf:type owl:Class . "
		                                   + ":p rdf:type owl:ObjectProperty . "
		                                   + "	[rdf:type owl:Restriction ;	owl:onProperty :p ;"
		                                   + "			owl:hasSelf \"true\"^^xsd:boolean]"
		                                   + "	rdfs:subClassOf :A . ");

		int nbRules = 0;

		while (parser.hasNext()) {
			Object o = parser.next();
			if (o instanceof Rule) {
				++nbRules;

				Rule r = (Rule) o;
				CloseableIteratorWithoutException<Atom> bodyIt = r.getBody().iterator();
				CloseableIteratorWithoutException<Atom> headIt = r.getHead().iterator();
				Assert.assertTrue(bodyIt.hasNext());
				Assert.assertTrue(headIt.hasNext());

				Atom body = bodyIt.next();
				Atom head = headIt.next();
				Assert.assertFalse(bodyIt.hasNext());
				Assert.assertFalse(headIt.hasNext());

				Assert.assertEquals(P, body.getPredicate());
				Assert.assertEquals(A, head.getPredicate());

				Assert.assertTrue(!body.getTerm(0).isConstant());
				Assert.assertEquals(body.getTerm(0), body.getTerm(1));
				Assert.assertEquals(body.getTerm(0), head.getTerm(0));
			}
		}
		parser.close();
		Assert.assertEquals("Number of assertions found:", 1, nbRules);
	}

	@Test
	public void objectMinCardinality0() throws OWL2ParserException {
		OWL2Parser parser = new OWL2Parser(PREFIXES
		                                   + ":A rdf:type owl:Class . "
		                                   + ":B rdf:type owl:Class . "
		                                   + ":p rdf:type owl:ObjectProperty . "
		                                   + " [ rdf:type owl:Restriction ; "
		                                   + " owl:onProperty :p ; "
		                                   + " owl:onClass :A ; "
		                                   + " owl:minCardinality 0 ] "
		                                   + "  "
		                                   + " rdfs:subClassOf :B . ");

		int nbRules = 0;

		while (parser.hasNext()) {
			Object o = parser.next();
			if (o instanceof Rule) {
				++nbRules;

				Rule r = (Rule) o;
				CloseableIteratorWithoutException<Atom> bodyIt = r.getBody().iterator();
				CloseableIteratorWithoutException<Atom> headIt = r.getHead().iterator();
				Assert.assertTrue(bodyIt.hasNext());
				Assert.assertTrue(headIt.hasNext());

				Atom body = bodyIt.next();
				Atom head = headIt.next();
				Assert.assertFalse(bodyIt.hasNext());
				Assert.assertFalse(headIt.hasNext());

				Assert.assertEquals(Predicate.TOP, body.getPredicate());
				Assert.assertEquals(B, head.getPredicate());

				Assert.assertTrue(!body.getTerm(0).isConstant());
				Assert.assertEquals(body.getTerm(0), head.getTerm(0));
			}
		}
		parser.close();
		Assert.assertEquals("Number of assertions found:", 1, nbRules);
	}

	@Test
	public void objectMinCardinality0InRightHandSide() throws OWL2ParserException {
		OWL2Parser parser = new OWL2Parser(PREFIXES
		                                   + ":A rdf:type owl:Class . "
		                                   + ":B rdf:type owl:Class . "
		                                   + ":p rdf:type owl:ObjectProperty . "
		                                   + " :B rdfs:subClassOf [ rdf:type owl:Restriction ; "
		                                   + " owl:onProperty :p ; "
		                                   + " owl:onClass :A ; "
		                                   + " owl:minCardinality 0 ] . ");

		int nbAssertions = 0;

		while (parser.hasNext()) {
			Object o = parser.next();
			if (!(o instanceof Prefix)) {
				++nbAssertions;
			}
		}
		parser.close();
		Assert.assertEquals("Number of assertions found:", 0, nbAssertions);
	}

	@Test
	public void complexObjectMinCardinality0InRightHandSide() throws OWL2ParserException {
		OWL2Parser parser = new OWL2Parser(PREFIXES
		                                   + ":A rdf:type owl:Class . "
		                                   + ":B rdf:type owl:Class . "
		                                   + ":p rdf:type owl:ObjectProperty . "
		                                   + " :B rdfs:subClassOf [a owl:Restriction; owl:onProperty :p; owl:someValuesFrom [ rdf:type owl:Restriction ; "
		                                   + " owl:onProperty :p ; "
		                                   + " owl:onClass :A ; "
		                                   + " owl:minCardinality 0 ] ]. ");

		int nbAssertions = 0;

		while (parser.hasNext()) {
			Object o = parser.next();
			if (!(o instanceof Prefix)) {
				++nbAssertions;
			}
		}
		parser.close();
		Assert.assertEquals("Number of assertions found:", 1, nbAssertions);
	}

	@Test
	public void objectMinCardinality1() throws OWL2ParserException {
		OWL2Parser parser = new OWL2Parser(PREFIXES
		                                   + ":A rdf:type owl:Class . "
		                                   + ":B rdf:type owl:Class . "
		                                   + ":p rdf:type owl:ObjectProperty . "
		                                   + " [ rdf:type owl:Restriction ; "
		                                   + " owl:onProperty :p ; "
		                                   + " owl:onClass :A ; "
		                                   + " owl:minCardinality 1 ] "
		                                   + "  "
		                                   + " rdfs:subClassOf :B . ");

		int nbRules = 0;

		while (parser.hasNext()) {
			Object o = parser.next();
			if (o instanceof Rule) {
				++nbRules;

				Rule r = (Rule) o;
				CloseableIteratorWithoutException<Atom> bodyIt = r.getBody().iterator();
				CloseableIteratorWithoutException<Atom> headIt = r.getHead().iterator();
				Assert.assertTrue(bodyIt.hasNext());
				Assert.assertTrue(headIt.hasNext());

				Atom body1 = bodyIt.next();
				Atom body2 = bodyIt.next();
				Atom head = headIt.next();
				Assert.assertFalse(bodyIt.hasNext());
				Assert.assertFalse(headIt.hasNext());

				Assert.assertTrue(A.equals(body1.getPredicate()) || A.equals(body2.getPredicate()));
				Assert.assertTrue(P.equals(body1.getPredicate()) || P.equals(body2.getPredicate()));
				Assert.assertTrue(A.equals(body1.getPredicate()) || P.equals(body1.getPredicate()));
				Assert.assertTrue(A.equals(body2.getPredicate()) || P.equals(body2.getPredicate()));

				Assert.assertEquals(B, head.getPredicate());

			}
		}
		parser.close();
		Assert.assertEquals("Number of assertions found:", 1, nbRules);
	}

	@Test
	public void objectMaxCardinality0() throws OWL2ParserException {
		OWL2Parser parser = new OWL2Parser(PREFIXES
		                                   + ":A rdf:type owl:Class . "
		                                   + ":B rdf:type owl:Class . "
		                                   + ":p rdf:type owl:ObjectProperty . "
		                                   + " :A rdfs:subClassOf [ rdf:type owl:Restriction ; "
		                                   + " owl:onProperty :p ; "
		                                   + " owl:onClass :B ; "
		                                   + " owl:maxCardinality 0 ] . ");

		int nbRules = 0;

		while (parser.hasNext()) {
			Object o = parser.next();
			if (o instanceof DefaultNegativeConstraint) {
				++nbRules;
			}
		}
		parser.close();
		Assert.assertEquals("Number of assertions found:", 1, nbRules);
	}

	@Test
	public void objectMaxCardinality1() throws OWL2ParserException {
		OWL2Parser parser = new OWL2Parser(PREFIXES
		                                   + ":A rdf:type owl:Class . "
		                                   + ":B rdf:type owl:Class . "
		                                   + ":p rdf:type owl:ObjectProperty . "
		                                   + " :A rdfs:subClassOf [ rdf:type owl:Restriction ; "
		                                   + " owl:onProperty :p ; "
		                                   + " owl:onClass :B ; "
		                                   + " owl:maxCardinality 1 ] . ");

		int nbRules = 0;

		while (parser.hasNext()) {
			if (parser.next() instanceof Rule)
				++nbRules;
		}
		parser.close();

		Assert.assertEquals("Number of assertions found:", 1, nbRules);
	}

	@Test
	public void objectMaxCardinality1WithInverseOf() throws OWL2ParserException {
		OWL2Parser parser = new OWL2Parser(PREFIXES
		                                   + ":A a owl:Class. "
		                                   + ":p a owl:ObjectProperty. "
		                                   + ":A rdfs:subClassOf [ rdf:type owl:Restriction ; "
		                                   + "                     owl:onProperty [owl:inverseOf :p] ; "
		                                   + "                     owl:maxCardinality 1 ]. ");
		int nbRules = 0;

		while (parser.hasNext()) {
			if (parser.next() instanceof Rule)
				++nbRules;
		}
		parser.close();

		Assert.assertEquals("Number of assertions found:", 1, nbRules);
	}

	@Test
	public void complexObjectMaxCardinality1() throws OWL2ParserException {
		OWL2Parser parser = new OWL2Parser(PREFIXES
		                                   + ":A rdf:type owl:Class . "
		                                   + ":B rdf:type owl:Class . "
		                                   + ":C rdf:type owl:Class . "
		                                   + ":p rdf:type owl:ObjectProperty . "
		                                   + ":q rdf:type owl:ObjectProperty . "
		                                   + ":r rdf:type owl:ObjectProperty . "
		                                   + " [a owl:Restriction; owl:onProperty :r; owl:someValuesFrom :A ] rdfs:subClassOf [ rdf:type owl:Restriction ; "
		                                   + " owl:onProperty :p ; "
		                                   + " owl:onClass [ owl:unionOf ( [a owl:Restriction; owl:onProperty :q; owl:someValuesFrom :B ] :C ) ] ; "
		                                   + " owl:maxCardinality 1 ] . ");

		int nbRules = 0;

		while (parser.hasNext()) {
			if (parser.next() instanceof Rule)
				++nbRules;
		}
		parser.close();

		Assert.assertEquals("Number of assertions found:", 3, nbRules);
	}

	@Test
	public void objectExactCardinality0() throws OWL2ParserException {
		OWL2Parser parser = new OWL2Parser(PREFIXES
		                                   + ":A rdf:type owl:Class . "
		                                   + ":B rdf:type owl:Class . "
		                                   + ":p rdf:type owl:ObjectProperty . "
		                                   + " :A rdfs:subClassOf [ rdf:type owl:Restriction ; "
		                                   + " owl:onProperty :p ; "
		                                   + " owl:onClass :B ; "
		                                   + " owl:qualifiedCardinality 0 ] . ");

		int nbRules = 0;

		while (parser.hasNext()) {
			if (parser.next() instanceof Rule)
				++nbRules;
		}
		parser.close();

		Assert.assertEquals("Number of assertions found:", 1, nbRules);
	}

	@Test
	public void objectExactCardinality1() throws OWL2ParserException {
		OWL2Parser parser = new OWL2Parser(PREFIXES
		                                   + ":A rdf:type owl:Class . "
		                                   + ":B rdf:type owl:Class . "
		                                   + ":p rdf:type owl:ObjectProperty . "
		                                   + " :A rdfs:subClassOf [ rdf:type owl:Restriction ; "
		                                   + " owl:onProperty :p ; "
		                                   + " owl:onClass :B ; "
		                                   + " owl:qualifiedCardinality 1 ] . ");

		int nbRules = 0;

		while (parser.hasNext()) {
			if (parser.next() instanceof Rule)
				++nbRules;
		}
		parser.close();

		Assert.assertEquals("Number of assertions found:", 2, nbRules);
	}

	@Test
	public void objectOneOf1() throws OWL2ParserException {
		OWL2Parser parser = new OWL2Parser(PREFIXES
		                                   + ":A rdf:type owl:Class . "
		                                   + " [ owl:oneOf ( :i1 ) ] "
		                                   + " rdfs:subClassOf :A . ");

		int nbRules = 0;

		while (parser.hasNext()) {
			Object o = parser.next();
			if (o instanceof InMemoryAtomSet) {
				++nbRules;

				InMemoryAtomSet a = (InMemoryAtomSet) o;
				CloseableIteratorWithoutException<Atom> bodyIt = a.iterator();
				Assert.assertTrue(bodyIt.hasNext());

				Atom body = bodyIt.next();
				Assert.assertFalse(bodyIt.hasNext());

				Assert.assertEquals(A, body.getPredicate());

				Assert.assertTrue(!body.getTerm(0).isConstant() || body.getTerm(0).equals(I1));
			}
		}
		parser.close();
		Assert.assertEquals("Number of assertions found:", 1, nbRules);
	}

	// /////////////////////////////////////////////////////////////////////////
	// SUPER CLASS EXPRESSION
	// /////////////////////////////////////////////////////////////////////////

	@Test
	public void objectComplementOf() throws OWL2ParserException {
		OWL2Parser parser = new OWL2Parser(PREFIXES
		                                   + ":A rdf:type owl:Class . "
		                                   + ":B rdf:type owl:Class . "
		                                   + ":A rdfs:subClassOf [ owl:complementOf :B ]. ");

		int nbRules = 0;

		while (parser.hasNext()) {
			Object o = parser.next();
			if (o instanceof DefaultNegativeConstraint) {
				++nbRules;

				DefaultNegativeConstraint r = (DefaultNegativeConstraint) o;
				CloseableIteratorWithoutException<Atom> bodyIt = r.getBody().iterator();
				Assert.assertTrue(bodyIt.hasNext());
				Atom body1 = bodyIt.next();
				Assert.assertTrue(bodyIt.hasNext());
				Atom body2 = bodyIt.next();
				Assert.assertFalse(bodyIt.hasNext());

				Assert.assertTrue(!body1.getTerm(0).isConstant());
				Assert.assertTrue(body1.getTerm(0).equals(body2.getTerm(0)));

				Assert.assertTrue(A.equals(body1.getPredicate()) || B.equals(body1.getPredicate()));
				Assert.assertTrue(A.equals(body2.getPredicate()) || B.equals(body2.getPredicate()));
			}
		}
		parser.close();
		Assert.assertEquals("Number of assertions found:", 1, nbRules);
	}

	@Test
	public void objectAllValuesFrom() throws OWL2ParserException {
		OWL2Parser parser = new OWL2Parser(PREFIXES
		                                   + ":A rdf:type owl:Class . "
		                                   + ":B rdf:type owl:Class . "
		                                   + ":p rdf:type owl:ObjectProperty . "
		                                   + ":A rdfs:subClassOf [ rdf:type owl:Restriction ;"
		                                   + "							owl:onProperty :p ;"
		                                   + "	                        owl:allValuesFrom :B ]. ");

		int nbRules = 0;

		while (parser.hasNext()) {
			Object o = parser.next();
			if (o instanceof Rule) {
				++nbRules;

				Rule r = (Rule) o;
				CloseableIteratorWithoutException<Atom> bodyIt = r.getBody().iterator();
				CloseableIteratorWithoutException<Atom> headIt = r.getHead().iterator();
				Assert.assertTrue(bodyIt.hasNext());
				Assert.assertTrue(headIt.hasNext());

				Atom body1 = bodyIt.next();
				Atom body2 = bodyIt.next();
				Atom head = headIt.next();
				Assert.assertTrue(!head.getTerm(0).isConstant());
				Assert.assertEquals(body1.getTerm(0), body2.getTerm(0));

				Assert.assertEquals(B, head.getPredicate());
				Assert.assertTrue(A.equals(body1.getPredicate()) || A.equals(body2.getPredicate()));
				Assert.assertTrue(P.equals(body1.getPredicate()) || P.equals(body2.getPredicate()));

				if (P.equals(body1.getPredicate())) {
					Assert.assertEquals(body1.getTerm(1), head.getTerm(0));
				} else {
					Assert.assertEquals(body2.getTerm(1), head.getTerm(0));
				}

				Assert.assertFalse(bodyIt.hasNext());
				Assert.assertFalse(headIt.hasNext());
			}
		}
		parser.close();
		Assert.assertEquals("Number of assertions found:", 1, nbRules);
	}

	// /////////////////////////////////////////////////////////////////////////
	// SUB CLASS EXPRESSION
	// /////////////////////////////////////////////////////////////////////////

	@Test
	public void objectUnionOf() throws OWL2ParserException {
		OWL2Parser parser = new OWL2Parser(PREFIXES
		                                   + ":A rdf:type owl:Class . "
		                                   + ":B rdf:type owl:Class . "
		                                   + ":C rdf:type owl:Class . "
		                                   + ":D rdf:type owl:Class . "
		                                   + "[ owl:unionOf ( :B :C :D ) ] rdfs:subClassOf :A . ");

		int nbRules = 0;

		while (parser.hasNext()) {
			Object o = parser.next();
			if (o instanceof Rule) {
				++nbRules;

				Rule r = (Rule) o;
				CloseableIteratorWithoutException<Atom> bodyIt = r.getBody().iterator();
				CloseableIteratorWithoutException<Atom> headIt = r.getHead().iterator();
				Assert.assertTrue(bodyIt.hasNext());
				Assert.assertTrue(headIt.hasNext());

				Atom body = bodyIt.next();
				Atom head = headIt.next();
				Assert.assertTrue(!body.getTerm(0).isConstant());
				Assert.assertTrue(body.getTerm(0).equals(head.getTerm(0)));
				Assert.assertEquals(A, head.getPredicate());
				Assert.assertTrue(
				    B.equals(body.getPredicate()) || C.equals(body.getPredicate()) || D.equals(body.getPredicate()));

				Assert.assertFalse(bodyIt.hasNext());
				Assert.assertFalse(headIt.hasNext());
			}
		}
		parser.close();
		Assert.assertEquals("Number of assertions found:", 3, nbRules);
	}

	@Test
	public void objectOneOf() throws OWL2ParserException {
		OWL2Parser parser = new OWL2Parser(PREFIXES + ":A rdf:type owl:Class . "
				+ "[ owl:oneOf ( :i1 :i2 :i3 ) ] rdfs:subClassOf :A . ");

		int nbRules = 0;

		while (parser.hasNext()) {
			Object o = parser.next();
			if (o instanceof InMemoryAtomSet) {
				CloseableIteratorWithoutException<Atom> it = ((InMemoryAtomSet) o).iterator();
				while(it.hasNext()) {
					Atom a = it.next();
					++nbRules;
					Assert.assertTrue(a.getTerm(0).equals(I1)
							|| a.getTerm(0).equals(I2)
							|| a.getTerm(0).equals(I3));
					Assert.assertEquals(A, a.getPredicate());
				}
			}
		}
		parser.close();
		Assert.assertEquals("Number of assertions found:", 3, nbRules);
	}

	@Test
	public void complexSubClassExpression() throws OWL2ParserException {
		OWL2Parser parser = new OWL2Parser(PREFIXES
		                                   + ":A rdf:type owl:Class . "
		                                   + ":B rdf:type owl:Class . "
		                                   + ":C rdf:type owl:Class . "
		                                   + ":D rdf:type owl:Class . "
		                                   + ":E rdf:type owl:Class . "
		                                   + ":p rdf:type owl:ObjectProperty . "
		                                   + "[ owl:intersectionOf ( "
		                                   + "		:B [ owl:unionOf ( "
		                                   + "				:E [rdf:type owl:Restriction ;	"
		                                   + "					owl:onProperty :p ;"
		                                   + "					owl:someValuesFrom ["
		                                   + "							owl:unionOf ( :C :D ) ]"
		                                   + "					] ) "
		                                   + "			] )"
		                                   + "] rdfs:subClassOf :A . ");

		int nbRules = 0;

		while (parser.hasNext()) {
			Object o = parser.next();
			if (o instanceof Rule) {
				++nbRules;

				Rule r = (Rule) o;
				CloseableIteratorWithoutException<Atom> bodyIt = r.getBody().iterator();
				CloseableIteratorWithoutException<Atom> headIt = r.getHead().iterator();
				Assert.assertTrue(bodyIt.hasNext());
				bodyIt.next();
				Assert.assertTrue(bodyIt.hasNext());
				Assert.assertTrue(headIt.hasNext());

				Atom head = headIt.next();
				Assert.assertEquals(A, head.getPredicate());

				Assert.assertFalse(headIt.hasNext());
			}
		}
		parser.close();
		Assert.assertEquals("Number of assertions found:", 3, nbRules);
	}

	// /////////////////////////////////////////////////////////////////////////
	// DISJUNCTIVE NORMAL FORM TEST
	// /////////////////////////////////////////////////////////////////////////

	@Test
	public void intersectionOfUnion() throws OWL2ParserException {
		OWL2Parser parser = new OWL2Parser(PREFIXES
		                                   + ":ClassA rdf:type owl:Class . "
		                                   + ":ClassB rdf:type owl:Class . "
		                                   + ":ClassC rdf:type owl:Class . "
		                                   + ":ClassD rdf:type owl:Class . "
		                                   + "[ owl:intersectionOf ( :ClassA [ owl:unionOf ( :ClassB :ClassC ) ] ) ] rdfs:subClassOf :ClassD. ");

		int nbRules = 0;

		while (parser.hasNext()) {
			Object o = parser.next();
			if (o instanceof Rule) {

				++nbRules;
			}
		}
		parser.close();
		Assert.assertEquals("Number of assertions found:", 2, nbRules);
	}

	@Test
	public void unionOfUnion() throws OWL2ParserException {
		OWL2Parser parser = new OWL2Parser(PREFIXES
		                                   + ":ClassA rdf:type owl:Class . "
		                                   + ":ClassB rdf:type owl:Class . "
		                                   + ":ClassC rdf:type owl:Class . "
		                                   + ":ClassD rdf:type owl:Class . "
		                                   + "[ owl:unionOf ( :ClassA [ owl:unionOf ( :ClassB :ClassC ) ] ) ] rdfs:subClassOf :ClassD. ");

		int nbRules = 0;

		while (parser.hasNext()) {
			Object o = parser.next();
			if (o instanceof Rule) {

				++nbRules;
			}
		}
		parser.close();
		Assert.assertEquals("Number of assertions found:", 3, nbRules);
	}

	@Test
	public void unionOfUnionOfUnion() throws OWL2ParserException {
		OWL2Parser parser = new OWL2Parser(PREFIXES
		                                   + ":ClassA rdf:type owl:Class . "
		                                   + ":ClassB rdf:type owl:Class . "
		                                   + ":ClassC rdf:type owl:Class . "
		                                   + ":ClassD rdf:type owl:Class . "
		                                   + ":ClassE rdf:type owl:Class . "
		                                   + "[ owl:unionOf ( :ClassA [ owl:unionOf ( :ClassB [ owl:unionOf ( :ClassC :ClassD ) ] ) ] ) ] rdfs:subClassOf :ClassE. ");

		int nbRules = 0;

		while (parser.hasNext()) {
			Object o = parser.next();
			if (o instanceof Rule) {

				++nbRules;
			}
		}
		parser.close();
		Assert.assertEquals("Number of assertions found:", 4, nbRules);
	}

	@Test
	public void unionOfObjectOneOf() throws OWL2ParserException {
		OWL2Parser parser = new OWL2Parser(PREFIXES
		                                   + ":A rdf:type owl:Class . "
		                                   + "[ owl:unionOf ( [ owl:oneOf ( :i1 :i2 ) ] [ owl:oneOf ( :i3 :i4 ) ] ) ] rdfs:subClassOf :A. ");

		int nbRules = 0;

		while (parser.hasNext()) {
			Object o = parser.next();
			if (o instanceof InMemoryAtomSet) {
				CloseableIteratorWithoutException<Atom> it = ((InMemoryAtomSet) o).iterator();
				while (it.hasNext()) {
					it.next();
					++nbRules;
				}
			}
		}
		parser.close();
		Assert.assertEquals("Number of assertions found:", 4, nbRules);
	}

	@Test
	public void intersectionOfObjectOneOfAndUnionOf() throws OWL2ParserException {
		OWL2Parser parser = new OWL2Parser(PREFIXES
		                                   + ":ClassA rdf:type owl:Class . "
		                                   + ":ClassB rdf:type owl:Class . "
		                                   + ":ClassC rdf:type owl:Class . "
		                                   + ":ClassD rdf:type owl:Class . "
		                                   + ":ClassE rdf:type owl:Class . "
		                                   + "[ owl:intersectionOf ( [ owl:oneOf ( :i1 :i2 ) ] [ owl:unionOf ( :ClassC :ClassD ) ] ) ] rdfs:subClassOf :ClassE. ");

		int nbRules = 0;

		while (parser.hasNext()) {
			Object o = parser.next();
			if (o instanceof Rule) {

				++nbRules;
			}
		}
		parser.close();
		Assert.assertEquals("Number of assertions found:", 4, nbRules);
	}

	@Test
	public void example6() throws OWL2ParserException {
		OWL2Parser parser = new OWL2Parser(PREFIXES
		                                   + ":A rdf:type owl:Class . "
		                                   + ":B rdf:type owl:Class . "
		                                   + ":C rdf:type owl:Class . "
		                                   + ":D rdf:type owl:Class . "
		                                   + ":p rdf:type owl:ObjectProperty . "
		                                   + ":q rdf:type owl:ObjectProperty . "
		                                   + ":r rdf:type owl:ObjectProperty . "
		                                   + "[owl:unionOf ("
		                                   + "		[owl:oneOf ( :i )] "
		                                   + "		[rdf:type owl:Restriction ;"
		                                   + "			owl:onProperty :p ;"
		                                   + "			owl:someValuesFrom :A]"
		                                   + "	)] "
		                                   + "	rdfs:subClassOf "
		                                   + "[owl:intersectionOf ( "
		                                   + "		[rdf:type owl:Restriction ;"
		                                   + "			owl:onProperty :q ;"
		                                   + "			owl:someValuesFrom :B]"
		                                   + "		[owl:complementOf :C ] "
		                                   + "		[rdf:type owl:Restriction ;"
		                                   + "			owl:onProperty :r ;"
		                                   + "			owl:allValuesFrom :D]"
		                                   + "	)] . ");

		int nbRules = 0;

		while (parser.hasNext()) {
			Object o = parser.next();
			if (o instanceof Rule || o instanceof AtomSet) {
				++nbRules;

			}
		}
		parser.close();
		Assert.assertEquals("Number of assertions found:", 6, nbRules);
	}

	// /////////////////////////////////////////////////////////////////////////
	// HAS KEY
	// /////////////////////////////////////////////////////////////////////////

	@Test
	public void hasKey() throws OWL2ParserException {
		OWL2Parser parser = new OWL2Parser(PREFIXES
		                                   + ":A a owl:Class . "
		                                   + ":p a owl:ObjectProperty . "
		                                   + ":q a owl:ObjectProperty . "
		                                   + ":A owl:hasKey (:p :q) .");

		int nbAssertions = 0;
		while (parser.hasNext()) {
			Object o = parser.next();
			if (!(o instanceof Prefix)) {
				Assert.assertTrue(o instanceof Rule);
				++nbAssertions;
			}
		}
		parser.close();
		Assert.assertEquals("Number of assertions found:", 1, nbAssertions);
	}

	@Test
	public void hasKeyWithUnion() throws OWL2ParserException {
		OWL2Parser parser = new OWL2Parser(PREFIXES
		                                   + ":A a owl:Class . "
		                                   + ":B a owl:Class . "
		                                   + ":p a owl:ObjectProperty . "
		                                   + ":q a owl:DatatypeProperty . "
		                                   + "[owl:unionOf( :A :B [owl:oneOf (:i1)] ) ] owl:hasKey (:p :q) .");

		int nbAssertions = 0;
		while (parser.hasNext()) {
			Object o = parser.next();
			if (!(o instanceof Prefix)) {
				Assert.assertTrue(o instanceof Rule);
				++nbAssertions;
			}
		}
		parser.close();
		Assert.assertEquals("Number of assertions found:", 6, nbAssertions);
	}
	// /////////////////////////////////////////////////////////////////////////
	// OBJECT PROPERTY AXIOMS
	// /////////////////////////////////////////////////////////////////////////

	@Test
	public void subObjectPropertyOf() {
		// q(X, Y) :- p(X, Y).
		try {
			OWL2Parser parser = new OWL2Parser(PREFIXES
			                                   + ":p rdf:type owl:ObjectProperty . "
			                                   + ":q rdf:type owl:ObjectProperty . "
			                                   + ":p rdfs:subPropertyOf :q .");

			int nbRules = 0;
			while (parser.hasNext()) {
				Object o = parser.next();
				if (o instanceof Rule) {

					++nbRules;
					Rule r = (Rule) o;
					Atom subProperty = (Atom) r.getBody().iterator().next();
					Atom property = (Atom) r.getHead().iterator().next();

					Assert.assertEquals(P, subProperty.getPredicate());
					Assert.assertEquals(Q, property.getPredicate());

					Assert.assertEquals(subProperty.getTerm(0), property.getTerm(0));
					Assert.assertEquals(subProperty.getTerm(1), property.getTerm(1));
				}
			}
			parser.close();
			Assert.assertEquals("Number of assertions found:", 1, nbRules);
		} catch (Exception e) {
			Assert.assertFalse(e.getMessage(), true);
		}
	}

	@Test
	public void equivalentProperty() {
		// q(X, Y) <-> p(X, Y).
		try {
			OWL2Parser parser = new OWL2Parser(PREFIXES
			                                   + ":p rdf:type owl:ObjectProperty . "
			                                   + ":q rdf:type owl:ObjectProperty . "
			                                   + ":p owl:equivalentProperty :q .");

			int nbRules = 0;
			while (parser.hasNext()) {
				Object o = parser.next();
				if (o instanceof Rule) {
					++nbRules;
					Rule r = (Rule) o;
					Atom subProperty = (Atom) r.getBody().iterator().next();
					Atom property = (Atom) r.getHead().iterator().next();

					Assert.assertTrue(Q.equals(property.getPredicate()) || P.equals(property.getPredicate()));
					Assert.assertTrue(Q.equals(subProperty.getPredicate()) || P.equals(subProperty.getPredicate()));

					Assert.assertEquals(property.getTerm(0), subProperty.getTerm(0));
					Assert.assertEquals(property.getTerm(1), subProperty.getTerm(1));
				}
			}
			parser.close();
			Assert.assertEquals("Number of assertions found:", 2, nbRules);
		} catch (Exception e) {
			Assert.assertFalse(e.getMessage(), true);
		}
	}

	@Test
	public void inverseObjectProperty() {
		// q(X, Y) <-> p(Y, X).
		try {
			OWL2Parser parser = new OWL2Parser(PREFIXES
			                                   + ":p rdf:type owl:ObjectProperty . "
			                                   + ":q rdf:type owl:ObjectProperty . "
			                                   + ":p owl:inverseOf :q .");

			int nbRules = 0;
			while (parser.hasNext()) {
				Object o = parser.next();
				if (o instanceof Rule) {
					++nbRules;
					Rule r = (Rule) o;
					Atom subProperty = (Atom) r.getBody().iterator().next();
					Atom property = (Atom) r.getHead().iterator().next();

					Assert.assertTrue(Q.equals(property.getPredicate()) || P.equals(property.getPredicate()));
					Assert.assertTrue(Q.equals(subProperty.getPredicate()) || P.equals(subProperty.getPredicate()));

					Assert.assertEquals(property.getTerm(0), subProperty.getTerm(1));
					Assert.assertEquals(property.getTerm(1), subProperty.getTerm(0));
				}
			}
			parser.close();
			Assert.assertEquals("Number of assertions found:", 2, nbRules);
		} catch (Exception e) {
			Assert.assertFalse(e.getMessage(), true);
		}
	}

	@Test
	public void disjointProperty() {
		// ! :- q(X, Y), p(X, Y).
		try {
			OWL2Parser parser = new OWL2Parser(PREFIXES
			                                   + ":p rdf:type owl:ObjectProperty . "
			                                   + ":q rdf:type owl:ObjectProperty . "
			                                   + ":p owl:propertyDisjointWith :q .");

			int nbRules = 0;
			while (parser.hasNext()) {
				Object o = parser.next();
				if (o instanceof Rule) {
					++nbRules;
					Rule r = (Rule) o;

					CloseableIteratorWithoutException<Atom> it = r.getBody().iterator();
					Atom a1 = (Atom) it.next();
					Atom a2 = (Atom) it.next();
					Assert.assertFalse(it.hasNext());

					Assert.assertTrue(P.equals(a1.getPredicate()) || Q.equals(a1.getPredicate()));
					Assert.assertTrue(P.equals(a2.getPredicate()) || Q.equals(a2.getPredicate()));
					Assert.assertTrue(P.equals(a2.getPredicate()) || P.equals(a1.getPredicate()));
					Assert.assertTrue(Q.equals(a2.getPredicate()) || Q.equals(a1.getPredicate()));

					Assert.assertEquals(a1.getTerm(0), a2.getTerm(0));
					Assert.assertEquals(a1.getTerm(1), a2.getTerm(1));
				}
			}
			parser.close();
			Assert.assertEquals("Number of assertions found:", 1, nbRules);
		} catch (Exception e) {
			Assert.assertFalse(e.getMessage(), true);
		}
	}

	@Test
	public void functionalObjectProperty() {
		// Y = Z :- p(X, Y), p(X, Z).
		try {
			OWL2Parser parser = new OWL2Parser(PREFIXES
			                                   + ":p rdf:type owl:ObjectProperty ; rdf:type owl:FunctionalProperty . ");

			int nbRules = 0;
			while (parser.hasNext()) {
				Object o = parser.next();
				if (o instanceof Rule) {
					++nbRules;

					Rule r = (Rule) o;

					CloseableIteratorWithoutException<Atom> it = r.getBody().iterator();
					Atom b1 = it.next();
					Atom b2 = it.next();
					Assert.assertFalse(it.hasNext());

					it = r.getHead().iterator();
					Atom h = it.next();
					Assert.assertFalse(it.hasNext());

					Assert.assertEquals(P, b1.getPredicate());
					Assert.assertEquals(P, b2.getPredicate());

					Assert.assertEquals(Predicate.EQUALITY, h.getPredicate());

					Assert.assertEquals(b1.getTerm(0), b2.getTerm(0));
					Assert.assertTrue(b1.getTerm(1).equals(h.getTerm(0)) || b1.getTerm(1).equals(h.getTerm(1)));
					Assert.assertTrue(b2.getTerm(1).equals(h.getTerm(0)) || b2.getTerm(1).equals(h.getTerm(1)));

				}
			}
			parser.close();
			Assert.assertEquals("Number of assertions found:", 1, nbRules);
		} catch (Exception e) {
			Assert.assertFalse(e.getMessage(), true);
		}
	}

	@Test
	public void inverseFunctionalObjectProperty() {
		// Y = Z :- p(Y, X), p(Z, X).
		try {
			OWL2Parser parser = new OWL2Parser(PREFIXES + ":p rdf:type owl:InverseFunctionalProperty . ");

			int nbRules = 0;
			while (parser.hasNext()) {
				Object o = parser.next();
				if (o instanceof Rule) {
					++nbRules;
					Rule r = (Rule) o;

					CloseableIteratorWithoutException<Atom> it = r.getBody().iterator();
					Atom b1 = it.next();
					Atom b2 = it.next();
					Assert.assertFalse(it.hasNext());

					it = r.getHead().iterator();
					Atom h = it.next();
					Assert.assertFalse(it.hasNext());

					Assert.assertEquals(P, b1.getPredicate());
					Assert.assertEquals(P, b2.getPredicate());

					Assert.assertEquals(Predicate.EQUALITY, h.getPredicate());

					Assert.assertEquals(b1.getTerm(1), b2.getTerm(1));
					Assert.assertTrue(b1.getTerm(0).equals(h.getTerm(0)) || b1.getTerm(0).equals(h.getTerm(1)));
					Assert.assertTrue(b2.getTerm(0).equals(h.getTerm(0)) || b2.getTerm(0).equals(h.getTerm(1)));

				}
			}
			parser.close();
			Assert.assertEquals("Number of assertions found:", 1, nbRules);
		} catch (Exception e) {
			Assert.assertFalse(e.getMessage(), true);
		}
	}

	@Test
	public void reflexiveObjectPropertyOf() {
		// p(X,X) :- Top(X).
		try {
			OWL2Parser parser = new OWL2Parser(PREFIXES + ":p rdf:type owl:ReflexiveProperty . ");

			int nbRules = 0;
			while (parser.hasNext()) {
				Object o = parser.next();
				if (o instanceof Rule) {
					++nbRules;
					Rule r = (Rule) o;

					CloseableIteratorWithoutException<Atom> it = r.getBody().iterator();
					Atom b = it.next();
					Assert.assertFalse(it.hasNext());

					it = r.getHead().iterator();
					Atom h = it.next();
					Assert.assertFalse(it.hasNext());

					Assert.assertEquals(Predicate.TOP, b.getPredicate());
					Assert.assertEquals(P, h.getPredicate());

					Assert.assertEquals(b.getTerm(0), h.getTerm(0));
					Assert.assertEquals(b.getTerm(0), h.getTerm(1));
				}
			}
			parser.close();
			Assert.assertEquals("Number of assertions found:", 1, nbRules);
		} catch (Exception e) {
			Assert.assertFalse(e.getMessage(), true);
		}
	}

	@Test
	public void irreflexiveObjectPropertyOf() {
		// ! :- p(X,X) .
		try {
			OWL2Parser parser = new OWL2Parser(PREFIXES + ":p rdf:type owl:IrreflexiveProperty . ");

			int nbRules = 0;
			while (parser.hasNext()) {
				Object o = parser.next();
				if (o instanceof DefaultNegativeConstraint) {
					++nbRules;
					Rule r = (Rule) o;

					CloseableIteratorWithoutException<Atom> it = r.getBody().iterator();
					Atom b = it.next();
					Assert.assertFalse(it.hasNext());

					Assert.assertEquals(P, b.getPredicate());
					Assert.assertEquals(b.getTerm(0), b.getTerm(1));
				}
			}
			parser.close();
			Assert.assertEquals("Number of assertions found:", 1, nbRules);
		} catch (Exception e) {
			Assert.assertFalse(e.getMessage(), true);
		}
	}

	@Test
	public void symetricObjectPropertyOf() {
		// p(X, Y) :- p(Y, X).
		try {
			OWL2Parser parser = new OWL2Parser(PREFIXES + ":p rdf:type owl:SymmetricProperty . ");

			int nbRules = 0;
			while (parser.hasNext()) {
				Object o = parser.next();
				if (o instanceof Rule) {
					++nbRules;
					Rule r = (Rule) o;

					CloseableIteratorWithoutException<Atom> it = r.getBody().iterator();
					Atom b = (Atom) it.next();
					Assert.assertFalse(it.hasNext());
					it = r.getHead().iterator();
					Atom h = (Atom) it.next();
					Assert.assertFalse(it.hasNext());

					Assert.assertEquals(P, b.getPredicate());
					Assert.assertEquals(P, h.getPredicate());

					Assert.assertNotEquals(b.getTerm(0), b.getTerm(1));
					Assert.assertEquals(b.getTerm(0), h.getTerm(1));
					Assert.assertEquals(b.getTerm(1), h.getTerm(0));
				}
			}
			parser.close();
			Assert.assertEquals("Number of assertions found:", 1, nbRules);
		} catch (Exception e) {
			Assert.assertFalse(e.getMessage(), true);
		}
	}

	@Test
	public void asymetricObjectPropertyOf() {
		// ! :- q(X, Y), q(X, Y).
		try {
			OWL2Parser parser = new OWL2Parser(PREFIXES + ":p rdf:type owl:AsymmetricProperty . ");

			int nbRules = 0;
			while (parser.hasNext()) {
				Object o = parser.next();
				if (o instanceof Rule) {
					++nbRules;
					Rule r = (Rule) o;

					CloseableIteratorWithoutException<Atom> it = r.getBody().iterator();
					Atom a1 = (Atom) it.next();
					Atom a2 = (Atom) it.next();
					Assert.assertFalse(it.hasNext());

					Assert.assertEquals(P, a1.getPredicate());
					Assert.assertEquals(P, a2.getPredicate());

					Assert.assertEquals(a1.getTerm(0), a2.getTerm(1));
					Assert.assertEquals(a1.getTerm(1), a2.getTerm(0));
				}
			}
			parser.close();
			Assert.assertEquals("Number of assertions found:", 1, nbRules);
		} catch (Exception e) {
			Assert.assertFalse(e.getMessage(), true);
		}
	}

	@Test
	public void transitiveObjectPropertyOf() {
		// p(X, Z) :- p(X, Y), p(Y, Z).
		try {
			OWL2Parser parser = new OWL2Parser(PREFIXES + ":p rdf:type owl:TransitiveProperty . ");

			int nbRules = 0;
			while (parser.hasNext()) {
				Object o = parser.next();
				if (o instanceof Rule) {
					++nbRules;
					Rule r = (Rule) o;

					CloseableIteratorWithoutException<Atom> it = r.getBody().iterator();
					Atom b1 = (Atom) it.next();
					Atom b2 = (Atom) it.next();
					Assert.assertFalse(it.hasNext());
					it = r.getHead().iterator();
					Atom h = (Atom) it.next();
					Assert.assertFalse(it.hasNext());

					Assert.assertEquals(P, b1.getPredicate());
					Assert.assertEquals(P, b2.getPredicate());
					Assert.assertEquals(P, h.getPredicate());

					Assert.assertTrue(b1.getTerm(0).equals(b2.getTerm(1)) || b1.getTerm(1).equals(b2.getTerm(0)));
					Assert.assertTrue(h.getTerm(0).equals(b1.getTerm(0)) || h.getTerm(0).equals(b2.getTerm(0)));
					Assert.assertTrue(h.getTerm(1).equals(b1.getTerm(1)) || h.getTerm(1).equals(b2.getTerm(1)));
				}
			}
			parser.close();
			Assert.assertEquals("Number of assertions found:", 1, nbRules);
		} catch (Exception e) {
			Assert.assertFalse(e.getMessage(), true);
		}
	}

	// /////////////////////////////////////////////////////////////////////////
	//
	// /////////////////////////////////////////////////////////////////////////

	@Test
	public void objectPropertyRange() {
		// C(Y) :- p(X, Y).
		try {
			OWL2Parser parser = new OWL2Parser(PREFIXES
			                                   + ":p rdf:type owl:ObjectProperty . "
			                                   + ":A rdf:type owl:Class . "
			                                   + ":p rdfs:range :A .");

			boolean found = false;
			while (parser.hasNext()) {
				Object o = parser.next();
				if (!(o instanceof Prefix)) {
					Rule r = (Rule) o;
					Atom property = (Atom) r.getBody().iterator().next();
					Assert.assertEquals(P, property.getPredicate());
					Atom classs = (Atom) r.getHead().iterator().next();
					Assert.assertEquals(A, classs.getPredicate());

					Assert.assertEquals(property.getTerm(1), classs.getTerm(0));
					found = true;
				}
			}
			parser.close();
			Assert.assertTrue("Number of assertions found:", found);
		} catch (Exception e) {
			Assert.assertFalse(e.getMessage(), true);
		}
	}

	@Test
	public void objectPropertyDomain() {
		// C(Y) :- p(X, Y).
		try {
			OWL2Parser parser = new OWL2Parser(PREFIXES
			                                   + ":p rdf:type owl:ObjectProperty . "
			                                   + ":A rdf:type owl:Class . "
			                                   + ":p rdfs:domain :A .");

			boolean found = false;
			while (parser.hasNext()) {
				Object o = parser.next();
				if (!(o instanceof Prefix)) {
					Rule r = (Rule) o;
					Atom property = (Atom) r.getBody().iterator().next();
					Assert.assertEquals(P, property.getPredicate());
					Atom classs = (Atom) r.getHead().iterator().next();
					Assert.assertEquals(A, classs.getPredicate());

					Assert.assertEquals(property.getTerm(0), classs.getTerm(0));
					found = true;
				}
			}
			parser.close();
			Assert.assertTrue("Number of assertions found:", found);
		} catch (Exception e) {
			Assert.assertFalse(e.getMessage(), true);
		}
	}

	// /////////////////////////////////////////////////////////////////////////
	// Data Property
	// /////////////////////////////////////////////////////////////////////////

	@Test
	public void subDataPropertyOfAxiom() throws OWL2ParserException {
		OWL2Parser parser = new OWL2Parser(PREFIXES
		                                   + ":p rdf:type owl:DatatypeProperty . "
		                                   + ":q rdf:type owl:DatatypeProperty . "
		                                   + ":p rdfs:subPropertyOf :q. ");

		int nbRules = 0;

		while (parser.hasNext()) {
			Object o = parser.next();
			if (o instanceof Rule) {
				++nbRules;

				Rule r = (Rule) o;
				CloseableIteratorWithoutException<Atom> bodyIt = r.getBody().iterator();
				CloseableIteratorWithoutException<Atom> headIt = r.getHead().iterator();
				Assert.assertTrue(bodyIt.hasNext());
				Assert.assertTrue(headIt.hasNext());

				Atom body = bodyIt.next();
				Atom head = headIt.next();
				Assert.assertTrue(!body.getTerm(0).isConstant());
				Assert.assertTrue(body.getTerm(0).equals(head.getTerm(0)));
				Assert.assertTrue(!body.getTerm(1).isConstant());
				Assert.assertTrue(body.getTerm(1).equals(head.getTerm(1)));
				Assert.assertEquals(P, body.getPredicate());
				Assert.assertEquals(Q, head.getPredicate());

				Assert.assertFalse(bodyIt.hasNext());
				Assert.assertFalse(headIt.hasNext());
			}
		}
		parser.close();
		Assert.assertEquals("Number of assertions found:", 1, nbRules);
	}

	@Test
	public void dataHasValue() {
	}

	@Test
	public void complexDataMinCardinality0InRightHandSide() throws OWL2ParserException {
		OWL2Parser parser = new OWL2Parser(PREFIXES
		                                   + ":D rdf:type rdfs:Datatype . "
		                                   + ":B rdf:type owl:Class . "
		                                   + ":p rdf:type owl:DatatypeProperty . "
		                                   + " :B rdfs:subClassOf [a owl:Restriction; owl:onProperty :p; owl:someValuesFrom [ rdf:type owl:Restriction ; "
		                                   + " owl:onProperty :p ; "
		                                   + " owl:onDataRange :D ; "
		                                   + " owl:minCardinality 0 ] ]. ");

		int nbAssertions = 0;

		while (parser.hasNext()) {
			Object o = parser.next();
			if (!(o instanceof Prefix)) {
				++nbAssertions;
			}
		}
		parser.close();
		Assert.assertEquals("Number of assertions found:", 1, nbAssertions);
	}

	@Test
	public void dataMinCardinality1() throws OWL2ParserException {
		OWL2Parser parser = new OWL2Parser(PREFIXES
		                                   + ":D rdf:type rdfs:Datatype . "
		                                   + ":B rdf:type owl:Class . "
		                                   + ":p rdf:type owl:DatatypeProperty . "
		                                   + " [ rdf:type owl:Restriction ; "
		                                   + " owl:onProperty :p ; "
		                                   + " owl:onDataRange :D ; "
		                                   + " owl:minCardinality 1 ] "
		                                   + "  "
		                                   + " rdfs:subClassOf :B . ");

		int nbRules = 0;

		while (parser.hasNext()) {
			Object o = parser.next();
			if (o instanceof Rule) {
				++nbRules;
				Rule r = (Rule) o;
				CloseableIteratorWithoutException<Atom> bodyIt = r.getBody().iterator();
				CloseableIteratorWithoutException<Atom> headIt = r.getHead().iterator();
				Assert.assertTrue(bodyIt.hasNext());
				Assert.assertTrue(headIt.hasNext());

				Atom body1 = bodyIt.next();
				Atom body2 = bodyIt.next();
				Atom head = headIt.next();
				Assert.assertFalse(bodyIt.hasNext());
				Assert.assertFalse(headIt.hasNext());

				Assert.assertTrue(D.equals(body1.getPredicate()) || D.equals(body2.getPredicate()));
				Assert.assertTrue(P.equals(body1.getPredicate()) || P.equals(body2.getPredicate()));
				Assert.assertTrue(D.equals(body1.getPredicate()) || P.equals(body1.getPredicate()));
				Assert.assertTrue(D.equals(body2.getPredicate()) || P.equals(body2.getPredicate()));

				Assert.assertEquals(B, head.getPredicate());

			}
		}
		parser.close();
		Assert.assertEquals("Number of assertions found:", 1, nbRules);
	}

	@Test
	public void dataMaxCardinality0() throws OWL2ParserException {
		OWL2Parser parser = new OWL2Parser(PREFIXES
		                                   + ":A rdf:type owl:Class . "
		                                   + ":B rdf:type owl:Datatype . "
		                                   + ":p rdf:type owl:DatatypeProperty . "
		                                   + " :A rdfs:subClassOf [ rdf:type owl:Restriction ; "
		                                   + " owl:onProperty :p ; "
		                                   + " owl:onDataRange :B ; "
		                                   + " owl:maxCardinality 0 ] . ");

		int nbRules = 0;

		while (parser.hasNext()) {
			Object o = parser.next();
			if (o instanceof DefaultNegativeConstraint) {
				++nbRules;
			}
		}
		parser.close();
		Assert.assertEquals("Number of assertions found:", 1, nbRules);
	}

	@Test
	public void dataMaxCardinality1() throws OWL2ParserException {
		OWL2Parser parser = new OWL2Parser(PREFIXES
		                                   + ":A rdf:type owl:Class . "
		                                   + ":B rdf:type owl:Datatype . "
		                                   + ":p rdf:type owl:DatatypeProperty . "
		                                   + " :A rdfs:subClassOf [ rdf:type owl:Restriction ; "
		                                   + " owl:onProperty :p ; "
		                                   + " owl:onDataRange :B ; "
		                                   + " owl:maxCardinality 1 ] . ");

		int nbRules = 0;

		while (parser.hasNext()) {
			Object o = parser.next();
			if (o instanceof Rule) {
				++nbRules;
			}
		}
		parser.close();
		Assert.assertEquals("Number of assertions found:", 1, nbRules);
	}

	@Test
	public void dataExactCardinality0() throws OWL2ParserException {
		OWL2Parser parser = new OWL2Parser(PREFIXES
		                                   + ":A rdf:type owl:Class . "
		                                   + ":B rdf:type owl:Datatype . "
		                                   + ":p rdf:type owl:DatatypeProperty . "
		                                   + " :A rdfs:subClassOf [ rdf:type owl:Restriction ; "
		                                   + " owl:onProperty :p ; "
		                                   + " owl:onDataRange :B ; "
		                                   + " owl:qualifiedCardinality 0 ] . ");

		int nbRules = 0;

		while (parser.hasNext()) {
			Object o = parser.next();
			if (o instanceof Rule) {
				++nbRules;
			}
		}
		parser.close();
		Assert.assertEquals("Number of assertions found:", 1, nbRules);
	}

	@Test
	public void dataExactCardinality1() throws OWL2ParserException {
		OWL2Parser parser = new OWL2Parser(PREFIXES
		                                   + ":A rdf:type owl:Class . "
		                                   + ":B rdf:type owl:Datatype . "
		                                   + ":p rdf:type owl:DatatypeProperty . "
		                                   + " :A rdfs:subClassOf [ rdf:type owl:Restriction ; "
		                                   + " owl:onProperty :p ; "
		                                   + " owl:onDataRange :B ; "
		                                   + " owl:qualifiedCardinality 1 ] . ");

		int nbRules = 0;

		while (parser.hasNext()) {
			Object o = parser.next();
			if (o instanceof Rule) {
				++nbRules;
			}
		}
		parser.close();
		Assert.assertEquals("Number of assertions found:", 2, nbRules);
	}

	@Test
	public void dataPropertyDomainAxiom() throws OWL2ParserException {
		// C(X) :- p(X, Y).
		OWL2Parser parser = new OWL2Parser(PREFIXES
		                                   + ":p rdf:type owl:DatatypeProperty . "
		                                   + ":C rdf:type owl:Class ."
		                                   + ":p rdfs:domain :C .");

		boolean found = false;
		while (parser.hasNext()) {
			Object o = parser.next();
			if (!(o instanceof Prefix)) {
				Assert.assertTrue(o instanceof Rule);
				Rule r = (Rule) o;

				Atom property = (Atom) r.getBody().iterator().next();
				Assert.assertEquals(P, property.getPredicate());
				Atom classs = (Atom) r.getHead().iterator().next();
				Assert.assertEquals(C, classs.getPredicate());

				Assert.assertEquals(property.getTerm(0), classs.getTerm(0));
				found = true;
			}
		}
		parser.close();
		Assert.assertTrue("Number of assertions found:", found);
	}

	@Test
	public void dataPropertyRangeAxiom() throws OWL2ParserException {
		// D(Y) :- p(X, Y).
		OWL2Parser parser = new OWL2Parser(PREFIXES
		                                   + ":p rdf:type owl:DatatypeProperty . "
		                                   + ":D rdf:type rdfs:Datatype ."
		                                   + ":p rdfs:range :D .");

		boolean found = false;
		while (parser.hasNext()) {
			Object o = parser.next();
			if (!(o instanceof Prefix)) {
				Assert.assertTrue(o instanceof Rule);
				Rule r = (Rule) o;

				Atom property = (Atom) r.getBody().iterator().next();
				Assert.assertEquals(P, property.getPredicate());
				Atom classs = (Atom) r.getHead().iterator().next();
				Assert.assertEquals(D, classs.getPredicate());

				Assert.assertEquals(property.getTerm(1), classs.getTerm(0));
				found = true;
			}
		}
		parser.close();
		Assert.assertTrue("Number of assertions found:", found);
	}

	@Test
	public void dataRangeIntersection() throws OWL2ParserException {
		// D(Y) :- p(X, Y).
		OWL2Parser parser = new OWL2Parser(PREFIXES
		                                   + ":p rdf:type owl:DatatypeProperty . "
		                                   + ":D rdf:type rdfs:Datatype ."
		                                   + ":E rdf:type rdfs:Datatype."
		                                   + ":p rdfs:range [owl:intersectionOf ( :D :E ) ] .");

		int nbAssertions = 0;
		while (parser.hasNext()) {
			Object o = parser.next();
			if (!(o instanceof Prefix)) {
				Assert.assertTrue(o instanceof Rule);
				++nbAssertions;
			}
		}
		parser.close();
		Assert.assertEquals("Number of assertions found:", 2, nbAssertions);
	}

	@Test
	public void dataRangeComplementOf() throws OWL2ParserException {
		// D(Y) :- p(X, Y).
		OWL2Parser parser = new OWL2Parser(PREFIXES
		                                   + ":p rdf:type owl:DatatypeProperty . "
		                                   + ":D rdf:type rdfs:Datatype ."
		                                   + ":E rdf:type rdfs:Datatype."
		                                   + ":p rdfs:range [owl:complementOf [owl:intersectionOf ( :D :E ) ]] .");

		int nbAssertions = 0;
		while (parser.hasNext()) {
			Object o = parser.next();
			if (!(o instanceof Prefix)) {
				Assert.assertTrue(o instanceof Rule);
				++nbAssertions;
			}
		}
		parser.close();
		Assert.assertEquals("Number of assertions found:", 1, nbAssertions);
	}

	@Test
	public void dataRangeOneOf() throws OWL2ParserException {
		// D(Y) :- p(X, Y).
		OWL2Parser parser = new OWL2Parser(PREFIXES
		                                   + ":p rdf:type owl:DatatypeProperty . "
		                                   + ":p rdfs:range [a rdfs:Datatype; owl:complementOf [a rdfs:Datatype; owl:oneOf ( :i1 :i2 :i3 ) ]] .");

		int nbAssertions = 0;
		while (parser.hasNext()) {
			Object o = parser.next();
			if (!(o instanceof Prefix)) {
				Assert.assertTrue(o instanceof Rule);
				++nbAssertions;
			}
		}
		parser.close();
		Assert.assertEquals("Number of assertions found:", 3, nbAssertions);
	}

	@Test
	public void dataRangeUnionOf() throws OWL2ParserException {
		// D(Y) :- p(X, Y).
		OWL2Parser parser = new OWL2Parser(PREFIXES
		                                   + ":p rdf:type owl:DatatypeProperty . "
		                                   + ":D1 a rdfs:Datatype."
		                                   + ":D2 a rdfs:Datatype."
		                                   + ":p rdfs:range [a rdfs:Datatype; owl:complementOf [a rdfs:Datatype; owl:unionOf ( :D1 :D2 ) ]] .");

		int nbAssertions = 0;
		while (parser.hasNext()) {
			Object o = parser.next();
			if (!(o instanceof Prefix)) {
				Assert.assertTrue(o instanceof Rule);
				++nbAssertions;
			}
		}
		parser.close();
		Assert.assertEquals("Number of assertions found:", 2, nbAssertions);
	}

	@Test
	public void dataSomeValuesFrom() throws OWL2ParserException {
		// p(X,Y) D(Y) :- A(X).
		OWL2Parser parser = new OWL2Parser(PREFIXES
		                                   + ":A a owl:Class. "
		                                   + ":D a rdfs:Datatype. "
		                                   + ":p rdf:type owl:DatatypeProperty . "
		                                   + ":A rdfs:subClassOf [a owl:Restriction; owl:onProperty :p; owl:someValuesFrom :D].");

		int nbAssertions = 0;
		while (parser.hasNext()) {
			Object o = parser.next();
			if (!(o instanceof Prefix)) {
				Assert.assertTrue(o instanceof Rule);
				++nbAssertions;
			}
		}
		parser.close();
		Assert.assertEquals("Number of assertions found:", 1, nbAssertions);
	}

	@Test
	public void subDataRange() throws OWL2ParserException {
		// p(X,Y) D(Y) :- A(X).
		OWL2Parser parser = new OWL2Parser(PREFIXES
		                                   + ":A a owl:Class. "
		                                   + ":D1 a rdfs:Datatype. "
		                                   + ":D2 a rdfs:Datatype. "
		                                   + ":D3 a rdfs:Datatype. "
		                                   + ":p rdf:type owl:DatatypeProperty . "
		                                   + "[a owl:Restriction; owl:onProperty :p; owl:someValuesFrom [a rdfs:Datatype; owl:unionOf (:D1 :D2 :D3)]] rdfs:subClassOf :A.");

		int nbAssertions = 0;
		while (parser.hasNext()) {
			Object o = parser.next();
			if (!(o instanceof Prefix)) {
				Assert.assertTrue(o instanceof Rule);
				++nbAssertions;
			}
		}
		parser.close();
		Assert.assertEquals("Number of assertions found:", 3, nbAssertions);
	}

	@Test
	public void dataAllValuesFrom() throws OWL2ParserException {
		// D(Y) :- p(X,Y) A(X).
		OWL2Parser parser = new OWL2Parser(PREFIXES
		                                   + ":A a owl:Class. "
		                                   + ":D a rdfs:Datatype. "
		                                   + ":p rdf:type owl:DatatypeProperty . "
		                                   + ":A rdfs:subClassOf [a owl:Restriction; owl:onProperty :p; owl:allValuesFrom :D].");

		int nbAssertions = 0;
		while (parser.hasNext()) {
			Object o = parser.next();
			if (!(o instanceof Prefix)) {
				Assert.assertTrue(o instanceof Rule);
				++nbAssertions;
			}
		}
		parser.close();
		Assert.assertEquals("Number of assertions found:", 1, nbAssertions);
	}

	// /////////////////////////////////////////////////////////////////////////
	// OTHERS
	// /////////////////////////////////////////////////////////////////////////

	@Test
	public void objectInverseOf() throws OWL2ParserException {
		OWL2Parser parser = new OWL2Parser(PREFIXES
		                                   + ":p rdf:type owl:ObjectProperty . "
		                                   + ":q rdf:type owl:ObjectProperty . "
		                                   + ":p rdfs:subPropertyOf [owl:inverseOf :q] .");

		int nbRules = 0;
		while (parser.hasNext()) {
			Object o = parser.next();
			if (o instanceof Rule) {
				++nbRules;
				Rule r = (Rule) o;
				Atom subProperty = (Atom) r.getBody().iterator().next();
				Atom property = (Atom) r.getHead().iterator().next();

				Assert.assertEquals(P, subProperty.getPredicate());
				Assert.assertEquals(Q, property.getPredicate());

				Assert.assertEquals(subProperty.getTerm(0), property.getTerm(1));
				Assert.assertEquals(subProperty.getTerm(1), property.getTerm(0));
			}
		}
		parser.close();
		Assert.assertEquals("Number of assertions found:", 1, nbRules);

	}

	@Test
	public void objectPropertyChain() throws OWL2ParserException {
		OWL2Parser parser = new OWL2Parser(PREFIXES
		                                   + ":p rdf:type owl:ObjectProperty . "
		                                   + ":q rdf:type owl:ObjectProperty . "
		                                   + ":r rdf:type owl:ObjectProperty . "
		                                   + ":s rdf:type owl:ObjectProperty . "
		                                   + "_:x rdfs:subPropertyOf :s ; "
		                                   + " owl:propertyChain  ( :p :q :r ) . ");

		int nbRules = 0;
		while (parser.hasNext()) {
			Object o = parser.next();
			if (o instanceof Rule) {
				++nbRules;
				Rule r = (Rule) o;
				CloseableIteratorWithoutException<Atom> it = r.getBody().iterator();
				Atom body1 = it.next();
				Atom body2 = it.next();
				Atom body3 = it.next();
				Assert.assertFalse(it.hasNext());
				Atom property = (Atom) r.getHead().iterator().next();

				Assert.assertTrue(
				    P.equals(body1.getPredicate()) || Q.equals(body1.getPredicate()) || R.equals(body1.getPredicate()));
				Assert.assertTrue(
				    P.equals(body2.getPredicate()) || Q.equals(body2.getPredicate()) || R.equals(body2.getPredicate()));
				Assert.assertTrue(
				    P.equals(body3.getPredicate()) || Q.equals(body3.getPredicate()) || R.equals(body3.getPredicate()));
				Assert.assertEquals(S, property.getPredicate());

				Term x = null, y = null;
				if (P.equals(body1.getPredicate())) {
					x = body1.getTerm(0);
				} else if (P.equals(body2.getPredicate())) {
					x = body2.getTerm(0);
				} else if (P.equals(body3.getPredicate())) {
					x = body3.getTerm(0);
				}

				if (R.equals(body1.getPredicate())) {
					y = body1.getTerm(1);
				} else if (R.equals(body2.getPredicate())) {
					y = body2.getTerm(1);
				} else if (R.equals(body3.getPredicate())) {
					y = body3.getTerm(1);
				}

				Assert.assertEquals(x, property.getTerm(0));
				Assert.assertEquals(y, property.getTerm(1));

			}
		}
		parser.close();
		Assert.assertEquals("Number of assertions found:", 1, nbRules);

	}

	// /////////////////////////////////////////////////////////////////////////
	// ASSERTIONS
	// /////////////////////////////////////////////////////////////////////////

	@Test
	public void classAssertion() throws OWL2ParserException {
		OWL2Parser parser = new OWL2Parser(PREFIXES + ":A rdf:type owl:Class . " + ":i1 a :A ." + "");

		int nbFacts = 0;
		while (parser.hasNext()) {
			Object o = parser.next();
			if ((o instanceof InMemoryAtomSet)) {
				++nbFacts;
				CloseableIteratorWithoutException<Atom> it = ((InMemoryAtomSet) o).iterator();
				Assert.assertTrue(it.hasNext());
				Atom a = it.next();

				Assert.assertEquals(A, a.getPredicate());
				Assert.assertEquals(I1, a.getTerm(0));

			}
		}
		parser.close();
		Assert.assertEquals("Number of assertions found:", 1, nbFacts);
	}

	@Test
	public void complexClassAssertion() throws OWL2ParserException {
		OWL2Parser parser = new OWL2Parser(PREFIXES
		                                   + ":A rdf:type owl:Class . "
		                                   + ":B rdf:type owl:Class . "
		                                   + ":i1 a [owl:intersectionOf ( :A [owl:complementOf :B] ) ] ."
		                                   + "");

		int nbFacts = 0;
		int nbConstraint = 0;
		while (parser.hasNext()) {
			Object o = parser.next();
			if ((o instanceof InMemoryAtomSet)) {

				++nbFacts;
				CloseableIteratorWithoutException<Atom> it = ((InMemoryAtomSet) o).iterator();
				Assert.assertTrue(it.hasNext());
				Atom a = it.next();

				Assert.assertEquals(A, a.getPredicate());
				Assert.assertEquals(I1, a.getTerm(0));

			}
			if ((o instanceof DefaultNegativeConstraint)) {
				++nbConstraint;
				CloseableIteratorWithoutException<Atom> it = ((DefaultNegativeConstraint) o).getBody().iterator();
				Assert.assertTrue(it.hasNext());
				Atom a = it.next();
				Assert.assertEquals(B, a.getPredicate());
				Assert.assertEquals(I1, a.getTerm(0));

			}
		}
		parser.close();
		Assert.assertEquals("Number of assertions found:", 1, nbFacts);
		Assert.assertEquals("Number of assertions found:", 1, nbConstraint);
	}

	@Test
	public void assertionObjectProperty() throws OWL2ParserException {
		OWL2Parser parser = new OWL2Parser(PREFIXES + ":p rdf:type owl:ObjectProperty . " + ":i1 :p :i2 ." + "");

		boolean found = false;
		while (parser.hasNext()) {
			Object o = parser.next();
			if (!(o instanceof Prefix)) {
				InMemoryAtomSet atomset = (InMemoryAtomSet) o;
				Atom a = atomset.iterator().next();
				Assert.assertEquals(P, a.getPredicate());
				Iterator<Term> it = a.iterator();
				Assert.assertEquals(I1, it.next());
				Assert.assertEquals(I2, it.next());
				found = true;
			}
		}
		parser.close();
		Assert.assertTrue("Number of assertions found:", found);

	}

	@Test
	public void assertionWithExistential() throws OWL2ParserException {
		try {
			OWL2Parser parser = new OWL2Parser(PREFIXES
			                                   + ":A rdf:type owl:Class . "
			                                   + ":p rdf:type owl:ObjectProperty . "
			                                   + ":i1 :p [a :A] ."
			                                   + "");

			int nbFacts = 0;
			int nbAtoms = 0;
			while (parser.hasNext()) {
				Object o = parser.next();
				if (o instanceof InMemoryAtomSet) {
					++nbFacts;
					CloseableIterator<Atom> it = ((AtomSet) o).iterator();
					while (it.hasNext()) {
						it.next();
						++nbAtoms;
					}
				}
			}
			parser.close();
			Assert.assertEquals("Number of facts found:", 1, nbFacts);
			Assert.assertEquals("Number of atoms found:", 2, nbAtoms);
		} catch (Throwable e) {
			Assert.fail("An exception was found: " + e);
		}

	}

	@Test
	public void complexAssertionWithExistential() throws OWL2ParserException {
		try {
			OWL2Parser parser = new OWL2Parser(PREFIXES
			                                   + ":A a owl:Class . "
			                                   + ":B a owl:Class . "
			                                   + ":p a owl:ObjectProperty . "
			                                   + "_:x1 a :A. "
			                                   + "_:y1 a :B. "
			                                   + "_:x1 :p _:y1. "
			                                   + "_:x2 a :A. "
			                                   + "_:y2 a :B. "
			                                   + "_:x2 :p _:y2. ");

			int nbFacts = 0;
			int nbAtoms = 0;
			while (parser.hasNext()) {
				Object o = parser.next();
				if (o instanceof AtomSet) {
					Term a0 = null, b0 = null, p0 = null, p1 = null;
					++nbFacts;
					CloseableIterator<Atom> it = ((AtomSet) o).iterator();
					while (it.hasNext()) {
						Atom a = it.next();
						++nbAtoms;
						if(a.getPredicate().equals(P)) {
							p0 = a.getTerm(0);
							p1 = a.getTerm(1);
						} else if (a.getPredicate().equals(A)) {
							a0 = a.getTerm(0);
						} else if (a.getPredicate().equals(B)) {
							b0 = a.getTerm(0);
						}
					}
					Assert.assertEquals(p0, a0);
					Assert.assertEquals(p1, b0);
				}
			}
			parser.close();
			Assert.assertEquals("Number of facts found:", 2, nbFacts);
			Assert.assertEquals("Number of atoms found:", 6, nbAtoms);
		} catch (Throwable e) {
			Assert.fail("An exception was found: " + e);
		}
	}

	@Test
	public void assertionDataProperty() throws OWL2ParserException, IteratorException {
		OWL2Parser parser = new OWL2Parser(PREFIXES + ":p rdf:type owl:DatatypeProperty . " + ":i1 :p 7 ." + "");

		boolean found = false;
		while (parser.hasNext()) {
			Object o = parser.next();
			if (!(o instanceof Prefix)) {

				AtomSet atomset = (AtomSet) o;
				Atom a = atomset.iterator().next();
				Assert.assertEquals(P, a.getPredicate());
				Iterator<Term> it = a.iterator();
				Assert.assertEquals(I1, it.next());
				Assert.assertEquals(L1, it.next());
				found = true;
			}
		}
		parser.close();
		Assert.assertTrue("Number of assertions found:", found);

	}

	// @Test
	// public void assertionDataProperty() {
	// try {
	// OWLParser parser = new OWLParser(
	// "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> . "
	// + "@prefix owl: <http://www.w3.org/2002/07/owl#> . "
	// + "@prefix test: <http://test.org/> . "
	// + "test:property rdf:type owl:DatatypeProperty . "
	// + "test:a test:property \"test\" ." + "");
	//
	// boolean found = false;
	// while (parser.hasNext()) {
	// Object o = parser.next();
	// if (!(o instanceof Prefix)) {
	// Atom a = (Atom) o;
	// Assert.assertEquals(new Predicate("test:property", 2),
	// a.getPredicate());
	// CloseableIteratorWithoutException<Term> it = a.iterator();
	// Assert.assertEquals(DefaultTermFactory.instance()
	// .createConstant("test:a"), it.next());
	// Assert.assertEquals(DefaultTermFactory.instance()
	// .createLiteral("test"), it.next());
	// found = true;
	// }
	// }
	//
	// Assert.assertTrue("Number of assertions found:", found);
	// } catch (Exception e) {
	// Assert.assertFalse(e.getMessage(), true);
	// }
	// }

	// /////////////////////////////////////////////////////////////////////////
	// MORE COMPLEX TEST
	// /////////////////////////////////////////////////////////////////////////

	@Test
	public void illegalAllValuesFromMaskedSomeValuesFrom() throws OWL2ParserException {
		OWL2Parser parser = new OWL2Parser(PREFIXES
		                                   + " :A a owl:Class. "
		                                   + ":B a owl:Class. "
		                                   + ":p a owl:ObjectProperty. "
		                                   + "[rdf:type owl:Restriction; "
		                                   + "    owl:onProperty :p; "
		                                   + "    owl:someValuesFrom [rdf:type owl:Restriction; "
		                                   + "        owl:onProperty :p ; "
		                                   + "        owl:allValuesFrom :A] "
		                                   + "] rdfs:subClassOf :B .");

		int nbAssertion = 0;
		while (parser.hasNext()) {
			Object o = parser.next();
			if (!(o instanceof Prefix)) {
				++nbAssertion;
			}
		}
		parser.close();
		Assert.assertEquals("Number of assertions found:", 0, nbAssertion);
	}

	@Test
	public void partiallyIllegalTest() throws OWL2ParserException {
		OWL2Parser parser = new OWL2Parser(PREFIXES
		                                   + " :A a owl:Class. "
		                                   + ":B a owl:Class. "
		                                   + ":C a owl:Class. "
		                                   + ":p a owl:ObjectProperty. "
		                                   + "[rdf:type owl:Restriction; "
		                                   + "    owl:onProperty :p; "
		                                   + "    owl:someValuesFrom [owl:unionOf ( [rdf:type owl:Restriction; "
		                                   + "        owl:onProperty :p ; "
		                                   + "        owl:allValuesFrom :A] :C ) ]"
		                                   + "] rdfs:subClassOf :B .");

		int nbAssertion = 0;
		while (parser.hasNext()) {
			Object o = parser.next();
			if (!(o instanceof Prefix)) {
				++nbAssertion;

			}
		}
		parser.close();
		Assert.assertEquals("Number of assertions found:", 1, nbAssertion);
	}

	@Test
	public void illegalAllValuesFromMaskedInIntersection() throws OWL2ParserException {
		OWL2Parser parser = new OWL2Parser(PREFIXES
		                                   + " :A a owl:Class. "
		                                   + ":B a owl:Class. :C a owl:Class. "
		                                   + ":p a owl:ObjectProperty. "
		                                   + "[owl:intersectionOf ( :A [rdf:type owl:Restriction; "
		                                   + "        owl:onProperty :p ; "
		                                   + "        owl:allValuesFrom :B] ) ] rdfs:subClassOf :C .");

		int nbAssertion = 0;
		while (parser.hasNext()) {
			Object o = parser.next();
			if (!(o instanceof Prefix)) {
				++nbAssertion;

			}
		}
		parser.close();
		Assert.assertEquals("Number of assertions found:", 0, nbAssertion);
	}

	@Test
	public void illegalAllValuesFromMaskedInUnion() throws OWL2ParserException {
		OWL2Parser parser = new OWL2Parser(PREFIXES
		                                   + " :A a owl:Class. "
		                                   + ":B a owl:Class. :C a owl:Class. "
		                                   + ":p a owl:ObjectProperty. "
		                                   + "[owl:unionOf ( :A [rdf:type owl:Restriction; "
		                                   + "        owl:onProperty :p ; "
		                                   + "        owl:allValuesFrom :B] ) ] rdfs:subClassOf :C .");

		int nbAssertion = 0;
		while (parser.hasNext()) {
			Object o = parser.next();
			if (!(o instanceof Prefix)) {
				++nbAssertion;
			}
		}
		parser.close();
		Assert.assertEquals("Number of assertions found:", 1, nbAssertion);
	}

	@Test
	public void intersectionOfIntersectionOfIntersection() throws OWL2ParserException {
		OWL2Parser parser = new OWL2Parser(PREFIXES
		                                   + " :A a owl:Class. "
		                                   + ":B a owl:Class. "
		                                   + ":C a owl:Class. "
		                                   + ":D a owl:Class. "
		                                   + ":E a owl:Class. "

		                                   + "[owl:intersectionOf ( :B [owl:intersectionOf (:C "
		                                   + "[owl:intersectionOf( :D :E ) ] )] )]"
		                                   + " rdfs:subClassOf :A .");

		int nbAssertion = 0;
		while (parser.hasNext()) {
			Object o = parser.next();
			if (!(o instanceof Prefix)) {
				++nbAssertion;
			}
		}
		parser.close();
		Assert.assertEquals("Number of assertions found:", 1, nbAssertion);
	}
	//
	// NOT IN OWL ER
	// @Test
	// public void test1() {
	// // !a(x) v b(x) v !c(x).
	// try {
	// OWLParser parser = new OWLParser(
	// "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> . "
	// + "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> ."
	// + "@prefix owl: <http://www.w3.org/2002/07/owl#> . "
	// + "@prefix test: <http://test.org/> . "
	// + ":ClassA rdf:type owl:Class . "
	// + ":ClassB rdf:type owl:Class . "
	// + ":ClassC rdf:type owl:Class . "
	// + ":ClassA rdfs:subClassOf [ "
	// + " owl:unionOf ( :ClassB [ "
	// + " owl:complementOf :ClassC ] ) ] ." + "");
	//
	// boolean found = false;
	//
	// while (parser.hasNext()) {
	// Object o = parser.next();
	// if (!(o instanceof Prefix)) {
	//
	// Rule r = (Rule) o;
	// CloseableIteratorWithoutException<Atom> it = r.getBody().iterator();
	// Atom body1 = it.next();
	// Atom body2 = it.next();
	// Assert.assertTrue(CLASS_A.equals(body1.getPredicate())
	// || CLASS_A.equals(body2.getPredicate()));
	// Assert.assertTrue(CLASS_C.equals(body1.getPredicate())
	// || CLASS_C.equals(body2.getPredicate()));
	//
	// Atom classs = (Atom) r.getHead().iterator().next();
	// Assert.assertEquals(CLASS_B, classs.getPredicate());
	//
	// Assert.assertEquals(body1.getTerm(0), classs.getTerm(0));
	// Assert.assertEquals(body2.getTerm(0), classs.getTerm(0));
	//
	// found = true;
	// }
	// }
	//
	// Assert.assertTrue("Number of assertions found:", found);
	// } catch (Exception e) {
	// Assert.assertFalse(e.getMessage(), true);
	// }
	// }

}
