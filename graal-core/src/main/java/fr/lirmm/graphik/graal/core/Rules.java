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
/**
 * 
 */
package fr.lirmm.graphik.graal.core;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import fr.lirmm.graphik.graal.GraalConstant;
import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.graal.core.atomset.AtomSetUtils;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.atomset.graph.DefaultInMemoryGraphStore;
import fr.lirmm.graphik.graal.core.factory.DefaultAtomFactory;
import fr.lirmm.graphik.graal.core.factory.DefaultAtomSetFactory;
import fr.lirmm.graphik.graal.core.factory.DefaultRuleFactory;
import fr.lirmm.graphik.util.EquivalentRelation;
import fr.lirmm.graphik.util.TreeMapEquivalentRelation;
import fr.lirmm.graphik.util.stream.CloseableIterableWithoutException;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public final class Rules {

	private Rules() {
	}

	/**
	 * Test if the body of the rule is atomic.
	 * 
	 * @param rule
	 * @return true if and only if the body of the specified rule contains only
	 *         one atom.
	 */
	public static boolean hasAtomicBody(Rule rule) {
		return AtomSetUtils.isSingleton(rule.getBody());
	}

	/**
	 * Test if the head of the rule is atomic.
	 * 
	 * @param rule
	 * @return true if and only if the head of the specified rule contains only
	 *         one atom.
	 */
	public static boolean hasAtomicHead(Rule rule) {
		return AtomSetUtils.isSingleton(rule.getHead());
	}

	public static boolean isThereOneAtomThatContainsAllVars(CloseableIterableWithoutException<Atom> atomset,
	    Collection<Variable> terms) {
		CloseableIteratorWithoutException<Atom> it = atomset.iterator();
		while (it.hasNext()) {
			Atom atom = it.next();
			if (atom.getVariables().containsAll(terms)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Compute and return the set of pieces of the head according to the
	 * frontier. On Rules with Existential Variables: Walking the Decidability
	 * Line Jean-François Baget, Michel Leclère, Marie-Laure Mugnier, Eric
	 * Salvat
	 *
	 * @return a Collection ofInMemoryAtomSet representing the set of pieces of the head of the specified rule.
	 */
	public static Collection<InMemoryAtomSet> getPieces(Rule rule) {
		Set<Variable> existentials = rule.getExistentials();
		Collection<InMemoryAtomSet> pieces = new LinkedList<InMemoryAtomSet>();

		// compute equivalent classes
		EquivalentRelation<Term> classes = new TreeMapEquivalentRelation<Term>();
		CloseableIteratorWithoutException<Atom> it = rule.getHead().iterator();
		while (it.hasNext()) {
			Atom a = it.next();
			Term representant = null;
			for (Term t : a) {
				if (existentials.contains(t)) {
					if (representant == null)
						representant = t;
					else
						classes.mergeClasses(representant, t);
				}
			}
		}

		// init pieces for equivalent classes
		Map<Integer, InMemoryAtomSet> tmpPieces = new TreeMap<Integer, InMemoryAtomSet>();
		for (Term e : existentials) {
			if (tmpPieces.get(classes.getIdClass(e)) == null) {
				tmpPieces.put(classes.getIdClass(e), DefaultAtomSetFactory
						.instance().create());
			}
		}

		// Affect atoms to one pieces
		boolean isAffected;
		InMemoryAtomSet atomset;
		Term e;
		it = rule.getHead().iterator();
		while (it.hasNext()) {
			Atom a = it.next();
			isAffected = false;
			Iterator<Variable> it2 = existentials.iterator();
			while (it2.hasNext() && !isAffected) {
				e = it2.next();
				if (a.getTerms().contains(e)) {
					tmpPieces.get(classes.getIdClass(e)).add(a);
					isAffected = true;
				}
			}
			if (!isAffected) { // does not contain existential variable
				atomset = DefaultAtomSetFactory.instance().create();
				atomset.add(a);
				pieces.add(atomset);
			}
		}

		pieces.addAll(tmpPieces.values());

		return pieces;
	}

	// /////////////////////////////////////////////////////////////////////////
	// TRANSFORM RULES
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Generate an iterator of mono-piece rules from an iterator of rules.
	 * 
	 * @param rules
	 *            a set of rules
	 * @return The equivalent set of mono-piece rules.
	 */
	public static SinglePieceRulesIterator computeSinglePiece(Iterator<Rule> rules) {
		return new SinglePieceRulesIterator(rules);
	}

	/**
	 * Generate an iterator of atomic head rules from an iterator of rules.
	 * 
	 * @param rules
	 *            a set of rules
	 * @return The equivalent set of atomic head rules.
	 */
	public static Iterator<Rule> computeAtomicHead(Iterator<Rule> rules) {
		return new AtomicHeadIterator(new SinglePieceRulesIterator(rules));
	}


	/**
	 * Generate a set of mono-piece rules equivalent of the specified rule.
	 * 
	 * @param rule
	 * @return a Collection of Rule which is a decomposition of the specified rule to single piece rules.
	 */
	public static Collection<Rule> computeSinglePiece(Rule rule) {
		String label = rule.getLabel();
		Collection<Rule> monoPiece = new LinkedList<Rule>();

		if (label.isEmpty()) {
			for (InMemoryAtomSet piece : getPieces(rule)) {
				monoPiece.add(DefaultRuleFactory.instance().create(rule.getBody(), piece));
			}
		} else {
			int i = -1;
			for (InMemoryAtomSet piece : getPieces(rule)) {
				monoPiece.add(DefaultRuleFactory.instance().create(label + "-p" + ++i, rule
						.getBody(), piece));
			}
		}

		return monoPiece;
	}

	private static int auxIndex = -1;

	/**
	 * Generate a set of atomic head rules equivalent of the specified rule.
	 * 
	 * @param rule
	 * @return a Collection of Rule which is a decomposition of the specified rule to atomic head rules.
	 */
	public static Collection<Rule> computeAtomicHead(Rule rule) {
		String label = rule.getLabel();
		Collection<Rule> atomicHead = new LinkedList<Rule>();

		if (rule.getHead().isEmpty() || hasAtomicHead(rule)) {
			return Collections.<Rule> singleton(rule);
		} else {
			Predicate predicate = new Predicate("aux_" + ++auxIndex, rule.getTerms().size());
			Atom aux = DefaultAtomFactory.instance().create(predicate,
			        rule.getTerms().toArray(new Term[rule.getTerms().size()]));

			if (label.isEmpty()) {
				atomicHead.add(DefaultRuleFactory.instance().create(rule.getBody(), new LinkedListAtomSet(aux)));
				CloseableIteratorWithoutException<Atom> it = rule.getHead().iterator();
				while (it.hasNext()) {
					Atom atom = it.next();
					atomicHead.add(DefaultRuleFactory.instance().create(aux, atom));
				}
			} else {
				int i = -1;
				atomicHead.add(DefaultRuleFactory.instance().create(label + "-a" + ++i, rule.getBody(),
				        new LinkedListAtomSet(aux)));
				CloseableIteratorWithoutException<Atom> it = rule.getHead().iterator();
				while (it.hasNext()) {
					Atom atom = it.next();
					atomicHead.add(DefaultRuleFactory.instance().create(label + "-a" + ++i, aux, atom));
				}
			}
		}

		return atomicHead;
	}
	
	// /////////////////////////////////////////////////////////////////////////
    // ANALYSE KIND OF RULE
    // /////////////////////////////////////////////////////////////////////////

	public static boolean isConcept(Atom a) {
		return a.getPredicate().getArity() == 1;
	}

	public static boolean isRole(Atom a) {
		return a.getPredicate().getArity() == 2;
	}

	public static boolean isInclusion(Rule r) {
		return hasAtomicBody(r) && hasAtomicHead(r);
	}

	public static boolean isConceptInclusion(Rule r) {
		if (!isInclusion(r))
			return false;
		Atom C1 = r.getBody().iterator().next();
		Atom C2 = r.getHead().iterator().next();
		if (!isConcept(C1))
			return false;
		if (!isConcept(C2))
			return false;
		return C1.getTerm(0).equals(C2.getTerm(0));
	}

	public static boolean isRoleInclusion(Rule r) {
		if (!isInclusion(r))
			return false;
		Atom P1 = r.getBody().iterator().next();
		Atom P2 = r.getHead().iterator().next();
		if (!isRole(P1))
			return false;
		if (!isRole(P2))
			return false;
		Term t1_1 = P1.getTerm(0);
		Term t1_2 = P1.getTerm(1);
		Term t2_1 = P2.getTerm(0);
		Term t2_2 = P2.getTerm(1);
		return (t1_1.equals(t2_1) && t1_2.equals(t2_2)) || (t1_1.equals(t2_2) && t1_2.equals(t2_1));
	}

	public static boolean isInverseRole(Rule r) {
		if (!isRoleInclusion(r))
			return false;
		Atom P1 = r.getBody().iterator().next();
		Atom P2 = r.getHead().iterator().next();
		Term t1_1 = P1.getTerm(0);
		Term t1_2 = P1.getTerm(1);
		Term t2_1 = P2.getTerm(0);
		Term t2_2 = P2.getTerm(1);
		return t1_1.equals(t2_2) && t1_2.equals(t2_1);
	}

	public static boolean isSignature(Rule r) {
		if (!isInclusion(r))
			return false;
		Atom P1 = r.getBody().iterator().next();
		Atom C1 = r.getHead().iterator().next();
		return isRole(P1) && isConcept(C1);
	}

	public static boolean isDomain(Rule r) {
		if (!isSignature(r))
			return false;
		Atom P1 = r.getBody().iterator().next();
		Atom C1 = r.getHead().iterator().next();
		return P1.getTerm(0).equals(C1.getTerm(0));
	}

	public static boolean isRange(Rule r) {
		if (!isSignature(r))
			return false;
		Atom P1 = r.getBody().iterator().next();
		Atom C1 = r.getHead().iterator().next();
		return P1.getTerm(1).equals(C1.getTerm(0));
	}

	public static boolean isMandatoryRole(Rule r) {
		if (!isInclusion(r))
			return false;
		Atom C1 = r.getBody().iterator().next();
		Atom P1 = r.getHead().iterator().next();
		if (!isConcept(C1))
			return false;
		if (!isRole(P1))
			return false;
		return (C1.getTerm(0).equals(P1.getTerm(0)) && !C1.getTerm(0).equals(P1.getTerm(1)));
	}

	public static boolean isInvMandatoryRole(Rule r) {
		if (!isInclusion(r))
			return false;
		Atom C1 = r.getBody().iterator().next();
		Atom P1 = r.getHead().iterator().next();
		if (!isConcept(C1))
			return false;
		if (!isRole(P1))
			return false;
		return (C1.getTerm(0).equals(P1.getTerm(1)) && !C1.getTerm(0).equals(P1.getTerm(0)));
	}

	public static boolean isExistRC(Rule r) {
		if (!AtomSetUtils.isSingleton(r.getBody()))
			return false;
		if (!AtomSetUtils.hasSize2(r.getHead()))
			return false;
		CloseableIteratorWithoutException<Atom> h = r.getHead().iterator();
		Atom P1 = r.getBody().iterator().next();
		Atom P2 = h.next();
		Atom P3 = h.next();
		Atom tmp;
		if (!isConcept(P1))
			return false;
		if (isConcept(P2) && isRole(P3)) {
			tmp = P2;
			P2 = P3;
			P3 = tmp;
		}
		if (!isRole(P2))
			return false;
		if (!isConcept(P3))
			return false;
		return (P1.getTerm(0).equals(P2.getTerm(0)) && P2.getTerm(1).equals(P3.getTerm(0)) && !P1.getTerm(0).equals(
		        P3.getTerm(0)));
	}

	public static boolean isInvExistRC(Rule r) {
		if (!AtomSetUtils.isSingleton(r.getBody()))
			return false;
		if (!AtomSetUtils.hasSize2(r.getHead()))
			return false;
		CloseableIteratorWithoutException<Atom> h = r.getHead().iterator();
		Atom P1 = r.getBody().iterator().next();
		Atom P2 = h.next();
		Atom P3 = h.next();
		Atom tmp;
		if (!isConcept(P1))
			return false;
		if (isConcept(P2) && isRole(P3)) {
			tmp = P2;
			P2 = P3;
			P3 = tmp;
		}
		if (!isRole(P2))
			return false;
		if (!isConcept(P3))
			return false;
		return (P1.getTerm(0).equals(P2.getTerm(1)) && P2.getTerm(0).equals(P3.getTerm(0)) && !P1.getTerm(0).equals(
		        P3.getTerm(0)));
	}

	public static boolean isRoleComposition(Rule r) {
		if (!AtomSetUtils.hasSize2(r.getBody()))
			return false;
		if (!AtomSetUtils.isSingleton(r.getHead()))
			return false;
		CloseableIteratorWithoutException<Atom> b = r.getBody().iterator();
		Atom P1 = b.next();
		Atom P2 = b.next();
		Atom P3 = r.getHead().iterator().next();
		if (!isRole(P1))
			return false;
		if (!isRole(P2))
			return false;
		if (!isRole(P3))
			return false;
		return (P1.getTerm(0).equals(P3.getTerm(0)) && P1.getTerm(1).equals(P2.getTerm(0)) && P2.getTerm(1).equals(
		        P3.getTerm(1)))
		       || (P2.getTerm(0).equals(P3.getTerm(0)) && P2.getTerm(1).equals(P1.getTerm(0)) && P1.getTerm(1).equals(
		               P3.getTerm(1)));
	}

	public static boolean isTransitivity(Rule r) {
		if (!isRoleComposition(r))
			return false;
		CloseableIteratorWithoutException<Atom> b = r.getBody().iterator();
		Atom P1 = b.next();
		Atom P2 = b.next();
		Atom P3 = r.getHead().iterator().next();
		return P1.getPredicate().equals(P2.getPredicate()) && P1.getPredicate().equals(P3.getPredicate());
	}

	public static boolean isFunctional(Rule r) {
		if (!AtomSetUtils.isSingleton(r.getHead()))
			return false;
		return r.getHead().iterator().next().getPredicate().equals(Predicate.EQUALITY);
	}

	public static boolean isNegativeConstraint(Rule r) {
		if (!AtomSetUtils.isSingleton(r.getHead()))
			return false;
		return r.getHead().iterator().next().equals(DefaultAtomFactory.instance().getBottom());
	}

	public static boolean isDisjointConcept(Rule r) {
		if (!AtomSetUtils.hasSize2(r.getBody()))
			return false;
		if (!isNegativeConstraint(r))
			return false;
		CloseableIteratorWithoutException<Atom> b = r.getBody().iterator();
		Atom C1 = b.next();
		Atom C2 = b.next();
		if (!isConcept(C1))
			return false;
		if (!isConcept(C2))
			return false;
		return (C1.getTerm(0).equals(C2.getTerm(0)));
	}

	public static boolean isDisjointRole(Rule r) {
		if (!AtomSetUtils.hasSize2(r.getBody()))
			return false;
		if (!isNegativeConstraint(r))
			return false;
		CloseableIteratorWithoutException<Atom> b = r.getBody().iterator();
		Atom P1 = b.next();
		Atom P2 = b.next();
		if (!isRole(P1))
			return false;
		if (!isRole(P2))
			return false;
		return (P1.getTerm(0).equals(P2.getTerm(0)) && P1.getTerm(1).equals(P2.getTerm(1)))
		       || (P1.getTerm(1).equals(P2.getTerm(0)) && P1.getTerm(0).equals(P2.getTerm(1)));
	}

	public static boolean isDisjointInverseRole(Rule r) {
		if (!isDisjointRole(r))
			return false;
		CloseableIteratorWithoutException<Atom> b = r.getBody().iterator();
		Atom P1 = b.next();
		Atom P2 = b.next();
		return P1.getTerm(1).equals(P2.getTerm(0)) && P1.getTerm(0).equals(P2.getTerm(1));
	}

	// /////////////////////////////////////////////////////////////////////////
	// Private classes
	// /////////////////////////////////////////////////////////////////////////

	private static class SinglePieceRulesIterator implements Iterator<Rule> {

		Iterator<Rule> it;
		Queue<Rule> currentMonoPiece = new LinkedList<Rule>();
		Rule currentRule;

		SinglePieceRulesIterator(Iterator<Rule> iterator) {
			this.it = iterator;
		}

		@Override
		public boolean hasNext() {
			return !currentMonoPiece.isEmpty() || it.hasNext();
		}

		@Override
		public Rule next() {
			if (currentMonoPiece.isEmpty()) {
				currentRule = it.next();
				currentMonoPiece
						.addAll(Rules.computeSinglePiece(currentRule));
			}
			return currentMonoPiece.poll();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	private static class AtomicHeadIterator implements Iterator<Rule> {

		Iterator<Rule> it;
		Queue<Rule> currentAtomicHead = new LinkedList<Rule>();
		Rule currentRule;

		AtomicHeadIterator(Iterator<Rule> iterator) {
			this.it = iterator;
		}

		@Override
		public boolean hasNext() {
			return !currentAtomicHead.isEmpty() || it.hasNext();
		}

		@Override
		public Rule next() {
			if (currentAtomicHead.isEmpty()) {
				currentRule = it.next();
				currentAtomicHead.addAll(Rules.computeAtomicHead(currentRule));
			}
			return currentAtomicHead.poll();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}


	public static InMemoryAtomSet criticalInstance(final Iterable<Rule> rules) {
		InMemoryAtomSet A = new DefaultInMemoryGraphStore();
		criticalInstance(rules,A);
		return A;
	}

	/**
	 * The skolem  chase on the critical instance of R halts iff the skolem chase of R halts
	 * universally.
	 * The critical instance for a set of rules R contains all facts that can be constructed
	 * using all predicates occuring in R, all constants occurring in the body of a rule in R, and one
	 * special fresh constant.
	 */
	public static void criticalInstance(final Iterable<Rule> rules, InMemoryAtomSet A) {
		Set<Term> terms = new TreeSet<Term>();
		terms.add(GraalConstant.freshConstant());
		Set<Predicate> predicates = new TreeSet<Predicate>();
		for (Rule r : rules) {
			CloseableIteratorWithoutException<Atom> it = r.getBody().iterator();
			while (it.hasNext()) {
				Atom b = it.next();
				predicates.add(b.getPredicate());
				for (Term t : b.getTerms())
					if (t.isConstant())
						terms.add(t);
			}
		}

		// TODO: In the definition of CI, we need to add all
		// predicates in rule head. But why? This doesn't make any
		// sense...

		for (Predicate p : predicates) {
			generateCriticalInstance(A,terms,p,0,new DefaultAtom(p));
		}
	}

	private static void generateCriticalInstance(InMemoryAtomSet A, Set<Term> terms, Predicate p, int position,
	    DefaultAtom a) {
		if (position >= p.getArity()) {
			A.add(a);
			return;
		}

		for (Term t : terms) {
			DefaultAtom a2 = new DefaultAtom(a);
			a2.setTerm(position,t);
			generateCriticalInstance(A,terms,p,position+1,a2);
		}
	}

};

