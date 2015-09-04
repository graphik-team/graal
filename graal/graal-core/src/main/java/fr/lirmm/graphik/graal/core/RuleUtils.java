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
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;

import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.InMemoryAtomSet;
import fr.lirmm.graphik.graal.core.factory.AtomSetFactory;
import fr.lirmm.graphik.graal.core.factory.RuleFactory;
import fr.lirmm.graphik.graal.core.term.Term;
import fr.lirmm.graphik.util.EquivalentRelation;
import fr.lirmm.graphik.util.TreeMapEquivalentRelation;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
// TODO we should move the static method from ruleanalyser.util.RuleUtils
// to this class... Refactor ;)
// 
// By the way, while refactoring, mono piece sounds worst than single piece
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

	/**
	 * Generate an iterator of mono-piece rules from an iterator of rules.
	 * 
	 * @param rules
	 *            a set of rules
	 * @return The equivalent set of mono-piece rules.
	 */
	public static Iterator<Rule> computeMonoPiece(Iterator<Rule> rules) {
		return new MonoPieceRulesIterator(rules);
	}

	/**
	 * Generate a set of mono-piece rules equivalent of the specified rule.
	 * 
	 * @param rule
	 * @return
	 */
	public static Collection<Rule> computeMonoPiece(Rule rule) {
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

	private static class MonoPieceRulesIterator implements Iterator<Rule> {

		Iterator<Rule> it;
		Queue<Rule> currentMonoPiece = new LinkedList<Rule>();
		Rule currentRule;

		MonoPieceRulesIterator(Iterator<Rule> iterator) {
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
						.addAll(RuleUtils.computeMonoPiece(currentRule));
			}
			return currentMonoPiece.poll();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}
}
