/**
 * 
 */
package fr.lirmm.graphik.graal.io.owl;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class OWLParserException extends Exception {

	/**
	 * @param e
	 */
	public OWLParserException(OWLOntologyCreationException e) {
		super(e);
	}

}
