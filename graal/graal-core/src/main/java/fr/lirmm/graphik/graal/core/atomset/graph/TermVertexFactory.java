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
package fr.lirmm.graphik.graal.core.atomset.graph;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.core.term.Constant;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.core.term.Literal;
import fr.lirmm.graphik.graal.core.term.Term;
import fr.lirmm.graphik.graal.core.term.TermFactory;
import fr.lirmm.graphik.graal.core.term.Variable;
import fr.lirmm.graphik.util.URI;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class TermVertexFactory implements TermFactory {

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
		} else if (term instanceof Variable) {
			return new VariableVertex(((Variable) term));
		} else if (term instanceof Constant) { 
			return new ConstantVertex(((Constant) term));
		} else if (term instanceof Literal) {
			return new LiteralVertex(((Literal) term));
		} else {
			return null;
		}
	}

	@Override
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
