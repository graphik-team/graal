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
package fr.lirmm.graphik.graal.cqa;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Set;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.Query;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.VariableGenerator;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseHaltingCondition;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismFactoryException;
import fr.lirmm.graphik.graal.core.DefaultVariableGenerator;
import fr.lirmm.graphik.graal.forward_chaining.halting_condition.ConjunctiveQueryWithFixedVariables;
import fr.lirmm.graphik.graal.homomorphism.StaticHomomorphism;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.GIterator;
import fr.lirmm.graphik.util.stream.IteratorAdapter;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class FGHRuleChaseCondition implements ChaseHaltingCondition {

	public FGHRuleChaseCondition(AtomIndex index, FGH fgh) {
		this.index = index;
		this.fgh = fgh;
		this.existentialGen = new DefaultVariableGenerator("EE");
	}

	public FGHRuleChaseCondition(AtomIndex index, FGH fgh, VariableGenerator existentialGen) {
		this.index = index;
		this.fgh = fgh;
		this.existentialGen = existentialGen;
	}

	@Override
	public GIterator<Atom> apply(Rule rule, Substitution substitution, AtomSet data)
	                                                                                throws HomomorphismFactoryException,
	                                                                                HomomorphismException {
		Set<Term> fixedVars = substitution.getValues();

		// Generate new existential variables
		for (Term t : rule.getExistentials()) {
			substitution.put(t, existentialGen.getFreshVar());
		}

		LinkedList<Integer> causes = new LinkedList<Integer>();
		for (Atom a : substitution.createImageOf(rule.getBody())) {
			causes.add(new Integer(this.index.get(a)));
		}

		AtomSet newFacts = substitution.createImageOf(rule.getHead());
		Query query = new ConjunctiveQueryWithFixedVariables(newFacts, fixedVars);
		CloseableIterator<Substitution> executeQuery = StaticHomomorphism.instance().execute(query, data);

		if (executeQuery.hasNext()) {
			while (executeQuery.hasNext()) {
				Substitution next = executeQuery.next();
				for (Atom a : newFacts) {
					this.fgh.add(causes, this.index.get(next.createImageOf(a)));
				}
			}
			return new IteratorAdapter<Atom>(Collections.<Atom> emptyList().iterator());
		} else {
			for (Atom a : newFacts) {
				this.fgh.add(causes, this.index.get(a));
			}
			return newFacts.iterator();
		}

	}

	private AtomIndex index;
	private FGH fgh;
	private VariableGenerator existentialGen;

}
