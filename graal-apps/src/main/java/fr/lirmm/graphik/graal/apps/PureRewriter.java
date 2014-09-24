/**
 * 
 */
package fr.lirmm.graphik.graal.apps;

import java.io.File;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import fr.lirmm.graphik.graal.backward_chaining.BackwardChainer;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.ruleset.LinkedListRuleSet;
import fr.lirmm.graphik.graal.core.ruleset.RuleSet;
import fr.lirmm.graphik.graal.io.dlgp.DlgpParser;
import fr.lirmm.graphik.graal.io.dlgp.DlgpWriter;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class PureRewriter {
	
	@Parameter(names = { "-h", "--help" }, help = true)
	private boolean help;
	
	@Parameter(names = { "-f", "--dlp" }, description = "DLP rule file")
	private String ruleFile = "";
	
	@Parameter(names = { "-q", "--query"}, description = "The query to rewrite in DLP")
	private String sQuery = "";

	
	public static void main(String args[]) throws Exception {
		
		PureRewriter options = new PureRewriter();
		JCommander commander = new JCommander(options, args);

		if (options.help) {
			commander.usage();
			System.exit(0);
		}
		
		RuleSet rules = new LinkedListRuleSet();
		DlgpParser parser = new DlgpParser(new File(options.ruleFile));
		for(Object o : parser) {
			if(o instanceof Rule) {
				rules.add((Rule)o);
			}
		}
		
		ConjunctiveQuery query = DlgpParser.parseQuery(options.sQuery); 
		BackwardChainer bc = new fr.lirmm.graphik.graal.backward_chaining.PureRewriter(query, rules);
		
		DlgpWriter writer = new DlgpWriter();
		while(bc.hasNext()) {
			writer.write(bc.next());
		}
	}
}
