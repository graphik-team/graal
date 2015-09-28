/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2015)
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

import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.VariableGenerator;
import fr.lirmm.graphik.graal.api.forward_chaining.AbstractChase;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseHaltingCondition;
import fr.lirmm.graphik.graal.api.forward_chaining.RuleApplier;
import fr.lirmm.graphik.graal.api.homomorphism.Homomorphism;
import fr.lirmm.graphik.graal.core.DefaultVariableGenerator;
import fr.lirmm.graphik.graal.forward_chaining.rule_applier.DefaultRuleApplier;
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
