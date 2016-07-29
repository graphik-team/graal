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
package fr.lirmm.graphik.graal.bench.core;

import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.Query;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.IteratorException;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class BenchRunner {

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	private static final Logger LOGGER = LoggerFactory.getLogger(BenchRunner.class);

	private final GraalBench    BENCH;
	private final OutputStream  OUT;
	private final int           NB_ITERATION;

	private long                timeout;

	public BenchRunner(GraalBench bench, OutputStream out) {
		this(bench, out, 1, 1000);
	}

	public BenchRunner(GraalBench bench, OutputStream out, int nbIteration, long timeout) {
		super();
		this.BENCH = bench;
		this.OUT = out;
		this.NB_ITERATION = nbIteration;
		this.timeout = timeout;
	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	public GraalBench getBench() {
		return this.BENCH;
	}


	public void run(Map<String, ? extends Object> params) throws FileNotFoundException, IteratorException {
		this.BENCH.init();
		PrintStream writer = new PrintStream(OUT);
		writer.format("%s,%s,%s,%s,%s,%s\n", "iteration", "query", "data instance", "parameter number", "data name",
		    "data value");

		for (int iteration = 0; iteration < NB_ITERATION; ++iteration) {
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("### CURRENT ITERATION: ", iteration, "###");
			}
			CloseableIterator<Map.Entry<String, AtomSet>> instances = BENCH.getInstances();

			while (instances.hasNext()) {
				Map.Entry<String, AtomSet> instance = instances.next();
				if (LOGGER.isInfoEnabled()) {
					LOGGER.info("current instance: ", instance.getKey());
				}

				CloseableIterator<Map.Entry<String, Query>> queries = BENCH.getQueries();

				while (queries.hasNext()) {
					Map.Entry<String, Query> qe = queries.next();
					Query query = qe.getValue();
					if (LOGGER.isInfoEnabled()) {
						LOGGER.info("## current query: ", qe.getKey());
					}


					for (Entry<String, ? extends Object> p : params.entrySet()) {
						BENCH.setParams(query, instance.getValue(), p.getValue());
						Thread thread = new Thread(BENCH);
						thread.start();
						try {
							thread.join(timeout);
						} catch (InterruptedException e1) {

						}
						if (thread.isAlive()) {
							writer.format("%d,%s,%s,%s,%s,%s\n", iteration, qe.getKey(), instance.getKey(), p.getKey(),
							    "info",
							    "TO");
								writer.flush();
								thread.stop();
						} else {
							Iterator<Map.Entry<String, Object>> data = BENCH.getResults();

							while (data.hasNext()) {
								Map.Entry<String, Object> e = data.next();
								writer.format("%d,%s,%s,%s,%s,%s\n", iteration, qe.getKey(), instance.getKey(),
								    p.getKey(),
								    e.getKey(), e.getValue().toString());
								writer.flush();
								}
							}



					}
				}
			}
		}

		writer.close();
	}

	// /////////////////////////////////////////////////////////////////////////
	// OBJECT OVERRIDE METHODS
	// /////////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

}
