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
import org.semanticweb.owlapi.model.OWLNamedIndividual;
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
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.SymbolGenerator;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.core.term.Term;
import fr.lirmm.graphik.graal.io.owl.logic.Literal;
import fr.lirmm.graphik.graal.io.owl.logic.LogicalFormula;
import fr.lirmm.graphik.graal.io.owl.logic.LogicalFormulaTranslator;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
class OWLAxiomParser implements
		OWLAxiomVisitorEx<Iterable<? extends Object>> {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(OWLAxiomParser.class);

	private static SpecificFreeVarGen freeVarGen = new SpecificFreeVarGen();

	private Term glueVarX;
	private Term glueVarY;
	private Term glueVarZ;

	private ShortFormProvider prefixManager;
	private Predicate equalityPredicate;
	private OWLClassExpressionVisitorImpl classVisitorX;
	private OWLClassExpressionVisitorImpl classVisitorY;
	private OWLPropertyExpressionVisitorImpl propertyVisiotrXX;
	private OWLPropertyExpressionVisitorImpl propertyVisitorXY;
	private OWLPropertyExpressionVisitorImpl propertyVisitorYX;
	private OWLPropertyExpressionVisitorImpl propertyVisitorXZ;
	private OWLPropertyExpressionVisitorImpl propertyVisitorYZ;

	public OWLAxiomParser(ShortFormProvider prefixManager) {
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
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Visit OWLDeclarationAxiom: " + arg);
		}
		return Collections.emptyList();
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

		return LogicalFormulaTranslator.getInstance().translate(subClass);
	}

	@Override
	public Iterable<? extends Object> visit(OWLEquivalentClassesAxiom arg) {
		freeVarGen.setIndex(1);
		Collection<Object> c = this.<Object> createCollection();
		LogicalFormula f1, f2, f1Save;
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
				f1Save = new LogicalFormula(f1);
				OWLClassExpression next = it2.next();
				f2 = next.accept(classVisitorX);

				// 1 -> 2
				f1.not();
				f1.or(f2);

				for (Object r : LogicalFormulaTranslator.getInstance()
						.translate(f1)) {
					c.add(r);
				}

				// 2 -> 1
				f2.not();
				f2.or(f1Save);

				for (Object r : LogicalFormulaTranslator.getInstance()
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

		return LogicalFormulaTranslator.getInstance().translate(c);
	}

	@Override
	public Iterable<? extends Object> visit(OWLDisjointUnionAxiom arg) {
		if (LOGGER.isWarnEnabled()) {
			LOGGER.warn("OWLDisjointUnionAxion is not supported. This axioms was skipped : "
					+ arg);
		}
		return Collections.emptyList();
	}

	// /////////////////////////////////////////////////////////////////////////
	// ObjectPropertyAxiom
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public Iterable<? extends Object> visit(OWLObjectPropertyDomainAxiom arg) {
		return this.propertyDomainAxiom(arg.getProperty(), arg.getDomain());
	}

	@Override
	public Iterable<? extends Object> visit(OWLObjectPropertyRangeAxiom arg) {
		freeVarGen.setIndex(2);
		LogicalFormula property = arg.getProperty().accept(propertyVisitorXY);
		LogicalFormula range = arg.getRange().accept(classVisitorY);

		property.not();
		property.or(range);

		return LogicalFormulaTranslator.getInstance().translate(property);
	}

	@Override
	public Iterable<? extends Object> visit(OWLAsymmetricObjectPropertyAxiom arg) {
		LogicalFormula f = arg.getProperty().accept(propertyVisitorXY);
		f.and(arg.getProperty().accept(propertyVisitorYX));

		f.not();
		return LogicalFormulaTranslator.getInstance().translate(f);
	}

	@Override
	public Iterable<? extends Object> visit(OWLReflexiveObjectPropertyAxiom arg) {
		if (LOGGER.isWarnEnabled()) {
			LOGGER.warn("OWLReflexiveObjectPropertyAxiom is not implemented. This axioms was skipped : "
					+ arg);
		}
		return Collections.emptyList();
	}

	@Override
	public Iterable<? extends Object> visit(
			OWLEquivalentObjectPropertiesAxiom arg) {
		List<OWLPropertyExpression> properties = new LinkedList<OWLPropertyExpression>(
				arg.getProperties());
		return this.equivalentPropertiesAxiom(properties);
	}

	@Override
	public Iterable<? extends Object> visit(OWLTransitiveObjectPropertyAxiom arg) {
		LogicalFormula f = arg.getProperty().accept(propertyVisitorXY);
		f.and(arg.getProperty().accept(propertyVisitorYZ));
		f.not();
		f.or(arg.getProperty().accept(propertyVisitorXZ));

		return LogicalFormulaTranslator.getInstance().translate(f);
	}

	@Override
	public Iterable<? extends Object> visit(
			OWLDisjointObjectPropertiesAxiom arg) {
		List<OWLPropertyExpression> properties = new LinkedList<OWLPropertyExpression>(
				arg.getProperties());
		return this.disjointPropertiesAxiom(properties);
	}

	@Override
	public Iterable<? extends Object> visit(OWLSymmetricObjectPropertyAxiom arg) {
		LogicalFormula f = arg.getProperty().accept(propertyVisitorXY);
		LogicalFormula tmp = arg.getProperty().accept(propertyVisitorYX);

		f.not();
		f.or(tmp);

		return LogicalFormulaTranslator.getInstance().translate(f);
	}

	@Override
	public Iterable<? extends Object> visit(OWLFunctionalObjectPropertyAxiom arg) {
		return this.functionalPropertyAxiom(arg.getProperty());
	}

	@Override
	public Iterable<? extends Object> visit(OWLSubObjectPropertyOfAxiom arg) {
		LogicalFormula subProperty = arg.getSubProperty().accept(
				propertyVisitorXY);
		LogicalFormula superProperty = arg.getSuperProperty().accept(
				propertyVisitorXY);

		subProperty.not();
		subProperty.or(superProperty);

		return LogicalFormulaTranslator.getInstance()
				.translate(subProperty);
	}

	@Override
	public Iterable<? extends Object> visit(
			OWLIrreflexiveObjectPropertyAxiom arg) {
		LogicalFormula f = arg.getProperty().accept(propertyVisiotrXX);
		f.not();

		return LogicalFormulaTranslator.getInstance().translate(f);
	}

	@Override
	public Iterable<? extends Object> visit(
			OWLInverseFunctionalObjectPropertyAxiom arg) {
		LogicalFormula f = arg.getProperty().accept(propertyVisitorXZ);
		f.and(arg.getProperty().accept(propertyVisitorYZ));
		f.not();

		f.or(new Literal(
				new DefaultAtom(equalityPredicate, glueVarX, glueVarY), true));

		return LogicalFormulaTranslator.getInstance().translate(f);
	}

	@Override
	public Iterable<? extends Object> visit(OWLInverseObjectPropertiesAxiom arg) {
		Collection<Object> rules = new LinkedList<Object>();
		LogicalFormula p1, p2, p1Save;
		Iterator<OWLObjectPropertyExpression> it = arg.getProperties()
				.iterator();
		p1 = it.next().accept(propertyVisitorXY);
		p1Save = new LogicalFormula(p1);
		p2 = it.next().accept(propertyVisitorYX);

		p1.not();
		p1.or(p2);

		for (Object r : LogicalFormulaTranslator.getInstance().translate(p1)) {
			rules.add(r);
		}

		p2.not();
		p2.or(p1Save);

		for (Object r : LogicalFormulaTranslator.getInstance().translate(p2)) {
			rules.add(r);
		}

		return rules;
	}

	// /////////////////////////////////////////////////////////////////////////
	// DataPropertyAxiom
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public Iterable<? extends Object> visit(OWLSubDataPropertyOfAxiom arg) {
		LogicalFormula subProperty = arg.getSubProperty().accept(
				propertyVisitorXY);
		LogicalFormula superProperty = arg.getSuperProperty().accept(
				propertyVisitorXY);

		subProperty.not();
		subProperty.or(superProperty);

		return LogicalFormulaTranslator.getInstance()
				.translate(subProperty);
	}
	
	@Override
	public Iterable<? extends Object> visit(OWLDataPropertyDomainAxiom arg) {
		return this.propertyDomainAxiom(arg.getProperty(), arg.getDomain());
	}

	@Override
	public Iterable<? extends Object> visit(OWLDataPropertyRangeAxiom arg) {
		if (LOGGER.isWarnEnabled()) {
			LOGGER.warn("OWLDataPropertyRangeAxiom is not supported. This axioms was skipped : "
					+ arg);
		}
		return Collections.emptyList();
	}

	@Override
	public Iterable<? extends Object> visit(OWLFunctionalDataPropertyAxiom arg) {
		return this.functionalPropertyAxiom(arg.getProperty());
	}

	@Override
	public Iterable<? extends Object> visit(OWLEquivalentDataPropertiesAxiom arg) {
		List<OWLPropertyExpression> properties = new LinkedList<OWLPropertyExpression>(
				arg.getProperties());
		return this.equivalentPropertiesAxiom(properties);
	}

	@Override
	public Iterable<? extends Object> visit(OWLDisjointDataPropertiesAxiom arg) {
		List<OWLPropertyExpression> properties = new LinkedList<OWLPropertyExpression>(
				arg.getProperties());
		return this.disjointPropertiesAxiom(properties);
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// PropertyChain
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public Iterable<? extends Object> visit(OWLSubPropertyChainOfAxiom arg) {
		LogicalFormula f = new LogicalFormula();
		Term varX, varY, firstVarInChain;
		firstVarInChain = varX = freeVarGen.getFreeVar();
		for(OWLPropertyExpression pe : arg.getPropertyChain()) {
			varY = freeVarGen.getFreeVar();
			f.and(pe.accept(new OWLPropertyExpressionVisitorImpl(this.prefixManager, varX, varY)));
			varX = varY;
		}
		
		f.not();
		f.or(arg.getSuperProperty().accept(new OWLPropertyExpressionVisitorImpl(this.prefixManager, firstVarInChain, varX)));
		
		return LogicalFormulaTranslator.getInstance().translate(f);
	}

	// /////////////////////////////////////////////////////////////////////////
	// DatatypeDefinition
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public Iterable<? extends Object> visit(OWLDatatypeDefinitionAxiom arg) {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Visit OWLDatatypeDefinitionAxiom is not implemented: " + arg);
		}
		return Collections.emptyList();
	}

	// /////////////////////////////////////////////////////////////////////////
	// HasKey
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public Iterable<? extends Object> visit(OWLHasKeyAxiom arg) {
		// =(Y, Z) :- C(Y), C(Z), p1(Y, X1), p1(Z, X1), ..., pn(Y, Xn), pn(Z, Xn).
		
		freeVarGen.setIndex(2);
		LogicalFormula f = arg.getClassExpression().accept(classVisitorX);
		f.and(arg.getClassExpression().accept(classVisitorY));
		
		for(OWLPropertyExpression pe : arg.getPropertyExpressions()) {
			Term var = freeVarGen.getFreeVar();
			f.and(pe.accept(new OWLPropertyExpressionVisitorImpl(this.prefixManager, glueVarX, var)));
			f.and(pe.accept(new OWLPropertyExpressionVisitorImpl(this.prefixManager, glueVarY, var)));
		}
		
		f.not();
		f.or(new LogicalFormula(new Literal(new DefaultAtom(equalityPredicate, glueVarX, glueVarY), true)));
		
		return LogicalFormulaTranslator.getInstance().translate(f);
	}

	// /////////////////////////////////////////////////////////////////////////
	// Assertion
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public Iterable<? extends Object> visit(OWLClassAssertionAxiom arg) {
		freeVarGen.setIndex(0);
		Term i = createConstant(arg.getIndividual().asOWLNamedIndividual());
		LogicalFormula f = arg.getClassExpression().accept(
				new OWLClassExpressionVisitorImpl(this.prefixManager,
						freeVarGen, i));
		return LogicalFormulaTranslator.getInstance().translate(f);
	}

	@Override
	public Iterable<? extends Object> visit(OWLObjectPropertyAssertionAxiom arg) {
		freeVarGen.setIndex(0);
		Term a = createConstant(arg.getSubject().asOWLNamedIndividual());
		Term b = createConstant(arg.getObject().asOWLNamedIndividual());
		LogicalFormula f = arg.getProperty().accept(
				new OWLPropertyExpressionVisitorImpl(this.prefixManager, a, b));
		return Collections.singleton(f.iterator().next().iterator().next());
	}

	@Override
	public Iterable<? extends Object> visit(
			OWLNegativeObjectPropertyAssertionAxiom arg) {
		if (LOGGER.isWarnEnabled()) {
			LOGGER.warn("OWLNegativeObjectPropertyAssertionAxiom is not supported. This axioms was skipped : "
					+ arg);
		}
		return Collections.emptyList();
	}

	@Override
	public Iterable<? extends Object> visit(OWLDataPropertyAssertionAxiom arg) {
		freeVarGen.setIndex(0);
		Term a = createConstant(arg.getSubject().asOWLNamedIndividual());
		Term b = DefaultTermFactory.instance().createLiteral(
				arg.getObject().getLiteral());
		LogicalFormula f = arg.getProperty().accept(
				new OWLPropertyExpressionVisitorImpl(this.prefixManager, a, b));
		return Collections.singleton(f.iterator().next().iterator().next());
	}

	@Override
	public Iterable<? extends Object> visit(
			OWLNegativeDataPropertyAssertionAxiom arg) {
		if (LOGGER.isWarnEnabled()) {
			LOGGER.warn("OWLNegativeDataPropertyAssertionAxiom is not supported. This axioms was skipped : "
					+ arg);
		}
		return Collections.emptyList();
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
			
			Term t1 = createConstant(individu1.asOWLNamedIndividual());
			
			it2 = list.iterator();
			while (it2.hasNext()) {
				OWLIndividual individu2 = it2.next();

				Term t2 = createConstant(individu2.asOWLNamedIndividual());
				Atom a = new DefaultAtom(equalityPredicate, t1, t2);
				c.add(a);
			}
		}
		return c;
	}
	
	@Override
	public Iterable<? extends Object> visit(OWLDifferentIndividualsAxiom arg) {
		if (LOGGER.isWarnEnabled()) {
			LOGGER.warn("OWLDifferentIndividualsAxiom is not supported. This axioms was skipped : "
					+ arg);
		}
		return Collections.emptyList();
	}

	// /////////////////////////////////////////////////////////////////////////
	// SWRLRules
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public Iterable<? extends Object> visit(SWRLRule arg) {
		if (LOGGER.isWarnEnabled()) {
			LOGGER.warn("Visit SWRLRule is not implemented: " + arg);
		}
		return Collections.emptyList();
	}

	// /////////////////////////////////////////////////////////////////////////
	// AnnotationAxiom
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public Iterable<? extends Object> visit(OWLAnnotationAssertionAxiom arg) {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Visit OWLAnnotationAssertionAxiom is not implemented: " + arg);
		}
		return Collections.emptyList();
	}

	@Override
	public Iterable<? extends Object> visit(OWLSubAnnotationPropertyOfAxiom arg) {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Visit OWLSubAnnotationPropertyOfAxiom is not implemented: " + arg);
		}
		return Collections.emptyList();
	}

	@Override
	public Iterable<? extends Object> visit(OWLAnnotationPropertyDomainAxiom arg) {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Visit OWLAnnotationPropertyDomainAxiom is not implemented: " + arg);
		}
		return Collections.emptyList();
	}

	@Override
	public Iterable<? extends Object> visit(OWLAnnotationPropertyRangeAxiom arg) {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Visit OWLAnnotationPropertyRangeAxiom is not implemented: " + arg);
		}
		return Collections.emptyList();
	}

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	private Term createConstant(OWLNamedIndividual individu) {
		String uri = this.prefixManager.getShortForm(individu);
		if(uri.charAt(0) == '<') {
			uri = uri.substring(1, uri.length()-1);
		}
		return DefaultTermFactory.instance().createConstant(uri);
	}

	private Iterable<? extends Object> propertyDomainAxiom(
			OWLPropertyExpression property, OWLClassExpression domain) {
		freeVarGen.setIndex(2);
		LogicalFormula p = property.accept(propertyVisitorXY);
		LogicalFormula d = domain.accept(classVisitorX);

		p.not();
		p.or(d);

		return LogicalFormulaTranslator.getInstance().translate(p);
	}

	private Iterable<? extends Object> functionalPropertyAxiom(
			OWLPropertyExpression property) {
		LogicalFormula f = property.accept(propertyVisitorXY);
		f.and(property.accept(propertyVisitorXZ));

		f.not();
		f.or(new Literal(
				new DefaultAtom(equalityPredicate, glueVarX, glueVarY), true));

		return LogicalFormulaTranslator.getInstance().translate(f);
	}

	private Iterable<? extends Object> equivalentPropertiesAxiom(
			List<OWLPropertyExpression> properties) {
		Collection<Object> c = this.<Object> createCollection();
		LogicalFormula f1, f2, f1Save;

		Iterator<OWLPropertyExpression> it1, it2;
		it1 = properties.iterator();
		while (it1.hasNext()) {
			OWLPropertyExpression propExpr = it1.next();
			it1.remove();

			it2 = properties.iterator();
			while (it2.hasNext()) {
				f1 = propExpr.accept(propertyVisitorXY);
				f1Save = new LogicalFormula(f1);
				OWLPropertyExpression next = it2.next();
				f2 = next.accept(propertyVisitorXY);

				// 1 -> 2
				f1.not();
				f1.or(f2);

				for (Object r : LogicalFormulaTranslator.getInstance()
						.translate(f1)) {
					c.add(r);
				}

				// 2 -> 1
				f2.not();
				f2.or(f1Save);

				for (Object r : LogicalFormulaTranslator.getInstance()
						.translate(f2)) {
					c.add(r);
				}

			}
		}
		return c;
	}
	
	private Iterable<? extends Object> disjointPropertiesAxiom(
			List<OWLPropertyExpression> properties) {
		Collection<Object> c = this.<Object> createCollection();
		LogicalFormula f1, f2;

		Iterator<OWLPropertyExpression> it1, it2;
		it1 = properties.iterator();
		while (it1.hasNext()) {
			OWLPropertyExpression propExpr = it1.next();
			f1 = propExpr.accept(propertyVisitorXY);
			it1.remove();

			it2 = properties.iterator();
			while (it2.hasNext()) {
				OWLPropertyExpression next = it2.next();
				f2 = next.accept(propertyVisitorXY);

				// ! :- f1 ^ f2 
				f2.and(f1);
				f2.not();

				for (Object r : LogicalFormulaTranslator.getInstance()
						.translate(f2)) {
					c.add(r);
				}
			}
		}
		return c;
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
			return DefaultTermFactory.instance().createVariable("X" + index++);
		}

		public void setIndex(int index) {
			this.index = index;
		}
	};
}
