package fr.lirmm.graphik.graal.apps;

import java.io.FileInputStream;
import java.util.Iterator;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.RuleUtils;
import fr.lirmm.graphik.graal.core.ruleset.LinkedListRuleSet;
import fr.lirmm.graphik.graal.core.ruleset.RuleSet;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.graal.io.dlp.DlgpWriter;

public class RuleSetTools {
	public static final String   PROGRAM_NAME   = "ruleset-tools";

	public static void main(String args[]) {
		RuleSetTools options = new RuleSetTools();

		JCommander commander = new JCommander(options,args);

		if (options.help) {
			commander.usage();
			System.exit(0);
		}

		// init parser
		DlgpParser parser = null;
		RuleSet rules = new LinkedListRuleSet();

		if (options.input_file.equals("-"))
			parser = new DlgpParser(System.in);
		else {
			try { parser = new DlgpParser(new FileInputStream(options.input_file)); }
			catch (Exception e) {
				System.err.println("Could not open file: " + options.input_file);
				System.err.println(e);
				e.printStackTrace();
				System.exit(1);
			}
		}

		// parse rule set
		while (parser.hasNext()) {
			Object o = parser.next();
			if (o instanceof Rule) rules.add((Rule)o);
			else System.err.println("[WARNING] Ignoring non rule: " + o);
		}

		if (options.singlepiece) {
			System.out.println("%%%% SINGLE PIECE %%%%");
			Iterator<Rule> it = RuleUtils.computeMonoPiece(rules.iterator());
			while (it.hasNext()) {
				System.out.print(DlgpWriter.writeToString(it.next()));
			}
			System.out.println("");
		}

		if (options.atomic) {
			System.out.println("%%%%% ATOMIC HEAD %%%%");
			System.out.println("TODO");
			//for (Rule r : RuleUtils.computeAtomicHead(rules.iterator()))
				//System.out.println(DlgpWriter.writeToString(r));
			System.out.println("");
		}

	}


	@Parameter(names = { "-p", "--singlepiece-head" },
	           description = "Translate rules to singlepiece-headed rules")
	private boolean singlepiece = false;

	@Parameter(names = { "-a", "--atomic-head" },
	           description = "Translate rules to atomic-headed rules")
	private boolean atomic = false;

	@Parameter(names = { "-i", "--input-file" },
	           description = "Input file (use '-' for stdin)")
	private String input_file = "-";

	@Parameter(names = { "-o", "--output-file" },
	           description = "Output file (use '-' for stdout)")
	private String output_file = "-";

	@Parameter(names = { "-h", "--help" },
	           description = "Print this message.")
	private boolean help = false;

	private RuleSetTools() { }

};

