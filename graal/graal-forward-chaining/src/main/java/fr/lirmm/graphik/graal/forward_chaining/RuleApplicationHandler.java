package fr.lirmm.graphik.graal.forward_chaining;

import fr.lirmm.graphik.graal.core.atomset.ReadOnlyAtomSet;

public interface RuleApplicationHandler {

	public boolean onRuleApplication(ReadOnlyAtomSet from, ReadOnlyAtomSet atomSet);

	public static RuleApplicationHandler DEFAULT = new RuleApplicationHandler() {
		@Override
		public boolean onRuleApplication(ReadOnlyAtomSet from, ReadOnlyAtomSet atomSet) { return true; }
	};

};

