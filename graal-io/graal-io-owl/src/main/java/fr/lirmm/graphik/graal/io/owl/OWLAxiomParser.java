/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2017)
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
 /**
 * 
 */
package fr.lirmm.graphik.graal.io.owl;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLAxiomVisitorEx;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataIntersectionOf;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDataRange;
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

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.graal.api.core.VariableGenerator;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.DefaultNegativeConstraint;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.factory.DefaultAtomFactory;
import fr.lirmm.graphik.graal.core.factory.DefaultRuleFactory;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.util.MathUtils;
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLDataMaxCardinalityImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLDataMinCardinalityImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLDataSomeValuesFromImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectIntersectionOfImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectMaxCardinalityImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectMinCardinalityImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectOneOfImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectSomeValuesFromImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLSubClassOfAxiomImpl;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
class OWLAxiomParser implements OWLAxiomVisitorEx<Iterable<? extends Object>> {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(OWLAxiomParser.class);

	private SpecificFreeVarGen freeVarGen = new SpecificFreeVarGen();
	private static final OWLDataFactory DF = new OWLDataFactoryImpl();
	private static final OWLClassExpression NOTHING = DF.getOWLNothing();

	private Variable glueVarX;
	private Variable glueVarY;
	private Variable glueVarZ;

	private ShortFormProvider prefixManager;
	private Predicate equalityPredicate;
	private OWLEquivalentClassExpressionVisitorImpl classVisitorX;
	private OWLEquivalentClassExpressionVisitorImpl classVisitorY;
	private OWLEquivalentClassExpressionVisitorImpl classVisitorZ;

	private OWLEquivalentDataRangeVisitorImpl dataRangeVisitorX;
	private OWLEquivalentDataRangeVisitorImpl dataRangeVisitorY;
	private OWLEquivalentDataRangeVisitorImpl dataRangeVisitorZ;

	private OWLPropertyExpressionVisitorImpl propertyVisiotrXX;
	private OWLPropertyExpressionVisitorImpl propertyVisitorXY;
	private OWLPropertyExpressionVisitorImpl propertyVisitorYX;
	private OWLPropertyExpressionVisitorImpl propertyVisitorXZ;
	private OWLPropertyExpressionVisitorImpl propertyVisitorYZ;

	private Collection<OWLAnnotation> emptyAnno = Collections
			.<OWLAnnotation> emptyList();

	public OWLAxiomParser(ShortFormProvider prefixManager) {
		this.prefixManager = prefixManager;
		this.glueVarX = freeVarGen.getFreshSymbol();
		this.glueVarY = freeVarGen.getFreshSymbol();
		this.glueVarZ = freeVarGen.getFreshSymbol();

		this.equalityPredicate = new Predicate("=", 2);
		this.classVisitorX = new OWLEquivalentClassExpressionVisitorImpl(
				this.prefixManager, freeVarGen, glueVarX);
		this.classVisitorY = new OWLEquivalentClassExpressionVisitorImpl(
				this.prefixManager, freeVarGen, glueVarY);
		this.classVisitorZ = new OWLEquivalentClassExpressionVisitorImpl(
				this.prefixManager, freeVarGen, glueVarZ);

		this.dataRangeVisitorX = new OWLEquivalentDataRangeVisitorImpl(glueVarX);
		this.dataRangeVisitorY = new OWLEquivalentDataRangeVisitorImpl(glueVarY);
		this.dataRangeVisitorZ = new OWLEquivalentDataRangeVisitorImpl(glueVarZ);

		this.propertyVisiotrXX = new OWLPropertyExpressionVisitorImpl(glueVarX,
				glueVarX);
		this.propertyVisitorXY = new OWLPropertyExpressionVisitorImpl(glueVarX,
				glueVarY);
		this.propertyVisitorYX = new OWLPropertyExpressionVisitorImpl(glueVarY,
				glueVarX);
		this.propertyVisitorXZ = new OWLPropertyExpressionVisitorImpl(glueVarX,
				glueVarZ);
		this.propertyVisitorYZ = new OWLPropertyExpressionVisitorImpl(glueVarY,
				glueVarZ);
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
		Collection<Object> objects = new LinkedList<Object>();

		freeVarGen.setIndex(3);
		OWLClassExpression superClass = OWLAPIUtils
				.classExpressionDisjunctiveNormalForm(arg.getSuperClass());
		OWLClassExpression subClass = OWLAPIUtils
				.classExpressionDisjunctiveNormalForm(arg.getSubClass());

		if (OWLAPIUtils.isIntersection(superClass)) {
			for (OWLClassExpression c : OWLAPIUtils
					.getObjectIntersectionOperands(superClass)) {
				CollectionUtils.addAll(objects, new OWLSubClassOfAxiomImpl(
						subClass, c, emptyAnno).accept(this));
			}
		} else if (superClass instanceof OWLObjectComplementOf) {
			TreeSet<OWLClassExpression> operands = new TreeSet<>();
			operands.add(subClass);
			operands.add(((OWLObjectComplementOf) superClass).getOperand());
			subClass = new OWLObjectIntersectionOfImpl(operands);
			CollectionUtils.addAll(objects, new OWLSubClassOfAxiomImpl(
					subClass, NOTHING, emptyAnno).accept(this));
		} else if (superClass instanceof OWLObjectAllValuesFrom) {
			OWLObjectAllValuesFrom allValuesFrom = (OWLObjectAllValuesFrom) superClass;
			subClass = new OWLObjectSomeValuesFromImpl(allValuesFrom
					.getProperty().getInverseProperty(), subClass);
			superClass = allValuesFrom.getFiller();
			CollectionUtils.addAll(objects, new OWLSubClassOfAxiomImpl(
					subClass, superClass, emptyAnno).accept(this));
		} else if (superClass instanceof OWLObjectMaxCardinality
				&& ((OWLObjectMaxCardinality) superClass).getCardinality() == 0) {
			TreeSet<OWLClassExpression> operands = new TreeSet<>();
			operands.add(subClass);
			OWLObjectMaxCardinality maxCard = (OWLObjectMaxCardinality) superClass;
			operands.add(new OWLObjectSomeValuesFromImpl(maxCard.getProperty(),
					maxCard.getFiller()));
			subClass = new OWLObjectIntersectionOfImpl(operands);
			CollectionUtils.addAll(objects, new OWLSubClassOfAxiomImpl(
					subClass, NOTHING, emptyAnno).accept(this));
		} else if (superClass instanceof OWLDataMaxCardinality
				&& ((OWLDataMaxCardinality) superClass).getCardinality() == 0) {
			TreeSet<OWLClassExpression> operands = new TreeSet<>();
			operands.add(subClass);
			OWLDataMaxCardinality maxCard = (OWLDataMaxCardinality) superClass;
			operands.add(new OWLDataSomeValuesFromImpl(maxCard.getProperty(),
					maxCard.getFiller()));
			subClass = new OWLObjectIntersectionOfImpl(operands);
			CollectionUtils.addAll(objects, new OWLSubClassOfAxiomImpl(
					subClass, NOTHING, emptyAnno).accept(this));
		} else if (superClass instanceof OWLObjectExactCardinality
				&& ((OWLObjectExactCardinality) superClass).getCardinality() <= 1) {
			OWLObjectExactCardinality exactCard = (OWLObjectExactCardinality) superClass;
			OWLObjectMaxCardinality maxCard = new OWLObjectMaxCardinalityImpl(
					exactCard.getProperty(), exactCard.getCardinality(),
					exactCard.getFiller());
			OWLObjectMinCardinality minCard = new OWLObjectMinCardinalityImpl(
					exactCard.getProperty(), exactCard.getCardinality(),
					exactCard.getFiller());

			CollectionUtils.addAll(objects, new OWLSubClassOfAxiomImpl(
					subClass, maxCard, emptyAnno).accept(this));
			CollectionUtils.addAll(objects, new OWLSubClassOfAxiomImpl(
					subClass, minCard, emptyAnno).accept(this));
		} else if (superClass instanceof OWLDataExactCardinality
				&& ((OWLDataExactCardinality) superClass).getCardinality() <= 1) {
			OWLDataExactCardinality exactCard = (OWLDataExactCardinality) superClass;
			OWLDataMaxCardinality maxCard = new OWLDataMaxCardinalityImpl(
					exactCard.getProperty(), exactCard.getCardinality(),
					exactCard.getFiller());
			OWLDataMinCardinality minCard = new OWLDataMinCardinalityImpl(
					exactCard.getProperty(), exactCard.getCardinality(),
					exactCard.getFiller());

			CollectionUtils.addAll(objects, new OWLSubClassOfAxiomImpl(
					subClass, maxCard, emptyAnno).accept(this));
			CollectionUtils.addAll(objects, new OWLSubClassOfAxiomImpl(
					subClass, minCard, emptyAnno).accept(this));
		} else if (isSuperClass(superClass)) {
			if (subClass instanceof OWLObjectUnionOf) {
				for (OWLClassExpression c : OWLAPIUtils
						.getObjectUnionOperands(subClass)) {
					CollectionUtils
							.addAll(objects, new OWLSubClassOfAxiomImpl(c,
									superClass, emptyAnno).accept(this));
				}
			} else if (isEquivClass(subClass)) {
				CollectionUtils.addAll(objects, mainProcess(arg));
			} else {
				if (LOGGER.isWarnEnabled()) {
					LOGGER.warn("[ "
							+ subClass
							+ "] is not supported as subClass. This axioms was skipped : "
							+ arg);
				}
			}
		} else {
			if (LOGGER.isWarnEnabled()) {
				LOGGER.warn("[ "
						+ superClass
						+ "] is not supported as superClass. This axioms was skipped : "
						+ arg);
			}
		}

		return objects;
	}

	@Override
	public Iterable<? extends Object> visit(OWLEquivalentClassesAxiom arg) {
		Collection<Object> objects = new LinkedList<Object>();
		List<OWLClassExpression> classes = new LinkedList<OWLClassExpression>(
				arg.getClassExpressionsAsList());

		Iterator<OWLClassExpression> it1, it2;
		it1 = classes.iterator();
		while (it1.hasNext()) {
			OWLClassExpression classExpr = it1.next();
			it1.remove();

			it2 = classes.iterator();
			while (it2.hasNext()) {
				OWLClassExpression next = it2.next();
				CollectionUtils.addAll(objects, new OWLSubClassOfAxiomImpl(
						classExpr, next, emptyAnno).accept(this));
				CollectionUtils.addAll(objects, new OWLSubClassOfAxiomImpl(
						next, classExpr, emptyAnno).accept(this));
			}
		}
		return objects;
	}

	@Override
	public Iterable<? extends Object> visit(OWLDisjointClassesAxiom arg) {
		Collection<Object> objects = new LinkedList<Object>();

		List<OWLClassExpression> classes = new LinkedList<OWLClassExpression>(
				arg.getClassExpressionsAsList());

		Iterator<OWLClassExpression> it1, it2;
		it1 = classes.iterator();
		while (it1.hasNext()) {
			OWLClassExpression classExpr = it1.next();
			it1.remove();

			it2 = classes.iterator();
			while (it2.hasNext()) {
				OWLClassExpression next = it2.next();
				Set<OWLClassExpression> operands = new TreeSet<>();
				operands.add(classExpr);
				operands.add(next);
				OWLClassExpression newExpr = new OWLObjectIntersectionOfImpl(
						operands);
				CollectionUtils.addAll(objects, new OWLSubClassOfAxiomImpl(
						newExpr, NOTHING, emptyAnno).accept(this));
			}
		}

		return objects;
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
	public Iterable<? extends Object> visit(OWLSubObjectPropertyOfAxiom arg) {
		InMemoryAtomSet a1, a2;
		a1 = arg.getSubProperty().accept(propertyVisitorXY);
		a2 = arg.getSuperProperty().accept(propertyVisitorXY);

		return Collections.singleton(DefaultRuleFactory.instance().create(a1, a2));
	}

	@Override
	public Iterable<? extends Object> visit(OWLObjectPropertyDomainAxiom arg) {
		OWLClassExpression subClass = new OWLObjectSomeValuesFromImpl(
				arg.getProperty(), DF.getOWLThing());
		OWLClassExpression superClass = arg.getDomain();
		return new OWLSubClassOfAxiomImpl(subClass, superClass, emptyAnno)
				.accept(this);
	}

	@Override
	public Iterable<? extends Object> visit(OWLObjectPropertyRangeAxiom arg) {
		OWLClassExpression subClass = new OWLObjectSomeValuesFromImpl(arg
				.getProperty().getInverseProperty(), DF.getOWLThing());
		OWLClassExpression superClass = arg.getRange();
		return new OWLSubClassOfAxiomImpl(subClass, superClass, emptyAnno)
				.accept(this);
	}

	@Override
	public Iterable<? extends Object> visit(OWLAsymmetricObjectPropertyAxiom arg) {
		InMemoryAtomSet atomset = arg.getProperty().accept(propertyVisitorXY);
		atomset.addAll(arg.getProperty().accept(propertyVisitorYX));
		return Collections.singleton(new DefaultNegativeConstraint(atomset));
	}

	@Override
	public Iterable<? extends Object> visit(OWLReflexiveObjectPropertyAxiom arg) {

		InMemoryAtomSet head = arg.getProperty().accept(propertyVisiotrXX);
		InMemoryAtomSet body = GraalUtils.createAtomSet(new DefaultAtom(
				Predicate.TOP, glueVarX));

		return Collections.singleton(DefaultRuleFactory.instance().create(body, head));
	}

	@Override
	public Iterable<? extends Object> visit(
			OWLIrreflexiveObjectPropertyAxiom arg) {
		InMemoryAtomSet body = arg.getProperty().accept(propertyVisiotrXX);

		return Collections.singleton(new DefaultNegativeConstraint(body));
	}

	@Override
	public Iterable<? extends Object> visit(
			OWLEquivalentObjectPropertiesAxiom arg) {
		return this.equivalentPropertiesAxiom(arg.getProperties());
	}

	@Override
	public Iterable<? extends Object> visit(OWLTransitiveObjectPropertyAxiom arg) {
		InMemoryAtomSet body = arg.getProperty().accept(propertyVisitorXY);
		body.addAll(arg.getProperty().accept(propertyVisitorYZ));
		InMemoryAtomSet head = arg.getProperty().accept(propertyVisitorXZ);

		return Collections.singleton(DefaultRuleFactory.instance().create(body, head));
	}

	@Override
	public Iterable<? extends Object> visit(OWLDisjointObjectPropertiesAxiom arg) {
		return this.disjointPropertiesAxiom(arg.getProperties());
	}

	@Override
	public Iterable<? extends Object> visit(OWLSymmetricObjectPropertyAxiom arg) {

		InMemoryAtomSet body = arg.getProperty().accept(propertyVisitorXY);
		InMemoryAtomSet head = arg.getProperty().accept(propertyVisitorYX);

		return Collections.singleton(DefaultRuleFactory.instance().create(body, head));
	}

	@Override
	public Iterable<? extends Object> visit(OWLFunctionalObjectPropertyAxiom arg) {
		return this.functionalPropertyAxiom(arg.getProperty());
	}

	@Override
	public Iterable<? extends Object> visit(
			OWLInverseFunctionalObjectPropertyAxiom arg) {
		InMemoryAtomSet body = arg.getProperty().accept(propertyVisitorXZ);
		body.addAll(arg.getProperty().accept(propertyVisitorYZ));

		InMemoryAtomSet head = GraalUtils.createAtomSet(new DefaultAtom(
				equalityPredicate, glueVarX, glueVarY));

		return Collections.<Rule> singleton(DefaultRuleFactory.instance().create(body, head));
	}

	@Override
	public Iterable<? extends Object> visit(OWLInverseObjectPropertiesAxiom arg) {
		Collection<Object> rules = new LinkedList<Object>();
		InMemoryAtomSet a1, a2;
		Iterator<OWLObjectPropertyExpression> it = arg.getProperties()
				.iterator();
		a1 = it.next().accept(propertyVisitorXY);
		a2 = it.next().accept(propertyVisitorYX);

		rules.add(DefaultRuleFactory.instance().create(a1, a2));
		rules.add(DefaultRuleFactory.instance().create(a2, a1));

		return rules;
	}

	// /////////////////////////////////////////////////////////////////////////
	// DataPropertyAxiom
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public Iterable<? extends Object> visit(OWLSubDataPropertyOfAxiom arg) {
		InMemoryAtomSet a1, a2;
		a1 = arg.getSubProperty().accept(propertyVisitorXY);
		a2 = arg.getSuperProperty().accept(propertyVisitorXY);

		return Collections.singleton(DefaultRuleFactory.instance().create(a1, a2));
	}

	@Override
	public Iterable<? extends Object> visit(OWLDataPropertyDomainAxiom arg) {
		OWLClassExpression subClass = new OWLDataSomeValuesFromImpl(
				arg.getProperty(), DF.getTopDatatype());
		OWLClassExpression superClass = arg.getDomain();
		return new OWLSubClassOfAxiomImpl(subClass, superClass, emptyAnno)
				.accept(this);
	}

	@Override
	public Iterable<? extends Object> visit(OWLDataPropertyRangeAxiom arg) {
		InMemoryAtomSet body = arg.getProperty().accept(propertyVisitorYX);
		InMemoryAtomSet head = null;
		try {
			head = arg.getRange().accept(dataRangeVisitorX);
		} catch (UnsupportedConstructor e) {
			if (LOGGER.isWarnEnabled()) {
				LOGGER.warn("[ " + e.getConstructor()
						+ "] is not supported here. This axioms was skipped : "
						+ arg);
			}
			return Collections.emptyList();
		}
		return Collections.singleton(DefaultRuleFactory.instance().create(body, head));
	}

	@Override
	public Iterable<? extends Object> visit(OWLFunctionalDataPropertyAxiom arg) {
		return this.functionalPropertyAxiom(arg.getProperty());
	}

	@Override
	public Iterable<? extends Object> visit(OWLEquivalentDataPropertiesAxiom arg) {
		return this.equivalentPropertiesAxiom(arg.getProperties());
	}

	@Override
	public Iterable<? extends Object> visit(OWLDisjointDataPropertiesAxiom arg) {
		return this.disjointPropertiesAxiom(arg.getProperties());
	}

	// /////////////////////////////////////////////////////////////////////////
	// PropertyChain
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public Iterable<? extends Object> visit(OWLSubPropertyChainOfAxiom arg) {
		freeVarGen.setIndex(0);
		InMemoryAtomSet body = GraalUtils.createAtomSet();
		Term varX, varY, firstVarInChain;
		firstVarInChain = varX = freeVarGen.getFreshSymbol();
		for (OWLPropertyExpression pe : arg.getPropertyChain()) {
			varY = freeVarGen.getFreshSymbol();
			body.addAll(pe.accept(new OWLPropertyExpressionVisitorImpl(varX,
					varY)));
			varX = varY;
		}

		InMemoryAtomSet head = arg.getSuperProperty().accept(
				new OWLPropertyExpressionVisitorImpl(firstVarInChain, varX));

		return Collections.singleton(DefaultRuleFactory.instance().create(body, head));

	}

	// /////////////////////////////////////////////////////////////////////////
	// DatatypeDefinition
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public Iterable<? extends Object> visit(OWLDatatypeDefinitionAxiom arg) {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Visit OWLDatatypeDefinitionAxiom is not implemented: "
					+ arg);
		}
		return Collections.emptyList();
	}

	// /////////////////////////////////////////////////////////////////////////
	// HasKey
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public Iterable<? extends Object> visit(OWLHasKeyAxiom arg) {
		// =(Y, Z) :- C(Y), C(Z), p1(Y, X1), p1(Z, X1), ..., pn(Y, Xn), pn(Z,
		// Xn).
		Collection<Rule> rules = GraalUtils.<Rule> createCollection();

		freeVarGen.setIndex(2);
		InMemoryAtomSet head = GraalUtils.createAtomSet(DefaultAtomFactory.instance().create(
				equalityPredicate, glueVarX, glueVarY));

		OWLClassExpression classExpression = OWLAPIUtils.classExpressionDisjunctiveNormalForm(arg.getClassExpression());
		for (Pair<OWLClassExpression, OWLClassExpression> pair : MathUtils
				.selfCartesianProduct(OWLAPIUtils
						.getObjectUnionOperands(classExpression))) {
			InMemoryAtomSet body = pair.getLeft().accept(classVisitorX);
			body.addAll(pair.getRight().accept(classVisitorY));

			for (OWLObjectPropertyExpression pe : arg
					.getObjectPropertyExpressions()) {
				Term var = freeVarGen.getFreshSymbol();

				body.addAll(pe.accept(new OWLPropertyExpressionVisitorImpl(
						glueVarX, var)));
				body.addAll(pe.accept(new OWLPropertyExpressionVisitorImpl(
						glueVarY, var)));
			}

			for (OWLDataPropertyExpression pe : arg
					.getDataPropertyExpressions()) {
				Term var = freeVarGen.getFreshSymbol();

				body.add(DefaultAtomFactory.instance().create(GraalUtils.createPredicate(pe),
						glueVarX, var));
				body.add(DefaultAtomFactory.instance().create(GraalUtils.createPredicate(pe),
						glueVarY, var));
			}

			rules.add(DefaultRuleFactory.instance().create(body, head));
		}

		return rules;
	}

	// /////////////////////////////////////////////////////////////////////////
	// Assertion
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public Iterable<? extends Object> visit(OWLClassAssertionAxiom arg) {
		Collection<Object> objects = GraalUtils.createCollection();
		OWLClassExpression sub = new OWLObjectOneOfImpl(
				Collections.singleton(arg.getIndividual()));
		OWLClassExpression sup = arg.getClassExpression();
		CollectionUtils.addAll(objects, new OWLSubClassOfAxiomImpl(sub, sup,
				emptyAnno).accept(this));
		return objects;
	}

	@Override
	public Iterable<? extends Object> visit(OWLObjectPropertyAssertionAxiom arg) {

		freeVarGen.setIndex(0);
		Term a = GraalUtils.createTerm(arg.getSubject());
		Term b = GraalUtils.createTerm(arg.getObject());
		AtomSet atomset = arg.getProperty().accept(
				new OWLPropertyExpressionVisitorImpl(a, b));
		return Collections.singleton(atomset);
	}

	@Override
	public Iterable<? extends Object> visit(
			OWLNegativeObjectPropertyAssertionAxiom arg) {
		freeVarGen.setIndex(0);
		Term a = GraalUtils.createTerm(arg.getSubject());
		Term b = GraalUtils.createTerm(arg.getObject());
		InMemoryAtomSet atomset = arg.getProperty().accept(
				new OWLPropertyExpressionVisitorImpl(a, b));
		return Collections.singleton(new DefaultNegativeConstraint(atomset));
	}

	@Override
	public Iterable<? extends Object> visit(OWLDataPropertyAssertionAxiom arg) {
		freeVarGen.setIndex(0);
		Term a = GraalUtils.createTerm(arg.getSubject());
		Term b = GraalUtils.createLiteral(arg.getObject());
		InMemoryAtomSet atomset = arg.getProperty().accept(
				new OWLPropertyExpressionVisitorImpl(a, b));
		return Collections.singleton(atomset);
	}

	@Override
	public Iterable<? extends Object> visit(
			OWLNegativeDataPropertyAssertionAxiom arg) {
		freeVarGen.setIndex(0);
		Term a = GraalUtils.createTerm(arg.getSubject());
		Term b = GraalUtils.createLiteral(arg.getObject());
		InMemoryAtomSet atomset = arg.getProperty().accept(
				new OWLPropertyExpressionVisitorImpl(a, b));
		return Collections.singleton(new DefaultNegativeConstraint(atomset));
	}

	@Override
	public Iterable<? extends Object> visit(OWLSameIndividualAxiom arg) {
		Collection<Atom> c = GraalUtils.<Atom> createCollection();
		LinkedList<OWLIndividual> list = new LinkedList<OWLIndividual>(
				arg.getIndividualsAsList());

		Iterator<OWLIndividual> it1, it2;
		it1 = list.iterator();
		while (it1.hasNext()) {
			OWLIndividual individu1 = it1.next();
			it1.remove();

			Term t1 = GraalUtils.createTerm(individu1);

			it2 = list.iterator();
			while (it2.hasNext()) {
				OWLIndividual individu2 = it2.next();

				Term t2 = GraalUtils.createTerm(individu2);
				Atom a = new DefaultAtom(equalityPredicate, t1, t2);
				c.add(a);
			}
		}
		return c;
	}

	@Override
	public Iterable<? extends Object> visit(OWLDifferentIndividualsAxiom arg) {
		Collection<Object> c = GraalUtils.<Object> createCollection();
		LinkedList<OWLIndividual> list = new LinkedList<OWLIndividual>(
				arg.getIndividualsAsList());

		Iterator<OWLIndividual> it1, it2;
		it1 = list.iterator();
		while (it1.hasNext()) {
			OWLIndividual individu1 = it1.next();
			it1.remove();

			Term t1 = GraalUtils.createTerm(individu1);

			it2 = list.iterator();
			while (it2.hasNext()) {
				OWLIndividual individu2 = it2.next();

				Term t2 = GraalUtils.createTerm(individu2);
				Atom a = new DefaultAtom(equalityPredicate, t1, t2);
				c.add(new DefaultNegativeConstraint(new LinkedListAtomSet(a)));
			}
		}
		return c;
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
			LOGGER.info("Visit OWLAnnotationAssertionAxiom is not implemented: "
					+ arg);
		}
		return Collections.emptyList();
	}

	@Override
	public Iterable<? extends Object> visit(OWLSubAnnotationPropertyOfAxiom arg) {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Visit OWLSubAnnotationPropertyOfAxiom is not implemented: "
					+ arg);
		}
		return Collections.emptyList();
	}

	@Override
	public Iterable<? extends Object> visit(OWLAnnotationPropertyDomainAxiom arg) {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Visit OWLAnnotationPropertyDomainAxiom is not implemented: "
					+ arg);
		}
		return Collections.emptyList();
	}

	@Override
	public Iterable<? extends Object> visit(OWLAnnotationPropertyRangeAxiom arg) {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Visit OWLAnnotationPropertyRangeAxiom is not implemented: "
					+ arg);
		}
		return Collections.emptyList();
	}

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

	private Iterable<? extends Object> functionalPropertyAxiom(
			OWLPropertyExpression property) {
		InMemoryAtomSet body = property.accept(propertyVisitorXY);
		body.addAll(property.accept(propertyVisitorXZ));

		InMemoryAtomSet head = GraalUtils.createAtomSet(new DefaultAtom(
				equalityPredicate, glueVarY, glueVarZ));

		return Collections.<Rule> singleton(DefaultRuleFactory.instance().create(body, head));
	}

	private Iterable<? extends Object> equivalentPropertiesAxiom(
			Iterable<? extends OWLPropertyExpression> properties) {
		Collection<Rule> rules = GraalUtils.<Rule> createCollection();
		InMemoryAtomSet a1, a2;

		Iterator<? extends OWLPropertyExpression> it1, it2;
		it1 = properties.iterator();
		while (it1.hasNext()) {
			OWLPropertyExpression propExpr = (OWLPropertyExpression) it1.next();
			a1 = propExpr.accept(propertyVisitorXY);
			it1.remove();

			it2 = properties.iterator();
			while (it2.hasNext()) {
				OWLPropertyExpression next = (OWLPropertyExpression) it2.next();
				a2 = next.accept(propertyVisitorXY);

				rules.add(DefaultRuleFactory.instance().create(a1, a2));
				rules.add(DefaultRuleFactory.instance().create(a2, a1));
			}
		}
		return rules;
	}

	private Iterable<? extends Object> disjointPropertiesAxiom(
			Iterable<? extends OWLPropertyExpression> properties) {

		Collection<Rule> rules = GraalUtils.<Rule> createCollection();
		InMemoryAtomSet a, a1, a2;

		Iterator<? extends OWLPropertyExpression> it1, it2;
		it1 = properties.iterator();
		while (it1.hasNext()) {
			OWLPropertyExpression propExpr = (OWLPropertyExpression) it1.next();
			a1 = propExpr.accept(propertyVisitorXY);
			it1.remove();

			it2 = properties.iterator();
			while (it2.hasNext()) {
				OWLPropertyExpression next = (OWLPropertyExpression) it2.next();
				a2 = next.accept(propertyVisitorXY);
				a = GraalUtils.createAtomSet();
				a.addAll(a1);
				a.addAll(a2);
				rules.add(new DefaultNegativeConstraint(a));
			}
		}
		return rules;

	}

	private boolean isEquivClass(OWLClassExpression expression) {
		return expression instanceof OWLClass
				|| expression instanceof OWLObjectIntersectionOf
				|| expression instanceof OWLDataIntersectionOf
				|| expression instanceof OWLObjectSomeValuesFrom
				|| expression instanceof OWLDataSomeValuesFrom
				|| expression instanceof OWLObjectHasValue
				|| expression instanceof OWLDataHasValue
				|| expression instanceof OWLObjectHasSelf
				|| (expression instanceof OWLObjectMinCardinality && ((OWLObjectMinCardinality) expression)
						.getCardinality() <= 1)
				|| (expression instanceof OWLDataMinCardinality && ((OWLDataMinCardinality) expression)
						.getCardinality() <= 1)
				|| (expression instanceof OWLObjectOneOf && ((OWLObjectOneOf) expression)
						.getIndividuals().size() == 1)
				|| (expression instanceof OWLDataOneOf && ((OWLDataOneOf) expression)
						.getValues().size() == 1);
	}

	private boolean isSuperClass(OWLClassExpression expression) {
		return isEquivClass(expression)
				|| (expression instanceof OWLObjectMaxCardinality && ((OWLObjectMaxCardinality) expression)
						.getCardinality() <= 1)
				|| (expression instanceof OWLDataMaxCardinality && ((OWLDataMaxCardinality) expression)
						.getCardinality() <= 1)
				|| (expression instanceof OWLDataAllValuesFrom);
	}

	private Iterable<? extends Rule> mainProcess(OWLSubClassOfAxiom arg) {
		Collection<Rule> objects = new LinkedList<Rule>();
		InMemoryAtomSet body = null;
		try {
			body = arg.getSubClass().accept(this.classVisitorX);
		} catch (UnsupportedConstructor e) {
			if (LOGGER.isWarnEnabled()) {
				LOGGER.warn("[ "
						+ arg.getSubClass()
						+ "] is not supported as subClass. This axioms was skipped : "
						+ arg);
			}
			return Collections.emptyList();
		}

		// RULES
		InMemoryAtomSet head = null;
		try {
			if (arg.getSuperClass() instanceof OWLObjectMaxCardinality) {
				OWLObjectMaxCardinality maxCard = (OWLObjectMaxCardinality) arg
						.getSuperClass();

				body.addAll(maxCard.getProperty().accept(this.propertyVisitorXY));
				body.addAll(maxCard.getProperty().accept(this.propertyVisitorXZ));

				InMemoryAtomSet bodyTemplate = body;
				head = GraalUtils.createAtomSet(DefaultAtomFactory.instance().create(
						Predicate.EQUALITY, glueVarY, glueVarZ));
				OWLClassExpression expr = OWLAPIUtils.classExpressionDisjunctiveNormalForm(maxCard.getFiller());
				for(Pair<OWLClassExpression,OWLClassExpression> pair : MathUtils.selfCartesianProduct(OWLAPIUtils
							.getObjectUnionOperands(expr))) {
					body = new LinkedListAtomSet(bodyTemplate);
					body.addAll(pair.getLeft().accept(classVisitorY));
					body.addAll(pair.getRight().accept(classVisitorZ));
					objects.add(DefaultRuleFactory.instance().create(body, head));
				}

			} else if (arg.getSuperClass() instanceof OWLDataMaxCardinality) {
				OWLDataMaxCardinality maxCard = (OWLDataMaxCardinality) arg
						.getSuperClass();

				Predicate p = GraalUtils.createPredicate(maxCard.getProperty());
				body.add(DefaultAtomFactory.instance().create(p, glueVarX, glueVarY));
				body.add(DefaultAtomFactory.instance().create(p, glueVarX, glueVarZ));
				InMemoryAtomSet bodyTemplate = body;
				head = GraalUtils.createAtomSet(DefaultAtomFactory.instance().create(
						Predicate.EQUALITY, glueVarY, glueVarZ));
				OWLDataRange expr = OWLAPIUtils.dataRangeDisjunctiveNormalForm(maxCard.getFiller());
				for(Pair<OWLDataRange,OWLDataRange> pair : MathUtils.selfCartesianProduct(OWLAPIUtils.getDataUnionOperands(expr))) {
					body = new LinkedListAtomSet(bodyTemplate);
					body.addAll(pair.getLeft().accept(dataRangeVisitorY));
					body.addAll(pair.getRight().accept(dataRangeVisitorZ));
					objects.add(DefaultRuleFactory.instance().create(body, head));
				}
			} else if (arg.getSuperClass() instanceof OWLDataAllValuesFrom) {
				OWLDataAllValuesFrom allvalues = (OWLDataAllValuesFrom) arg
						.getSuperClass();

				Predicate p = GraalUtils.createPredicate(allvalues
						.getProperty());
				body.add(DefaultAtomFactory.instance().create(p, glueVarX, glueVarY));
				head = allvalues.getFiller().accept(dataRangeVisitorY);
				objects.add(DefaultRuleFactory.instance().create(body, head));
			} else {
				head = arg.getSuperClass().accept(this.classVisitorX);
				objects.add(DefaultRuleFactory.instance().create(body, head));
			}
		} catch (UnsupportedConstructor e) {
			if (LOGGER.isWarnEnabled()) {
				LOGGER.warn("[ " + e.getConstructor()
						+ "] is not supported here. This axioms was skipped : "
						+ arg);
			}
			objects = Collections.emptyList();
		}

		return objects;
	}

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE CLASSES
	// /////////////////////////////////////////////////////////////////////////

	private static class SpecificFreeVarGen implements VariableGenerator {

		private int index = 0;

		@Override
		public Variable getFreshSymbol() {
			return DefaultTermFactory.instance().createVariable("X" + index++);
		}

		public void setIndex(int index) {
			this.index = index;
		}
	};
}
