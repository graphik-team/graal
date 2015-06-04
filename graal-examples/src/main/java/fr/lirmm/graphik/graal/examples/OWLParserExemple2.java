/**
 * 
 */
package fr.lirmm.graphik.graal.examples;

import java.io.IOException;

import fr.lirmm.graphik.graal.io.dlp.DlgpWriter;
import fr.lirmm.graphik.graal.io.owl.OWL2Parser;
import fr.lirmm.graphik.graal.io.owl.OWL2ParserException;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class OWLParserExemple2 {

	public static void main(String args[]) throws OWL2ParserException,
			IOException {

			String owl = "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> . "
						+ "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> ."
						+ "@prefix owl: <http://www.w3.org/2002/07/owl#> . "
						+ "@prefix : <http://test.org/> . "
						+ ":A rdf:type owl:Class . "
						+ ":B rdf:type owl:Class . "
						+ ":C rdf:type owl:Class . "
						+ ":D rdf:type owl:Class . "
						+ ":E rdf:type owl:Class . "
				+ ":F rdf:type owl:Class . "
						+ ":p rdf:type owl:ObjectProperty . "
				+ ":q rdf:type owl:ObjectProperty . "
				

				// " :A rdfs:subClassOf [owl:intersectionOf ( :C [owl:complementOf  [a owl:Restriction; owl:onProperty :p ; owl:allValuesFrom :E]] ) ] .";
				
				+ " [rdf:type owl:Restriction; owl:onProperty :p; "
				+ "     owl:someValuesFrom [rdf:type owl:Restriction; owl:onProperty :p ; owl:allValuesFrom :A] ] rdfs:subClassOf :B .";
				
				
				// +
				// " [owl:unionOf ( :A [owl:complementOf :B])] rdfs:subClassOf :C .";
				
				// +
				// "[ owl:intersectionOf ( :D [ owl:unionOf ( :A :B ) ]) ] rdfs:subClassOf [ rdf:type owl:Restriction; owl:onProperty :p ; owl:allValuesFrom :C ] .";
		// + "[owl:intersectionOf ( "
		// + "		:C "
		// + "		[owl:unionOf ( :A :B ) ] "
		// +
		// "		[ a owl:Restriction; owl:onProperty :p ; owl:someValuesFrom [owl:intersectionOf "
		// + "			( [owl:unionOf ( :C :D )] "
		// +
		// "				[ a owl:Restriction; owl:onProperty :q; owl:someValuesFrom :E]"
		// + "			)" + "		]] ) ]" + "rdfs:subClassOf :F .";// +
		// "test:ClassA rdfs:subClassOf [owl:unionOf (test:ClassB test:ClassC )]. ";
		// + "test:ClassE rdfs:subClassOf [ owl:complementOf test:ClassD ]. "
		// + "test:ClassC owl:equivalentClass test:ClassE. "
		// + "test:ClassA rdfs:subClassOf ["
		// + "		owl:intersectionOf ( test:ClassC test:ClassD ) ]. "
		// +
		// "[ owl:unionOf ( test:ClassC test:ClassD ) ] rdfs:subClassOf test:ClassA."
		// +
		// "[ a owl:Restriction; owl:someValuesFrom test:ClassA; owl:onProperty :prop1 ] rdfs:subClassOf test:ClassB. "
		// + "[ rdf:type             owl:Restriction ;   "
		// + "  rdfs:subClassOf      test:ClassC ; "
		// + "  owl:onProperty       test:prop1 ;   "
		// + "  owl:someValuesFrom   test:ClassA ]. "
		// + "[ rdf:type             owl:Restriction ;   "
		// + "  rdfs:subClassOf      test:ClassC ; "
		// + "  owl:onProperty       test:prop1 ;   "
		// +
		// "  owl:someValuesFrom   [ owl:intersectionOf ( test:ClassA test:ClassB ) ] ]. "
		// + "[ rdf:type             owl:Restriction ;   "
		// + "  rdfs:subClassOf      test:ClassC ; "
		// + "  owl:onProperty       test:prop1 ;   "
		// + "  owl:someValuesFrom   [ rdf:type owl:Restriction ;"
		// + "							owl:onProperty test:prop1 ;"
		// + "	                        owl:someValuesFrom test:ClassA ] ]. "
		// + " test:ClassA rdfs:subClassOf [ rdf:type owl:Restriction ;"
		// + "							owl:onProperty test:prop1 ;"
		// + "	                        owl:someValuesFrom test:ClassB ]. "
		// + "[ rdf:type             owl:Restriction ;   "
		// + "  rdfs:subClassOf      test:ClassC ; "
		// + "  owl:onProperty       <http://example.com/p> ;   "
		// +
		// "  owl:someValuesFrom   [ owl:intersectionOf ( [ owl:unionOf ( test:ClassA test:ClassD ) ] test:ClassB ) ] ]. ";

		

		OWL2Parser parser = new OWL2Parser(owl);

		

		DlgpWriter w = new DlgpWriter();
		for (Object o : parser) {
			w.write(o);
			w.flush();
		}
		w.close();
	}

}
