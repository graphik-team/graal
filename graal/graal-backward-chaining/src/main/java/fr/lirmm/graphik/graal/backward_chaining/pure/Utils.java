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
package fr.lirmm.graphik.graal.backward_chaining.pure;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.DefaultVariableGenerator;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.Substitution;
import fr.lirmm.graphik.graal.core.TreeMapSubstitution;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.AtomSetUtils;
import fr.lirmm.graphik.graal.core.atomset.InMemoryAtomSet;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.factory.ConjunctiveQueryFactory;
import fr.lirmm.graphik.graal.core.factory.RuleFactory;
import fr.lirmm.graphik.graal.core.term.Term;
import fr.lirmm.graphik.graal.homomorphism.HomomorphismException;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
final class Utils {

	private static DefaultVariableGenerator varGen = new DefaultVariableGenerator("X"
			+ Integer.toString(Utils.class.hashCode()));

	private Utils() {
	}

	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Rewrite the fact q according to the unifier u between the head of r and q
	 * 
	 * @param q
	 *            the fact to rewrite
	 * @param r
	 *            the rule which is unified with q
	 * @param u
	 *            the unifier between q and r
	 * @return the rewrite of q according to the unifier u between the head of r
	 *         and q
	 */
	public static ConjunctiveQuery rewrite(ConjunctiveQuery q, QueryUnifier u) {
		AtomSet ajout = u.getImageOf(u.getRule().getBody());
		AtomSet restant = u.getImageOf(AtomSetUtils.minus(q.getAtomSet(),
				u.getPiece()));
		ConjunctiveQuery rew = null;
		if (ajout != null && restant != null) { // FIXME
			AtomSet res = AtomSetUtils.union(ajout, restant);
			ArrayList<Term> ansVar = new ArrayList<Term>();
			ansVar.addAll(q.getAnswerVariables());
			rew = ConjunctiveQueryFactory.instance().create(res, ansVar);
		}
		return rew;
	}

	/**
	 * Rewrite the marked fact q according to the unifier u between the head of
	 * r and q
	 * 
	 * @param q
	 *            the fact to rewrite must be a marked fact
	 * @param r
	 *            the rule which is unified with q
	 * @param u
	 *            the unifier between q and r
	 * @return the rewrite of q according to the unifier u between the head of r
	 *         and q
	 */
	public static MarkedQuery rewriteWithMark(ConjunctiveQuery q, QueryUnifier u) {

		AtomSet ajout = u.getImageOf(u.getRule().getBody());
		AtomSet restant = u.getImageOf(AtomSetUtils.minus(q.getAtomSet(),
				u.getPiece()));
		MarkedQuery rew = null;
		if (ajout != null && restant != null) { // FIXME
			AtomSet res = AtomSetUtils.union(ajout, restant);
			ArrayList<Term> ansVar = new ArrayList<Term>();
			ansVar.addAll(q.getAnswerVariables());
			rew = new MarkedQuery(res, ansVar);

			ArrayList<Atom> markedAtoms = new ArrayList<Atom>();
			for (Atom a : ajout)
				markedAtoms.add(a);

			rew.setMarkedAtom(markedAtoms);
		}

		return rew;
	}

	public static Rule getSafeCopy(Rule rule) {
		Substitution substitution = new TreeMapSubstitution();
		for (Term t : rule.getTerms(Term.Type.VARIABLE)) {
			substitution.put(t, varGen.getFreshVar());
		}

		InMemoryAtomSet body = rule.getBody();
		InMemoryAtomSet head = rule.getHead();

		InMemoryAtomSet safeBody = new LinkedListAtomSet();
		InMemoryAtomSet safeHead = new LinkedListAtomSet();

		substitution.apply(body, safeBody);
		substitution.apply(head, safeHead);

		return RuleFactory.instance().create(safeBody, safeHead);
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
		if (testInclu && AtomSetUtils.contains(f, h)) {
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
				if (o != q && Utils.isMoreGeneralThan(o, q)) {
					finished = true;
					beg.remove();
				}
			}
		}
	}

}
