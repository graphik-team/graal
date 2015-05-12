package fr.lirmm.graphik.graal.apps;

import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.Substitution;
import fr.lirmm.graphik.graal.core.term.Term;
import fr.lirmm.graphik.graal.core.ruleset.LinkedListRuleSet;
import fr.lirmm.graphik.graal.core.ruleset.RuleSet;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.graal.io.dlp.DlgpWriter;

public class RuleLabeler {

	
	public static void main(String args[]) {
		RuleLabeler options = new RuleLabeler();

		JCommander commander = new JCommander(options,args);

		if (options.help) {
			commander.usage();
			System.exit(0);
		}

		RuleSet rules = new LinkedListRuleSet();

		try {

			if (options.input_file != "") {
				if (options.verbose)
					System.err.println("Reading data from dlp file: " + options.input_file);
				Reader reader;
				if (options.input_file.equals("-")) reader = new InputStreamReader(System.in);
				else reader = new FileReader(options.input_file);

				DlgpParser parser = new DlgpParser(reader);

				for (Object o : parser) {
					if (o instanceof Rule)
						rules.add((Rule)o);
					else if (options.verbose)
						System.err.println("Ignoring non rule object: " + o);
				}
				if (options.verbose)
					System.err.println("Done!");
			}

			if (options.verbose)
				System.err.println("Start analysing rules...");
			DlgpWriter writer = new DlgpWriter(System.out);
			for (Rule r : rules) {
				r.setLabel(computeLabel(r));
				writer.write(r);
			}
			writer.close();
			if (options.verbose)
				System.err.println("Done!");

		}

		catch (Exception e) {
			System.err.println("Something went wrong: " + e);
			e.printStackTrace();
			System.exit(1);
		}
	}

	private static int currentRuleID = 0;
	public static String computeLabel(final Rule r) {
		return RuleUtils.INSTANCE.computeBaseLabel(r) + "r" + (currentRuleID++);
	}

	@Parameter(names = {"-v","--verbose"}, description = "Enable verbose mode")
	private boolean verbose = false;

	@Parameter(names = {"-h","--help"}, description = "Print this message")
	private boolean help = false;

	//@Parameter(names = {"-p","--pieces"}, description = "Convert all rules to single-piece headed rules before analysing")
	//private boolean to_single_piece = false;

	@Parameter(names = {"-f","--file"}, description = "Input file path (dlgp)")
	private String input_file = "-";

};

