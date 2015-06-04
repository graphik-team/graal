/**
 * 
 */
package fr.lirmm.graphik.graal.io.owl;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class OWL2ParserException extends Exception {

	private static final long serialVersionUID = 9139309286569714679L;

	/**
	 * @param e
	 */
	public OWL2ParserException(OWLOntologyCreationException e) {
		super(e);
	}

}
