/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2015)
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
 package fr.lirmm.graphik.graal.apps;

import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.Query;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.RuleSet;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.homomorphism.Homomorphism;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.DefaultConjunctiveQuery;
import fr.lirmm.graphik.graal.core.UnionConjunctiveQueries;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.ruleset.LinkedListRuleSet;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.forward_chaining.NaiveChase;
import fr.lirmm.graphik.graal.homomorphism.ComplexHomomorphism;
import fr.lirmm.graphik.graal.homomorphism.StaticHomomorphism;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.graal.io.dlp.DlgpWriter;
import fr.lirmm.graphik.graal.store.rdbms.AbstractRdbmsStore;
import fr.lirmm.graphik.graal.store.rdbms.DefaultRdbmsStore;
import fr.lirmm.graphik.graal.store.rdbms.driver.SqliteDriver;
import fr.lirmm.graphik.graal.store.rdbms.homomorphism.SqlHomomorphism;

public class CLI {

	public static final int ERROR_ARG_FILE_INPUT  = 1;
	public static final int ERROR_ARG_FILE_OUTPUT = 2;
	public static final int ERROR_ARG_UCQ         = 4;

	public static final int ERROR_DB_OPEN         = 8;
	public static final int ERROR_FILE_CLOSE      = 16;
	public static final int ERROR_FILE_PARSE      = 32;
	public static final int ERROR_CHASE           = 64;
	public static final int ERROR_QUERY           = 128;
	public static final int ERROR_PRINTFACT       = 256;


	public static final String   PROGRAM_NAME    = "graal-cli";
	public static final String[] ARG_HELP        = new String[]{"-h","--help"};
	public static final String[] ARG_VERBOSE     = new String[]{"-v","--verbose"};
	public static final String[] ARG_FILE_INPUT  = new String[]{"-f","--file","--input-file"};
	public static final String[] ARG_FILE_OUTPUT = new String[]{"-d","--db","--database"};
	public static final String[] ARG_UCQ         = new String[]{"-u","-q","--ucq"};
	public static final String[] ARG_SATURATE    = new String[]{"-s","--saturate"};
	public static final String[] ARG_PRINTFACT   = new String[]{"-p","--print-fact"};

	public static final String ARG_ERROR_MSG = "Error while parsing argument!";

	public static void main(String args[]) {
		int error = 0;

		CLI cli = new CLI();

		error |= cli.parseArgs(args);

		if (error != 0) {
			System.err.println(ARG_ERROR_MSG);
			cli.printHelp();
			System.exit(error);
		}

		error |= cli.prepare();
		error |= cli.execute();

		if (error != 0) {
			System.err.println("Something went wrong!");
			System.err.println("Error:"+error);
			System.exit(error);
		}
	}

	public int execute() {
		int error = 0;
		int i = 0;
		int k = 0;
		try { k = Integer.parseInt(args.get(SATURATE)); }
		catch (NumberFormatException e) { } // no saturation requested

		DlgpWriter writer = new DlgpWriter(System.out);
		Homomorphism solver = new ComplexHomomorphism(SqlHomomorphism.instance());

		NaiveChase chase = new NaiveChase(rules,atomset,solver);

		if (k != 0) {
			if (k < 0) {
				if (verbose) System.out.println("Saturating until fix point...");
				try { chase.execute(); } 
				catch (Exception e) {
					error |= ERROR_CHASE;
					System.err.println("An error has occured while saturating: "+e);
					e.printStackTrace();
				}
			}
			else {
				if (verbose) System.out.println("Saturating "+k+" steps...");
				try { for (i = 0 ; i < k ; ++i) if (chase.hasNext()) chase.next(); }
				catch (Exception e) {
					error |= ERROR_CHASE;
					System.err.println("An error has occured during the " + i + " step of saturation: "+e);
				}
			}
			if (verbose) System.out.println("Atomset saturated!");
		}

		if (!queries.isEmpty()) {
			if (verbose) System.out.println("Querying...");
			for (Query q : queries) {
				try {
					writer.write(q);
					for (Substitution s : StaticHomomorphism.executeQuery(q,atomset)) System.out.println(s);
				}
				catch (Exception e) {
					error |= ERROR_QUERY;
					System.err.println("An exception has occured while querying: " + e);
				}
			}
			if (verbose) System.out.println("Querying done!");
		}

		if (args.get(PRINTFACT) != null) {
			if (verbose) System.out.println("Printing fact...");
			try {
				for (Atom a : atomset)
					System.out.println(a);
			}
			catch (Exception e) {
				System.err.println("An error occurs while printing fact : " 
			                   	   + e + " (" + e.getMessage() + ")");
				e.printStackTrace();
				error |= ERROR_PRINTFACT;
			}
			catch (Error e) {
				System.err.println("An error occurs while printing fact : " 
			                   	   + e + " (" + e.getMessage() + ")");
				e.printStackTrace();
				error |= ERROR_PRINTFACT;
			}
			if (verbose) System.out.println("Fact printed!");
		}

		return error;
	}

	public int prepare() {
		int error = 0;

		verbose = args.get(VERBOSE) != null;

		String database;
		if ((database = args.get(FILE_OUTPUT)) == null) database = "_default_graal.db";
		if (verbose) System.out.println("Database filepath: " + database);

		if (verbose) System.out.println("Opening database...");
		try {
			File f = new File(database);
			atomset = new DefaultRdbmsStore(new SqliteDriver(f));
		}
		catch (Exception e) {
			System.err.println("An error has occured while opening database: " +e);
			return ERROR_DB_OPEN;
		}
		if (verbose) System.out.println("Database opened!");

		String inputFile = args.get(FILE_INPUT);
		if (inputFile != null) {
			try {
				Reader reader;
				if (inputFile.equals("-")) {
					if (verbose) System.out.println("Reading stdin...");
					reader = new InputStreamReader(System.in);
				}
				else {
					if (verbose) System.out.println("Opening file "+inputFile+"...");
					reader = new FileReader(inputFile);
				}
				DlgpParser parser = new DlgpParser(reader);
				for (Object o : parser) {
					if (o instanceof Atom) {
						if (verbose) System.out.println("Adding atom " + (Atom)o);
						atomset.addUnbatched((Atom)o);
						if (verbose) System.out.println("Atom added!");
					}
					else if (o instanceof Rule) {
						if (verbose) System.out.println("Adding rule " + (Rule)o);
						rules.add((Rule)o);
						if (verbose) System.out.println("Rule added!");
					}
					else if (o instanceof Query) {
						if (verbose) System.out.println("Adding query " + (Query)o);
						queries.add((Query)o);
						if (verbose) System.out.println("Query added!");
					}
					else {
						if (verbose) System.out.println("Ignoring non recognized object: " + o);
					}
				}
				atomset.commitAtoms();
			}
			catch (Exception e) {
				System.err.println("An error has occured: " +e);
				error |= ERROR_FILE_PARSE;
			}
		}

		String ucqFile = args.get(FILE_UCQ);
		if (ucqFile != null) {
			UnionConjunctiveQueries ucq = new UnionConjunctiveQueries();
			if (verbose) System.out.println("Opening UCQ file "+ucqFile+"...");
			try {
				DlgpParser parser = new DlgpParser(new FileReader(ucqFile));
				for (Object o : parser) {
					if (o instanceof ConjunctiveQuery) {
						if (verbose) System.out.println("Adding query to union " + (Query)o);
						ucq.add((ConjunctiveQuery)o);
					}
					else {
						if (verbose) System.out.println("Ignoring non query object: " + o);
					}
				}
				queries.add(ucq);
				try {
					parser.close();
					if (verbose) System.out.println("File closed!");
				}
				catch (Exception e) {
					System.err.println("Cannot close file: " + e);
					error |= ERROR_FILE_CLOSE;
				}
			}
			catch (Exception e) {
				System.err.println("An error has occured: " +e);
				error |= ERROR_FILE_PARSE;
			}
		}

		String ucqString = args.get(STRING_UCQ);
		if (ucqString != null) {
			UnionConjunctiveQueries ucq = new UnionConjunctiveQueries();
			if (verbose) System.out.println("Reading UCQ string "+ucqString+"...");
			try {
				DlgpParser parser = new DlgpParser(new StringReader(ucqString));
				for (Object o : parser) {
					if (o instanceof ConjunctiveQuery) {
						if (verbose) System.out.println("Adding query to union " + (Query)o);
						ucq.add(prepareConjunctiveQuery((ConjunctiveQuery)o));
					}
					else {
						if (verbose) System.out.println("Ignoring non query object: " + o);
					}
				}
				queries.add(ucq);
			}
			catch (Exception e) {
				System.err.println("An error has occured: " +e);
				error |= ERROR_FILE_PARSE;
			}
		}

		return error;

	}

	public int parseArgs(String argv[]) {
		int n = argv.length;
		for (int i = 0 ; i < n ; ++i) {
			if (isArg(argv[i],ARG_HELP)) {
				printHelp();
				System.exit(0);
			}
			else if (isArg(argv[i],ARG_VERBOSE)) {
				args.put(VERBOSE,"1");
			}
			else if (isArg(argv[i],ARG_PRINTFACT)) {
				args.put(PRINTFACT,"1");
			}
			else if (isArg(argv[i],ARG_FILE_INPUT)) {
				++i;
				if (i >= n) return ERROR_ARG_FILE_INPUT;
				args.put(FILE_INPUT,argv[i]);
			}
			else if (isArg(argv[i],ARG_FILE_OUTPUT)) {
				++i;
				if (i >= n) return ERROR_ARG_FILE_OUTPUT;
				args.put(FILE_OUTPUT,argv[i]);
			}
			else if (isArg(argv[i],ARG_UCQ)) {
				++i;
				if (i >= n) return ERROR_ARG_UCQ;
				if (argv[i].charAt(0) == '?') args.put(STRING_UCQ,argv[i]);
				else args.put(FILE_UCQ,argv[i]);
			}
			else if (isArg(argv[i],ARG_SATURATE)) {
				if ((i+1 < n) && (argv[i+1].charAt(0) != '-')) {
					++i;
					args.put(SATURATE,argv[i]);
				}
				else args.put(SATURATE,"-1");
			}
			else {
				System.err.println("Ignoring unrecognized argument: " + argv[i]);
			}
		}

		return 0;
	}

	public void printHelp() {
		System.out.println(PROGRAM_NAME);
		System.out.println(" [-h] [-v] [-f <input_file>] [-d <db_file>] [-u <ucq_file|ucq_string>] [-s [<n>]]");
		System.out.print("---------");
		System.out.println("-----------------------------------------------------------------");

		System.out.println("-h    --help                                  print this message");
		System.out.println("-v    --verbose                               enable verbose mode (more outputs)");
		System.out.println("-f    --file        <file_path>               read a dlp file as input (use - for stdin)");
		System.out.println("-d    --database    <file_path>               select the database file");
		System.out.println("-p    --print-fact                            print the fact to stdout");
		System.out.println("-u    --ucq         <file_path|dlp_string>    read a dlp file or string as a union of conjunctive queries");
		System.out.println("-s    --saturate    [<n>]                     execute the chase for n steps ; if n is negative of not specified, the chase will be executed until a fixpoint is reached");
	}

	public boolean isArg(String a, String[] arg) {
		for (String s : arg) if (a.equals(s)) return true;
		return false;
	}

	public ConjunctiveQuery prepareConjunctiveQuery(ConjunctiveQuery q) {
		DefaultConjunctiveQuery qRw = new DefaultConjunctiveQuery(new LinkedListAtomSet());
		qRw.setAnswerVariables(q.getAnswerVariables());
		for (Atom a : q) {
			Atom a2 = new DefaultAtom(a.getPredicate());
			int i = 0;
			for (Term t : a) {
				a2.setTerm(i,prepareConjunctiveQueryAtomTerm(t));
				++i;
			}
			qRw.getAtomSet().add(a2);
		}
		return qRw;
	}

	public Term prepareConjunctiveQueryAtomTerm(Term t) {
		String value = (String)(t.getIdentifier());
		if ((value.length() >= 4)
		 && (value.charAt(0) == 'C')
		 && (value.charAt(1) == 'S')
		 && (value.charAt(2) == 'T')
		 && (value.charAt(3) == '_'))
			return DefaultTermFactory.instance().createConstant(
					value.substring(4));
		return t;
	}


	private static final String VERBOSE     = "verbose";
	private static final String SATURATE    = "saturate";
	private static final String FILE_INPUT  = "in";
	private static final String FILE_OUTPUT = "out";
	private static final String FILE_UCQ    = "ucq_file";
	private static final String STRING_UCQ  = "ucq_str";
	private static final String PRINTFACT   = "print_fact";

	private Map<String,String> args = new TreeMap<String,String>();
	private boolean verbose = false;
	//private AtomSet _atomset = null;
	private AbstractRdbmsStore atomset = null;
	private RuleSet rules = new LinkedListRuleSet();
	private LinkedList<Query> queries = new LinkedList<Query>();

};

