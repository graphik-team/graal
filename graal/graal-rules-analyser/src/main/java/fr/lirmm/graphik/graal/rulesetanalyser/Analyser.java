package fr.lirmm.graphik.graal.rulesetanalyser;

import java.util.Map;
import java.util.TreeMap;
import java.util.List;
import java.util.LinkedList;

import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.rulesetanalyser.util.AnalyserRuleSet;
import fr.lirmm.graphik.graal.rulesetanalyser.property.RuleSetProperty;

public class Analyser {

	private AnalyserRuleSet             ruleSet;
	private RuleSetPropertyHierarchy    hierarchy;

	public Analyser() { }

	public void setRuleSet(AnalyserRuleSet rules) {
		this.ruleSet = rules;
	}
	public void setRuleSet(Iterable<Rule> rules) {
		this.ruleSet = new AnalyserRuleSet(rules);
	}

	public void setProperties(RuleSetPropertyHierarchy h) {
		this.hierarchy = h;
	}
	public void setProperties(Iterable<RuleSetProperty> pties) {
		this.hierarchy = new RuleSetPropertyHierarchy(pties);
	}

	/**
	 * @return true only if some property ensures the rule set is decidable
	 */
	public boolean isDecidable() {
		return false;
	}

	public Map<String,Integer> ruleSetProperties() {
		return computeProperties(this.ruleSet);
	}

	public List<Map<String,Integer>> sccProperties() {
		List<Map<String,Integer>> result = new LinkedList<Map<String,Integer>>();
		for (AnalyserRuleSet subAnalyser : this.ruleSet.getSCC()) {
			result.add(computeProperties(subAnalyser));
		}
		return result;
	}

	protected Map<String,Integer> computeProperties(AnalyserRuleSet set) {
		Map<String, Integer> result = new TreeMap<String, Integer>();
		Iterable<RuleSetProperty> pties = this.hierarchy.getOrderedProperties();
		int res;
		for (RuleSetProperty p : pties) {
			if (result.get(p.getLabel()) == null) {
				res = p.check(set);
				result.put(p.getLabel(), new Integer(res));
				if (res > 0) {
					for (RuleSetProperty p2 : this.hierarchy.getGeneralisationsOf(p))
						result.put(p2.getLabel(), new Integer(res));
				}
			}
		}
		return result;
	}

};

