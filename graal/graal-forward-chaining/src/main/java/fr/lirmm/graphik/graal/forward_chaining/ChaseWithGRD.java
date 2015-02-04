/**
 * 
 */
package fr.lirmm.graphik.graal.forward_chaining;

import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.DefaultFreeVarGen;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.Substitution;
import fr.lirmm.graphik.graal.core.SymbolGenerator;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.grd.GraphOfRuleDependencies;
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
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ChaseWithGRD.class);
	
	private ChaseHaltingCondition stopCondition = new RestrictedChaseStopCondition();
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
				if(this.apply(rule, this.atomSet)) {
					for(Rule triggeredRule : this.grd.getOutEdges(rule)) {
						if(LOGGER.isDebugEnabled()) {
							LOGGER.debug("-- -- Dependency: " + triggeredRule);
						}
						this.queue.add(triggeredRule);
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
	
	// /////////////////////////////////////////////////////////////////////////
	// ABSTRACT METHODS IMPLEMENTATION
	// /////////////////////////////////////////////////////////////////////////
	
	@Override
	protected Iterable<Substitution> executeQuery(ConjunctiveQuery query, AtomSet atomSet) throws HomomorphismFactoryException, HomomorphismException {
		return StaticHomomorphism.executeQuery(query, atomSet);
	}

	@Override
	protected Term getFreeVar() {
		return this.existentialGen.getFreeVar();
	}

	@Override
	public ChaseHaltingCondition getHaltingCondition() {
		return this.stopCondition;
	}

	@Override
	public void setHaltingCondition(ChaseHaltingCondition haltingCondition) {
		this.stopCondition = haltingCondition;
	}

}
