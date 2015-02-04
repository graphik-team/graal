package fr.lirmm.graphik.graal.forward_chaining;

import java.util.Set;

import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.homomorphism.HomomorphismFactoryException;

public class ChaseStopConditionWithHandler implements ChaseStopCondition {

	public ChaseStopConditionWithHandler(ChaseStopCondition c, RuleApplicationHandler h) {
		this.realHaltingCondition = c;
		this.handler = h;
	}

	public ChaseStopConditionWithHandler(ChaseStopCondition c) {
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
	public void setRealChaseHaltingCondition(ChaseStopCondition c) {
		this.realHaltingCondition = c;
	}

	private ChaseStopCondition      realHaltingCondition;
	private RuleApplicationHandler  handler;

};

