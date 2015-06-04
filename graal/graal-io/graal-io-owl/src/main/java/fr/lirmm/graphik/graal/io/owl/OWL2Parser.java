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
import java.util.Iterator;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.DefaultRule;
import fr.lirmm.graphik.graal.core.NegativeConstraint;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.Substitution;
import fr.lirmm.graphik.graal.core.TreeMapSubstitution;
import fr.lirmm.graphik.graal.core.atomset.InMemoryAtomSet;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.io.AbstractParser;
import fr.lirmm.graphik.util.Prefix;
import fr.lirmm.graphik.util.stream.ArrayBlockingStream;
import fr.lirmm.graphik.util.stream.transformator.Transformator;


/**
 * This class parses OWL2 ontologies and converts them into Rule, Facts and
 * Constraints.
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class OWL2Parser extends AbstractParser<Object> {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(OWL2Parser.class);
	private static final RuleTransformator ruleTransfo = new RuleTransformator();
	private static final InMemoryAtomSet bottomAtomSet = new LinkedListAtomSet(
			Atom.BOTTOM);
	
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
	 * @throws OWL2ParserException 
	 */
	public OWL2Parser(InputStream stream) throws OWL2ParserException {
		this.inputStream = stream;
		try {
			this.ontology = this.manager.loadOntologyFromOntologyDocument(stream);
		} catch (OWLOntologyCreationException e) {
			throw new OWL2ParserException(e);
		}
		ShortFormProvider sfp = getShortFormProvider(this.ontology);
		new Thread(new Producer(this.ontology, sfp, buffer)).start();
	}

	/**
	 * Constructor for parsing from the standard input.
	 * @throws OWL2ParserException 
	 */
	public OWL2Parser() throws OWL2ParserException {
		this(System.in);
	}

	/**
	 * Constructor for parsing from the given file.
	 * 
	 * @param file
	 * @throws FileNotFoundException
	 * @throws OWL2ParserException 
	 */
	public OWL2Parser(File file) throws FileNotFoundException, OWL2ParserException {
		try {
			this.ontology = this.manager.loadOntologyFromOntologyDocument(file);
		} catch (OWLOntologyCreationException e) {
			throw new OWL2ParserException(e);
		}
		ShortFormProvider sfp = getShortFormProvider(this.ontology);
		new Thread(new Producer(this.ontology, sfp, buffer)).start();
	}

	/**
	 * Constructor for parsing the content of the string s as OWL content.
	 * 
	 * @param s
	 * @throws OWL2ParserException 
	 */
	public OWL2Parser(String s) throws OWL2ParserException {
		this(new ByteArrayInputStream(s.getBytes()));
	}

	/**
	 * Constructor for parsing the given InputStream.
	 * 
	 * @param in
	 * @throws OWL2ParserException 
	 */
	public OWL2Parser(Reader in) throws OWL2ParserException {
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
	
	@Override
	public boolean hasNext() {
		return buffer.hasNext();
	}

	@Override
	public Object next() {
		return buffer.next();
	}
	
	/**
	 * Closes the stream and releases any system resources associated with it.
	 * Closing a previously closed parser has no effect.
	 * 
	 * @throws IOException
	 */
	@Override
	public void close() {
		if(this.inputStream != null) {
			try {
				this.inputStream.close();
			} catch (IOException e) {
				LOGGER.error("Error during closing inputStream");
			}
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

		@Override
		public void run() {
			try {
				
				OWLAxiomParser visitor = new OWLAxiomParser(shortForm);
	
				for (OWLAxiom a : onto.getAxioms()) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("### OWLAxiom: " + a.toString());
					}
					Iterable<?> iterable = a.accept(visitor);
					if (iterable != null) {
						for (Object o : iterable) {
							if (o instanceof Rule) {
								o = ruleTransfo.transform((Rule) o);
							}
							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug(" => " + o.toString());
							}
							if (o != null) {
								buffer.write(o);
							}
						}
					}
				}
			} finally {
				buffer.close();
			}
		}

	}

	/**
	 * This filter some rule into a fact or a negativeConstraint or delete it.
	 * 
	 * @param r
	 * @return
	 */
	private static class RuleTransformator implements
			Transformator<Rule, Object> {

		@Override
		public Object transform(Rule r) {
			InMemoryAtomSet body = r.getBody();
			InMemoryAtomSet head = r.getHead();

			Iterator<Atom> bodyIt = body.iterator();
			
			// Remove equality in body
			Substitution s = new TreeMapSubstitution();
			while(bodyIt.hasNext()) {
				Atom a = bodyIt.next();
				if (a.getPredicate().equals(Predicate.EQUALITY)
						&& (!a.getTerm(0).isConstant() || !a.getTerm(1)
								.isConstant())) {
					bodyIt.remove();
					if (a.getTerm(0).isConstant()) {
						s.put(a.getTerm(1), a.getTerm(0));
					} else {
						s.put(a.getTerm(0), a.getTerm(1));
					}
				}
			}

			body = removeUselessBottom(s.createImageOf(body));
			bodyIt = body.iterator();

			head = removeUselessTopInHead(removeUselessBottom(s
					.createImageOf(head)));
			Iterator<Atom> headIt = head.iterator();
			
			// USELESS STATEMENT
			if(!headIt.hasNext()) {
				return null;
				// CONSTRAINTS
			} else if (headIt.next().getPredicate().equals(Predicate.BOTTOM)) {
				return new NegativeConstraint(body);
				// ASSERTIONS
			} else if (!bodyIt.hasNext()) {
				return head;
				// USELESS STATEMENT
			} else if (bodyIt.next().getPredicate().equals(Predicate.BOTTOM)) {
				return null;
			} else {
				return new DefaultRule(body, head);
			}
		}
	}

	/**
	 * bottom and A => bottom
	 * 
	 * @param atomset
	 * @return
	 */
	private static InMemoryAtomSet removeUselessBottom(
			InMemoryAtomSet atomset) {
		Iterator<Atom> it = atomset.iterator();
		Atom a;
		while (it.hasNext()) {
			a = it.next();
			if (a.getPredicate().equals(Predicate.BOTTOM)) {
				return bottomAtomSet;
			}
		}

		return atomset;
	}

	private static InMemoryAtomSet removeUselessTopInHead(
			InMemoryAtomSet atomset) {
		InMemoryAtomSet newAtomset = new LinkedListAtomSet();
		Iterator<Atom> it = atomset.iterator();
		Atom a;
		while (it.hasNext()) {
			a = it.next();
			if (!a.getPredicate().equals(Predicate.TOP)) {
				newAtomset.add(a);
			}
		}

		return newAtomset;
	}


}
