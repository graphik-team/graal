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
import fr.lirmm.graphik.graal.ParseError;
import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.DefaultConjunctiveQuery;
import fr.lirmm.graphik.graal.core.DefaultRule;
import fr.lirmm.graphik.graal.core.KnowledgeBase;
import fr.lirmm.graphik.graal.core.NegativeConstraint;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
import fr.lirmm.graphik.graal.core.filter.AtomFilter;
import fr.lirmm.graphik.graal.io.Parser;
import fr.lirmm.graphik.util.stream.ArrayBlockingStream;
import fr.lirmm.graphik.util.stream.FilterIterator;

/**
 * 
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public final class DlpParser extends Parser {
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(DlpParser.class);

	private ArrayBlockingStream<Object> buffer = new ArrayBlockingStream<Object>(
			512);

	private static class DlgpListener extends AbstractDlpListener {

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
	public DlpParser(Reader reader) {
		this.reader = reader;
		new Thread(new Producer(reader,buffer)).start();
	}
	
	/**
	 * Constructor for parsing from the standard input.
	 */
	public DlpParser() {
		this(new InputStreamReader(System.in));
	}
	
	/**
	 * Constructor for parsing from the given file.
	 * @param file
	 * @throws FileNotFoundException
	 */
	public DlpParser(File file) throws FileNotFoundException {
		this(new FileReader(file));
	}

	/**
	 * Constructor for parsing the content of the string s as DLGP content.
	 * @param s
	 */
	public DlpParser(String s) {
		this(new StringReader(s));
	}
	
	/**
	 * Constructor for parsing the given InputStream.
	 * @param in
	 */
	public DlpParser(InputStream in) {
		this(new InputStreamReader(in));
	}
	
	protected void finalize() throws Throwable {
		this.close();
		super.finalize();
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
		return (DefaultConjunctiveQuery) new DlpParser(s).next();
	}

	public static Atom parseAtom(String s) {
		return (Atom) new DlpParser(s).next();
	}
	
	public static Iterator<Atom> parseAtomSet(String s) {
		return new FilterIterator<Object, Atom>(new DlpParser(s), new AtomFilter());
	}
	
	public static Rule parseRule(String s) {
		return (Rule) new DlpParser(s).next();
	}
	
	public static NegativeConstraint parseNegativeConstraint(String s) {
		return (NegativeConstraint) new DlpParser(s).next();
	}
	
	/**
	 * Parse a DLP content and store data into the KnowledgeBase target.
	 * 
	 * @param src
	 * @param target
	 * @throws AtomSetException 
	 */
	public static void parseKnowledgeBase(Reader src, KnowledgeBase target) throws AtomSetException {
		DlpParser parser = new DlpParser(src);

		for (Object o : parser) {
			if (o instanceof Rule) {
				target.getRuleSet().add((Rule) o);
			} else if (o instanceof Atom) {
				target.getAtomSet().add((Atom) o);
			}
		}
	}

};
