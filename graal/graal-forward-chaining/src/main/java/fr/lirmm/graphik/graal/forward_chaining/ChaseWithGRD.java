/**
 * 
 */
package fr.lirmm.graphik.graal.forward_chaining;

import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.DefaultConjunctiveQuery;
import fr.lirmm.graphik.graal.core.DefaultFreeVarGen;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.Substitution;
import fr.lirmm.graphik.graal.core.SymbolGenerator;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
import fr.lirmm.graphik.graal.core.atomset.ReadOnlyAtomSet;
import fr.lirmm.graphik.graal.grd.GraphOfRuleDependencies;
import fr.lirmm.graphik.graal.grd.GraphOfRuleDependenciesWithUnifiers;
import fr.lirmm.graphik.graal.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.homomorphism.HomomorphismFactoryException;
import fr.lirmm.graphik.graal.homomorphism.StaticHomomorphism;

/**
 * This chase (forward-chaining) algorithm use GRD to define the Rules that will 
 * be triggered in the next step.
 * 
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class ChaseWithGRD extends AbstractChase {
	
	private static final Logger logger = LoggerFactory
			.getLogger(ChaseWithGRD.class);
	
	private ChaseStopCondition stopCondition = new RestrictedChaseStopCondition();
	private SymbolGenerator existentialGen = new DefaultFreeVarGen("E");
	private GraphOfRuleDependencies grd;
	private AtomSet atomSet;
	private TreeSet<Rule> queue = new TreeSet<Rule>();
	
	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////
	
	public ChaseWithGRD(GraphOfRuleDependencies grd, AtomSet atomSet) {
		this.grd = grd;
		this.atomSet = atomSet;
		for(Rule r : grd.getRules()) {			
			this.queue.add(r);
		}
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public void next() throws ChaseException {
		Rule rule;
		try {
			rule = queue.pollFirst();
			if(rule != null) {
				this.apply(rule);
			}
		} catch (Exception e) {
			throw new ChaseException("An error occur pending saturation step.", e);
		}
	}

	@Override
	public boolean hasNext() {
		return !queue.isEmpty();
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	/**
	 * @param rule
	 * @throws AtomSetException 
	 * @throws HomomorphismException 
	 * @throws HomomorphismFactoryException 
	 */
	private void apply(Rule rule) throws HomomorphismFactoryException, HomomorphismException, AtomSetException {
		ConjunctiveQuery query = new DefaultConjunctiveQuery(rule.getBody(), rule.getFrontier());
		if(logger.isDebugEnabled()) {
			logger.debug("Rule to execute: " + rule);
			logger.debug("       -- Query: " + query);
		}
		
		for (Substitution substitution : StaticHomomorphism.executeQuery(query, atomSet)) {
			if(logger.isDebugEnabled()) {
				logger.debug("-- Found homomorphism: " + substitution );
			}
			Set<Term> fixedTerm = substitution.getValues();
			
			// Generate new existential variables
			for(Term t : rule.getExistentials()) {
				substitution.put(t, existentialGen.getFreeVar());
			}

			// the atom set producted by the rule application
			ReadOnlyAtomSet deductedAtomSet = substitution.getSubstitut(rule.getHead());

			if(stopCondition.canIAdd(deductedAtomSet, fixedTerm, this.atomSet)) {
				this.atomSet.addAll(deductedAtomSet);
				for(Rule triggeredRule : this.grd.getOutEdges(rule)) {
					if(logger.isDebugEnabled()) {
						logger.debug("-- -- Dependency: " + triggeredRule);
					}
					this.queue.add(triggeredRule);
				}
			}
		}
	}

}
