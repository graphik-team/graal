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
import java.util.LinkedList;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Literal;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.util.DefaultURI;
import fr.lirmm.graphik.util.URI;
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;

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
	 * Convert {@link org.semanticweb.owlapi.model.IRI} into {@link fr.lirmm.graphik.util.URI}
	 * 
	 * @param iri
	 * @return a {@link URI} equivalent to {@link IRI}
	 */
	public static URI convertIRI(IRI iri) {
		return new DefaultURI(iri.toString());
	}

	/**
	 * 
	 * @param owlClass
	 * @return a {@link Predicate} with arity 1 which represents the specified owl class.
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
	 * @return a {@link Predicate} with arity 1 which represents the specified owl datatype.
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
	 * @return a {@link Predicate} with arity 2 which represents the specified owl object property.
	 */
	public static Predicate createPredicate(OWLObjectPropertyExpression property) {
		Predicate predicate = null;
		URI uri = convertIRI(property.asOWLObjectProperty().getIRI());
		predicate = new Predicate(uri, 2);
		return predicate;
	}

	/**
	 * 
	 * @param property
	 * @return a {@link Predicate} with arity 2 which represents the specified owl data property.
	 */
	public static Predicate createPredicate(OWLDataPropertyExpression property) {
		Predicate predicate = null;
		URI uri = convertIRI(property.asOWLDataProperty().getIRI());
		predicate = new Predicate(uri, 2);
		return predicate;
	}

	/**
	 * @param individu
	 * @return a {@link Term} which represents the owl individual.
	 */
	public static Term createTerm(OWLIndividual individu) {
		if (individu.isNamed()) {
			return DefaultTermFactory.instance().createConstant(
				convertIRI(individu.asOWLNamedIndividual().getIRI()));
		} else {
			return DefaultTermFactory.instance().createVariable(
					individu.asOWLAnonymousIndividual().getID().getID());
		}
	}

	/**
	 * @param object
	 * @return a {@link Literal} which represents the owl literal.
	 */
	public static Literal createLiteral(OWLLiteral object) {
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
	 * @return a new empty {@link Collection}
	 */
	public static <T> Collection<T> createCollection() {
		return new LinkedList<T>();
	}

}
