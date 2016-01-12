/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2015)
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

import java.io.IOException;

import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.RuleSet;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismFactoryException;
import fr.lirmm.graphik.graal.core.atomset.graph.DefaultInMemoryGraphAtomSet;
import fr.lirmm.graphik.graal.core.ruleset.LinkedListRuleSet;
import fr.lirmm.graphik.graal.forward_chaining.StaticChase;
import fr.lirmm.graphik.graal.homomorphism.StaticHomomorphism;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.graal.io.dlp.DlgpWriter;
import fr.lirmm.graphik.util.stream.CloseableIterator;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class DefaultUseCaseExample {

	public static void main(String[] args) throws ChaseException, IOException, HomomorphismFactoryException, HomomorphismException {
		
		// /////////////////////////////////////////////////////////////////////
		// create an atom set
		InMemoryAtomSet atomSet = new DefaultInMemoryGraphAtomSet();

		// add assertions into this atom set
		atomSet.add(DlgpParser.parseAtom("p(a)."));
		atomSet.add(DlgpParser.parseAtom("p(c)."));
		atomSet.add(DlgpParser.parseAtom("q(b)."));
		atomSet.add(DlgpParser.parseAtom("q(c)."));
		atomSet.add(DlgpParser.parseAtom("s(z,z)."));
		
		// /////////////////////////////////////////////////////////////////////
		// create a rule set
		RuleSet ruleSet = new LinkedListRuleSet();
		
		// add a rule into this rule set
		ruleSet.add(DlgpParser.parseRule("r(X) :- p(X), q(X)."));
		ruleSet.add(DlgpParser.parseRule("s(X, Y) :- p(X), q(Y)."));
		
		// /////////////////////////////////////////////////////////////////////
		// run saturation
		StaticChase.executeChase(atomSet, ruleSet);
		// equivalent code:
		// Chase chase = new DefaultChase(ruleSet, atomSet);
		// chase.execute();
		
		// /////////////////////////////////////////////////////////////////////
		// show result with Dlgp format
		DlgpWriter writer = new DlgpWriter(System.out);
		writer.write(atomSet);
		// equivalent code:
		// for(Atom a : atomSet) {
		//	   writer.write(a);
		// }
		
		// /////////////////////////////////////////////////////////////////////
		// execute query
		ConjunctiveQuery query = DlgpParser.parseQuery("?(X,Y) :- s(X, Y), p(X), q(Y).");
		CloseableIterator<Substitution> subReader = StaticHomomorphism.instance().execute(query, atomSet);
		while (subReader.hasNext()) {
			System.out.println(subReader.next());
		}
		subReader.close();
				
	}
}
