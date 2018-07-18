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

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.RulesCompilation;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.core.Substitutions;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.util.profiler.Profiler;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
public abstract class AbstractRulesCompilation implements RulesCompilation {

	private Profiler profiler;

	@Override
	public void setProfiler(Profiler profiler) {
		this.profiler = profiler;
	}

	@Override
	public Profiler getProfiler() {
		return this.profiler;
	}

	@Override
	public InMemoryAtomSet getIrredondant(InMemoryAtomSet atomSet) {
		InMemoryAtomSet irr = new LinkedListAtomSet(atomSet);
		CloseableIteratorWithoutException<Atom> i = irr.iterator();
		CloseableIteratorWithoutException<Atom> j;
		InMemoryAtomSet toRemove = new LinkedListAtomSet();
		Atom origin;
		Atom target;
		boolean isSubsumed;
		while (i.hasNext()) {
			target = i.next();
			j = irr.iterator();
			isSubsumed = false;
			while (j.hasNext() && !isSubsumed) {
				origin = j.next();
				if (target != origin && !toRemove.contains(origin) && this.isImplied(target, origin)) {
					isSubsumed = true;
					toRemove.add(target);
				}
			}
		}

		irr.removeAll(toRemove);
		return irr;
	}

	/**
	 * Remove compilable rule from ruleSet and return a List of compilable
	 * rules.
	 *
	 * @param ruleSet
	 * @return a List containing the compilable rules.
	 */
	protected final LinkedList<Rule> extractCompilable(Iterator<Rule> ruleSet) {
		LinkedList<Rule> compilable = new LinkedList<Rule>();
		Rule r;

		while (ruleSet.hasNext()) {
			r = ruleSet.next();
			if (this.isCompilable(r)) {
				compilable.add(r);
				ruleSet.remove();
			}
		}

		if (this.getProfiler() != null) {
			this.getProfiler().put("Compiled rules", compilable.size());
		}

		return compilable;
	}
	
	public Collection<Substitution> homomorphism(Atom father, Atom son) {
		return this.homomorphism(father, son, Substitutions.emptySubstitution());
	}

}
