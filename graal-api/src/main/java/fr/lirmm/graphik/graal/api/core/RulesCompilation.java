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
 package fr.lirmm.graphik.graal.api.core;

import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.lang3.tuple.Pair;

import fr.lirmm.graphik.util.Partition;
import fr.lirmm.graphik.util.profiler.Profilable;

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
	 * Return true if the given rule is compilable by this kind of rules 
	 * compilation.
	 */
	public boolean isCompilable(Rule r);

	/**
	 * Return true iff there is a way to rewrite an atom with the predicate
	 * father into an atom with the predicate son.
	 * 
	 */
	public boolean isMappable(Predicate father, Predicate son);
	
	/**
	 * Return the list of c-homomorphisms of the atom father to the atom son. <br>
	 * ie. return all the homomorphisms that map father with a fact implied from
	 * the atom son with compiled rules.
	 */
	public Collection<Substitution> homomorphism(Atom father, Atom son);

	/**
	 * Return the list of c-homomorphisms of the atom father to the atom son
	 * such that it respects s. <br>
	 * ie. return all the homomorphisms that map father with a fact implied from
	 * the atom son with compiled rules.
	 */
	public Collection<Substitution> homomorphism(Atom father, Atom son, Substitution s);

	/**
	 * Return the list of c-unifier from the atom father to the atom son
	 */
	public Collection<Partition<Term>> getUnification(Atom father, Atom son);

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
	public Collection<Pair<Atom, Substitution>> getRewritingOf(Atom father);

	/**
	 * Return a collection of predicate unifiable with the given one
	 */
	public Collection<Predicate> getUnifiablePredicate(Predicate p);

	/**
	 * Filters redundant atom in the specified atom-set with respect to this rule compilation.
	 * @param atomSet
	 * @return an InMemoryAtomSet which is a subset of the specified one and which is equivalent to with respect to this rule compilation.
	 */
	InMemoryAtomSet getIrredondant(InMemoryAtomSet atomSet);

}
