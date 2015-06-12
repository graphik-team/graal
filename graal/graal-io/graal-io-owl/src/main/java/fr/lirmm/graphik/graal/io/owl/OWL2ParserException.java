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
