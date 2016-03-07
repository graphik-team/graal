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
 package fr.lirmm.graphik.graal.trash;
//import java.util.Collections;
//
//import org.semanticweb.owlapi.apibinding.OWLManager;
//import org.semanticweb.owlapi.model.IRI;
//import org.semanticweb.owlapi.model.OWLClass;
//import org.semanticweb.owlapi.model.OWLClassExpression;
//import org.semanticweb.owlapi.model.OWLDataFactory;
//import org.semanticweb.owlapi.model.OWLDataIntersectionOf;
//import org.semanticweb.owlapi.model.OWLDataProperty;
//import org.semanticweb.owlapi.model.OWLDatatypeRestriction;
//import org.semanticweb.owlapi.model.OWLObjectInverseOf;
//import org.semanticweb.owlapi.model.OWLObjectProperty;
//import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
//import org.semanticweb.owlapi.model.OWLOntology;
//import org.semanticweb.owlapi.model.OWLOntologyCreationException;
//import org.semanticweb.owlapi.model.OWLOntologyManager;
//import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
//import org.semanticweb.owlapi.profiles.OWL2DLProfile;
//import org.semanticweb.owlapi.profiles.OWLProfileReport;
//import org.semanticweb.owlapi.profiles.OWLProfileViolation;
//import org.semanticweb.owlapi.util.OWLOntologyWalker;
//import org.semanticweb.owlapi.util.OWLOntologyWalkerVisitor;
//
///**
// * 
// */
//
///**
// * @author Clément Sipieter (INRIA) <clement@6pi.fr>
// *
// */
//public class OwlApiTest {
//
//	
//    public static final String SP_ENTITY_EXPANSION_LIMIT = "entityExpansionLimit"; 
//
//	public static void main(String[] args) throws OWLOntologyCreationException {
//		System.out.println("OwlApiTest");
////		System.setProperty(SP_ENTITY_EXPANSION_LIMIT, "128000");
////		System.out.println(System.getProperty(SP_ENTITY_EXPANSION_LIMIT));
//		
//		
//		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
//		OWLDataFactory dataFactory = OWLManager.getOWLDataFactory();
//		IRI iri = IRI.create("file:///home/clement/projets/ontologies/test.owl");
//		
//		OWLOntology ontology = manager.loadOntologyFromOntologyDocument(iri);
//		System.out.println("Ontology loaded");
////		checkProfiles(ontology);
////		showClasses(ontology);
////		getSuperClasses(
////				dataFactory.getOWLClass(IRI.create("http://swat.cse.lehigh.edu/onto/univ-bench.owl#VisitingProfessor")),
////				ontology);
//		
//		walk(ontology);
//		System.out.println("End");
//	}
//	
//	
//	/**
//	 * @param ontology
//	 */
//	private static void walk(OWLOntology ontology) {
//		System.out.println("### WALK ONTOLOGY");
//		OWLOntologyWalker walker = new OWLOntologyWalker(Collections.singleton(ontology));
//		OWLOntologyWalkerVisitor<Object> visitor = new TestVisitor(walker);
//		walker.walkStructure(visitor);
//	}
//
//
//	private static class TestVisitor extends OWLOntologyWalkerVisitor<Object> {
//
//		/**
//		 * @param walker
//		 */
//		public TestVisitor(OWLOntologyWalker walker) {
//			super(walker);
//		}
//		
//		@Override
//		public Object visit(OWLObjectSomeValuesFrom desc) {
//			System.out.println("--- SomeValuesFrom");
//			System.out.println(desc);
//			System.out.println(this.getCurrentAxiom());
//
//			return null;
//		}
//		
//		@Override
//		public Object visit(OWLDataIntersectionOf desc) {
//			System.out.println("--- IntersectionOf");
//			System.out.println(desc);
//			System.out.println(this.getCurrentAxiom());
//
//			return null;
//		}
//		
//		@Override
//		public Object visit(OWLDatatypeRestriction desc) {
//			System.out.println("--- Restriction");
//			System.out.println(desc);
//			System.out.println(this.getCurrentAxiom());
//
//			return null;
//		}
//		
//		@Override
//		public Object visit(OWLDataProperty prop) {
//			System.out.println("--- data property");
//			System.out.println(prop);
//			System.out.println(this.getCurrentAxiom());
//
//			return null;
//		}
//		
//		@Override
//		public Object visit(OWLObjectProperty prop) {
//			System.out.println("--- object property");
//			System.out.println(prop);
//			System.out.println(this.getCurrentAxiom());
//
//			return null;
//			
//		}
//		@Override 
//		public Object visit(OWLObjectInverseOf prop) {
//			System.out.println("--- inverse of");
//			System.out.println(prop);
//			System.out.println(this.getCurrentAxiom());
//
//			return null;
//		}
//		
//		@Override 
//		public Object visit(OWLSubPropertyChainOfAxiom axiom) {
//			System.out.println("--- chain of axiom");
//			System.out.println(axiom);
//			return null;
//		}
//		
//	}
//
//	/**
//	 * @param owlClass
//	 */
//	private static void getSuperClasses(OWLClass owlClass, OWLOntology ontology) {
//		System.out.println("### GET SUPER CLASSES");
//		for(OWLClassExpression cls : owlClass.getSuperClasses(ontology)) {
//			System.out.println(cls);
//		}
//	}
//
//	/**
//	 * @param ontology
//	 */
//	private static void showClasses(OWLOntology ontology) {
//		System.out.println("### SHOW CLASSES");
//		for(OWLClass cls : ontology.getClassesInSignature())
//			System.out.println(cls);
//	}
//
//	/**
//	 * @param ontology
//	 */
//	private static void checkProfiles(OWLOntology ontology) {
//		System.out.println("### CHECK PROFILE");
//		OWLProfileReport report = new OWL2DLProfile().checkOntology(ontology); 
//		for(OWLProfileViolation violation : report.getViolations()) {
//			System.out.println(violation);
//		}
//	}
//	
//	
//}
