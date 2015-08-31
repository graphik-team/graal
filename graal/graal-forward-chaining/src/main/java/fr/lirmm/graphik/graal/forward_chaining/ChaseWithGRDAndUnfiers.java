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

import java.util.LinkedList;
import java.util.Queue;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.Substitution;
import fr.lirmm.graphik.graal.core.Unifier;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.factory.ConjunctiveQueryFactory;
import fr.lirmm.graphik.graal.core.impl.HashMapSubstitution;
import fr.lirmm.graphik.graal.forward_chaining.rule_applier.DefaultRuleApplier;
import fr.lirmm.graphik.graal.grd.GraphOfRuleDependencies;
import fr.lirmm.graphik.graal.homomorphism.StaticHomomorphism;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class ChaseWithGRDAndUnfiers extends AbstractChase {
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ChaseWithGRDAndUnfiers.class);
	
	private GraphOfRuleDependencies grd;
	private AtomSet atomSet;
	private Queue<Pair<Rule, Substitution>> queue = new LinkedList<Pair<Rule, Substitution>>();
	
	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////
	
	public ChaseWithGRDAndUnfiers(GraphOfRuleDependencies grd, AtomSet atomSet) {
		super(new DefaultRuleApplier(StaticHomomorphism.getSolverFactory()
.getSolver(
				ConjunctiveQueryFactory.instance().create(), atomSet)));
		this.grd = grd;
		this.atomSet = atomSet;
		for(Rule r : grd.getRules()) {			
			this.queue.add(new ImmutablePair<Rule, Substitution>(r, new HashMapSubstitution()));
		}
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public void next() throws ChaseException {
		Rule rule, unifiedRule;
		Substitution unificator;

		try {
			Pair<Rule, Substitution> pair = queue.poll();
			if(pair != null) {
				unificator = pair.getRight();
				rule = pair.getLeft();
				unifiedRule = Unifier.computeInitialAtomSetTermsSubstitution(rule.getBody()).createImageOf(rule);
				unifiedRule = unificator.createImageOf(unifiedRule);
				
				if(LOGGER.isDebugEnabled()) {
					LOGGER.debug("Execute rule: " + rule + " with unificator " + unificator);
				}
				
				if (this.getRuleApplier().apply(unifiedRule, this.atomSet)) {
					for (Integer e : this.grd.getOutgoingEdgesOf(rule)) {
						Rule triggeredRule = this.grd.getEdgeTarget(e);
						for (Substitution u : this.grd.getUnifiers(e)) {
							if(LOGGER.isDebugEnabled()) {
								LOGGER.debug("-- -- Dependency: " + triggeredRule + " with " + u);
								LOGGER.debug("-- -- Unificator: " + u);
							}
							if(u != null) {
								this.queue.add(new ImmutablePair<Rule, Substitution>(triggeredRule, u));
							}
						}
					}
				}
			}
		} catch (Exception e) {
			throw new ChaseException("An error occur pending saturation step.", e);
		}
	}
	
	@Override
	public boolean hasNext() {
		return !queue.isEmpty();
	}

}
