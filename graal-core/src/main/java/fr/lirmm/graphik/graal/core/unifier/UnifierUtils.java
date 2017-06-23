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
package fr.lirmm.graphik.graal.core.unifier;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.RulesCompilation;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.core.DefaultVariableGenerator;
import fr.lirmm.graphik.graal.core.FreshVarSubstitution;
import fr.lirmm.graphik.graal.core.atomset.AtomSetUtils;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.factory.DefaultRuleFactory;
import fr.lirmm.graphik.util.Partition;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public final class UnifierUtils {

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	private UnifierUtils() {
	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	public static List<QueryUnifier> getSinglePieceUnifiersNAHR(ConjunctiveQuery q, Rule r,
	    RulesCompilation compilation) {
		LinkedList<QueryUnifier> u = new LinkedList<QueryUnifier>();
		Rule ruleCopy = getSafeCopy(r);
		HashMap<Atom, LinkedList<Partition<Term>>> possibleUnification = new HashMap<Atom, LinkedList<Partition<Term>>>();
		// compute possible unification between atoms of Q and head(R)
		CloseableIteratorWithoutException<Atom> it = q.iterator();
		while (it.hasNext()) {
			Atom a = it.next();
			CloseableIteratorWithoutException<Atom> it2 = ruleCopy.getHead().iterator();
			while (it2.hasNext()) {
				Atom b = it2.next();
				if (compilation.isMappable(a.getPredicate(), b.getPredicate())) {
					Collection<Partition<Term>> unification = compilation.getUnification(a, b);
					for (Partition<Term> partition : unification) {
						if (TermPartitionUtils.isAdmissible(partition, ruleCopy)) {
							if (possibleUnification.get(a) == null)
								possibleUnification.put(a, new LinkedList<Partition<Term>>());
							possibleUnification.get(a).add(partition);
						}
					}
				}
			}
		}

		LinkedList<Atom> atoms = getUnifiableAtoms(q, r, compilation);
		for (Atom a : atoms) {
			LinkedList<Partition<Term>> partitionList = possibleUnification.get(a);
			if (partitionList != null) {
				Iterator<Partition<Term>> i = partitionList.iterator();
				while (i.hasNext()) {
					Partition<Term> unif = i.next();
					InMemoryAtomSet p = new LinkedListAtomSet();
					p.add(a);
					u.addAll(extend(p, unif, possibleUnification, q, ruleCopy));
					i.remove();
				}
			}
		}

		return u;
	}
	
	/**
	 * Returns the list of all single-piece unifier between the given query and
	 * the given atomic-head rule cannot work with IDCompilation ( have to
	 * conserve the fact that an atom of the query can only been associated by a
	 * single unification with an atom of the head
	 * 
	 * <br/>
	 * AHR: Atomic Header Rule
	 * 
	 * @param q
	 *            the query that we want unify
	 * @param r
	 *            the atomic-head rule that we want unify
	 * @return the ArrayList of all single-piece unifier between the query of
	 *         the receiving object and R an atomic-head rule
	 */
	public static LinkedList<QueryUnifier> getSinglePieceUnifiersAHR(
			ConjunctiveQuery q, AtomicHeadRule r, RulesCompilation compilation) {
		LinkedList<Atom> unifiableAtoms = getUnifiableAtoms(q, r, compilation);
		LinkedList<QueryUnifier> unifiers = new LinkedList<QueryUnifier>();

		Iterator<Atom> i = unifiableAtoms.iterator();
		while (i.hasNext()) {
			InMemoryAtomSet p = new LinkedListAtomSet();
			Rule tmpRule = getSafeCopy(r);
			AtomicHeadRule copy = new AtomicHeadRule(tmpRule.getBody(), tmpRule
					.getHead().iterator().next());
			Atom toUnif = i.next();
			p.add(toUnif);
			Partition<Term> partition = new Partition<Term>(toUnif.getTerms(), copy.getHead().getAtom().getTerms());
			// compute separating variable
			LinkedList<Term> sep = AtomSetUtils.sep(p, q.getAtomSet());
			// compute sticky variable
			LinkedList<Term> sticky = TermPartitionUtils.getStickyVariable(partition, sep, copy);
			InMemoryAtomSet pBar = AtomSetUtils.minus(q.getAtomSet(), p);
			while (partition != null && !sticky.isEmpty()) {

				CloseableIteratorWithoutException<Atom> ia = pBar.iterator();
				while (partition != null && ia.hasNext()) {

					Atom a = ia.next();
					Iterator<Term> ix = sticky.iterator();
					while (partition != null && ix.hasNext()) {

						Term x = ix.next();
						// all the atoms of Q/P which contain x must be add to P
						if (a.getTerms().contains(x)) {
							// TODO use isMappable instead of isUnifiable
							// because isUnifiable just made a call to
							// isMappable
							if (compilation.isMappable(a.getPredicate(), copy.getHead().getAtom().getPredicate())) {
								p.add(a);
								Partition<Term> part = partition.join(new Partition<Term>(a.getTerms(), copy.getHead()
								                                                                            .getAtom()
								                                                                            .getTerms()));
								if (TermPartitionUtils.isAdmissible(part, copy)) {
									partition = part;
								} else
									partition = null;
							} else
								partition = null;
						}
					}
				}
				if (partition != null) {
					sep = AtomSetUtils.sep(p, q.getAtomSet());
					pBar = AtomSetUtils.minus(q.getAtomSet(), p);
					sticky = TermPartitionUtils.getStickyVariable(partition, sep, copy);
				}
			}
			i.remove();
			if (partition != null) {
				QueryUnifier u = new QueryUnifier(p, partition, copy, q);
				unifiers.add(u);
			}
		}

		return unifiers;
	}
	
	
	/**
	 * Returns the list of the atoms of the query that can be unify with the
	 * head of R
	 * 
	 * @param query
	 *            the query to unify
	 * 
	 * @param r
	 *            the rule whose has the head to unify
	 * @return the list of the atoms of the query that have the same predicate
	 *         as the head atom of R
	 */
	public static LinkedList<Atom> getUnifiableAtoms(ConjunctiveQuery query, Rule r, RulesCompilation compilation) {
		LinkedList<Atom> answer = new LinkedList<Atom>();
		CloseableIteratorWithoutException<Atom> it = query.iterator();
		while (it.hasNext()) {
			Atom a = it.next();
			CloseableIteratorWithoutException<Atom> it2 = r.getHead().iterator();
			while (it2.hasNext()) {
				Atom b = it2.next();
				// TODO use isMappable instead of isUnifiable because
				// isUnifiable just made a call to isMappable
				if (compilation.isMappable(a.getPredicate(), b.getPredicate()))
					answer.add(a);
			}
		}
		return answer;
	}

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

	private static Collection<? extends QueryUnifier> extend(InMemoryAtomSet p, Partition<Term> unif,
	    HashMap<Atom, LinkedList<Partition<Term>>> possibleUnification, ConjunctiveQuery q, Rule r) {
		LinkedList<QueryUnifier> u = new LinkedList<QueryUnifier>();

		// compute separating variable
		LinkedList<Term> sep = AtomSetUtils.sep(p, q.getAtomSet());
		// compute sticky variable
		LinkedList<Term> sticky = TermPartitionUtils.getStickyVariable(unif, sep, r);
		if (sticky.isEmpty()) {
			u.add(new QueryUnifier(p, unif, r, q));
		} else {
			// compute Pext the atoms of Pbar linked to P by the sticky
			// variables
			InMemoryAtomSet pBar = AtomSetUtils.minus(q.getAtomSet(), p);
			InMemoryAtomSet pExt = new LinkedListAtomSet();
			InMemoryAtomSet toRemove = new LinkedListAtomSet();
			for (Term t : sticky) {
				pBar.removeAll(toRemove);
				toRemove.clear();
				CloseableIteratorWithoutException<Atom> ib = pBar.iterator();
				while (ib.hasNext()) {
					Atom b = ib.next();
					if (b.getTerms().contains(t)) {
						pExt.add(b);
						toRemove.add(b);
					}
				}
			}
			Partition<Term> part;
			for (Partition<Term> uExt : preUnifier(pExt, r, possibleUnification)) {
				part = unif.join(uExt);
				if (part != null && TermPartitionUtils.isAdmissible(part, r)) {
					u.addAll(extend(AtomSetUtils.union(p, pExt), part, possibleUnification, q, r));
				}
			}

		}

		return u;
	}

	private static LinkedList<Partition<Term>> preUnifier(InMemoryAtomSet p, Rule r,
	    HashMap<Atom, LinkedList<Partition<Term>>> possibleUnification) {
		LinkedList<Partition<Term>> res = new LinkedList<Partition<Term>>();
		CloseableIteratorWithoutException<Atom> it = p.iterator();
		while (it.hasNext()) {
			Atom a = it.next();
			if (possibleUnification.get(a) != null)
				for (Partition<Term> ua : possibleUnification.get(a)) {
					InMemoryAtomSet fa = new LinkedListAtomSet();
					fa.add(a);
					InMemoryAtomSet aBar = null;
					aBar = AtomSetUtils.minus(p, fa);

					if (!aBar.iterator().hasNext())
						res.add(ua);
					else {
						Partition<Term> part;
						for (Partition<Term> u : preUnifier(aBar, r, possibleUnification)) {
							part = ua.join(u);
							if (part != null && TermPartitionUtils.isAdmissible(part, r)) {
								res.add(part);
							}
						}
					}
				}
			else
				return res;
		}
		return res;
	}
	
	private static DefaultVariableGenerator varGen = new DefaultVariableGenerator("X" + UnifierUtils.class.hashCode());

	
	
	public static Rule getSafeCopy(Rule rule) {
		Substitution substitution = new FreshVarSubstitution(varGen);

		InMemoryAtomSet body = rule.getBody();
		InMemoryAtomSet head = rule.getHead();

		InMemoryAtomSet safeBody = new LinkedListAtomSet();
		InMemoryAtomSet safeHead = new LinkedListAtomSet();

		substitution.apply(body, safeBody);
		substitution.apply(head, safeHead);

		return DefaultRuleFactory.instance().create(safeBody, safeHead);
	}


}
