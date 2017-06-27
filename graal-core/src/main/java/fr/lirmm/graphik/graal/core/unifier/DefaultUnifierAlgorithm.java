/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2017)
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
package fr.lirmm.graphik.graal.core.unifier;

import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.unifier.DependencyChecker;
import fr.lirmm.graphik.graal.api.core.unifier.UnifierAlgorithm;
import fr.lirmm.graphik.graal.api.core.unifier.UnifierChecker;
import fr.lirmm.graphik.graal.core.ConjunctiveQueryRuleAdapter;
import fr.lirmm.graphik.graal.core.VariablePrefixSubstitution;
import fr.lirmm.graphik.graal.core.VariableRemovePrefixSubstitution;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class DefaultUnifierAlgorithm implements UnifierAlgorithm {

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////////
	// SINGLETON
	// /////////////////////////////////////////////////////////////////////////

	private static DefaultUnifierAlgorithm instance;

	protected DefaultUnifierAlgorithm() {
		super();
	}

	public static synchronized DefaultUnifierAlgorithm instance() {
		if (instance == null)
			instance = new DefaultUnifierAlgorithm();

		return instance;
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Computes piece unifiers between the specified query and the head of the specified rule.
	 * The rule and the query must not contain variables with same names. You can use {@link #getSourceVariablesSubstitution()} and
	 * {@link #getTargetVariablesSubstitution()} to obtain equivalent rule and query without conflict on variable names.
	 */
	public CloseableIteratorWithoutException<Substitution> computePieceUnifier(Rule rule, InMemoryAtomSet query, UnifierChecker... filters) {
		return new UnifierIterator(rule, new ConjunctiveQueryRuleAdapter(query), filters);
	}
	
	/**
	 * Computes piece unifiers between the head of the first rule and the body of the second.
	 * The rules must not contain variables with same names. You can use {@link #getSourceVariablesSubstitution()} and
	 * {@link #getTargetVariablesSubstitution()} to obtain equivalent rules without conflict on variable names.
	 */
	public CloseableIteratorWithoutException<Substitution> computePieceUnifier(Rule rule, Rule target, DependencyChecker... filters) {
		return new UnifierIterator(rule, target, filters);
	}

	/**
	 * Check if there exist a piece unifier between the specified query and the head of the specified rule.
	 * The rule and the query must not contain variables with same names. You can use {@link #getSourceVariablesSubstitution()} and
	 * {@link #getTargetVariablesSubstitution()} to obtain equivalent rules without conflict on variable names.
	 */
	public boolean existPieceUnifier(Rule rule, InMemoryAtomSet query, UnifierChecker... filters) {
		 UnifierIterator it = new UnifierIterator(rule, new ConjunctiveQueryRuleAdapter(query), filters);
		 boolean res = it.hasNext();
		 it.close();
		 return res;
	}
	
	/**
	 * Check if there exist piece unifiers between the head of the first rule and the body of the second.
	 * The rules must not contain variables with same names. You can use {@link #getSourceVariablesSubstitution()} and
	 * {@link #getTargetVariablesSubstitution()} to obtain equivalent rules without conflict on variable names.
	 */
	public boolean existPieceUnifier(Rule source, Rule target, DependencyChecker... filters) {
		 UnifierIterator it = new UnifierIterator(source, target, filters);
		 boolean res = it.hasNext();
		 it.close();
		 return res;
	}

	
	// /////////////////////////////////////////////////////////////////////////
	// OBJECT OVERRIDE METHODS
	// /////////////////////////////////////////////////////////////////////////
		
	private static Substitution sourceSubstitution = new VariablePrefixSubstitution("S::");
	public static Substitution getSourceVariablesSubstitution() {
		return sourceSubstitution;
	}
	
	private static Substitution targetSubstitution = new VariablePrefixSubstitution("T::");
	public static Substitution getTargetVariablesSubstitution() {
		return targetSubstitution;
	}
	
	private static Substitution sourceReverseSubstitution = new VariableRemovePrefixSubstitution("S::");	
	public static Substitution getReverseSourceVariablesSubstitution() {
		return sourceReverseSubstitution;
	}
	
	private static Substitution targetReverseSubstitution = new VariableRemovePrefixSubstitution("T::");
	public static Substitution getReverseTargetVariablesSubstitution() {
		return targetReverseSubstitution;
	}

}
