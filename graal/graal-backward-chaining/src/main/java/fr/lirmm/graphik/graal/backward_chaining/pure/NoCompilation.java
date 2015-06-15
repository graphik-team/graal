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
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.Rule;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class NoCompilation extends AbstractRulesCompilation {

	@Override
	public Iterable<ConjunctiveQuery> unfold(
			Iterable<ConjunctiveQuery> pivotRewritingSet) {
		return pivotRewritingSet;
	}
	
	@Override
	public void compile(Iterator<Rule> ruleset) {
	}

	@Override
	public void load(Iterator<Rule> ruleset, Iterator<Rule> compilation) {
	}

	@Override
	public Iterable<Rule> getSaturation() {
		return Collections.emptyList();
	}

	@Override
	public boolean isCompilable(Rule r) {
		return false;
	}

	@Override
	public boolean isMappable(Atom father, Atom son) {
		return false;
	}

	// @Override
	// public Collection<Substitution> getMapping(Atom father, Atom son) {
	// return Collections.emptyList();
	// }

	@Override
	public boolean isUnifiable(Atom father, Atom son) {
		return father.getPredicate().equals(son.getPredicate());
	}

	@Override
	public Collection<TermPartition> getUnification(Atom father, Atom son) {
		LinkedList<TermPartition> res = new LinkedList<TermPartition>();
		TermPartition p = TermPartition.getPartitionByPosition(father, son);
		if (p != null)
			res.add(p);
		return res;
	}

	@Override
	public boolean isImplied(Atom father, Atom son) {
		return false;
	}

	@Override
	public Collection<Atom> getRewritingOf(Atom father) {
		return Collections.singleton(father);
	}

	@Override
	public Collection<Predicate> getUnifiablePredicate(Predicate p) {
		return Collections.singleton(p);
	}

}
