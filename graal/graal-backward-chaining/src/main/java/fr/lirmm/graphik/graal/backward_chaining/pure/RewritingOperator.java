/* Graal v0.7.4
 * Copyright (c) 2014-2015 Inria Sophia Antipolis - Méditerranée / LIRMM (Université de Montpellier & CNRS)
 * All rights reserved.
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * Author(s): Clément SIPIETER
 *            Mélanie KÖNIG
 *            Swan ROCHER
 *            Jean-François BAGET
 *            Michel LECLÈRE
 *            Marie-Laure MUGNIER
 */
 /**
 * 
 */
package fr.lirmm.graphik.graal.backward_chaining.pure;

import java.util.Collection;

import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.ruleset.IndexedByHeadPredicatesRuleSet;
import fr.lirmm.graphik.util.Profilable;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public interface RewritingOperator extends Profilable {
	
	/**
	 * Returns the rewrites compute from the given fact and the rule set of the
	 * receiving object.
	 * 
	 * @param q
	 *            A fact
	 * @return the ArrayList that contains the rewrites compute from the given
	 *         fact and the rule set of the receiving object.
	 * @throws Exception
	 */
	Collection<ConjunctiveQuery> getRewritesFrom(ConjunctiveQuery q, IndexedByHeadPredicatesRuleSet ruleSet, RulesCompilation compilation);
}
