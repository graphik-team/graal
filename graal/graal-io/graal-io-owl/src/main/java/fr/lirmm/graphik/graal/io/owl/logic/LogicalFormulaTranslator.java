/* Graal v0.7.4
 * Copyright (c) 2014-2015 Inria Sophia Antipolis - Méditerranée / LIRMM (Université de Montpellier & CNRS)
 * All rights reserved.
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * Author(s): Clément SIPIETER
 *            Mélanie KÖNIG
 *            Swan ROCHER
 *            Jean-François BAGET
 *            Michel LECLÈRE
 *            Marie-Laure MUGNIER
 */
 /**
 * 
 */
package fr.lirmm.graphik.graal.io.owl.logic;

import java.util.Collection;
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
public final class LogicalFormulaTranslator {

	private static LogicalFormulaTranslator instance;

	private LogicalFormulaTranslator() {
	}

	public static synchronized LogicalFormulaTranslator getInstance() {
		if (instance == null)
			instance = new LogicalFormulaTranslator();

		return instance;
	}
	
	public Iterable<Object> translate(LogicalFormula f) {
		Collection<Object> objectList = new LinkedList<Object>();
		Collection<Rule> ruleList = new LinkedList<Rule>();
		for(Collection<Literal> clause : f) {
			
			
			Rule r = this.createRule(clause);
			
			Iterator<Atom> itBody = r.getBody().iterator();
			if (!itBody.hasNext()) {
				// it is a fact
				for (Atom a : r.getHead()) {
					objectList.add(a);
				}
			} else {
				Iterator<Atom> itHead = r.getHead().iterator();
				if (!itHead.hasNext()) { // head.size == 0
					add(ruleList,r);
				} else {
					itHead.next();
					if (!itHead.hasNext()) { // head.size == 1
						add(ruleList, r);
					} else {
						// if head.size == 2, the rule imply a disjunction
						// we does not deal with disjunction in the conclusion
						// part
						System.err.println("rejected: ");
						for (Collection<Literal> c : f) {
							System.err.println(c);
						}
					}
				}
			}
		}
		objectList.addAll(ruleList);
		return objectList;
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
