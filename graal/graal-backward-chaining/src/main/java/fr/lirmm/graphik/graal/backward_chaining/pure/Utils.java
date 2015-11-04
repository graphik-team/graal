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
package fr.lirmm.graphik.graal.backward_chaining.pure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.RulesCompilation;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.core.DefaultVariableGenerator;
import fr.lirmm.graphik.graal.core.TreeMapSubstitution;
import fr.lirmm.graphik.graal.core.atomset.AtomSetUtils;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.factory.ConjunctiveQueryFactory;
import fr.lirmm.graphik.graal.core.factory.RuleFactory;
import fr.lirmm.graphik.graal.homomorphism.PureHomomorphism;
import fr.lirmm.graphik.util.Profiler;

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

	public static Iterable<ConjunctiveQuery> unfold(Iterable<ConjunctiveQuery> pivotRewritingSet,
	    RulesCompilation compilation, Profiler profiler) {
		if (profiler != null) {
			profiler.start("Unfolding time");
		}

		Collection<ConjunctiveQuery> unfoldingRewritingSet = developpRewriting(pivotRewritingSet, compilation);

		/** clean the rewrites to return **/
		Utils.computeCover(unfoldingRewritingSet);

		if (profiler != null) {
			profiler.stop("Unfolding time");
			Iterator<?> it = unfoldingRewritingSet.iterator();
			int i = 0;
			while (it.hasNext()) {
				it.next();
				++i;
			}
			profiler.add("Unfolded rewritings", i);
		}

		return unfoldingRewritingSet;

	}

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
			InMemoryAtomSet res = AtomSetUtils.union(ajout, restant);
			List<Term> ansVar = new LinkedList<Term>();
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
			InMemoryAtomSet res = AtomSetUtils.union(ajout, restant);
			List<Term> ansVar = new LinkedList<Term>();
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
				moreGen = PureHomomorphism.instance().exist(
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

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE FUNCTIONS
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Add in the given rewriting set the rewrites that can be entailed from the
	 * predicate order ex: the rewrite A(x) can be entailed from the rewrite
	 * B(x) and the predicate order A > B
	 * 
	 * @return
	 */
	private static Collection<ConjunctiveQuery> developpRewriting(Iterable<ConjunctiveQuery> rewritingSet,
	    RulesCompilation compilation) {
		Collection<ConjunctiveQuery> unfoldingRewritingSet = new LinkedList<ConjunctiveQuery>();
		LinkedList<InMemoryAtomSet> newQueriesBefore = new LinkedList<InMemoryAtomSet>();
		LinkedList<InMemoryAtomSet> newQueriesAfter = new LinkedList<InMemoryAtomSet>();
		LinkedList<InMemoryAtomSet> newQueriesTmp;
		Iterable<Atom> atomsRewritings;
		InMemoryAtomSet copy;

		// ConjunctiveQuery q;

		for (ConjunctiveQuery originalQuery : rewritingSet) {
			// q = query.getIrredondant(compilation);
			// for all atom of the query we will build a list of all the
			// rewriting
			newQueriesBefore.clear();
			newQueriesBefore.add(new LinkedListAtomSet());

			// we will build all the possible fact from the rewriting of the
			// atoms
			for (Atom a : originalQuery) {
				atomsRewritings = compilation.getRewritingOf(a);
				for (AtomSet q : newQueriesBefore) {
					// for each possible atom at the next position clone the
					// query and add the atom
					for (Atom atom : atomsRewritings) {//
						copy = new LinkedListAtomSet(q);
						copy.add(atom);
						newQueriesAfter.add(copy);
					}
				}

				// switch list
				newQueriesTmp = newQueriesBefore;
				newQueriesBefore = newQueriesAfter;
				newQueriesAfter = newQueriesTmp;
				newQueriesAfter.clear();
			}
			for (InMemoryAtomSet a : newQueriesBefore) {
				unfoldingRewritingSet.add(ConjunctiveQueryFactory.instance().create(a,
				    originalQuery.getAnswerVariables()));
			}
		}

		return unfoldingRewritingSet;
	}

}
