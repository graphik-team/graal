/**
 * 
 */
package fr.lirmm.graphik.graal.io.owl;

import org.semanticweb.owlapi.model.OWLDataIntersectionOf;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.atomset.InMemoryAtomSet;
import fr.lirmm.graphik.graal.core.term.Variable;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
class OWLEquivalentDataRangeVisitorImpl extends
		OWLEquivalentDataRangeVisitor {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(OWLEquivalentDataRangeVisitorImpl.class);

	private Variable glueVariable;

	public OWLEquivalentDataRangeVisitorImpl(Variable glueVariable) {
		this.glueVariable = glueVariable;
	}

	// /////////////////////////////////////////////////////////////////////////
	//
	// /////////////////////////////////////////////////////////////////////////
		
	@Override
	public InMemoryAtomSet visit(OWLDatatype node) {
		Predicate p = GraalUtils.createPredicate(node);
		return GraalUtils.createAtomSet(GraalUtils.createAtom(p, glueVariable));
	}

	@Override
	public InMemoryAtomSet visit(OWLDataIntersectionOf node) {
		InMemoryAtomSet atomset = GraalUtils.createAtomSet();
		for (OWLDataRange c : node.getOperands()) {
			atomset.addAll(c.accept(this));
		}
		return atomset;
	}

	@Override
	public InMemoryAtomSet dataOneOf1(OWLLiteral literal) {
		InMemoryAtomSet atomset = GraalUtils.createAtomSet();
		atomset.add(GraalUtils.createAtom(Predicate.EQUALITY, glueVariable,
				GraalUtils.createLiteral(literal)));
		return atomset;
	}

}
