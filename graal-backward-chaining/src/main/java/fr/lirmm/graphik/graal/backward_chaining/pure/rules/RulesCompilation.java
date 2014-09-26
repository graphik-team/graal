package fr.lirmm.graphik.graal.backward_chaining.pure.rules;

import java.util.Collection;

import fr.lirmm.graphik.graal.backward_chaining.pure.utils.TermPartition;
import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.Substitution;

public interface RulesCompilation {

	/**
	 * Compute a Predicate Order from the set of rule. Remove from the set of
	 * rule the rule coded by the predicate order
	 * 
	 * @param arrayList
	 * @param rule_base
	 */
	public void code(Iterable<Rule> arrayList, String rule_base);

	/**
	 * Return true if the given rule is compilable by this
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
	public Collection<Substitution> getMapping(Atom father, Atom son);

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

}
