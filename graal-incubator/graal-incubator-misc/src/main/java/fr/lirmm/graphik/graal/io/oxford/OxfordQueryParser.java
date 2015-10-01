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
 /**
 * 
 */
package fr.lirmm.graphik.graal.io.oxford;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.io.ParseException;

/**
 * 
 * Read some query like below : Q(?0,?1,?2,?3) <- FinantialInstrument(?0),
 * belongsToCompany(?0,?1), Company(?1)
 * 
 * Grammar: QUERY: HEAD '<-' BODY <br/>
 * HEAD: 'Q('TERMS')' <br/>
 * BODY: ATOM | BODY ',' ATOM <br/>
 * ATOM: PREDICATE'('TERMS')' <br/>
 * PREDICATE: [a-zA-Z0-9_\-]* <br/>
 * TERMS: TERM | TERMS','TERM <br/>
 * TERM: TERM_CST | TERM_VAR <br/>
 * TERM_CST: [a-zA-Z]* <br/>
 * TERM_VAR: '?'[0-9]*
 * 
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 */
public class OxfordQueryParser  {

	private OxfordQueryParserListener listener = new OxfordQueryParserListener();

	private char c;
	private int index = -1;
	private Reader reader;
	
	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	public OxfordQueryParser(String s) {
		this(new StringReader(s));
	}

	public OxfordQueryParser(Reader reader) {
		this.reader = reader;
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// STATIC METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	public static ConjunctiveQuery parseQuery(String s) throws ParseException {
		OxfordQueryParser p = new OxfordQueryParser(s);
		p.parse();
		return p.listener.getQuery();
	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLICS METHODS
	// /////////////////////////////////////////////////////////////////////////


	public void parse() throws ParseException {
		listener.startQuery();

		forward();
		readHead();
		read("<-");
		readBody();

		listener.endOfQuery();
	}

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////
	private void readHead() throws ParseException {
		read("Q(");
		readTerms();
		read(')');
	}

	private void readBody() throws ParseException {
		listener.startBody();

		readAtom();
		if (test(',')) {
			forward();
			readBody();
		}
	}

	private void readAtom() throws ParseException {
		listener.startAtom();

		readPredicate();
		read('(');
		readTerms();
		read(')');

		listener.endOfAtom();
	}

	private void readPredicate() throws ParseException {
		StringBuilder predicate = new StringBuilder();

		while ((c >= 'A' && c <= 'Z')
			   || (c >= 'a' && c <= 'z')
			   || (c >= '0' && c <= '9')
			   || (c == '-')
			   || (c == '_')) {
			predicate.append(c);
			forward();
		}

		listener.predicate(predicate.toString());
	}

	private void readTerms() throws ParseException {
		readTerm();
		if (test(',')) {
			forward();
			readTerms();
		}
	}

	private void readTerm() throws ParseException {
		if (test('?'))
			readTermVar();
		else
			readTermCst();
	}

	private void readTermCst() throws ParseException {
		StringBuilder term = new StringBuilder();

		while ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) {
			term.append(c);
			forward();
		}

		listener.constant(term.toString());
	}

	private void readTermVar() throws ParseException {
		StringBuilder term = new StringBuilder();
		term.append('X');
		
		read('?');
		while (c >= '0' && c <= '9') {
			term.append(c);
			forward();
		}

		listener.variable(term.toString());
	}

	// /////////////////////////////////////////////////////////////////////////
	// PARSE UTILS
	// /////////////////////////////////////////////////////////////////////////

	private void error() throws ParseException {
		throw new ParseException("Parse error at postion " + index + '.');
	}

	private void forward() throws ParseException {
		try {
			do {
				c = (char) reader.read();
				++this.index;
			} while (isBlank());
		} catch (IOException e) {
			throw new ParseException("Error on reading char", e);
		}
	}

	private void read(String s) throws ParseException {
		for (char expectedChar : s.toCharArray()) {
			if (c == expectedChar)
				forward();
			else
				error();
		}
	}

	private void read(char expectedChar) throws ParseException {
		if (c == expectedChar)
			forward();
		else
			error();
	}

	private boolean test(char expectedChar) {
		return c == expectedChar;
	}

	private boolean isBlank() {
		return c == ' ' || c == '\t';
	}

}
