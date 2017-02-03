/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2016)
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

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.graal.core.factory.SubstitutionFactory;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.util.LinkedSet;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;
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
	 * @return a Set of Substitution representing the piece unifiers between the head of the specified rule and the specified atomset. 
	 */
	public Set<Substitution> computePieceUnifier(Rule rule, InMemoryAtomSet atomset) {
		return computePieceUnifier(rule,atomset,new Filter<Substitution>() { 
			@Override
			public boolean filter(Substitution s) { return true; } 
		} );
	}

	public Set<Substitution> computePieceUnifier(Rule rule, InMemoryAtomSet set, Filter<Substitution> filter) {

		Substitution s1 = Unifier.computeInitialSourceTermsSubstitution(rule);
		Substitution s2 = Unifier.computeInitialTargetTermsSubstitution(set);

		Rule r1 = s1.createImageOf(rule);
		InMemoryAtomSet atomset = s2.createImageOf(set);

		Set<Substitution> unifiers = new LinkedSet<Substitution>();
		Queue<Atom> atomQueue = new LinkedList<Atom>();
		CloseableIteratorWithoutException<Atom> it = atomset.iterator();
		while (it.hasNext()) {
			Atom a = it.next();
			atomQueue.add(a);
		}

		it = atomset.iterator();
		while (it.hasNext()) {
			Atom a = it.next();
			Queue<Atom> tmp = new LinkedList<Atom>(atomQueue);
			unifiers.addAll(extendUnifier(r1, tmp, a, new TreeMapSubstitution(), filter));
		}
		return unifiers;
	}

	public boolean existPieceUnifier(Rule rule, InMemoryAtomSet atomset) {
		return existPieceUnifier(rule,atomset,new Filter<Substitution>() { @Override
		public boolean filter(Substitution s) { return true; } } );
	}

	public boolean existPieceUnifier(Rule rule, InMemoryAtomSet set, Filter<Substitution> filter) {

		Substitution s1 = Unifier.computeInitialSourceTermsSubstitution(rule);
		Substitution s2 = Unifier.computeInitialTargetTermsSubstitution(set);

		Rule r1 = s1.createImageOf(rule);
		InMemoryAtomSet atomset = s2.createImageOf(set);

		Queue<Atom> atomQueue = new LinkedList<Atom>();
		CloseableIteratorWithoutException<Atom> it = atomset.iterator();
		while (it.hasNext()) {
			Atom a = it.next();
			atomQueue.add(a);
		}

		it = atomset.iterator();
		while (it.hasNext()) {
			Atom a = it.next();
			Queue<Atom> tmp = new LinkedList<Atom>(atomQueue);
			if (existExtendedUnifier(r1, tmp, a, new TreeMapSubstitution(), filter)) {
				return true;
			}
		}
		return false;
	}

	public static Substitution computeInitialSourceTermsSubstitution(Rule rule) {
		Substitution s = new TreeMapSubstitution();

		for (Variable t1 : rule.getVariables()) {
			Variable t1b = DefaultTermFactory.instance().createVariable("S::" + t1.getIdentifier().toString());
			s.put(t1, t1b);
		}

		return s;
	}

	public static Substitution computeInitialTargetTermsSubstitution(InMemoryAtomSet set) {
		Substitution s = new TreeMapSubstitution();

		for (Variable t2 : set.getVariables()) {
			Variable t2b = DefaultTermFactory.instance().createVariable("T::" + t2.getIdentifier().toString());
			s.put(t2, t2b);
		}

		return s;
	}

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE FUNCTIONS
	// /////////////////////////////////////////////////////////////////////////

	private static Collection<Substitution> extendUnifier(
	                                                      Rule rule,
	                                                      Queue<Atom> atomset,
	                                                      Atom pieceElement,
	                                                      Substitution unifier,
	                                                      Filter<Substitution> filter) {
		atomset.remove(pieceElement);
		Collection<Substitution> unifierCollection = new LinkedList<Substitution>();
		Set<Variable> frontierVars = rule.getFrontier();
		Set<Variable> existentialVars = rule.getExistentials();

		CloseableIteratorWithoutException<Atom> it = rule.getHead().iterator();
		while (it.hasNext()) {
			Atom atom = it.next();
			Substitution u = unifier(unifier, pieceElement, atom, frontierVars,
					existentialVars);
			if (u != null) {
				Iterator<Atom> it2 = atomset.iterator();
				Atom newPieceElement = null;
				while (it2.hasNext() && newPieceElement == null) {
					Atom a = it2.next();

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

	private static boolean existExtendedUnifier(
	                                            Rule rule,
	                                            Queue<Atom> atomset,
	                                            Atom pieceElement,
	                                            Substitution unifier,
	                                            Filter<Substitution> filter) {
		atomset.remove(pieceElement);
		Collection<Substitution> unifierCollection = new LinkedList<Substitution>();
		Set<Variable> frontierVars = rule.getFrontier();
		Set<Variable> existentialVars = rule.getExistentials();

		CloseableIteratorWithoutException<Atom> it = rule.getHead().iterator();
		while (it.hasNext()) {
			Atom atom = it.next();
			Substitution u = unifier(unifier, pieceElement, atom, frontierVars, existentialVars);
			if (u != null) {
				Iterator<Atom> it2 = atomset.iterator();
				Atom newPieceElement = null;
				while (it2.hasNext() && newPieceElement == null) {
					Atom a = it2.next();

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
						return true;
					}
				} else {
					if (existExtendedUnifier(rule, new LinkedList<Atom>(atomset), newPieceElement, u, filter)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private static Substitution unifier(Substitution baseUnifier, Atom a1,
	    Atom a2, Set<Variable> frontierVars, Set<Variable> existentialVars) {
		if (a1.getPredicate().equals(a2.getPredicate())) {
			boolean error = false;
			Substitution u = SubstitutionFactory.instance().createSubstitution();
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

	private static boolean compose(Substitution u, Set<Variable> frontierVars, Set<Variable> existentials, Term term,
	    Term substitut) {
		Term termSubstitut = u.createImageOf(term);
		Term substitutSubstitut = u.createImageOf(substitut);

		if (!termSubstitut.equals(substitutSubstitut)) {
			if (termSubstitut.isConstant() || existentials.contains(termSubstitut)) {
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

	private static boolean put(Substitution u, Set<Variable> frontierVars, Set<Variable> existentials, Term term,
	    Term substitut) {
		if (!term.equals(substitut)) {
			// two (constant | existentials vars)
			if (term.isConstant()
					|| existentials.contains(term)) {
				return false;
				// fr -> existential vars
			} else if (frontierVars.contains(term)
					&& existentials.contains(substitut)) {
				return false;
			}
			u.put((Variable) term, substitut);
		}
		return true;
	}

}
