/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2016)
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

import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.core.Rules;

public final class RuleLabeler {

	public static final String LABEL_PREFIX                 = "_";
	public static final String LABEL_SUFFIX                 = "";
	public static final String LABEL_SEPARATOR              = "-";

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
	// a(y) -> p(x,y)
	public static final String LABEL_INVERSE_MANDATORY_ROLE = "IMR";
	// a(x) -> p(x,y), b(y)
	public static final String LABEL_EXIST_RC               = "ERC";
	// a(x) -> p(y,x), b(y)
	public static final String LABEL_INV_EXIST_RC           = "IERC";
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

	public static String computeBaseLabel(Rule r) {
		String label = LABEL_PREFIX;
		if (Rules.isConceptInclusion(r))
			label = updateLabel(label, LABEL_CONCEPT_INCLUSION);
		if (Rules.isRoleInclusion(r))
			label = updateLabel(label, LABEL_ROLE_INCLUSION);
		if (Rules.isInverseRole(r))
			label = updateLabel(label, LABEL_INVERSE_ROLE);
		if (Rules.isDomain(r))
			label = updateLabel(label, LABEL_DOMAIN);
		if (Rules.isRange(r))
			label = updateLabel(label, LABEL_RANGE);
		if (Rules.isMandatoryRole(r))
			label = updateLabel(label, LABEL_MANDATORY_ROLE);
		if (Rules.isInvMandatoryRole(r))
			label = updateLabel(label, LABEL_INVERSE_MANDATORY_ROLE);
		if (Rules.isExistRC(r))
			label = updateLabel(label, LABEL_EXIST_RC);
		if (Rules.isInvExistRC(r))
			label = updateLabel(label, LABEL_INV_EXIST_RC);
		if (Rules.isTransitivity(r))
			label = updateLabel(label, LABEL_TRANSITIVITY);
		if (Rules.isRoleComposition(r))
			label = updateLabel(label, LABEL_ROLE_COMPOSITION);
		if (Rules.isFunctional(r))
			label = updateLabel(label, LABEL_FUNCTIONAL);
		if (Rules.isDisjointConcept(r))
			label = updateLabel(label, LABEL_DISJOINT_CONCEPT);
		if (Rules.isDisjointRole(r))
			label = updateLabel(label, LABEL_DISJOINT_ROLE);
		if (Rules.isDisjointInverseRole(r))
			label = updateLabel(label, LABEL_INVERSE_ROLE);
		if (Rules.isNegativeConstraint(r))
			label = updateLabel(label, LABEL_NEGATIVE_CONSTRAINT);
		label += LABEL_SUFFIX;
		return label;
	}

	private static String updateLabel(String l, String s) {
		if (!l.equals(LABEL_PREFIX))
			l += LABEL_SEPARATOR;
		l += s;
		return l;
	}

	private RuleLabeler() { }

};

