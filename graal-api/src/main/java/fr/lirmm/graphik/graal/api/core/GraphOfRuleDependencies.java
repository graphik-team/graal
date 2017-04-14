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
package fr.lirmm.graphik.graal.api.core;

import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import fr.lirmm.graphik.util.graph.scc.StronglyConnectedComponentsGraph;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public interface GraphOfRuleDependencies {

	/**
	 * Returns true, if there exists an unifier from src to dest, false otherwise.
	 * @param src
	 * @param dest
	 * @return true iff there exists an unifier from src to dest.
	 */
	boolean existUnifier(Rule src, Rule dest);
	
	/**
	 * Returns a set of Substitution representing the set of unifiers from src to dest.
	 * @param src 
	 * @param dest
	 * @return a set of Substitution representing the set of unifiers from src to dest.
	 */
	Set<Substitution> getUnifiers(Rule src, Rule dest);
	
	/**
	 * Returns all rules that can be triggered by the specified one.
	 * @param src a Rule.
	 * @return all rules that can be triggered by the specified one.
	 */
	Set<Rule> getTriggeredRules(Rule src);
	
	/**
	 * Returns all pair of rule and unifier that can be triggered by the specfied rule.
	 * @param src
	 * @return all pair of rule and unifier that can be triggered by the specfied rule.
	 */
	Set<Pair<Rule,Substitution>> getTriggeredRulesWithUnifiers(Rule src);
	
	/**
     * Performs cycle detection on the {@link GraphOfRuleDependencies}.
     *
     * @return true iff the graph contains at least one cycle.
     */
	boolean hasCircuit();

	/**
	 * Creates and return an induced SubGraph of the current one.
	 * @param ruleSet
	 * @return the induced subgraph by specified rules.
	 */
	GraphOfRuleDependencies getSubGraph(Iterable<Rule> ruleSet);

	/**
	 * @return all rules containing in this {@link GraphOfRuleDependencies}.
	 */
	Iterable<Rule> getRules();


	/**
	 * @return the graph of strongly connected components of this graph.
	 */
	StronglyConnectedComponentsGraph<Rule> getStronglyConnectedComponentsGraph();

}
