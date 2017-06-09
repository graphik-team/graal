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
package fr.lirmm.graphik.graal.rulesetanalyser.property;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.graal.core.Rules;
import fr.lirmm.graphik.graal.rulesetanalyser.graph.AffectedPositionSet;
import fr.lirmm.graphik.graal.rulesetanalyser.util.AnalyserRuleSet;

/**
 * At least one atom in the body of each rule contains all affected variables
 * from the body ({@link AffectedPositionSet}).
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * @author Swan Rocher
 * 
 */
public final class WeaklyGuardedSetProperty extends RuleSetProperty.Default {

	private static WeaklyGuardedSetProperty instance = null;
	private WeaklyGuardedSetProperty() { }

	public static synchronized WeaklyGuardedSetProperty instance() {
		if (instance == null) {
			instance = new WeaklyGuardedSetProperty();
		}
		return instance;
	}

	@Override
	public String getFullName() {
		return "Weakly guarded";
	}

	@Override
	public String getDescription() {
		return "At least one atom in the body of each rule contains all affected variables from the body (cf. affected position set).";
	}

	@Override
	public int check(AnalyserRuleSet ruleSet) {
		if (this.check(ruleSet.getAffectedPositionSet())) return 1;
		return -1;
	}

	@Override
	public String getLabel() {
		return "wg";
	}
	
	private boolean check(AffectedPositionSet affectedPositionSet) {
		for (Rule r : affectedPositionSet.getRules()) {
			Set<Variable> affectedVars = affectedPositionSet
					.getAllAffectedVariables(r.getBody());
			if (!Rules.isThereOneAtomThatContainsAllVars(r.getBody(),
					affectedVars)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public Iterable<RuleSetProperty> getGeneralisations() {
		List<RuleSetProperty> gen = new LinkedList<RuleSetProperty>();
		gen.add(WeaklyFrontierGuardedSetProperty.instance());
		gen.add(JointlyFrontierGuardedSetProperty.instance());
		gen.add(GBTSProperty.instance());
		gen.add(BTSProperty.instance());
		return gen;
	}

};

