package fr.lirmm.graphik.graal.forward_chaining;

import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.Query;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.forward_chaining.RuleApplier;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismWithCompilation;
import fr.lirmm.graphik.graal.core.compilation.IDCompilation;
import fr.lirmm.graphik.graal.forward_chaining.rule_applier.DefaultRuleApplierWithCompilation;

public class BreadthFirstChaseWithCompilation extends BreadthFirstChase {

	public BreadthFirstChaseWithCompilation(Iterable<Rule> rules, AtomSet atomSet, IDCompilation compilation) {
		super(rules, atomSet, new DefaultRuleApplierWithCompilation<AtomSet>(compilation));
	}

	public BreadthFirstChaseWithCompilation(Iterable<Rule> rules, AtomSet atomSet, IDCompilation compilation,
			HomomorphismWithCompilation<Query, AtomSet> h) {
		super(rules, atomSet, new DefaultRuleApplierWithCompilation<AtomSet>(h, compilation));
	}

	public BreadthFirstChaseWithCompilation(Iterable<Rule> rules, AtomSet atomSet,
			RuleApplier<Rule, AtomSet> ruleApplier) {
		super(rules, atomSet, ruleApplier);
	}
}