package fr.lirmm.graphik.graal.forward_chaining;

import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.Query;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.forward_chaining.DirectRuleApplier;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismWithCompilation;
import fr.lirmm.graphik.graal.core.compilation.IDCompilation;
import fr.lirmm.graphik.graal.forward_chaining.rule_applier.DefaultRuleApplierWithCompilation;

public class BasicChaseWithCompilation<T extends AtomSet> extends BasicChase<T> {

	public BasicChaseWithCompilation(Iterable<Rule> rules, T atomSet, IDCompilation compilation) {
		super(rules, atomSet, new DefaultRuleApplierWithCompilation<T>(compilation));
	}

	public BasicChaseWithCompilation(Iterable<Rule> rules, T atomSet, IDCompilation compilation,
			HomomorphismWithCompilation<? super Query, ? super T> h) {
		super(rules, atomSet, new DefaultRuleApplierWithCompilation<T>(h, compilation));
	}

	public BasicChaseWithCompilation(Iterable<Rule> rules, T atomSet,
			DirectRuleApplier<? super Rule, ? super T> ruleApplier) {
		super(rules, atomSet, ruleApplier);
	}
}