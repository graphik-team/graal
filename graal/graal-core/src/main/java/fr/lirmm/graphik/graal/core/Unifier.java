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
package fr.lirmm.graphik.graal.core;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.InMemoryAtomSet;
import fr.lirmm.graphik.graal.core.factory.SubstitutionFactory;
import fr.lirmm.graphik.graal.core.impl.FreshVarSubstitution;
import fr.lirmm.graphik.graal.core.impl.TreeMapSubstitution;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.core.term.Term;
import fr.lirmm.graphik.util.LinkedSet;
import fr.lirmm.graphik.util.stream.filter.Filter;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
public class Unifier {

	private static Unifier instance;

	private Unifier() {
	}

	public static synchronized Unifier instance() {
		if (instance == null)
			instance = new Unifier();

		return instance;
	}

	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * @param rule
	 * @param atomset
	 * @return
	 */
	public Set<Substitution> computePieceUnifier(Rule rule, InMemoryAtomSet atomset) {
		return computePieceUnifier(rule,atomset,new Filter<Substitution>() { 
			@Override
			public boolean filter(Substitution s) { return true; } 
		} );
	}

	public static Substitution computeInitialRuleTermsSubstitution(Rule rule) {
		Substitution s = new TreeMapSubstitution();

		for (Term t1 : rule.getTerms(Term.Type.VARIABLE)) {
			Term t1b = DefaultTermFactory.instance().createVariable(
					"D::"
					+ t1.getIdentifier().toString());
			s.put(t1, t1b);
		}

		return s;
	}

	public static Substitution computeInitialAtomSetTermsSubstitution(InMemoryAtomSet set) {
		Substitution s = new TreeMapSubstitution();

		for (Term t2 : set.getTerms(Term.Type.VARIABLE)) {
			Term t2b = DefaultTermFactory.instance().createVariable(
					"R::" + t2.getIdentifier().toString());
			s.put(t2, t2b);
		}

		return s;
	}

	public Set<Substitution> computePieceUnifier(Rule rule, InMemoryAtomSet set, Filter<Substitution> filter) {
		Rule r1;
		AtomSet atomset;

		Substitution s1 = Unifier.computeInitialRuleTermsSubstitution(rule);
		Substitution s2 = Unifier.computeInitialAtomSetTermsSubstitution(set);

		r1 = s1.createImageOf(rule);
		atomset = s2.createImageOf(set);

		Set<Substitution> unifiers = new LinkedSet<Substitution>();
		Queue<Atom> atomQueue = new LinkedList<Atom>();
		for (Atom a : atomset) {
			atomQueue.add(a);
		}

		for (Atom a : atomset) {
			Queue<Atom> tmp = new LinkedList<Atom>(atomQueue);
			unifiers.addAll(extendUnifier(r1, tmp, a, new TreeMapSubstitution(), filter));
		}
		return unifiers;
	}

	public boolean existPieceUnifier(Rule rule, InMemoryAtomSet atomset) {
		return existPieceUnifier(rule,atomset,new Filter<Substitution>() { @Override
		public boolean filter(Substitution s) { return true; } } );
	}

	public boolean existPieceUnifier(Rule rule, InMemoryAtomSet atomset,
			Filter<Substitution> filter) {
		FreshVarSubstitution substitution = new FreshVarSubstitution();
		InMemoryAtomSet atomsetSubstitut = substitution.createImageOf(atomset);

		Queue<Atom> atomQueue = new LinkedList<Atom>();
		for (Atom a : atomsetSubstitut) {
			atomQueue.add(a);
		}

		for (Atom a : atomsetSubstitut) {
			Queue<Atom> tmp = new LinkedList<Atom>(atomQueue);
			if (existExtendedUnifier(rule, tmp, a, new TreeMapSubstitution(), filter)) {
				return true;
			}
		}
		return false;
	}

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE FUNCTIONS
	// /////////////////////////////////////////////////////////////////////////

	private static Collection<Substitution> extendUnifier(Rule rule,
			Queue<Atom> atomset, Atom pieceElement, Substitution unifier, Filter<Substitution> filter) {
		atomset.remove(pieceElement);
		Collection<Substitution> unifierCollection = new LinkedList<Substitution>();
		Set<Term> frontierVars = rule.getFrontier();
		Set<Term> existentialVars = rule.getExistentials();

		for (Atom atom : rule.getHead()) {
			Substitution u = unifier(unifier, pieceElement, atom, frontierVars,
					existentialVars);
			if (u != null) {
				Iterator<Atom> it = atomset.iterator();
				Atom newPieceElement = null;
				while (it.hasNext() && newPieceElement == null) {
					Atom a = it.next();

					for (Term t : a) {
						if (existentialVars.contains(u.createImageOf(t))) {
							newPieceElement = a;
							break;
						}
					}

				}

				if (newPieceElement == null) {
					if (filter.filter(u)) {
						unifierCollection.add(u);
					}
				} else {
					unifierCollection.addAll(extendUnifier(rule, atomset, newPieceElement, u, filter));
				}
			}
		}
		return unifierCollection;
	}

	private static Substitution unifier(Substitution baseUnifier, Atom a1,
			Atom atomFromHead, Set<Term> frontierVars, Set<Term> existentialVars) {
		if (a1.getPredicate().equals(atomFromHead.getPredicate())) {
			boolean error = false;
			Substitution u = SubstitutionFactory.instance()
					.createSubstitution();
			u.put(baseUnifier);
			for (int i = 0; i < a1.getPredicate().getArity(); ++i) {
				Term t1 = a1.getTerm(i);
				Term t2 = atomFromHead.getTerm(i);
				if (!t1.equals(t2)) {
					if (Term.Type.VARIABLE.equals(t1.getType())) {
						if (!compose(u, frontierVars, existentialVars, t1, t2))
							error = true;
					} else if (Term.Type.VARIABLE.equals(t2.getType())
							&& !existentialVars.contains(t2)) {
						if (!compose(u, frontierVars, existentialVars, t2, t1))
							error = true;
					} else {
						error = true;
					}
				}
			}

			if (!error)
				return u;
		}

		return null;
	}

	private static boolean compose(Substitution u, Set<Term> frontierVars,
			Set<Term> existentials, Term term, Term substitut) {
		Term termSubstitut = u.createImageOf(term);
		Term substitutSubstitut = u.createImageOf(substitut);

		if (Term.Type.CONSTANT.equals(termSubstitut.getType())
				|| existentials.contains(termSubstitut)) {
			Term tmp = termSubstitut;
			termSubstitut = substitutSubstitut;
			substitutSubstitut = tmp;
		}

		for (Term t : u.getTerms()) {
			if (termSubstitut.equals(u.createImageOf(t))) {
				if (!put(u, frontierVars, existentials, t, substitutSubstitut)) {
					return false;
				}
			}
		}

		if (!put(u, frontierVars, existentials, termSubstitut, substitutSubstitut)) {
			return false;
		}
		return true;
	}

	private static boolean put(Substitution u, Set<Term> frontierVars,
			Set<Term> existentials, Term term, Term substitut) {
		if (!term.equals(substitut)) {
			// two (constant | existentials vars)
			if (Term.Type.CONSTANT.equals(term.getType())
					|| existentials.contains(term)) {
				return false;
				// fr -> existential vars
			} else if (frontierVars.contains(term)
					&& existentials.contains(substitut)) {
				return false;
			}
		}
		return u.put(term, substitut);
	}

	private static boolean existExtendedUnifier(Rule rule, Queue<Atom> atomset,
			Atom pieceElement, Substitution unifier, Filter<Substitution> filter) {
		atomset.remove(pieceElement);
		Set<Term> frontierVars = rule.getFrontier();
		Set<Term> existentialVars = rule.getExistentials();

		for (Atom atom : rule.getHead()) {
			Substitution u = unifier(unifier, pieceElement, atom, frontierVars,
					existentialVars);
			if (u != null) {
				Iterator<Atom> it = atomset.iterator();
				Atom newPieceElement = null;
				while (it.hasNext() && newPieceElement == null) {
					Atom a = it.next();

					for (Term t : a) {
						if (existentialVars.contains(u.createImageOf(t))) {
							newPieceElement = a;
							break;
						}
					}

				}

				if (newPieceElement == null) {
					if (filter.filter(u)) {
						return true;
					}
				} else {
					if (existExtendedUnifier(rule, atomset, newPieceElement, u, filter)) {
						return true;
					}
				}
			}
		}
		return false;
	}

}
