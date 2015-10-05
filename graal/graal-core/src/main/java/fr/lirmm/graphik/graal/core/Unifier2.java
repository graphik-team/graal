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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.factory.AtomSetFactory;
import fr.lirmm.graphik.graal.core.factory.SubstitutionFactory;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.util.stream.filter.Filter;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
public class Unifier2 {

	private static Unifier2 instance;

	private Unifier2() {
	}

	public static synchronized Unifier2 instance() {
		if (instance == null)
			instance = new Unifier2();

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
		return computePieceUnifier(rule, atomset, new Filter<Substitution>() {
			@Override
			public boolean filter(Substitution s) {
				return true;
			}
		});
	}

	/**
	 * This method consider that the specified rule is a mono piece rule (only
	 * one piece in its head)
	 * 
	 * @param rule
	 * @param set
	 * @param filter
	 * @return
	 */
	public Set<Substitution> computePieceUnifier(Rule rule, InMemoryAtomSet set, Filter<Substitution> filter) {
		Rule renamedRule;
		AtomSet renamedSet;

		Substitution s1 = Unifier2.computeInitialRuleTermsSubstitution(rule);
		Substitution s2 = Unifier2.computeInitialAtomSetTermsSubstitution(set);

		renamedRule = s1.createImageOf(rule);
		renamedSet = s2.createImageOf(set);

		Queue<Atom> query = new LinkedList<Atom>();
		for (Atom a : renamedSet) {
			query.add(a);
		}

//		for (Atom selectedAtom : renamedSet) {
//			Queue<Atom> queue = new LinkedList<Atom>(atomQueue);
//			unifiers.addAll(extendUnifier(renamedRule, queue, selectedAtom, new TreeMapSubstitution(), filter));
//		}
		
		Set<Substitution> unifiers = null; //this.computeMonoPieceUnifier(renamedRule, query);
		
		return unifiers;
	}

	public boolean existPieceUnifier(Rule rule, InMemoryAtomSet atomset) {
		return existPieceUnifier(rule, atomset, new Filter<Substitution>() {
			@Override
			public boolean filter(Substitution s) {
				return true;
			}
		});
	}

	public boolean existPieceUnifier(Rule rule, InMemoryAtomSet atomset, Filter<Substitution> filter) {
		// TODO: check, why do you do that?
		/*
		 * FreshVarSubstitution substitution = new FreshVarSubstitution();
		 * InMemoryAtomSet atomsetSubstitut = substitution.createImageOf(atomset);
		 */

		Substitution s1 = Unifier2.computeInitialRuleTermsSubstitution(rule);
		Substitution s2 = Unifier2.computeInitialAtomSetTermsSubstitution(atomset);

		Rule rule_fresh = s1.createImageOf(rule);
		AtomSet atomset_fresh = s2.createImageOf(atomset);

		Queue<Atom> atomQueue = new LinkedList<Atom>();
		for (Atom a : atomset_fresh/* Substitut */) {
			atomQueue.add(a);
		}

		for (Atom a : atomset_fresh/* Substitut */) {
			Queue<Atom> tmp = new LinkedList<Atom>(atomQueue);
			if (existExtendedUnifier(rule_fresh, tmp, a, new TreeMapSubstitution(), filter)) {
				return true;
			}
		}
		return false;
	}

	// /////////////////////////////////////////////////////////////////////////
	// STATIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	public static Substitution computeInitialRuleTermsSubstitution(Rule rule) {
		Substitution s = new TreeMapSubstitution();

		for (Term t1 : rule.getTerms(Term.Type.VARIABLE)) {
			Term t1b = DefaultTermFactory.instance().createVariable("D::" + t1.getIdentifier().toString());
			s.put(t1, t1b);
		}

		return s;
	}

	public static Substitution computeInitialAtomSetTermsSubstitution(InMemoryAtomSet set) {
		Substitution s = new TreeMapSubstitution();

		for (Term t2 : set.getTerms(Term.Type.VARIABLE)) {
			Term t2b = DefaultTermFactory.instance().createVariable("R::" + t2.getIdentifier().toString());
			s.put(t2, t2b);
		}

		return s;
	}

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE FUNCTIONS
	// /////////////////////////////////////////////////////////////////////////

	protected LinkedList<Substitution> getSinglePieceUnifiers(ConjunctiveQuery q, Rule r) {
//		if (atomic)
//			if (!(compilation instanceof IDCompilation))
//				return getSinglePieceUnifiersAHR(q, (AtomicHeadRule) r, compilation);
//			else {
//				if (LOGGER.isWarnEnabled()) {
//					LOGGER.warn("IDCompilation is not compatible with atomic unification");
//				}
//				return getSinglePieceUnifiersNAHR(q, r, compilation);
//			}
//		else {
			return getSinglePieceUnifiersNAHR(q, r);
		// }
	}

	/**
	 * Returns the list of all single-piece unifier between the given query and
	 * the given rule
	 * 
	 * @param query
	 *            the query that we want unify
	 * @param r
	 *            the atomic-head rule that we want unify
	 * @return the ArrayList of all single-piece unifier between the query of
	 *         the receiving object and R an atomic-head rule
	 * @throws Exception
	 */
	private LinkedList<Substitution> getSinglePieceUnifiersNAHR(ConjunctiveQuery q, Rule r) {
		Set<Term> frontierVars = r.getFrontier();
		Set<Term> existentialVars = r.getExistentials();

		LinkedList<Substitution> unifiers = new LinkedList<Substitution>();
		HashMap<Atom, LinkedList<Substitution>> possibleUnification = new HashMap<Atom, LinkedList<Substitution>>();

		// compute possible unification between atoms of Q and head(R)
		for (Atom qAtom : q) {
			for (Atom rAtom : r.getHead()) {
				Substitution u = unifier(SubstitutionFactory.instance().createSubstitution(), qAtom, rAtom,
				        frontierVars, existentialVars);
				if (u != null) {
					if (possibleUnification.get(qAtom) == null)
						possibleUnification.put(qAtom, new LinkedList<Substitution>());
					possibleUnification.get(qAtom).add(u);
				}
			}
		}

		for (Map.Entry<Atom, LinkedList<Substitution>> e : possibleUnification.entrySet()) {
			Atom a = e.getKey();
			LinkedList<Substitution> unifierList = e.getValue();
			if (unifierList != null) {
				Iterator<Substitution> i = unifierList.iterator();
				while (i.hasNext()) {
					Substitution unif = i.next();
					InMemoryAtomSet p = new LinkedListAtomSet();
					p.add(a);
					// unifiers.addAll(extend(p, unif, possibleUnification, q,
					// r));
					i.remove();
				}
			}
		}

		return unifiers;
	}

	// private Collection<? extends Substitution> extend(
	// InMemoryAtomSet p,
	// Substitution unif,
	// HashMap<Atom, LinkedList<Substitution>> possibleUnification,
	// ConjunctiveQuery q,
	// Rule r) {
	// LinkedList<Substitution> u = new LinkedList<Substitution>();
	//
	// // compute separating variable
	// LinkedList<Term> sep = AtomSetUtils.sep(p, q.getAtomSet());
	// // compute sticky variable
	// LinkedList<Term> glue = unif.getStickyVariable(sep, r);
	// if (glue.isEmpty()) {
	// u.add(unif);
	// } else {
	// // compute Pext the atoms of Pbar linked to P by the sticky
	// // variables
	// InMemoryAtomSet pBar = AtomSetUtils.minus(q.getAtomSet(), p);
	// InMemoryAtomSet pExt = new LinkedListAtomSet();
	// for (Term t : glue) {
	// Iterator<Atom> ib = pBar.iterator();
	// while (ib.hasNext()) {
	// Atom b = ib.next();
	// if (b.getTerms().contains(t)) {
	// pExt.add(b);
	// ib.remove();
	// }
	// }
	// }
	// TermPartition part;
	// for (TermPartition uExt : preUnifier(pExt, r, possibleUnification)) {
	// part = unif.join(uExt);
	// if (part != null && part.isAdmissible(r)) {
	// u.addAll(extend(AtomSetUtils.union(p, pExt), part, possibleUnification,
	// q, r));
	// }
	// }
	//
	// }
	//
	// return u;
	// }

	private Set<Term> computeGlueVar(Substitution u, Set<Term> existentialVars, Set<Term> queryVars) {
		Set<Term> glueVars = new TreeSet<Term>();
		for (Term t : queryVars) {

		}
		for (Term t : existentialVars) {
			glueVars.add(u.createImageOf(t));
		}
		return glueVars;
	}

	// private static Collection<Substitution> computeMonoPieceUnifier(Rule
	// rule, Queue<Atom> query) {
	// Set<Term> frontierVars = rule.getFrontier();
	// Set<Term> existentialVars = rule.getExistentials();
	//
	// for (Atom aHead : rule.getHead()) {
	// List<Atom> queue = new LinkedList<Atom>(query);
	//
	// for (Atom aQuery : query) {
	// queue.remove(aQuery);
	// Substitution u =
	// unifier(SubstitutionFactory.instance().createSubstitution(), aQuery,
	// aHead,
	// frontierVars, existentialVars);
	// if (u != null) {
	// Set<Term> glueVars = new TreeSet<Term>();
	// for(Term t : existentialVars) {
	// glueVars.add(u.createImageOf(t));
	// }
	// InMemoryAtomSet newPieceElement = getAtomLinkedByGlueVars(glueVars,
	// queue);
	// SubstitutionReader subReader = null;
	// try {
	// subReader = PureHomomorphism.instance().execute(newPieceElement,
	// rule.getHead());
	// } catch (HomomorphismException e) {
	// // TODO treat this exception
	// e.printStackTrace();
	// throw new Error("Untreated exception");
	// }
	// }
	// }
	// }
	// }

	private static InMemoryAtomSet getAtomLinkedByGlueVars(Set<Term> glueVars, List<Atom> atoms) {
		InMemoryAtomSet atomLinked = AtomSetFactory.instance().createAtomSet();
		for (Atom a : atoms) {
			for (Term t : a) {
				if (glueVars.contains(t)) {
					atomLinked.add(a);
					break;
				}
			}
		}
		return atomLinked;
	}

	private static Collection<Substitution> extendUnifier(
	                                                      Rule rule,
	                                                      Queue<Atom> atomset,
	                                                      Atom pieceElement,
	                                                      Substitution unifier,
	                                                      Filter<Substitution> filter) {
		atomset.remove(pieceElement);
		Collection<Substitution> unifierCollection = new LinkedList<Substitution>();

		for (Atom atom : rule.getHead()) {
			Substitution u = null; // unifier(unifier, pieceElement, atom,
								   // frontierVars, existentialVars);
			if (u != null) {
				List<Atom> newPieceElement = new LinkedList<Atom>();

				atomset.remove(newPieceElement);

				if (filter.filter(u)) {
					unifierCollection.add(u);
				}
			}
		}
		return unifierCollection;
	}

	private static Substitution unifier(
	                                    Substitution baseUnifier,
	                                    Atom a1,
	                                    Atom atomFromHead,
	                                    Set<Term> frontierVars,
	                                    Set<Term> existentialVars) {
		if (a1.getPredicate().equals(atomFromHead.getPredicate())) {
			boolean error = false;
			Substitution u = SubstitutionFactory.instance().createSubstitution();
			u.put(baseUnifier);
			for (int i = 0; i < a1.getPredicate().getArity(); ++i) {
				Term t1 = a1.getTerm(i);
				Term t2 = atomFromHead.getTerm(i);
				if (!t1.equals(t2)) {
					if (Term.Type.VARIABLE.equals(t1.getType())) {
						if (!compose(u, frontierVars, existentialVars, t1, t2))
							error = true;
					} else if (Term.Type.VARIABLE.equals(t2.getType()) && !existentialVars.contains(t2)) {
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

	private static boolean compose(
	                               Substitution u,
	                               Set<Term> frontierVars,
	                               Set<Term> existentials,
	                               Term term,
	                               Term substitut) {
		Term termSubstitut = u.createImageOf(term);
		Term substitutSubstitut = u.createImageOf(substitut);

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
		return true;
	}

	private static boolean put(Substitution u, Set<Term> frontierVars, Set<Term> existentials, Term term, Term substitut) {
		if (!term.equals(substitut)) {
			// two (constant | existentials vars)
			if (Term.Type.CONSTANT.equals(term.getType()) || existentials.contains(term)) {
				return false;
				// fr -> existential vars
			} else if (frontierVars.contains(term) && existentials.contains(substitut)) {
				return false;
			}
		}
		return u.put(term, substitut);
	}

	private static boolean existExtendedUnifier(
	                                            Rule rule,
	                                            Queue<Atom> atomset,
	                                            Atom pieceElement,
	                                            Substitution unifier,
	                                            Filter<Substitution> filter) {
		atomset.remove(pieceElement);
		Set<Term> frontierVars = rule.getFrontier();
		Set<Term> existentialVars = rule.getExistentials();

		for (Atom atom : rule.getHead()) {
			Substitution u = unifier(unifier, pieceElement, atom, frontierVars, existentialVars);
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
