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
package fr.lirmm.graphik.graal.io.owl;

import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLPropertyExpressionVisitorEx;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.term.Term;
import fr.lirmm.graphik.graal.io.owl.logic.Literal;
import fr.lirmm.graphik.graal.io.owl.logic.LogicalFormula;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
class OWLPropertyExpressionVisitorImpl implements
		OWLPropertyExpressionVisitorEx<LogicalFormula> {
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(OWLPropertyExpressionVisitorImpl.class);
	
	private Term glueVariable1, glueVariable2;
	private ShortFormProvider prefixManager;

	public OWLPropertyExpressionVisitorImpl(ShortFormProvider prefixManager, Term glueVarX, Term glueVarY) {
		this.prefixManager = prefixManager;
		this.glueVariable1 = glueVarX;
		this.glueVariable2 = glueVarY;
	}

	// /////////////////////////////////////////////////////////////////////////
	//
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public LogicalFormula visit(OWLAnnotationProperty arg) {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Visit OWLAnnotationProperty is not implemented: " + arg);
		}
		return new LogicalFormula();
	}
	
	@Override
	public LogicalFormula visit(OWLObjectProperty property) {
		Predicate p = this.createPredicate(property);
		Atom a = this.createAtom(p, glueVariable1, glueVariable2);
		return this.createLogicalFormula(a);
	}

	@Override
	public LogicalFormula visit(OWLObjectInverseOf property) {
		Predicate p = this.createPredicate(property.getInverse());
		Atom a = this.createAtom(p, glueVariable2, glueVariable1);
		return this.createLogicalFormula(a);
	}

	@Override
	public LogicalFormula visit(OWLDataProperty property) {
		Predicate p = this.createPredicate(property);
		Atom a = this.createAtom(p, glueVariable1, glueVariable2);
		return this.createLogicalFormula(a);
	}

	// /////////////////////////////////////////////////////////////////////////
	//
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * @param property
	 * @return
	 */
	private Predicate createPredicate(OWLObjectPropertyExpression property) {
		Predicate predicate = null;
		String name = this.prefixManager.getShortForm(property.asOWLObjectProperty());
		predicate = new Predicate(name , 2);
		return predicate;
	}
	
	private Predicate createPredicate(OWLDataPropertyExpression property) {
		Predicate predicate = null;
		String name = this.prefixManager.getShortForm(property.asOWLDataProperty());
		predicate = new Predicate(name , 2);
		return predicate;
	}

	private Atom createAtom(Predicate p, Term... terms) {
		return new DefaultAtom(p, terms);
	}

	private LogicalFormula createLogicalFormula(Atom a) {
		return new LogicalFormula(new Literal(a, true));
	}

}
