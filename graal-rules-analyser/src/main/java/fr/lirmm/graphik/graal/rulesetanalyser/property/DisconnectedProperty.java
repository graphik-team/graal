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
 package fr.lirmm.graphik.graal.rulesetanalyser.property;

import java.util.LinkedList;
import java.util.List;

import fr.lirmm.graphik.graal.api.core.Rule;

/**
 * The rule frontier is empty. Note that any disconnected rule needs to be
 * applied only once.
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * @author Swan Rocher
 * 
 */
public final class DisconnectedProperty extends RuleSetProperty.Local {

	private static DisconnectedProperty instance = null;
	
	private DisconnectedProperty(){}
	
	public static synchronized DisconnectedProperty instance() {
		if(instance == null) {
			instance = new DisconnectedProperty();
		}
		return instance;	
	}

	@Override
	public String getFullName() {
		return "Disconnected";
	}

	@Override
	public String getDescription() {
		return "The frontier is empty. Note that any disconnected rule needs to be applied only once.";
	}
	
	@Override
	public int check(Rule rule) {
		if (rule.getFrontier().isEmpty())
			return 1;
		return -1;
	}

	@Override
	public String getLabel() {
		return "disc";
	}

	@Override
	public Iterable<RuleSetProperty> getGeneralisations() {
		List<RuleSetProperty> gen = new LinkedList<RuleSetProperty>();
		gen.add(WeaklyAcyclicProperty.instance());
		gen.add(FrontierGuardedProperty.instance());
		gen.add(DomainRestrictedProperty.instance());
		gen.add(FESProperty.instance());
		gen.add(FUSProperty.instance());
		gen.add(GBTSProperty.instance());
		gen.add(BTSProperty.instance());
		return gen;
	}

};

