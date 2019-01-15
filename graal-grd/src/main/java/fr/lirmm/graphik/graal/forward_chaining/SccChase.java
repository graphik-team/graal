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
package fr.lirmm.graphik.graal.forward_chaining;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.GraphOfRuleDependencies;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.forward_chaining.AbstractChase;
import fr.lirmm.graphik.graal.api.forward_chaining.Chase;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.api.forward_chaining.RuleApplicationException;
import fr.lirmm.graphik.graal.api.forward_chaining.RuleApplier;
import fr.lirmm.graphik.graal.core.grd.DefaultGraphOfRuleDependencies;
import fr.lirmm.graphik.graal.forward_chaining.rule_applier.RestrictedChaseRuleApplier;
import fr.lirmm.graphik.util.graph.scc.StronglyConnectedComponentsGraph;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.CloseableIteratorAdapter;
import fr.lirmm.graphik.util.stream.IteratorException;

/**
 * This Chase apply rules following the layers of the Strongly Connected
 * Components Graph (SCCG) of the Graph Of Rule Dependencies (GRD). It applies
 * rules layer by layer, indeed a rule from a layer of the SCCG can not trigger
 * a rule from a previous layer. <br/>
 * This chase is not breadth first.
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class SccChase<T extends AtomSet> extends AbstractChase<Rule,T> {

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	private GraphOfRuleDependencies grd;
	private T atomSet;
	private List<Atom> tmpAtom;
	private Queue<Rule> queue = new LinkedList<Rule>();
	private List<Integer>[] layers;
	int level = -1;
	int levelmax;
	StronglyConnectedComponentsGraph<Rule> sccg;

	public SccChase(GraphOfRuleDependencies grd, T atomSet, RuleApplier<Rule, ? super T> ruleApplier) {
		super(ruleApplier);
		this.grd = grd;
		this.atomSet = atomSet;
		for (Rule r : grd.getRules()) {
			this.queue.add(r);
		}
		init();
	}

	public SccChase(Iterator<Rule> rules, T atomSet, RuleApplier<Rule, ? super T> ruleApplier) {
		this(new DefaultGraphOfRuleDependencies(rules), atomSet, ruleApplier);
	}

	public SccChase(GraphOfRuleDependencies grd, T atomSet) {
		this(grd, atomSet, new RestrictedChaseRuleApplier<T>());
	}

	public SccChase(Iterator<Rule> rules, T atomSet) {
		this(new DefaultGraphOfRuleDependencies(rules), atomSet);
	}

	// compute scc layer
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private final void init() {
		sccg = this.grd.getStronglyConnectedComponentsGraph();
		int[] sccLayer = sccg.computeLayers(sccg.getSources(), true);
		layers = new List[sccLayer.length];
		levelmax = -1;
		for (int scc = 0; scc < sccLayer.length; ++scc) {
			if (sccLayer[scc] > levelmax) {
				levelmax = sccLayer[scc];
			}
			List l = layers[sccLayer[scc]];
			if (l == null) {
				l = new LinkedList();
				layers[sccLayer[scc]] = l;
			}
			l.add(scc);
		}

	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public void next() throws ChaseException {
		++this.level;
		tmpAtom = new LinkedList<Atom>();

		for (Integer scc : layers[level]) {
			Set<Rule> component = this.sccg.getComponent(scc);
			GraphOfRuleDependencies subGraph = this.grd.getSubGraph(component);

			if (component.size() == 1 && !subGraph.hasCircuit()) {
				try {
					CloseableIterator<Atom> it = this.getRuleApplier().delegatedApply(component.iterator().next(), atomSet);
					while(it.hasNext()) {
						tmpAtom.add(it.next());
					}
					it.close();
				} catch (RuleApplicationException e) {
					throw new ChaseException("", e);
				} catch (IteratorException e) {
					throw new ChaseException("", e);
				}
			} else {
				Chase chase = new ChaseWithGRD<T>(subGraph, atomSet, this.getRuleApplier());
				chase.execute();
			}
		}
		try {
			atomSet.addAll(new CloseableIteratorAdapter<Atom>(tmpAtom.iterator()));
		} catch (AtomSetException e) {
			throw new ChaseException("", e);
		}
	}

	@Override
	public boolean hasNext() {
		return this.level < this.levelmax;
	}

}
