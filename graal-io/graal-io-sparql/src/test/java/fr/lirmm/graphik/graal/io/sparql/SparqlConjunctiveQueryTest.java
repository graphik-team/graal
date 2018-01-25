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
package fr.lirmm.graphik.graal.io.sparql;

import org.junit.Assert;
import org.junit.Test;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.Constant;
import fr.lirmm.graphik.graal.api.core.Literal;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.io.ParseException;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.util.URIUtils;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;
import fr.lirmm.graphik.util.stream.Iterators;

/**
 * 
 */

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class SparqlConjunctiveQueryTest {

	private static final String PREFIX = "http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#";
	private static final Predicate A = new Predicate(URIUtils.createURI(PREFIX + "A"), 1);
	private static final Predicate P = new Predicate(URIUtils.createURI(PREFIX + "p"), 2);
	private static final Predicate Q = new Predicate(URIUtils.createURI(PREFIX + "q"), 2);
	private static final Constant TOTO = DefaultTermFactory.instance()
	                                                       .createConstant(URIUtils.createURI(PREFIX + "toto"));
	private static final Constant TITI = DefaultTermFactory.instance()
	                                                       .createConstant(URIUtils.createURI(PREFIX + "titi"));
	private static final Literal STRING = DefaultTermFactory.instance().createLiteral(URIUtils.XSD_STRING, "toto");
	private static final Literal INTEGER = DefaultTermFactory.instance().createLiteral(URIUtils.XSD_INTEGER, 7);

	@Test
	public void test1() throws ParseException {
		String query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
		               + "PREFIX : <"
		               + PREFIX
		               + ">"
		               + "SELECT DISTINCT ?0 "
		               + "WHERE"
		               + "{"
		               + "	?0 :p ?1  ."
		               + "	?1 :q ?2 "
		               + "}";
		ConjunctiveQuery cq = new SparqlConjunctiveQueryParser(query).getConjunctiveQuery();
		Assert.assertEquals(1, cq.getAnswerVariables().size());
		int nbTriple = 0;
		CloseableIteratorWithoutException<Atom> it = cq.getAtomSet().iterator();
		while (it.hasNext()) {
			Atom a = it.next();
			++nbTriple;
			Assert.assertTrue("Unrecognized triple", P.equals(a.getPredicate()) || Q.equals(a.getPredicate()));
		}
		Assert.assertEquals(2, nbTriple);
	}

	@Test
	public void test2() throws ParseException {
		String query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
		               + "PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>"
		               + "SELECT DISTINCT ?0 ?1 ?2 "
		               + "WHERE"
		               + "{"
		               + "	?0  :worksFor ?1  ."
		               + "	?1  :affiliatedOrganizationOf ?2 "
		               + "}";
		ConjunctiveQuery cq = new SparqlConjunctiveQueryParser(query).getConjunctiveQuery();
		Assert.assertEquals(3, cq.getAnswerVariables().size());
		Assert.assertEquals(2, Iterators.count(cq.getAtomSet().iterator()));
	}

	@Test
	public void test3() throws ParseException {
		String query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
		               + "PREFIX : <"
		               + PREFIX
		               + ">"
		               + "SELECT DISTINCT ?x ?y "
		               + "WHERE"
		               + "{"
		               + "	?x :p :toto  ."
		               + "	:titi :q ?y "
		               + "}";

		ConjunctiveQuery cq = new SparqlConjunctiveQueryParser(query).getConjunctiveQuery();
		Assert.assertEquals(2, cq.getAnswerVariables().size());
		int nbTriple = 0;
		CloseableIteratorWithoutException<Atom> it = cq.getAtomSet().iterator();
		while (it.hasNext()) {
			Atom a = it.next();
			++nbTriple;
			if (P.equals(a.getPredicate())) {
				Assert.assertEquals(a.getTerm(1), TOTO);
			} else if (Q.equals(a.getPredicate())) {
				Assert.assertEquals(a.getTerm(0), TITI);
			} else {
				Assert.assertFalse("Unrecognized triple", true);
			}
		}
		Assert.assertEquals(2, nbTriple);
	}

	@Test
	public void testRDFType() throws ParseException {
		String query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
		               + "PREFIX : <"
		               + PREFIX
		               + ">"
		               + "SELECT DISTINCT ?x "
		               + "WHERE"
		               + "{"
		               + "	?x a :A  ."
		               + "}";
		ConjunctiveQuery cq = new SparqlConjunctiveQueryParser(query).getConjunctiveQuery();
		CloseableIteratorWithoutException<Atom> it = cq.getAtomSet().iterator();
		while (it.hasNext()) {
			Atom a = it.next();
			Assert.assertEquals(A, a.getPredicate());
		}
	}

	@Test
	public void testStar() throws ParseException {
		String query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
		               + "PREFIX : <"
		               + PREFIX
		               + ">"
		               + "SELECT DISTINCT * "
		               + "WHERE"
		               + "{"
		               + "	?0 :p ?1  ."
		               + "	?1 :q ?2 "
		               + "}";
		ConjunctiveQuery cq = new SparqlConjunctiveQueryParser(query).getConjunctiveQuery();
		Assert.assertEquals(3, cq.getAnswerVariables().size());
	}

	@Test
	public void testIntegerLiteral() throws ParseException {
		String query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
		               + "PREFIX : <"
		               + PREFIX
		               + ">"
		               + "SELECT DISTINCT ?x "
		               + "WHERE"
		               + "{"
		               + "	?x :p 7 ."
		               + "}";
		ConjunctiveQuery cq = new SparqlConjunctiveQueryParser(query).getConjunctiveQuery();
		CloseableIteratorWithoutException<Atom> it = cq.getAtomSet().iterator();
		while (it.hasNext()) {
			Atom a = it.next();
			Assert.assertEquals(P, a.getPredicate());
			Assert.assertEquals(INTEGER, a.getTerm(1));
		}
	}

	@Test
	public void testStringLiteral() throws ParseException {
		String query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
		               + "PREFIX : <"
		               + PREFIX
		               + ">"
		               + "SELECT DISTINCT ?x "
		               + "WHERE"
		               + "{"
		               + "	?x :p 'toto' ."
		               + "}";
		ConjunctiveQuery cq = new SparqlConjunctiveQueryParser(query).getConjunctiveQuery();
		CloseableIteratorWithoutException<Atom> it = cq.getAtomSet().iterator();
		while (it.hasNext()) {
			Atom a = it.next();
			Assert.assertEquals(P, a.getPredicate());
			Assert.assertEquals(STRING, a.getTerm(1));
		}
	}
}
