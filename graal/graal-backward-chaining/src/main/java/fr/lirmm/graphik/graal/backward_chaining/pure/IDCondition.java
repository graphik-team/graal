/* Graal v0.7.4
 * Copyright (c) 2014-2015 Inria Sophia Antipolis - Méditerranée / LIRMM (Université de Montpellier & CNRS)
 * All rights reserved.
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * Author(s): Clément SIPIETER
 *            Mélanie KÖNIG
 *            Swan ROCHER
 *            Jean-François BAGET
 *            Michel LECLÈRE
 *            Marie-Laure MUGNIER
 */
 /**
 * 
 */
package fr.lirmm.graphik.graal.backward_chaining.pure;

import java.util.List;

import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.term.Term;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
interface IDCondition {

	List<Integer> getBody();
	/**
	 * @param body
	 * @param head
	 * @return true iff the body imply the head by this condition.
	 */
	boolean imply(List<Term> body, List<Term> head);

	/**
	 * @param head
	 * @return true iff the given terms fulfills the condition on the head
	 */
	boolean checkHead(List<Term> head);

	/**
	 * 
	 * @param body
	 * @return true iff the given terms fulfills the condition on the body
	 */
	boolean checkBody(List<Term> body);

	/**
	 * Generate body according to the given terms of the head
	 * 
	 * @param head
	 * @return a Term list.
	 */
	List<Term> generateBody(List<Term> head);

	/**
	 * Generate head
	 * 
	 * @param body
	 * @return
	 */
	List<Term> generateHead();

	/**
	 * Generate the needed unification between the head and the body.
	 * 
	 * @param body
	 * @param head
	 * @return a partition that represents the unification.
	 */
	TermPartition generateUnification(List<Term> body, List<Term> head);

	/**
	 * Compose the current IDCondition with another IDCondition. (x,y,x) ->
	 * (x,y) with (x,x) -> (x) produce (x,x,x) -> (x) (x,y,x) -> (y,x) with
	 * (x,y) -> (y) produce (x,y,x) -> (y)
	 * 
	 * @param condition2
	 * @return a new IDCondition representing the composition.
	 */
	IDCondition composeWith(IDCondition condition2);

	/**
	 * @param p
	 * @param q
	 */
	Rule generateRule(Predicate bodyPredicate, Predicate headPredicate);

	/**
	 * @return
	 */
	boolean isIdentity();

}
