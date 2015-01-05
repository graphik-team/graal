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
public class OWLDataRangeVisitorImpl implements OWLDataRangeVisitorEx<LogicalFormula> {

	private static final Logger logger = LoggerFactory
			.getLogger(OWLDataRangeVisitorImpl.class);
		
	@Override
	public LogicalFormula visit(OWLDatatype node) {
		if (logger.isWarnEnabled()) {
			logger.warn("OWLDatatype is not supported. This axioms was skipped : "
					+ node);
		}
		return new LogicalFormula();
	}

	@Override
	public LogicalFormula visit(OWLDataOneOf node) {
		if (logger.isWarnEnabled()) {
			logger.warn("OWLDataOneOf is not supported. This axioms was skipped : "
					+ node);
		}
		return new LogicalFormula();
	}

	@Override
	public LogicalFormula visit(OWLDataComplementOf node) {
		if (logger.isWarnEnabled()) {
			logger.warn("OWLDataComplementOf is not supported. This axioms was skipped : "
					+ node);
		}
		return new LogicalFormula();
	}

	@Override
	public LogicalFormula visit(OWLDataIntersectionOf node) {
		if (logger.isWarnEnabled()) {
			logger.warn("OWLDataIntersectionOf is not supported. This axioms was skipped : "
					+ node);
		}
		return new LogicalFormula();
	}

	@Override
	public LogicalFormula visit(OWLDataUnionOf node) {
		if (logger.isWarnEnabled()) {
			logger.warn("OWLDataUnionOf is not supported. This axioms was skipped : "
					+ node);
		}
		return new LogicalFormula();
	}

	@Override
	public LogicalFormula visit(OWLDatatypeRestriction node) {
		if (logger.isWarnEnabled()) {
			logger.warn("OWLDatatypeRestriction is not supported. This axioms was skipped : "
					+ node);
		}
		return new LogicalFormula();
	}



}
