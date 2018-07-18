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
package fr.lirmm.graphik.graal.core.compilation;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.graal.core.Substitutions;
import fr.lirmm.graphik.graal.core.factory.DefaultSubstitutionFactory;
import fr.lirmm.graphik.util.Partition;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class NoCompilation extends AbstractRulesCompilation {

	private static NoCompilation instance;

	protected NoCompilation() {
		super();
	}

	public static synchronized NoCompilation instance() {
		if (instance == null)
			instance = new NoCompilation();

		return instance;
	}

	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public void compile(Iterator<Rule> ruleset) {
	}

	@Override
	public void load(Iterator<Rule> ruleset, Iterator<Rule> compilation) {
	}

	@Override
	public Iterable<Rule> getSaturation() {
		return Collections.emptyList();
	}

	@Override
	public boolean isCompilable(Rule r) {
		return false;
	}

	@Override
	public boolean isMappable(Predicate father, Predicate son) {
		return son.equals(father);
	}

	@Override
	public Collection<Substitution> homomorphism(Atom father, Atom son, Substitution s) {
		Set<Variable> fixedTerms = s.getTerms();

		LinkedList<Substitution> res = new LinkedList<Substitution>();
		if (father.getPredicate().equals(son.getPredicate())) {
			Substitution sub = DefaultSubstitutionFactory.instance().createSubstitution();
			Iterator<Term> fatherTermsIt = father.getTerms().iterator();
			Iterator<Term> sonTermsIt = son.getTerms().iterator();

			Term fatherTerm, sonTerm;
			while (fatherTermsIt.hasNext() && sonTermsIt.hasNext()) {
				fatherTerm = fatherTermsIt.next();
				sonTerm = sonTermsIt.next();
				
				if (fatherTerm.isConstant()  || fixedTerms.contains(fatherTerm)) {
					if (!s.createImageOf(fatherTerm).equals(sonTerm)) {
						return res;
					}
				} else if (!sub.getTerms().contains(fatherTerm))
					sub.put((Variable) fatherTerm, sonTerm);
				else if (!sub.createImageOf(fatherTerm).equals(sonTerm))
					return res;
			}
			res.add(sub);
		}
		return res;
	}

	@Override
	public Collection<Partition<Term>> getUnification(Atom father, Atom son) {
		LinkedList<Partition<Term>> res = new LinkedList<Partition<Term>>();
		if (isMappable(father.getPredicate(), son.getPredicate())) {
			res.add(new Partition<Term>(father.getTerms(), son.getTerms()));
		}
		return res;
	}

	@Override
	public boolean isImplied(Atom father, Atom son) {
		return Objects.equals(son, father);
	}

	@Override
	public Collection<Pair<Atom, Substitution>> getRewritingOf(Atom father) {
		return Collections.<Pair<Atom, Substitution>> singleton(
		    new ImmutablePair<Atom, Substitution>(father, Substitutions.emptySubstitution()));
	}

	@Override
	public Collection<Predicate> getUnifiablePredicate(Predicate p) {
		return Collections.singleton(p);
	}

}
