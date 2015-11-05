package fr.lirmm.graphik.graal.rulesetanalyser.property;

import java.util.LinkedList;

import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.rulesetanalyser.util.AnalyserRuleSet;

/**
 * Basic interface for rule set properties.
 * 
 * To implement your own property, you should first check whether it
 * is a local property (meaning that the union of two rule sets
 * satisfying the property, satisfies the property), or not.
 * If it is, you should extend RuleSetProperty.Local, and override
 * the {@code int check(Rule)} method.
 * Otherwise you should extend RuleSetProperty.Default and override
 * the {@code int check(AnalyserRuleSet)} method.
 * An instance of {@code AnalyserRuleSet} provides various data
 * structures used by others rule properties.
 * 
 * Then, you should also override the
 * {@code getSpecialisations()} and {@code getGeneralisations}
 * methods.
 * These allow the analyser to not check all rule properties if a
 * more specific one is already satisfied,
 * and to know if some abstract (i.e., unrecognizable) properties
 * are satisfied (usefull for the 'combine' step).
 * Also, the rule property hierarchy is only computed on the
 * selected properties. Therefore, you should specify all
 * generalisations (even the ones that can be deduced by the
 * transitive closure).
 * 
 * The various check methods must return 1 if the property is
 * satisfied, -1 if it is not, and 0 if you cannot tell.
 * 
 * @see fr.lirmm.graphik.graal.rulesetanalyser.Analyser
 */
public interface RuleSetProperty {

	/**
	 * Check whether the given rule set satisfies the property
	 * or does not.
	 * @return A negative value if the ruleset does not satisfy the property,
	 * a positive value if it does, and 0 if unknown.
	 */
	public int check(AnalyserRuleSet ruleset);

	/**
	 * This method must return a label not used by any other
	 * rule property or undefined behaviour is to be expected.
	 */
	public String getLabel();

	public String getFullName();

	public String getDescription();

	public Iterable<RuleSetProperty> getSpecialisations();
	public Iterable<RuleSetProperty> getGeneralisations();


	public static abstract class Default implements RuleSetProperty {
		@Override
		public int check(AnalyserRuleSet ruleset) {
			return 0;
		}

		@Override
		public Iterable<RuleSetProperty> getSpecialisations() {
			return new LinkedList<RuleSetProperty>();
		}

		@Override
		public Iterable<RuleSetProperty> getGeneralisations() {
			return new LinkedList<RuleSetProperty>();
		}
	};

	/**
	 * Abstract class usefull for implementing local rule set
	 * properties.
	 */
	public static abstract class Local extends RuleSetProperty.Default {
		@Override
		public int check(AnalyserRuleSet ruleset) {
			int tmp;
			int res = 1;
			for (Rule r : ruleset) {
				tmp = check(r);
				if (tmp == 0) res = 0;
				if (tmp < 0) return tmp;
			}
			return res;
		}

		public abstract int check(Rule r);
	};

};

