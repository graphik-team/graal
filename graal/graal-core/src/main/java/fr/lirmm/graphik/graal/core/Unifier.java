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
		// TODO: check, why do you do that?
		/*FreshVarSubstitution substitution = new FreshVarSubstitution();
		InMemoryAtomSet atomsetSubstitut = substitution.createImageOf(atomset);*/

		Substitution s1 = Unifier.computeInitialRuleTermsSubstitution(rule);
		Substitution s2 = Unifier.computeInitialAtomSetTermsSubstitution(atomset);

		Rule rule_fresh = s1.createImageOf(rule);
		AtomSet atomset_fresh = s2.createImageOf(atomset);


		Queue<Atom> atomQueue = new LinkedList<Atom>();
		for (Atom a : atomset_fresh/*Substitut*/) {
			atomQueue.add(a);
		}

		for (Atom a : atomset_fresh/*Substitut*/) {
			Queue<Atom> tmp = new LinkedList<Atom>(atomQueue);
			if (existExtendedUnifier(rule_fresh, tmp, a, new TreeMapSubstitution(), filter)) {
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

					for (Term t1 : a) {
						for (Term t2 : existentialVars) {
							if (u.createImageOf(t2).equals(u.createImageOf(t1))) {
								newPieceElement = a;
								break;
							}
						}
					}

				}

				if (newPieceElement == null) {
					if (filter.filter(u)) {
						unifierCollection.add(u);
					}
				} else {
					unifierCollection.addAll(extendUnifier(rule, new LinkedList<Atom>(atomset), newPieceElement, u,
					        filter));
				}
			}
		}
		return unifierCollection;
	}

	private static Substitution unifier(Substitution baseUnifier, Atom a1,
			Atom a2, Set<Term> frontierVars, Set<Term> existentialVars) {
		if (a1.getPredicate().equals(a2.getPredicate())) {
			boolean error = false;
			Substitution u = SubstitutionFactory.instance()
					.createSubstitution();

			u.put(baseUnifier);

			for (int i = 0; i < a1.getPredicate().getArity(); ++i) {
				Term t1 = a1.getTerm(i);
				Term t2 = a2.getTerm(i);
				error = error || !compose(u, frontierVars, existentialVars, t1, t2);
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

		if (!termSubstitut.equals(substitutSubstitut)) {
			if (Term.Type.CONSTANT.equals(termSubstitut.getType()) || existentials.contains(termSubstitut)) {
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
				} 
				else {
					if (existExtendedUnifier(rule, atomset, newPieceElement, u, filter)) {
						return true;
					}
				}
			}
		}
		return false;
	}

}
