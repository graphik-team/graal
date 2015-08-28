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
package fr.lirmm.graphik.graal.examples;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.atomset.InMemoryAtomSet;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.ruleset.LinkedListRuleSet;
import fr.lirmm.graphik.graal.core.ruleset.RuleSet;
import fr.lirmm.graphik.graal.io.AbstractGraalWriter;
import fr.lirmm.graphik.graal.io.ConjunctiveQueryWriter;
import fr.lirmm.graphik.graal.io.dlp.Dlgp1Writer;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.graal.io.dlp.DlgpWriter;
import fr.lirmm.graphik.graal.io.sparql.SparqlConjunctiveQueryWriter;
import fr.lirmm.graphik.util.Prefix;
import fr.lirmm.graphik.util.PrefixManager;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class WriterExample {

	public static void main(String args[]) throws IOException {

		RuleSet rules = new LinkedListRuleSet();
		InMemoryAtomSet facts = new LinkedListAtomSet();
		List<ConjunctiveQuery> queries = new LinkedList<ConjunctiveQuery>();

		rules.add(DlgpParser
				.parseRule("sister(tomtom, X) :- girl(X), sibling(X,tomtom)."));
		rules.add(DlgpParser.parseRule("sibling(X,Y) :- sibling(Y,X)."));
		rules.add(DlgpParser
				.parseRule("father(X,Z), father(Y,Z) :- sibling(X,Y)."));
		rules.add(DlgpParser.parseRule("! :- girl(X), boy(X)."));

		facts.add(DlgpParser.parseAtom("sibling(nana,tomtom)."));
		facts.add(DlgpParser.parseAtom("girl(nana)."));

		queries.add(DlgpParser
				.parseQuery("? :- father(nana, X), father(tomtom, X)."));
		queries.add(DlgpParser.parseQuery("?(X) :- father(X,Y)."));

		AbstractGraalWriter w;
		// w = new RuleMLWriter(new File("/tmp/test.ruleml"));
		w = new DlgpWriter();

		for (Prefix p : PrefixManager.getInstance()) {
			w.write(p);
		}
		w.writeComment("facts");
		w.write(facts);
		w.writeComment("rules");
		w.write(rules);
		w.writeComment("queries");
		w.write(queries);
		w.flush();

		System.out.println("##########################");

		w = new Dlgp1Writer();

		for (Prefix p : PrefixManager.getInstance()) {
			w.write(p);
		}
		w.writeComment("facts");
		w.write(facts);
		w.writeComment("rules");
		w.write(rules);
		w.writeComment("queries");
		w.write(queries);
		w.flush();

		System.out.println("##########################");

		ConjunctiveQueryWriter qw = new SparqlConjunctiveQueryWriter();
		for (Prefix p : PrefixManager.getInstance()) {
			qw.write(p);
		}
		for (ConjunctiveQuery cq : queries)
			qw.write(cq);
		
		qw.flush();

	}
}
