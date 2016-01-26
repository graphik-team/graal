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
package fr.lirmm.graphik.graal.bench.homomorphism;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.TreeMap;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import fr.lirmm.graphik.graal.api.homomorphism.Homomorphism;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.homomorphism.BacktrackHomomorphism;
import fr.lirmm.graphik.graal.homomorphism.forward_checking.NFC2;
import fr.lirmm.graphik.graal.homomorphism.forward_checking.NFC2WithLimit;
import fr.lirmm.graphik.graal.homomorphism.forward_checking.SimpleFC;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class BacktrackHomomorphismBench {

	@Parameter(names = { "-h", "--help" }, description = "Print this message", help = true)
	private boolean            help;

	@Parameter(names = { "-v", "--verbose" }, description = "Enable verbose mode")
	private boolean            verbose      = false;

	@Parameter(names = { "-V", "--version" }, description = "Print version information")
	private boolean            version      = false;

	@Parameter(names = { "-o", "--output-file" }, description = "Output file (use '-' for stdout)")
	private String             outputFilePath = "-";

	@Parameter(names = { "-s", "--max-size" }, description = "Max data size")
	private int                maxSize        = 51200;

	@Parameter(names = { "-r", "--nb-repeat" }, description = "Number of bench repeats")
	private int                nbRepeat       = 10;

	public static final String PROGRAM_NAME = "bench-homo";

	public static void main(String args[]) throws HomomorphismException, FileNotFoundException {

		BacktrackHomomorphismBench options = new BacktrackHomomorphismBench();

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

		TreeMap<String, Homomorphism> params = new TreeMap<String, Homomorphism>();

		params.put("Backtrack", new BacktrackHomomorphism());
		params.put("simpleNFC", new BacktrackHomomorphism(new SimpleFC()));
		params.put("NFC2", new BacktrackHomomorphism(new NFC2()));
		params.put("NFC2WithLimit32", new BacktrackHomomorphism(new NFC2WithLimit(32)));
		params.put("NFC2WithLimit128", new BacktrackHomomorphism(new NFC2WithLimit(128)));

		HomomorphismBench bench = new HomomorphismBench();
		bench.setOutputStream(outputStream);
		bench.setNbIteration(options.nbRepeat);
		bench.setMaxInstanceSize(options.maxSize);
		bench.setNbIteration(10);
		bench.run(params);

	}

}
