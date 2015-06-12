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
