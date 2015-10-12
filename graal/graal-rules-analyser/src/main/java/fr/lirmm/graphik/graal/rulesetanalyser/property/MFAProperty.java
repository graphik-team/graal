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
package fr.lirmm.graphik.graal.rulesetanalyser.property;

import java.util.LinkedList;
import java.util.List;

import fr.lirmm.graphik.graal.rulesetanalyser.util.AnalyserRuleSet;

/**
 * There is no cycle of functional symbol during the skolem chase 
 * executed on the critical instance.
 */
/*
public final class MFAProperty extends RuleSetProperty.Default {

	private static MFAProperty instance = null;

	private MFAProperty() { }

	public static synchronized MFAProperty instance() {
		if (instance == null) {
			instance = new MFAProperty();
		}
		return instance;
	}

	@Override
	public int check(AnalyserRuleSet ruleSet) {
		// first rewrite the rules
		// then call the skolem chase
		// then see if it halts
		// 
		// I think that for now, we don't have an easy way to have
		// the semi-decidability.
		// It should be added somewhere.
		// (perhaps a class 'Reasoner' that takes all the knowledge
		// base and effectively computes forward chaining but
		// halts if there is an answer (if the query is boolean),
		// or when all answers are found (if it is not))
		return 0;
	}

	@Override
	public String getLabel() {
		return "mfa";
	}

	@Override
	public Iterable<RuleSetProperty> getGeneralisations() {
		List<RuleSetProperty> gen = new LinkedList<RuleSetProperty>();
		gen.add(FESProperty.instance());
		gen.add(BTSProperty.instance());
		return gen;
	}

};*/

