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
package fr.lirmm.graphik.graal.io.rdf;

import java.io.IOException;
import java.io.OutputStream;

import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.Rio;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.io.AtomWriter;
import fr.lirmm.graphik.graal.api.io.Writer;
import fr.lirmm.graphik.graal.api.store.WrongArityException;
import fr.lirmm.graphik.graal.common.rdf4j.MalformedLangStringException;
import fr.lirmm.graphik.graal.common.rdf4j.RDF4jUtils;
import fr.lirmm.graphik.util.Prefix;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class RDFWriter implements AtomWriter {

	private final RDF4jUtils utils = new RDF4jUtils(new Prefix("rdf4j", "file://rdf4j/"), SimpleValueFactory.getInstance());
	private final org.eclipse.rdf4j.rio.RDFWriter writer;
	
	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	public RDFWriter(OutputStream os, RDFFormat format) {
		this.writer = Rio.createWriter(format, os);
		this.writer.startRDF();
	}
	public RDFWriter(java.io.Writer writer, RDFFormat format) {
		this.writer = Rio.createWriter(format, writer);
		this.writer.startRDF();
	}
	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public Writer write(Prefix prefix) throws IOException {
		this.writer.handleNamespace(prefix.getPrefixName(), prefix.getPrefix());
		return this;
	}

	@Override
	public Writer writeComment(String comment) throws IOException {
		this.writer.handleComment(comment);
		return this;
	}

	@Override
	public AtomWriter write(Atom atom) throws IOException {
		
		try {
			this.writer.handleStatement(utils.atomToStatement(atom));
		} catch (RDFHandlerException e) {
			// TODO treat this exception
			e.printStackTrace();
			throw new Error("Untreated exception");
		} catch (WrongArityException e) {
			// TODO treat this exception
			e.printStackTrace();
			throw new Error("Untreated exception");
		} catch (MalformedLangStringException e) {
			// TODO treat this exception
			e.printStackTrace();
			throw new Error("Untreated exception");
		}
		return this;
	}

	@Override
	public void flush() throws IOException {
	}

	@Override
	public void close() throws IOException {
		this.writer.endRDF();
	}

	// /////////////////////////////////////////////////////////////////////////
	// OBJECT OVERRIDE METHODS
	// /////////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	
}
