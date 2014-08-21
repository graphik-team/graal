/**
 * 
 */
package fr.lirmm.graphik.graal.io.dlgp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collection;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.Query;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.NegativeConstraint;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.Term.Type;
import fr.lirmm.graphik.graal.core.atomset.ReadOnlyAtomSet;
import fr.lirmm.graphik.graal.writer.AtomWriter;
import fr.lirmm.graphik.graal.writer.ConjunctiveQueryWriter;
import fr.lirmm.graphik.util.stream.ObjectWriter;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class DlgpWriter extends Writer implements ObjectWriter<Object>,ConjunctiveQueryWriter {

	protected Writer writer;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////

	public DlgpWriter() {
		this.writer = new OutputStreamWriter(System.out);
	}
	
	public DlgpWriter(OutputStream out) {
		this.writer = new OutputStreamWriter(out);
	}
	
	public DlgpWriter(Writer out) {
		this.writer = out;
	}
	
	public DlgpWriter(File file) throws IOException {
		this(new FileWriter(file));
	}
	
	public DlgpWriter(String path) throws IOException {
		 this(new FileWriter(path));
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	@Override
	public void write(Iterable<Object> it) throws IOException {
		for(Object o: it)
			this.write(o);
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
		}
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
			for (Object q : (Iterable)query) {
				if (q instanceof ConjunctiveQuery) {
					this.write((ConjunctiveQuery)q);
				}
			}
		}
	}
	
	@Override
	public void write(ConjunctiveQuery query) throws IOException {	
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
	
	// /////////////////////////////////////////////////////////////////////////
	// OVERRIDE METHODS
	// /////////////////////////////////////////////////////////////////////////

	/* (non-Javadoc)
	 * @see java.io.Writer#close()
	 */
	@Override
	public void close() throws IOException {
		this.writer.close();
	}

	/* (non-Javadoc)
	 * @see java.io.Writer#flush()
	 */
	@Override
	public void flush() throws IOException {
		this.writer.flush();
	}

	/* (non-Javadoc)
	 * @see java.io.Writer#write(char[], int, int)
	 */
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
		String term = t.toString();
		if(Type.VARIABLE.equals(t.getType())) {
			if (term.charAt(0) < 65 || term.charAt(0) > 90) {
				this.writer.write("VAR_");
			}
			this.writer.write(term);
		} else if(Type.CONSTANT.equals(t.getType())) {
			if (term.charAt(0) < 97 || term.charAt(0) > 122) {
				this.writer.write("cst_");
			}
			this.writer.write(term);
		} else {
			this.writer.write('"');
			this.writer.write(t.toString());
			this.writer.write('"');
		}
	}
	
	protected void writePredicate(Predicate p) throws IOException {
		String s = p.getLabel();
		if(s.charAt(0) != '"') {
			this.writer.write('"');
		}

		this.writer.write(s);
		if(s.charAt(0) != '"') {
			this.writer.write('"');
		}
	}
	
};
