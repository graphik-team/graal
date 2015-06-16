/* Graal v0.7.4
 * Copyright (c) 2014-2015 Inria Sophia Antipolis - Méditerranée / LIRMM (Université de Montpellier & CNRS)
 * All rights reserved.
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * Author(s): Clément SIPIETER
 *            Mélanie KÖNIG
 *            Swan ROCHER
 *            Jean-François BAGET
 *            Michel LECLÈRE
 *            Marie-Laure MUGNIER
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
import fr.lirmm.graphik.graal.core.DefaultAtom;
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