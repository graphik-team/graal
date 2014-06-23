/**
 * 
 */
package fr.lirmm.graphik.obda.parser.semanticweb;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.Rio;

import parser.DatalogGrammar;
import parser.ParseException;
import fr.lirmm.graphik.kb.core.Atom;
import fr.lirmm.graphik.kb.core.DefaultAtom;
import fr.lirmm.graphik.obda.ParseError;
import fr.lirmm.graphik.util.stream.AbstractReader;
import fr.lirmm.graphik.util.stream.ArrayBlockingStream;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public class RDFParser extends AbstractReader<Atom> {

	private static ArrayBlockingStream<Atom> buffer = new ArrayBlockingStream<Atom>(
			512);

	private static class RDFListener extends AbstractRDFListener {

		private ArrayBlockingStream<Atom> set = buffer;

		@Override
		protected void createAtom(DefaultAtom atom) {
			this.set.write(atom);
		}
	};
	

	private static class Producer implements Runnable {

		private Reader reader;

		Producer(Reader reader) {
			this.reader = reader;
		}

		public void run() {
			
			org.openrdf.rio.RDFParser rdfParser = Rio.createParser(RDFFormat.RDFXML);
			rdfParser.setRDFHandler(new RDFListener());
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
		}
	}

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////

	public RDFParser(Reader reader) {
		new Thread(new Producer(reader)).start();
		
	}

	public RDFParser(String s) {
		this(new StringReader(s));
	}

	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////

	public boolean hasNext() {
		return buffer.hasNext();
	}

	public Atom next() {
		return buffer.next();
	}

};