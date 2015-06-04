/**
 * 
 */
package fr.lirmm.graphik.graal.io.owl;

import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLPropertyExpressionVisitorEx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.atomset.InMemoryAtomSet;
import fr.lirmm.graphik.graal.core.term.Term;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
class OWLPropertyExpressionVisitorImpl implements
		OWLPropertyExpressionVisitorEx<InMemoryAtomSet> {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(OWLPropertyExpressionVisitorImpl.class);

	private Term glueVariable1, glueVariable2;

	public OWLPropertyExpressionVisitorImpl(Term glueVarX, Term glueVarY) {
		this.glueVariable1 = glueVarX;
		this.glueVariable2 = glueVarY;
	}

	// /////////////////////////////////////////////////////////////////////////
	//
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public InMemoryAtomSet visit(OWLAnnotationProperty arg) {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Visit OWLAnnotationProperty is not implemented: "
					+ arg);
		}
		return GraalUtils.createAtomSet();
	}

	@Override
	public InMemoryAtomSet visit(OWLObjectProperty property) {
		Predicate p = GraalUtils.createPredicate(property);
		Atom a = GraalUtils.createAtom(p, glueVariable1, glueVariable2);
		return GraalUtils.createAtomSet(a);
	}

	@Override
	public InMemoryAtomSet visit(OWLObjectInverseOf property) {
		Predicate p = GraalUtils.createPredicate(property.getInverse());
		Atom a = GraalUtils.createAtom(p, glueVariable2, glueVariable1);
		return GraalUtils.createAtomSet(a);
	}

	@Override
	public InMemoryAtomSet visit(OWLDataProperty property) {
		Predicate p = GraalUtils.createPredicate(property);
		Atom a = GraalUtils.createAtom(p, glueVariable1, glueVariable2);
		return GraalUtils.createAtomSet(a);
	}


}
