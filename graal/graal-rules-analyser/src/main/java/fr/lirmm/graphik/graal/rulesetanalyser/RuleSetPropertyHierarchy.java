package fr.lirmm.graphik.graal.rulesetanalyser;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import fr.lirmm.graphik.graal.rulesetanalyser.property.RuleSetProperty;

public class RuleSetPropertyHierarchy {

	private ArrayList<RuleSetProperty> properties;
	private Map<String,Integer> propertyIndex;
	private int currentPtyIndex;

	private boolean specialisations[][];

	public RuleSetPropertyHierarchy() { }
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
		boolean found;
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

};

