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
package fr.lirmm.graphik.graal.forward_chaining.halting_condition;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.core.Query;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.term.Term;
import fr.lirmm.graphik.graal.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.homomorphism.HomomorphismFactoryException;
import fr.lirmm.graphik.graal.homomorphism.StaticHomomorphism;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class RestrictedChaseStopCondition implements ChaseHaltingCondition {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(RestrictedChaseStopCondition.class);
	
	@Override
	public boolean canIAdd(AtomSet atomSet, Set<Term> fixedTerms, AtomSet from, AtomSet base) throws HomomorphismFactoryException, HomomorphismException {
		
		Query query = new ConjunctiveQueryWithFixedVariables(atomSet, fixedTerms);
		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("Fixed Query:" + query);
		}
		if (StaticHomomorphism.executeQuery(query, base).hasNext()) {
			return false;
		}
		return true;
	}

}
