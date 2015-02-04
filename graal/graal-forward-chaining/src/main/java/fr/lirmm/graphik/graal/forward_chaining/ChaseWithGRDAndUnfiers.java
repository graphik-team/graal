/**
 * 
 */
package fr.lirmm.graphik.graal.forward_chaining;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.core.DefaultConjunctiveQuery;
import fr.lirmm.graphik.graal.core.DefaultFreeVarGen;
import fr.lirmm.graphik.graal.core.HashMapSubstitution;
import fr.lirmm.graphik.graal.core.Query;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.Substitution;
import fr.lirmm.graphik.graal.core.SymbolGenerator;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.grd.GraphOfRuleDependenciesWithUnifiers;
import fr.lirmm.graphik.graal.homomorphism.StaticHomomorphism;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class ChaseWithGRDAndUnfiers extends AbstractChase {
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ChaseWithGRDAndUnfiers.class);
	
	private ChaseStopCondition stopCondition = new RestrictedChaseStopCondition();
	private SymbolGenerator existentialGen = new DefaultFreeVarGen("E");
	private GraphOfRuleDependenciesWithUnifiers grd;
	private AtomSet atomSet;
	private Queue<Pair<Rule, Substitution>> queue = new LinkedList<Pair<Rule, Substitution>>();
	
	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////
	
	public ChaseWithGRDAndUnfiers(GraphOfRuleDependenciesWithUnifiers grd, AtomSet atomSet) {
		this.grd = grd;
		this.atomSet = atomSet;
		for(Rule r : grd.getRules()) {			
			this.queue.add(new ImmutablePair<Rule, Substitution>(r, new HashMapSubstitution()));
		}
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////

	/* (non-Javadoc)
	 * @see fr.lirmm.graphik.alaska.saturator.Saturator#next()
	 */
	@Override
	public void next() throws ChaseException {
		Rule rule, unifiedRule;
		Substitution unificator;
		Query query;
		try {
			Pair<Rule, Substitution> pair = queue.poll();
			if(pair != null) {
				unificator = pair.getRight();
				rule = pair.getLeft();
				unifiedRule = unificator.getSubstitut(pair.getLeft());
				query = new DefaultConjunctiveQuery(unifiedRule.getBody(), unifiedRule.getFrontier());
				if(LOGGER.isDebugEnabled()) {
					LOGGER.debug("Execute rule: " + rule + " with unificator " + unificator);
					LOGGER.debug("-- Query: " + query);
				}
				
				for (Substitution substitution : StaticHomomorphism.executeQuery(query, atomSet)) {
					if(LOGGER.isDebugEnabled()) {
						LOGGER.debug("-- Found homomorphism: " + substitution );
					}
					Set<Term> fixedTerm = substitution.getValues();
					
					// Generate new existential variables
					for(Term t : unifiedRule.getExistentials()) {
						substitution.put(t, existentialGen.getFreeVar());
					}

					// the atom set producted by the rule application
					AtomSet deductedAtomSet = substitution.getSubstitut(unifiedRule.getHead());
	
					if(stopCondition.canIAdd(deductedAtomSet, fixedTerm, this.atomSet)) {
						this.atomSet.addAll(deductedAtomSet);
						for(Rule triggeredRule : this.grd.getOutEdges(rule)) {
							for(Substitution u : this.grd.getUnifiers(rule, triggeredRule)) {
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
