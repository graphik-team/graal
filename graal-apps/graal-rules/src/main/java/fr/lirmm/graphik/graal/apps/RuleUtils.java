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
 package fr.lirmm.graphik.graal.apps;

import java.util.Iterator;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.term.Term;

public class RuleUtils {

	public static final RuleUtils INSTANCE = new RuleUtils();

	public static final String LABEL_PREFIX                 = "";
	public static final String LABEL_SUFFIX                 = ":";
	public static final String LABEL_SEPARATOR              = ";";

	// A(x) -> B(x)
	public static final String LABEL_CONCEPT_INCLUSION      = "CI";
	// p(x,y) -> r(x,y)
	public static final String LABEL_ROLE_INCLUSION         = "RI";
	// p(x,y) -> r(y,x)
	public static final String LABEL_INVERSE_ROLE           = "Inv";
	// p(x,y) -> a(x)
	public static final String LABEL_DOMAIN                 = "Dom";
	// p(x,y) -> a(y)
	public static final String LABEL_RANGE                  = "Rng";
	// a(x) -> p(x,y)
	public static final String LABEL_MANDATORY_ROLE         = "MR";
	// p(x,y),p(y,z) -> p(x,z)
	public static final String LABEL_TRANSITIVITY           = "Trans";
	// p(x,y),r(y,z) -> s(x,z)
	public static final String LABEL_ROLE_COMPOSITION       = "RC";
	// B -> x = y
	public static final String LABEL_FUNCTIONAL             = "Func";
	// ! a(x),b(x)
	public static final String LABEL_DISJOINT_CONCEPT       = "DC";
	// ! p(x,y),r(x,y)
	public static final String LABEL_DISJOINT_ROLE          = "DR";
	// ! B
	public static final String LABEL_NEGATIVE_CONSTRAINT    = "NC";

	public String computeBaseLabel(Rule r) {
		String label = LABEL_PREFIX;
		if (isConceptInclusion(r))
			label = updateLabel(label, LABEL_CONCEPT_INCLUSION);
		if (isRoleInclusion(r))
			label = updateLabel(label, LABEL_ROLE_INCLUSION);
		if (isInverseRole(r))
			label = updateLabel(label, LABEL_INVERSE_ROLE);
		if (isDomain(r))
			label = updateLabel(label, LABEL_DOMAIN);
		if (isRange(r))
			label = updateLabel(label, LABEL_RANGE);
		if (isMandatoryRole(r))
			label = updateLabel(label, LABEL_MANDATORY_ROLE);
		if (isTransitivity(r))
			label = updateLabel(label, LABEL_TRANSITIVITY);
		if (isRoleComposition(r))
			label = updateLabel(label, LABEL_ROLE_COMPOSITION);
		if (isFunctional(r))
			label = updateLabel(label, LABEL_FUNCTIONAL);
		if (isDisjointConcept(r))
			label = updateLabel(label, LABEL_DISJOINT_CONCEPT);
		if (isDisjointRole(r))
			label = updateLabel(label, LABEL_DISJOINT_ROLE);
		if (isDisjointInverseRole(r))
			label = updateLabel(label, LABEL_INVERSE_ROLE);
		if (isNegativeConstraint(r))
			label = updateLabel(label, LABEL_NEGATIVE_CONSTRAINT);
		label += LABEL_SUFFIX;
		return label;
	}

	public boolean isSingleton(AtomSet a) {
		Iterator<Atom> i = a.iterator();
		if (!i.hasNext()) return false;
		i.next();
		return !i.hasNext();
	}

	public boolean hasSize2(AtomSet a) {
		Iterator<Atom> i = a.iterator();
		if (!i.hasNext()) return false;
		i.next();
		if (!i.hasNext()) return false;
		i.next();
		return !i.hasNext();
	}

	public boolean isConcept(Atom a) {
		return a.getPredicate().getArity() == 1;
	}

	public boolean isRole(Atom a) {
		return a.getPredicate().getArity() == 2;
	}

	public boolean isInclusion(Rule r) {
		return isSingleton(r.getBody()) && isSingleton(r.getHead());
	}

	public boolean isConceptInclusion(Rule r) {
		if (!isInclusion(r)) return false;
		Atom C1 = r.getBody().iterator().next();
		Atom C2 = r.getHead().iterator().next();
		if (!isConcept(C1)) return false;
		if (!isConcept(C2)) return false;
		return C1.getTerm(0).equals(C2.getTerm(0));
	}

	public boolean isRoleInclusion(Rule r) {
		if (!isInclusion(r)) return false;
		Atom P1 = r.getBody().iterator().next();
		Atom P2 = r.getHead().iterator().next();
		if (!isRole(P1)) return false;
		if (!isRole(P2)) return false;
		Term t1_1 = P1.getTerm(0);
		Term t1_2 = P1.getTerm(1);
		Term t2_1 = P2.getTerm(0);
		Term t2_2 = P2.getTerm(1);
		return (t1_1.equals(t2_1) && t1_2.equals(t2_2))
			|| (t1_1.equals(t2_2) && t1_2.equals(t2_1));
	}

	public boolean isInverseRole(Rule r) {
		if (!isRoleInclusion(r)) return false;
		Atom P1 = r.getBody().iterator().next();
		Atom P2 = r.getHead().iterator().next();
		Term t1_1 = P1.getTerm(0);
		Term t1_2 = P1.getTerm(1);
		Term t2_1 = P2.getTerm(0);
		Term t2_2 = P2.getTerm(1);
		return t1_1.equals(t2_2) && t1_2.equals(t2_1);
	}

	public boolean isSignature(Rule r) {
		if (!isInclusion(r)) return false;
		Atom P1 = r.getBody().iterator().next();
		Atom C1 = r.getHead().iterator().next();
		return isRole(P1) && isConcept(C1);
	}

	public boolean isDomain(Rule r) {
		if (!isSignature(r)) return false;
		Atom P1 = r.getBody().iterator().next();
		Atom C1 = r.getHead().iterator().next();
		return P1.getTerm(0).equals(C1.getTerm(0));
	}

	public boolean isRange(Rule r) {
		if (!isSignature(r)) return false;
		Atom P1 = r.getBody().iterator().next();
		Atom C1 = r.getHead().iterator().next();
		return P1.getTerm(1).equals(C1.getTerm(0));
	}

	public boolean isMandatoryRole(Rule r) {
		if (!isInclusion(r)) return false;
		Atom C1 = r.getBody().iterator().next();
		Atom P1 = r.getHead().iterator().next();
		if (!isConcept(C1)) return false;
		if (!isRole(P1)) return false;
		return C1.getTerm(0).equals(P1.getTerm(0));
	}

	public boolean isRoleComposition(Rule r) {
		if (!hasSize2(r.getBody())) return false;
		if (!isSingleton(r.getHead())) return false;
		Iterator<Atom> b = r.getBody().iterator();
		Atom P1 = b.next();
		Atom P2 = b.next();
		Atom P3 = r.getHead().iterator().next();
		if (!isRole(P1)) return false;
		if (!isRole(P2)) return false;
		if (!isRole(P3)) return false;
		return P1.getTerm(0).equals(P3.getTerm(0))
			&& P1.getTerm(1).equals(P2.getTerm(0))
			&& P2.getTerm(1).equals(P3.getTerm(1));
	}

	public boolean isTransitivity(Rule r) {
		if (!isRoleComposition(r)) return false;
		Iterator<Atom> b = r.getBody().iterator();
		Atom P1 = b.next();
		Atom P2 = b.next();
		Atom P3 = r.getHead().iterator().next();
		return P1.getPredicate().equals(P2.getPredicate())
			&& P1.getPredicate().equals(P3.getPredicate());
	}

	public boolean isFunctional(Rule r) {
		if (!isSingleton(r.getHead())) return false;
		return r.getHead().iterator().next().getPredicate().equals(Predicate.EQUALITY);
	}

	public boolean isNegativeConstraint(Rule r) {
		if (!isSingleton(r.getHead())) return false;
		return r.getHead().iterator().next().equals(Atom.BOTTOM);
	}

	public boolean isDisjointConcept(Rule r) {
		if (!hasSize2(r.getBody())) return false;
		if (!isNegativeConstraint(r)) return false;
		Iterator<Atom> b = r.getBody().iterator();
		Atom C1 = b.next();
		Atom C2 = b.next();
		if (!isConcept(C1)) return false;
		if (!isConcept(C2)) return false;
		return (C1.getTerm(0).equals(C2.getTerm(0)));
	}

	public boolean isDisjointRole(Rule r) {
		if (!hasSize2(r.getBody())) return false;
		if (!isNegativeConstraint(r)) return false;
		Iterator<Atom> b = r.getBody().iterator();
		Atom P1 = b.next();
		Atom P2 = b.next();
		if (!isRole(P1)) return false;
		if (!isRole(P2)) return false;
		return (P1.getTerm(0).equals(P2.getTerm(0))
			 && P1.getTerm(1).equals(P2.getTerm(1)))
			|| (P1.getTerm(1).equals(P2.getTerm(0))
			 && P1.getTerm(0).equals(P2.getTerm(1)));
	}

	public boolean isDisjointInverseRole(Rule r) {
		if (!isDisjointRole(r)) return false;
		Iterator<Atom> b = r.getBody().iterator();
		Atom P1 = b.next();
		Atom P2 = b.next();
		return P1.getTerm(1).equals(P2.getTerm(0))
			&& P1.getTerm(0).equals(P2.getTerm(1));
	}

	private String updateLabel(String l, String s) {
		if (!l.equals(LABEL_PREFIX))
			l += LABEL_SEPARATOR;
		l += s;
		return l;
	}

	private RuleUtils() { }

};

