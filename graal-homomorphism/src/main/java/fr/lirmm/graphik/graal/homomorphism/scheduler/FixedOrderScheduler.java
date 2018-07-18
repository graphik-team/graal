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
package fr.lirmm.graphik.graal.homomorphism.scheduler;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.RulesCompilation;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.homomorphism.Var;
import fr.lirmm.graphik.graal.homomorphism.VarSharedData;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class FixedOrderScheduler extends AbstractScheduler implements Scheduler {

	private List<Variable> order;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	public FixedOrderScheduler(List<Variable> order) {
		this.order = order;
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public VarSharedData[] execute(InMemoryAtomSet query, Set<Variable> preAffectedVars, List<Term> ans, AtomSet data, RulesCompilation rc) throws HomomorphismException {
		InMemoryAtomSet h = (preAffectedVars.isEmpty())? query : computeFixedQuery(query, preAffectedVars);

		Set<Variable> terms = h.getVariables();
		VarSharedData[] vars = new VarSharedData[terms.size() + 2];

		int level = 0;
		vars[level] = new VarSharedData(level);

		Set<Term> alreadyAffected = new TreeSet<Term>();
		for (Variable v : this.order) {
			if(!terms.contains(v)) {
				throw new HomomorphismException("Try to schedule a variable which is not in the query :" + v);
			}
			if(alreadyAffected.contains(v)) {
				throw new HomomorphismException("There is two occurences of the same variable in the specified order.");
			}
			++level;
			vars[level] = new VarSharedData(level);
			vars[level].value = v;
			alreadyAffected.add(v);
		}

		terms.removeAll(alreadyAffected);
		if(!terms.isEmpty()) {
			throw new HomomorphismException("Some variables of the query are not scheduled :" + terms);
		}

		++level;
		vars[level] = new VarSharedData(level);
		vars[level].previousLevel = level - 1;

		return vars;
	}
	
	public void clear() {
		
	}
	@Override
	public boolean isAllowed(Var var, Term image) {
		return true;
	}
	
	@Override
	public String getInfos(Var var) {
		return Integer.toString(order.indexOf(var.shared.value));
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// OBJECT OVERRIDE METHODS
	// /////////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

}
