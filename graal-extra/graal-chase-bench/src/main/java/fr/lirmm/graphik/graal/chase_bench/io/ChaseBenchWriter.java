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
package fr.lirmm.graphik.graal.chase_bench.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.Literal;
import fr.lirmm.graphik.graal.api.core.NegativeConstraint;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Term.Type;
import fr.lirmm.graphik.graal.api.io.AbstractGraalWriter;
import fr.lirmm.graphik.graal.api.io.AtomSetWriter;
import fr.lirmm.graphik.graal.api.io.AtomWriter;
import fr.lirmm.graphik.graal.api.io.ConjunctiveQueryWriter;
import fr.lirmm.graphik.graal.api.io.NegativeConstraintWriter;
import fr.lirmm.graphik.graal.api.io.RuleWriter;
import fr.lirmm.graphik.graal.core.factory.DefaultAtomFactory;
import fr.lirmm.graphik.util.MethodNotImplementedError;
import fr.lirmm.graphik.util.Prefix;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class ChaseBenchWriter extends AbstractGraalWriter {


	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Write into the standard output. Warning, if you close this object, you
	 * will close the standard output.
	 */
	public ChaseBenchWriter() {
		this(new OutputStreamWriter(System.out));
	}

	public ChaseBenchWriter(OutputStream out) {
		this(new OutputStreamWriter(out));
	}

	public ChaseBenchWriter(Writer out) {
		super(out, DefaultAtomFactory.instance());
	}

	public ChaseBenchWriter(File file) throws IOException {
		this(new FileWriter(file));
	}

	/**
	 * Write into a file specified by the path file.
	 * 
	 * @param path
	 *            the file path
	 * @throws IOException
	 */
	public ChaseBenchWriter(String path) throws IOException {
		 this(new FileWriter(path));
	}

	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public ConjunctiveQueryWriter write(ConjunctiveQuery query) throws IOException {
		// TODO implement this method
		throw new MethodNotImplementedError();
	}

	@Override
	public fr.lirmm.graphik.graal.api.io.Writer write(Prefix prefix) throws IOException {
		// TODO implement this method
		throw new MethodNotImplementedError();
	}

	@Override
	public RuleWriter write(Rule rule) throws IOException {
		// TODO implement this method
		throw new MethodNotImplementedError();
	}

	@Override
	public AtomSetWriter write(AtomSet atomSet) throws IOException {
		for (Atom a : atomSet) {
			this.write(a);
		}
		return this;
	}

	@Override
	public AtomWriter write(Atom atom) throws IOException {
		this.writeAtom(atom);
		return this;
	}

	@Override
	public NegativeConstraintWriter write(NegativeConstraint constraint) throws IOException {
		// TODO implement this method
		throw new MethodNotImplementedError();
	}

	@Override
	public AbstractGraalWriter writeComment(String comment) throws IOException {
		// TODO implement this method
		throw new MethodNotImplementedError();
	}

	@Override
	protected void writeStandardAtom(Atom atom) throws IOException {
		this.write(atom.getPredicate().getIdentifier());
		this.write('(');
		boolean first = true;
		for (Term t : atom) {
			if (!first)
				this.write(',');
			first = false;
			this.writeTerm(t);
		}
		this.writeln(") .");
	}

	@Override
	protected void writeEquality(Term term, Term term2) throws IOException {
		this.write(term);
		this.write(" = ");
		this.write(term2);
		this.writeln(".");
	}

	@Override
	protected void writeBottom() throws IOException {
		// TODO implement this method
		throw new MethodNotImplementedError();
	}

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////////
	// OBJECT OVERRIDE METHODS
	// /////////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

	protected void writeTerm(Term t) throws IOException {
		if (Type.VARIABLE.equals(t.getType())) {
			this.write("?" + t.getIdentifier());
		} else if (Type.CONSTANT.equals(t.getType())) {
			this.write(t.getIdentifier());
		} else { // LITERAL
			this.writeLiteral((Literal) t);
		}
	}

	protected void writeLiteral(Literal l) throws IOException {
		if (l.getValue() instanceof String) {
			this.write('"');
			this.write(l.getValue());
			this.write('"');
		} else {
			this.write(l.getValue().toString());
		}
	}

}
