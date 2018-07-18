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
package fr.lirmm.graphik.graal.core.compilation;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.util.Partition;

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
	 * 
	 * @param body
	 * @return true iff the given terms fulfills the condition on the body
	 */
	boolean checkBody(List<Term> body);

	/**
	 * Generate body according to the given terms of the head
	 * 
	 * @param head
	 * @return a Term list
	 */
	Pair<List<Term>, Substitution> generateBody(List<Term> head);

	/**
	 * Generate head
	 * 
	 * @return TODO
	 */
	List<Term> generateHead();

	/**
	 * Generate the needed unification between the head and the body.
	 * 
	 * @param body
	 * @param head
	 * @return a partition that represents the unification.
	 */
	Partition<Term> generateUnification(List<Term> body, List<Term> head);

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
	 * Generate the rule corresponding to this IDCondition.
	 * 
	 * @param bodyPredicate
	 *            the predicate to use in the body
	 * @param headPredicate
	 *            the predicate to use in the head
	 */
	Rule generateRule(Predicate bodyPredicate, Predicate headPredicate);

	/**
	 * @return true if the current IDCondition represents an identity condition, false otherwise.
	 */
	boolean isIdentity();
	
	/**
	 * @param head
	 * @param body
	 * @return  TODO
	 */
	Substitution homomorphism(List<Term> head, List<Term> body);

	/**
	 * @param head
	 * @param body
	 * @return  TODO
	 */
	Substitution homomorphism(List<Term> head, List<Term> body, Substitution s);

}
