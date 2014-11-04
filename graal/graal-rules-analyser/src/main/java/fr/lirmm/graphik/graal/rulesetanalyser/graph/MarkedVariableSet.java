/**
 * 
 */
package fr.lirmm.graphik.graal.rulesetanalyser.graph;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.rulesetanalyser.util.PredicatePosition;

/**
 * The marked variable set is built from a rule set by the following marking
 * procedure: (i) for each rule Ri and for each variable v occuring in its body,
 * if v does not occur in all atoms of its head, mark (each occurrence of) v in
 * its body; (ii) apply until a fixpoint is reached: for each rule Ri, if a
 * marked variable v appears at position p[k] in its body, then for each rule Rj
 * (including i = j) and for each variable x appearing at position p[k] in the
 * head of Rj, mark each occurence of x in the body of Rj.
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * @author Swan Rocher
 * 
 */
public class MarkedVariableSet {

	private LinkedList<MarkedRule> markedRuleSet;
	private Map<Predicate, LinkedList<MarkedRule>> map;
	private Queue<PredicatePosition> markedPosition;

	public static class MarkedRule {

		public MarkedRule(Rule rule) {
			this.rule = rule;
			this.markedVars = new TreeSet<Term>();
		}

		public Rule rule;
		public Set<Term> markedVars;
	}

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////

	public MarkedVariableSet(Iterable<Rule> rules) {
		this.markedPosition = new LinkedList<PredicatePosition>();
		this.markedRuleSet = new LinkedList<MarkedRule>();
		for (Rule r : rules) {
			this.markedRuleSet.add(new MarkedRule(r));
		}
		map = new HashMap<Predicate, LinkedList<MarkedRule>>();
		process();
	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	public Collection<MarkedRule> getMarkedRuleCollection() {
		return this.markedRuleSet;
	}

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

	private void process() {
		firstStep();
		secondStep();
	}

	/**
	 * for each rule Ri and for each variable v occuring in its body, if v does
	 * not occur in all atoms of its head, mark (each occurrence of) v in its
	 * body;
	 */
	private void firstStep() {
		for (MarkedRule markedRule : this.markedRuleSet) {
			// put rule in the map
			for (Atom atom : markedRule.rule.getHead()) {
				Predicate p = atom.getPredicate();
				LinkedList<MarkedRule> set = map.get(p);
				if (set == null) {
					set = new LinkedList<MarkedRule>();
					map.put(p, set);
				}
				set.add(markedRule);
			}

			// mark the rule
			testRule(markedRule);
		}
	}

	private void testRule(MarkedRule mrule) {
		Set<Term> bodyVars = mrule.rule.getBody().getTerms(Term.Type.VARIABLE);
		for (Term v : bodyVars) {
			for (Atom a : mrule.rule.getHead()) {
				if (!a.getTerms().contains(v)) {
					mark(v, mrule);
				}
			}
		}
	}

	private void mark(Term v, MarkedRule mrule) {
		if (!mrule.markedVars.contains(v)) {
			mrule.markedVars.add(v);
			for (Atom a : mrule.rule.getBody()) {
				int i = 0;
				for (Term t : a) {
					if (v.equals(t)) {
						this.markedPosition.add(new PredicatePosition(a
								.getPredicate(), i));
					}
					++i;
				}
			}
		}
	}

	/**
	 * apply until a fixpoint is reached: for each rule Ri, if a marked variable
	 * v appears at position p[k] in its body, then for each rule Rj (including
	 * i = j) and for each variable x appearing at position p[k] in the head of
	 * Rj, mark each occurence of x in the body of Rj.
	 */
	private void secondStep() {
		LinkedList<MarkedRule> mrList;
		PredicatePosition mpos;
		Term v;
		while (!this.markedPosition.isEmpty()) {
			mpos = this.markedPosition.poll();
			mrList = this.map.get(mpos.predicate);
			if (mrList != null) {
				for (MarkedRule mr : mrList) {
					for (Atom a : mr.rule.getHead()) {
						if (a.getPredicate().equals(mpos.predicate)) {
							v = a.getTerm(mpos.position);
							if (v.getType().equals(Term.Type.VARIABLE)) {
								this.mark(v, mr);
							}
						}
					}
				}
			}
		}
	}
}
