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
package fr.lirmm.graphik.graal.core.mapper;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.mapper.Mapper;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.factory.DefaultAtomFactory;
import fr.lirmm.graphik.graal.core.factory.DefaultConjunctiveQueryFactory;
import fr.lirmm.graphik.graal.core.factory.DefaultRuleFactory;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public abstract class AbstractMapper implements Mapper {

	private Mapper inverse;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	protected AbstractMapper() {
		this.inverse = new InverseMapper(this);
	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public Atom map(Atom atom) {
		return DefaultAtomFactory.instance().create(this.map(atom.getPredicate()), atom.getTerms());
	}

	@Override
	public Atom unmap(Atom atom) {
		return DefaultAtomFactory.instance().create(this.unmap(atom.getPredicate()), atom.getTerms());
	}

	@Override
	public ConjunctiveQuery map(ConjunctiveQuery cq) {
		return DefaultConjunctiveQueryFactory.instance().create(cq.getLabel(), this.map(cq.getAtomSet()),
		    cq.getAnswerVariables());
	}

	@Override
	public ConjunctiveQuery unmap(ConjunctiveQuery cq) {
		return DefaultConjunctiveQueryFactory.instance().create(cq.getLabel(), this.unmap(cq.getAtomSet()),
		    cq.getAnswerVariables());
	}

	@Override
	public Rule map(Rule rule) {
		InMemoryAtomSet body = this.map(rule.getBody());
		InMemoryAtomSet head = this.map(rule.getHead());
		return DefaultRuleFactory.instance().create(rule.getLabel(), body, head);
	}

	@Override
	public Rule unmap(Rule rule) {
		InMemoryAtomSet body = this.unmap(rule.getBody());
		InMemoryAtomSet head = this.unmap(rule.getHead());
		return DefaultRuleFactory.instance().create(rule.getLabel(), body, head);
	}

	@Override
	public InMemoryAtomSet map(InMemoryAtomSet atomset) {
		InMemoryAtomSet mapped;
		try {
			mapped = atomset.getClass().newInstance();
		} catch (InstantiationException e) {
			mapped = new LinkedListAtomSet();
		} catch (IllegalAccessException e) {
			mapped = new LinkedListAtomSet();
		}

		CloseableIteratorWithoutException<Atom> it = atomset.iterator();
		while (it.hasNext()) {
			mapped.add(this.map(it.next()));
		}
		it.close();

		return mapped;
	}

	@Override
	public InMemoryAtomSet unmap(InMemoryAtomSet atomset) {
		InMemoryAtomSet mapped;
		try {
			mapped = atomset.getClass().newInstance();
		} catch (InstantiationException e) {
			mapped = new LinkedListAtomSet();
		} catch (IllegalAccessException e) {
			mapped = new LinkedListAtomSet();
		}

		CloseableIteratorWithoutException<Atom> it = atomset.iterator();
		while (it.hasNext()) {
			mapped.add(this.unmap(it.next()));
		}
		it.close();

		return mapped;
	}

	@Override
	public Mapper inverse() {
		return this.inverse;
	}

	// /////////////////////////////////////////////////////////////////////////
	// OBJECT OVERRIDE METHODS
	// /////////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

}
