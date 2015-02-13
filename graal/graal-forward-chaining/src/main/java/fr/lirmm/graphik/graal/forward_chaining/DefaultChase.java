/**
 * 
 */
package fr.lirmm.graphik.graal.forward_chaining;

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
import fr.lirmm.graphik.graal.homomorphism.Homomorphism;
import fr.lirmm.graphik.graal.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.homomorphism.HomomorphismFactoryException;
import fr.lirmm.graphik.graal.homomorphism.StaticHomomorphism;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class DefaultChase extends AbstractChase {
	
//	private static final Logger LOGGER = LoggerFactory
//			.getLogger(DefaultChase.class);

	private ChaseHaltingCondition haltingCondition = new RestrictedChaseStopCondition();
	private SymbolGenerator existentialGen;
	private Iterable<Rule> ruleSet;
	private AtomSet atomSet;
	private Homomorphism solver;
	boolean hasNext = true;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////
	
	public DefaultChase(Iterable<Rule> ruleSet, AtomSet atomSet, SymbolGenerator existentialGen, Homomorphism solver) {
		this.ruleSet = ruleSet;
		this.atomSet = atomSet;
		this.existentialGen = existentialGen;
		this.solver = solver;
	}
	
	public DefaultChase(Iterable<Rule> ruleSet, AtomSet atomSet, Homomorphism solver) {
		this.ruleSet = ruleSet;
		this.atomSet = atomSet;
		this.existentialGen = new DefaultFreeVarGen("E");
		this.solver = solver;
	}
	
	public DefaultChase(Iterable<Rule> ruleSet, AtomSet atomSet, SymbolGenerator existentialGen) {
		this.ruleSet = ruleSet;
		this.atomSet = atomSet;
		this.existentialGen = existentialGen;
		this.solver = StaticHomomorphism.getSolverFactory().getSolver(new DefaultConjunctiveQuery(), atomSet);
	}

	public DefaultChase(Iterable<Rule> ruleSet, AtomSet atomSet) {
		this(ruleSet,atomSet,new DefaultFreeVarGen("E"));
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
    				if(this.apply(rule, atomSet)) {
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
	protected Iterable<Substitution> executeQuery(ConjunctiveQuery query,
			AtomSet atomSet) throws HomomorphismFactoryException,
			HomomorphismException {
		return this.solver.execute(query, atomSet);
	}

	@Override
	protected Term getFreeVar() {
		return this.existentialGen.getFreeVar();
	}
	
	@Override
	public void setHaltingCondition(ChaseHaltingCondition haltingCondition) {
		this.haltingCondition = haltingCondition;
	}

	@Override
	public ChaseHaltingCondition getHaltingCondition() {
		return this.haltingCondition;
	}
}
