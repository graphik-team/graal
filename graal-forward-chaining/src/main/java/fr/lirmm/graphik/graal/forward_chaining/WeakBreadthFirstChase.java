/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2017)
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

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.forward_chaining.AbstractChase;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseHaltingCondition;
import fr.lirmm.graphik.graal.api.forward_chaining.RuleApplier;
import fr.lirmm.graphik.graal.api.homomorphism.Homomorphism;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.ruleset.IndexedByBodyPredicatesRuleSet;
import fr.lirmm.graphik.graal.forward_chaining.rule_applier.DefaultRuleApplier;
import fr.lirmm.graphik.util.stream.CloseableIteratorAdapter;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;

/**
 * This chase (forward-chaining) algorithm iterates over all rules at each step.
 * It stops if a step does not produce new facts.
 * 
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class WeakBreadthFirstChase extends AbstractChase {
	
	private IndexedByBodyPredicatesRuleSet ruleSet;
	private AtomSet atomSet;
	private Collection<Atom> tmpData = new LinkedList<Atom>();

	private Map<Rule, AtomSet> rulesToCheck;
	private Map<Rule, AtomSet> nextRulesToCheck;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////
	
	public WeakBreadthFirstChase(Iterator<Rule> rules, AtomSet atomSet) {
		super(new DefaultRuleApplier<AtomSet>());
		this.atomSet = atomSet;
		this.ruleSet = new IndexedByBodyPredicatesRuleSet();
		init(rules);
	}
	
	public WeakBreadthFirstChase(Iterable<Rule> rules, AtomSet atomSet) {
		super(new DefaultRuleApplier<AtomSet>());
		this.atomSet = atomSet;
		this.ruleSet = new IndexedByBodyPredicatesRuleSet();
		init(rules.iterator());
	}
	
	public WeakBreadthFirstChase(Iterator<Rule> rules, AtomSet atomSet,
			RuleApplier ruleApplier) {
		super(ruleApplier);
		this.atomSet = atomSet;
		this.ruleSet = new IndexedByBodyPredicatesRuleSet();
		init(rules);
	}

	public WeakBreadthFirstChase(Iterable<Rule> rules, AtomSet atomSet,
			RuleApplier ruleApplier) {
		this(rules.iterator(), atomSet, ruleApplier);
	}

	public WeakBreadthFirstChase(Iterable<Rule> rules, AtomSet atomSet,
			Homomorphism<ConjunctiveQuery, AtomSet> solver) {
		this(rules, atomSet, new DefaultRuleApplier<AtomSet>(solver));
	}

	public WeakBreadthFirstChase(Iterable<Rule> rules, AtomSet atomSet, ChaseHaltingCondition haltingCondition) {
		this(rules, atomSet, new DefaultRuleApplier<AtomSet>(haltingCondition));

	}

	public WeakBreadthFirstChase(Iterable<Rule> rules, AtomSet atomSet,
            Homomorphism<ConjunctiveQuery, AtomSet> solver,
			ChaseHaltingCondition haltingCondition) {
		this(rules, atomSet, new DefaultRuleApplier<AtomSet>(solver, haltingCondition));
	}

	private void init(Iterator<Rule> rules) {
		this.nextRulesToCheck = new TreeMap<Rule, AtomSet>();
		while(rules.hasNext()) {
			Rule r = rules.next();
			this.ruleSet.add(r);
			this.nextRulesToCheck.put(r, atomSet);
		}
	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLICS METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public void next() throws ChaseException {
		this.rulesToCheck = this.nextRulesToCheck;
		this.nextRulesToCheck = new TreeMap<Rule, AtomSet>();
		try {
			if (!this.rulesToCheck.isEmpty()) {
				if (this.getProfiler().isProfilingEnabled()) {
					this.getProfiler().start("saturationTime");
				}
				
				for (Entry<Rule, AtomSet> e : this.rulesToCheck.entrySet()) {

					String key = null;
					Rule rule = e.getKey();
					AtomSet data = e.getValue();

					if (this.getProfiler().isProfilingEnabled()) {
						key = "Rule " + rule.getLabel() + " application time";
						this.getProfiler().clear(key);
						this.getProfiler().trace(rule.toString());
						this.getProfiler().start(key);
    				}
					this.getRuleApplier().apply(rule, data, tmpData);
					this.dispatchNewData(this.tmpData);
					this.atomSet.addAll(new CloseableIteratorAdapter<Atom>(this.tmpData.iterator()));
					this.tmpData.clear();
					
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

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE CLASS
	// /////////////////////////////////////////////////////////////////////////
	
	protected void dispatchNewData(Collection<Atom> newData) throws ChaseException {
		for(Atom a : newData) {
			Predicate p = a.getPredicate();
			for (Rule r : ruleSet.getRulesByBodyPredicate(p)) {
				if (linearRuleCheck(r)) {
					AtomSet set = nextRulesToCheck.get(r);
					if (set == null) {
						set = new LinkedListAtomSet();
						//set =  new DefaultInMemoryGraphAtomSet();
						nextRulesToCheck.put(r, set);
					}
					try {
						set.add(a);
					} catch (AtomSetException e) {
						throw new ChaseException("Exception while adding data into a tmp store",e);
					}
				} else {
					nextRulesToCheck.put(r, atomSet);
				}
			}
		}
	}

	private static boolean linearRuleCheck(Rule r) {
		CloseableIteratorWithoutException<Atom> it = r.getBody().iterator();
		if (it.hasNext()) {
			it.next();
		} else {
			return false;
		}
		return !it.hasNext();

	}

}
