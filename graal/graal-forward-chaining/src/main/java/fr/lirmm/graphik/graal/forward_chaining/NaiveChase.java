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
package fr.lirmm.graphik.graal.forward_chaining;

import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.VariableGenerator;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.impl.DefaultVariableGenerator;
import fr.lirmm.graphik.graal.forward_chaining.halting_condition.ChaseHaltingCondition;
import fr.lirmm.graphik.graal.forward_chaining.rule_applier.DefaultRuleApplier;
import fr.lirmm.graphik.graal.forward_chaining.rule_applier.RuleApplier;
import fr.lirmm.graphik.graal.homomorphism.Homomorphism;
import fr.lirmm.graphik.graal.homomorphism.StaticHomomorphism;
import fr.lirmm.graphik.util.Verbosable;

/**
 * This chase (forward-chaining) algorithm iterates over all rules at each step.
 * It stops if a step does not produce new facts.
 * 
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class NaiveChase extends AbstractChase implements Verbosable {
	
//	private static final Logger LOGGER = LoggerFactory
//			.getLogger(DefaultChase.class);

	private Iterable<Rule> ruleSet;
	private AtomSet atomSet;
	boolean hasNext = true;
	private boolean isVerbose = false;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////
	
	public NaiveChase(Iterable<Rule> ruleSet, AtomSet atomSet) {
		this(ruleSet, atomSet, new DefaultVariableGenerator("E"));
	}

	public NaiveChase(Iterable<Rule> ruleSet, AtomSet atomSet,
			VariableGenerator existentialGen) {
		super(new DefaultRuleApplier<AtomSet>(StaticHomomorphism
				.getSolverFactory().getConjunctiveQuerySolver(atomSet)));
		this.ruleSet = ruleSet;
		this.atomSet = atomSet;
	}

	public NaiveChase(Iterable<Rule> ruleSet, AtomSet atomSet,
			RuleApplier ruleApplier) {
		super(ruleApplier);
		this.ruleSet = ruleSet;
		this.atomSet = atomSet;
	}

	public NaiveChase(Iterable<Rule> ruleSet, AtomSet atomSet,
			VariableGenerator existentialGen,
			Homomorphism<ConjunctiveQuery, AtomSet> solver) {
		super(new DefaultRuleApplier<AtomSet>(solver));
		this.ruleSet = ruleSet;
		this.atomSet = atomSet;
	}
	
	public NaiveChase(Iterable<Rule> ruleSet, AtomSet atomSet,
			Homomorphism<ConjunctiveQuery, AtomSet> solver) {
		super(new DefaultRuleApplier<AtomSet>(solver));
		this.ruleSet = ruleSet;
		this.atomSet = atomSet;
	}

	public NaiveChase(Iterable<Rule> ruleSet, AtomSet atomSet,
			Homomorphism<ConjunctiveQuery, AtomSet> solver,
			ChaseHaltingCondition haltingCondition) {
		super(new DefaultRuleApplier<AtomSet>(solver, haltingCondition));
		this.ruleSet = ruleSet;
		this.atomSet = atomSet;
	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLICS METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	@Override
	public void next() throws ChaseException {
		try {
    		if(this.hasNext) {
    			this.hasNext = false;
    			for (Rule rule : this.ruleSet) {
    				if(this.isVerbose) {
    					System.out.println("Rule: " + rule);
    				}
					if (this.getRuleApplier().apply(rule, atomSet)) {
    					this.hasNext = true;
    				}
    			}
    		}
		} catch (Exception e) {
			throw new ChaseException("An error occured during saturation step.", e);
		}
	}
	
	@Override
	public boolean hasNext() {
		return this.hasNext;
	}

	////////////////////////////////////////////////////////////////////////////
	// ABSTRACT METHODS IMPLEMENTATION
	////////////////////////////////////////////////////////////////////////////

	@Override
	public void enableVerbose(boolean enable) {
		this.isVerbose = enable;
	}
}
