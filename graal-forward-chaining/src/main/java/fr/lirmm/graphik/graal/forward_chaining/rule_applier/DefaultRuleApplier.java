/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2017)
 *
 * Contributors :
 *
 * Clément SIPIETER <clement.sipieter@inria.fr>
 * Mélanie KÖNIG
 * Swan ROCHER
 * Jean-François BAGET
 * Michel LECLÈRE
 * Marie-Laure MUGNIER <mugnier@lirmm.fr>
 *
 *
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
/**
 * 
 */
package fr.lirmm.graphik.graal.forward_chaining.rule_applier;

import java.util.LinkedList;

import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseHaltingCondition;
import fr.lirmm.graphik.graal.api.homomorphism.Homomorphism;
import fr.lirmm.graphik.graal.core.factory.DefaultConjunctiveQueryFactory;
import fr.lirmm.graphik.graal.forward_chaining.halting_condition.RestrictedChaseHaltingCondition;
import fr.lirmm.graphik.graal.homomorphism.SmartHomomorphism;

/**
 * This Applier executes a call to the chaseStopCondition for all unique
 * homomorphisms of frontier variables.
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class DefaultRuleApplier<T extends AtomSet> extends AbstractRuleApplier<T> {

	// //////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// //////////////////////////////////////////////////////////////////////////

	/**
	 * Construct a DefaultRuleApplier with a
	 * {@link RestrictedChaseHaltingCondition} and a {@link SmartHomomorphism}
	 */
	public DefaultRuleApplier() {
		this(SmartHomomorphism.instance());
	}

	/**
	 * Construct a DefaultRuleApplier with a
	 * {@link RestrictedChaseHaltingCondition} and the given homomorphism solver.
	 */
	public DefaultRuleApplier(Homomorphism<? super ConjunctiveQuery, ? super T> homomorphismSolver) {
		this(homomorphismSolver, new RestrictedChaseHaltingCondition());
	}

	/**
	 * Construct a DefaultRuleApplier with the given HaltingCondition.
	 * 
	 * @param haltingCondition
	 */
	public DefaultRuleApplier(ChaseHaltingCondition haltingCondition) {
		this(SmartHomomorphism.instance(), haltingCondition);
	}

	/**
	 * Construct a DefaultRuleApplier with the given HaltingCondition,
	 * homomorphism solver and SymbolGenerator
	 * 
	 * @param haltingCondition
	 * @param homomorphismSolver
	 */
	public DefaultRuleApplier(Homomorphism<? super ConjunctiveQuery, ? super T> homomorphismSolver,
	    ChaseHaltingCondition haltingCondition) {
		super(homomorphismSolver, haltingCondition);
	}

	// //////////////////////////////////////////////////////////////////////////
	// METHODS
	// //////////////////////////////////////////////////////////////////////////

	@Override
	protected ConjunctiveQuery generateQuery(Rule rule) {
		return DefaultConjunctiveQueryFactory.instance().create(rule.getBody(),
		    new LinkedList<Term>(rule.getFrontier()));
	}
}
