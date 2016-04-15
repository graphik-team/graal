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

import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.Query;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.util.stream.CloseableIterator;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public interface GraalBench extends Runnable {

	// /////////////////////////////////////////////////////////////////////////
	// DATASET METHODS
	// /////////////////////////////////////////////////////////////////////////

	CloseableIterator<Map.Entry<String, Query>> getQueries();

	CloseableIterator<Rule> getOntology();

	CloseableIterator<Map.Entry<String, AtomSet>> getInstances();

	void setParams(Query q, AtomSet it, Object o);

	@Override
	void run();

	void init();

	Iterator<Entry<String, Object>> getResults();
	
	// /////////////////////////////////////////////////////////////////////////
	// RUN PARAMETERS
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * @return
	 */
	int getNbIteration();

	/**
	 * Set the number of times the test suite will be executed
	 * 
	 * @param nbIteration
	 */
	void setNbIteration(int nbIteration);

	/**
	 * Set the file will be used for write the test suite results
	 * 
	 * @param outputFile
	 */
	void setOutputStream(OutputStream outputStream);

	/**
	 * @return
	 */
	OutputStream getOutputStream();



}
