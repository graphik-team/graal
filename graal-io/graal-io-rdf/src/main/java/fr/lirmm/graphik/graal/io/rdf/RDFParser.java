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
 /**
 * 
 */
package fr.lirmm.graphik.graal.io.rdf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;

import org.eclipse.rdf4j.rio.ParserConfig;
import org.eclipse.rdf4j.rio.RDFFormat;

import fr.lirmm.graphik.graal.api.io.Parser;
import fr.lirmm.graphik.util.stream.AbstractCloseableIterator;
import fr.lirmm.graphik.util.stream.ArrayBlockingStream;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public final class RDFParser extends AbstractCloseableIterator<Object> implements Parser<Object> {

	private ArrayBlockingStream<Object> buffer = new ArrayBlockingStream<Object>(
			512);

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////
	
	public RDFParser(InputStream is, RDFFormat format) {
		this(new InputStreamReader(is), format, null);
	}
	
	public RDFParser(File file, RDFFormat format) throws FileNotFoundException {
		this(new FileReader(file), format, null);
	}

	public RDFParser(Reader reader, RDFFormat format) {
		this(reader, format, null);
	}
	
	public RDFParser(Reader reader, RDFFormat format, ParserConfig parserConfig) {
		new Thread(new Producer(reader, buffer, format, parserConfig)).start();
	}

	public RDFParser(URL url, RDFFormat format) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				url.openStream()));
		new Thread(new Producer(reader, buffer, format, null)).start();
	}

	public RDFParser(String s, RDFFormat format) {
		this(new StringReader(s), format);
	}

	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public boolean hasNext() {
		return buffer.hasNext();
	}

	@Override
	public Object next() {
		return buffer.next();
	}

	@Override
	public void close() {
		this.buffer.close();
	}

};