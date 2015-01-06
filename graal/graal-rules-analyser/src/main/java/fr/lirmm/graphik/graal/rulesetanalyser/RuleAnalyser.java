/**
 * 
 */
package fr.lirmm.graphik.graal.rulesetanalyser;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.io.output.StringBuilderWriter;
import org.apache.commons.lang3.StringUtils;

import fr.lirmm.graphik.graal.core.LabelRuleComparator;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.grd.GraphOfRuleDependencies;
import fr.lirmm.graphik.graal.io.dlgp.DlgpWriter;
import fr.lirmm.graphik.graal.rulesetanalyser.property.AtomicBodyProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.BTSProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.DisconnectedProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.DomainRestrictedProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.FESProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.FUSProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.FrontierGuardedProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.FrontierOneProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.GBTSProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.GuardedProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.RangeRestrictedProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.RuleProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.StickyProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.WeaklyAcyclicProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.WeaklyFrontierGuardedSetProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.WeaklyGuardedSetProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.WeaklyStickyProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.util.AnalyserRuleSet;
import fr.lirmm.graphik.util.graph.scc.StronglyConnectedComponentsGraph;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
public class RuleAnalyser {

	private static class ComponentCalculabilityValue {
		static final int FES = 1;
		static final int FUS = 2;
		//static final int BTS = 4;
		//static final int UNKNOWN = 0;
	}

	private RuleHierarchyGraph hierarchy;
	private Map<String, Boolean> properties;
	private AnalyserRuleSet rules;

	private StronglyConnectedComponentsGraph<Rule> scc;
	private RuleAnalyser[] ruleAnalyserArray;
	private int[] componentCalculability;

	protected static final AtomicBodyProperty ATOMIC_BODY = AtomicBodyProperty
			.getInstance();
	protected static final BTSProperty BTS = BTSProperty.getInstance();
	protected static final DisconnectedProperty DISCONNECTED = DisconnectedProperty
			.getInstance();
	protected static final DomainRestrictedProperty DOMAIN_RESTRICTED = DomainRestrictedProperty
			.getInstance();
	protected static final FESProperty FES = FESProperty.getInstance();
	protected static final FrontierGuardedProperty FRONTIER_GUARDED = FrontierGuardedProperty
			.getInstance();
	protected static final FrontierOneProperty FRONTIER_ONE = FrontierOneProperty
			.getInstance();
	protected static final FUSProperty FUS = FUSProperty.getInstance();
	protected static final GBTSProperty GBTS = GBTSProperty.getInstance();
	protected static final GuardedProperty GUARDED = GuardedProperty
			.getInstance();
	protected static final RangeRestrictedProperty RANGE_RESTRICTED = RangeRestrictedProperty
			.getInstance();
	protected static final StickyProperty STICKY = StickyProperty.getInstance();
	protected static final WeaklyAcyclicProperty WEAKLY_ACYCLIC = WeaklyAcyclicProperty
			.getInstance();
	protected static final WeaklyFrontierGuardedSetProperty WEAKLY_FRONTIER_GUARDED_SET = WeaklyFrontierGuardedSetProperty
			.getInstance();
	protected static final WeaklyGuardedSetProperty WEAKLY_GUARDED_SET = WeaklyGuardedSetProperty
			.getInstance();
	protected static final WeaklyStickyProperty WEAKLY_STICKY = WeaklyStickyProperty
			.getInstance();

	protected Collection<RuleProperty> propertiesList;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////

	public RuleAnalyser(Iterable<Rule> rules) {
		this.rules = new AnalyserRuleSet(rules);
		this.properties = new TreeMap<String, Boolean>();
		this.hierarchy = new RuleHierarchyGraph();
		this.initPropertiesList();

		for (RuleProperty property : this.getAllProperty()) {
			this.hierarchy.add(property);
		}
		for (RuleProperty property : this.getAllProperty()) {
			for (RuleProperty parentProperty : this.getParentsLabels(property
					.getLabel())) {
				this.hierarchy.addParent(property.getLabel(),
						parentProperty.getLabel());
			}
		}
	}

	public RuleAnalyser(GraphOfRuleDependencies grd) {
		this(grd.getRules());
		this.rules.setGraphOfRuleDependencies(grd);
	}

	private void initPropertiesList() {
		propertiesList = new LinkedList<RuleProperty>();
		propertiesList.add(ATOMIC_BODY);
		propertiesList.add(BTS);
		propertiesList.add(DISCONNECTED);
		propertiesList.add(DOMAIN_RESTRICTED);
		propertiesList.add(FES);
		propertiesList.add(FRONTIER_GUARDED);
		propertiesList.add(FRONTIER_ONE);
		propertiesList.add(FUS);
		propertiesList.add(GBTS);
		propertiesList.add(GUARDED);
		propertiesList.add(RANGE_RESTRICTED);
		propertiesList.add(STICKY);
		propertiesList.add(WEAKLY_ACYCLIC);
		propertiesList.add(WEAKLY_FRONTIER_GUARDED_SET);
		propertiesList.add(WEAKLY_GUARDED_SET);
		propertiesList.add(WEAKLY_STICKY);
	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * @return return true if a proof of decidability was found, false otherwise.
	 */
	public boolean isDecidable() {
		if (scc == null) {
			this.scc = this.getStronglyConnectedComponentsGraph();

			int nbrScc = scc.getNbrComponents();
			ruleAnalyserArray = new RuleAnalyser[nbrScc];
			componentCalculability = new int[nbrScc];

			for (int c : scc.getVertices()) {
				RuleAnalyser subRA = this.getSubRuleAnalyser(scc
						.getComponent(c));
				ruleAnalyserArray[c] = subRA;
				subRA.checkAll();
			}

			// combine
			computeFESComponent();
			computeFUSComponent();
		}

		// result
		for (int i = 0; i < componentCalculability.length; ++i) {
			if (componentCalculability[i] == 0)
				return false;
		}
		return true;
	}

	/**
	 * 
	 * @return an array with index of strongly connected components (SCC) as
	 *         index and 1 if the component have to be compute with forward
	 *         chaining algorithm and 2 if it have to be compute with backward
	 *         chaining algorithm.
	 */
	public int[] getCombineWithFESPriority() {
		int[] res = new int[componentCalculability.length];
		for (int i = 0; i < componentCalculability.length; ++i) {

			if ((componentCalculability[i] & ComponentCalculabilityValue.FES) != 0)
				res[i] = ComponentCalculabilityValue.FES;
			else if ((componentCalculability[i] & ComponentCalculabilityValue.FUS) != 0)
				res[i] = ComponentCalculabilityValue.FUS;
		}
		return res;
	}

	/**
	 * 
	 * @return an array with index of strongly connected components (SCC) as
	 *         index and 1 if the component have to be compute with forward
	 *         chaining algorithm and 2 if it have to be compute with backward
	 *         chaining algorithm.
	 */
	public int[] getCombineWithFUSPriority() {
		int[] res = new int[componentCalculability.length];
		for (int i = 0; i < componentCalculability.length; ++i) {
			if ((componentCalculability[i] & ComponentCalculabilityValue.FUS) != 0)
				res[i] = ComponentCalculabilityValue.FUS;
			else if ((componentCalculability[i] & ComponentCalculabilityValue.FES) != 0)
				res[i] = ComponentCalculabilityValue.FES;
		}
		return res;
	}

	/**
	 * Compute and memorize all properties satisfaction. If you need to check
	 * major parts of properties, it is recommended to call this method first in
	 * order to optimize call with the graph of property dependencies.
	 */
	public void checkAll() {
		Queue<RuleProperty> queue = new LinkedList<RuleProperty>();
		for (RuleProperty property : this.hierarchy.getSources()) {
			queue.add(property);
		}

		RuleProperty property;
		while (!queue.isEmpty()) {
			property = queue.poll();
			Boolean isChecked = this.properties.get(property.getLabel());
			if (isChecked == null) {
				isChecked = property.check(this.rules);
				this.properties.put(property.getLabel(), isChecked);
				if (isChecked != null && isChecked) {
					this.check(property.getLabel());
				} else {
					for (RuleProperty p : this.getParentsLabels(property
							.getLabel())) {
						queue.add(p);
					}
				}
			}
		}
	}

	/**
	 * Check one property.
	 * 
	 * @param property
	 * @return warning, this method can return null value if the validity of its
	 *         property is unknown.
	 */
	public Boolean check(RuleProperty property) {
		Boolean isChecked = this.properties.get(property.getLabel());
		if (isChecked == null) {
			isChecked = property.check(this.rules);
			this.properties.put(property.getLabel(), isChecked);
			if (isChecked != null && isChecked) {
				this.check(property.getLabel());
			}
		}
		return isChecked;
	}

	/**
	 * 
	 * @param label
	 * @return
	 */
	public Collection<RuleProperty> getParentsLabels(String label) {
		Collection<RuleProperty> list = new LinkedList<RuleProperty>();
		if (ATOMIC_BODY.getLabel().equals(label)) {
			list.add(FUS);
			list.add(GUARDED);
		} else if (BTS.getLabel().equals(label)) {
		} else if (DISCONNECTED.getLabel().equals(label)) {
			list.add(DOMAIN_RESTRICTED);
			list.add(FRONTIER_GUARDED);
			list.add(WEAKLY_ACYCLIC);
		} else if (DOMAIN_RESTRICTED.getLabel().equals(label)) {
			list.add(FUS);
		} /*
		 * else if (FES.getLabel().equals(label)) { }
		 */else if (FRONTIER_GUARDED.getLabel().equals(label)) {
			list.add(WEAKLY_FRONTIER_GUARDED_SET);
		} else if (FRONTIER_ONE.getLabel().equals(label)) {
			list.add(FRONTIER_GUARDED);
		} /*
		 * else if (FUS.getLabel().equals(label)) { }
		 */else if (GBTS.getLabel().equals(label)) {
			list.add(BTS);
		} else if (GUARDED.getLabel().equals(label)) {
			list.add(FRONTIER_GUARDED);
			list.add(WEAKLY_GUARDED_SET);
		} else if (RANGE_RESTRICTED.getLabel().equals(label)) {
			list.add(WEAKLY_ACYCLIC);
			list.add(WEAKLY_GUARDED_SET);
		} else if (STICKY.getLabel().equals(label)) {
			list.add(WEAKLY_STICKY);
			list.add(FUS);
		} else if (WEAKLY_ACYCLIC.getLabel().equals(label)) {
			list.add(WEAKLY_STICKY);
			list.add(FES);
		} else if (WEAKLY_FRONTIER_GUARDED_SET.getLabel().equals(label)) {
			list.add(GBTS);
		} else if (WEAKLY_GUARDED_SET.getLabel().equals(label)) {
			list.add(WEAKLY_FRONTIER_GUARDED_SET);
		} /*
		 * else if (WEAKLY_STICKY.getLabel().equals(label)) { }
		 */
		return list;
	}

	/**
	 * 
	 * @return
	 */
	public final Collection<RuleProperty> getAllProperty() {
		return this.propertiesList;
	}

	public StronglyConnectedComponentsGraph<Rule> getStronglyConnectedComponentsGraph() {
		return this.rules.getStronglyConnectedComponentsGraph();
	}

	/**
	 * @param component
	 */
	public RuleAnalyser getSubRuleAnalyser(Iterable<Rule> component) {
		return new RuleAnalyser(this.rules.getSubRuleSetAnalyser(component));
	}

	// /////////////////////////////////////////////////////////////////////////
	// OBJECT METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		this.toString(sb);
		return sb.toString();
	}

	public void toString(StringBuilder sb) {
		StronglyConnectedComponentsGraph<Rule> scc = this
				.getStronglyConnectedComponentsGraph();
		this.checkAll();

		TreeSet<Rule> sortedRules = new TreeSet<Rule>(new LabelRuleComparator());
		for (Rule r : this.rules) {
			sortedRules.add(r);
		}
		printRules(sortedRules, sb);

		sb.append("\n\nGraph of Rule Dependencies\n");
		sb.append("==========================\n");
		sb.append(this.rules.getGraphOfRuleDependencies().toString());

		sb.append("\n\nStrongly Connected Components in the GRD\n");
		sb.append("========================================\n");
		for (int v : scc.getVertices()) {
			sb.append("C" + v + " = {");
			boolean isFirst = true;
			for (Rule r : scc.getComponent(v)) {
				if (!isFirst)
					sb.append(", ");
				sb.append(r.getLabel());
				isFirst = false;
			}
			sb.append("}\n");

		}

		sb.append("\n\nGraph of Strongly Connected Components\n");
		sb.append("======================================\n");
		for (int src = 0; src < scc.getNbrComponents(); ++src) {
			for (int target : scc.getOutbound(src)) {
				sb.append("C" + src + " ---> C" + target);
				sb.append('\n');
			}
		}

		sb.append("\n\nRecognized Rule Classes\n");
		sb.append("=======================\n");
		int cellSize = 6;
		sb.append(StringUtils.center(StringUtils.left("C", cellSize), cellSize));
		for (RuleProperty rp : this.getAllProperty()) {
			String pString = StringUtils.center(
					StringUtils.left(rp.getLabel(), cellSize), cellSize);
			sb.append("|" + pString);
		}
		sb.append("|\n");

		sb.append(StringUtils.center("", (cellSize + 1)
				* (this.getAllProperty().size() + 1), '-'));
		sb.append('\n');

		for (int c : scc.getVertices()) {
			RuleAnalyser subRA = this.getSubRuleAnalyser(scc.getComponent(c));
			subRA.checkAll();

			sb.append(StringUtils.center(StringUtils.left("C" + c, cellSize),
					cellSize));
			for (RuleProperty rp : this.getAllProperty()) {
				sb.append("|");
				Boolean b = subRA.check(rp);
				if (b == null) {
					sb.append(StringUtils.center("?", cellSize));
				} else if (b) {
					sb.append(StringUtils.center("X", cellSize));
				} else {
					sb.append(StringUtils.center(" ", cellSize));
				}
			}
			sb.append("|\n");
		}

		sb.append(StringUtils.center("", (cellSize + 1)
				* (this.getAllProperty().size() + 1), '-'));
		sb.append("\n");
		// Recognized classes for the whole ruleset
		sb.append(StringUtils.center(StringUtils.left("KB", cellSize), cellSize));

		for (RuleProperty rp : this.getAllProperty()) {
			sb.append("|");
			Boolean b = this.check(rp);
			if (b == null) {
				sb.append(StringUtils.center("?", cellSize));
			} else if (b) {
				sb.append(StringUtils.center("X", cellSize));
			} else {
				sb.append(StringUtils.center(" ", cellSize));
			}
		}
		sb.append("|\n");

		sb.append(StringUtils.center("", (cellSize + 1)
				* (this.getAllProperty().size() + 1), '-'));
		sb.append("\n");

		sb.append(StringUtils.center(StringUtils.left("C", cellSize), cellSize));
		for (RuleProperty rp : this.getAllProperty()) {
			String pString = StringUtils.center(
					StringUtils.left(rp.getLabel(), cellSize), cellSize);
			sb.append("|" + pString);
		}
		sb.append("|\n");

		// /////////////////////////////////////////////////////////////////////
		// display combine
		sb.append("\n\nCombined Algorithms FES/FUS/BTS\n");
		sb.append("===============================\n");

		if (this.isDecidable()) {
			sb.append("Decidable combination found\n");
		} else {
			sb.append("Decidable combination not found\n");
		}

		sb.append("\n\nPriority: FES\n");
		sb.append("-------------\n");
		int[] combination = this.getCombineWithFESPriority();
		printDecidableCombination(combination, sb);

		sb.append("\n\nPriority: FUS\n");
		sb.append("-------------\n");
		combination = this.getCombineWithFUSPriority();
		printDecidableCombination(combination, sb);

	}

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

	private void check(String label) {
		this.properties.put(label, true);
		for (RuleProperty property : this.getParentsLabels(label)) {
			this.check(property.getLabel());
		}
	}

	/**
	 * @param scc
	 * @param layers
	 * @param componentCalculability
	 * @return
	 */
	private void computeFESComponent() {
		int[] layers = scc.computeLayers(scc.getSources(), true);

		boolean[] mark = new boolean[scc.getNbrComponents()];

		Queue<Integer> queue = new LinkedList<Integer>();

		// init
		for (int c : scc.getSources()) {
			mark[c] = true;
			queue.add(c);
		}

		// process
		while (!queue.isEmpty()) {
			int c = queue.poll();
			boolean predFES = true;
			for (int pred : scc.getInbound(c)) {
				if ((componentCalculability[pred] & ComponentCalculabilityValue.FES) == 0) {
					predFES = false;
				}
			}
			Boolean bool = ruleAnalyserArray[c]
					.check(FESProperty.getInstance());
			if (bool != null && bool && predFES) {
				componentCalculability[c] += ComponentCalculabilityValue.FES;
				for (int succ : scc.getOutbound(c)) {
					if (!mark[succ] && layers[c] + 1 == layers[succ]) {
						mark[succ] = true;
						queue.add(succ);
					}
				}
			}
		}
	}

	/**
	 * @param scc
	 * @param layers
	 * @param componentCalculability
	 * @return
	 */
	private void computeFUSComponent() {
		int[] layers = scc.computeLayers(scc.getSinks(), false);

		boolean[] mark = new boolean[scc.getNbrComponents()];
		Queue<Integer> queue = new LinkedList<Integer>();

		// init
		for (int c : scc.getSinks()) {
			mark[c] = true;
			queue.add(c);
		}

		// process
		while (!queue.isEmpty()) {
			int c = queue.poll();
			boolean succFUS = true;
			for (int succ : scc.getOutbound(c)) {
				if ((componentCalculability[succ] & ComponentCalculabilityValue.FUS) == 0) {
					succFUS = false;
				}
			}
			Boolean bool = ruleAnalyserArray[c]
					.check(FUSProperty.getInstance());
			if (bool != null && bool && succFUS) {
				componentCalculability[c] += ComponentCalculabilityValue.FUS;
				for (int pred : scc.getInbound(c)) {
					if (!mark[pred] && layers[c] + 1 == layers[pred]) {
						mark[pred] = true;
						queue.add(pred);
					}
				}
			}
		}
	}

	private static void printDecidableCombination(int[] combination,
			StringBuilder sb) {
		for (int i = 0; i < combination.length; ++i) {
			if (combination[i] == 1) {
				sb.append("C" + i + "    " + "FES\n");
			} else if (combination[i] == 2) {
				sb.append("C" + i + "    " + "FUS\n");
			} else if (combination[i] == 4) {
				sb.append("C" + i + "    " + "BTS\n");
			} else {
				sb.append("C" + i);
			}
		}
	}

	private void printRules(Iterable<Rule> rules, StringBuilder sb) {
		DlgpWriter writer = new DlgpWriter(new StringBuilderWriter(sb));
		sb.append("\n\nInput Rule Base\n");
		sb.append("===============\n\n");

		for (Rule r : rules) {
			try {
				writer.write(r);
			} catch (IOException e) {
			}
		}
	}

}
