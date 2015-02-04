/**
 * 
 */
package fr.lirmm.graphik.graal.forward_chaining;

import java.util.Set;

import fr.lirmm.graphik.graal.core.DefaultConjunctiveQuery;
import fr.lirmm.graphik.graal.core.DefaultFreeVarGen;
import fr.lirmm.graphik.graal.core.Query;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.Substitution;
import fr.lirmm.graphik.graal.core.SymbolGenerator;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.homomorphism.Homomorphism;
import fr.lirmm.graphik.graal.homomorphism.StaticHomomorphism;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class DefaultChase extends AbstractChase {

	private ChaseStopCondition stopCondition = new RestrictedChaseStopCondition();
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

	public void setHaltingCondition(ChaseStopCondition c) {
		this.stopCondition = c;
	}
	
	public void next() throws ChaseException {
		try {
    		if(this.hasNext) {
    			this.hasNext = false;
    			for (Rule rule : this.ruleSet) {
    				Query query = new DefaultConjunctiveQuery(rule.getBody(),
    						rule.getFrontier());
					for (Substitution s : this.solver.execute(query, atomSet)) {
						Set<Term> fixedTerm = s.getValues();
						
						// Generate new existential variables
						for(Term t : rule.getExistentials()) {
							s.put(t, existentialGen.getFreeVar());
						}

						// the atom set producted by the rule application
						AtomSet deductedAtomSet = s.getSubstitut(rule.getHead());
						AtomSet bodyAtomSet = s.getSubstitut(rule.getBody());
						
						if(stopCondition.canIAdd(deductedAtomSet, fixedTerm, bodyAtomSet, this.atomSet)) {
							this.atomSet.addAll(deductedAtomSet);
							this.hasNext = true;
						}
					}
    			}
    		}
		} catch (Exception e) {
			throw new ChaseException("An error occured during saturation step.", e);
		}
	}
	
	public boolean hasNext() {
		return this.hasNext;
	}

}
