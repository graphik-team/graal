/**
 * 
 */
package fr.lirmm.graphik.graal.forward_chaining;

import fr.lirmm.graphik.graal.forward_chaining.rule_applier.RuleApplier;


/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public abstract class AbstractChase implements Chase {

	private RuleApplier ruleApplier;

	protected AbstractChase(RuleApplier ruleApplier) {
		this.ruleApplier = ruleApplier;
	}

	@Override
	public void execute() throws ChaseException {
		while (this.hasNext())
			this.next();
	}

	protected RuleApplier getRuleApplier() {
		return this.ruleApplier;
	}
};
