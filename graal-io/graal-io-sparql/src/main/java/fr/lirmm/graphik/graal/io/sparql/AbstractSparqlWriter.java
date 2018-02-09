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
package fr.lirmm.graphik.graal.io.sparql;

import java.io.IOException;
import java.io.Writer;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.Literal;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.io.AbstractWriter;
import fr.lirmm.graphik.graal.api.io.WriterException;
import fr.lirmm.graphik.util.Prefix;
import fr.lirmm.graphik.util.PrefixManager;
import fr.lirmm.graphik.util.URI;
import fr.lirmm.graphik.util.URIUtils;
import fr.lirmm.graphik.util.URIzer;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class AbstractSparqlWriter extends AbstractWriter {

	private PrefixManager pm;
	private URIzer urizer;


	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * @param out
	 */
	public AbstractSparqlWriter(Writer out, URIzer urizer) {
		super(out);
		this.pm = new PrefixManager();
		this.urizer = urizer;
	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	public AbstractSparqlWriter write(Prefix prefix) throws IOException {
		this.pm.putPrefix(prefix);
		this.write("PREFIX ");
		this.write(prefix.getPrefixName());
		this.write(": <");
		this.write(prefix.getPrefix());
		this.writeln('>');

		return this;
	}

	public AbstractSparqlWriter writeComment(String comment) throws IOException {
		this.write("# ");
		this.writeln(comment);

		return this;
	}

	@Override
	public void write(char c) throws IOException {
		super.write(c);
	}

	@Override
	public void write(String str) throws IOException {
		super.write(str);
	}

	// /////////////////////////////////////////////////////////////////////////
	// PROTECTED METHODS
	// /////////////////////////////////////////////////////////////////////////

	protected void writeURI(URI uri) throws IOException {
		Prefix prefix = this.pm.getPrefixByValue(uri.getPrefix());
		if (prefix == null) {
			this.write('<');
			this.write(uri.toString());
			this.write('>');
		} else {
			this.write(prefix.getPrefixName() + ":" + uri.getLocalname());
		}
	}

	/**
	 * @param a
	 * @throws IOException
	 */
	protected void writeAtom(Atom a) throws IOException {
		this.write("\t");
		this.write(a.getTerm(0));
		this.write(' ');

		if (a.getPredicate().getArity() == 1) {
			this.write("a ");
			this.write(a.getPredicate());
		} else if (a.getPredicate().getArity() == 2) {
			this.write(a.getPredicate());
			this.write(' ');
			this.write(a.getTerm(1));
		} else {
			throw new WriterException("Unsupported predicate arity");
		}
	}

	/**
	 * @param predicate
	 * @throws IOException
	 */
	protected void write(Predicate predicate) throws IOException {
		this.writeIdentifier(predicate.getIdentifier());
	}

	protected void writeIdentifier(Object identifier) throws IOException {

		if (!(identifier instanceof URI)) {
			identifier = URIUtils.createURI(urizer.input(identifier.toString()));
		}
		this.writeURI((URI) identifier);
	}

	/**
	 * @param t
	 * @throws IOException
	 */
	protected void write(Term t) throws IOException {
		if (t.isVariable()) {
			this.write('?');
			this.writeSimpleIdentifier(t.getIdentifier().toString());
		} else if (t.isLiteral()) {
			this.writeLiteral((Literal) t);
		} else {
			this.writeIdentifier(t.getIdentifier());
		}
	}

	protected void writeSimpleIdentifier(String identifier) throws IOException {
		identifier = identifier.replaceAll("[^a-zA-Z0-9_]", "_");
		this.write(identifier);
	}
	
	/**
	 * FIXME this is a copy/paste from graal-io-dlgp, this method must be shared through a tiers common module.
	 * @param l
	 * @throws IOException
	 */
	protected void writeLiteral(Literal l) throws IOException {
		if(URIUtils.XSD_STRING.equals(l.getDatatype())) {
			this.write('"');
			this.write(l.getValue().toString().replaceAll("\"", "\\\\\""));
			this.write('"');
		} else if (URIUtils.RDF_LANG_STRING.equals(l.getDatatype())) {
			String value = l.getValue().toString();
			int delim = value.lastIndexOf('@');
			if (delim > 0) {
				this.write('"');
				this.write(value.substring(0, delim).replaceAll("\"", "\\\\\""));
				this.write("\"@");
				this.write(value.substring(delim + 1));
			} else {
				this.write('"');
				this.write(value);
				this.write('"');
			}
		} else if (URIUtils.XSD_INTEGER.equals(l.getDatatype())) {
			this.write(l.getValue().toString());
		} else if (URIUtils.XSD_DECIMAL.equals(l.getDatatype())) {
			this.write(l.getValue().toString());
		} else if (URIUtils.XSD_DOUBLE.equals(l.getDatatype())) {
			this.write(l.getValue().toString()); // FIXME ?
		} else if (URIUtils.XSD_BOOLEAN.equals(l.getDatatype())) {
			this.write(l.getValue().toString());
		} else {
			this.write('"');
			this.write(l.getValue().toString().replaceAll("\"", "\\\\\""));
			this.write("\"^^<");
			this.write(l.getDatatype().toString());
			this.write('>');
		}
	}

}
