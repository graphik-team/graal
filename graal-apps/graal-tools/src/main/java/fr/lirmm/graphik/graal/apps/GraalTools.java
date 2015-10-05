package fr.lirmm.graphik.graal.apps;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.RuleSet;
import fr.lirmm.graphik.graal.core.RuleUtils;
import fr.lirmm.graphik.graal.core.ruleset.LinkedListRuleSet;
import fr.lirmm.graphik.graal.io.dlp.Directive;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.graal.io.dlp.DlgpWriter;
import fr.lirmm.graphik.util.Prefix;

public class GraalTools {
	public static final String   PROGRAM_NAME   = "ruleset-tools";

	public static void main(String args[]) throws IOException {
		GraalTools options = new GraalTools();

		JCommander commander = new JCommander(options,args);

		if (options.help) {
			commander.usage();
			System.exit(0);
		}

		// init parser
		DlgpParser parser = null;
		DlgpWriter writer = null;
		RuleSet rules = new LinkedListRuleSet();

		if (options.input_file.equals("-")) {
			if (options.verbose)
				System.err.println("Reading data from standard input...");
			parser = new DlgpParser(System.in);
		}
		else {
			try {
				if (options.verbose)
					System.err.println("Reading data from dlp file: " + options.input_file);
				parser = new DlgpParser(new FileInputStream(options.input_file));
			} catch (Exception e) {
				System.err.println("Could not open file: " + options.input_file);
				System.err.println(e);
				e.printStackTrace();
				System.exit(1);
			}
		}

		if (options.output_file.equals("-")) {
			writer = new DlgpWriter(System.out);
		} else {
			try {
				writer = new DlgpWriter(new FileOutputStream(options.output_file));
			} catch (Exception e) {
				System.err.println("Could not open file: " + options.output_file);
				System.err.println(e);
				e.printStackTrace();
				System.exit(1);
			}
		}

		// parse rule set
		while (parser.hasNext()) {
			Object o = parser.next();
			if (o instanceof Rule) {
				rules.add((Rule) o);
			} else if (o instanceof Directive) {
				writer.writeDirective((Directive) o);
			} else if (o instanceof Prefix) {
				writer.write((Prefix) o);
			} else {
				System.err.println("[WARNING] Ignoring non rule: " + o);
			}
		}

		if (options.singlepiece) {
			System.out.println("%%%% SINGLE PIECE %%%%");
			Iterator<Rule> it = RuleUtils.computeSinglePiece(rules.iterator());
			while (it.hasNext()) {
				writer.write(it.next());
			}
		}

		if (options.atomic) {
			System.out.println("%%%%% ATOMIC HEAD %%%%");
			Iterator<Rule> it = RuleUtils.computeAtomicHead(RuleUtils.computeSinglePiece(rules.iterator()));
			while (it.hasNext()) {
				writer.write(it.next());
			}
		}

		if (options.labeler) {
			if (options.verbose)
				System.err.println("Start analysing rules...");
			for (Rule r : rules) {
				r.setLabel(computeLabel(r));
				writer.write(r);
			}
			if (options.verbose)
				System.err.println("Done!");
		}

		writer.write('\n');
		writer.close();

	}

	@Parameter(names = { "-v", "--verbose" }, description = "Enable verbose mode")
	private boolean verbose = false;

	@Parameter(names = { "-l", "--labeler" }, 
	           description = "Add label on rules")
	private boolean labeler = false;

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

	private GraalTools() { }

	private static int currentRuleID = 0;

	public static String computeLabel(final Rule r) {
		return RuleLabeler.computeBaseLabel(r) + "r" + (currentRuleID++);
	}

};

