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
package fr.lirmm.graphik.graal.api.core;

import java.util.List;

/**
 * <p>
 * This interface represents a conjunctive query with negated parts. Such a 
 * query is composed of a set of atoms which must be true and other sets of
 * atoms whose for each set at least one atom must be false.
 * </p>
 * <p>
 * In the following, X is the set of free variables (answer variables), Y the
 * set of variables that appear in the positive part minus X and Zi where i ∈
 * [1..n] the set of variables that appear only in the ith negated part.
 * Note that each variable from X and Y must appear in the positive part but 
 * only a subpart of them may appear in each Zi. $ ∀i,j ∈
 * [1..n] such that i ≠ j, Zi ∩ Zj = ∅.
 * <br/>
 * A conjunctive query with negated parts Q is formally defined as
 * ∃Y∀Z1..Zn(Q+[X,Y] ∧ not(Q1[X,Y,Z1]) ∧ ... ∧ not(Qn[X,Y,Zn])) where Q+, Q1, ...,
 * Qn are conjunctions of atoms over specified variable sets.
 * </p>
 * <p>
 * A mapping A from X to a set of terms is an answer to this query with respect
 * to a set of facts iff A(Fq) is true, where Fq is the formula associated to Q.
 * </p>
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public interface ConjunctiveQueryWithNegatedParts extends Query {

	/**
	 * The label (the name) for this query.
	 * 
	 * @return the label of this query.
	 */
	String getLabel();

	/**
	 * Get the set of facts which must be true.
	 * 
	 * @return an atom set representing the atom conjunction of the query.
	 */
	InMemoryAtomSet getPositivePart();

	/**
	 * Get the set of facts which must be false (at least one).
	 * 
	 * @return a list of atom sets representing negated conjunctions of atoms.
	 */
	List<InMemoryAtomSet> getNegatedParts();

	/**
	 * Get the answer variables
	 * 
	 * @return an Collection of Term representing the answer variables.
	 */
	List<Term> getAnswerVariables();


}
