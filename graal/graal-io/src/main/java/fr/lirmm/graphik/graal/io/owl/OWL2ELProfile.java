/**
 * 
 */
package fr.lirmm.graphik.graal.io.owl;

import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDatatypeDefinitionAxiom;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointUnionAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLHasKeyAxiom;
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.SWRLRule;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class OWL2ELProfile {
	
	/*OWLAxiomParser full = OWLAxiomParser.getInstance();
	
	// /////////////////////////////////////////////////////////////////////////
	// METADATA AXIOMS
	// /////////////////////////////////////////////////////////////////////////
	
	@Override
	public Iterable<? extends Object> visit(OWLDeclarationAxiom arg) {
		return full.visit(arg);
	}
	
	@Override
	public Iterable<? extends Object> visit(OWLAnnotationAssertionAxiom arg) {
		return full.visit(arg);
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// AXIOMS
	// /////////////////////////////////////////////////////////////////////////
	
	@Override
	public Iterable<? extends Object> visit(OWLSubClassOfAxiom arg) {
		return full.visit(arg);
	}
	
	@Override
	public Iterable<? extends Object> visit(OWLEquivalentClassesAxiom arg) {
		return full.visit(arg);
	}

	@Override
	public Iterable<? extends Object> visit(OWLDisjointClassesAxiom arg) {
		return full.visit(arg);
	}
	
	@Override
	public Iterable<? extends Object> visit(OWLSubObjectPropertyOfAxiom arg) {
		return full.visit(arg);
	}
	
	@Override
	public Iterable<? extends Object> visit(OWLEquivalentObjectPropertiesAxiom arg) {
		return full.visit(arg);
	}
	
	@Override
	public Iterable<? extends Object> visit(OWLTransitiveObjectPropertyAxiom arg) {
		return full.visit(arg);
	}
	
	@Override
	public Iterable<? extends Object> visit(OWLReflexiveObjectPropertyAxiom arg) {
		return full.visit(arg);
	}
	
	@Override
	public Iterable<? extends Object> visit(OWLObjectPropertyDomainAxiom arg) {
		return full.visit(arg);
	}
	
	@Override
	public Iterable<? extends Object> visit(OWLObjectPropertyRangeAxiom arg) {
		return full.visit(arg);
	}
	
	@Override
	public Iterable<? extends Object> visit(OWLSameIndividualAxiom arg) {
		return full.visit(arg);
	}
	
	@Override
	public Iterable<? extends Object> visit(OWLDifferentIndividualsAxiom arg) {
		return full.visit(arg);
	}
	
	@Override
	public Iterable<? extends Object> visit(OWLClassAssertionAxiom arg) {
		return full.visit(arg);
	}
	
	@Override
	public Iterable<? extends Object> visit(OWLObjectPropertyAssertionAxiom arg) {
		return full.visit(arg);
	}
	
	@Override
	public Iterable<? extends Object> visit(OWLNegativeObjectPropertyAssertionAxiom arg) {
		return full.visit(arg);
	}
			
	@Override
	public Iterable<? extends Object> visit(OWLHasKeyAxiom arg) {
		return full.visit(arg);
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// Data
	// /////////////////////////////////////////////////////////////////////////
	
	@Override
	public Iterable<? extends Object> visit(OWLSubDataPropertyOfAxiom arg) {
		return null;
	}
	
	@Override
	public Iterable<? extends Object> visit(OWLEquivalentDataPropertiesAxiom arg) {
		return null;
	}
	
	@Override
	public Iterable<? extends Object> visit(OWLDataPropertyDomainAxiom arg) {
		return null;
	}
	
	@Override
	public Iterable<? extends Object> visit(OWLDataPropertyRangeAxiom arg) {
		return null;
	}
	
	@Override
	public Iterable<? extends Object> visit(OWLDataPropertyAssertionAxiom arg) {
		return null;
	}

	@Override
	public Iterable<? extends Object> visit(OWLNegativeDataPropertyAssertionAxiom arg) {
		return null;
	}

	@Override
	public Iterable<? extends Object> visit(OWLFunctionalDataPropertyAxiom arg) {
		return null;
	}

	// /////////////////////////////////////////////////////////////////////////
	// 
	// /////////////////////////////////////////////////////////////////////////
	
	@Override
	public Iterable<? extends Object> visit(OWLAsymmetricObjectPropertyAxiom arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public Iterable<? extends Object> visit(OWLDisjointDataPropertiesAxiom arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public Iterable<? extends Object> visit(OWLDisjointObjectPropertiesAxiom arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public Iterable<? extends Object> visit(OWLFunctionalObjectPropertyAxiom arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public Iterable<? extends Object> visit(OWLDisjointUnionAxiom arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public Iterable<? extends Object> visit(OWLSymmetricObjectPropertyAxiom arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public Iterable<? extends Object> visit(OWLIrreflexiveObjectPropertyAxiom arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public Iterable<? extends Object> visit(
			OWLInverseFunctionalObjectPropertyAxiom arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public Iterable<? extends Object> visit(OWLSubPropertyChainOfAxiom arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public Iterable<? extends Object> visit(OWLInverseObjectPropertiesAxiom arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public Iterable<? extends Object> visit(OWLDatatypeDefinitionAxiom arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public Iterable<? extends Object> visit(SWRLRule arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public Iterable<? extends Object> visit(OWLSubAnnotationPropertyOfAxiom arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public Iterable<? extends Object> visit(OWLAnnotationPropertyDomainAxiom arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public Iterable<? extends Object> visit(OWLAnnotationPropertyRangeAxiom arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}*/
	
	
}
