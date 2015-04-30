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
import fr.lirmm.graphik.graal.io.AbstractGraalWriter;
import fr.lirmm.graphik.util.Prefix;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class DlgpWriter extends AbstractGraalWriter {
	
	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Write into the standard output. Warning, if you close this object, you
	 * will close the standard output.
	 */
	public DlgpWriter() {
		this(new OutputStreamWriter(System.out));
	}
	
	public DlgpWriter(OutputStream out) {
		this(new OutputStreamWriter(out));
	}
	
	public DlgpWriter(Writer out) {
		super(out);
	}
	
	public DlgpWriter(File file) throws IOException {
		this(new FileWriter(file));
	}
	
	/**
	 * Write into a file specified by the path file.
	 * 
	 * @param path
	 *            the file path
	 * @throws IOException
	 */
	public DlgpWriter(String path) throws IOException {
		 this(new FileWriter(path));
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	@Override
	public void writeComment(String comment) throws IOException {
		this.write("% ");
		this.writeln(comment);
	}

	@Override
	public void write(AtomSet atomset) throws IOException {
		this.writeAtomSet(atomset, true);
		this.writeln(".");
	}

	public void write(RuleSet ruleset) throws IOException {
		for (Rule r : ruleset) {
			this.write(r);
		}
	}
	
	@Override
	public void write(Rule rule) throws IOException {
		this.writeLabel(rule.getLabel());

		this.writeAtomSet(rule.getHead(), false);
		this.write(" :- ");
		this.writeAtomSet(rule.getBody(), false);
		this.write(".\n");
	}
	
	public void write(NegativeConstraint constraint) throws IOException {
		this.writeLabel(constraint.getLabel());
		
		this.write(" ! :- ");
		this.writeAtomSet(constraint.getBody(), false);
		this.write(".\n");
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
		this.write('?');
		Collection<Term> avars = query.getAnswerVariables();
		if(!avars.isEmpty()) {
			boolean isFirst = true;
			this.write('(');
			for(Term t: avars) {
				if(isFirst) {
					isFirst = false;
				} else {
					this.write(',');
				} 
				
				this.writeTerm(t);
			}
			this.write(')');
		}
		this.write(" :- ");
		this.writeAtomSet(query.getAtomSet(), false);
		this.write(".\n");
	}
	
	@Override
	public void write(Prefix prefix) throws IOException {
		this.write("@prefix ");
		this.write(prefix.getPrefixName());
		this.write(": <");
		this.write(prefix.getPrefix());
		this.write(">\n");
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
				this.write(", ");
				if(addCarriageReturn)
					this.write('\n');
			}
			
			this.write(a);
		}
	}
	
	@Override
	protected void writeAtom(Atom atom) throws IOException {
		this.writePredicate(atom.getPredicate());
		this.write('(');
		
		boolean isFirst = true;
		for(Term t : atom.getTerms()) {
			if(isFirst) {
				isFirst = false;
			} else {
				this.write(", ");
			}
			
			this.writeTerm(t);
			
			
		}
		this.write(')');
	}

	@Override
	protected void writeEquality(Term term, Term term2) throws IOException {
		this.writeTerm(term);
		this.write(" = ");
		this.writeTerm(term2);
	}

	@Override
	protected void writeBottom() throws IOException {
		this.write("!");
	}

	protected void writeTerm(Term t) throws IOException {
		if(Type.VARIABLE.equals(t.getType())) {
			if(!Character.isUpperCase(t.getIdentifier().charAt(0))) {
				this.write("VAR_");
			}
			this.write(t.getIdentifier());
		} else if(Type.CONSTANT.equals(t.getType())) {
			this.write('<');
			this.write(t.getIdentifier());
			this.write('>');
		} else { // LITERAL
			this.write('"');
			this.write(t.getIdentifier());
			this.write('"');
		}
	}
	
	protected void writePredicate(Predicate p) throws IOException {
		this.write('<');
		this.write(p.getIdentifier());
		this.write('>');
	}
	
	////////////////////////////////////////////////////////////////////////////
	// STATIC METHODS this.writeln("PREFIX : <" + rdfPrefix + '>');

	////////////////////////////////////////////////////////////////////////////

	public static String writeToString(Object o) {
		StringWriter s = new StringWriter();
		DlgpWriter w = new DlgpWriter(s);
		try {
			w.write(o);
			w.close();
		} catch (IOException e) {
			
		}
		return s.toString();
	}
	
};
