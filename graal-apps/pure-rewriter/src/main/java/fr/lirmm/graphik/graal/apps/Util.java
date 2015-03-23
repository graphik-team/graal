/**
 * 
 */
package fr.lirmm.graphik.graal.apps;

import java.io.File;
import java.io.FileNotFoundException;

import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.ruleset.LinkedListRuleSet;
import fr.lirmm.graphik.graal.core.ruleset.RuleSet;
import fr.lirmm.graphik.graal.io.dlp.Dlgp1Parser;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
class Util {
	
	private Util(){};
	
	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

	static RuleSet parseOntology(String ontologyFile)
			throws FileNotFoundException {
		RuleSet rules = new LinkedListRuleSet();
		Dlgp1Parser parser = new Dlgp1Parser(new File(ontologyFile));
		for (Object o : parser) {
			if (o instanceof Rule) {
				rules.add((Rule) o);
			}
		}
		return rules;
	}

}
