/**
 * 
 */
package fr.lirmm.graphik.graal.rulesetanalyser;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;

import fr.lirmm.graphik.graal.core.Rule;
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

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
public class RuleAnalyser {

	private RuleHierarchyGraph hierarchy;
	private Map<String, Boolean> properties;
	private AnalyserRuleSet rules;

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
				if (isChecked) {
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
	
	public boolean check(RuleProperty property) {
		Boolean isChecked = this.properties.get(property.getLabel());
		if (isChecked == null) {
			isChecked = property.check(this.rules);
			this.properties.put(property.getLabel(), isChecked);
			if (isChecked) {
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
		} else if (FES.getLabel().equals(label)) {
		} else if (FRONTIER_GUARDED.getLabel().equals(label)) {
			list.add(WEAKLY_FRONTIER_GUARDED_SET);
		} else if (FRONTIER_ONE.getLabel().equals(label)) {
			list.add(FRONTIER_GUARDED);
		} else if (FUS.getLabel().equals(label)) {
		} else if (GBTS.getLabel().equals(label)) {
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
		} else if (WEAKLY_STICKY.getLabel().equals(label)) {
		}
		return list;
	}

	/**
	 * 
	 * @return
	 */
	public Collection<RuleProperty> getAllProperty() {
		return this.propertiesList;
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// OBJECT METHODS
	// /////////////////////////////////////////////////////////////////////////

	public String toString() {
		StringBuilder s = new StringBuilder();
		for(Rule rule : this.rules) {
			s.append(rule);
			s.append('\n');
		}
		s.append('\n');
		for(RuleProperty property : this.getAllProperty()) {
			s.append(property.getLabel());
			s.append(": ");
			s.append(this.properties.get(property.getLabel()));
		}
		return s.toString();
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

	private void check(String label) {
		for (RuleProperty property : this.getParentsLabels(label)) {
			this.properties.put(label, true);
			this.check(property.getLabel());
		}
	}
	
}
