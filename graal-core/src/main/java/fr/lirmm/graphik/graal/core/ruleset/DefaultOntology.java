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
package fr.lirmm.graphik.graal.core.ruleset;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import fr.lirmm.graphik.graal.api.core.NegativeConstraint;
import fr.lirmm.graphik.graal.api.core.Ontology;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.RuleLabeler;
import fr.lirmm.graphik.graal.api.core.RuleSet;
import fr.lirmm.graphik.graal.api.core.RuleSetException;
import fr.lirmm.graphik.graal.core.DefaultRuleLabeler;
import fr.lirmm.graphik.graal.core.stream.filter.RuleFilterIterator;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.IteratorException;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class DefaultOntology implements Ontology {

	private Map<String, Rule>               map;
	private Map<String, NegativeConstraint> constraintsMap;
	private Set<Predicate>                  vocabulary;
	private RuleLabeler                     labeler;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	public DefaultOntology() {
		this.map = new HashMap<String, Rule>();
		this.constraintsMap = new HashMap<String, NegativeConstraint>();
		this.labeler = new DefaultRuleLabeler();
		this.vocabulary = new TreeSet<Predicate>();
	}

	public DefaultOntology(RuleSet rules) {
		this();
		this.addAll(rules.iterator());
	}

	public DefaultOntology(CloseableIterator<Object> parser) throws RuleSetException {
		this();
		this.addAll(new RuleFilterIterator(parser));
		parser.close();
	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public Set<String> getRuleNames() {
		return map.keySet();
	}

	@Override
	public Rule getRule(String name) {
		return map.get(name);
	}

	@Override
	public Set<String> getNegativeConstraintNames() {
		return constraintsMap.keySet();
	}

	@Override
	public NegativeConstraint getNegativeConstraint(String name) {
		return constraintsMap.get(name);
	}

	@Override
	public boolean add(Rule rule) {
		if (rule == null) {
			return false;
		}
		if (rule.equals(this.map.get(rule.getLabel()))) {
			return false;
		}
		this.vocabulary.addAll(rule.getBody().getPredicates());
		this.vocabulary.addAll(rule.getHead().getPredicates());

		this.labeler.setLabel(rule);
		if(rule instanceof NegativeConstraint) {
			this.constraintsMap.put(rule.getLabel(), (NegativeConstraint) rule);
		}
		return this.map.put(rule.getLabel(), rule) == null;

	}

	@Override
	public boolean addAll(Iterator<Rule> ruleIterator) {
		boolean b = true;
		while (ruleIterator.hasNext()) {
			b = this.add(ruleIterator.next()) && b;
		}
		return b;
	}

	@Override
	public boolean addAll(CloseableIterator<Rule> ruleIterator) throws RuleSetException {
		boolean b = true;
		try {
			while (ruleIterator.hasNext()) {
				b = this.add(ruleIterator.next()) && b;
			}
		} catch (IteratorException e) {
			throw new RuleSetException(e);
		} finally {
			ruleIterator.close();
		}
		return b;
	}

	@Override
	public boolean remove(Rule rule) {
		boolean res = this.removeWithoutVocReset(rule);
		if(res) {
			this.resetVocabulary();
		}
		return res;
	}

	@Override
	public boolean removeAll(Iterator<Rule> ruleIterator) {
		boolean b = true;
		while (ruleIterator.hasNext()) {
			b = this.removeWithoutVocReset(ruleIterator.next()) && b;
		}
		if(b) {
			this.resetVocabulary();
		}
		return b;
	}

	@Override
	public boolean removeAll(CloseableIterator<Rule> ruleIterator) throws RuleSetException {
		boolean b = true;
		try {
			while (ruleIterator.hasNext()) {
				b = this.removeWithoutVocReset(ruleIterator.next()) && b;
			}
		} catch (IteratorException e) {
			throw new RuleSetException(e);
		} finally {
			ruleIterator.close();
		}
		if (b) {
			this.resetVocabulary();
		}
		return b;
	}

	@Override
	public boolean contains(Rule rule) {
		return this.map.containsValue(rule);
	}

	@Override
	public int size() {
		return this.map.size();
	}

	@Override
	public Iterator<Rule> iterator() {
		return this.map.values().iterator();
	}

	@Override
	public Set<Predicate> getVocabulary() {
		return Collections.<Predicate>unmodifiableSet(this.vocabulary);
	}

	@Override
	public boolean isEmpty() {
		return this.map.isEmpty();
	}

	// /////////////////////////////////////////////////////////////////////////
	// OBJECT OVERRIDE METHODS
	// /////////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * 
	 */
	private void resetVocabulary() {
		// reset vocabulary
		this.vocabulary = new TreeSet<Predicate>();
		for (Rule r : this) {
			this.vocabulary.addAll(r.getBody().getPredicates());
			this.vocabulary.addAll(r.getHead().getPredicates());
		}
	}

	private boolean removeWithoutVocReset(Rule rule) {
		if(rule instanceof NegativeConstraint) {
			this.constraintsMap.remove(rule.getLabel());
		}
		return this.map.remove(rule.getLabel()) != null;
	}

}
