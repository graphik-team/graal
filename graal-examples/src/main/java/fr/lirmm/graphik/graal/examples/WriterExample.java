/**
 * 
 */
package fr.lirmm.graphik.graal.examples;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import fr.lirmm.graphik.graal.core.Query;
import fr.lirmm.graphik.graal.core.atomset.InMemoryAtomSet;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.ruleset.LinkedListRuleSet;
import fr.lirmm.graphik.graal.core.ruleset.RuleSet;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.graal.io.ruleml.RuleMLWriter;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class WriterExample {

	public static void main(String args[]) throws IOException {

		RuleSet rules = new LinkedListRuleSet();
		InMemoryAtomSet facts = new LinkedListAtomSet();
		List<Query> queries = new LinkedList<Query>();

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

		RuleMLWriter w;
		w = new RuleMLWriter(new File("/tmp/test.ruleml"));
		// w = new RuleMLWriter();

		w.writeComment("facts");
		w.write(facts);
		w.writeComment("rules");
		w.write(rules);
		w.writeComment("queries");
		w.write(queries);
		w.close();

	}
}
