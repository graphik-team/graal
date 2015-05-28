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

import fr.lirmm.graphik.graal.core.DefaultConjunctiveQuery;
import fr.lirmm.graphik.graal.core.HashMapSubstitution;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.Substitution;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
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
				.getSolver(new DefaultConjunctiveQuery(), atomSet)));
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
				unifiedRule = unificator.getSubstitut(pair.getLeft());
				
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
