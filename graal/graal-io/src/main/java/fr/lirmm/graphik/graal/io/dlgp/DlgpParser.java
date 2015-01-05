/**
 * 
 */
package fr.lirmm.graphik.graal.io.dlgp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.IOException;

import parser.DatalogGrammar;
import parser.ParseException;
import parser.TERM_TYPE;
import parser.TermFactory;
import fr.lirmm.graphik.graal.ParseError;
import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.DefaultConjunctiveQuery;
import fr.lirmm.graphik.graal.core.DefaultRule;
import fr.lirmm.graphik.graal.core.KnowledgeBase;
import fr.lirmm.graphik.graal.core.NegativeConstraint;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.filter.AtomFilter;
import fr.lirmm.graphik.util.stream.AbstractReader;
import fr.lirmm.graphik.util.stream.ArrayBlockingStream;
import fr.lirmm.graphik.util.stream.FilterReader;

/**
 * 
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public final class DlgpParser extends AbstractReader<Object> {

	private ArrayBlockingStream<Object> buffer = new ArrayBlockingStream<Object>(
			512);

	private static class DlgpListener extends AbstractDlgpListener {

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
			Term.Type type = null;
			switch(termType) {
			case ANSWER_VARIABLE:
			case VARIABLE:
				type = Term.Type.VARIABLE;
				break;
			case CONSTANT: 
				type = Term.Type.CONSTANT;
				break;
			case FLOAT:
			case INTEGER:
			case STRING:
				type = Term.Type.LITERAL;
				break;
			}
			return new Term(term, type);
		}
	}

	private static class Producer implements Runnable {

		private Reader reader;
		private ArrayBlockingStream<Object> buffer;

		Producer(Reader reader, ArrayBlockingStream<Object> buffer) {
			this.reader = reader;
			this.buffer = buffer;
		}

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
	public DlgpParser(Reader reader) {
		this.reader = reader;
		new Thread(new Producer(reader,buffer)).start();
	}
	
	/**
	 * Constructor for parsing from the standard input.
	 */
	public DlgpParser() {
		this(new InputStreamReader(System.in));
	}
	
	/**
	 * Constructor for parsing from the given file.
	 * @param file
	 * @throws FileNotFoundException
	 */
	public DlgpParser(File file) throws FileNotFoundException {
		this(new FileReader(file));
	}

	/**
	 * Constructor for parsing the content of the string s as DLGP content.
	 * @param s
	 */
	public DlgpParser(String s) {
		this(new StringReader(s));
	}
	
	/**
	 * Constructor for parsing the given InputStream.
	 * @param in
	 */
	public DlgpParser(InputStream in) {
		this(new InputStreamReader(in));
	}

	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////

	public boolean hasNext() {
		return buffer.hasNext();
	}

	public Object next() {
		return buffer.next();
	}
	
	/**
	 * Closes the stream and releases any system resources associated with it.
	 * Closing a previously closed parser has no effect.
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {
		if(this.reader != null) {
			this.reader.close();
			this.reader = null;
		}
	}

	// /////////////////////////////////////////////////////////////////////////
	// STATIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	public static DefaultConjunctiveQuery parseQuery(String s) {
		return (DefaultConjunctiveQuery) new DlgpParser(s).next();
	}

	public static Atom parseAtom(String s) {
		return (Atom) new DlgpParser(s).next();
	}
	
	public static Iterable<Atom> parseAtomSet(String s) {
		return new FilterReader<Atom, Object>(new DlgpParser(s), new AtomFilter());
	}
	
	public static Rule parseRule(String s) {
		return (Rule) new DlgpParser(s).next();
	}
	
	public static NegativeConstraint parseNegativeConstraint(String s) {
		return (NegativeConstraint) new DlgpParser(s).next();
	}
	
	/**
	 * Parse a DLP content and store data into the KnowledgeBase target.
	 * 
	 * @param src
	 * @param target
	 */
	public static void parseKnowledgeBase(Reader src, KnowledgeBase target) {
		DlgpParser parser = new DlgpParser(src);

		for (Object o : parser) {
			if (o instanceof Rule) {
				target.getRuleSet().add((Rule) o);
			} else if (o instanceof Atom) {
				target.getAtomSet().add((Atom) o);
			}
		}
	}

};
