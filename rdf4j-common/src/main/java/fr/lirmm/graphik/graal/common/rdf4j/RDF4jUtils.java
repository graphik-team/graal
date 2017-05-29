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

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.Literal;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.store.WrongArityException;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.util.Prefix;
import fr.lirmm.graphik.util.URI;
import fr.lirmm.graphik.util.URIUtils;
import fr.lirmm.graphik.util.URIzer;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class RDF4jUtils {

	private URIzer urizer;
	private ValueFactory valueFactory;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	public RDF4jUtils(Prefix prefix, ValueFactory valueFactory) {
		this.urizer = new URIzer(prefix);
		this.valueFactory = valueFactory;
	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	public Statement atomToStatement(Atom atom) throws WrongArityException, MalformedLangStringException {
		if (atom.getPredicate().getArity() != 2) {
			throw new WrongArityException("Error on " + atom + ": arity " + atom.getPredicate().getArity()
					+ " is not supported by this store. ");
		}
		IRI predicate = this.createURI(atom.getPredicate());
		IRI term0 = this.createURI(atom.getTerm(0));
		Value term1 = this.createValue(atom.getTerm(1));
		return valueFactory.createStatement(term0, predicate, term1);
	}

	public Atom statementToAtom(Statement stat) {
		Predicate predicate = valueToPredicate(stat.getPredicate());
		Term term0 = valueToTerm(stat.getSubject());
		Term term1 = valueToTerm(stat.getObject());
		return new DefaultAtom(predicate, term0, term1);
	}

	public Term valueToTerm(Value value) {
		if (value instanceof Resource) {
			return DefaultTermFactory.instance().createConstant(urizer.output(value.toString()));
		} else { //  Literal
			org.eclipse.rdf4j.model.Literal l = (org.eclipse.rdf4j.model.Literal) value;
			URI uri = URIUtils.createURI(l.getDatatype().toString());
			String label = l.getLabel();
			if (uri.equals(URIUtils.RDF_LANG_STRING)) {
				String opt = l.getLanguage();
				label += "@";
				label += opt;
			}
			return DefaultTermFactory.instance().createLiteral(uri, label);
		}
	}

	public Predicate valueToPredicate(Value value) {
		return new Predicate(urizer.output(value.toString()), 2);
	}

	public IRI createURI(Predicate p) {
		return createURI(urizer.input(p.getIdentifier().toString()));
	}
	
	public URIzer getURIzer() {
		return this.urizer;
	}

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

	private IRI createURI(Term t) {
		if (t.isConstant()) {
			return createURI(urizer.input(t.getIdentifier().toString()));
		} else {
			return createURI("_:" + t.getIdentifier().toString());
		}
	}

	/**
	 * Create URI from string. If the specified string is not a valid URI, the
	 * method add a default prefix to the string.
	 */
	private IRI createURI(String string) {
		return valueFactory.createIRI(string);
	}

	private Value createValue(Term t) throws MalformedLangStringException {
		if (t instanceof Literal) {
			Literal l = (Literal) t;
			if (l.getDatatype().equals(URIUtils.RDF_LANG_STRING)) {
				String value = l.getValue().toString();
				int pos = value.lastIndexOf('@');
				if (pos < 0) {
					throw new MalformedLangStringException("The following label does not contains lang part: " + value);
				}
				String label = value.substring(0, pos + 1);
				String lang = value.substring(pos + 1);
				return valueFactory.createLiteral(label, lang);
			} else {
				return valueFactory.createLiteral(l.getValue().toString(),
						valueFactory.createIRI(l.getDatatype().toString()));
			}
		} else {
			return createURI(t);
		}
	}

}
