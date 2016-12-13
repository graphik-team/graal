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
package fr.lirmm.graphik.graal.store.triplestore;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;

import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Term.Type;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.util.URIUtils;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
final class Utils {

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////
	
	private Utils(){}

	static String predicateToString(Predicate p) {
		return "<" + URIzer.instance().input(p.getIdentifier().toString()) + ">";
	}

	static String termToString(Term t) {
		return Utils.termToString(t, "<" + URIzer.instance().input(t.getIdentifier().toString()) + ">");
	}

	static String termToString(Term t, String valueIfVariable) {
		if (Term.Type.CONSTANT.equals(t.getType())) {
			return "<" + URIzer.instance().input(t.getIdentifier().toString()) + ">";
		} else if (Term.Type.LITERAL.equals(t.getType())) {
			return t.getIdentifier().toString();
		} else if (Term.Type.VARIABLE.equals(t.getType())) {
			return valueIfVariable;
		} else {
			return "";
		}
	}

	static Term createTerm(RDFNode node) {
		Term term = null;
		if (node.isLiteral()) {
			Literal l = node.asLiteral();
			term = DefaultTermFactory.instance().createLiteral(URIUtils.createURI(l.getDatatypeURI()), l.getValue());
		} else {
			term = DefaultTermFactory.instance().createConstant(URIzer.instance().output(node.toString()));
		}
		return term;
	}

	static Predicate createPredicate(RDFNode node, int arity) {
		String s = node.toString();
		s = URIzer.instance().output(s);
		return new Predicate(s, arity);
	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	
	// /////////////////////////////////////////////////////////////////////////
	// OBJECT OVERRIDE METHODS
	// /////////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

}
