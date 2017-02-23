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
import java.io.Reader;

import org.eclipse.rdf4j.rio.ParserConfig;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.Rio;

import fr.lirmm.graphik.graal.api.io.ParseError;
import fr.lirmm.graphik.util.stream.ArrayBlockingStream;

class Producer implements Runnable {

	private Reader reader;
	private ArrayBlockingStream<Object> buffer;
	private RDFFormat format;
	private ParserConfig                config;

	Producer(Reader reader, ArrayBlockingStream<Object> buffer, RDFFormat format, ParserConfig config) {
		this.reader = reader;
		this.buffer = buffer;
		this.format = format;
		this.config = config;
	}

	@Override
	public void run() {

		org.eclipse.rdf4j.rio.RDFParser rdfParser = Rio
				.createParser(format);
		if (this.config != null) {
			rdfParser.setParserConfig(config);
		}
		rdfParser.setRDFHandler(new RDFListener(buffer));
		try {
			rdfParser.parse(this.reader, "");
		} catch (RDFParseException e) {
			throw new ParseError("An error occured while parsing", e);
		} catch (RDFHandlerException e) {
			throw new ParseError("An error occured while parsing", e);
		} catch (IOException e) {
			throw new ParseError("An error occured while parsing", e);
		}
		buffer.close();

		try {
			this.reader.close();
		} catch (IOException e) {
		}

	}
}