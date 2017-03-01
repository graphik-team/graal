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
package fr.lirmm.graphik.graal.core.mapper;

import java.util.Iterator;

import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.RuleSet;
import fr.lirmm.graphik.graal.api.core.RuleSetException;
import fr.lirmm.graphik.graal.api.core.mapper.Mapper;
import fr.lirmm.graphik.util.ShouldNeverHappenedError;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.CloseableIteratorAdapter;
import fr.lirmm.graphik.util.stream.IteratorAdapter;
import fr.lirmm.graphik.util.stream.converter.ConverterIterator;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class MappedRuleSet implements RuleSet {

	private RuleSet ruleSet;
	private Mapper mapper;
	private MapperRuleConverter ruleUnconverter;
	private MapperRuleConverter ruleConverter;
	
	
	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////
	
	public MappedRuleSet(RuleSet ruleSet, Mapper mapper) {
		this.ruleSet = ruleSet;
		this.mapper = mapper;
		this.ruleConverter = new MapperRuleConverter(this.mapper);
		this.ruleUnconverter = new MapperRuleConverter(this.mapper.inverse());
	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public boolean add(Rule rule) {
		return ruleSet.add(mapper.map(rule));
	}

	@Override
	public boolean addAll(Iterator<Rule> ruleIterator) {
		try {
			return this.addAll(new CloseableIteratorAdapter<Rule>(ruleIterator));
		} catch (Exception e) {
			throw new ShouldNeverHappenedError(e);
		}
	}

	@Override
	public boolean addAll(CloseableIterator<Rule> ruleIterator) throws RuleSetException {
		return ruleSet.addAll(new ConverterIterator<Rule, Rule>(ruleIterator, ruleConverter));
	}

	@Override
	public boolean remove(Rule rule) {
		return ruleSet.remove(mapper.map(rule));
	}

	@Override
	public boolean removeAll(Iterator<Rule> ruleIterator) {
		try {
			return this.removeAll(new CloseableIteratorAdapter<Rule>(ruleIterator));
		} catch (Exception e) {
			throw new ShouldNeverHappenedError(e);
		}
	}

	@Override
	public boolean removeAll(CloseableIterator<Rule> ruleIterator) throws RuleSetException {
		return ruleSet.removeAll(new ConverterIterator<Rule, Rule>(ruleIterator, ruleConverter));
	}

	@Override
	public boolean contains(Rule rule) {
		return ruleSet.contains(mapper.map(rule));
	}

	@Override
	public int size() {
		return ruleSet.size();
	}

	@Override
	public Iterator<Rule> iterator() {
		return new IteratorAdapter<Rule>(new ConverterIterator<Rule, Rule>(new CloseableIteratorAdapter<Rule>(ruleSet.iterator()),
		                                                                   ruleUnconverter));
	}

	@Override
	public boolean isEmpty() {
		return ruleSet.isEmpty();
	}

	// /////////////////////////////////////////////////////////////////////////
	// OBJECT OVERRIDE METHODS
	// /////////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

}
