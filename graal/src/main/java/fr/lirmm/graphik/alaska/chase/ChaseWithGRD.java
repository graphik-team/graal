/**
 * 
 */
package fr.lirmm.graphik.alaska.chase;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.alaska.Alaska;
import fr.lirmm.graphik.alaska.grd.GraphOfRuleDependencies;
import fr.lirmm.graphik.kb.core.AtomSet;
import fr.lirmm.graphik.kb.core.DefaultConjunctiveQuery;
import fr.lirmm.graphik.kb.core.HashMapSubstitution;
import fr.lirmm.graphik.kb.core.Query;
import fr.lirmm.graphik.kb.core.ReadOnlyAtomSet;
import fr.lirmm.graphik.kb.core.Rule;
import fr.lirmm.graphik.kb.core.Substitution;
import fr.lirmm.graphik.kb.core.Term;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class ChaseWithGRD extends AbstractChase {
	
	private static final Logger logger = LoggerFactory
			.getLogger(ChaseWithGRD.class);
	
	private ChaseStopCondition stopCondition = new RestrictedChaseStopCondition();
	private FreeExistentialVariableGenerator varGen = new FreeExistentialVariableGenerator();
	private GraphOfRuleDependencies grd;
	private AtomSet atomSet;
	private Queue<Pair<Rule, Substitution>> queue = new LinkedList<Pair<Rule, Substitution>>();
	
	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////
	
	public ChaseWithGRD(GraphOfRuleDependencies grd, AtomSet atomSet) {
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
				if(logger.isDebugEnabled()) {
					logger.debug("Execute rule: " + rule + " with unificator " + unificator);
					logger.debug("-- Query: " + query);
				}
				
				for (Substitution substitution : Alaska.execute(query, atomSet)) {
					if(logger.isDebugEnabled()) {
						logger.debug("-- Found homomorphism: " + substitution );
					}
					Set<Term> fixedTerm = substitution.getValues();
					
					// Generate substitution for existential var
					substitution.put(varGen.getExistentialSubstitution(unifiedRule.getExistentials()));

					// the atom set producted by the rule application
					ReadOnlyAtomSet deductedAtomSet = Alaska.substitute(substitution, unifiedRule.getHead());
	
					if(stopCondition.canIAdd(deductedAtomSet, fixedTerm, this.atomSet)) {
						this.atomSet.add(deductedAtomSet);
						for(Map.Entry<Substitution, Rule> entry : this.grd.getOutEdges(rule).entrySet()) {
							Substitution u = entry.getKey().compose(substitution);
							if(logger.isDebugEnabled()) {
								logger.debug("-- -- Dependency: " + entry.getValue() + " with " + entry.getKey());
								logger.debug("-- -- Unificator: " + u);
							}
							this.queue.add(new ImmutablePair<Rule, Substitution>(entry.getValue(), u));
						}
					}
				}
			}
		} catch (Exception e) {
			throw new ChaseException("An error occur pending saturation step.", e);
		}
	}
	

	/* (non-Javadoc)
	 * @see fr.lirmm.graphik.alaska.saturator.Saturator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return !queue.isEmpty();
	}

}
