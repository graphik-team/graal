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
package fr.lirmm.graphik.graal.io.sparql;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;

import fr.lirmm.graphik.graal.GraalConstant;
import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.UnionOfConjunctiveQueries;
import fr.lirmm.graphik.graal.api.io.ConjunctiveQueryWriter;
import fr.lirmm.graphik.util.Prefix;
import fr.lirmm.graphik.util.URIzer;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;
import fr.lirmm.graphik.util.stream.IteratorException;
import fr.lirmm.graphik.util.stream.Iterators;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class SparqlConjunctiveQueryWriter extends AbstractSparqlWriter implements ConjunctiveQueryWriter {

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////

	public SparqlConjunctiveQueryWriter(Writer out, URIzer urizer) {
		super(out, urizer);
	}

	public SparqlConjunctiveQueryWriter(Writer out) {
		super(out, new URIzer(GraalConstant.INTERNAL_PREFIX));
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
	public SparqlConjunctiveQueryWriter write(Prefix prefix) throws IOException {
		super.write(prefix);
		return this;
	}

	@Override
	public SparqlConjunctiveQueryWriter write(ConjunctiveQuery query) throws IOException {

		if (query.getAnswerVariables().isEmpty()) {

			if (query.getAtomSet().isEmpty()) { // empty query
				this.write("SELECT ?X \nWHERE {}\n");
				return this;
			}

			this.write("ASK "); // ask query
		} else {
			this.write("SELECT DISTINCT ");

			for (Term t : query.getAnswerVariables()) {
				this.write(t);
				this.write(' ');
			}
		}
		this.write("\nWHERE\n{\n");
		writeQuery(query);
		this.write("\n}\n");

		return this;
	}

	/*
	 * @param ucq : the union of conjunctive query to test
	 * 
	 * @return true if all Conjunctive of ucq have the same Arity
	 * false if ucq has no query
	 * 
	 */
	public boolean checkAllSameArity(Collection<ConjunctiveQuery> queryList) throws IteratorException {

		int arity = 0;
		Iterator<ConjunctiveQuery> it = queryList.iterator();

		if (it.hasNext()) {
			arity = it.next().getAnswerVariables().size();
		} else {
			return false;
		}

		while (it.hasNext()) {
			int queryArity = it.next().getAnswerVariables().size();
			if (queryArity != arity) {
				return false;
			}
		}
		return true;
	}

	/*
	 * @return a SparqlConjunctiveQueryWriter of a union of conjunctiveWriter
	 * Write the SPARQL Query as : SELECT|ASK WHERE {} UNION {} UNION {}.
	 * 
	 * @param queryUnion : A iterator of conjunctiveQuery
	 * 
	 * @maxUnionNb :
	 */
	public SparqlConjunctiveQueryWriter write(UnionOfConjunctiveQueries ucq) {
		try {
			Collection<ConjunctiveQuery> queryList = Iterators.toList(ucq.iterator());

			if (queryList.isEmpty()) {
				return this;
			}

			if (!checkAllSameArity(queryList)) { // check if all query have the same arity
				return this;
			}

			if (ucq.getAnswerVariables().isEmpty()) {
				this.write("ASK "); // ask query
			} else {
				this.write("SELECT DISTINCT "); // select query

				for (Term t : ucq.getAnswerVariables()) {
					this.write(t);
					this.write(' ');
				}
			}
			this.write("\nWHERE\n{\n");

			boolean firstClause = true;
			ConjunctiveQuery query;
			CloseableIterator<ConjunctiveQuery> it = ucq.iterator(); // write all query of ucq

			while (it.hasNext()) {
				query = it.next();

				if (!query.getAtomSet().isEmpty()) {

					if (!firstClause) {
						this.write("UNION {\n");
					} else {
						this.write("{");
						firstClause = false;
					}
					writeQuery(query);
				}

				this.write("\n}\n");
			}
			this.write("}");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this;

	}

	/*
	 * Write query to the the writer
	 * 
	 * @param query : the query to write
	 */
	private void writeQuery(ConjunctiveQuery query) throws IOException {
		boolean isFirst = true;
		CloseableIteratorWithoutException<Atom> it = query.getAtomSet().iterator();
		while (it.hasNext()) {
			Atom a = it.next();
			if (!isFirst) {
				this.write(" .\n");
			} else {
				isFirst = false;
			}
			this.writeAtom(a);
		}
	}

	@Override
	public SparqlConjunctiveQueryWriter writeComment(String comment) throws IOException {
		super.writeComment(comment);
		return this;
	}
}
