package fr.lirmm.graphik.graal.forward_chaining;

import java.util.Set;

import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.atomset.ReadOnlyAtomSet;
import fr.lirmm.graphik.graal.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.homomorphism.HomomorphismFactoryException;

public class ChaseStopConditionWithHandler implements ChaseStopCondition {

	public ChaseStopConditionWithHandler(ChaseStopCondition c, RuleApplicationHandler h) {
		_realHaltingCondition = c;
		_handler = h;
	}

	public ChaseStopConditionWithHandler(ChaseStopCondition c) {
		_realHaltingCondition = c;
		_handler = RuleApplicationHandler.DEFAULT;
	}

	/**
	 * @param atomSet
	 * @param fixedTerm
	 * @param base
	 * @return
	 * @throws HomomorphismFactoryException
	 * @throws HomomorphismException
	 */
	public boolean canIAdd(ReadOnlyAtomSet atomSet, Set<Term> fixedTerm,
	                       ReadOnlyAtomSet from, ReadOnlyAtomSet base) 
		throws HomomorphismFactoryException, HomomorphismException {
		if (_handler.onRuleApplication(from,atomSet,base)) {
			return _realHaltingCondition.canIAdd(atomSet,fixedTerm,from,base);
		}
		return false;
	}

	public void setHandler(RuleApplicationHandler h) {
		_handler = h;
	}
	public void setRealChaseHaltingCondition(ChaseStopCondition c) {
		_realHaltingCondition = c;
	}

	private ChaseStopCondition      _realHaltingCondition;
	private RuleApplicationHandler  _handler;

};

