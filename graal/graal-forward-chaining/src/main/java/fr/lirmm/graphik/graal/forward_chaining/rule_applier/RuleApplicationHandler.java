package fr.lirmm.graphik.graal.forward_chaining.rule_applier;

import fr.lirmm.graphik.graal.core.atomset.AtomSet;

public interface RuleApplicationHandler {

	public boolean onRuleApplication(AtomSet from, AtomSet atomSet, AtomSet base);

	public static RuleApplicationHandler DEFAULT = new RuleApplicationHandler() {
		@Override
		public boolean onRuleApplication(AtomSet from, AtomSet atomSet, AtomSet base) { return true; }
	};

};

