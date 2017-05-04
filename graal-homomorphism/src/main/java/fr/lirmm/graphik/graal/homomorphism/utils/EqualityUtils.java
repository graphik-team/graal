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
package fr.lirmm.graphik.graal.homomorphism.utils;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.graal.core.Substitutions;
import fr.lirmm.graphik.graal.core.factory.DefaultAtomSetFactory;
import fr.lirmm.graphik.graal.core.factory.DefaultConjunctiveQueryFactory;
import fr.lirmm.graphik.graal.core.factory.DefaultSubstitutionFactory;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;

/**
 * This utility class provides methods to handle equality in conjunctive
 * queries.
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 * 
 */
public final class EqualityUtils {

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	private EqualityUtils() {
	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * This method produces a conjunctive query based on the specified one where
	 * equality atoms are removed and affected variables are replaced or merged.
	 * It returns the new query and a substitution which allow to rebuild
	 * answers to the original query based on answers to the returned one by
	 * composition of each answer with the returned substitution.
	 * 
	 * @param q
	 *            a conjunctive query
	 * @return a pair composed of the computed conjunctive query and the
	 *         substitution which allow to rebuild answers.
	 *
	 */
	public static Pair<ConjunctiveQuery, Substitution> processEquality(ConjunctiveQuery q) {
		LinkedList<Atom> toRemove = new LinkedList<Atom>();
		Substitution s = DefaultSubstitutionFactory.instance().createSubstitution();
		CloseableIteratorWithoutException<Atom> it = q.getAtomSet().iterator();
		while (it.hasNext()) {
			Atom a = it.next();
			if (Predicate.EQUALITY.equals(a.getPredicate())) {
				if (a.getTerm(0).isVariable()) {
					if (!updateSubstitution(s, (Variable) a.getTerm(0), a.getTerm(1))) {
						return generateBottomResult();
					}
					toRemove.add(a);
				} else if (a.getTerm(1).isVariable()) {
					if (!updateSubstitution(s, (Variable) a.getTerm(1), a.getTerm(0))) {
						return generateBottomResult();
					}
					toRemove.add(a);
				} else {
					return generateBottomResult();
				}
			}
		}
		return new ImmutablePair<ConjunctiveQuery, Substitution>(generateQuery(q, s, toRemove), s);
	}

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

	private static boolean updateSubstitution(Substitution s, Variable var, Term image) {
		return s.aggregate(var, image);
	}

	private static ConjunctiveQuery generateQuery(ConjunctiveQuery q, Substitution s, LinkedList<Atom> toRemove) {
		if (toRemove.isEmpty()) {
			return q;
		}

		List<Term> newAns = new LinkedList<Term>(q.getAnswerVariables());
		newAns.removeAll(s.getTerms());

		InMemoryAtomSet newAtomSet = DefaultAtomSetFactory.instance().create();
		CloseableIteratorWithoutException<Atom> it = q.getAtomSet().iterator();
		while (it.hasNext()) {
			Atom a = it.next();
			if (!toRemove.contains(a)) {
				newAtomSet.add(s.createImageOf(a));
			}
		}

		return DefaultConjunctiveQueryFactory.instance().create(newAtomSet, newAns);
	}

	private static ImmutablePair<ConjunctiveQuery, Substitution> generateBottomResult() {
		return new ImmutablePair<ConjunctiveQuery, Substitution>(
				DefaultConjunctiveQueryFactory.instance().BOOLEAN_BOTTOM_QUERY, Substitutions.emptySubstitution());
	}

}
