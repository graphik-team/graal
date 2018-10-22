package fr.lirmm.graphik.graal.core.grd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.DefaultDirectedGraph;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.GraphOfRuleDependencies;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.RuleSet;
import fr.lirmm.graphik.graal.api.core.RulesCompilation;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.unifier.DependencyChecker;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.core.DefaultRule;
import fr.lirmm.graphik.graal.core.LabelRuleComparator;
import fr.lirmm.graphik.graal.core.Substitutions;
import fr.lirmm.graphik.graal.core.atomset.AtomSetUtils;
import fr.lirmm.graphik.graal.core.atomset.graph.DefaultInMemoryGraphStore;
import fr.lirmm.graphik.graal.core.mapper.RDFTypeMapper;
import fr.lirmm.graphik.graal.core.ruleset.LinkedListRuleSet;
import fr.lirmm.graphik.graal.core.unifier.DefaultUnifierAlgorithm;
import fr.lirmm.graphik.graal.forward_chaining.BasicChase;
import fr.lirmm.graphik.util.LinkedSet;
import fr.lirmm.graphik.util.graph.scc.StronglyConnectedComponentsGraph;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;

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
	private DependencyChecker[] checkerArray;
	private boolean computingUnifiers;

	// To handle the compilation
	private RuleSet ruleSet;
	private Map<Rule, Rule> ruleToHeadSaturatedRule;
	private RulesCompilation compilation;
	private boolean init = false;
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

		Iterator<Rule> rulesetIterator = this.ruleSet.iterator();
		while (rulesetIterator.hasNext()) {
			this.addRule(rulesetIterator.next());
		}

		initAndSaturateHeads();
		this.computeDependencies();

	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	public boolean hasCircuit() {
		CycleDetector<Rule, Integer> cycle = new CycleDetector<Rule, Integer>(this.grd);
		return cycle.detectCycles();
	}

	public Set<Substitution> getUnifiers(Integer e) {
		if (this.computingUnifiers) {
			return Collections.unmodifiableSet(this.edgesValue.get(e));
		} else {
			return this.computeDependency(this.grd.getEdgeSource(e), this.grd.getEdgeTarget(e));
		}
	}

//	public DefaultGraphOfRuleDependenciesWithCompilation getSubGraph(Iterable<Rule> ruleSet) {
//		DefaultGraphOfRuleDependenciesWithCompilation subGRD = null;
//		try {
//			subGRD = new DefaultGraphOfRuleDependenciesWithCompilation(this.ruleSet, this.compilation);
//		} catch (ChaseException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		subGRD.addRuleSet(ruleSet);
//		for (Rule src : ruleSet) {
//			for (Rule target : ruleSet) {
//				Integer e = this.grd.getEdge(src, target);
//				if (e != null) { // there is an edge
//					for (Substitution s : this.edgesValue.get(e)) {
//						subGRD.addDependency(src, s, target);
//					}
//				}
//			}
//		}
//		return subGRD;
//	}
	
	@Override
	public DefaultGraphOfRuleDependencies getSubGraph(Iterable<Rule> ruleSet) {
		DefaultGraphOfRuleDependencies subGRD = new DefaultGraphOfRuleDependencies(this.computingUnifiers);
		subGRD.addRuleSet(ruleSet);
		for (Rule src : ruleSet) {
			for (Rule target : ruleSet) {
				Integer e = this.grd.getEdge(src, target);
				if (e != null) { // there is an edge
					for (Substitution s : this.edgesValue.get(e)) {
						subGRD.addDependency(src, s, target);
					}
				}
			}
		}
		return subGRD;
	}

	public Iterable<Rule> getRules() {
		return this.grd.vertexSet();
	}

	public Iterable<Integer> getOutgoingEdgesOf(Rule src) {
		return this.grd.outgoingEdgesOf(src);
	}

	public Set<Rule> getTriggeredRules(Rule src) {
		Set<Rule> set = new HashSet<Rule>();
		for (Integer i : this.grd.outgoingEdgesOf(src)) {
			set.add(this.grd.getEdgeTarget(i));
		}
		return set;
	}

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

	public Rule getEdgeTarget(Integer i) {
		return this.grd.getEdgeTarget(i);
	}

	public boolean existUnifier(Rule src, Rule dest) {
		Integer index = this.grd.getEdge(src, dest);
		return index != null;
	}

	public Set<Substitution> getUnifiers(Rule src, Rule dest) {
		Integer index = this.grd.getEdge(src, dest);
		if (index != null) {
			return this.getUnifiers(index);
		} else {
			return Collections.<Substitution> emptySet();
		}
	}

	public StronglyConnectedComponentsGraph<Rule> getStronglyConnectedComponentsGraph() {
		return new<Integer> StronglyConnectedComponentsGraph<Rule>(this.grd);
	}

	// /////////////////////////////////////////////////////////////////////////
	// OVERRIDE METHODS

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		TreeSet<Rule> rules = new TreeSet<Rule>(new LabelRuleComparator());
		for (Rule r : this.grd.vertexSet()) {
			rules.add(r);
		}
		for (Rule src : rules) {
			for (Integer e : this.grd.outgoingEdgesOf(src)) {
				Rule dest = this.grd.getEdgeTarget(e);

				s.append(src.getLabel());
				s.append(" -");
				if (this.computingUnifiers) {
					for (Substitution sub : this.edgesValue.get(this.grd.getEdge(src, dest))) {
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

	protected void addDependency(Rule src, Substitution sub, Rule dest) {
		Integer edgeIndex = this.grd.getEdge(src, dest);
		Set<Substitution> edge = null;
		if (edgeIndex != null) {
			edge = this.edgesValue.get(edgeIndex);
		} else {
			edgeIndex = this.edgesValue.size();
			edge = new LinkedSet<Substitution>();
			this.edgesValue.add(edgeIndex, edge);
			this.grd.addEdge(src, dest, edgeIndex);
		}
		edge.add(sub);
	}

	protected void setDependency(Rule src, Set<Substitution> subs, Rule dest) {
		Integer edgeIndex = this.grd.getEdge(src, dest);
		if (edgeIndex == null) {
			edgeIndex = this.edgesValue.size();
			this.edgesValue.add(edgeIndex, subs);
			this.grd.addEdge(src, dest, edgeIndex);
		} else {
			this.edgesValue.set(edgeIndex, subs);
		}
	}

	private static final String PREFIX = "R" + new Date().hashCode() + "-";
	private static int ruleIndex = -1;

	protected void addRule(Rule r) {
		if (r.getLabel().isEmpty()) {
			r.setLabel(PREFIX + ++ruleIndex);
		}
		this.grd.addVertex(r);
	}

	protected void addRuleSet(Iterable<Rule> ruleSet) {
		for (Rule r : ruleSet) {
			this.addRule(r);
		}
	}

	protected void computeDependencies(DependencyChecker... checkers) {

		Iterable<Rule> candidates = this.grd.vertexSet();
		Set<String> marked = new TreeSet<String>();
		for (Rule r1 : this.grd.vertexSet()) {
			marked.clear();
			CloseableIteratorWithoutException<Atom> it = r1.getHead().iterator();
			while (it.hasNext()) {
				Atom a = it.next();
				if (candidates != null) {
					for (Rule r2 : candidates) {
						if (marked.add(r2.getLabel())) {
			
							Set<Substitution> unifiers = computeDependency(r1, r2);
							if (!unifiers.isEmpty()) {
								this.setDependency(r1, unifiers, r2);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * initialize the GRD computation saturates the rule head so as to
	 * compensate the fact that compilable rules are handled differently
	 * 
	 * @throws ChaseException
	 * @throws IOException
	 */
	private final void initAndSaturateHeads() throws ChaseException, IOException {
		init = true;

		ruleToHeadSaturatedRule = new HashMap<Rule, Rule>();

		Iterator<Rule> it = this.getRules().iterator();

		while (it.hasNext()) {
			Rule rule = it.next();

			// System.out.println("\n\nRULE\n" + rule.toString());

			InMemoryAtomSet headAtoms = new DefaultInMemoryGraphStore();


			headAtoms.addAll(rule.getHead());
		
			BasicChase bchase = new BasicChase(this.compilation.getSaturation(), headAtoms);

			bchase.execute();

			Rule newRule = new DefaultRule(rule.getBody(), headAtoms);

			this.ruleToHeadSaturatedRule.put(rule, newRule);

		}
	}

	protected Set<Substitution> computeDependency(Rule r1, Rule r2) {

		Rule saturatedR1 = this.ruleToHeadSaturatedRule.get(r1);
		
		
		RDFTypeMapper rdfTypeMapper = new RDFTypeMapper();
		
		List<Rule> rulesToUnmap = new LinkedList<Rule>();
		rulesToUnmap.add(saturatedR1);
		rulesToUnmap.add(r2);
		
		List<Rule> rules = rulesToUnmap.stream()
				.map(rule -> rdfTypeMapper.unmap(rule))
				.collect(Collectors.toList());

		saturatedR1 = rules.get(0);
		r2 = rules.get(1);
		
		
		Substitution s1 = DefaultUnifierAlgorithm.getSourceVariablesSubstitution();
		Rule source = s1.createImageOf(saturatedR1);

		Substitution s2 = DefaultUnifierAlgorithm.getTargetVariablesSubstitution();
		Rule target = s2.createImageOf(r2);

		if (DefaultUnifierAlgorithm.instance().existPieceUnifier(source, target)) {
			return Collections.<Substitution> singleton(Substitutions.emptySubstitution());
		}
		return Collections.<Substitution> emptySet();
	}

}
