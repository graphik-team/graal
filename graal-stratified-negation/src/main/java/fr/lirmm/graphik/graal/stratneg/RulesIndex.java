package fr.lirmm.graphik.graal.stratneg;

import java.util.ArrayList;
import java.util.TreeMap;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CheckReturnValue;
import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.RuleSet;
import fr.lirmm.graphik.graal.core.ruleset.LinkedListRuleSet;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;

@CheckReturnValue
public class RulesIndex {

  private final TreeMap<Predicate, RuleSet> index_ = new TreeMap<>();

  public RulesIndex(Iterable<Rule> rules) {
    index(rules);
  }

  public Iterable<Rule> rules(Iterable<Predicate> predicates) {

    Preconditions.checkNotNull(predicates, "predicates is null");

    ArrayList<Rule> list = new ArrayList<>();

    for (Predicate predicate : predicates) {
      RuleSet rules = index_.get(predicate);
      if (rules != null) {
        for (Rule rule : rules) {
          list.add(rule);
        }
      }
    }
    return list;
  }

  private void index(Iterable<Rule> rules) {

    Preconditions.checkNotNull(rules, "rules is null");

    for (Rule rule : rules) {
      index(rule, rule.getBody());
      index(rule, ((RuleWithNegation) rule).negativeBody());
    }
  }

  private void index(Rule rule, InMemoryAtomSet atomSet) {

    Preconditions.checkNotNull(rule, "rule is null");
    Preconditions.checkNotNull(atomSet, "atomSet is null");

    try (CloseableIteratorWithoutException<Atom> it = atomSet.iterator()) {
      while (it.hasNext()) {
        Atom atom = it.next();
        Predicate predicate = atom.getPredicate();
        RuleSet ruleSet;
        if (index_.containsKey(predicate)) {
          ruleSet = index_.get(predicate);
        } else {
          ruleSet = new LinkedListRuleSet();
          index_.put(predicate, ruleSet);
        }
        ruleSet.add(rule);
      }
    }
  }
}
