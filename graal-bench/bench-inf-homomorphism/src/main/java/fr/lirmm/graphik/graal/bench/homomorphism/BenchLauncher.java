/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2016)
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
package fr.lirmm.graphik.graal.bench.homomorphism;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.TreeMap;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.bench.core.BenchRunner;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class BenchLauncher {

	@Parameter(names = { "-h", "--help" }, description = "Print this message", help = true)
	private boolean            help;

	// @Parameter(names = { "-v", "--verbose" }, description =
	// "Enable verbose mode")
	// private boolean verbose = false;

	@Parameter(names = { "-V", "--version" }, description = "Print version information")
	private boolean            version        = false;

	@Parameter(names = { "-u", "--nb-univ" }, description = "Sets the number of universities")
	private int                nbUniv         = 10;

	@Parameter(names = { "-m", "--mode" }, description = "SAT|UCQ|SEMI|INF")
	private String             mode           = "UCQ";

	@Parameter(names = { "--db" }, description = "MEM|SQL")
	private String             db             = "MEM";

	@Parameter(names = { "-o", "--output-file" }, description = "Output file (use '-' for stdout)")
	private String             outputFilePath = "-";

	@Parameter(names = { "-d", "--data-dir" }, description = "")
	private String             dataDir        = "./src/main/resources/data";

	@Parameter(names = { "-t", "--timeout" }, description = "Timeout in ms")
	private long               timeout        = 600000;

	// @Parameter(names = { "-d", "--domain-min-size" }, description =
	// "Min domain size")
	// private int domainSize = 32;
	//
	// @Parameter(names = { "--dof", "--domain-factor" }, description =
	// "Domain increase factor")
	// private float domainIncreaseFactor = 1f;
	//
	// @Parameter(names = { "--daf", "--data-factor" }, description =
	// "Data increase factor")
	// private float dataIncreaseFactor = 2f;
	//
	// @Parameter(names = { "-m", "--data-min-size" }, description =
	// "Min data size")
	// private int minSize = 50;
	//
	// @Parameter(names = { "-M", "--data-max-size" }, description =
	// "Max data size")
	// private int maxSize = 51200;
	//
	// @Parameter(names = { "-r", "--nb-repeat" }, description =
	// "Number of bench repeats")
	// private int nbRepeat = 10;

	public static final String PROGRAM_NAME   = "bench-homo";

	public static void main(String args[]) throws HomomorphismException, FileNotFoundException {

		BenchLauncher options = new BenchLauncher();

		JCommander commander = new JCommander(options, args);

		if (options.help) {
			commander.usage();
			System.exit(0);
		}

		OutputStream outputStream = null;
		if (options.outputFilePath.equals("-")) {
			outputStream = System.out;
		} else {
			try {
				outputStream = new FileOutputStream(options.outputFilePath);
			} catch (Exception e) {
				System.err.println("Could not open file: " + options.outputFilePath);
				System.err.println(e);
				e.printStackTrace();
				System.exit(1);
			}
		}

		TreeMap<String, String> params = new TreeMap<String, String>();
		params.put("fix", "me");



		InfHomomorphismBench bench = new InfHomomorphismBench(options.dataDir);
		bench.setOutputStream(outputStream);
		bench.setMode(options.mode);
		bench.setDb(options.db);
		bench.setNbUniv(options.nbUniv);
//		bench.setOutputStream(outputStream);
//		bench.setNbIteration(options.nbRepeat);
//		bench.setInstanceIncreaseFactor(options.dataIncreaseFactor);
//		bench.setMinInstanceSize(options.minSize);
//		bench.setMaxInstanceSize(options.maxSize);
//		bench.setDomainSize(options.domainSize);
//		bench.setDomainIncreaseFactor(options.domainIncreaseFactor);
		new BenchRunner(bench, outputStream, 1, options.timeout).run(params);
	}

}
