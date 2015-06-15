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
 package fr.lirmm.graphik.graal.backward_chaining.pure;

import java.util.Collection;
import java.util.Iterator;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.util.Profilable;

public interface RulesCompilation extends Profilable {

	/**
	 * Extract compilable rules from the specified ruleset and compile them.
	 * @param ruleset
	 */
	public void compile(Iterator<Rule> ruleset);
	
	/**
	 * Load compilation from a preprocessed ruleset
	 * @param ruleset
	 * @param compilation 
	 */
	public void load(Iterator<Rule> ruleset, Iterator<Rule> compilation);

	public Iterable<Rule> getSaturation();

	/**
	 * Unfold the pivot rewriting set with this rules compilation.
	 * 
	 * @param pivotRewritingSet
	 * @return
	 */
	Iterable<ConjunctiveQuery> unfold(
			Iterable<ConjunctiveQuery> pivotRewritingSet);

	/**
	 * Return true if the given rule is compilable by this kind of rules 
	 * compilation.
	 */
	public boolean isCompilable(Rule r);

	/**
	 * Return true iff there is a c-homomorphism from the atom father to the
	 * atom son i. e. there exist a fact that is implied from the atom son with
	 * compiled rules, and s. t. the atom father can be mapped to this fact by
	 * an homomorphism
	 */
	public boolean isMappable(Atom father, Atom son);

	/**
	 * Return the list of c-homomorphisms of the atom father to the atom son i.
	 * e. return all the homomorphisms that map father with a fact implied from
	 * the atom son with compiled rules
	 */
	// public Collection<Substitution> getMapping(Atom father, Atom son);

	/**
	 * Return true iff there is a c-unifier from the atom father to the atom son
	 * i. e. a substitution from the variables of father and son to the terms of
	 * father and son such that the image of son implies the image of father
	 * with the compiled rules
	 */
	public boolean isUnifiable(Atom father, Atom son);

	/**
	 * Return the list of c-unifier from the atom father to the atom son
	 */
	public Collection<TermPartition> getUnification(Atom father, Atom son);

	/**
	 * Return true iff the atom father is implied from the atom son with
	 * compiled rules (son -> father) i. e. the atom son is a R-rewriting of the
	 * atom father by compiled rules
	 */
	public boolean isImplied(Atom father, Atom son);

	/**
	 * Return the list of atom that are R-rewriting of the atom father by
	 * compiled rules
	 */
	public Collection<Atom> getRewritingOf(Atom father);

	/**
	 * Return a collection of predicate unifiable with the given one
	 */
	public Collection<Predicate> getUnifiablePredicate(Predicate p);

	/**
	 * @param atomSet
	 * @return
	 */
	AtomSet getIrredondant(AtomSet atomSet);

}
