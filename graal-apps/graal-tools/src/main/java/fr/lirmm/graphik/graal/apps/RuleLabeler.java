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
 package fr.lirmm.graphik.graal.apps;

import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.RuleSet;
import fr.lirmm.graphik.graal.core.ruleset.LinkedListRuleSet;
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

