package fr.lirmm.graphik.graal.stratneg;

import java.util.LinkedList;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CheckReturnValue;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.Query;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseHaltingCondition;
import fr.lirmm.graphik.graal.api.homomorphism.Homomorphism;
import fr.lirmm.graphik.graal.forward_chaining.halting_condition.RestrictedChaseHaltingCondition;
import fr.lirmm.graphik.graal.forward_chaining.rule_applier.AbstractRuleApplier;

@CheckReturnValue
public class RuleApplierWithNegation<T extends AtomSet> extends AbstractRuleApplier<T> {

  public RuleApplierWithNegation() {
    this(HomomorphismWithNegation.instance());
  }

  public RuleApplierWithNegation(Homomorphism<? super Query, ? super T> homomorphismSolver) {
    this(homomorphismSolver, new RestrictedChaseHaltingCondition());
  }

  public RuleApplierWithNegation(ChaseHaltingCondition haltingCondition) {
    this(HomomorphismWithNegation.instance(), haltingCondition);
  }

  public RuleApplierWithNegation(Homomorphism<? super Query, ? super T> homomorphismSolver,
      ChaseHaltingCondition haltingCondition) {
    super(homomorphismSolver, haltingCondition);
  }

  @Override
  protected ConjunctiveQuery generateQuery(Rule rule) {

    Preconditions.checkNotNull(rule, "rule is null");
    Preconditions.checkArgument(rule instanceof RuleWithNegation,
        "rule is not an instance of RuleWithNegation");

    return new ConjunctiveQueryWithNegation(rule.getBody(),
        ((RuleWithNegation) rule).negativeBody(), new LinkedList<>(rule.getFrontier()));
  }
}
