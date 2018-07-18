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
package fr.lirmm.graphik.graal.forward_chaining.halting_condition;

import java.util.Collections;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseHaltingCondition;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismFactoryException;
import fr.lirmm.graphik.graal.core.factory.DefaultConjunctiveQueryFactory;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.homomorphism.SmartHomomorphism;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.CloseableIteratorAdapter;

/**
 * This HaltingCondition will produce new data according to a rule, a
 * substitution and specified data if it has not already been applied with an
 * other substitution that maps the rule's frontier in the same way. This chase
 * is also known as semi-oblivious chase and is equivalent to the skolem chase.
 * 
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class FrontierRestrictedChaseHaltingCondition implements ChaseHaltingCondition {

	private static final Logger LOGGER = LoggerFactory.getLogger(FrontierRestrictedChaseHaltingCondition.class);

	private Map<Rule,Integer> ruleIndex = new TreeMap<Rule,Integer>();
	private int _currentRuleIndex = 0;

	@Override
	public CloseableIterator<Atom> apply(Rule rule, Substitution substitution, AtomSet data)
	                                                                                 throws HomomorphismFactoryException,
	                                                                                 HomomorphismException {
		if (ruleIndex.get(rule) == null) {
			ruleIndex.put(rule, _currentRuleIndex++);
		}

		final int index = ruleIndex.get(rule).intValue();
		StringBuilder frontierSb = new StringBuilder();
		SortedSet<Variable> frontierSet = new TreeSet<Variable>(rule.getFrontier());
		for (Term t : frontierSet) {
			frontierSb.append("_");
			frontierSb.append(t.getLabel());
			frontierSb.append("-");
			frontierSb.append(substitution.createImageOf(t).getLabel());
		}
		String frontier = frontierSb.toString();
		
		for (Variable t : rule.getExistentials()) {
			substitution.put(t,
			    DefaultTermFactory.instance().createConstant("f_" + index + "_" + t.getIdentifier() + frontier));
		}

		ConjunctiveQuery query = DefaultConjunctiveQueryFactory.instance().create(rule.getHead());

		if (SmartHomomorphism.instance().exist(query, data, substitution)) {
			return new CloseableIteratorAdapter<Atom>(Collections.<Atom> emptyList().iterator());
		}

		return substitution.createImageOf(rule.getHead()).iterator();
	}

};

