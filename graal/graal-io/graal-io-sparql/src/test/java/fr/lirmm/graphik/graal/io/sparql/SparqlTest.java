package fr.lirmm.graphik.graal.io.sparql;

import org.junit.Assert;
import org.junit.Test;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.term.Constant;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.core.term.Literal;
import fr.lirmm.graphik.graal.io.ParseException;
import fr.lirmm.graphik.util.Iterators;
import fr.lirmm.graphik.util.URIUtils;


/**
 * 
 */

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class SparqlTest {

	private static final String PREFIX = "http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#";
	private static final Predicate P = new Predicate(URIUtils.createURI(PREFIX + "p"), 2);
	private static final Predicate Q = new Predicate(URIUtils.createURI(PREFIX + "q"), 2);
	private static final Constant TOTO = DefaultTermFactory.instance().createConstant(
			URIUtils.createURI(PREFIX + "toto"));
	private static final Constant TITI = DefaultTermFactory.instance().createConstant(
			URIUtils.createURI(PREFIX + "titi"));
	private static final Literal STRING = DefaultTermFactory.instance().createLiteral(URIUtils.XSD_STRING, "toto");

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
		ConjunctiveQuery cq = SparqlConjunctiveQueryParser.parse(query);
		Assert.assertEquals(1, cq.getAnswerVariables().size());
		int nbTriple = 0;
		for (Atom a : cq.getAtomSet()) {
			++nbTriple;
			Assert.assertTrue("Unrecognized triple", P.equals(a.getPredicate()) || Q.equals(a.getPredicate()));
		}
		Assert.assertEquals(2, nbTriple);
	}

	@Test
	public void test2() throws ParseException {
		String query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
					   + "PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>"
					   + "SELECT DISTINCT ?0 ?1 ?2"
					   + "WHERE"
					   + "{"
					   + "	?0  :worksFor ?1  ."
					   + "	?1  :affiliatedOrganizationOf ?2 "
					   + "}";
		ConjunctiveQuery cq = SparqlConjunctiveQueryParser.parse(query);
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
		ConjunctiveQuery cq = SparqlConjunctiveQueryParser.parse(query);
		Assert.assertEquals(2, cq.getAnswerVariables().size());
		int nbTriple = 0;
		for (Atom a : cq.getAtomSet()) {
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
	public void testStringLiteral() throws ParseException {
		String query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
				   + "PREFIX : <"
				   + PREFIX
				   + ">"
				   + "SELECT DISTINCT ?x ?y "
				   + "WHERE"
				   + "{"
				   + "	?x :p \"toto\" ."
				   + "}";
    	ConjunctiveQuery cq = SparqlConjunctiveQueryParser.parse(query);
    	Assert.assertEquals(2, cq.getAnswerVariables().size());
    	int nbTriple = 0;
    	for (Atom a : cq.getAtomSet()) {
    		++nbTriple;
    		Assert.assertEquals(P, a.getPredicate());
			Assert.assertEquals(a.getTerm(1), STRING);
    		
    	}
    	Assert.assertEquals(1, nbTriple);
	}


}
