package fr.lirmm.graphik.graal.rulesetanalyser;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.collections4.IterableUtils;

import fr.lirmm.graphik.graal.rulesetanalyser.property.AGRDProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.BTSProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.DisconnectedProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.DomainRestrictedProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.FESProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.FUSProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.FrontierGuardedProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.FrontierOneProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.GBTSProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.GuardedProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.JointlyFrontierGuardedSetProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.LinearProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.MFAProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.MSAProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.RangeRestrictedProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.RuleSetProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.StickyProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.WeaklyAcyclicProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.WeaklyFrontierGuardedSetProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.WeaklyGuardedSetProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.WeaklyStickyProperty;

public class RuleSetPropertyHierarchy {

	private ArrayList<RuleSetProperty> properties;
	private Map<String,Integer> propertyIndex;
	private int currentPtyIndex;

	private boolean specialisations[][];

	public RuleSetPropertyHierarchy() {
		this.setProperties(generatePropertyMap().values());
	}

	public RuleSetPropertyHierarchy(Iterable<RuleSetProperty> pties) {
		this.setProperties(pties);
	}

	public void setProperties(Iterable<RuleSetProperty> pties) {
		this.properties = new ArrayList<RuleSetProperty>();
		this.propertyIndex = new TreeMap<String,Integer>();
		this.currentPtyIndex = 0;

		for (RuleSetProperty p : pties) {
			if (this.propertyIndex.get(p.getLabel()) == null) {
				this.propertyIndex.put(p.getLabel(),new Integer(this.currentPtyIndex++));
				this.properties.add(p);
			}
		}

		this.specialisations = new boolean[this.currentPtyIndex][this.currentPtyIndex];
		this.computeSpecialisations();
	}

	public Iterable<RuleSetProperty> getOrderedProperties() {
		List<RuleSetProperty> result = new LinkedList<RuleSetProperty>();
		boolean spec[][] = new boolean[this.currentPtyIndex][this.currentPtyIndex];
		boolean mark[] = new boolean[this.currentPtyIndex];
		int i,j;
		for (i = 0 ; i < this.currentPtyIndex ; ++i) {
			mark[i] = false;
			for (j = 0 ; j < this.currentPtyIndex ; ++j)
				spec[i][j] = this.specialisations[i][j];
		}

		boolean running = true;
		boolean is_source;
		while (running) {
			is_source = false;
			// find a source
			for (i = 0 ; i < this.currentPtyIndex && !is_source; ++i) {
				if (!mark[i]) {
					is_source = true;
					for (j = 0 ; j < this.currentPtyIndex && is_source ; ++j) {
						if (spec[j][i]) is_source = false;
					}
				}
			}
			--i;
			if (is_source) {
				mark[i] = true;
				// add to list
				result.add(this.properties.get(i));
				// update spec list
				for (j = 0 ; j < this.currentPtyIndex ; ++j)
					spec[i][j] = false;
					//spec[j][i] = false;
			}
			else running = false;
		}

		return result;
	}

	public Iterable<RuleSetProperty> getGeneralisationsOf(RuleSetProperty p) {
		List<RuleSetProperty> result = new LinkedList<RuleSetProperty>();
		int id = this.propertyIndex.get(p.getLabel());
		for (int j = 0 ; j < this.currentPtyIndex ; ++j) {
			if (this.specialisations[id][j])
				result.add(this.properties.get(j));
		}
		return result;
	}

	private void computeSpecialisations() {
		for (int i = 0 ; i < this.currentPtyIndex ; ++i)
			for (int j = 0 ; j < this.currentPtyIndex ; ++j)
				this.specialisations[i][j] = false;

		int id0, id1;
		for (RuleSetProperty p : this.properties) {
			id0 = this.propertyIndex.get(p.getLabel()).intValue();
			for (RuleSetProperty p2 : p.getGeneralisations()) {
				if (this.propertyIndex.get(p2.getLabel()) != null) {
					id1 = this.propertyIndex.get(p2.getLabel()).intValue();
					this.specialisations[id0][id1] = true;
				}
			}
			for (RuleSetProperty p2 : p.getSpecialisations()) {
				if (this.propertyIndex.get(p2.getLabel()) != null) {
					id1 = this.propertyIndex.get(p2.getLabel()).intValue();
					this.specialisations[id1][id0] = true;
				}
			}
		}

		this.computeSpecialisationClosure();
	}

	private void computeSpecialisationClosure() {
		while (computeSpecialisationClosureStep());
	}

	private boolean computeSpecialisationClosureStep() {
		boolean mod = false;
		for (int i = 0 ; i < this.currentPtyIndex ; ++i) {
			for (int j = 0 ; j < this.currentPtyIndex ; ++j) {
				if (this.specialisations[i][j]) {
					for (int k = 0 ; k < this.currentPtyIndex ; ++k) {
						if (this.specialisations[j][k]) {
							if (!this.specialisations[i][k])
								mod = true;
							this.specialisations[i][k] = true;
						}
					}
				}
			}
		}
		return mod;
	}

	/**
	 * Prepare the list of rule set properties. If you have implemented a new
	 * rule set property, and you want an easy way to test it, you are in the
	 * right place. Just add a line that will add an instance of your new class,
	 * compile, and everything will (should) work!
	 * 
	 * @return a Map of RuleSetProperty with their label as key.
	 */
	public static final Map<String, RuleSetProperty> generatePropertyMap() {
		Map<String, RuleSetProperty> propertyMap = new TreeMap<String, RuleSetProperty>();
		addToPropertyMap(propertyMap, AGRDProperty.instance());
		addToPropertyMap(propertyMap, BTSProperty.instance());
		addToPropertyMap(propertyMap, DisconnectedProperty.instance());
		addToPropertyMap(propertyMap, DomainRestrictedProperty.instance());
		addToPropertyMap(propertyMap, FESProperty.instance());
		addToPropertyMap(propertyMap, FrontierGuardedProperty.instance());
		addToPropertyMap(propertyMap, FrontierOneProperty.instance());
		addToPropertyMap(propertyMap, FUSProperty.instance());
		addToPropertyMap(propertyMap, GuardedProperty.instance());
		addToPropertyMap(propertyMap, GBTSProperty.instance());
		addToPropertyMap(propertyMap, JointlyFrontierGuardedSetProperty.instance());
		addToPropertyMap(propertyMap, LinearProperty.instance());
		addToPropertyMap(propertyMap, MFAProperty.instance());
		addToPropertyMap(propertyMap, MSAProperty.instance());
		addToPropertyMap(propertyMap, RangeRestrictedProperty.instance());
		addToPropertyMap(propertyMap, StickyProperty.instance());
		addToPropertyMap(propertyMap, WeaklyAcyclicProperty.instance());
		addToPropertyMap(propertyMap, WeaklyFrontierGuardedSetProperty.instance());
		addToPropertyMap(propertyMap, WeaklyGuardedSetProperty.instance());
		addToPropertyMap(propertyMap, WeaklyStickyProperty.instance());
		return propertyMap;
	}
	
	/**
	 * Prepare a map containing all specialization of specified rule property.
	 * @param prop
	 * @return a map containing all specialization of specified rule property
	 */
	public static final Map<String, RuleSetProperty> generatePropertyMapSpecializationOf(RuleSetProperty prop) {
		Map<String, RuleSetProperty> propertyMap = new TreeMap<String, RuleSetProperty>();
		addToPropertyMap(propertyMap, prop);
		for(Map.Entry<String, RuleSetProperty> e : generatePropertyMap().entrySet()) {
			if(IterableUtils.contains(e.getValue().getGeneralisations(), prop)) {
				propertyMap.put(e.getKey(), e.getValue());
			}
		}
		return propertyMap;
	}

	private static final void addToPropertyMap(Map<String, RuleSetProperty> map, RuleSetProperty p) {
		map.put(p.getLabel(), p);
	}

};

