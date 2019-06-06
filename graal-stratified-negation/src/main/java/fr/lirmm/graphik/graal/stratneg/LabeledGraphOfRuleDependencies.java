package fr.lirmm.graphik.graal.stratneg;

import static java.nio.charset.StandardCharsets.UTF_8;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.tuple.Pair;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.cycle.TarjanSimpleCycles;
import org.jgrapht.graph.DefaultDirectedGraph;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.CheckReturnValue;
import com.google.errorprone.annotations.Var;
import fr.lirmm.graphik.graal.api.core.GraphOfRuleDependencies;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.kb.KBBuilder;
import fr.lirmm.graphik.util.graph.scc.StronglyConnectedComponentsGraph;

@CheckReturnValue
public class LabeledGraphOfRuleDependencies implements GraphOfRuleDependencies {

  private final DirectedGraph<Rule, DirectedLabeledEdge> graph_;
  private final Iterable<Rule> rules_;

  private boolean computeCircuits_ = false;
  private List<List<Rule>> circuits_;

  private boolean computeScc_ = false;
  private StronglyConnectedComponentsGraph<Rule> scc_;

  public LabeledGraphOfRuleDependencies(File src) {
    this(readRules(src));
  }

  protected LabeledGraphOfRuleDependencies(Iterable<Rule> rules) {

    Preconditions.checkNotNull(rules, "rules is null");

    graph_ = new DefaultDirectedGraph<>(DirectedLabeledEdge.class);
    rules_ = rules;

    for (Rule rule : rules_) {
      graph_.addVertex(rule);
    }

    computeDependencies();
    hasCircuit();
    scc_ = stronglyConnectedComponentsGraph();
  }

  static private Iterable<Rule> readRules(File file) {

    Preconditions.checkNotNull(file, "file is null");
    Preconditions.checkArgument(file.exists(), "file does not exist");

    KBBuilder kbb = new KBBuilder();

    try (BufferedReader br =
        new BufferedReader(new InputStreamReader(new FileInputStream(file), UTF_8))) {

      @Var
      String row;

      while ((row = br.readLine()) != null) {
        if (row.length() > 0 && row.charAt(0) != '%') {
          kbb.add(Utils.parseRule(row));
        }
      }
    } catch (Exception e) {
      Throwables.getRootCause(e).printStackTrace();
    }
    return kbb.build().getOntology();
  }

  @Override
  public boolean existUnifier(Rule src, Rule dest) {

    Preconditions.checkNotNull(src, "src is null");
    Preconditions.checkNotNull(dest, "dest is null");

    return graph_.getEdge(src, dest) != null;
  }

  @Override
  public Set<Substitution> getUnifiers(Rule src, Rule dest) {
    return null;
  }

  @Override
  public Set<Rule> getTriggeredRules(Rule src) {

    Preconditions.checkNotNull(src, "src is null");

    Set<Rule> set = new HashSet<>();

    for (DirectedLabeledEdge edge : graph_.outgoingEdgesOf(src)) {
      if (edge.getLabel() == '+') {
        set.add(graph_.getEdgeTarget(edge));
      }
    }
    return set;
  }

  @Override
  public Set<Pair<Rule, Substitution>> getTriggeredRulesWithUnifiers(Rule src) {
    return null;
  }

  @Override
  public GraphOfRuleDependencies getSubGraph(Iterable<Rule> rules) {

    Preconditions.checkNotNull(rules, "rules is null");

    LabeledGraphOfRuleDependencies subGrd = new LabeledGraphOfRuleDependencies(rules);

    for (Rule src : rules) {
      for (Rule target : rules) {
        DirectedLabeledEdge edge = graph_.getEdge(src, target);
        if (edge != null) {
          subGrd.addDependency((RuleWithNegation) src, (RuleWithNegation) target, edge.getLabel());
        }
      }
    }
    return subGrd;
  }

  @Override
  public Iterable<Rule> getRules() {
    return rules_;
  }

  @Override
  public StronglyConnectedComponentsGraph<Rule> stronglyConnectedComponentsGraph() {
    if (!computeScc_) {
      scc_ = new StronglyConnectedComponentsGraph<>(graph_);
      computeScc_ = true;
    }
    return scc_;
  }

  @CanIgnoreReturnValue
  @Override
  public boolean hasCircuit() {
    if (!computeCircuits_) {
      circuits_ = new TarjanSimpleCycles<>(graph_).findSimpleCycles();
      computeCircuits_ = true;
    }
    return !circuits_.isEmpty();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("Rules :\n");
    for (Rule rule : rules_) {
      sb.append(rule.toString());
    }
    sb.append("\n");
    return sb.append(graph_.toString()).toString();
  }

  public Set<Rule> getInhibitedRules(Rule src) {

    Preconditions.checkNotNull(src, "src is null");

    Set<Rule> set = new HashSet<>();

    for (DirectedLabeledEdge edge : graph_.outgoingEdgesOf(src)) {
      if (edge.getLabel() == '-') {
        set.add(graph_.getEdgeTarget(edge));
      }
    }
    return set;
  }

  public boolean hasCircuitWithNegativeEdge() {

    if (!computeCircuits_) {
      hasCircuit();
    }
    if (circuits_.isEmpty()) {
      return false;
    }

    for (List<Rule> c : circuits_) {
      if (containsNegativeEdge(c)) {
        return true;
      }
    }
    return false;
  }

  private void computeDependencies() {

    UnifierWithNegationAlgorithm unifier = UnifierWithNegationAlgorithm.instance();
    RulesIndex index = new RulesIndex(rules_);

    for (Rule rule : rules_) {

      RuleWithNegation goal = (RuleWithNegation) rule;
      Iterable<RuleWithNegation> subGoals =
          (Iterable<RuleWithNegation>) (Iterable<?>) index.rules(rule.getHead().getPredicates());

      for (RuleWithNegation subGoal : subGoals) {
        if (!graph_.containsEdge(rule, subGoal)) {

          // Negative Dependency
          if (unifier.existNegativeDependency(goal, subGoal)) {
            graph_.addEdge(goal, subGoal,
                new DirectedLabeledEdge(goal.indice(), subGoal.indice(), '-'));
          }
          // Positive Dependency
          else if (unifier.existPositiveDependency(goal, subGoal)) {
            graph_.addEdge(goal, subGoal,
                new DirectedLabeledEdge(goal.indice(), subGoal.indice(), '+'));
          } else {
            // ERROR
          }
        }
      }
    }
  }

  private boolean containsNegativeEdge(List<Rule> circuit) {

    Preconditions.checkNotNull(circuit, "circuit is null");

    for (int i = 0; i < circuit.size() - 1; i++) {
      for (DirectedLabeledEdge edge : graph_.outgoingEdgesOf(circuit.get(i))) {
        if (edge.getHead() == ((RuleWithNegation) circuit.get(i + 1)).indice()) {
          if (edge.getLabel() == '-') {
            return true;
          }
          break;
        }
      }
    }

    int i = circuit.size() - 1;

    for (DirectedLabeledEdge edge : graph_.outgoingEdgesOf(circuit.get(i))) {
      if (edge.getHead() == ((RuleWithNegation) circuit.get(0)).indice()) {
        if (edge.getLabel() == '-') {
          return true;
        }
        break;
      }
    }
    return false;
  }

  private void addDependency(RuleWithNegation src, RuleWithNegation target, char label) {

    Preconditions.checkNotNull(src, "src is null");
    Preconditions.checkNotNull(target, "target is null");

    graph_.addEdge(src, target, new DirectedLabeledEdge(src.indice(), target.indice(), label));
  }
}
