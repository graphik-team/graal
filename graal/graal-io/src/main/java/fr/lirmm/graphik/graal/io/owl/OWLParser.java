/**
 * 
 */
package fr.lirmm.graphik.graal.io.owl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.input.ReaderInputStream;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.PrefixDocumentFormat;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.util.ShortFormProvider;

import fr.lirmm.graphik.graal.io.Prefix;
import fr.lirmm.graphik.util.stream.AbstractReader;
import fr.lirmm.graphik.util.stream.ArrayBlockingStream;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class OWLParser extends AbstractReader<Object> {

	private ArrayBlockingStream<Object> buffer = new ArrayBlockingStream<Object>(
			512);
	
	private InputStream inputStream = null;
	private OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	private OWLOntology ontology;
	private boolean prefixEnable = true;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Constructor for parsing from the given reader.
	 * 
	 * @param inputStream
	 * @throws OWLParserException 
	 */
	public OWLParser(InputStream stream) throws OWLParserException {
		this.inputStream = stream;
		try {
			this.ontology = this.manager.loadOntologyFromOntologyDocument(stream);
		} catch (OWLOntologyCreationException e) {
			throw new OWLParserException(e);
		}
		ShortFormProvider sfp = getShortFormProvider(this.ontology);
		new Thread(new Producer(this.ontology, sfp, buffer)).start();
	}

	/**
	 * Constructor for parsing from the standard input.
	 * @throws OWLParserException 
	 */
	public OWLParser() throws OWLParserException {
		this(System.in);
	}

	/**
	 * Constructor for parsing from the given file.
	 * 
	 * @param file
	 * @throws FileNotFoundException
	 * @throws OWLParserException 
	 */
	public OWLParser(File file) throws FileNotFoundException, OWLParserException {
		try {
			this.ontology = this.manager.loadOntologyFromOntologyDocument(file);
		} catch (OWLOntologyCreationException e) {
			throw new OWLParserException(e);
		}
		ShortFormProvider sfp = getShortFormProvider(this.ontology);
		new Thread(new Producer(this.ontology, sfp, buffer)).start();
	}

	/**
	 * Constructor for parsing the content of the string s as OWL content.
	 * 
	 * @param s
	 * @throws OWLParserException 
	 */
	public OWLParser(String s) throws OWLParserException {
		this(new ByteArrayInputStream(s.getBytes()));
	}

	/**
	 * Constructor for parsing the given InputStream.
	 * 
	 * @param in
	 * @throws OWLParserException 
	 */
	public OWLParser(Reader in) throws OWLParserException {
		this(new ReaderInputStream(in));
	}

	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	/**
	 * Enable or disable prefix short form. 
	 * @param b Default value is true.
	 */
	public void prefixEnable(boolean b) {
		this.prefixEnable = b;
	}
	
	public boolean hasNext() {
		return buffer.hasNext();
	}

	public Object next() {
		return buffer.next();
	}
	
	/**
	 * Closes the stream and releases any system resources associated with it.
	 * Closing a previously closed parser has no effect.
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {
		if(this.inputStream != null) {
			this.inputStream.close();
			this.inputStream = null;
		}
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	private ShortFormProvider getShortFormProvider(OWLOntology ontology) {
		OWLDocumentFormat format = this.manager.getOntologyFormat(this.ontology);
		DefaultPrefixManager pm = new DefaultPrefixManager();
		if(prefixEnable && format.isPrefixOWLOntologyFormat()) {
			PrefixDocumentFormat prefixFormat = format.asPrefixOWLOntologyFormat();
			
			Map<String, String> prefixMap = prefixFormat.getPrefixName2PrefixMap();
		
			Set<String> forbiddenPrefix = new TreeSet<String>();
			forbiddenPrefix.add("xml:");
			forbiddenPrefix.add("rdf:");
			forbiddenPrefix.add("rdfs:");
			forbiddenPrefix.add("owl:");
			
			for(Map.Entry<String, String> entry : prefixMap.entrySet()) {
				String prefix = entry.getKey();
				if(!forbiddenPrefix.contains(prefix)) {
					pm.setPrefix(prefix, entry.getValue());
					prefix = prefix.substring(0, prefix.length() - 1);
					buffer.write(new Prefix(prefix, entry.getValue()));
				}
			}
		}
		return pm;
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE CLASSES
	// /////////////////////////////////////////////////////////////////////////
	
	private static class Producer implements Runnable {

		private OWLOntology onto;
		private ArrayBlockingStream<Object> buffer;
		private ShortFormProvider shortForm;

		Producer(OWLOntology onto, ShortFormProvider shortForm, ArrayBlockingStream<Object> buffer) {
			this.onto = onto;
			this.buffer = buffer;
			this.shortForm = shortForm;
		}

		public void run() {
			try {
				
				OWLAxiomParser visitor = new OWLAxiomParser(shortForm);
	
				for (OWLAxiom a : onto.getAxioms()) {
					Iterable<?> iterable = a.accept(visitor);
					if(iterable != null) {
						for(Object o : iterable) {
							 buffer.write(o);
						}
					}
				}
			} finally {
				buffer.close();
			}
		}

	}
}
