/* Graal v0.7.4
 * Copyright (c) 2014-2015 Inria Sophia Antipolis - Méditerranée / LIRMM (Université de Montpellier & CNRS)
 * All rights reserved.
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * Author(s): Clément SIPIETER
 *            Mélanie KÖNIG
 *            Swan ROCHER
 *            Jean-François BAGET
 *            Michel LECLÈRE
 *            Marie-Laure MUGNIER
 */
 /**
 * 
 */
package fr.lirmm.graphik.graal.core.factory;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.Rule;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public final class RuleFactory {

	private static RuleFactory instance = new RuleFactory();
	
	private RuleFactory() {
		super();
	}

	public static RuleFactory instance() {
		return instance;
	}

	public Rule create() {
		return new DefaultRule();
	}

	public Rule create(Iterable<Atom> body, Iterable<Atom> head) {
		return new DefaultRule(body, head);
	}

	public Rule create(String label, Iterable<Atom> body, Iterable<Atom> head) {
		return new DefaultRule(label, body, head);
	}

	public Rule create(Rule rule) {
		return new DefaultRule(rule);
	}

}