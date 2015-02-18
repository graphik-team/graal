/**
 * 
 */
package fr.lirmm.graphik.graal.forward_chaining;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.DefaultConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.Substitution;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
import fr.lirmm.graphik.graal.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.homomorphism.HomomorphismFactoryException;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public abstract class AbstractChase implements Chase {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(AbstractChase.class);

	public void execute() throws ChaseException {
		while (this.hasNext())
			this.next();
	}

	protected abstract Iterable<Substitution> executeQuery(
			ConjunctiveQuery query, AtomSet atomSet)
			throws HomomorphismFactoryException, HomomorphismException;

	protected abstract Term getFreeVar();
	
	//protected abstract void afterAdd(Rule rule);


	public abstract ChaseHaltingCondition getHaltingCondition();
	
	public abstract void setHaltingCondition(ChaseHaltingCondition haltingCondition);

	/**
	 * @param rule
	 * @throws AtomSetException
	 * @throws HomomorphismException
	 * @throws HomomorphismFactoryException
	 */
	protected boolean apply(Rule rule, AtomSet atomSet)
			throws HomomorphismFactoryException, HomomorphismException,
			AtomSetException {
		boolean isChanged = false;
		ConjunctiveQuery query = new DefaultConjunctiveQuery(rule.getBody(),
				rule.getFrontier());
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Rule to execute: " + rule);
			LOGGER.debug("       -- Query: " + query);
		}

		for (Substitution substitution : this.executeQuery(query, atomSet)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("-- Found homomorphism: " + substitution);
			}
			Set<Term> fixedTerm = substitution.getValues();

			// Generate new existential variables
			for (Term t : rule.getExistentials()) {
				substitution.put(t, this.getFreeVar());
			}

			// the atom set producted by the rule application
			AtomSet deductedAtomSet = substitution.getSubstitut(rule.getHead());
			AtomSet bodyAtomSet = substitution.getSubstitut(rule.getBody());

			if (this.getHaltingCondition().canIAdd(deductedAtomSet, fixedTerm,
					bodyAtomSet, atomSet)) {
				atomSet.addAll(deductedAtomSet);
				isChanged = true;
			}
		}
		
		return isChanged;
	}

};
