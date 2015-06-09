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
 package fr.lirmm.graphik.graal.forward_chaining.halting_condition;

import java.util.Set;

import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.term.Term;
import fr.lirmm.graphik.graal.forward_chaining.rule_applier.RuleApplicationHandler;
import fr.lirmm.graphik.graal.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.homomorphism.HomomorphismFactoryException;

public class ChaseStopConditionWithHandler implements ChaseHaltingCondition {

	public ChaseStopConditionWithHandler(ChaseHaltingCondition c, RuleApplicationHandler h) {
		this.realHaltingCondition = c;
		this.handler = h;
	}

	public ChaseStopConditionWithHandler(ChaseHaltingCondition c) {
		this.realHaltingCondition = c;
		this.handler = RuleApplicationHandler.DEFAULT;
	}

	/**
	 * @param atomSet
	 * @param fixedTerm
	 * @param base
	 * @return
	 * @throws HomomorphismFactoryException
	 * @throws HomomorphismException
	 */
	@Override
	public boolean canIAdd(AtomSet atomSet, Set<Term> fixedTerm,
	                       AtomSet from, AtomSet base) 
		throws HomomorphismFactoryException, HomomorphismException {
		if (this.handler.onRuleApplication(from,atomSet,base)) {
			return this.realHaltingCondition.canIAdd(atomSet,fixedTerm,from,base);
		}
		return false;
	}

	public void setHandler(RuleApplicationHandler h) {
		this.handler = h;
	}
	public void setRealChaseHaltingCondition(ChaseHaltingCondition c) {
		this.realHaltingCondition = c;
	}

	private ChaseHaltingCondition realHaltingCondition;
	private RuleApplicationHandler handler;

};

