/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2016)
 *
 * Contributors :
 *
 * Clément SIPIETER <clement.sipieter@inria.fr>
 * Mélanie KÖNIG
 * Swan ROCHER
 * Jean-François BAGET
 * Michel LECLÈRE
 * Marie-Laure MUGNIER <mugnier@lirmm.fr>
 *
 *
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
 /**
 * 
 */
package fr.lirmm.graphik.graal.forward_chaining;

import java.util.Set;
import java.util.TreeSet;

import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.forward_chaining.AbstractChase;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseHaltingCondition;
import fr.lirmm.graphik.graal.api.forward_chaining.RuleApplier;
import fr.lirmm.graphik.graal.api.homomorphism.Homomorphism;
import fr.lirmm.graphik.graal.core.DefaultRule;
import fr.lirmm.graphik.graal.core.ruleset.IndexedByBodyPredicatesRuleSet;
import fr.lirmm.graphik.graal.forward_chaining.rule_applier.DefaultRuleApplier;

/**
 * This chase (forward-chaining) algorithm iterates over all rules at each step.
 * It stops if a step does not produce new facts.
 * 
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class ConfigurableChase extends AbstractChase {
	
//	private static final Logger LOGGER = LoggerFactory
//			.getLogger(DefaultChase.class);

	private IndexedByBodyPredicatesRuleSet ruleSet;
	private AtomSet atomSet;

	private Set<Rule> rulesToCheck;
	private Set<Rule> nextRulesToCheck;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////
	
	public ConfigurableChase(Iterable<Rule> rules, AtomSet atomSet) {
		this(rules, atomSet, new DefaultRuleApplier<AtomSet>());
	}

	public ConfigurableChase(Iterable<Rule> rules, AtomSet atomSet,
			RuleApplier ruleApplier) {
		super(ruleApplier);
		this.atomSet = atomSet;
		this.ruleSet = new IndexedByBodyPredicatesRuleSet();
		init(rules);
	}

	public ConfigurableChase(Iterable<Rule> rules, AtomSet atomSet,
			Homomorphism<ConjunctiveQuery, AtomSet> solver) {
		this(rules, atomSet, new DefaultRuleApplier<AtomSet>(solver));
	}

	public ConfigurableChase(Iterable<Rule> rules, AtomSet atomSet, ChaseHaltingCondition haltingCondition) {
		this(rules, atomSet, new DefaultRuleApplier<AtomSet>(haltingCondition));

	}

	public ConfigurableChase(Iterable<Rule> rules, AtomSet atomSet,
            Homomorphism<ConjunctiveQuery, AtomSet> solver,
			ChaseHaltingCondition haltingCondition) {
		this(rules, atomSet, new DefaultRuleApplier<AtomSet>(solver, haltingCondition));
	}

	private void init(Iterable<Rule> rules) {
		int i = 0;
		this.nextRulesToCheck = new TreeSet<Rule>();
		for (Rule r : rules) {
			Rule copy = new DefaultRule(r);
			copy.setLabel(Integer.toString(++i));
			this.ruleSet.add(copy);
			this.nextRulesToCheck.add(copy);
		}
	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLICS METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	@Override
	public void next() throws ChaseException {
		this.rulesToCheck = this.nextRulesToCheck;
		this.nextRulesToCheck = new TreeSet<Rule>();
		try {
			if (!this.rulesToCheck.isEmpty()) {
				if (this.getProfiler().isProfilingEnabled()) {
					this.getProfiler().start("saturationTime");
				}
				for (Rule rule : this.rulesToCheck) {
					String key = null;
					if (this.getProfiler().isProfilingEnabled()) {
						key = "Rule " + rule.getLabel() + " application time";
						this.getProfiler().clear(key);
						this.getProfiler().trace(rule.toString());
						this.getProfiler().start(key);
    				}
					if (this.getRuleApplier().apply(rule, atomSet)) {
						for (Predicate p : rule.getHead().getPredicates()) {
							for (Rule r : this.ruleSet.getRulesByBodyPredicate(p)) {
								this.nextRulesToCheck.add(r);
							}
						}
					}
					if (this.getProfiler().isProfilingEnabled()) {
						this.getProfiler().stop(key);
					}
    			}
				if (this.getProfiler().isProfilingEnabled()) {
					this.getProfiler().stop("saturationTime");
				}
    		}
		} catch (Exception e) {
			throw new ChaseException("An error occured during saturation step.", e);
		}
	}
	
	@Override
	public boolean hasNext() {
		return !this.nextRulesToCheck.isEmpty();
	}

	////////////////////////////////////////////////////////////////////////////
	// ABSTRACT METHODS IMPLEMENTATION
	////////////////////////////////////////////////////////////////////////////


}
