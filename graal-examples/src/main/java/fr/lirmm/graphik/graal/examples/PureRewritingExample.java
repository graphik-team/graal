/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2016)
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
package fr.lirmm.graphik.graal.examples;

import fr.lirmm.graphik.graal.api.backward_chaining.BackwardChainer;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.RuleSet;
import fr.lirmm.graphik.graal.backward_chaining.pure.PureRewriter;
import fr.lirmm.graphik.graal.core.factory.ConjunctiveQueryFactory;
import fr.lirmm.graphik.graal.core.ruleset.LinkedListRuleSet;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class PureRewritingExample {

	public static void main(String args[]) throws Exception {
		
		// Query
		ConjunctiveQuery query = ConjunctiveQueryFactory.instance().create(DlgpParser.parseQuery("?(X) :- f(X)."));
		
		// RuleSet
		RuleSet rules = new LinkedListRuleSet();
		rules.add(DlgpParser.parseRule("b(X) :- a(X)."));
		rules.add(DlgpParser.parseRule("c(X) :- b(X)."));
		rules.add(DlgpParser.parseRule("d(X) :- c(X)."));
		rules.add(DlgpParser.parseRule("e(X) :- d(X)."));
		rules.add(DlgpParser.parseRule("f(X) :- e(X)."));
		rules.add(DlgpParser.parseRule("f(X) :- p(X,Y), q(X)."));
		rules.add(DlgpParser.parseRule("q(X) :- r(X)."));
		rules.add(DlgpParser.parseRule("r(X) :- s(X)."));
		
		BackwardChainer bc = new PureRewriter(query, rules);
		
		while(bc.hasNext()) {
			System.out.println(bc.next());
		}
	}
}
