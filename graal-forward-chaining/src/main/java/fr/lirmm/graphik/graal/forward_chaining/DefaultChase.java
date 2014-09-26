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
import fr.lirmm.graphik.graal.core.atomset.ReadOnlyAtomSet;
import fr.lirmm.graphik.graal.solver.Solver;
import fr.lirmm.graphik.graal.solver.StaticSolver;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class DefaultChase extends AbstractChase {

	private ChaseStopCondition stopCondition = new RestrictedChaseStopCondition();
	private SymbolGenerator existentialGen;
	private Iterable<Rule> ruleSet;
	private AtomSet atomSet;
	private Solver solver;
	boolean hasNext = true;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////
	
	public DefaultChase(Iterable<Rule> ruleSet, AtomSet atomSet, SymbolGenerator existentialGen, Solver solver) {
		this.ruleSet = ruleSet;
		this.atomSet = atomSet;
		this.existentialGen = existentialGen;
		this.solver = solver;
	}
	
	public DefaultChase(Iterable<Rule> ruleSet, AtomSet atomSet, Solver solver) {
		this.ruleSet = ruleSet;
		this.atomSet = atomSet;
		this.existentialGen = new DefaultFreeVarGen("E");
		this.solver = solver;
	}
	
	public DefaultChase(Iterable<Rule> ruleSet, AtomSet atomSet, SymbolGenerator existentialGen) {
		this.ruleSet = ruleSet;
		this.atomSet = atomSet;
		this.existentialGen = existentialGen;
		this.solver = StaticSolver.getSolverFactory().getSolver(new DefaultConjunctiveQuery(), atomSet);
	}

	public DefaultChase(Iterable<Rule> ruleSet, AtomSet atomSet) {
		this(ruleSet,atomSet,new DefaultFreeVarGen("E"));
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// PUBLICS METHODS
	// /////////////////////////////////////////////////////////////////////////
	
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
						ReadOnlyAtomSet deductedAtomSet = s.getSubstitut(rule.getHead());
						
						if(stopCondition.canIAdd(deductedAtomSet, fixedTerm, this.atomSet)) {
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
