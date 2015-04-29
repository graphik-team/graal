/**
 * 
 */
package fr.lirmm.graphik.graal.io.owl.logic;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.DefaultRule;
import fr.lirmm.graphik.graal.core.NegativeConstraint;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.term.Term;

/**
 * use Translator pattern
 * @author clement
 *
 */
public final class LogicalFormulaRuleTranslator {

	private static LogicalFormulaRuleTranslator instance;

	private LogicalFormulaRuleTranslator() {
	}

	public static synchronized LogicalFormulaRuleTranslator getInstance() {
		if (instance == null)
			instance = new LogicalFormulaRuleTranslator();

		return instance;
	}
	
	public Iterable<Rule> translate(LogicalFormula f) {
		Collection<Rule> ruleList = new LinkedList<Rule>();
		for(Collection<Literal> clause : f) {
			
			
			Rule r = this.createRule(clause);
			
			Iterator<Atom> it = r.getHead().iterator();
			if(!it.hasNext()) { // head.size == 0
				add(ruleList,r);
			} else {
				it.next();
				if(!it.hasNext()) { // head.size == 1
					add(ruleList,r);
				} else {
					// if head.size == 2, the rule imply a disjunction
					// we does not deal with disjunction in the conclusion part
					System.err.println("rejected: ");
					for(Collection<Literal> c : f) {
						System.err.println(c);
					}
					return Collections.emptyList();
				}
			}
		}
		return ruleList;
	}
	
	/**
	 * @param clause
	 * @return
	 */
	private Rule createRule(Collection<Literal> clause) {
		Rule r = new DefaultRule();
		for(Literal l : clause) {
			if(l.isPositive) {
				r.getHead().add(l);
			} else {
				r.getBody().add(new DefaultAtom(l));
			}
		}
		
		if(r.getHead().isEmpty()) {
			r = new NegativeConstraint(r.getBody());
		}
		return r;
	}

	public LogicalFormula translate(Rule r) {
		return null;
	}
	
	/**
	 * a -> b(X, E1)
	 * a -> c(E2)
	 * a -> c(E1, E2)
	 * @param list
	 * @param rule
	 */
	private static void add(Collection<Rule> list, Rule rule) {
		Set<Term> exists = rule.getExistentials();
		Rule r;
		for(Term e : exists) {
			Iterator<Rule> it = list.iterator();
			while(it.hasNext()) {
				r = it.next();
				if(r.getTerms().contains(e)) {
					try {
						rule.getHead().addAll(r.getHead());
					} catch (Exception ex) {}
					it.remove();
				}
			}
		}
		list.add(rule);
	}
	

}
