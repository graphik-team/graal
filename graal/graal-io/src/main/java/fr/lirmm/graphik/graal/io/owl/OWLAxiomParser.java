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
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLPropertyDomainAxiom;
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
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.SymbolGenerator;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.Term.Type;
import fr.lirmm.graphik.graal.io.owl.logic.Literal;
import fr.lirmm.graphik.graal.io.owl.logic.LogicalFormula;
import fr.lirmm.graphik.graal.io.owl.logic.LogicalFormulaRuleTranslator;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class OWLAxiomParser implements
		OWLAxiomVisitorEx<Iterable<? extends Object>> {

	private static final Logger logger = LoggerFactory
			.getLogger(OWLAxiomParser.class);

	private static SpecificFreeVarGen freeVarGen = new SpecificFreeVarGen();

	private Term glueVarX;
	private Term glueVarY;
	private Term glueVarZ;

	private DefaultPrefixManager prefixManager;
	private Predicate equalityPredicate;
	private OWLClassExpressionVisitorImpl classVisitorX;
	private OWLClassExpressionVisitorImpl classVisitorY;
	private OWLPropertyExpressionVisitorImpl propertyVisiotrXX;
	private OWLPropertyExpressionVisitorImpl propertyVisitorXY;
	private OWLPropertyExpressionVisitorImpl propertyVisitorYX;
	private OWLPropertyExpressionVisitorImpl propertyVisitorXZ;
	private OWLPropertyExpressionVisitorImpl propertyVisitorYZ;

	

	public OWLAxiomParser(DefaultPrefixManager prefixManager) {
		this.prefixManager = prefixManager;
		this.glueVarX = freeVarGen.getFreeVar();
		this.glueVarY = freeVarGen.getFreeVar();
		this.glueVarZ = freeVarGen.getFreeVar();

		this.equalityPredicate = new Predicate("=", 2);
		this.classVisitorX = new OWLClassExpressionVisitorImpl(
				this.prefixManager, freeVarGen, glueVarX);
		this.classVisitorY = new OWLClassExpressionVisitorImpl(
				this.prefixManager, freeVarGen, glueVarY);
		this.propertyVisiotrXX = new OWLPropertyExpressionVisitorImpl(
				this.prefixManager, glueVarX, glueVarX);
		this.propertyVisitorXY = new OWLPropertyExpressionVisitorImpl(
				this.prefixManager, glueVarX, glueVarY);
		this.propertyVisitorYX = new OWLPropertyExpressionVisitorImpl(
				this.prefixManager, glueVarY, glueVarX);
		this.propertyVisitorXZ = new OWLPropertyExpressionVisitorImpl(
				this.prefixManager, glueVarX, glueVarZ);
		this.propertyVisitorYZ = new OWLPropertyExpressionVisitorImpl(
				this.prefixManager, glueVarY, glueVarZ);
	}


	// /////////////////////////////////////////////////////////////////////////
	// Declaration
	// /////////////////////////////////////////////////////////////////////////
	
	@Override
	public Iterable<? extends Object> visit(OWLDeclarationAxiom arg) {
		if (logger.isInfoEnabled()) {
			logger.info("Visit OWLDeclarationAxiom: " + arg);
		}
		return null;
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// ClassAxiom
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public Iterable<? extends Object> visit(OWLSubClassOfAxiom arg) {
		freeVarGen.setIndex(1);
		LogicalFormula superClass = arg.getSuperClass().accept(classVisitorX);
		LogicalFormula subClass = arg.getSubClass().accept(classVisitorX);

		subClass.not();
		subClass.or(superClass);

		return LogicalFormulaRuleTranslator.getInstance().translate(subClass);
	}

	@Override
	public Iterable<? extends Object> visit(OWLEquivalentClassesAxiom arg) {
		freeVarGen.setIndex(1);
		Collection<Rule> c = this.<Rule> createCollection();
		LogicalFormula f1, f2, f1_save;
		List<OWLClassExpression> classes = new LinkedList<OWLClassExpression>(
				arg.getClassExpressionsAsList());

		Iterator<OWLClassExpression> it1, it2;
		it1 = classes.iterator();
		while (it1.hasNext()) {
			OWLClassExpression classExpr = it1.next();
			it1.remove();

			it2 = classes.iterator();
			while (it2.hasNext()) {
				f1 = classExpr.accept(classVisitorX);
				f1_save = new LogicalFormula(f1);
				OWLClassExpression next = it2.next();
				f2 = next.accept(classVisitorX);

				// 1 -> 2
				f1.not();
				f1.or(f2);

				for (Rule r : LogicalFormulaRuleTranslator.getInstance()
						.translate(f1)) {
					c.add(r);
				}

				// 2 -> 1
				f2.not();
				f2.or(f1_save);

				for (Rule r : LogicalFormulaRuleTranslator.getInstance()
						.translate(f2)) {
					c.add(r);
				}

			}
		}
		return c;
	}
	
	@Override
	public Iterable<? extends Object> visit(OWLDisjointClassesAxiom arg) {
		freeVarGen.setIndex(1);
		LogicalFormula c, tmp;

		List<LogicalFormula> list = new LinkedList<LogicalFormula>();
		for (OWLClassExpression e : arg.getClassExpressions()) {
			tmp = e.accept(classVisitorX);
			tmp.not();
			list.add(tmp);
		}

		c = new LogicalFormula();
		Iterator<LogicalFormula> it1, it2;
		it1 = list.iterator();
		LogicalFormula next;
		while (it1.hasNext()) {
			next = it1.next();
			it1.remove();

			it2 = list.iterator();
			while (it2.hasNext()) {
				tmp = new LogicalFormula(next);
				tmp.or(it2.next());
				c.and(tmp);
			}
		}

		return LogicalFormulaRuleTranslator.getInstance().translate(c);
	}
	
	@Override
	public Iterable<? extends Object> visit(OWLDisjointUnionAxiom arg) {
		if (logger.isWarnEnabled()) {
			logger.warn("OWLDisjointUnionAxion is not enable. This axioms was skipped : "
					+ arg);
		}
		return Collections.emptyList();
	}

	// /////////////////////////////////////////////////////////////////////////
	// ObjectPropertyAxiom
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public Iterable<? extends Object> visit(OWLObjectPropertyDomainAxiom arg) {
		return this.visit((OWLPropertyDomainAxiom<?>) arg);
	}
	
	@Override
	public Iterable<? extends Object> visit(OWLObjectPropertyRangeAxiom arg) {
		freeVarGen.setIndex(2);
		LogicalFormula property = arg.getProperty().accept(propertyVisitorXY);
		LogicalFormula range = arg.getRange().accept(classVisitorY);

		property.not();
		property.or(range);

		return LogicalFormulaRuleTranslator.getInstance().translate(property);
	}
	
	@Override
	public Iterable<? extends Object> visit(OWLAsymmetricObjectPropertyAxiom arg) {
		LogicalFormula f = arg.getProperty().accept(propertyVisitorXY);
		f.and(arg.getProperty().accept(propertyVisitorYX));

		f.not();
		return LogicalFormulaRuleTranslator.getInstance().translate(f);
	}

	@Override
	public Iterable<? extends Object> visit(OWLReflexiveObjectPropertyAxiom arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public Iterable<? extends Object> visit(
			OWLEquivalentObjectPropertiesAxiom arg) {
		Collection<Rule> c = this.<Rule> createCollection();
		LogicalFormula f1, f2, f1_save;
		LinkedList<OWLObjectPropertyExpression> properties = new LinkedList<OWLObjectPropertyExpression>(
				arg.getProperties());

		Iterator<OWLObjectPropertyExpression> it1, it2;
		it1 = properties.iterator();
		while (it1.hasNext()) {
			OWLObjectPropertyExpression propExpr = it1.next();
			it1.remove();

			it2 = properties.iterator();
			while (it2.hasNext()) {
				f1 = propExpr.accept(propertyVisitorXY);
				f1_save = new LogicalFormula(f1);
				OWLObjectPropertyExpression next = it2.next();
				f2 = next.accept(propertyVisitorXY);

				// 1 -> 2
				f1.not();
				f1.or(f2);

				for (Rule r : LogicalFormulaRuleTranslator.getInstance()
						.translate(f1)) {
					c.add(r);
				}

				// 2 -> 1
				f2.not();
				f2.or(f1_save);

				for (Rule r : LogicalFormulaRuleTranslator.getInstance()
						.translate(f2)) {
					c.add(r);
				}

			}
		}
		return c;
	}

	@Override
	public Iterable<? extends Object> visit(OWLTransitiveObjectPropertyAxiom arg) {
		LogicalFormula f = arg.getProperty().accept(propertyVisitorXY);
		f.and(arg.getProperty().accept(propertyVisitorYZ));
		f.not();
		f.or(arg.getProperty().accept(propertyVisitorXZ));

		return LogicalFormulaRuleTranslator.getInstance().translate(f);
	}

	@Override
	public Iterable<? extends Object> visit(
			OWLDisjointObjectPropertiesAxiom arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public Iterable<? extends Object> visit(OWLSymmetricObjectPropertyAxiom arg) {
		LogicalFormula f = arg.getProperty().accept(propertyVisitorXY);
		LogicalFormula tmp = arg.getProperty().accept(propertyVisitorYX);

		f.not();
		f.or(tmp);

		return LogicalFormulaRuleTranslator.getInstance().translate(f);
	}

	@Override
	public Iterable<? extends Object> visit(OWLFunctionalObjectPropertyAxiom arg) {
		// =(Y, Z) :- p(X, Y), p(X, Z).

		LogicalFormula f = arg.getProperty().accept(propertyVisitorXY);
		f.and(arg.getProperty().accept(propertyVisitorXZ));

		f.not();
		f.or(new Literal(
				new DefaultAtom(equalityPredicate, glueVarX, glueVarY), true));

		return LogicalFormulaRuleTranslator.getInstance().translate(f);
	}

	@Override
	public Iterable<? extends Object> visit(OWLSubObjectPropertyOfAxiom arg) {
		LogicalFormula subProperty = arg.getSubProperty().accept(
				propertyVisitorXY);
		LogicalFormula superProperty = arg.getSuperProperty().accept(
				propertyVisitorXY);

		subProperty.not();
		subProperty.or(superProperty);

		return LogicalFormulaRuleTranslator.getInstance()
				.translate(subProperty);
	}

	@Override
	public Iterable<? extends Object> visit(
			OWLIrreflexiveObjectPropertyAxiom arg) {
		LogicalFormula f = arg.getProperty().accept(propertyVisiotrXX);
		f.not();

		return LogicalFormulaRuleTranslator.getInstance().translate(f);
	}

	@Override
	public Iterable<? extends Object> visit(
			OWLInverseFunctionalObjectPropertyAxiom arg) {
		LogicalFormula f = arg.getProperty().accept(propertyVisitorXZ);
		f.and(arg.getProperty().accept(propertyVisitorYZ));
		f.not();

		f.or(new Literal(
				new DefaultAtom(equalityPredicate, glueVarX, glueVarY), true));

		return LogicalFormulaRuleTranslator.getInstance().translate(f);
	}

	@Override
	public Iterable<? extends Object> visit(OWLInverseObjectPropertiesAxiom arg) {
		Collection<Rule> rules = new LinkedList<Rule>();
		LogicalFormula p1, p2, p1_bis;
		Iterator<OWLObjectPropertyExpression> it = arg.getProperties()
				.iterator();
		p1 = it.next().accept(propertyVisitorXY);
		p1_bis = new LogicalFormula(p1);
		p2 = it.next().accept(propertyVisitorYX);

		p1.not();
		p1.or(p2);

		for (Rule r : LogicalFormulaRuleTranslator.getInstance().translate(p1)) {
			rules.add(r);
		}

		p2.not();
		p2.or(p1_bis);

		for (Rule r : LogicalFormulaRuleTranslator.getInstance().translate(p2)) {
			rules.add(r);
		}

		return rules;
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// PropertyChain
	// /////////////////////////////////////////////////////////////////////////
	
	@Override
	public Iterable<? extends Object> visit(OWLSubPropertyChainOfAxiom arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	// /////////////////////////////////////////////////////////////////////////
	// DataPropertyAxiom
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public Iterable<? extends Object> visit(OWLDataPropertyDomainAxiom arg) {
		return this.visit((OWLPropertyDomainAxiom<?>) arg);
	}
	
	@Override
	public Iterable<? extends Object> visit(OWLFunctionalDataPropertyAxiom arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public Iterable<? extends Object> visit(
			OWLEquivalentDataPropertiesAxiom arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public Iterable<? extends Object> visit(OWLDataPropertyAssertionAxiom arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public Iterable<? extends Object> visit(OWLDisjointDataPropertiesAxiom arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public Iterable<? extends Object> visit(OWLDataPropertyRangeAxiom arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public Iterable<? extends Object> visit(OWLSubDataPropertyOfAxiom arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	// /////////////////////////////////////////////////////////////////////////
	// DatatypeDefinition
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public Iterable<? extends Object> visit(OWLDatatypeDefinitionAxiom arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	// /////////////////////////////////////////////////////////////////////////
	// HasKey
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public Iterable<? extends Object> visit(OWLHasKeyAxiom arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	// /////////////////////////////////////////////////////////////////////////
	// Assertion
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public Iterable<? extends Object> visit(
			OWLNegativeObjectPropertyAssertionAxiom arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public Iterable<? extends Object> visit(
			OWLNegativeDataPropertyAssertionAxiom arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public Iterable<? extends Object> visit(OWLDifferentIndividualsAxiom arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public Iterable<? extends Object> visit(OWLObjectPropertyAssertionAxiom arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public Iterable<? extends Object> visit(OWLClassAssertionAxiom arg) {
		freeVarGen.setIndex(0);
		Term i = new Term(arg.getIndividual().toString(), Type.CONSTANT);
		return arg.getClassExpression().accept(
				new OWLClassExpressionVisitorImpl(this.prefixManager, freeVarGen, i));
	}

	@Override
	public Iterable<? extends Object> visit(OWLSameIndividualAxiom arg) {
		Collection<Atom> c = this.<Atom> createCollection();
		LinkedList<OWLIndividual> list = new LinkedList<OWLIndividual>(
				arg.getIndividualsAsList());

		Iterator<OWLIndividual> it1, it2;
		it1 = list.iterator();
		while (it1.hasNext()) {
			OWLIndividual individu1 = it1.next();
			it1.remove();
			Term t1 = new Term(individu1.asOWLNamedIndividual().getIRI()
					.toString(), Term.Type.CONSTANT);

			it2 = list.iterator();
			while (it2.hasNext()) {
				OWLIndividual individu2 = it2.next();

				Term t2 = new Term(individu2.asOWLNamedIndividual().getIRI()
						.toString(), Term.Type.CONSTANT);
				Atom a = new DefaultAtom(equalityPredicate, t1, t2);
				c.add(a);
			}
		}
		return c;
	}

	// /////////////////////////////////////////////////////////////////////////
	// SWRLRules
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public Iterable<? extends Object> visit(SWRLRule arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	// /////////////////////////////////////////////////////////////////////////
	// AnnotationAxiom
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public Iterable<? extends Object> visit(OWLAnnotationAssertionAxiom arg) {
		if (logger.isInfoEnabled()) {
			logger.info("Visit OWLAnnotationAssertionAxiom: " + arg);
		}
		return null;
	}

	@Override
	public Iterable<? extends Object> visit(OWLSubAnnotationPropertyOfAxiom arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public Iterable<? extends Object> visit(OWLAnnotationPropertyDomainAxiom arg) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public Iterable<? extends Object> visit(OWLAnnotationPropertyRangeAxiom arg0) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

	private Iterable<? extends Object> visit(OWLPropertyDomainAxiom<?> arg) {
		freeVarGen.setIndex(2);
		LogicalFormula property = arg.getProperty().accept(propertyVisitorXY);
		LogicalFormula domain = arg.getDomain().accept(classVisitorX);

		property.not();
		property.or(domain);

		return LogicalFormulaRuleTranslator.getInstance().translate(property);
	}

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE FACTORY
	// /////////////////////////////////////////////////////////////////////////

	private <T> Collection<T> createCollection() {
		return new LinkedList<T>();
	}

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE CLASSES
	// /////////////////////////////////////////////////////////////////////////

	private static class SpecificFreeVarGen implements SymbolGenerator {

		private int index = 0;

		@Override
		public Term getFreeVar() {
			return new Term("X" + index++, Term.Type.VARIABLE);
		}

		public void setIndex(int index) {
			this.index = index;
		}
	};
}
