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

import java.io.IOException;
import java.io.StringWriter;

import org.junit.Assert;
import org.junit.Test;

import com.hp.hpl.jena.query.QueryParseException;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.Literal;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.util.Prefix;
import fr.lirmm.graphik.util.URIUtils;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class SparqlRuleTest {

	private static final String    PREFIX  = "http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#";
	private static final Predicate A       = new Predicate(URIUtils.createURI(PREFIX + "A"), 1);
	private static final Predicate B       = new Predicate(URIUtils.createURI(PREFIX + "B"), 1);
	private static final Predicate P       = new Predicate(URIUtils.createURI(PREFIX + "p"), 2);
	private static final Predicate Q       = new Predicate(URIUtils.createURI(PREFIX + "q"), 2);

	private static final Literal   STRING  = DefaultTermFactory.instance().createLiteral(URIUtils.XSD_STRING, "toto");
	private static final Literal   INTEGER = DefaultTermFactory.instance().createLiteral(URIUtils.XSD_INTEGER, 7);

	@Test
	public void test0() throws IOException {
		String queryString = "PREFIX foaf:    <http://xmlns.com/foaf/0.1/> "
		                     + " PREFIX vcard:   <http://www.w3.org/2001/vcard-rdf/3.0#>                  "
		                     + "                                                                          "
		                     + " CONSTRUCT { ?x  vcard:N _:v .                                            "
		                     + "             _:v vcard:givenName ?gname .                                 "
		                     + "             _:v vcard:familyName ?fname }                                "
		                     + " WHERE                                                                    "
		                     + "  {                                                                       "
		                     + "     { ?x foaf:firstname ?gname }  . "
		                     + "     { ?x foaf:surname   ?fname }  . "
		                     + "  }";
		SparqlRuleParser parser = new SparqlRuleParser(queryString);

		StringWriter sw = new StringWriter();
		SparqlRuleWriter writer = new SparqlRuleWriter(sw);
		for (Prefix p : parser.getPrefixes())
			writer.write(p);

		writer.write(parser.getRule());
		writer.close();

		String s = sw.toString();

		try {
			parser = new SparqlRuleParser(s);
		} catch (QueryParseException e) {
			Assert.assertFalse("QueryParseException", true);
		}
	}

	@Test
	public void testRDFType() {
		String query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
		               + "PREFIX : <"
		               + PREFIX
		               + ">"
		               + "CONSTRUCT"
		               + "{"
		               + "  ?x rdf:type :B "
		               + "}"
		               + "WHERE"
		               + "{"
		               + "	?x a :A  ."
		               + "}";
		Rule rule = new SparqlRuleParser(query).getRule();
		CloseableIteratorWithoutException<Atom> it = rule.getBody().iterator();
		while (it.hasNext()) {
			Atom a = it.next();
			Assert.assertEquals(A, a.getPredicate());
		}
		it = rule.getHead().iterator();
		while (it.hasNext()) {
			Atom a = it.next();
			Assert.assertEquals(B, a.getPredicate());
		}
	}

	@Test
	public void testIntegerLiteral() {
		String query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
		               + "PREFIX : <"
		               + PREFIX
		               + ">"
		               + "CONSTRUCT"
		               + "{"
		               + "  ?x :q 7 "
		               + "}"
		               + "WHERE"
		               + "{"
		               + "	?x :p 7 ."
		               + "}";
		Rule rule = new SparqlRuleParser(query).getRule();
		CloseableIteratorWithoutException<Atom> it = rule.getBody().iterator();
		while (it.hasNext()) {
			Atom a = it.next();
			Assert.assertEquals(P, a.getPredicate());
			Assert.assertEquals(INTEGER, a.getTerm(1));
		}
		it = rule.getHead().iterator();
		while (it.hasNext()) {
			Atom a = it.next();
			Assert.assertEquals(Q, a.getPredicate());
			Assert.assertEquals(INTEGER, a.getTerm(1));
		}
	}

	@Test
	public void testStringLiteral() {
		String query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
		               + "PREFIX : <"
		               + PREFIX
		               + ">"
		               + "CONSTRUCT"
		               + "{"
		               + "  ?x :q 'toto' "
		               + "}"
		               + "WHERE"
		               + "{"
		               + "	?x :p 'toto' ."
		               + "}";
		Rule rule = new SparqlRuleParser(query).getRule();
		CloseableIteratorWithoutException<Atom> it = rule.getBody().iterator();
		while (it.hasNext()) {
			Atom a = it.next();
			Assert.assertEquals(P, a.getPredicate());
			Assert.assertEquals(STRING, a.getTerm(1));
		}
		it = rule.getHead().iterator();
		while (it.hasNext()) {
			Atom a = it.next();
			Assert.assertEquals(Q, a.getPredicate());
			Assert.assertEquals(STRING, a.getTerm(1));
		}
	}

}
