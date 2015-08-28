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
package fr.lirmm.graphik.graal.forward_chaining.rule_applier;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.Substitution;
import fr.lirmm.graphik.graal.core.VariableGenerator;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
import fr.lirmm.graphik.graal.core.factory.ConjunctiveQueryFactory;
import fr.lirmm.graphik.graal.core.impl.DefaultVariableGenerator;
import fr.lirmm.graphik.graal.core.term.Term;
import fr.lirmm.graphik.graal.forward_chaining.halting_condition.ChaseHaltingCondition;
import fr.lirmm.graphik.graal.forward_chaining.halting_condition.RestrictedChaseStopCondition;
import fr.lirmm.graphik.graal.homomorphism.Homomorphism;
import fr.lirmm.graphik.graal.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.homomorphism.HomomorphismFactoryException;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class DefaultRuleApplier<T extends AtomSet> implements
		RuleApplier<Rule, T> {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(DefaultRuleApplier.class);

	private ChaseHaltingCondition haltingCondition;
	private Homomorphism<ConjunctiveQuery, T> solver;
	private VariableGenerator existentialGen;

	// //////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// //////////////////////////////////////////////////////////////////////////
	
	/**
	 * Construct a DefaultRuleApplier with a RestrictedChaseStopCondition and
	 * the given homomorphism solver.
	 */
	public DefaultRuleApplier(
			Homomorphism<ConjunctiveQuery, T> homomorphismSolver) {
		this(homomorphismSolver, new RestrictedChaseStopCondition());
	}

	/**
	 * Construct a DefaultRuleApplier with the given HaltingCondition.
	 * 
	 * @param haltingCondition
	 */
	public DefaultRuleApplier(
			Homomorphism<ConjunctiveQuery, T> homomorphismSolver,
			ChaseHaltingCondition haltingCondition) {
		this(homomorphismSolver, haltingCondition, new DefaultVariableGenerator("E"));
	}
	
	/**
	 * Construct a DefaultRuleApplier with the given HaltingCondition,
	 * homomorphism solver and SymbolGenerator. The SymbolGenerator is used to
	 * generate new existential variables.
	 * 
	 * @param haltingCondition
	 * @param homomorphismSolver
	 * @param existentialVarGenerator
	 */
	public DefaultRuleApplier(
			Homomorphism<ConjunctiveQuery, T> homomorphismSolver,
			ChaseHaltingCondition haltingCondition,
			VariableGenerator existentialVarGenerator) {
		this.haltingCondition = haltingCondition;
		this.solver = homomorphismSolver;
		this.existentialGen = existentialVarGenerator;
	}
	
	// //////////////////////////////////////////////////////////////////////////
	// METHODS
	// //////////////////////////////////////////////////////////////////////////

	@Override
	public boolean apply(Rule rule, T atomSet)
			throws RuleApplicationException {
		boolean isChanged = false;
		ConjunctiveQuery query = ConjunctiveQueryFactory.instance().create(rule.getBody(),
				rule.getFrontier());
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Rule to execute: " + rule);
			LOGGER.debug("       -- Query: " + query);
		}

		try {
			for (Substitution substitution : this.executeQuery(query, atomSet)) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("-- Found homomorphism: " + substitution);
				}
				Set<Term> fixedVars = substitution.getValues();

				// Generate new existential variables
				for (Term t : rule.getExistentials()) {
					substitution.put(t, this.getFreeVar());
				}

				// the atom set produced by the rule application
				AtomSet deductedAtomSet = substitution.createImageOf(rule
						.getHead());
				AtomSet bodyAtomSet = substitution
						.createImageOf(rule.getBody());

				if (this.getHaltingCondition().canIAdd(deductedAtomSet,
						fixedVars, bodyAtomSet, atomSet)) {
					atomSet.addAll(deductedAtomSet);
					isChanged = true;
				}
			}
		} catch (HomomorphismFactoryException e) {
			throw new RuleApplicationException("Error during rule application",
					e);
		} catch (HomomorphismException e) {
			throw new RuleApplicationException("Error during rule application",
					e);
		} catch (AtomSetException e) {
			throw new RuleApplicationException("Error during rule application",
					e);
		}

		return isChanged;
	}

	// //////////////////////////////////////////////////////////////////////////
	//
	// //////////////////////////////////////////////////////////////////////////

	protected ChaseHaltingCondition getHaltingCondition() {
		return this.haltingCondition;
	}

	protected Iterable<Substitution> executeQuery(ConjunctiveQuery query,
			T atomSet) throws HomomorphismFactoryException,
			HomomorphismException {
		return this.solver.execute(query, atomSet);
	}

	protected Term getFreeVar() {
		return this.existentialGen.getFreshVar();
	}

}
