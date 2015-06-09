/* Graal v0.7.4
 * Copyright (c) 2014-2015 Inria Sophia Antipolis - Méditerranée / LIRMM (Université de Montpellier & CNRS)
 * All rights reserved.
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * Author(s): Clément SIPIETER
 *            Mélanie KÖNIG
 *            Swan ROCHER
 *            Jean-François BAGET
 *            Michel LECLÈRE
 *            Marie-Laure MUGNIER
 */
 /**
 * 
 */
package fr.lirmm.graphik.graal.io.dlp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import parser.DatalogGrammar;
import parser.ParseException;
import parser.TERM_TYPE;
import parser.TermFactory;
import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.DefaultConjunctiveQuery;
import fr.lirmm.graphik.graal.core.DefaultRule;
import fr.lirmm.graphik.graal.core.KnowledgeBase;
import fr.lirmm.graphik.graal.core.NegativeConstraint;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
import fr.lirmm.graphik.graal.core.filter.AtomFilterIterator;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.core.term.Term;
import fr.lirmm.graphik.graal.io.AbstractParser;
import fr.lirmm.graphik.graal.io.ParseError;
import fr.lirmm.graphik.util.stream.ArrayBlockingStream;

/**
 * 
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public final class Dlgp1Parser extends AbstractParser<Object> {
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(Dlgp1Parser.class);

	private ArrayBlockingStream<Object> buffer = new ArrayBlockingStream<Object>(
			512);

	private static class DlgpListener extends AbstractDlgp1Listener {

		private ArrayBlockingStream<Object> set;

		DlgpListener(ArrayBlockingStream<Object> buffer) {
			this.set = buffer;
		}

		@Override
		protected void createAtom(DefaultAtom atom) {
			this.set.write(atom);
		}

		@Override
		protected void createQuery(DefaultConjunctiveQuery query) {
			this.set.write(query);
		}

		@Override
		protected void createRule(DefaultRule rule) {
			this.set.write(rule);
		}

		@Override
		protected void createNegConstraint(NegativeConstraint negativeConstraint) {
			this.set.write(negativeConstraint);
		}
	};

	private static class InternalTermFactory implements TermFactory {

		@Override
		public Term createTerm(TERM_TYPE termType, Object term) {
			switch(termType) {
			case ANSWER_VARIABLE:
			case VARIABLE:
				return DefaultTermFactory.instance().createVariable(
						(String) term);
			case CONSTANT: 
				return DefaultTermFactory.instance().createConstant(
						(String) term);
			case FLOAT:
			case INTEGER:
			case STRING:
				return DefaultTermFactory.instance().createLiteral(term);
			}
			return null;
		}
	}

	private static class Producer implements Runnable {

		private Reader reader;
		private ArrayBlockingStream<Object> buffer;

		Producer(Reader reader, ArrayBlockingStream<Object> buffer) {
			this.reader = reader;
			this.buffer = buffer;
		}

		@Override
		public void run() {
			DatalogGrammar dlpGrammar = new DatalogGrammar(
					new InternalTermFactory(), reader);
			dlpGrammar.addParserListener(new DlgpListener(buffer));
			try {
				dlpGrammar.document();
			} catch (ParseException e) {
				throw new ParseError("An error occured while parsing", e);
			} finally {
				buffer.close();
			}
		}
	}

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////
	
	private Reader reader = null;

	/**
	 * Constructor for parsing from the given reader.
	 * @param reader
	 */
	public Dlgp1Parser(Reader reader) {
		this.reader = reader;
		new Thread(new Producer(reader,buffer)).start();
	}
	
	/**
	 * Constructor for parsing from the standard input.
	 */
	public Dlgp1Parser() {
		this(new InputStreamReader(System.in));
	}
	
	/**
	 * Constructor for parsing from the given file.
	 * @param file
	 * @throws FileNotFoundException
	 */
	public Dlgp1Parser(File file) throws FileNotFoundException {
		this(new FileReader(file));
	}

	/**
	 * Constructor for parsing the content of the string s as DLGP content.
	 * @param s
	 */
	public Dlgp1Parser(String s) {
		this(new StringReader(s));
	}
	
	/**
	 * Constructor for parsing the given InputStream.
	 * @param in
	 */
	public Dlgp1Parser(InputStream in) {
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
	public Object next() {
		return buffer.next();
	}
	
	/**
	 * Closes the stream and releases any system resources associated with it.
	 * Closing a previously closed parser has no effect.
	 * 
	 * @throws IOException
	 */
	@Override
	public void close() {
		if(this.reader != null) {
			try {
				this.reader.close();
			} catch (IOException e) {
				LOGGER.error("Error during closing reader", e);
			}
			this.reader = null;
		}
	}

	// /////////////////////////////////////////////////////////////////////////
	// STATIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	public static DefaultConjunctiveQuery parseQuery(String s) {
		return (DefaultConjunctiveQuery) new Dlgp1Parser(s).next();
	}

	public static Atom parseAtom(String s) {
		return (Atom) new Dlgp1Parser(s).next();
	}
	
	public static Iterator<Atom> parseAtomSet(String s) {
		return new AtomFilterIterator(new Dlgp1Parser(s));
	}
	
	public static Rule parseRule(String s) {
		return (Rule) new Dlgp1Parser(s).next();
	}
	
	public static NegativeConstraint parseNegativeConstraint(String s) {
		return (NegativeConstraint) new Dlgp1Parser(s).next();
	}
	
	/**
	 * Parse a DLP content and store data into the KnowledgeBase target.
	 * 
	 * @param src
	 * @param target
	 * @throws AtomSetException 
	 */
	public static void parseKnowledgeBase(Reader src, KnowledgeBase target) throws AtomSetException {
		Dlgp1Parser parser = new Dlgp1Parser(src);

		for (Object o : parser) {
			if (o instanceof Rule) {
				target.getRuleSet().add((Rule) o);
			} else if (o instanceof Atom) {
				target.getAtomSet().add((Atom) o);
			}
		}
	}

};
