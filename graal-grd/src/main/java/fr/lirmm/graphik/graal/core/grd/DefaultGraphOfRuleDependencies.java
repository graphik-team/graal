package fr.lirmm.graphik.graal.core.grd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.DefaultDirectedGraph;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.GraphOfRuleDependencies;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.RuleSet;
import fr.lirmm.graphik.graal.api.core.RulesCompilation;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.unifier.DependencyChecker;
import fr.lirmm.graphik.graal.core.LabelRuleComparator;
import fr.lirmm.graphik.graal.core.Substitutions;
import fr.lirmm.graphik.graal.core.compilation.NoCompilation;
import fr.lirmm.graphik.graal.core.factory.DefaultConjunctiveQueryFactory;
import fr.lirmm.graphik.graal.core.ruleset.IndexedByBodyPredicatesRuleSet;
import fr.lirmm.graphik.graal.core.ruleset.LinkedListRuleSet;
import fr.lirmm.graphik.graal.core.unifier.DefaultUnifierAlgorithm;
import fr.lirmm.graphik.graal.core.unifier.QueryUnifier;
import fr.lirmm.graphik.graal.core.unifier.RuleDependencyUtils;
import fr.lirmm.graphik.graal.core.unifier.UnifierUtils;
import fr.lirmm.graphik.util.graph.scc.StronglyConnectedComponentsGraph;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;
import fr.lirmm.graphik.util.stream.Iterators;

/**
 * This class computes the GRD of a rule set when a compilation is used for
 * saturating the data. The class shall be implemented as a subclass of
 * DefaultGraphOfRuleDependency, but some issues with dependency checking led to
 * the current implementation. In particular, the fact that all pairs of rules
 * are candidate for a dependency when a compilation is available.
 * 
 * In the resulting GRD, the compilation has been used to reestablish the
 * indirect dependencies between rules. Say for instance that R1-->R2-->R3-->R4
 * but R2 and R3 are compiled and thus removed from the ruleset. This class
 * makes sure that the indirect dependency R1-->R4 is present in the GRD.
 * 
 * @author federico
 * @author Olivier Rodriguez
 * 
 */
public class DefaultGraphOfRuleDependencies implements GraphOfRuleDependencies {
	private ArrayList<Set<Substitution>> edgesValue;

	private boolean computingUnifiers;

	private DirectedGraph<Rule, Integer> grd;

	private RulesCompilation compilation;
	private DependencyChecker[] checkers;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	protected DefaultGraphOfRuleDependencies(RuleSet rules, RulesCompilation compilation, boolean withUnifiers, DependencyChecker checkers[]) {
		this.compilation = compilation;
		this.checkers = checkers;
		this.computingUnifiers = withUnifiers;

		this.grd = new DefaultDirectedGraph<Rule, Integer>(Integer.class);
		this.edgesValue = new ArrayList<Set<Substitution>>();

		for (Rule rule : rules)
			addRuleToGRD(rule);

		if (checkers.length > 0)
			computeDependencies(checkers);
		else
			computeDependencies(compilation, checkers);
	}

	// ===

	public DefaultGraphOfRuleDependencies(RuleSet rules, RulesCompilation compilation, boolean withUnifiers) {
		this(rules, compilation, withUnifiers, new DependencyChecker[0]);
	}

	public DefaultGraphOfRuleDependencies(Iterator<Rule> rules, RulesCompilation compilation, boolean withUnifiers) {
		this(new LinkedListRuleSet(rules), compilation, withUnifiers);
	}

	public DefaultGraphOfRuleDependencies(Iterable<Rule> rules, RulesCompilation compilation, boolean withUnifiers) {
		this(new LinkedListRuleSet(rules), compilation, withUnifiers);
	}

	// ===

	public DefaultGraphOfRuleDependencies(RuleSet rules, RulesCompilation compilation) {
		this(rules, compilation, false);
	}

	public DefaultGraphOfRuleDependencies(Iterator<Rule> rules, RulesCompilation compilation) {
		this(new LinkedListRuleSet(rules), compilation);
	}

	public DefaultGraphOfRuleDependencies(Iterable<Rule> rules, RulesCompilation compilation) {
		this(new LinkedListRuleSet(rules), compilation);
	}

	// ===

	public DefaultGraphOfRuleDependencies(RuleSet rules, boolean withUnifiers, DependencyChecker... checkers) {
		this(rules, NoCompilation.instance(), withUnifiers, checkers);
	}

	public DefaultGraphOfRuleDependencies(Iterator<Rule> rules, boolean withUnifiers, DependencyChecker... checkers) {
		this(new LinkedListRuleSet(rules), withUnifiers, checkers);
	}

	public DefaultGraphOfRuleDependencies(Iterable<Rule> rules, boolean withUnifiers, DependencyChecker... checkers) {
		this(new LinkedListRuleSet(rules), withUnifiers, checkers);
	}

	// ===

	public DefaultGraphOfRuleDependencies(RuleSet rules, DependencyChecker... checkers) {
		this(rules, NoCompilation.instance(), false, checkers);
	}

	public DefaultGraphOfRuleDependencies(Iterator<Rule> rules, DependencyChecker... checkers) {
		this(new LinkedListRuleSet(rules), checkers);
	}

	public DefaultGraphOfRuleDependencies(Iterable<Rule> rules, DependencyChecker... checkers) {
		this(new LinkedListRuleSet(rules), checkers);
	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public boolean hasCircuit() {
		return new CycleDetector<Rule, Integer>(this.grd).detectCycles();
	}

	/**
	 * Get the Unifiers of the edge $e
	 * 
	 * @param e The reference edge
	 * @return
	 */
	public Set<Substitution> getUnifiers(Integer e) {

		if (this.computingUnifiers)
			return Collections.unmodifiableSet(this.edgesValue.get(e));

		if (checkers.length > 0)
			return this.computeDependency(this.grd.getEdgeSource(e), this.grd.getEdgeTarget(e), checkers);

		return this.computeDependency(this.grd.getEdgeSource(e), this.grd.getEdgeTarget(e), compilation, checkers);
	}

	// /////////////////////////////////////////////////////////////////////////
	// OVERRIDE METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public DefaultGraphOfRuleDependencies getSubGraph(Iterable<Rule> rules) {
		return new DefaultGraphOfRuleDependencies(new LinkedListRuleSet(rules), this.compilation, this.computingUnifiers, this.checkers);
	}

	@Override
	public Iterable<Rule> getRules() {
		return this.grd.vertexSet();
	}

	@Override
	public Set<Rule> getTriggeredRules(Rule src) {
		Set<Rule> set = new HashSet<Rule>();

		for (Integer i : this.grd.outgoingEdgesOf(src)) {
			set.add(this.grd.getEdgeTarget(i));
		}
		return set;
	}

	@Override
	public Set<Pair<Rule, Substitution>> getTriggeredRulesWithUnifiers(Rule src) {
		Set<Pair<Rule, Substitution>> set = new HashSet<Pair<Rule, Substitution>>();

		for (Integer i : this.grd.outgoingEdgesOf(src)) {
			Rule target = this.grd.getEdgeTarget(i);

			for (Substitution u : this.getUnifiers(i)) {
				Pair<Rule, Substitution> p = new ImmutablePair<Rule, Substitution>(target, u);
				set.add(p);
			}
		}
		return set;
	}

	@Override
	public boolean existUnifier(Rule src, Rule dest) {
		return grd.getEdge(src, dest) != null;
	}

	@Override
	public Set<Substitution> getUnifiers(Rule src, Rule dest) {
		Integer index = grd.getEdge(src, dest);

		if (index != null) {
			return getUnifiers(index);
		} else {
			return Collections.<Substitution>emptySet();
		}
	}

	@Override
	public StronglyConnectedComponentsGraph<Rule> getStronglyConnectedComponentsGraph() {
		return new <Integer>StronglyConnectedComponentsGraph<Rule>(grd);
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		TreeSet<Rule> rules = new TreeSet<Rule>(new LabelRuleComparator());

		for (Rule r : grd.vertexSet()) {
			rules.add(r);
		}

		for (Rule src : rules) {

			for (Integer e : grd.outgoingEdgesOf(src)) {
				Rule dest = grd.getEdgeTarget(e);

				s.append(src.getLabel());
				s.append(" -");

				if (this.computingUnifiers) {

					for (Substitution sub : edgesValue.get(grd.getEdge(src, dest))) {
						s.append(sub);
					}
				}
				s.append("-> ");
				s.append(dest.getLabel());
				s.append('\n');
			}

		}
		return s.toString();
	}

	// /////////////////////////////////////////////////////////////////////////
	// PROTECTED METHODS
	// /////////////////////////////////////////////////////////////////////////

	protected void addDependency(Rule src, Set<Substitution> subs, Rule dest) {
		Integer edgeIndex = grd.getEdge(src, dest);

		if (edgeIndex == null) {
			edgeIndex = edgesValue.size();
			edgesValue.add(edgeIndex, subs);
			grd.addEdge(src, dest, edgeIndex);
		} else {
			edgesValue.get(edgeIndex).addAll(subs);
		}
	}

	protected void addDependency(Rule src, Substitution sub, Rule dest) {
		Set<Substitution> set = new HashSet<>();
		set.add(sub);
		addDependency(src, set, dest);
	}

	private static final String PREFIX = "R" + new Date().hashCode() + "-";
	private static int ruleIndex = 0;

	protected void addRuleToGRD(Rule r) {

		if (r.getLabel().isEmpty()) {
			r.setLabel(PREFIX + ruleIndex++);
		}
		this.grd.addVertex(r);
	}

	protected void addRulesToGRD(Iterable<Rule> rules) {

		for (Rule r : rules) {
			this.addRuleToGRD(r);
		}
	}

	protected void computeDependencies(RulesCompilation compilation, DependencyChecker checkers[]) {
		
		for (Rule r1 : grd.vertexSet()) {

			for (Rule r2 : grd.vertexSet()) {
				Set<Substitution> unifiers = computeDependency(r1, r2, compilation, checkers);

				if (!unifiers.isEmpty()) {
					addDependency(r1, unifiers, r2);
				}
			}
		}
	}

	protected Set<Substitution> computeDependency(Rule ra, Rule rb, RulesCompilation compilation, DependencyChecker checkers[]) {
		/*
		 * We cannot do the existential check if checkers are present
		 */
		final boolean EXISTS = checkers.length == 0;

		List<QueryUnifier> unifiers = UnifierUtils.getSinglePieceUnifiersNAHR(DefaultConjunctiveQueryFactory.instance().create(rb.getBody()), ra, compilation, EXISTS);
		Set<Substitution> ret = new HashSet<>();

		if (unifiers.isEmpty())
			return ret;

		// Nothing to check in the exist case
		if (EXISTS)
			ret.add(unifiers.get(0).getAssociatedSubstitution());
		else {

			for (QueryUnifier qunifier : unifiers) {
				Substitution substitution = qunifier.getAssociatedSubstitution();

				if (RuleDependencyUtils.validateUnifier(ra, rb, substitution, checkers)) {
					ret.add(substitution);
					break;
				}
			}
		}
		return ret;
	}

	// /////////////////////////////////////////////////////////////////////////
	// DEPENDENCY CHECKERS
	// /////////////////////////////////////////////////////////////////////////

	protected Set<Substitution> computeDependency(Rule r1, Rule r2, DependencyChecker checkers[]) {
		Substitution s1 = DefaultUnifierAlgorithm.getSourceVariablesSubstitution();
		Rule source = s1.createImageOf(r1);

		Substitution s2 = DefaultUnifierAlgorithm.getTargetVariablesSubstitution();
		Rule target = s2.createImageOf(r2);
		Set<Substitution> ret = null;

		if (this.computingUnifiers) {
			ret = Iterators.toSet(DefaultUnifierAlgorithm.instance().computePieceUnifier(source, target, checkers));
		} else if (DefaultUnifierAlgorithm.instance().existPieceUnifier(source, target, checkers)) {
			ret = Collections.<Substitution>singleton(Substitutions.emptySubstitution());
		}

		if (ret == null)
			return new HashSet<>();

		return ret;
	}

	protected void computeDependencies(DependencyChecker... checkers) {
		// preprocess
		IndexedByBodyPredicatesRuleSet index = new IndexedByBodyPredicatesRuleSet(this.grd.vertexSet());

		Set<String> marked = new TreeSet<String>();

		for (Rule r1 : this.grd.vertexSet()) {
			marked.clear();
			CloseableIteratorWithoutException<Atom> headAtomsIterator = r1.getHead().iterator();

			while (headAtomsIterator.hasNext()) {
				Atom a = headAtomsIterator.next();
				Iterable<Rule> candidates = index.getRulesByBodyPredicate(a.getPredicate());

				if (candidates == null)
					continue;

				for (Rule r2 : candidates) {

					// Candidate already checked
					if (!marked.add(r2.getLabel()))
						continue;

					Set<Substitution> unifiers = computeDependency(r1, r2, checkers);

					if (unifiers.isEmpty())
						continue;

					addDependency(r1, unifiers, r2);
				}
			}
		}
	}
}
