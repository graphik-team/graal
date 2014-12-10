/**
 * 
 */
package fr.lirmm.graphik.graal.io.owl;

import java.io.File;
import java.io.IOException;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import fr.lirmm.graphik.graal.io.dlgp.DlgpWriter;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
public class OWLParser /* extends Iterator<Atom> */{

	public static void main(String args[]) throws OWLOntologyCreationException, IOException {
		
		DlgpWriter writer = new DlgpWriter(System.out);
		
		OWLOntologyManager man = OWLManager.createOWLOntologyManager();
		/*OWLOntology onto = man.loadOntologyFromOntologyDocument(new File(
				"/home/clement/graphik/ontologies/V/V.owl"));*/
		OWLOntology onto = man.loadOntologyFromOntologyDocument(new File(
				"./src/test/resources/V.owl"));
		

		OWL2ELProfile visitor = new OWL2ELProfile();
		

		for (OWLAxiom a : onto.getAxioms()) {
			Iterable iterable = a.accept(visitor);
			if(iterable != null) {
				for(Object o : iterable) {
					 writer.write(o);
				}
			}
		}
		
		/*OWLOntologyWalker walker = new OWLOntologyWalker(
				Collections.singleton(onto));

		walker.walkStructure(visitor);*/

	}


	
	
//	private static abstract class AbstractOWL2Profile implements
//			OWLObjectVisitorEx<Iterable<Object>> {
//		
//		private static final Logger logger = LoggerFactory
//				.getLogger(OWLParser.class);
//		
		
//		
//		// /////////////////////////////////////////////////////////////////////////
//		// NOT IMPLEMENTED
//		// /////////////////////////////////////////////////////////////////////////
//
//		@Override
//		public Iterable<Object> visit(OWLSubClassOfAxiom arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(OWLNegativeObjectPropertyAssertionAxiom arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(OWLAsymmetricObjectPropertyAxiom arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(OWLReflexiveObjectPropertyAxiom arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(OWLDisjointClassesAxiom arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(OWLDataPropertyDomainAxiom arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(OWLObjectPropertyDomainAxiom arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(OWLEquivalentObjectPropertiesAxiom arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(OWLNegativeDataPropertyAssertionAxiom arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(OWLDifferentIndividualsAxiom arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(OWLDisjointDataPropertiesAxiom arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(OWLDisjointObjectPropertiesAxiom arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(OWLObjectPropertyRangeAxiom arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(OWLObjectPropertyAssertionAxiom arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(OWLFunctionalObjectPropertyAxiom arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(OWLSubObjectPropertyOfAxiom arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(OWLDisjointUnionAxiom arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(OWLSymmetricObjectPropertyAxiom arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(OWLDataPropertyRangeAxiom arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(OWLFunctionalDataPropertyAxiom arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(OWLEquivalentDataPropertiesAxiom arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(OWLClassAssertionAxiom arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(OWLEquivalentClassesAxiom arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(OWLDataPropertyAssertionAxiom arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(OWLTransitiveObjectPropertyAxiom arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(OWLIrreflexiveObjectPropertyAxiom arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(OWLSubDataPropertyOfAxiom arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(OWLInverseFunctionalObjectPropertyAxiom arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(OWLSameIndividualAxiom arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(OWLSubPropertyChainOfAxiom arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(OWLInverseObjectPropertiesAxiom arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(OWLHasKeyAxiom arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(OWLDatatypeDefinitionAxiom arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(SWRLRule arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(OWLSubAnnotationPropertyOfAxiom arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(OWLAnnotationPropertyDomainAxiom arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(OWLAnnotationPropertyRangeAxiom arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(OWLObjectIntersectionOf arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(OWLObjectUnionOf arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(OWLObjectComplementOf arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(OWLObjectSomeValuesFrom arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(OWLObjectAllValuesFrom arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(OWLObjectHasValue arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(OWLObjectMinCardinality arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(OWLObjectExactCardinality arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(OWLObjectMaxCardinality arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(OWLObjectHasSelf arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(OWLObjectOneOf arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(OWLDataSomeValuesFrom arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(OWLDataAllValuesFrom arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(OWLDataHasValue arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(OWLDataMinCardinality arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(OWLDataExactCardinality arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(OWLDataMaxCardinality arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(OWLDataComplementOf arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(OWLDataOneOf arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(OWLDataIntersectionOf arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(OWLDataUnionOf arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(OWLDatatypeRestriction arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(OWLFacetRestriction arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(OWLObjectInverseOf arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(OWLDataProperty arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(OWLNamedIndividual arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(OWLAnonymousIndividual arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(SWRLClassAtom arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(SWRLDataRangeAtom arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(SWRLObjectPropertyAtom arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(SWRLDataPropertyAtom arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(SWRLBuiltInAtom arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(SWRLVariable arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(SWRLIndividualArgument arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(SWRLLiteralArgument arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(SWRLSameIndividualAtom arg0) {
//			throw new WrongProfileError();
//		}
//
//		@Override
//		public Iterable<Object> visit(SWRLDifferentIndividualsAtom arg0) {
//			throw new WrongProfileError();
//		}
//		
//		
//
//	}



}
