/**
 * 
 */
package fr.lirmm.graphik.graal.io.owl;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLAxiomVisitorEx;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
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
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
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
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.Term.Type;
import fr.lirmm.graphik.graal.logic.Literal;
import fr.lirmm.graphik.graal.logic.LogicalFormula;
import fr.lirmm.graphik.graal.logic.LogicalFormulaRuleTranslator;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class OWLAxiomParser implements OWLAxiomVisitorEx<Iterable<? extends Object>> {
	
	private Term glueVarX = freeVarGen.getFreeVar();
	private Term glueVarY = freeVarGen.getFreeVar();
	private Term glueVarZ = freeVarGen.getFreeVar();
	
	private Predicate equalityPredicate = new Predicate("=", 2);
	private OWLClassExpressionVisitorImpl2 classVisitorX = new OWLClassExpressionVisitorImpl2(glueVarX);
	private OWLClassExpressionVisitorImpl2 classVisitorY = new OWLClassExpressionVisitorImpl2(glueVarY);
	private OWLPropertyExpressionVisitorImpl propertyVisitorXY = new OWLPropertyExpressionVisitorImpl(glueVarX, glueVarY);
	private OWLPropertyExpressionVisitorImpl propertyVisitorYX = new OWLPropertyExpressionVisitorImpl(glueVarY, glueVarX);
	private OWLPropertyExpressionVisitorImpl propertyVisitorXZ = new OWLPropertyExpressionVisitorImpl(glueVarX, glueVarZ);
	private static DefaultFreeVarGen freeVarGen = new DefaultFreeVarGen("X");	
	
	
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
		LogicalFormula superClass = arg.getSuperClass().accept(classVisitorX);
		LogicalFormula subClass = arg.getSubClass().accept(classVisitorX);

		subClass.not();
		subClass.or(superClass);

		return LogicalFormulaRuleTranslator.getInstance().translate(subClass);
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
	public Iterable<? extends Object> visit(OWLDisjointClassesAxiom arg) {
		LogicalFormula c = new LogicalFormula();
		
		for(OWLClassExpression e : arg.getClassExpressions()) {
			c.and(e.accept(classVisitorX));
		}
		
		return LogicalFormulaRuleTranslator.getInstance().translate(c);
	}

	@Override
	public Iterable<? extends Object> visit(OWLDataPropertyDomainAxiom arg) {
		LogicalFormula property = arg.getProperty().accept(propertyVisitorXY);
		LogicalFormula domain = arg.getDomain().accept(classVisitorX);

		property.not();
		property.or(domain);

		return LogicalFormulaRuleTranslator.getInstance().translate(property);
	}

	@Override
	public Iterable<? extends Object> visit(OWLObjectPropertyDomainAxiom arg) {
		LogicalFormula property = arg.getProperty().accept(propertyVisitorXY);
		LogicalFormula domain = arg.getDomain().accept(classVisitorX);

		property.not();
		property.or(domain);

		return LogicalFormulaRuleTranslator.getInstance().translate(property);
	}

	@Override
	public Iterable<? extends Object> visit(OWLEquivalentObjectPropertiesAxiom arg) {
		Collection<Rule> c = this.<Rule>createCollection();
		LogicalFormula f1, f2, f1_save;
		LinkedList<OWLObjectPropertyExpression> properties = new LinkedList<OWLObjectPropertyExpression>(arg.getProperties());
		
		Iterator<OWLObjectPropertyExpression> it1, it2;
		it1 = properties.iterator();
		while(it1.hasNext()) {
			OWLObjectPropertyExpression propExpr = it1.next();
			it1.remove();
			
			it2 = properties.iterator();
			while(it2.hasNext()) {
				f1 = propExpr.accept(propertyVisitorXY);
				f1_save = new LogicalFormula(f1);
				OWLObjectPropertyExpression next = it2.next();
				f2 = next.accept(propertyVisitorXY);
				
				// 1 -> 2
				f1.not();
				f1.or(f2);
				
				for(Rule r : LogicalFormulaRuleTranslator.getInstance().translate(f1)) {
					c.add(r);
				}
				
				// 2 -> 1
				f2.not();
				f2.or(f1_save);
				
				for(Rule r : LogicalFormulaRuleTranslator.getInstance().translate(f2)) {
					c.add(r);
				}
				
			}
		}
		return c;
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
		LogicalFormula property = arg.getProperty().accept(propertyVisitorXY);
		LogicalFormula range = arg.getRange().accept(classVisitorY);

		property.not();
		property.or(range);

		return LogicalFormulaRuleTranslator.getInstance().translate(property);
	}

	@Override
	public Iterable<? extends Object> visit(OWLObjectPropertyAssertionAxiom arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public Iterable<? extends Object> visit(OWLFunctionalObjectPropertyAxiom arg) {
		// =(Y, Z) :- p(X, Y), p(X, Z).

		LogicalFormula f = arg.getProperty().accept(propertyVisitorXY);
		f.and(arg.getProperty().accept(propertyVisitorXZ));
		
		f.not();
		f.or(new Literal(new DefaultAtom(equalityPredicate, glueVarX, glueVarY), true));
		
		return LogicalFormulaRuleTranslator.getInstance().translate(f);
	}

	@Override
	public Iterable<? extends Object> visit(OWLSubObjectPropertyOfAxiom arg) {
		LogicalFormula subProperty = arg.getSubProperty().accept(propertyVisitorXY);
		LogicalFormula superProperty = arg.getSuperProperty().accept(propertyVisitorXY);
		
		subProperty.not();
		subProperty.or(superProperty);
		
		return LogicalFormulaRuleTranslator.getInstance().translate(subProperty);
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
		LogicalFormula f1, f2, f1_save;
		List<OWLClassExpression> classes = new LinkedList<OWLClassExpression>(arg.getClassExpressionsAsList());
		
		Iterator<OWLClassExpression> it1, it2;
		it1 = classes.iterator();
		while(it1.hasNext()) {
			OWLClassExpression classExpr = it1.next();
			it1.remove();
			
			it2 = classes.iterator();
			while(it2.hasNext()) {
				f1 = classExpr.accept(classVisitorX);
				f1_save = new LogicalFormula(f1);
				OWLClassExpression next = it2.next();
				f2 = next.accept(classVisitorX);
				
				// 1 -> 2
				f1.not();
				f1.or(f2);
				
				for(Rule r : LogicalFormulaRuleTranslator.getInstance().translate(f1)) {
					c.add(r);
				}
				
				// 2 -> 1
				f2.not();
				f2.or(f1_save);
				
				for(Rule r : LogicalFormulaRuleTranslator.getInstance().translate(f2)) {
					c.add(r);
				}
				
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
	public Iterable<? extends Object> visit(OWLInverseObjectPropertiesAxiom arg) {
		Collection<Rule> rules = new LinkedList<Rule>();
		LogicalFormula p1, p2, p1_bis;
		Iterator<OWLObjectPropertyExpression> it = arg.getProperties().iterator();
		p1 = it.next().accept(propertyVisitorXY);
		p1_bis = new LogicalFormula(p1);
		p2 = it.next().accept(propertyVisitorYX);
			
		p1.not();
		p1.or(p2);
		
		for(Rule r : LogicalFormulaRuleTranslator.getInstance().translate(p1)) {
			rules.add(r);
		}
		
		p2.not();
		p2.or(p1_bis);
		
		for(Rule r : LogicalFormulaRuleTranslator.getInstance().translate(p2)) {
			rules.add(r);
		}

		return rules;
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
