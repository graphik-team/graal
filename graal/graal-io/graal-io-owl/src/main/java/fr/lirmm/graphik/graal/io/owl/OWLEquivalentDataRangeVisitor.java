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

import java.util.Set;

import org.semanticweb.owlapi.model.OWLDataComplementOf;
import org.semanticweb.owlapi.model.OWLDataIntersectionOf;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDataRangeVisitorEx;
import org.semanticweb.owlapi.model.OWLDataUnionOf;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDatatypeRestriction;
import org.semanticweb.owlapi.model.OWLLiteral;

import fr.lirmm.graphik.graal.core.atomset.InMemoryAtomSet;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
abstract class OWLEquivalentDataRangeVisitor implements
		OWLDataRangeVisitorEx<InMemoryAtomSet> {

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public abstract InMemoryAtomSet visit(OWLDatatype node);

	public abstract InMemoryAtomSet dataOneOf1(OWLLiteral literal);

	@Override
	public abstract InMemoryAtomSet visit(OWLDataIntersectionOf node);


	@Override
	public InMemoryAtomSet visit(OWLDataOneOf node) {
		Set<OWLLiteral> literals = node.getValues();
		if (literals.size() == 1) {
			return this.dataOneOf1(literals.iterator().next());
		}
		throw new UnsupportedConstructor(node);
	}

	@Override
	public InMemoryAtomSet visit(OWLDataComplementOf node) {
		throw new UnsupportedConstructor(node);
	}

	@Override
	public InMemoryAtomSet visit(OWLDataUnionOf node) {
		throw new UnsupportedConstructor(node);
	}

	@Override
	public InMemoryAtomSet visit(OWLDatatypeRestriction node) {
		throw new UnsupportedConstructor(node);
	}

}
