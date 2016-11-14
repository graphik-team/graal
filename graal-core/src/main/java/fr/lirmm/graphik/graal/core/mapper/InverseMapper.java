/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2015)
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
package fr.lirmm.graphik.graal.core.mapper;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Mapper;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Rule;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class InverseMapper implements Mapper {

	private Mapper mapper;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	public InverseMapper(Mapper mapper) {
		this.mapper = mapper;
	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public Predicate map(Predicate predicate) {
		return this.mapper.unmap(predicate);
	}

	@Override
	public Predicate unmap(Predicate predicate) {
		return this.mapper.map(predicate);
	}

	@Override
	public Atom map(Atom atom) {
		return this.mapper.unmap(atom);
	}

	@Override
	public Atom unmap(Atom atom) {
		return this.mapper.map(atom);
	}

	@Override
	public ConjunctiveQuery map(ConjunctiveQuery cq) {
		return this.mapper.unmap(cq);
	}

	@Override
	public ConjunctiveQuery unmap(ConjunctiveQuery cq) {
		return this.mapper.map(cq);
	}

	@Override
	public Rule map(Rule rule) {
		return this.mapper.unmap(rule);
	}

	@Override
	public Rule unmap(Rule rule) {
		return this.mapper.map(rule);
	}

	@Override
	public InMemoryAtomSet map(InMemoryAtomSet atomset) {
		return this.mapper.unmap(atomset);
	}

	@Override
	public InMemoryAtomSet unmap(InMemoryAtomSet atomset) {
		return this.mapper.map(atomset);
	}

	@Override
	public Mapper inverse() {
		return this.mapper;
	}

	// /////////////////////////////////////////////////////////////////////////
	// OBJECT OVERRIDE METHODS
	// /////////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

}
