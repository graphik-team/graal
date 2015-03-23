/**
 * 
 */
package fr.lirmm.graphik.graal.io.dlp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.NegativeConstraint;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.Query;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.Term.Type;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.ruleset.RuleSet;
import fr.lirmm.graphik.graal.io.ConjunctiveQueryWriter;
import fr.lirmm.graphik.graal.io.RuleWriter;
import fr.lirmm.graphik.util.DefaultURI;
import fr.lirmm.graphik.util.Prefix;
import fr.lirmm.graphik.util.URI;
import fr.lirmm.graphik.util.stream.ObjectWriter;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class Dlgp1Writer extends Writer implements ObjectWriter<Object>, ConjunctiveQueryWriter, RuleWriter {
	
	protected Writer writer;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////

	public Dlgp1Writer() {
		this.writer = new OutputStreamWriter(System.out);
	}
	
	public Dlgp1Writer(OutputStream out) {
		this.writer = new OutputStreamWriter(out);
	}
	
	public Dlgp1Writer(Writer out) {
		this.writer = out;
	}
	
	public Dlgp1Writer(File file) throws IOException {
		this(new FileWriter(file));
	}
	
	public Dlgp1Writer(String path) throws IOException {
		 this(new FileWriter(path));
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	@Override
	public void write(String str) throws IOException {
		super.write(str);
		this.flush();
	}
	
	public void writeln(String str) throws IOException {
		super.write(str);
		super.write('\n');
		this.flush();
	}
	
	@Override
	public void write(Iterable<Object> it) throws IOException {
		for(Object o: it)
			this.write(o);
	}
	
	public void writeIterable(Iterable<?> it) throws IOException {
		for(Object o: it)
			this.write(o);
	}
	
	public void write(Iterator<Object> it) throws IOException {
		while(it.hasNext())
			this.write(it.next());
	}
	
	public void writeIterator(Iterator<?> it) throws IOException {
		while(it.hasNext())
			this.write(it.next());
	}
		
	@Override
	public void write(Object o) throws IOException {
		if(o instanceof Atom) {
			this.write((Atom)o);
		} else if(o instanceof NegativeConstraint) {
			this.write((NegativeConstraint)o);
		} else if(o instanceof Rule) {
			this.write((Rule)o);
		} else if(o instanceof ConjunctiveQuery) {
			this.write((ConjunctiveQuery)o);
		} else if(o instanceof Prefix) {
			this.write((Prefix)o);
		} else if(o instanceof Iterable<?>) {
			this.writeIterable((Iterable<?>)o);
		} else if(o instanceof Iterator<?>) {
			this.writeIterator((Iterator<?>)o);
		}
	}

	public void write(AtomSet atomset) throws IOException {
		this.writeAtomSet(atomset, true);
		this.write(".");
		this.writer.flush();
	}

	public void write(RuleSet ruleset) throws IOException {
		for (Rule r : ruleset) {
			this.write(r);
		}
		this.writer.flush();
	}

	public void write(Atom atom) throws IOException{
		this.writeAtom(atom);
		this.writer.write(".\n");
		this.writer.flush();
	}
	
	/*public void write(Iterable<Atom> atoms) throws IOException {
		this.writeAtomSet(atoms, true);
		this.writer.write(".\n");
		this.writer.flush();
	}*/
	
	public void write(Rule rule) throws IOException {
		this.writeLabel(rule.getLabel());

		this.writeAtomSet(rule.getHead(), false);
		this.writer.write(" :- ");
		this.writeAtomSet(rule.getBody(), false);
		this.writer.write(".\n");
		this.writer.flush();
	}
	
	public void write(NegativeConstraint constraint) throws IOException {
		this.writeLabel(constraint.getLabel());
		
		this.writer.write(" ! :- ");
		this.writeAtomSet(constraint.getBody(), false);
		this.writer.write(".\n");
		this.writer.flush();
	}

	public void write(Query query) throws IOException {
		if (query instanceof ConjunctiveQuery) {
			this.write((ConjunctiveQuery)query);
		}
		else if (query instanceof Iterable) {
			for (Object q : (Iterable<?>)query) {
				if (q instanceof ConjunctiveQuery) {
					this.write((ConjunctiveQuery)q);
				}
			}
		}
	}
	
	@Override
	public void write(ConjunctiveQuery query) throws IOException {
		if(!query.getLabel().isEmpty()) {
			this.writeLabel(query.getLabel());
		}
		this.writer.write('?');
		Collection<Term> avars = query.getAnswerVariables();
		if(!avars.isEmpty()) {
			boolean isFirst = true;
			this.writer.write('(');
			for(Term t: avars) {
				if(isFirst) {
					isFirst = false;
				} else {
					this.writer.write(',');
				} 
				
				this.writeTerm(t);
			}
			this.writer.write(')');
		}
		this.writer.write(" :- ");
		this.writeAtomSet(query.getAtomSet(), false);
		this.writer.write(".\n");
		this.writer.flush();
	}
	
	public void write(Prefix prefix) throws IOException {
		this.writer.write("%prefix ");
		this.writer.write(prefix.getPrefixName());
		this.writer.write(" <");
		this.writer.write(prefix.getPrefix());
		this.writer.write(">\n");
	}

	// /////////////////////////////////////////////////////////////////////////
	// OVERRIDE METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public void close() throws IOException {
		this.writer.close();
	}

	@Override
	public void flush() throws IOException {
		this.writer.flush();
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		this.writer.write(cbuf, off, len);
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	protected void writeLabel(String label) throws IOException {
		if(!label.isEmpty()) {
			this.write("[");
			this.write(label);
			this.write("] ");
		}
	}
	
	protected void writeAtomSet(Iterable<Atom> atomSet, boolean addCarriageReturn) throws IOException {
		boolean isFirst = true;
		for(Atom a : atomSet) {
			if(isFirst) {
				isFirst = false;
			} else {
				this.writer.write(", ");
				if(addCarriageReturn)
					this.writer.write('\n');
			}
			
			this.writeAtom(a);
		}
	}
	
	protected void writeAtom(Atom atom) throws IOException {
		this.writePredicate(atom.getPredicate());
		this.writer.write('(');
		
		boolean isFirst = true;
		for(Term t : atom.getTerms()) {
			if(isFirst) {
				isFirst = false;
			} else {
				this.writer.write(", ");
			}
			
			this.writeTerm(t);
			
			
		}
		this.writer.write(')');
	}

	protected void writeTerm(Term t) throws IOException {
		URI uri = new DefaultURI(t.getIdentifier());
		String s = uri.getLocalname();
		char first = s.charAt(0);
		
		if(Type.VARIABLE.equals(t.getType())) {
			if(first < 'A' || first > 'Z') {
				this.writer.write("VAR_");
			}
			this.writer.write(s);
		} else if(Type.CONSTANT.equals(t.getType())) {
			if(first < 'a' || first > 'z') {
				this.writer.write("cst_");
			}
			this.writer.write(s);
		} else {
			this.writer.write('"');
			this.writer.write(uri.toString());
			this.writer.write('"');
		}
		
	}
	
	protected void writePredicate(Predicate p) throws IOException {
		String s = p.getIdentifier();
		URI uri = new DefaultURI(p.getIdentifier());
		if(uri.getPrefix().equals(Prefix.DEFAULT)) {
			s = uri.getLocalname();
		}
		
		char first = s.charAt(0);
		if(onlyValidChar(s) && first > 'a' && first < 'z' ) {
			this.writer.write(s);
		} else {
			this.writer.write('"');
			this.writer.write(s);
			this.writer.write('"');
		}
	}
	
	////////////////////////////////////////////////////////////////////////////
	// STATIC METHODS
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Check if the string contains only simple char (a-z A-Z 0-9 -)
	 * @param s
	 * @return
	 */
	private static boolean onlyValidChar(String s) {
		char c;
		for(int i = 0; i < s.length(); ++i) {
			c = s.charAt(i);
			if( !(c >= 'a' && c <= 'z')  &&
					!(c >= 'A' && c <= 'Z') && 
					!(c >= '0' && c <= '9') &&
					!(c == '-')  ) {
				return false;
			}
		}
		return true;
	}
	
	public static String writeToString(Object o) {
		StringWriter s = new StringWriter();
		Dlgp1Writer w = new Dlgp1Writer(s);
		try {
			w.write(o);
			w.close();
		} catch (IOException e) {
			
		}
		return s.toString();
	}
	
	
};
