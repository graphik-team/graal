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

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.rulesetanalyser.graph.MarkedVariableSet;
import fr.lirmm.graphik.graal.rulesetanalyser.graph.MarkedVariableSet.MarkedRule;
import fr.lirmm.graphik.graal.rulesetanalyser.util.AnalyserRuleSet;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;

/**
 * Each marked variable occurs at most once in a rule body
 * (cf. {@link MarkedVariableSet}).
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * @author Swan Rocher
 * 
 */
public final class StickyProperty extends RuleSetProperty.Default {

	private static StickyProperty instance = null;
	
	private StickyProperty() { }
	
	public static synchronized StickyProperty instance() {
		if(instance == null) {
			instance = new StickyProperty();
		}
		return instance;
	}

	@Override
	public String getFullName() {
		return "Sticky";
	}

	@Override
	public String getDescription() {
		return "Each marked variable occurs at most once in a rule body (cf. marked variable set).";
	}
	

	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
    public int check(AnalyserRuleSet ruleSet) {
		if (this.check(ruleSet.getMarkedVariableSet())) return 1;
		return -1;
	}

	public boolean check(MarkedVariableSet markedVariableSet) {
		int nbOccurence;
		for (MarkedRule mrule : markedVariableSet.getMarkedRuleCollection()) {
			for (Term mvar : mrule.markedVars) {
				nbOccurence = 0;
				CloseableIteratorWithoutException<Atom> it = mrule.rule.getBody().iterator();
				while (it.hasNext()) {
					Atom a = it.next();
					for (Term t : a) {
						if (mvar.equals(t)) {
							++nbOccurence;
							if (nbOccurence > 1) {
								return false;
							}
						}
					}
				}
			}
		}
		return true;
	}

	@Override
	public String getLabel() {
		return "s";
	}

	@Override
	public Iterable<RuleSetProperty> getGeneralisations() {
		List<RuleSetProperty> gen = new LinkedList<RuleSetProperty>();
		gen.add(FUSProperty.instance());
		return gen;
	}

};

