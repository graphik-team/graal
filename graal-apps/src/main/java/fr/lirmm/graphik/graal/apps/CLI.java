package fr.lirmm.graphik.graal.apps;

import java.io.File;
import java.io.Reader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Map;
import java.util.TreeMap;
import java.util.LinkedList;

import fr.lirmm.graphik.graal.Graal;
import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.ConjunctiveQueriesUnion;
import fr.lirmm.graphik.graal.core.Query;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.RuleSet;
import fr.lirmm.graphik.graal.core.LinkedListRuleSet;
import fr.lirmm.graphik.graal.core.Substitution;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.stream.SubstitutionReader;
import fr.lirmm.graphik.graal.io.dlgp.DlgpParser;
import fr.lirmm.graphik.graal.io.dlgp.DlgpWriter;
import fr.lirmm.graphik.graal.store.rdbms.DefaultRdbmsStore;
import fr.lirmm.graphik.graal.store.rdbms.driver.RdbmsDriver;
import fr.lirmm.graphik.graal.store.rdbms.driver.SqliteDriver;
import fr.lirmm.graphik.util.stream.ObjectReader;

public class CLI {

	public static final String   PROGRAM_NAME    = "graal-cli";
	public static final String[] ARG_HELP        = new String[]{"-h","--help"};
	public static final String[] ARG_VERBOSE     = new String[]{"-v","--verbose"};
	public static final String[] ARG_FILE_INPUT  = new String[]{"-f","--file","--input-file"};
	public static final String[] ARG_FILE_OUTPUT = new String[]{"-d","-o","--output","--database","--db","--output-database"};
	public static final String[] ARG_UCQ         = new String[]{"-u","--ucq"};
	public static final String[] ARG_SATURATE    = new String[]{"-s","--saturate"};

	public static final String ARG_ERROR_MSG = "Error while parsing argument!";

	public static final int ERROR_ARG_FILE_INPUT  = 1;
	public static final int ERROR_ARG_FILE_OUTPUT = 2;
	public static final int ERROR_ARG_UCQ         = 4;

	public static final int ERROR_DB_OPEN         = 8;
	public static final int ERROR_FILE_CLOSE      = 16;
	public static final int ERROR_FILE_PARSE      = 32;
	public static final int ERROR_CHASE           = 64;
	public static final int ERROR_QUERY           = 128;

	private static final String VERBOSE     = "verbose";
	private static final String SATURATE    = "saturate";
	private static final String FILE_INPUT  = "in";
	private static final String FILE_OUTPUT = "out";
	private static final String FILE_UCQ    = "ucq_file";
	private static final String STRING_UCQ  = "ucq_str";

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
		try { k = Integer.valueOf(_args.get(SATURATE)).intValue(); }
		catch (NumberFormatException e) { } // no saturation requested

		DlgpWriter writer = new DlgpWriter(System.out);

		if (k != 0) {
			if (k < 0) {
				if (_verbose) System.out.println("Saturating until fix point...");
				try { Graal.executeChase(_atomset,_rules); }
				catch (Exception e) {
					error |= ERROR_CHASE;
					System.err.println("An error has occured while saturating: "+e);
				}
			}
			else {
				if (_verbose) System.out.println("Saturating "+k+" steps...");
				try { for (i = 0 ; i < k ; ++i) Graal.executeOneStepChase(_atomset,_rules); }
				catch (Exception e) {
					error |= ERROR_CHASE;
					System.err.println("An error has occured during the " + i + " step of saturation: "+e);
				}
			}
			if (_verbose) System.out.println("Atomset saturated!");
		}

		if (!_queries.isEmpty()) {
			if (_verbose) System.out.println("Querying...");
			for (Query q : _queries) {
				try {
					writer.write(q);
					for (Substitution s : Graal.executeQuery(q,_atomset)) System.out.println(s);
				}
				catch (Exception e) {
					error |= ERROR_QUERY;
					System.err.println("An exception has occured while querying: " + e);
				}
			}
			if (_verbose) System.out.println("Querying done!");
		}

		return error;
	}

	public int prepare() {
		int error = 0;

		_verbose = _args.get(VERBOSE) != null;

		String database;
		if ((database = _args.get(FILE_OUTPUT)) == null) database = "_default_graal.db";
		if (_verbose) System.out.println("Database filepath: " + database);

		if (_verbose) System.out.println("Opening database...");
		try {
			File f = new File(database);
			_atomset = new DefaultRdbmsStore(new SqliteDriver(f));
		}
		catch (Exception e) {
			System.err.println("An error has occured while opening database: " +e);
			return ERROR_DB_OPEN;
		}
		if (_verbose) System.out.println("Database opened!");

		String input_file = _args.get(FILE_INPUT);
		if (input_file != null) {
			try {
				Reader reader;
				if (input_file.equals("-")) {
					if (_verbose) System.out.println("Reading stdin...");
					reader = new InputStreamReader(System.in);
				}
				else {
					if (_verbose) System.out.println("Opening file "+input_file+"...");
					reader = new FileReader(input_file);
				}
				DlgpParser parser = new DlgpParser(reader);
				for (Object o : parser) {
					if (o instanceof Atom) {
						if (_verbose) System.out.println("Adding atom " + (Atom)o);
						_atomset.add((Atom)o);
						if (_verbose) System.out.println("Atom added!");
					}
					else if (o instanceof Rule) {
						if (_verbose) System.out.println("Adding rule " + (Rule)o);
						_rules.add((Rule)o);
						if (_verbose) System.out.println("Rule added!");
					}
					else if (o instanceof Query) {
						if (_verbose) System.out.println("Adding query " + (Query)o);
						_queries.add((Query)o);
						if (_verbose) System.out.println("Query added!");
					}
					else {
						if (_verbose) System.out.println("Ignoring non recognized object: " + o);
					}
				}
			//	try {
			//		if (_verbose) System.out.println("Closing file...");
			//		parser.close();
			//		if (_verbose) System.out.println("File closed!");
			//	}
			//	catch (Exception e) {
			//		System.err.println("Cannot close file: " + e);
			//		error |= ERROR_FILE_CLOSE;
			//	}
			}
			catch (Exception e) {
				System.err.println("An error has occured: " +e);
				error |= ERROR_FILE_PARSE;
			}
		}

		String ucq_file = _args.get(FILE_UCQ);
		if (ucq_file != null) {
			ConjunctiveQueriesUnion ucq = new ConjunctiveQueriesUnion();
			if (_verbose) System.out.println("Opening UCQ file "+ucq_file+"...");
			try {
				DlgpParser parser = new DlgpParser(new FileReader(ucq_file));
				for (Object o : parser) {
					if (o instanceof ConjunctiveQuery) {
						if (_verbose) System.out.println("Adding query to union " + (Query)o);
						ucq.add((ConjunctiveQuery)o);
					}
					else {
						if (_verbose) System.out.println("Ignoring non query object: " + o);
					}
				}
				_queries.add(ucq);
				try {
					parser.close();
					if (_verbose) System.out.println("File closed!");
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

		String ucq_string = _args.get(STRING_UCQ);
		if (ucq_string != null) {
			ConjunctiveQueriesUnion ucq = new ConjunctiveQueriesUnion();
			if (_verbose) System.out.println("Reading UCQ string "+ucq_string+"...");
			try {
				DlgpParser parser = new DlgpParser(new StringReader(ucq_string));
				for (Object o : parser) {
					if (o instanceof ConjunctiveQuery) {
						if (_verbose) System.out.println("Adding query to union " + (Query)o);
						ucq.add((ConjunctiveQuery)o);
					}
					else {
						if (_verbose) System.out.println("Ignoring non query object: " + o);
					}
				}
				_queries.add(ucq);
			}
			catch (Exception e) {
				System.err.println("An error has occured: " +e);
				error |= ERROR_FILE_PARSE;
			}
		}

		return error;

	}

	public boolean isArg(String a, String[] arg) {
		for (String s : arg) if (a.equals(s)) return true;
		return false;
	}

	public void printHelp() {
		int i;
		final int v = 24;
		final int c = 40;
		System.out.println(PROGRAM_NAME);
		System.out.println(" [-h] [-v] [-f <input_file>] [-d <db_file>] [-u <ucq_file|ucq_string>] [-s [<n>]]");
		System.out.print("---------");
		System.out.println("-----------------------------------------------------------------");

		System.out.println("-h    --help                                  print this message");
		System.out.println("-v    --verbose                               enable verbose mode (more outputs)");
		System.out.println("-f    --file        <file_path>               read a dlp file as input (use - for stdin)");
		System.out.println("-d    --database    <file_path>               select the database file");
		System.out.println("-u    --ucq         <file_path|dlp_string>    read a dlp file or string as a union of conjunctive queries");
		System.out.println("-s    --saturate    [<n>]                     execute the chase for n steps ; if n is negative of not specified, the chase will be executed until a fixpoint is reached");
	}

	public int parseArgs(String argv[]) {
		int n = argv.length;
		for (int i = 0 ; i < n ; ++i) {
			if (isArg(argv[i],ARG_HELP)) {
				printHelp();
				System.exit(0);
			}
			else if (isArg(argv[i],ARG_VERBOSE)) {
				_args.put(VERBOSE,"1");
			}
			else if (isArg(argv[i],ARG_FILE_INPUT)) {
				++i;
				if (i >= n) return ERROR_ARG_FILE_INPUT;
				_args.put(FILE_INPUT,argv[i]);
			}
			else if (isArg(argv[i],ARG_FILE_OUTPUT)) {
				++i;
				if (i >= n) return ERROR_ARG_FILE_OUTPUT;
				_args.put(FILE_OUTPUT,argv[i]);
			}
			else if (isArg(argv[i],ARG_UCQ)) {
				++i;
				if (i >= n) return ERROR_ARG_UCQ;
				if (argv[i].charAt(0) == '?') _args.put(STRING_UCQ,argv[i]);
				else _args.put(FILE_UCQ,argv[i]);
			}
			else if (isArg(argv[i],ARG_SATURATE)) {
				if ((i+1 < n) && (argv[i+1].charAt(0) != '-')) {
					++i;
					_args.put(SATURATE,argv[i]);
				}
				else _args.put(SATURATE,"-1");
			}
			else {
				System.err.println("Ignoring unrecognized argument: " + argv[i]);
			}
		}

		return 0;
	}


	private Map<String,String> _args = new TreeMap<String,String>();
	private boolean _verbose = false;
	private AtomSet _atomset = null;
	private RuleSet _rules = new LinkedListRuleSet();
	private LinkedList<Query> _queries = new LinkedList<Query>();

};

