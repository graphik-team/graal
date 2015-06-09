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
 package fr.lirmm.graphik.graal.core.ruleset;

import java.util.Iterator;

import fr.lirmm.graphik.graal.core.Rule;

public interface RuleSet extends ImmutableRuleSet {

	public boolean add(Rule rule);
	
	public boolean addAll(Iterator<Rule> ruleIterator);

	public boolean remove(Rule rule);
	
	public boolean removeAll(Iterator<Rule> ruleIterator);

	@Override
	public boolean contains(Rule rule);

	@Override
	public Iterator<Rule> iterator();

};
