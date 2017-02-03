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

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.rulesetanalyser.graph.GraphPositionDependencies;
import fr.lirmm.graphik.graal.rulesetanalyser.graph.MarkedVariableSet;
import fr.lirmm.graphik.graal.rulesetanalyser.graph.MarkedVariableSet.MarkedRule;
import fr.lirmm.graphik.graal.rulesetanalyser.util.AnalyserRuleSet;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;

/**
 * This class does not belong to any abstract class defined earlier. It is a
 * generalization of Sticky ({@link StickyProperty}) and Weakly-Acyclic (
 * {@link WeaklyAcyclicProperty}). It relies on the same graph of position
 * dependencies ({@link GraphPositionDependencies}) as the Weakly-Acyclic test
 * and on the same marking procedure as the Sticky test. Property: All marked
 * variables that occur more than once in a rule body appear at some position of
 * finite rank.
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * @author Swan Rocher
 * 
 */
public final class WeaklyStickyProperty extends RuleSetProperty.Default {

	private static WeaklyStickyProperty instance = null;
	private WeaklyStickyProperty() { }

	public static synchronized WeaklyStickyProperty instance() {
		if (instance == null) {
			instance = new WeaklyStickyProperty();
		}
		return instance;
	}

	@Override
    public int check(AnalyserRuleSet ruleSet) {
		if (this.check(ruleSet.getMarkedVariableSet(), ruleSet.getGraphPositionDependencies())) return 1;
		return -1;
	}

	@Override
	public String getFullName() {
		return "Weakly sticky";
	}

	@Override
	public String getDescription() {
		return "This class does not belong to any abstract class defined earlier. It is a generalisation of Sticky and Weakly-Acyclic. It relies upon the same graph of position dependencies as the Weakly-Acyclic test and upon the same marking procedure as the Sticky test. All marked variables that occur more than once in a rule body appear at some position of finite rank.";
	}
	
	@Override
	public String getLabel() {
		return "ws";
	}
	
	private boolean check(MarkedVariableSet markedVariableSet, GraphPositionDependencies gpd) {
		int nbOccurence;
		int position;
		boolean thereIsAFiniteRank;
		for (MarkedRule mrule : markedVariableSet.getMarkedRuleCollection()) {
			for (Term mvar : mrule.markedVars) {
				nbOccurence = 0;
				thereIsAFiniteRank = false;
				CloseableIteratorWithoutException<Atom> it = mrule.rule.getBody().iterator();
				while (it.hasNext()) {
					Atom a = it.next();
					position = -1;
					for (Term t : a) {
						++position;
						if (mvar.equals(t)) {
							++nbOccurence;
							if (gpd.isFiniteRank(a.getPredicate(), position)) {
								thereIsAFiniteRank = true;
							}
						}
					}
				}
				if (nbOccurence > 1 && !thereIsAFiniteRank) {
					return false;
				}
			}
		}
		return true;
	}

};

