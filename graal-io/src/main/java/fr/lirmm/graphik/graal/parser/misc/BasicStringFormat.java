package fr.lirmm.graphik.graal.parser.misc;

import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.factory.AtomSetFactory;
import fr.lirmm.graphik.util.stream.ObjectReader;

/**
 * Representation of Atoms like predicat(a,b)
 * 
 * @deprecated use {@link fr.lirmm.graphik.graal.io.basic.BasicParser} instead
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class BasicStringFormat implements StringFormat {
	
    private static final long serialVersionUID = 6227663087766130234L;
    private static final String TERM_SEPARATOR = ",";
	private static final String ATOM_SEPARATOR = ".";

	public String toString(Atom atom) {
		StringBuilder string = new StringBuilder(atom.getPredicate().getLabel());
		string.append('(');
		append(string, atom.getTerms(), TERM_SEPARATOR);
		string.append(')');
					
		return string.toString();
	}
	
	public Atom parseAtom(String s) {
		try {
			return parseAtom(s.toCharArray());
		} catch (Exception e) {
			// TODO
			//throw new ParseException(e.getMessage(), e);
			return null;
		}
	}
	
	/**
	 * TODO return Iterator<Atom>
	 */
	public AtomSet parse(String s) {
	    AtomSet atomSet = AtomSetFactory.getInstance().createAtomSet();
	    for(Atom atom : new StringAtomReader(s, this))
	        atomSet.add(atom);
	    
	    return atomSet;
	}
	
	/**
	 * Reads an atom and creates an Atom object with the string information.
	 */
	public Atom parseAtom(char[] atomCharArray) throws Exception {
		
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
	
	private static void append(StringBuilder stringBuilder, Iterable<Term> objects, String separator) {
		Iterator<Term> it = objects.iterator();
		if(it.hasNext()) {
			stringBuilder.append(it.next());
			while(it.hasNext()) {
				stringBuilder.append(TERM_SEPARATOR);
				stringBuilder.append(it.next());
			}
		}
	}
	
	public Atom readAtom(Reader reader) throws IOException {
		Atom atom = null;
		List<Term> terms = new LinkedList<Term>();
		
		this.skipSeparator(reader);
		
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

	private void skipSeparator(Reader reader) throws IOException {
		reader.mark(1);
		int c = reader.read();
		while ( c != 1 && (c == ' ' || c == '.')) {
			reader.mark(1);
			c = reader.read();
		}
		reader.reset();
	}

	public ObjectReader<Atom> parse(Reader reader) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getAtomSeparator() {
		return ATOM_SEPARATOR;
	}
	

	
	
}
