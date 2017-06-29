/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2015)
 *
 * Contributors :
 *
 * Clément SIPIETER <clement.sipieter@inria.fr>
 * Mélanie KÖNIG
 * Swan ROCHER
 * Jean-François BAGET
 * Michel LECLÈRE
 * Marie-Laure MUGNIER <mugnier@lirmm.fr>
 *
 *
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
package fr.lirmm.graphik.graal.forward_chaining.rule_applier;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseHaltingCondition;
import fr.lirmm.graphik.graal.api.forward_chaining.RuleApplicationException;
import fr.lirmm.graphik.graal.api.forward_chaining.RuleApplier;
import fr.lirmm.graphik.graal.api.homomorphism.Homomorphism;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismFactoryException;
import fr.lirmm.graphik.graal.forward_chaining.halting_condition.RestrictedChaseHaltingCondition;
import fr.lirmm.graphik.graal.homomorphism.SmartHomomorphism;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.IteratorException;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public abstract class AbstractRuleApplier<T extends AtomSet> implements RuleApplier<Rule, T> {

	private ChaseHaltingCondition							  haltingCondition;
	private Homomorphism<? super ConjunctiveQuery, ? super T> solver;

	// //////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// //////////////////////////////////////////////////////////////////////////

	/**
	 * Construct a DefaultRuleApplier with a
	 * {@link RestrictedChaseHaltingCondition} and a {@link SmartHomomorphism}
	 */
	public AbstractRuleApplier() {
		this(SmartHomomorphism.instance());
	}

	/**
	 * Construct a DefaultRuleApplier with a
	 * {@link RestrictedChaseHaltingCondition} and the given homomorphism solver.
	 */
	public AbstractRuleApplier(Homomorphism<? super ConjunctiveQuery, ? super T> homomorphismSolver) {
		this(homomorphismSolver, new RestrictedChaseHaltingCondition());
	}

	/**
	 * Construct a DefaultRuleApplier with the given HaltingCondition.
	 * 
	 * @param haltingCondition
	 */
	public AbstractRuleApplier(ChaseHaltingCondition haltingCondition) {
		this(SmartHomomorphism.instance(), haltingCondition);
	}

	/**
	 * Construct a DefaultRuleApplier with the given HaltingCondition,
	 * homomorphism solver and SymbolGenerator
	 * 
	 * @param haltingCondition
	 * @param homomorphismSolver
	 */
	public AbstractRuleApplier(Homomorphism<? super ConjunctiveQuery, ? super T> homomorphismSolver,
	    ChaseHaltingCondition haltingCondition) {
		this.haltingCondition = haltingCondition;
		this.solver = homomorphismSolver;
	}

	// //////////////////////////////////////////////////////////////////////////
	// METHODS
	// //////////////////////////////////////////////////////////////////////////
	
	protected abstract ConjunctiveQuery generateQuery(Rule rule);

	@Override
	public boolean apply(Rule rule, T atomSet) throws RuleApplicationException {
		boolean isChanged = false;
		ConjunctiveQuery query = this.generateQuery(rule);

		try {
			CloseableIterator<Substitution> subIt = this.executeQuery(query, atomSet);
			while (subIt.hasNext()) {
				Substitution substitution = subIt.next();
				CloseableIterator<Atom> it = this.getHaltingCondition().apply(rule, substitution, atomSet);
				if (it.hasNext()) {
					atomSet.addAll(it);
					isChanged = true;
				}
			}
			subIt.close();
		} catch (HomomorphismFactoryException e) {
			throw new RuleApplicationException("Error during rule application", e);
		} catch (HomomorphismException e) {
			throw new RuleApplicationException("Error during rule application", e);
		} catch (AtomSetException e) {
			throw new RuleApplicationException("Error during rule application", e);
		} catch (IteratorException e) {
			throw new RuleApplicationException("Error during rule application", e);
		}

		return isChanged;
	}

	@Override
	public CloseableIterator<Atom> delegatedApply(Rule rule, T atomSet) throws RuleApplicationException {		
		return delegatedApply(rule, atomSet, atomSet);
	}

	@Override
	public CloseableIterator<Atom> delegatedApply(Rule rule, T atomSetOnWichQuerying, T atomSetOnWichCheck)
	    throws RuleApplicationException {		
		ConjunctiveQuery query = this.generateQuery(rule);
		CloseableIterator<Substitution> subIt;
		try {
			subIt = this.executeQuery(query, atomSetOnWichQuerying);
		} catch (HomomorphismFactoryException e) {
			throw new RuleApplicationException("Error during rule application", e);
		} catch (HomomorphismException e) {
			throw new RuleApplicationException("Error during rule application", e);
		}
		return new RuleApplierIterator(subIt, rule, atomSetOnWichCheck, haltingCondition);
	}

	// //////////////////////////////////////////////////////////////////////////
	//
	// //////////////////////////////////////////////////////////////////////////

	protected ChaseHaltingCondition getHaltingCondition() {
		return this.haltingCondition;
	}

	protected CloseableIterator<Substitution> executeQuery(ConjunctiveQuery query, T atomSet)
	    throws HomomorphismFactoryException, HomomorphismException {
		return this.solver.execute(query, atomSet);
	}

}