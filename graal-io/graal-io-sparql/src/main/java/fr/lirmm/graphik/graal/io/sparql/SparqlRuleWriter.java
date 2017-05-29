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

import fr.lirmm.graphik.graal.GraalConstant;
import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.io.RuleWriter;
import fr.lirmm.graphik.util.Prefix;
import fr.lirmm.graphik.util.URIzer;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class SparqlRuleWriter extends AbstractSparqlWriter implements RuleWriter {

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////

	public SparqlRuleWriter(Writer out, URIzer urizer) {
		super(out, urizer);
	}
	
	public SparqlRuleWriter(Writer out) {
		super(out, new URIzer(GraalConstant.INTERNAL_PREFIX));
	}

	public SparqlRuleWriter() {
		this(new OutputStreamWriter(System.out));
	}

	public SparqlRuleWriter(OutputStream out) {
		this(new OutputStreamWriter(out));
	}

	public SparqlRuleWriter(File file) throws IOException {
		this(new FileWriter(file));
	}

	public SparqlRuleWriter(String path) throws IOException {
		this(new FileWriter(path));
	}

	// //////////////////////////////////////////////////////////////////////////
	//
	// //////////////////////////////////////////////////////////////////////////
	@Override
	public SparqlRuleWriter write(Prefix prefix) throws IOException {
		super.write(prefix);
		return this;
	}

	@Override
	public SparqlRuleWriter write(Rule rule) throws IOException {

		this.write("\nCONSTRUCT\n");
		this.writeAtomSet(rule.getHead());

		this.write("\nWHERE\n");
		this.writeAtomSet(rule.getBody());

		return this;
	}

	@Override
	public SparqlRuleWriter writeComment(String comment) throws IOException {
		super.writeComment(comment);
		return this;
	}

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

	private void writeAtomSet(InMemoryAtomSet atomset) throws IOException {
		this.write(" {\n");
		boolean isFirst = true;
		CloseableIteratorWithoutException<Atom> it = atomset.iterator();
		while (it.hasNext()) {
			Atom a = it.next();
			if (!isFirst) {
				this.write(" .\n");
			} else {
				isFirst = false;
			}
			this.writeAtom(a);
		}
		this.write("\n }\n");
	}










}
