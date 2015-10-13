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
 package fr.lirmm.graphik.graal.cqa;

import java.util.LinkedList;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.Query;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.forward_chaining.RuleApplicationHandler;
import fr.lirmm.graphik.graal.api.homomorphism.Homomorphism;
import fr.lirmm.graphik.graal.core.DefaultConjunctiveQuery;

public class FGHRuleApplicationHandler implements RuleApplicationHandler {

	public FGHRuleApplicationHandler(AtomIndex index, FGH fgh) {
		this.index = index;
		this.fgh = fgh;
	}

	@Override
	public boolean onRuleApplication(Rule rule, Substitution substitution, AtomSet base) {
		AtomSet from = substitution.createImageOf(rule.getBody());
		AtomSet atomSet = substitution.createImageOf(rule.getHead());

		try {
		Query q = new DefaultConjunctiveQuery(from,from.getTerms(Term.Type.VARIABLE));
		for (Substitution s : this.solver.execute(q,base)) {

			//AtomSet from2 = s.getSubstitut(from);

			LinkedList causes = new LinkedList<Integer>();
			for (Atom a : from) {
				causes.add(new Integer(this.index.get(s.createImageOf(a))));
			}
			for (Atom a : atomSet) {
				this.fgh.add(causes,this.index.get(a));
			}
		}

		return true;
		}
		catch (Exception e) {
			System.err.println(e);
			e.printStackTrace();
		}
		return true;
	}

	public void setSolver(Homomorphism solver) {
		this.solver = solver;
	}

	private Homomorphism solver;
	private AtomIndex   index;
	private FGH         fgh;

};

