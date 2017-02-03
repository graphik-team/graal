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
package fr.lirmm.graphik.graal.kb;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.Mapper;
import fr.lirmm.graphik.graal.api.core.MutableMapper;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.RuleSet;
import fr.lirmm.graphik.graal.api.core.RuleSetException;
import fr.lirmm.graphik.graal.api.kb.KnowledgeBase;
import fr.lirmm.graphik.graal.api.kb.Priority;
import fr.lirmm.graphik.graal.core.atomset.graph.DefaultInMemoryGraphAtomSet;
import fr.lirmm.graphik.graal.core.mapper.MappedRuleSet;
import fr.lirmm.graphik.graal.core.mapper.MappedStore;
import fr.lirmm.graphik.graal.core.mapper.MapperAtomConverter;
import fr.lirmm.graphik.graal.core.mapper.MapperRuleConverter;
import fr.lirmm.graphik.graal.core.ruleset.LinkedListRuleSet;
import fr.lirmm.graphik.graal.core.stream.filter.AtomFilterIterator;
import fr.lirmm.graphik.graal.core.stream.filter.RuleFilterIterator;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.IteratorException;
import fr.lirmm.graphik.util.stream.converter.Converter;
import fr.lirmm.graphik.util.stream.converter.ConverterCloseableIterator;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class KBBuilder {

	private AtomSet store = new DefaultInMemoryGraphAtomSet();
	private AtomSet mappedStore = store;
	private RuleSet ontology = new LinkedListRuleSet();
	private RuleSet mappedOntology = ontology;
	private MutableMapper mapper;
	private Priority priority;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	public KnowledgeBase build() {
		DefaultKnowledgeBase kb = new DefaultKnowledgeBase(this.mappedStore, this.mappedOntology);
		kb.setPriority(this.priority);
		return kb;
	}

	public void setStore(AtomSet store) {
		this.store = store;
		if (mapper != null) {
			this.mappedStore = new MappedStore(store, mapper);
		} else {
			this.mappedStore = store;
		}
	}
	
	public void setOntology(RuleSet rules) {
		this.ontology = rules;
	}

	public void setMapper(MutableMapper mapper) {
		this.mapper = mapper;
		this.mappedStore = new MappedStore(store, mapper);
		this.mappedOntology = new MappedRuleSet(ontology, mapper);
	}

	public void addAll(CloseableIterator<Object> it) throws KBBuilderException {
		Object o;
		try {
			while (it.hasNext()) {
				o = it.next();
				if (o instanceof Rule) {
					this.ontology.add((Rule) o);
				} else if (o instanceof Atom) {
					this.mappedStore.add((Atom) o);
				}
			}
		} catch (IteratorException e) {
			throw new KBBuilderException(e);
		} catch (AtomSetException e) {
			throw new KBBuilderException(e);
		}
	}

	public void add(Rule rule) {
		this.ontology.add(rule);
	}

	public void add(Rule rule, Mapper mapper) {
		this.ontology.add(mapper.map(rule));
	}

	public void addRules(CloseableIterator<Object> it) throws KBBuilderException {
		try {
			this.ontology.addAll(new RuleFilterIterator(it));
		} catch (RuleSetException e) {
			throw new KBBuilderException(e);
		}
	}

	public void addRules(CloseableIterator<Object> it, Mapper mapper) throws KBBuilderException {
		try {
			Converter<Rule, Rule> converter = new MapperRuleConverter(mapper);
			this.ontology.addAll(new ConverterCloseableIterator<Rule, Rule>(new RuleFilterIterator(it), converter));
		} catch (RuleSetException e) {
			throw new KBBuilderException(e);
		}
	}

	public void add(Atom atom) throws KBBuilderException {
		try {
			this.mappedStore.add(atom);
		} catch (AtomSetException e) {
			throw new KBBuilderException(e);
		}
	}

	public void add(Atom atom, Mapper mapper) throws KBBuilderException {
		try {
			this.mappedStore.add(mapper.map(atom));
		} catch (AtomSetException e) {
			throw new KBBuilderException(e);
		}
	}

	public void addAtoms(CloseableIterator<Object> it) throws KBBuilderException {
		try {
			this.mappedStore.addAll(new AtomFilterIterator(it));
		} catch (AtomSetException e) {
			throw new KBBuilderException(e);
		}
	}

	public void addAtoms(CloseableIterator<Object> it, Mapper mapper) throws KBBuilderException {
		try {
			Converter<Atom, Atom> converter = new MapperAtomConverter(mapper);
			this.mappedStore.addAll(
			    new ConverterCloseableIterator<Atom, Atom>(new AtomFilterIterator(it), converter));
		} catch (AtomSetException e) {
			throw new KBBuilderException(e);
		}
	}
	
	public void setPriority(Priority priority) {
		this.priority = priority;
	}


	// /////////////////////////////////////////////////////////////////////////
	// OBJECT OVERRIDE METHODS
	// /////////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

}
