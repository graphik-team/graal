package fr.lirmm.graphik.graal.forward_chaining;

import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.forward_chaining.DirectRuleApplier;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismWithCompilation;
import fr.lirmm.graphik.graal.core.compilation.IDCompilation;
import fr.lirmm.graphik.graal.forward_chaining.rule_applier.DefaultRuleApplierWithCompilation;

public class BreadthFirstChaseWithCompilation<T extends AtomSet> extends BreadthFirstChase {
	
	public BreadthFirstChaseWithCompilation(Iterable<Rule> rules, T atomSet, IDCompilation compilation) {
		super(rules, atomSet, new DefaultRuleApplierWithCompilation(compilation));
	}

	public BreadthFirstChaseWithCompilation(Iterable<Rule> rules, AtomSet atomSet, IDCompilation compilation,
			HomomorphismWithCompilation<? super ConjunctiveQuery, ? super T> h) {
		super(rules, atomSet, new DefaultRuleApplierWithCompilation(h, compilation));
	}

//	public BreadthFirstChaseWithCompilation(Iterable<Rule> rules, AtomSet atomSet,
//			DirectRuleApplier<? super Rule, ? super T> ruleApplier) {
//		super(rules, atomSet, ruleApplier);
//	}
	
}
