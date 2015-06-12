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
package fr.lirmm.graphik.graal.store;

import fr.lirmm.graphik.graal.core.atomset.AbstractAtomSet;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public abstract class AbstractTripleStore extends AbstractAtomSet implements
		TripleStore {

	protected static final String DEFAULT_PREFIX = "graal:";
	
	protected static final String DEFAULT_PREFIX_VALUE = "<http://inria.fr/graphik/graal/>";
	
	protected static final String PREFIX = "PREFIX " + DEFAULT_PREFIX + " " + DEFAULT_PREFIX_VALUE + " ";

	protected static final String SELECT_TERMS_QUERY = PREFIX
			+ "SELECT DISTINCT ?term " + " WHERE { { ?term  ?p  ?o } "
			+ " UNION { ?s ?p ?term } } ";

	protected static final String SELECT_PREDICATES_QUERY = PREFIX
			+ "SELECT DISTINCT ?p " + " WHERE { ?s ?p ?o }";
	
	public String getDefaultPrefix() {
		return "graal:";
	}
}
