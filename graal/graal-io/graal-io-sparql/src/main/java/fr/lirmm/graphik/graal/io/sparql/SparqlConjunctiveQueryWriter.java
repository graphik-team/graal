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
 /**
 * 
 */
package fr.lirmm.graphik.graal.io.sparql;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.io.AbstractWriter;
import fr.lirmm.graphik.graal.api.io.ConjunctiveQueryWriter;
import fr.lirmm.graphik.graal.api.io.WriterException;
import fr.lirmm.graphik.util.Prefix;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class SparqlConjunctiveQueryWriter extends AbstractWriter implements
		ConjunctiveQueryWriter {

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////

	public SparqlConjunctiveQueryWriter(Writer out) {
		super(out);
	}

	public SparqlConjunctiveQueryWriter() {
		this(new OutputStreamWriter(System.out));
	}

	public SparqlConjunctiveQueryWriter(OutputStream out) {
		this(new OutputStreamWriter(out));
	}

	public SparqlConjunctiveQueryWriter(File file) throws IOException {
		this(new FileWriter(file));
	}

	public SparqlConjunctiveQueryWriter(String path) throws IOException {
		this(new FileWriter(path));
	}

	// //////////////////////////////////////////////////////////////////////////
	//
	// //////////////////////////////////////////////////////////////////////////
	@Override
	public void write(Prefix prefix) throws IOException {
		this.write("PREFIX ");
		this.write(prefix.getPrefixName());
		this.write(": <");
		this.write(prefix.getPrefix());
		this.writeln('>');
	}
	
	@Override
	public void write(ConjunctiveQuery query)
			throws IOException {

		this.write("SELECT DISTINCT ");
		for(Term t : query.getAnswerVariables())
			this.write(t);

		this.write("\nWHERE\n{\n");
		boolean isFirst = true;
		for(Atom a : query.getAtomSet()) {
			if(!isFirst) {
				this.write(" .\n");
			} else {
				isFirst = false;
			}
			this.writeAtom(a);
		}
		
		this.write("\n}\n");
	}

	/**
	 * @param a
	 * @throws IOException 
	 */
	private void writeAtom(Atom a) throws IOException {
		this.write("\t");
		this.write(a.getTerm(0));
		this.write(' ');
		
		if(a.getPredicate().getArity() == 1) {
			this.write("rdf:type ");
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
	private void write(Predicate predicate) throws IOException {
		this.write(predicate.getIdentifier().toString());
	}

	/**
	 * @param t
	 * @throws IOException 
	 */
	private void write(Term t) throws IOException {
		if (Term.Type.VARIABLE.equals(t.getType())) {
			this.write('?');
		}
		
		this.write(t.getIdentifier().toString());
		this.write(' ');
	}

	@Override
	public void writeComment(String comment) throws IOException {
		this.write("# ");
		this.writeln(comment);
	}

}
