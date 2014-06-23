/**
 * 
 */
package fr.lirmm.graphik.alaska.chase;

import java.util.Set;

import fr.lirmm.graphik.alaska.Alaska;
import fr.lirmm.graphik.kb.core.AtomSet;
import fr.lirmm.graphik.kb.core.ReadOnlyAtomSet;
import fr.lirmm.graphik.kb.core.Rule;
import fr.lirmm.graphik.kb.core.Substitution;
import fr.lirmm.graphik.kb.core.Term;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class DefaultChase extends AbstractChase {

	private ChaseStopCondition stopCondition = new RestrictedChaseStopCondition();
	private FreeExistentialVariableGenerator varGen = new FreeExistentialVariableGenerator();
	private Iterable<Rule> ruleSet;
	private AtomSet atomSet;
	boolean hasNext = true;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	public DefaultChase(Iterable<Rule> ruleSet, AtomSet atomSet) {
		this.ruleSet = ruleSet;
		this.atomSet = atomSet;
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// PUBLICS METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	public void next() throws ChaseException {
		try {
    		if(this.hasNext) {
    			this.hasNext = false;
    			for (Rule rule : this.ruleSet) {
					for (Substitution s : Alaska.getRuleBodyHomomorphisms(rule, this.atomSet)) {
						Set<Term> fixedTerm = s.getValues();
						
						// Generate substitution for existential var
						s.put(varGen.getExistentialSubstitution(rule.getExistentials()));

						// the atom set producted by the rule application
						ReadOnlyAtomSet deductedAtomSet = Alaska.substitute(s, rule.getHead());
						
						if(stopCondition.canIAdd(deductedAtomSet, fixedTerm, this.atomSet)) {
							this.atomSet.add(deductedAtomSet);
							this.hasNext = true;
						}
					}
    			}
    		}
		} catch (Exception e) {
			throw new ChaseException("An error occur pending saturation step.", e);
		}
	}
	
	public boolean hasNext() {
		return this.hasNext;
	}

}
