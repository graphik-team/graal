/**
 * 
 */
package fr.lirmm.graphik.graal.io.owl;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLAxiomVisitorEx;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLClassExpressionVisitorEx;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
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
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasSelf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.DefaultFreeVarGen;
import fr.lirmm.graphik.graal.core.DefaultRule;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.Substitution;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.Term.Type;
import fr.lirmm.graphik.graal.core.TreeMapSubstitution;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class OWLAxiomParser implements OWLAxiomVisitorEx<Iterable<? extends Object>> {
	
	private static final Term X = new Term("X", Term.Type.VARIABLE);
	private static final Term Y = new Term("Y", Term.Type.VARIABLE);
	
	private static final Logger logger = LoggerFactory
			.getLogger(OWLAxiomParser.class);
	
	// /////////////////////////////////////////////////////////////////////////
	// SINGLETON
	// /////////////////////////////////////////////////////////////////////////
	
	private static OWLAxiomParser instance;

	private OWLAxiomParser() {
	}

	public static synchronized OWLAxiomParser getInstance() {
		if (instance == null)
			instance = new OWLAxiomParser();

		return instance;
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// METADATA AXIOMS
	// /////////////////////////////////////////////////////////////////////////
	
	@Override
	public Iterable<? extends Object> visit(OWLDeclarationAxiom arg) {
		if(logger.isInfoEnabled()) {
			logger.info("Visit OWLDeclarationAxiom: " + arg);
		}
		return null;
	}
	
	@Override
	public Iterable<? extends Object> visit(OWLAnnotationAssertionAxiom arg) {
		if(logger.isInfoEnabled()) {
			logger.info("Visit OWLAnnotationAssertionAxiom: " + arg);
		}
		return null;
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// 
	// /////////////////////////////////////////////////////////////////////////

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
	}

	@Override
	public Iterable<? extends Object> visit(OWLSubClassOfAxiom arg) {
		AtomSet superClass = arg.getSuperClass().accept(classVisitor);
		AtomSet subClass = arg.getSubClass().accept(classVisitor);
		
		Rule rule = new DefaultRule(subClass, superClass);

		return Collections.singleton(rule);
	}

	@Override
	public Iterable<? extends Object> visit(OWLNegativeObjectPropertyAssertionAxiom arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public Iterable<? extends Object> visit(OWLAsymmetricObjectPropertyAxiom arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public Iterable<? extends Object> visit(OWLReflexiveObjectPropertyAxiom arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public Iterable<? extends Object> visit(OWLDisjointClassesAxiom arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public Iterable<? extends Object> visit(OWLDataPropertyDomainAxiom arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public Iterable<? extends Object> visit(OWLObjectPropertyDomainAxiom arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public Iterable<? extends Object> visit(OWLEquivalentObjectPropertiesAxiom arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public Iterable<? extends Object> visit(OWLNegativeDataPropertyAssertionAxiom arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public Iterable<? extends Object> visit(OWLDifferentIndividualsAxiom arg0) {
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
	public Iterable<? extends Object> visit(OWLObjectPropertyRangeAxiom arg) {
		Predicate predicate = this.createPredicate(arg.getProperty());
		AtomSet range = arg.getRange().accept(classVisitor);
		
		Rule rule = this.createRule();
		rule.getBody().add(this.createAtom(predicate, X, Y));
		try {
			rule.getHead().addAll(range);
		} catch (AtomSetException e) {
		}
		
		return Collections.singleton(rule);
	}

	@Override
	public Iterable<? extends Object> visit(OWLObjectPropertyAssertionAxiom arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public Iterable<? extends Object> visit(OWLFunctionalObjectPropertyAxiom arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public Iterable<? extends Object> visit(OWLSubObjectPropertyOfAxiom arg0) {
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
	public Iterable<? extends Object> visit(OWLDataPropertyRangeAxiom arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public Iterable<? extends Object> visit(OWLFunctionalDataPropertyAxiom arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public Iterable<? extends Object> visit(OWLEquivalentDataPropertiesAxiom arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public Iterable<? extends Object> visit(OWLClassAssertionAxiom arg) {
		Term i = new Term(arg.getIndividual().toString(), Type.CONSTANT);
		return arg.getClassExpression().accept(new OWLClassExpressionVisitorImpl(i));
	}

	@Override
	public Iterable<? extends Object> visit(OWLEquivalentClassesAxiom arg) {
		Collection<Rule> c = this.<Rule>createCollection();
		AtomSet atomset1, atomset2;
		
		List<OWLClassExpression> classes = new LinkedList<OWLClassExpression>(arg.getClassExpressionsAsList());
		
		Iterator<OWLClassExpression> it1, it2;
		it1 = classes.iterator();
		while(it1.hasNext()) {
			OWLClassExpression classExpr = it1.next();
			it1.remove();
			
			it2 = classes.iterator();
			while(it2.hasNext()) {
				atomset1 = classExpr.accept(classVisitor);
				atomset2 = it2.next().accept(classVisitor);
				c.add(new DefaultRule(atomset1, atomset2));
				c.add(new DefaultRule(atomset2, atomset1));
			}
		}
		return c;
	}
	
	@Override
	public Iterable<? extends Object> visit(OWLDataPropertyAssertionAxiom arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public Iterable<? extends Object> visit(OWLTransitiveObjectPropertyAxiom arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public Iterable<? extends Object> visit(OWLIrreflexiveObjectPropertyAxiom arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public Iterable<? extends Object> visit(OWLSubDataPropertyOfAxiom arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public Iterable<? extends Object> visit(OWLInverseFunctionalObjectPropertyAxiom arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public Iterable<? extends Object> visit(OWLSameIndividualAxiom arg0) {
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
	public Iterable<? extends Object> visit(OWLHasKeyAxiom arg0) {
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
	
	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////
	private Term glueVar = freeVarGen.getFreeVar();
	private OWLClassExpressionVisitorImpl classVisitor = new OWLClassExpressionVisitorImpl(glueVar);
	
	private static DefaultFreeVarGen freeVarGen = new DefaultFreeVarGen("X" + OWLAxiomParser.class.hashCode() + "_");	


	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE FACTORY
	// /////////////////////////////////////////////////////////////////////////
	
	private <T> Collection<T> createCollection() {
		return new LinkedList<T>();
	}
	
	private Rule createRule() {
		return new DefaultRule();
	}
	
	private Atom createAtom(Predicate p, Term... terms) {
		return new DefaultAtom(p, terms);
	}
	
	/**
	 * @param property
	 * @return
	 */
	private Predicate createPredicate(OWLObjectPropertyExpression property) {
		Predicate predicate = null;
		if(!property.isAnonymous()) {
			predicate = new Predicate(property.asOWLObjectProperty().getIRI().toString(), 2);
		} else {
			throw new Error("not yet implemented");
		}
		return predicate;
	}
}
