/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2016)
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
package fr.lirmm.graphik.graal.examples;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import fr.lirmm.graphik.graal.api.backward_chaining.QueryRewriter;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.backward_chaining.pure.PureRewriter;
import fr.lirmm.graphik.graal.io.sparql.SparqlConjunctiveQueryParser;
import fr.lirmm.graphik.graal.io.sparql.SparqlConjunctiveQueryWriter;
import fr.lirmm.graphik.graal.io.sparql.SparqlRuleParser;
import fr.lirmm.graphik.util.Prefix;
import fr.lirmm.graphik.util.stream.GIterator;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class SPARQLRewriting {

	static final String sparqlQuery = " PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>              "
	                                  + " PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>       "
	                                  + " SELECT DISTINCT ?0                                                     "
	                                  + "   WHERE                                                                "
	                                  + "   {                                                                    "
	                                  + "     ?0  a :Person .                                                    "
	                                  + "     ?0  :worksFor ?1  .                                                "
	                                  + "     ?1  a :University .                                                "
	                                  + "     ?1  :hasAlumnus ?0                                                 "
	                                  + "   }                                                                    ";

	public static void main(String[] args) throws IOException {
		List<Prefix> prefixes;
		ConjunctiveQuery query;
		LinkedList<Rule> ontology = new LinkedList<Rule>();
		SparqlConjunctiveQueryWriter writer = new SparqlConjunctiveQueryWriter();
		QueryRewriter bc;

		// 1 - Parse the SPARQL query
		SparqlConjunctiveQueryParser queryParser = new SparqlConjunctiveQueryParser(sparqlQuery);
		prefixes = queryParser.getPrefixes();
		query = queryParser.getConjunctiveQuery();

		// 2 - Parse the ontology (provides as SPARQL CONSTRUCT queries)
		for (String rule : ontology()) {
			SparqlRuleParser ruleParser = new SparqlRuleParser(rule);
			ontology.add(ruleParser.getRule());
		}

		// 3 - Execute the query rewriter
		bc = new PureRewriter();
		GIterator<ConjunctiveQuery> it = bc.execute(query, ontology);

		while (it.hasNext()) {
			// 4 - Print the query as SPARQL
			writer.write("\n");
			for (Prefix p : prefixes) {
				writer.write(p);
			}
			writer.write(it.next());
			writer.flush();
		}

		// 5 - Close the writer
		writer.close();

	}

	/**
	 * Provides a List of String representing the LUBM ontology (U version) as
	 * SPARQL CONSTRUCT query.
	 * 
	 * @return
	 */
	static LinkedList<String> ontology() {
		LinkedList<String> rules = new LinkedList<>();
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  univ-bench:degreeFrom ?X1  "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X0  univ-bench:undergraduateDegreeFrom ?X1  "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  univ-bench:worksFor ?X1  "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X0  univ-bench:headOf ?X1  "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  univ-bench:degreeFrom ?X1  "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X0  univ-bench:mastersDegreeFrom ?X1  "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  univ-bench:degreeFrom ?X1  "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X0  univ-bench:doctoralDegreeFrom ?X1  "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  univ-bench:memberOf ?X1  "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X0  univ-bench:worksFor ?X1  "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  a univ-bench:Person "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X0  univ-bench:degreeFrom ?X3  "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  a univ-bench:Person "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X0  univ-bench:mastersDegreeFrom ?X3  "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  a univ-bench:Professor "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X0  univ-bench:tenured ?X3  "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  a univ-bench:University "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X0  univ-bench:hasAlumnus ?X3  "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  a univ-bench:Person "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X0  univ-bench:advisor ?X3  "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  a univ-bench:Organization "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X0  univ-bench:affiliatedOrganizationOf ?X3  "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  a univ-bench:University "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X0  univ-bench:hasFaculty ?X3  "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  a univ-bench:Faculty "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X0  univ-bench:isPartOfUniversity ?X3  "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  a univ-bench:Student "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X0  univ-bench:hasExamRecord ?X3  "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  a univ-bench:Organization "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X0  univ-bench:affiliateOf ?X3  "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  a univ-bench:Organization "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X0  univ-bench:orgPublication ?X3  "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  a univ-bench:Person "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X0  univ-bench:doctoralDegreeFrom ?X3  "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  a univ-bench:FacultyStaff "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X0  univ-bench:teacherOf ?X3  "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  a univ-bench:Organization "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X0  univ-bench:member ?X3  "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  a univ-bench:Person "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X0  univ-bench:undergraduateDegreeFrom ?X3  "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  univ-bench:worksFor ?X3 .  "
		          + "	?X3  a univ-bench:Organization "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X0  a univ-bench:Employee "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  a univ-bench:Work "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X0  a univ-bench:Exam "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  a univ-bench:Professor "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X0  a univ-bench:AssociateProfessor "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  a univ-bench:Organization "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X0  a univ-bench:Program "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  a univ-bench:Professor "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X0  a univ-bench:AssistantProfessor "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  a univ-bench:Course "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X0  a univ-bench:GraduateCourse "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  a univ-bench:Work "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X0  a univ-bench:Degree "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  a univ-bench:Employee "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X0  a univ-bench:AdministrativeStaff "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  a univ-bench:AdministrativeStaff "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X0  a univ-bench:ClericalStaff "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  a univ-bench:Person "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X0  a univ-bench:Director "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  a univ-bench:Professor "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X0  a univ-bench:ExDean "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  a univ-bench:Professor "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X0  a univ-bench:Dean "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  a univ-bench:FacultyStaff "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X0  a univ-bench:Professor "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  a univ-bench:Student "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X0  a univ-bench:ResearchAssistant "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  univ-bench:takesCourse ?X3  .  "
		          + "	?X3  a univ-bench:Course "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X0  a univ-bench:Student "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  a univ-bench:Work "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X0  a univ-bench:Career "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  a univ-bench:Person "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X0  a univ-bench:Employee "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  a univ-bench:FacultyStaff "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X0  a univ-bench:Lecturer "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  a univ-bench:Person "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X0  a univ-bench:GraduateStudent "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  a univ-bench:AdministrativeStaff "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X0  a univ-bench:SystemsStaff "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  a univ-bench:Professor "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X0  a univ-bench:FullProfessor "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  a univ-bench:Work "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X0  a univ-bench:ExamRecord "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  a univ-bench:Work "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X0  a univ-bench:Course "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  univ-bench:headOf ?X3  .  "
		          + "	?X3  a univ-bench:Program "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X0  a univ-bench:Director "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  a univ-bench:Organization "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X0  a univ-bench:College "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  a univ-bench:Professor "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X0  a univ-bench:VisitingProfessor "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  a univ-bench:FacultyStaff "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X0  a univ-bench:PostDoc "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  a univ-bench:Student "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X0  a univ-bench:UndergraduateStudent "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  a univ-bench:Professor "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X0  a univ-bench:Chair "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  univ-bench:headOf ?X3  .  "
		          + "	?X3  a univ-bench:College "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X0  a univ-bench:Dean "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  a univ-bench:Person "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X0  a univ-bench:Student "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  a univ-bench:Exam "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X0  a univ-bench:BachelorExam "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  a univ-bench:Organization "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X0  a univ-bench:Faculty "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  univ-bench:takesCourse ?X3  .  "
		          + "	?X3  a univ-bench:GraduateCourse "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X0  a univ-bench:GraduateStudent "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  a univ-bench:Employee "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X0  a univ-bench:FacultyStaff "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  a univ-bench:Organization "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X0  a univ-bench:University "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X1  univ-bench:hasAlumnus ?X0  "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X0  univ-bench:degreeFrom ?X1  "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  univ-bench:degreeFrom ?X1  "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X1  univ-bench:hasAlumnus ?X0  "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X1  univ-bench:isPartOfUniversity ?X0  "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X0  univ-bench:hasFaculty ?X1  "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  univ-bench:hasFaculty ?X1  "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X1  univ-bench:isPartOfUniversity ?X0  "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X1  univ-bench:memberOf ?X0  "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X0  univ-bench:member ?X1  "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  univ-bench:member ?X1  "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X1  univ-bench:memberOf ?X0  "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  a univ-bench:Organization "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X3  univ-bench:affiliatedOrganizationOf ?X0  "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  a univ-bench:Person "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X3  univ-bench:member ?X0  "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  a univ-bench:Course "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X3  univ-bench:teachingAssistantOf ?X0  "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  a univ-bench:Person "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X3  univ-bench:hasAlumnus ?X0  "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  a univ-bench:University "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X3  univ-bench:mastersDegreeFrom ?X0  "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  a univ-bench:University "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X3  univ-bench:isPartOfUniversity ?X0  "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  a univ-bench:University "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X3  univ-bench:doctoralDegreeFrom ?X0  "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  a univ-bench:Person "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X3  univ-bench:publicationAuthor ?X0  "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  a univ-bench:University "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X3  univ-bench:degreeFrom ?X0  "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  a univ-bench:Faculty "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X3  univ-bench:hasFaculty ?X0  "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  a univ-bench:University "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X3  univ-bench:undergraduateDegreeFrom ?X0  "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  a univ-bench:Course "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X3  univ-bench:teacherOf ?X0  "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  a univ-bench:Person "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X3  univ-bench:affiliateOf ?X0  "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  a univ-bench:ExamRecord "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X3  univ-bench:hasExamRecord ?X0  "
		          + " }  ");
		rules.add(" PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          + " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  "
		          + " PREFIX owl2xml: <http://www.w3.org/2006/12/owl2-xml#>  "
		          + " PREFIX univ-bench: <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>  "
		          +

		          "CONSTRUCT "
		          + " {  "
		          + "	?X0  a univ-bench:Professor "
		          + " }  "
		          + "WHERE "
		          + " {  "
		          + "	?X3  univ-bench:advisor ?X0  "
		          + " }  ");
		return rules;
	}
}
