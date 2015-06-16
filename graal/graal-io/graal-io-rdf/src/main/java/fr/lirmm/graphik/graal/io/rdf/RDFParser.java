/**
 * 
 */
package fr.lirmm.graphik.graal.io.rdf;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;

import org.openrdf.rio.RDFFormat;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.io.AbstractAtomParser;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class RDFParser extends AbstractAtomParser {

	private RDF2Atom rdf2atom;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	public RDFParser(Reader reader, RDFFormat format) {
		this.rdf2atom = new RDF2Atom(new RDFRawParser(reader, format));
	}

	public RDFParser(URL url, RDFFormat format) throws IOException {
		this.rdf2atom = new RDF2Atom(new RDFRawParser(url, format));
	}

	public RDFParser(String s, RDFFormat format) {
		this(new StringReader(s), format);
	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public boolean hasNext() {
		return this.rdf2atom.hasNext();
	}

	@Override
	public Atom next() {
		return this.rdf2atom.next();
	}

	@Override
	public void close() {
		this.rdf2atom.close();
	}

}
