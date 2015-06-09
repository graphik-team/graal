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
package fr.lirmm.graphik.graal.examples;

import fr.lirmm.graphik.graal.io.owl.OWLParser;
import fr.lirmm.graphik.graal.io.owl.OWLParserException;
import fr.lirmm.graphik.util.Prefix;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class OWLParserExemple2 {

	public static void main(String args[]) throws OWLParserException {
		 OWLParser parser = new OWLParser(
		 "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> . "
		 + "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> ."
		 + "@prefix owl: <http://www.w3.org/2002/07/owl#> . "
		 + "@prefix test: <http://test.org/> . "
		 + "test:ClassA rdf:type owl:Class . "
		 // + "test:toto rdf:type test:ClassA ."
		 + "test:ClassB rdf:type owl:Class . "
		 // + "test:ClassC rdf:type owl:Class . "
		 + "test:ClassD rdf:type owl:Class . "
		 + "test:ClassE rdf:type owl:Class . "
		 // + "test:property rdfs:domain test:ClassA ."
		 // + "test:ClassD owl:complementOf test:ClassC ."
		 // // +
		 + "test:ClassA rdfs:subClassOf [ "
		 +
 "			owl:unionOf ( test:ClassB [ owl:complementOf test:ClassD ] ) ]. "
		 + "test:toto a [ "
		 +
 "			owl:unionOf ( test:ClassB [ owl:complementOf test:ClassD ] ) ]. "
		

						+ "test:toto a test:ClassD .");
		parser = new OWLParser(
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
						+ "test:p rdf:type owl:ObjectProperty."
						+ "				[ rdf:type           owl:Restriction ;   "
						+ "			      rdfs:subClassOf    test:ClassC ; "
						+ "				  owl:onProperty     test:p ;   "
						+ "               owl:allValuesFrom   test:ClassB ] . "
						+ "		"
						+ "");
//		OWLParser parser = new OWLParser(
//				"@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> . "
//						+ "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> ."
//						+ "@prefix owl: <http://www.w3.org/2002/07/owl#> . "
//						+ "@prefix test: <http://test.org/> . "
//						+ "test:ClassA rdf:type owl:Class . "
//						// + "test:toto rdf:type test:ClassA ."
//						+ "test:ClassB rdf:type owl:Class . "
//						// + "test:ClassC rdf:type owl:Class . "
//						+ "test:ClassD rdf:type owl:Class . "
//						+ "test:ClassE rdf:type owl:Class . "
//						// + "test:property rdfs:domain test:ClassA ."
//						// + "test:ClassD owl:complementOf test:ClassC ."
//						// // +
//						+ "[ owl:oneOf ( test:a test:b ) ] rdfs:subClassOf test:ClassA "
//						+ "			. "
// + " "
//						//
//						//
//						// // +
//						// "test:ClassA rdfs:subClassOf owl:UnionOf test:ClassB owl:ComplementOf test:ClassC . "
//						+ "");

		for (Object o : parser) {
			if (!(o instanceof Prefix)) {
				System.out.println(o);

			}
		}
	}
	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////////
	// OBJECT OVERRIDE METHODS
	// /////////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

}
