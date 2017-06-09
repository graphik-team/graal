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
package fr.lirmm.graphik.graal.rulesetanalyser.graph;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;

/**
 * A position p[i] is affected if there exists an existential variable occurring 
 * in position p[i] or if there is a frontier variable on position p[i] in the head 
 * of a rule occurring in an affected position in its body.
 *
 * A variable from a rule body is said to be affected if it occurs in some affected position.
 * 
 * The affected position set of a rule set R is the set of all affected positions in R. 
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * @author Swan Rocher
 * 
 */
public class AffectedPositionSet extends AbstractAffectedPositionSet {



	public AffectedPositionSet(Iterable<Rule> ruleSet) {
		super(ruleSet);
	}
	
	
	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

	

	

	/**
	 * for each rule and for each variable x that occurs at some affected
	 * positions in its body, all positions q[j] in its head where occurs x are
	 * affected.
	 */
	protected void step2() {
		InMemoryAtomSet body;
		CloseableIteratorWithoutException<Atom> atomIt;
		Atom a;
		boolean fixPoint = false;

		while (!fixPoint) {
			fixPoint = true;
			for (Rule rule : ruleSet) {
				body = rule.getBody();
				atomIt = body.iterator();
				while(atomIt.hasNext()) {
					a = atomIt.next();
					for(int i = 0; i < a.getPredicate().getArity(); ++i) {
						if(isAffected(a.getPredicate(), i)) {
							this.affectInHead(rule, a.getTerm(i));
						}
					}
				}
			}
		}
	}

	

};
