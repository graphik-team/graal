/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2015)
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
package fr.lirmm.graphik.graal.examples;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.ruleset.RuleSet;
import fr.lirmm.graphik.graal.api.io.AbstractGraalWriter;
import fr.lirmm.graphik.graal.api.io.ConjunctiveQueryWriter;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.ruleset.LinkedListRuleSet;
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

		for (Prefix p : PrefixManager.instance()) {
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

		w = new DlgpWriter();

		for (Prefix p : PrefixManager.instance()) {
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
		for (Prefix p : PrefixManager.instance()) {
			qw.write(p);
		}
		for (ConjunctiveQuery cq : queries)
			qw.write(cq);
		
		qw.flush();

	}
}
