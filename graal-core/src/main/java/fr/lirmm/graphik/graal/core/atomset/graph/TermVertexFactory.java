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
package fr.lirmm.graphik.graal.core.atomset.graph;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.api.core.Constant;
import fr.lirmm.graphik.graal.api.core.Literal;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.graal.api.factory.TermFactory;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.util.URI;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
class TermVertexFactory implements TermFactory {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(TermVertexFactory.class);

	private static TermVertexFactory instance = null;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	public static synchronized final TermVertexFactory instance() {
		if(instance == null) {
			instance = new TermVertexFactory();
		}
		return instance;
	}
	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public TermVertex createTerm(Term term) {
		if (term instanceof VariableVertex) {
			return new VariableVertex(((VariableVertex)term).getTerm());
		} else if (term instanceof ConstantVertex) {
			return new ConstantVertex(((ConstantVertex)term).getTerm());
		} else if (term instanceof LiteralVertex) {
			return new LiteralVertex(((LiteralVertex) term).getTerm());
		} else if (term.isVariable()) {
			return new VariableVertex(((Variable) term));
		} else if (term.isLiteral()) { 
			return new LiteralVertex(((Literal) term));
		} else if (term.isConstant()) {
			return new ConstantVertex(((Constant) term));
		} else {
			return null;
		}
	}

	@Override
	@Deprecated
	public Term createTerm(Object o, Term.Type type) {
		switch (type) {
		case VARIABLE:
			return this.createVariable(o.toString());
		case CONSTANT:
			return this.createConstant(o.toString());
		case LITERAL:
			return this.createLiteral(o);
		default:
			LOGGER.error("unknown term type");
			return null;
		}
	}

	@Override
	public VariableVertex createVariable(Object identifier) {
		return new VariableVertex(DefaultTermFactory.instance().createVariable(
				identifier));
	}

	@Override
	public LiteralVertex createLiteral(Object value) {
		return new LiteralVertex(DefaultTermFactory.instance().createLiteral(
				value));
	}

	@Override
	public LiteralVertex createLiteral(URI datatype, Object value) {
		return new LiteralVertex(DefaultTermFactory.instance().createLiteral(
				datatype, value));
	}

	@Override
	public ConstantVertex createConstant(Object identifier) {
		return new ConstantVertex(DefaultTermFactory.instance().createConstant(
				identifier));
	}

}
