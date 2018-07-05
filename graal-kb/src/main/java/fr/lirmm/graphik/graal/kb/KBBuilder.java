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
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.RuleSet;
import fr.lirmm.graphik.graal.api.core.mapper.Mapper;
import fr.lirmm.graphik.graal.api.kb.Approach;
import fr.lirmm.graphik.graal.api.kb.KnowledgeBase;
import fr.lirmm.graphik.graal.api.store.Store;
import fr.lirmm.graphik.graal.core.atomset.graph.DefaultInMemoryGraphStore;
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

	private static final String EQUALITY_PREDICATE_NOT_MANAGED_EXCEPTION = "Equality predicate is not yet managed";
	
	private Store store = new DefaultInMemoryGraphStore();
	private RuleSet ontology = new LinkedListRuleSet();
	private Approach approach;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	/**
	 * Generates a KnowledgeBase based on previous method calls on this KBBuilder.
	 * 
	 * @return a KnowledgeBase.
	 */
	public KnowledgeBase build() {
		DefaultKnowledgeBase kb = new DefaultKnowledgeBase(this.store, this.ontology);
		if(this.approach != null) {
			kb.setPriority(this.approach);
		}
		return kb;
	}

	/**
	 * Defines the store which will be used in the generate KnowledgeBase.
	 * @param store the Store to used.
	 */
	public void setStore(Store store) {
		this.store = store;
	}
	
	/**
	 * Defines rules which will be used in the generate KnowledgeBase.
	 * @param rules
	 */
	public void setOntology(RuleSet rules) {
		this.ontology = rules;
	}

	/**
	 * Loads rules and atoms form the specified CloseableIterator.
	 * @param it
	 * @throws KBBuilderException
	 */
	public void addAll(CloseableIterator<?> it) throws KBBuilderException {
		Object o;
		try {
			while (it.hasNext()) {
				o = it.next();
				if (o instanceof Rule) {
					this.add((Rule) o);
				} else if (o instanceof Atom) {
					this.store.add((Atom) o);
				}
			}
		} catch (IteratorException e) {
			throw new KBBuilderException(e);
		} catch (AtomSetException e) {
			throw new KBBuilderException(e);
		}
	}

	/**
	 * Loads the specified rule.
	 * @param rule
	 * @throws KBBuilderException 
	 */
	public void add(Rule rule) throws KBBuilderException {
		if(rule.getBody().getPredicates().contains(Predicate.EQUALITY) || rule.getHead().getPredicates().contains(Predicate.EQUALITY)) {
			throw new KBBuilderException(EQUALITY_PREDICATE_NOT_MANAGED_EXCEPTION);
		}
		this.ontology.add(rule);
	}

	/**
	 * Maps and loads the specifed rule.
	 * @param rule
	 * @param mapper
	 * @throws KBBuilderException 
	 */
	public void add(Rule rule, Mapper mapper) throws KBBuilderException {
		this.add(mapper.map(rule));
	}

	/**
	 * Loads rules from the specified CloseableIterator.
	 * 
	 * @param it
	 * @throws KBBuilderException
	 * @throws  
	 */
	public void addRules(CloseableIterator<?> it) throws KBBuilderException {
		try {
			RuleFilterIterator ruleIt = new RuleFilterIterator(it);
			while(ruleIt.hasNext()) {
				Rule next = ruleIt.next();
				this.add(next);
			}
			ruleIt.close();
		} catch (IteratorException e) {
			throw new KBBuilderException(e);
		}
	}

	/**
	 * Maps rules from the specified CloseableIterator and loads them.
	 * @param it
	 * @param mapper
	 * @throws KBBuilderException
	 */
	public void addRules(CloseableIterator<?> it, Mapper mapper) throws KBBuilderException {
		Converter<Rule, Rule> converter = new MapperRuleConverter(mapper);
		this.addRules(new ConverterCloseableIterator<Rule, Rule>(new RuleFilterIterator(it), converter));
	}

	/**
	 * Loads the specified atom.
	 * @param atom
	 * @throws KBBuilderException
	 */
	public void add(Atom atom) throws KBBuilderException {
		try {
			this.store.add(atom);
		} catch (AtomSetException e) {
			throw new KBBuilderException(e);
		}
	}

	/**
	 * Maps and loads the specified atom.
	 * @param atom
	 * @param mapper
	 * @throws KBBuilderException
	 */
	public void add(Atom atom, Mapper mapper) throws KBBuilderException {
		try {
			this.store.add(mapper.map(atom));
		} catch (AtomSetException e) {
			throw new KBBuilderException(e);
		}
	}

	/**
	 * Loads atoms from the specified CloseableIterator.
	 * @param it
	 * @throws KBBuilderException
	 */
	public void addAtoms(CloseableIterator<?> it) throws KBBuilderException {
		try {
			this.store.addAll(new AtomFilterIterator(it));
		} catch (AtomSetException e) {
			throw new KBBuilderException(e);
		}
	}

	/**
	 * Maps and loads atoms from the specified CloseableIterator.
	 * @param it
	 * @param mapper
	 * @throws KBBuilderException
	 */
	public void addAtoms(CloseableIterator<?> it, Mapper mapper) throws KBBuilderException {
		try {
			Converter<Atom, Atom> converter = new MapperAtomConverter(mapper);
			this.store.addAll(
			    new ConverterCloseableIterator<Atom, Atom>(new AtomFilterIterator(it), converter));
		} catch (AtomSetException e) {
			throw new KBBuilderException(e);
		}
	}
	
	/**
	 * Set the query answering {@link Approach approach} of this KnowledgeBase (ie saturation or
	 * rewriting).
	 * 
	 * @param the approach to be used.
	 */
	public void setApproach(Approach approach) {
		this.approach = approach;
	}


}
