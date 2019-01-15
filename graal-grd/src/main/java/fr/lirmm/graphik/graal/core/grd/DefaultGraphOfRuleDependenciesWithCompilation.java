package fr.lirmm.graphik.graal.core.grd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.DefaultDirectedGraph;

import fr.lirmm.graphik.graal.api.core.GraphOfRuleDependencies;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.RuleSet;
import fr.lirmm.graphik.graal.api.core.RulesCompilation;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.core.DefaultRule;
import fr.lirmm.graphik.graal.core.LabelRuleComparator;
import fr.lirmm.graphik.graal.core.atomset.graph.DefaultInMemoryGraphStore;
import fr.lirmm.graphik.graal.core.factory.DefaultConjunctiveQueryFactory;
import fr.lirmm.graphik.graal.core.ruleset.LinkedListRuleSet;
import fr.lirmm.graphik.graal.core.unifier.QueryUnifier;
import fr.lirmm.graphik.graal.core.unifier.UnifierUtils;
import fr.lirmm.graphik.graal.forward_chaining.BasicChase;
import fr.lirmm.graphik.util.graph.scc.StronglyConnectedComponentsGraph;

/**
 * This class computes the GRD of a ruleset when a compilation is used for
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
 * 
 */
public class DefaultGraphOfRuleDependenciesWithCompilation implements GraphOfRuleDependencies {

	private ArrayList<Set<Substitution>> edgesValue;

	/**
	 * ??
	 */
	private boolean computingUnifiers = false;

	// To handle the compilation
	private RuleSet ruleSet;
	private Map<Rule, Rule> ruleToHeadSaturatedRule;
	private RulesCompilation compilation;
	private DirectedGraph<Rule, Integer> grd;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	public DefaultGraphOfRuleDependenciesWithCompilation(RuleSet rulesetToBeAnalyzed, RulesCompilation compilation)
			throws ChaseException, IOException {
		this.ruleSet = rulesetToBeAnalyzed;
		this.compilation = compilation;

		this.grd = new DefaultDirectedGraph<Rule, Integer>(Integer.class);
		this.edgesValue = new ArrayList<Set<Substitution>>();

		for (Rule rule : ruleSet)
			addRuleToGRD(rule);

		initAndSaturateHeads();
		computeDependencies();
	}

	public DefaultGraphOfRuleDependenciesWithCompilation(Iterator<Rule> rulesetToBeAnalyzed,
			RulesCompilation compilation) throws ChaseException, IOException {
		this(new LinkedListRuleSet(rulesetToBeAnalyzed), compilation);
	}

	public DefaultGraphOfRuleDependenciesWithCompilation(Iterable<Rule> rulesetToBeAnalyzed,
			RulesCompilation compilation) throws ChaseException, IOException {
		this(new LinkedListRuleSet(rulesetToBeAnalyzed), compilation);
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

		return this.computeDependency(this.grd.getEdgeSource(e), this.grd.getEdgeTarget(e));
	}

	// /////////////////////////////////////////////////////////////////////////
	// OVERRIDE METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public DefaultGraphOfRuleDependencies getSubGraph(Iterable<Rule> ruleSet) {
		DefaultGraphOfRuleDependencies subGRD = new DefaultGraphOfRuleDependencies(this.computingUnifiers);
		subGRD.addRuleSet(ruleSet);

		for (Integer e : grd.edgeSet()) {

			for (Substitution s : this.edgesValue.get(e)) {
				subGRD.addDependency(grd.getEdgeSource(e), s, grd.getEdgeTarget(e));
			}
		}
		return subGRD;
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
		Integer index = grd.getEdge(src, dest);
		return index != null;
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

	protected void addADependency(Rule src, Set<Substitution> subs, Rule dest) {
		Integer edgeIndex = grd.getEdge(src, dest);

		if (edgeIndex == null) {
			edgeIndex = edgesValue.size();
			edgesValue.add(edgeIndex, subs);
			grd.addEdge(src, dest, edgeIndex);
		} else {
			edgesValue.set(edgeIndex, subs);
		}
	}

	private static final String PREFIX = "R" + new Date().hashCode() + "-";
	private static int ruleIndex = 0;

	protected void addRuleToGRD(Rule r) {

		if (r.getLabel().isEmpty()) {
			r.setLabel(PREFIX + ruleIndex++);
		}
		this.grd.addVertex(r);
	}

	protected void computeDependencies() {

		for (Rule r1 : grd.vertexSet()) {

			for (Rule r2 : grd.vertexSet()) {
				Set<Substitution> unifiers = computeDependency(r1, r2);

				if (!unifiers.isEmpty()) {
					addADependency(r1, unifiers, r2);
				}
			}
		}
	}

	/**
	 * initialize the GRD computation saturates the rule head so as to compensate
	 * the fact that compilable rules are handled differently
	 * 
	 * @throws ChaseException
	 * @throws IOException
	 */
	private final void initAndSaturateHeads() throws ChaseException, IOException {
		ruleToHeadSaturatedRule = new HashMap<Rule, Rule>();

		ArrayList<Rule> compilationSaturation = new ArrayList<>();
		CollectionUtils.addAll(compilationSaturation, compilation.getSaturation());

		for (Rule rule : getRules()) {
			InMemoryAtomSet headAtoms = new DefaultInMemoryGraphStore();
			headAtoms.addAll(rule.getHead());

			BasicChase<InMemoryAtomSet> bchase = new BasicChase<>(compilationSaturation, headAtoms);
			bchase.execute();

			/*
			 * We associate to the current rule a new one wich is $current.body ->
			 * $headAtoms
			 */
			ruleToHeadSaturatedRule.put(rule, new DefaultRule(rule.getBody(), headAtoms));
		}
	}

	protected Set<Substitution> computeDependency(Rule ra, Rule rb) {
		final boolean EXISTS = true;
		Rule saturateda = ruleToHeadSaturatedRule.get(ra);

		List<QueryUnifier> unifiers = UnifierUtils.getSinglePieceUnifiersNAHR(DefaultConjunctiveQueryFactory.instance().create(rb.getBody()), saturateda,
				compilation, EXISTS);
		
		if(!unifiers.isEmpty())
		{
			Substitution substitution = unifiers.get(0).getAssociatedSubstitution();
			Set<Substitution> ret = new HashSet<>();
			ret.add(substitution);
			return ret;
		}
		return Collections.<Substitution>emptySet();
	}
}
