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
package fr.lirmm.graphik.graal.io.rdf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;

import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.Rio;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.impl.DefaultAtom;
import fr.lirmm.graphik.graal.io.AbstractAtomParser;
import fr.lirmm.graphik.graal.io.ParseError;
import fr.lirmm.graphik.util.stream.ArrayBlockingStream;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public final class RDFRawParser extends AbstractAtomParser {

	private ArrayBlockingStream<Atom> buffer = new ArrayBlockingStream<Atom>(
			512);

	private static class RDFListener extends AbstractRDFListener {

		private ArrayBlockingStream<Atom> set;

		public RDFListener(ArrayBlockingStream<Atom> set) {
			this.set = set;
		}

		@Override
		protected void createAtom(DefaultAtom atom) {
			this.set.write(atom);
		}
	};

	private static class Producer implements Runnable {

		private Reader reader;
		private ArrayBlockingStream<Atom> buffer;
		private RDFFormat format;

		Producer(Reader reader, ArrayBlockingStream<Atom> buffer, RDFFormat format) {
			this.reader = reader;
			this.buffer = buffer;
			this.format = format;
		}

		@Override
		public void run() {

			org.openrdf.rio.RDFParser rdfParser = Rio
					.createParser(format);
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

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////

	public RDFRawParser(Reader reader, RDFFormat format) {
		new Thread(new Producer(reader, buffer, format)).start();
	}

	public RDFRawParser(URL url, RDFFormat format) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				url.openStream()));
		new Thread(new Producer(reader, buffer, format)).start();
	}

	public RDFRawParser(String s, RDFFormat format) {
		this(new StringReader(s), format);
	}

	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public boolean hasNext() {
		return buffer.hasNext();
	}

	@Override
	public Atom next() {
		return buffer.next();
	}

	@Override
	public void close() {
		this.buffer.close();
	}

};