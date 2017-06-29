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
package fr.lirmm.graphik.graal.common.rdf4j;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.mapper.AtomMapper;
import fr.lirmm.graphik.graal.core.factory.DefaultAtomFactory;
import fr.lirmm.graphik.graal.core.mapper.InverseAtomMapper;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.util.URIUtils;

/**
 * This class map an atom like rdf:type(<individual>, <Concept>) to
 * <Concept>(<individual>) and conversely.
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class RDFTypeAtomMapper implements AtomMapper {

	private static final Predicate rdfTypePredicate = new Predicate(URIUtils.RDF_TYPE, 2);
	private final AtomMapper inverseMapper = new InverseAtomMapper(this);

	// /////////////////////////////////////////////////////////////////////////
	// SINGLETON
	// /////////////////////////////////////////////////////////////////////////

	private static RDFTypeAtomMapper instance;

	protected RDFTypeAtomMapper() {
		super();
	}

	public static synchronized RDFTypeAtomMapper instance() {
		if (instance == null)
			instance = new RDFTypeAtomMapper();

		return instance;
	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * This method returns an atom like <Concept>(<individual>) if the specified atom is like rdf:type(<individual>, <Concept>).
	 * Otherwise, it returns the specified atom itself.
	 * 
	 * @param atom
	 *            any atom
	 * @return a corresponding atom. It returns an atom like
	 *         <Concept>(<individual>) if the specfied atom is like
	 *         rdf:type(<individual>, <Concept>). Otherwise, it returns the
	 *         specified an atom like rdf:type(<individual>, <Concept>) to <Concept>(<individual>)atom itself.
	 */
	@Override
	public Atom map(Atom atom) {
		if (rdfTypePredicate.equals(atom.getPredicate())) {
			return DefaultAtomFactory.instance().create(new Predicate(atom.getTerm(1).getIdentifier(), 1),
					atom.getTerm(0));
		}
		return atom;
	}

	/**
	 * This method is the reverse method of map, it returns an atom like rdf:type(<individual>, <Concept>) if the specified atom is like <Concept>(<individual>).
	 * Otherwise, it returns the specified atom itself.
	 * 
	 * @param atom
	 *            any atom
	 * @return a corresponding atom. It returns an atom like
	 *         rdf:type(<individual>, <Concept>) if the specfied atom is like
	 *         <Concept>(<individual>). Otherwise, it returns the
	 *         specified an atom like rdf:type(<individual>, <Concept>) to <Concept>(<individual>)atom itself.
	 */
	@Override
	public Atom unmap(Atom atom) {
		if (atom.getPredicate().getArity() == 1) {
			return DefaultAtomFactory.instance().create(rdfTypePredicate, atom.getTerm(0),
					DefaultTermFactory.instance().createConstant(atom.getPredicate().getIdentifier()));
		}
		return atom;
	}

	@Override
	public AtomMapper inverse() {
		return this.inverseMapper;
	}

	// /////////////////////////////////////////////////////////////////////////
	// OBJECT OVERRIDE METHODS
	// /////////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

}
