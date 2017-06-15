/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2017)
 *
 * Contributors :
 *
 * Clément SIPIETER <clement.sipieter@inria.fr>
 * Mélanie KÖNIG
 * Swan ROCHER
 * Jean-François BAGET
 * Michel LECLÈRE
 * Marie-Laure MUGNIER <mugnier@lirmm.fr>
 *
 *
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
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
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.Literal;
import fr.lirmm.graphik.graal.api.core.NegativeConstraint;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.io.AbstractGraalWriter;
import fr.lirmm.graphik.graal.core.factory.DefaultAtomFactory;
import fr.lirmm.graphik.util.Prefix;
import fr.lirmm.graphik.util.PrefixManager;
import fr.lirmm.graphik.util.URI;
import fr.lirmm.graphik.util.URIUtils;
import fr.lirmm.graphik.util.stream.CloseableIterator;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class DlgpWriter extends AbstractGraalWriter {
	
	private PrefixManager pm;
	private Predicate top = Predicate.TOP;
	private String base = null;

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
		super(out, DefaultAtomFactory.instance());
		this.pm = new PrefixManager();
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
	
	public DlgpWriter write(Directive d) throws IOException {
		switch (d.getType()) {
		case UNA:
			this.write("@una");
			break;
		case BASE:
			if(!d.getValue().toString().isEmpty()) {
				this.base  = d.getValue().toString();
				this.write("@base ");
				this.write('<');
				this.write(encode(this.base));
				this.write('>');
			}
			break;
		case TOP:
			if (d.getValue() instanceof Predicate) {
				this.top = (Predicate) d.getValue();
			} else {
				this.top = new Predicate(d.getValue().toString(), 1);
			}
			this.write("@top ");
			this.writePredicate(this.top);
			break;
		case COMMENT:
			this.write("%% ");
			this.write(d.getValue());
			break;
		}
		this.write("\n");
		return this;
	}

	@Override
	public DlgpWriter writeComment(String comment) throws IOException {
		this.write("% ");
		this.writeln(comment);

		return this;
	}

	@Override
	public DlgpWriter write(AtomSet atomset) throws IOException {
		CloseableIterator<Atom> it = atomset.iterator();
		if(it.hasNext()) {
			this.writeAtomSet(it, true);
			this.writeln(".");
		}

		return this;
	}
	
	@Override
	public DlgpWriter write(Atom atom) throws IOException {
		this.writeAtom(atom);
		this.write(".\n");

		return this;
	}

	@Override
	public DlgpWriter write(Rule rule) throws IOException {
		this.writeLabel(rule.getLabel());

		this.writeAtomSet(rule.getHead().iterator(), false);
		this.write(" :- ");
		this.writeAtomSet(rule.getBody().iterator(), false);
		this.write(".\n");

		return this;
	}
	
	@Override
	public DlgpWriter write(NegativeConstraint constraint) throws IOException {
		this.writeLabel(constraint.getLabel());
		
		this.write(" ! :- ");
		this.writeAtomSet(constraint.getBody().iterator(), false);
		this.write(".\n");

		return this;
	}
	
	@Override
	public DlgpWriter write(ConjunctiveQuery query) throws IOException {
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
		this.writeAtomSet(query.getAtomSet().iterator(), false);
		this.write(".\n");

		return this;
	}
	
	@Override
	public DlgpWriter write(Prefix prefix) throws IOException {
		this.pm.putPrefix(prefix);
		this.write("@prefix ");
		this.write(prefix.getPrefixName());
		this.write(": <");
		this.write(encode(prefix.getPrefix()));
		this.write(">\n");

		return this;
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
	
	protected void writeAtomSet(CloseableIterator<Atom> atomsetIt, boolean addCarriageReturn) throws IOException {
		boolean isFirst = true;
		while (atomsetIt.hasNext()) {
			Atom a = atomsetIt.next();
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
		if(t.isVariable()) {
			this.writeUpperIdentifier(t.getIdentifier());
		} else if(t.isLiteral()) {
			this.writeLiteral((Literal) t);
		} else { // Constant
			this.writeLowerIdentifier(t.getIdentifier());
		}
	}

	protected void writeLiteral(Literal l) throws IOException {
		if(URIUtils.XSD_STRING.equals(l.getDatatype())) {
			this.write('"');
			this.write(l.getValue().toString().replaceAll("\"", "\\\\\""));
			this.write('"');
		} else if (URIUtils.RDF_LANG_STRING.equals(l.getDatatype())) {
			String value = l.getValue().toString();
			int delim = value.lastIndexOf('@');
			if (delim > 0) {
				this.write('"');
				this.write(value.substring(0, delim).replaceAll("\"", "\\\\\""));
				this.write("\"@");
				this.write(value.substring(delim + 1));
			} else {
				this.write('"');
				this.write(value);
				this.write('"');
			}
		} else if (URIUtils.XSD_INTEGER.equals(l.getDatatype())) {
			this.write(l.getValue().toString());
		} else if (URIUtils.XSD_DECIMAL.equals(l.getDatatype())) {
			this.write(l.getValue().toString());
		} else if (URIUtils.XSD_DOUBLE.equals(l.getDatatype())) {
			this.write(l.getValue().toString()); // FIXME ?
		} else if (URIUtils.XSD_BOOLEAN.equals(l.getDatatype())) {
			this.write(l.getValue().toString());
		} else {
			this.write('"');
			this.write(l.getValue().toString().replaceAll("\"", "\\\\\""));
			this.write("\"^^<");
			this.write(l.getDatatype().toString());
			this.write('>');
		}
	}

	protected void writePredicate(Predicate p) throws IOException {
		if (p.equals(Predicate.TOP)) {
			p = top;
		}
		this.writeLowerIdentifier(p.getIdentifier());
	}

	protected void writeURI(URI uri) throws IOException {
		if(base != null && uri.toString().startsWith(base)) {
			this.writeLowerIdentifier(uri.toString().substring(base.length()));
		} else {
			Prefix prefix = this.pm.getPrefixByValue(uri.getPrefix());
			boolean isPrefixable = prefix != null && DlgpGrammarUtils.checkLocalName(uri.getLocalname());
			if(isPrefixable) {
				this.write(prefix.getPrefixName() + ":"
						+ uri.getLocalname());
			} else {
				this.write('<');
				this.write(encode(uri.toString()));
				this.write('>');
			}
		}
	}

	/**
	 * @param identifier
	 * @throws IOException
	 */
	private void writeLowerIdentifier(Object identifier) throws IOException {

		if (identifier instanceof URI) {
			this.writeURI((URI) identifier);
		} else {
			String s = identifier.toString();
			if (DlgpGrammarUtils.checkLIdent(s)) {
				this.write(s);
			} else {
				this.write('<');
				this.write(encode(s));
				this.write('>');
			}
		}
	}

	/**
	 * @param identifier
	 * @throws IOException
	 */
	private void writeUpperIdentifier(Object identifier) throws IOException {
		String s = identifier.toString();
		char first = s.charAt(0);
		if (first < 'A' || first > 'Z') {
			s = "VAR_" + s;
		}
		s = s.replaceAll("[^a-zA-Z0-9_]", "_");
		this.write(s);
	}

	// //////////////////////////////////////////////////////////////////////////
	// STATIC METHODS
	// //////////////////////////////////////////////////////////////////////////

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
	
	private static Map<String, String> replacements = new HashMap<String, String>();
	private static Pattern pattern;
	static {
		replacements.put("\\", "\\\\u00" + Integer.toHexString('\\'));
		replacements.put(" ", "\\\\u00" + Integer.toHexString(' '));
		replacements.put("<", "\\\\u00" + Integer.toHexString('<'));
		replacements.put(">", "\\\\u00" + Integer.toHexString('>'));
		replacements.put("\"", "\\\\u00" + Integer.toHexString('"'));
		replacements.put("{", "\\\\u00" + Integer.toHexString('{'));
		replacements.put("}", "\\\\u00" + Integer.toHexString('}'));
		replacements.put("|", "\\\\u00" + Integer.toHexString('|'));
		replacements.put("^", "\\\\u00" + Integer.toHexString('^'));
		replacements.put("`", "\\\\u00" + Integer.toHexString('`'));
		
		StringBuilder regexp = new StringBuilder();
		boolean first = true;
		for(String key : replacements.keySet()) {
			if(!first) {
				regexp.append('|');
			}
			first = false;
			regexp.append("\\").append(key);
		}
		pattern = Pattern.compile(regexp.toString());
	}
	/*
	 * Replace some chararcters
	 */
	private static String encode(String s) {
		StringBuffer sb = new StringBuffer();
		Matcher m = pattern.matcher(s);

		while (m.find()) {
		    m.appendReplacement(sb, replacements.get(m.group())); 
		}
		m.appendTail(sb);

		return sb.toString();
	}
	
};
