package fr.lirmm.graphik.graal.io.basic;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.factory.AtomSetFactory;
import fr.lirmm.graphik.graal.parser.ParseException;
import fr.lirmm.graphik.util.stream.AbstractReader;

/**
 * 
 * @author clement
 *
 * Representation of Atoms like predicat(a,b)
 */
public class BasicParser extends AbstractReader<Atom> {
	
	private static final Logger logger = LoggerFactory.getLogger(BasicParser.class);
	
	private Reader reader;
	private Atom atom;
	
	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////

	public BasicParser(Reader reader) {
		this.reader = reader;
	}
	
	/**
     * @param string
     */
    public BasicParser(String string) {
        this.reader = new StringReader(string);
    }
    
	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////
    
    public boolean hasNext() { 
		atom = null;
		 
	 	try {
			atom = readAtom(this.reader);
			if(logger.isDebugEnabled())
				logger.debug("Write : "+atom);
		} catch (IOException e) {
			logger.error("Error during atom parsing", e);
		}
		
		return atom != null;
	}
	
	public Atom next() {
		try {
			return (atom == null)? readAtom(this.reader) : atom;		
		} catch (IOException e) {
			throw new NoSuchElementException();
		}
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// STATIC METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	public static Atom parseAtom(String s) throws ParseException {
		try {
			return parseAtom(s.toCharArray());
		} catch (Exception e) {
			throw new ParseException(e.getMessage(), e);
		}
	}
	
	/**
	 * Reads an atom and creates an Atom object with the string information.
	 */
	public static Atom parseAtom(char[] atomCharArray) throws Exception {
		
		Atom atom = null;
		List<Term> terms = new LinkedList<Term>();
		int index = -1;
		
		if (atomCharArray.length > 0) {
			StringBuilder predicateString = new StringBuilder();
			while(atomCharArray[++index] != '(')
				predicateString.append(atomCharArray[index]);
			
			StringBuilder termString = new StringBuilder();
			while(atomCharArray[index] != ')')
			{
				++index;
				while(atomCharArray[index] != ',' && atomCharArray[index] != ')')
				{
					termString.append(atomCharArray[index]);
					++index;
				}
				
				terms.add(new Term(termString.toString(), Term.Type.CONSTANT));
				termString.setLength(0);
			}
			
			atom = new DefaultAtom(new Predicate(predicateString.toString(), terms.size()), terms);
		}
		return atom;
	}
	
	/**
	 * TODO return Iterator<Atom>
	 */
	public static AtomSet parse(String s) {
	    AtomSet atomSet = AtomSetFactory.getInstance().createAtomSet();
	    for(Atom atom : new BasicParser(s))
	        atomSet.add(atom);
	    
	    return atomSet;
	}
	
	public static Atom readAtom(Reader reader) throws IOException {
		Atom atom = null;
		List<Term> terms = new LinkedList<Term>();
		
		skipSeparator(reader);
		
		int c = reader.read();
		if (c != -1) {
			StringBuilder predicateString = new StringBuilder();
			StringBuilder termString = new StringBuilder();
			Term.Type termType;
			
			// read predicate label
			while(c != '(' && c != -1) {
				predicateString.append((char)c);
				c = reader.read();
			}
			
			// read terms
			while(c != ')' && c != -1)
			{
				c = reader.read();
				if(c >= 'A' && c <= 'Z')
					termType = Term.Type.VARIABLE;
				else
				    termType = Term.Type.CONSTANT;
				
				while(c != ',' && c != ')' && c != -1)
				{
					termString.append((char)c);
					c = reader.read();
				}
				
				
				terms.add(new Term(termString.toString(), termType));
				termString.setLength(0);
			}
			
			// if correct end of atom
			if(c == ')')
				atom = new DefaultAtom(new Predicate(predicateString.toString(), terms.size()), terms);
		}
		return atom;
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

	private static void skipSeparator(Reader reader) throws IOException {
		reader.mark(1);
		int c = reader.read();
		while ( c != 1 && (c == ' ' || c == '.')) {
			reader.mark(1);
			c = reader.read();
		}
		reader.reset();
	}

}
