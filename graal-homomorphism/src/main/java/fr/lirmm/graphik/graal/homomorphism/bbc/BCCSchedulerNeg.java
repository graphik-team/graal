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
package fr.lirmm.graphik.graal.homomorphism.bbc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.RulesCompilation;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.graal.core.HashMapSubstitution;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.homomorphism.Scheduler;
import fr.lirmm.graphik.graal.homomorphism.Var;
import fr.lirmm.graphik.graal.homorphism.utils.ProbaUtils;
import fr.lirmm.graphik.util.graph.DefaultDirectedEdge;
import fr.lirmm.graphik.util.graph.DefaultGraph;
import fr.lirmm.graphik.util.graph.DefaultHyperEdge;
import fr.lirmm.graphik.util.graph.DefaultHyperGraph;
import fr.lirmm.graphik.util.graph.DirectedEdge;
import fr.lirmm.graphik.util.graph.Graph;
import fr.lirmm.graphik.util.graph.HyperGraph;
import fr.lirmm.graphik.util.profiler.AbstractProfilable;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;

public class BCCSchedulerNeg extends BCCScheduler {
	
	/**
	 * @param bcc
	 */
	BCCSchedulerNeg(BCC bcc, boolean withForbiddenCandidate) {
		super(bcc, withForbiddenCandidate);
	}

	public Var[] execute(InMemoryAtomSet negH, List<Term> ans, AtomSet data, RulesCompilation rc) {
		 Comparator<Integer> varComparator;
		 Term[]              inverseMap;
		 
		Substitution s = new HashMapSubstitution();
		Term tmp = DefaultTermFactory.instance().createConstant("_tmp");
		for(Term t : ans) {
			if(t instanceof Variable)
			s.put((Variable)t, tmp);
		}
		InMemoryAtomSet h = s.createImageOf(negH);
		
		Set<Variable> variables = h.getVariables();

		// BCC
		Map<Term, Integer> map = new TreeMap<Term, Integer>();
		inverseMap = new Term[variables.size() + 1];
		HyperGraph graph = constructHyperGraph(h, variables, inverseMap, map, Collections.<Term>emptyList());
		
		double[] proba = computeProba(h, data, variables.size(), map, rc);
		varComparator = new IntegerComparator(proba);

		TmpData d = biconnect(graph, varComparator);

		Var[] vars = new Var[variables.size() + 2];
		this.BCC.varData = new VarData[ans.size() + variables.size() + 2];

		int j = 0;
		vars[0] = new Var(0);
		this.BCC.varData[0] = new VarData();
		
		for (int i = 1; i < d.vars.length; ++i) {
			Var v = d.vars[i];
			vars[j + v.level] = v;
			this.BCC.varData[v.level] = d.ext[i];
			v.value = (Variable) inverseMap[i];
			v.nextLevel = j + v.level + 1;
			v.previousLevel = j + v.level - 1;
			if (this.withForbiddenCandidate && this.BCC.varData[v.level].isAccesseur) {
				this.BCC.varData[v.level].forbidden = new TreeSet<Term>();
			}

		}

		int level = variables.size() + 1;
		vars[j + level] = new Var(level);
		this.BCC.varData[level] = new VarData();
		
		
		return vars;
	}


}