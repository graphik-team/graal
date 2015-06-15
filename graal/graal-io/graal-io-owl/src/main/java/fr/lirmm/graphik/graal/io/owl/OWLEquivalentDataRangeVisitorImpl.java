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

import org.semanticweb.owlapi.model.OWLDataIntersectionOf;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLLiteral;

import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.atomset.InMemoryAtomSet;
import fr.lirmm.graphik.graal.core.term.Variable;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
class OWLEquivalentDataRangeVisitorImpl extends
		OWLEquivalentDataRangeVisitor {

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
