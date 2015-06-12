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
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.ruleset.RuleSet;
import fr.lirmm.graphik.graal.core.term.Literal;
import fr.lirmm.graphik.graal.core.term.Term;
import fr.lirmm.graphik.graal.io.AbstractGraalWriter;
import fr.lirmm.graphik.util.DefaultURI;
import fr.lirmm.graphik.util.Prefix;
import fr.lirmm.graphik.util.URI;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class Dlgp1Writer extends AbstractGraalWriter {
	
	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Write into the standard output. Warning, if you close this object, you
	 * will close the standard output.
	 */
	public Dlgp1Writer() {
		this(new OutputStreamWriter(System.out));
	}
	
	public Dlgp1Writer(OutputStream out) {
		this(new OutputStreamWriter(out));
	}
	
	public Dlgp1Writer(Writer out) {
		super(out);
	}
	
	public Dlgp1Writer(File file) throws IOException {
		this(new FileWriter(file));
	}
	
	/**
	 * Write into a file specified by the path file.
	 * 
	 * @param path
	 *            the file path
	 * @throws IOException
	 */
	public Dlgp1Writer(String path) throws IOException {
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
	public void write(Atom atom) throws IOException {
		this.writeAtom(atom);
		this.write(".\n");
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
	
	/*public void write(Iterable<Atom> atoms) throws IOException {
		this.writeAtomSet(atoms, true);
		this.writer.write(".\n");
		this.writer.flush();
	}*/
	
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
		this.flush();
	}
	
	@Override
	public void write(Prefix prefix) throws IOException {
		this.write("%prefix ");
		this.write(prefix.getPrefixName());
		this.write(" <");
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
			
			this.writeAtom(a);
		}
	}
	
	@Override
	protected void writeStandardAtom(Atom atom) throws IOException {
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
		URI uri = new DefaultURI(t.getIdentifier().toString());
		String s = uri.getLocalname();
		char first = s.charAt(0);
		
		if (Term.Type.VARIABLE.equals(t.getType())) {
			if(first < 'A' || first > 'Z') {
				this.write("VAR_");
			}
			this.write(s);
		} else if (Term.Type.CONSTANT.equals(t.getType())) {
			if(first < 'a' || first > 'z') {
				this.write("cst_");
			}
			this.write(s);
		} else { // literal
			Literal l = (Literal) t;
			if (l.getValue() instanceof String) {
				this.write("\"");
				this.write(l.getValue().toString());
				this.write("\"");
			} else {
				this.write(l.getValue().toString());
			}
		}
		
	}
	
	protected void writePredicate(Predicate p) throws IOException {
		String s = p.getIdentifier().toString();
		URI uri = new DefaultURI(p.getIdentifier().toString());
		if (uri.getPrefix().equals(Prefix.PREDICATE)) {
			s = uri.getLocalname();
		}
		
		char first = s.charAt(0);
		if (onlyValidChar(s) && first >= 'a' && first <= 'z') {
			this.write(s);
		} else {
			this.write('"');
			this.write(s);
			this.write('"');
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
