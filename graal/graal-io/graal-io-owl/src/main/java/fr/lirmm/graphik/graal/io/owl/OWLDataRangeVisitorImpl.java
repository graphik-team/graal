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

import org.semanticweb.owlapi.model.OWLDataComplementOf;
import org.semanticweb.owlapi.model.OWLDataIntersectionOf;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDataRangeVisitorEx;
import org.semanticweb.owlapi.model.OWLDataUnionOf;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDatatypeRestriction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.io.owl.logic.LogicalFormula;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
class OWLDataRangeVisitorImpl implements OWLDataRangeVisitorEx<LogicalFormula> {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(OWLDataRangeVisitorImpl.class);
		
	@Override
	public LogicalFormula visit(OWLDatatype node) {
		if (LOGGER.isWarnEnabled()) {
			LOGGER.warn("OWLDatatype is not supported. This axioms was skipped : "
					+ node);
		}
		return new LogicalFormula();
	}

	@Override
	public LogicalFormula visit(OWLDataOneOf node) {
		if (LOGGER.isWarnEnabled()) {
			LOGGER.warn("OWLDataOneOf is not supported. This axioms was skipped : "
					+ node);
		}
		return new LogicalFormula();
	}

	@Override
	public LogicalFormula visit(OWLDataComplementOf node) {
		if (LOGGER.isWarnEnabled()) {
			LOGGER.warn("OWLDataComplementOf is not supported. This axioms was skipped : "
					+ node);
		}
		return new LogicalFormula();
	}

	@Override
	public LogicalFormula visit(OWLDataIntersectionOf node) {
		if (LOGGER.isWarnEnabled()) {
			LOGGER.warn("OWLDataIntersectionOf is not supported. This axioms was skipped : "
					+ node);
		}
		return new LogicalFormula();
	}

	@Override
	public LogicalFormula visit(OWLDataUnionOf node) {
		if (LOGGER.isWarnEnabled()) {
			LOGGER.warn("OWLDataUnionOf is not supported. This axioms was skipped : "
					+ node);
		}
		return new LogicalFormula();
	}

	@Override
	public LogicalFormula visit(OWLDatatypeRestriction node) {
		if (LOGGER.isWarnEnabled()) {
			LOGGER.warn("OWLDatatypeRestriction is not supported. This axioms was skipped : "
					+ node);
		}
		return new LogicalFormula();
	}



}
