/**
 * 
 */
package fr.lirmm.graphik.obda.parser.oxford;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.LinkedList;

import fr.lirmm.graphik.obda.parser.ParseException;

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
public class OxfordQueryParser {

	private LinkedList<OxfordQueryParserListener> parserListenerList = new LinkedList<OxfordQueryParserListener>();

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
	// PUBLICS METHODS
	// /////////////////////////////////////////////////////////////////////////

	public void addListener(OxfordQueryParserListener listener) {
		this.parserListenerList.add(listener);
	}

	public void parse() throws ParseException {
		for (OxfordQueryParserListener listener : parserListenerList)
			listener.startQuery();

		forward();
		readHead();
		read("<-");
		readBody();

		for (OxfordQueryParserListener listener : parserListenerList)
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
		for (OxfordQueryParserListener listener : parserListenerList)
			listener.startBody();

		readAtom();
		if (test(',')) {
			forward();
			readBody();
		}
	}

	private void readAtom() throws ParseException {
		for (OxfordQueryParserListener listener : parserListenerList)
			listener.startAtom();

		readPredicate();
		read('(');
		readTerms();
		read(')');

		for (OxfordQueryParserListener listener : parserListenerList)
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

		for (OxfordQueryParserListener listener : parserListenerList)
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

		while ((c >= 'A' && c <= 'Z') | (c >= 'a' && c <= 'z')) {
			term.append(c);
			forward();
		}

		for (OxfordQueryParserListener listener : parserListenerList)
			listener.constant(term.toString());
	}

	private void readTermVar() throws ParseException {
		StringBuilder term = new StringBuilder();

		read('?');
		while (c >= '0' && c <= '9') {
			term.append(c);
			forward();
		}

		for (OxfordQueryParserListener listener : parserListenerList)
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

	private boolean isEOF() {
		return (int) c == -1;
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
