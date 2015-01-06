/**
 * 
 */
package fr.lirmm.graphik.graal.rulesetanalyser.property;

import java.util.LinkedList;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.rulesetanalyser.graph.GraphPositionDependencies;
import fr.lirmm.graphik.graal.rulesetanalyser.graph.MarkedVariableSet;
import fr.lirmm.graphik.graal.rulesetanalyser.graph.MarkedVariableSet.MarkedRule;
import fr.lirmm.graphik.graal.rulesetanalyser.util.AnalyserRuleSet;

/**
 * This class does not belong to any abstract class defined earlier. It is a
 * generalization of Sticky ({@link StickyProperty}) and Weakly-Acyclic (
 * {@link WeaklyAcyclicProperty}). It relies on the same graph of position
 * dependencies ({@link GraphPositionDependencies}) as the Weakly-Acyclic test
 * and on the same marking procedure as the Sticky test. Property: All marked
 * variables that occur more than once in a rule body appear at some position of
 * finite rank.
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * @author Swan Rocher
 * 
 */
public final class WeaklyStickyProperty implements RuleProperty {

	private static WeaklyStickyProperty instance = null;

	private WeaklyStickyProperty() {
	}

	public static synchronized WeaklyStickyProperty getInstance() {
		if (instance == null) {
			instance = new WeaklyStickyProperty();
		}
		return instance;
	}

	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public Boolean check(Rule rule) {
		LinkedList<Rule> rules = new LinkedList<Rule>();
		rules.add(rule);
		return this.check(rules);

	}

	public Boolean check(Iterable<Rule> rules) {
		GraphPositionDependencies gpd = new GraphPositionDependencies(rules);
		MarkedVariableSet markedVariableSet = new MarkedVariableSet(rules);
		return this.check(markedVariableSet, gpd);
	}

	public Boolean check(AnalyserRuleSet ruleSet) {
		return this.check(ruleSet.getMarkedVariableSet(), ruleSet.getGraphPositionDependencies());
	}
	

	@Override
	public String getLabel() {
		return "ws";
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

	private boolean check(MarkedVariableSet markedVariableSet, GraphPositionDependencies gpd) {
		int nbOccurence;
		int position;
		boolean thereIsAFiniteRank;
		for (MarkedRule mrule : markedVariableSet.getMarkedRuleCollection()) {
			for (Term mvar : mrule.markedVars) {
				nbOccurence = 0;
				thereIsAFiniteRank = false;
				for (Atom a : mrule.rule.getBody()) {
					position = -1;
					for (Term t : a) {
						++position;
						if (mvar.equals(t)) {
							++nbOccurence;
							if (gpd.isFiniteRank(a.getPredicate(), position)) {
								thereIsAFiniteRank = true;
							}
						}
					}
				}
				if (nbOccurence > 1 && !thereIsAFiniteRank) {
					return false;
				}
			}
		}
		return true;
	}
}
