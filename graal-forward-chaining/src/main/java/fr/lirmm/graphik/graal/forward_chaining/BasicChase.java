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
import fr.lirmm.graphik.graal.api.core.RuleSet;
import fr.lirmm.graphik.graal.api.forward_chaining.AbstractDirectChase;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseHaltingCondition;
import fr.lirmm.graphik.graal.api.forward_chaining.DirectRuleApplier;
import fr.lirmm.graphik.graal.api.homomorphism.Homomorphism;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.ruleset.IndexedByBodyPredicatesRuleSet;
import fr.lirmm.graphik.graal.core.ruleset.LinkedListRuleSet;
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
public class BasicChase extends AbstractDirectChase {
	
	private RuleSet ruleSet;
	private AtomSet atomSet;
	private boolean hasNext = true;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////
	
	public BasicChase(Iterator<Rule> rules, AtomSet atomSet) {
		super(new DefaultRuleApplier<AtomSet>());
		this.atomSet = atomSet;
		this.ruleSet = new LinkedListRuleSet(rules);
	}
	
	public BasicChase(Iterable<Rule> rules, AtomSet atomSet) {
		super(new DefaultRuleApplier<AtomSet>());
		this.atomSet = atomSet;
		this.ruleSet = new LinkedListRuleSet(rules);
	}
	
	public BasicChase(Iterator<Rule> rules, AtomSet atomSet,
			DirectRuleApplier ruleApplier) {
		super(ruleApplier);
		this.atomSet = atomSet;
		this.ruleSet = new LinkedListRuleSet(rules);
	}

	public BasicChase(Iterable<Rule> rules, AtomSet atomSet,
		DirectRuleApplier ruleApplier) {
		this(rules.iterator(), atomSet, ruleApplier);
	}

	public BasicChase(Iterable<Rule> rules, AtomSet atomSet,
			Homomorphism<ConjunctiveQuery, AtomSet> solver) {
		this(rules, atomSet, new DefaultRuleApplier<AtomSet>(solver));
	}

	public BasicChase(Iterable<Rule> rules, AtomSet atomSet, ChaseHaltingCondition haltingCondition) {
		this(rules, atomSet, new DefaultRuleApplier<AtomSet>(haltingCondition));

	}

	public BasicChase(Iterable<Rule> rules, AtomSet atomSet,
            Homomorphism<ConjunctiveQuery, AtomSet> solver,
			ChaseHaltingCondition haltingCondition) {
		this(rules, atomSet, new DefaultRuleApplier<AtomSet>(solver, haltingCondition));
	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLICS METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public void next() throws ChaseException {
		try {
			this.hasNext = false;
			for(Rule rule : this.ruleSet) {
				if (this.getProfiler().isProfilingEnabled()) {
					this.getProfiler().start("saturationTime");
				}

				String key = null;
				if (this.getProfiler().isProfilingEnabled()) {
					key = "Rule " + rule.getLabel() + " application time";
					this.getProfiler().clear(key);
					this.getProfiler().trace(rule.toString());
					this.getProfiler().start(key);
				}
				boolean val = this.getRuleApplier().apply(rule, this.atomSet);
				this.hasNext = this.hasNext || val;
				
				if (this.getProfiler().isProfilingEnabled()) {
					this.getProfiler().stop(key);
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
		return this.hasNext;
	}

}
