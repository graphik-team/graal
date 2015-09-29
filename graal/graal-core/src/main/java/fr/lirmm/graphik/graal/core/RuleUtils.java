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
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Term.Type;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.factory.AtomSetFactory;
import fr.lirmm.graphik.graal.core.factory.DefaultAtomFactory;
import fr.lirmm.graphik.graal.core.factory.RuleFactory;
import fr.lirmm.graphik.util.EquivalentRelation;
import fr.lirmm.graphik.util.TreeMapEquivalentRelation;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public final class RuleUtils {

	private RuleUtils() {
	}

	/**
	 * Test if the body of the rule is atomic.
	 * 
	 * @param rule
	 * @return true if and only if the body of the specified rule contains only
	 *         one atom.
	 */
	public static boolean hasAtomicBody(Rule rule) {
		Iterator<Atom> it = rule.getBody().iterator();
		boolean res = it.hasNext();
		it.next();
		return res && !it.hasNext();
	}

	/**
	 * Test if the head of the rule is atomic.
	 * 
	 * @param rule
	 * @return true if and only if the head of the specified rule contains only
	 *         one atom.
	 */
	public static boolean hasAtomicHead(Rule rule) {
		Iterator<Atom> it = rule.getHead().iterator();
		boolean res = it.hasNext();
		it.next();
		return res && !it.hasNext();
	}

	public static boolean isThereOneAtomThatContainsAllVars(Iterable<Atom> atomset, Collection<Term> terms) {
		for (Atom atom : atomset) {
			if (atom.getTerms(Type.VARIABLE).containsAll(terms)) {
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
	 * @return
	 */
	public static Collection<InMemoryAtomSet> getPieces(Rule rule) {
		Set<Term> existentials = rule.getExistentials();
		Collection<InMemoryAtomSet> pieces = new LinkedList<InMemoryAtomSet>();

		// compute equivalent classes
		EquivalentRelation<Term> classes = new TreeMapEquivalentRelation<Term>();
		for (Atom a : rule.getHead()) {
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
				tmpPieces.put(classes.getIdClass(e), AtomSetFactory
						.instance().createAtomSet());
			}
		}

		// Affect atoms to one pieces
		boolean isAffected;
		InMemoryAtomSet atomset;
		Term e;
		for (Atom a : rule.getHead()) {
			isAffected = false;
			Iterator<Term> it = existentials.iterator();
			while (it.hasNext() && !isAffected) {
				e = it.next();
				if (a.getTerms().contains(e)) {
					tmpPieces.get(classes.getIdClass(e)).add(a);
					isAffected = true;
				}
			}
			if (!isAffected) { // does not contain existential variable
				atomset = AtomSetFactory.instance().createAtomSet();
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
		return new AtomicHeadIterator(rules);
	}


	/**
	 * Generate a set of mono-piece rules equivalent of the specified rule.
	 * 
	 * @param rule
	 * @return
	 */
	public static Collection<Rule> computeSinglePiece(Rule rule) {
		String label = rule.getLabel();
		Collection<Rule> monoPiece = new LinkedList<Rule>();

		if (label.isEmpty()) {
			for (AtomSet piece : getPieces(rule)) {
				monoPiece.add(RuleFactory.instance().create(rule.getBody(), piece));
			}
		} else {
			int i = -1;
			for (InMemoryAtomSet piece : getPieces(rule)) {
				monoPiece.add(RuleFactory.instance().create(label + "-p" + ++i, rule
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
	 * @return
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
				atomicHead.add(RuleFactory.instance().create(rule.getBody(), new LinkedListAtomSet(aux)));
				for (Atom atom : rule.getHead()) {
					atomicHead.add(RuleFactory.instance().create(aux, atom));
				}
			} else {
				int i = -1;
				atomicHead.add(RuleFactory.instance().create(label + "-a" + ++i, rule.getBody(),
				        new LinkedListAtomSet(aux)));
				for (Atom atom : rule.getHead()) {
					atomicHead.add(RuleFactory.instance().create(label + "-a" + ++i, aux, atom));
				}
			}
		}

		return atomicHead;
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
						.addAll(RuleUtils.computeSinglePiece(currentRule));
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
				currentAtomicHead.addAll(RuleUtils.computeAtomicHead(currentRule));
			}
			return currentAtomicHead.poll();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

}
