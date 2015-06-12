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

import java.util.Collection;
import java.util.LinkedList;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;
import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.atomset.InMemoryAtomSet;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.term.Constant;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.core.term.Term;
import fr.lirmm.graphik.util.DefaultURI;
import fr.lirmm.graphik.util.URI;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
final class GraalUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(GraalUtils.class);

	private static final OWLDataFactory DF = new OWLDataFactoryImpl();
	private static final IRI THING_IRI = DF.getOWLThing().getIRI();
	private static final IRI NOTHING_IRI = DF.getOWLNothing().getIRI();
	private static final IRI LITERAL_IRI = IRI
			.create("http://www.w3.org/2000/01/rdf-schema#Literal");

	private GraalUtils() {
	}

	// /////////////////////////////////////////////////////////////////////////
	//
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Convert org.semanticweb.owlapi.model.IRI into fr.lirmm.graphik.util.URI
	 * 
	 * @param iri
	 * @return
	 */
	public static URI convertIRI(IRI iri) {
		return new DefaultURI(iri.toString());
	}

	public static Atom createAtom(Predicate p, Term... terms) {
		return new DefaultAtom(p, terms);
	}

	/**
	 * 
	 * @param owlClass
	 * @return
	 */
	public static Predicate createPredicate(OWLClassExpression owlClass) {
		Predicate predicate = null;
		if (!owlClass.isAnonymous()) {
			predicate = GraalUtils.createPredicate(owlClass.asOWLClass()
					.getIRI());
		} else {
			LOGGER.error("Create predicate from an anonymous owl class."
					+ owlClass.toString());
		}
		return predicate;
	}

	/**
	 * 
	 * @param owlDatatype
	 * @return
	 */
	public static Predicate createPredicate(OWLDatatype owlDatatype) {
		return GraalUtils.createPredicate(owlDatatype.getIRI());
	}

	private static Predicate createPredicate(IRI iri) {
		if (iri.equals(THING_IRI) || iri.equals(LITERAL_IRI)) {
			return Predicate.TOP;
		} else if (iri.equals(NOTHING_IRI)) {
			return Predicate.BOTTOM;
		} else {
			return new Predicate(GraalUtils.convertIRI(iri), 1);
		}
	}

	/**
	 * @param property
	 * @return
	 */
	public static Predicate createPredicate(OWLObjectPropertyExpression property) {
		Predicate predicate = null;
		URI uri = convertIRI(property.asOWLObjectProperty().getIRI());
		predicate = new Predicate(uri, 2);
		return predicate;
	}

	public static Predicate createPredicate(OWLDataPropertyExpression property) {
		Predicate predicate = null;
		URI uri = convertIRI(property.asOWLDataProperty().getIRI());
		predicate = new Predicate(uri, 2);
		return predicate;
	}

	/**
	 * @param value
	 * @return
	 */
	public static Constant createConstant(OWLNamedIndividual individu) {
		return DefaultTermFactory.instance().createConstant(
				convertIRI(individu.asOWLNamedIndividual().getIRI()));
	}

	/**
	 * @param object
	 * @return
	 */
	public static Term createLiteral(OWLLiteral object) {
		return DefaultTermFactory.instance().createLiteral(
				convertIRI(object.getDatatype().getIRI()), object.getLiteral());
	}

	public static InMemoryAtomSet createAtomSet(Atom a) {
		return new LinkedListAtomSet(a);
	}

	public static InMemoryAtomSet createAtomSet() {
		return new LinkedListAtomSet();
	}

	/**
	 * @return
	 */
	public static <T> Collection<T> createCollection() {
		return new LinkedList<T>();
	}

}
