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
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.io.owl.logic.Literal;
import fr.lirmm.graphik.graal.io.owl.logic.LogicalFormula;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class OWLPropertyExpressionVisitorImpl implements
		OWLPropertyExpressionVisitorEx<LogicalFormula> {
	
	private static final Logger logger = LoggerFactory
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
		if (logger.isInfoEnabled()) {
			logger.info("Visit OWLAnnotationProperty is not implemented: " + arg);
		}
		return null;
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
		if (!property.isAnonymous()) {
			String name = this.prefixManager.getShortForm(property.asOWLObjectProperty());
			predicate = new Predicate(name , 2);
		} else {
			throw new Error("not yet implemented");
		}
		return predicate;
	}
	
	private Predicate createPredicate(OWLDataPropertyExpression property) {
		Predicate predicate = null;
		if (!property.isAnonymous()) {
			String name = this.prefixManager.getShortForm(property.asOWLDataProperty());
			predicate = new Predicate(name , 2);
		} else {
			throw new Error("not yet implemented");
		}
		return predicate;
	}

	private Atom createAtom(Predicate p, Term... terms) {
		return new DefaultAtom(p, terms);
	}

	private LogicalFormula createLogicalFormula(Atom a) {
		return new LogicalFormula(new Literal(a, true));
	}

}
