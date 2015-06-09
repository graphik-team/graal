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
package fr.lirmm.graphik.graal.backward_chaining.pure.utils;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import fr.lirmm.graphik.graal.backward_chaining.pure.rules.RulesCompilation;
import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.DefaultFreeVarGen;
import fr.lirmm.graphik.graal.core.DefaultRule;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.Substitution;
import fr.lirmm.graphik.graal.core.TreeMapSubstitution;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.AtomSets;
import fr.lirmm.graphik.graal.core.atomset.InMemoryAtomSet;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.term.Term;
import fr.lirmm.graphik.graal.homomorphism.HomomorphismException;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
public final class Misc {

	private static DefaultFreeVarGen varGen = new DefaultFreeVarGen("X"
			+ Integer.toString(Misc.class.hashCode()));

	private Misc() {
	}

	public static Rule getSafeCopy(Rule rule) {
		Substitution substitution = new TreeMapSubstitution();
		for (Term t : rule.getTerms(Term.Type.VARIABLE)) {
			substitution.put(t, varGen.getFreeVar());
		}

		InMemoryAtomSet body = rule.getBody();
		InMemoryAtomSet head = rule.getHead();

		InMemoryAtomSet safeBody = new LinkedListAtomSet();
		InMemoryAtomSet safeHead = new LinkedListAtomSet();

		substitution.apply(body, safeBody);
		substitution.apply(head, safeHead);

		return new DefaultRule(safeBody, safeHead);
	}

	/**
	 * Returns true if AtomSet h is more general than AtomSet f, and mark all
	 * the atom of h if h is a marked fact; else return false
	 * 
	 * @param comp
	 */
	public static boolean testInclu = true;

	public static boolean isMoreGeneralThan(InMemoryAtomSet h, InMemoryAtomSet f,
			RulesCompilation compilation) {

		boolean moreGen = false;
		if (testInclu && AtomSets.contains(f, h)) {
			moreGen = true;
		} else {
			try {
				moreGen = PureHomomorphismWithCompilation.getInstance().exist(
						h, f, compilation);
			} catch (HomomorphismException e) {
			}
		}

		return moreGen;
	}

	public static boolean isMoreGeneralThan(InMemoryAtomSet h, InMemoryAtomSet f) {

		return isMoreGeneralThan(h, f, null);
	}

	/**
	 * This methods test if the first rule logically imply the second. This
	 * methods works with linear rules (atomic head and body).
	 * 
	 * @param r1
	 * @param r2
	 * @return true, if r1 logically imply r2.
	 */
	public static boolean imply(Rule r1, Rule r2) {

		Atom b1 = r1.getBody().iterator().next();
		Atom b2 = r2.getBody().iterator().next();
		Atom h1 = r1.getHead().iterator().next();
		Atom h2 = r2.getHead().iterator().next();

		if (b1.getPredicate().equals(b2.getPredicate())
				&& h1.getPredicate().equals(h2.getPredicate())) {
			Map<Term, Term> s = new TreeMap<Term, Term>();
			Term term;
			for (int i = 0; i < b1.getPredicate().getArity(); i++) {
				term = s.get(b1.getTerm(i));
				if (term == null)
					s.put(b1.getTerm(i), b2.getTerm(i));
				else if (!term.equals(b2.getTerm(i)))
					return false;
			}
			for (int i = 0; i < h1.getPredicate().getArity(); i++) {
				term = s.get(h1.getTerm(i));
				if (term == null)
					s.put(h1.getTerm(i), h2.getTerm(i));
				else if (!term.equals(h2.getTerm(i)))
					return false;
			}
			return true;
		}

		return false;
	}
	
	/**
	 * Remove the fact that are not the most general (taking account of compiled
	 * rules) in the given facts
	 * 
	 * @param comp
	 * @throws Exception
	 */
	public static void computeCover(Iterable<ConjunctiveQuery> set,
			RulesCompilation comp) {
		Iterator<ConjunctiveQuery> beg = set.iterator();
		Iterator<ConjunctiveQuery> end;
		InMemoryAtomSet q;
		InMemoryAtomSet o;
		boolean finished;
		while (beg.hasNext()) {
			q = beg.next().getAtomSet();
			finished = false;
			end = set.iterator();
			while (!finished && end.hasNext()) {
				o = end.next().getAtomSet();
				if (o != q && isMoreGeneralThan(o, q, comp)) {
					finished = true;
					beg.remove();
				}
			}
		}
	}

	/**
	 * Remove the queries that are not the most general in the given set of
	 * queries
	 * 
	 * @param comp
	 * @throws Exception
	 */
	public static void computeCover(Iterable<ConjunctiveQuery> set) {
		computeCover(set, null);
	}

	/**
	 * Remove the queries that are not the most general in the given set of
	 * queries
	 * 
	 * @param comp
	 * @throws Exception
	 */
	public static void computeCoverAtomSet(Iterable<InMemoryAtomSet> set) {

		Iterator<InMemoryAtomSet> beg = set.iterator();
		Iterator<InMemoryAtomSet> end;
		InMemoryAtomSet q;
		InMemoryAtomSet o;
		boolean finished;
		while (beg.hasNext()) {
			q = beg.next();
			finished = false;
			end = set.iterator();
			while (!finished && end.hasNext()) {
				o = end.next();
				if (o != q && Misc.isMoreGeneralThan(o, q)) {
					finished = true;
					beg.remove();
				}
			}
		}
	}

}
