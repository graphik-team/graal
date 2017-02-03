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

import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpressionVisitorEx;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasSelf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;

import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
abstract class OWLEquivalentClassExpressionVisitor implements
		OWLClassExpressionVisitorEx<InMemoryAtomSet> {

	// /////////////////////////////////////////////////////////////////////////
	//
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public InMemoryAtomSet visit(OWLObjectMinCardinality arg) {
		if (arg.getCardinality() == 0) {
			return this.objectMinCardinality0(arg);
		} else if (arg.getCardinality() == 1) {
			return this.objectMinCardinality1(arg);
		}
		throw new UnsupportedConstructor(arg);
	}
	
	@Override
	public InMemoryAtomSet visit(OWLDataMinCardinality arg) {
		if (arg.getCardinality() == 0) {
			return this.dataMinCardinality0(arg);
		} else if (arg.getCardinality() == 1) {
			return this.dataMinCardinality1(arg);
		}
		throw new UnsupportedConstructor(arg);
	}

	@Override
	public InMemoryAtomSet visit(OWLObjectOneOf arg) {
		Set<OWLIndividual> individuals = arg.getIndividuals();
		if (individuals.size() == 1) {
			return this.objectOneOf1(individuals.iterator().next());
		}
		throw new UnsupportedConstructor(arg);
	}

	// /////////////////////////////////////////////////////////////////////////
	//
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public abstract InMemoryAtomSet visit(OWLClass arg);

	@Override
	public abstract InMemoryAtomSet visit(OWLObjectIntersectionOf arg);

	@Override
	public abstract InMemoryAtomSet visit(OWLObjectSomeValuesFrom arg);

	@Override
	public abstract InMemoryAtomSet visit(OWLDataSomeValuesFrom arg);

	@Override
	public abstract InMemoryAtomSet visit(OWLObjectHasValue arg);

	@Override
	public abstract InMemoryAtomSet visit(OWLDataHasValue arg);


	@Override
	public abstract InMemoryAtomSet visit(OWLObjectHasSelf arg);

	public abstract InMemoryAtomSet objectMinCardinality0(
			OWLObjectMinCardinality arg);

	public abstract InMemoryAtomSet dataMinCardinality0(
			OWLDataMinCardinality arg);

	public abstract InMemoryAtomSet objectMinCardinality1(
			OWLObjectMinCardinality arg);
	
	public abstract InMemoryAtomSet dataMinCardinality1(
			OWLDataMinCardinality arg);

	public abstract InMemoryAtomSet objectOneOf1(OWLIndividual owlIndividual);

	// /////////////////////////////////////////////////////////////////////////
	//
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public InMemoryAtomSet visit(OWLObjectUnionOf arg) {
		throw new UnsupportedConstructor(arg);
	}

	@Override
	public InMemoryAtomSet visit(OWLObjectComplementOf arg) {
		throw new UnsupportedConstructor(arg);
	}

	@Override
	public InMemoryAtomSet visit(OWLObjectAllValuesFrom arg) {
		throw new UnsupportedConstructor(arg);
	}

	@Override
	public InMemoryAtomSet visit(OWLDataAllValuesFrom arg) {
		throw new UnsupportedConstructor(arg);
	}

	@Override
	public InMemoryAtomSet visit(OWLObjectExactCardinality arg) {
		throw new UnsupportedConstructor(arg);
	}
	
	@Override
	public InMemoryAtomSet visit(OWLDataExactCardinality arg) {
		throw new UnsupportedConstructor(arg);
	}

	@Override
	public InMemoryAtomSet visit(OWLObjectMaxCardinality arg) {
		throw new UnsupportedConstructor(arg);
	}

	@Override
	public InMemoryAtomSet visit(OWLDataMaxCardinality arg) {
		throw new UnsupportedConstructor(arg);
	}

}
