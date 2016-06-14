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
package fr.lirmm.graphik.graal.chase_bench.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.io.Parser;
import fr.lirmm.graphik.graal.core.DefaultConjunctiveQuery;
import fr.lirmm.graphik.util.stream.AbstractCloseableIterator;

/**
 * 
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public final class ChaseBenchQueryParser extends AbstractCloseableIterator<ConjunctiveQuery>
                                         implements Parser<ConjunctiveQuery> {
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ChaseBenchQueryParser.class);

	private Iterator<Rule> buffer;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Constructor for parsing from the given reader.
	 * 
	 * @param reader
	 */
	public ChaseBenchQueryParser(Reader reader) {
		super();
		LinkedHashSet<Rule> objects = new LinkedHashSet<Rule>();
		CommonParser parser;
		try {
			parser = new CommonParser(reader);
			parser.parse(new InputCollector(objects, null));
			reader.close();
		} catch (IOException e) {
			LOGGER.warn("Error while parsing data.", e);
		}
		buffer = objects.iterator();
	}

	/**
	 * Constructor for parsing from the standard input.
	 */
	public ChaseBenchQueryParser() {
		this(new InputStreamReader(System.in));
	}

	/**
	 * Constructor for parsing from the given file.
	 * 
	 * @param file
	 * @throws FileNotFoundException
	 */
	public ChaseBenchQueryParser(File file) throws FileNotFoundException {
		this(new FileReader(file));
	}

	/**
	 * Constructor for parsing the content of the string s as DLGP content.
	 * 
	 * @param s
	 */
	public ChaseBenchQueryParser(String s) {
		this(new StringReader(s));
	}

	/**
	 * Constructor for parsing the given InputStream.
	 * 
	 * @param in
	 */
	public ChaseBenchQueryParser(InputStream in) {
		this(new InputStreamReader(in));
	}

	@Override
	protected void finalize() throws Throwable {
		this.close();
		super.finalize();
	}

	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public boolean hasNext() {
		return buffer.hasNext();
	}

	@Override
	public ConjunctiveQuery next() {
		return ruleToQuery(buffer.next());
	}

	/**
	 * Closes the stream and releases any system resources associated with it.
	 * Closing a previously closed parser has no effect.
	 * 
	 * @throws IOException
	 */
	@Override
	public void close() {
	}
	
	private ConjunctiveQuery ruleToQuery(Rule r) {
		Atom head = r.getHead().iterator().next();
		String label = head.getPredicate().getIdentifier().toString();
		List<Term> ans = head.getTerms();
		return new DefaultConjunctiveQuery(label, r.getBody(), ans);
	}

};
