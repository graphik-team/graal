package fr.lirmm.graphik.graal.io.sparql;

import org.junit.Assert;
import org.junit.Test;

import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.io.ParseException;
import fr.lirmm.graphik.util.Iterators;


/**
 * 
 */

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class SparqlTest {

	@Test
	public void test1() throws ParseException {
		String query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
					   + "PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>"
					   + "SELECT DISTINCT ?0 "
					   + "WHERE"
					   + "{"
					   + "	?0  :worksFor ?1  ."
					   + "	?1  :affiliatedOrganizationOf ?2 "
					   + "}";
		ConjunctiveQuery cq = SparqlConjunctiveQueryParser.parse(query);
		Assert.assertEquals(1, cq.getAnswerVariables().size());
		Assert.assertEquals(2, Iterators.count(cq.getAtomSet().iterator()));
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

}
