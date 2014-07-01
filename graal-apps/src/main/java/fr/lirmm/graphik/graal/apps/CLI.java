package fr.lirmm.graphik.graal.apps;

import java.io.File;
import java.io.StringReader;

import fr.lirmm.graphik.graal.Graal;
import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.ConjunctiveQueriesUnion;
import fr.lirmm.graphik.graal.core.DefaultConjunctiveQuery;
import fr.lirmm.graphik.graal.core.LinkedListRuleSet;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.RuleSet;
import fr.lirmm.graphik.graal.core.Substitution;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.stream.SubstitutionReader;
import fr.lirmm.graphik.graal.io.dlgp.DlgpParser;
import fr.lirmm.graphik.graal.store.rdbms.DefaultRdbmsStore;
import fr.lirmm.graphik.graal.store.rdbms.driver.RdbmsDriver;
import fr.lirmm.graphik.graal.store.rdbms.driver.SqliteDriver;
import fr.lirmm.graphik.util.stream.ObjectReader;
// execute query

public class CLI {

	public static final int ERR_ARGS        = 1;
	public static final int ERR_FACT        = 2;
	public static final int ERR_PARSEQUERY  = 4;
	public static final int ERR_QUERY       = 8;
	public static final int ERR_PARSERULE   = 16;
	public static final int ERR_APPLYRULE   = 32;
	public static final int ERR_PRINTFACT   = 64;


	public static final int DRIVER_SQLITE   = 1;
	public static final int DRIVER_MYSQL    = 2;

	public CLI() { }

	public void createAtomSet() throws Exception {
		_dbFile = new File(_filename);
		RdbmsDriver driver = null;
		if (_driver == DRIVER_SQLITE)
			driver = new SqliteDriver(_dbFile);
		//else if (_driver == DRIVER_MYSQL)
			//driver = new MysqlDriver();
		_atoms = new DefaultRdbmsStore(driver);
	}

	public void printHelp() {
		System.out.print("alaska-cli ");
		System.out.print("[" + ARG_HELP[0] + "] ");
		System.out.print("[" + ARG_FILE[0] + " <file_path>] ");
		System.out.print("[" + ARG_DRIVER[0] + " <sqlite | mysql>] ");
		System.out.print("[" + ARG_INPUTFORMAT[0] + " <dlp>] ");
		System.out.print("[" + ARG_QUERY[0] + " <query>] ");
		System.out.print("[" + ARG_ADDFACT[0] + " <fact>]");
		System.out.println("");

		System.out.println(ARG_HELP[0] + " | " + ARG_HELP[1] + " \t\t\t\t\t" + 
		                   "print this message");
		System.out.println(ARG_FILE[0] + " | " + ARG_FILE[1] + "\t\t<file_path>\t\t" + 
		                   "select the database file");
		System.out.println(ARG_DRIVER[0] + " | " + ARG_DRIVER[1] + "\t\t<sqlite | mysql>\t" + 
		                   "select the database driver");
		System.out.println(ARG_INPUTFORMAT[0] + " | " + ARG_INPUTFORMAT[1] + "\t<dlp>\t\t\t" + 
		                   "select the input format");
		System.out.println(ARG_QUERY[0] + " | " + ARG_QUERY[1] + "\t\t<query>\t\t\t" + 
		                   "get answers (substitutions) to a given query");
		System.out.println(ARG_ADDFACT[0] + " | " + ARG_ADDFACT[1] + "\t\t<fact>\t\t\t" + 
		                   "add some fact to the atomset");
		System.out.println(ARG_RULE[0] + " | " + ARG_RULE[1] + "\t\t<rule>\t\t\t" + 
		                   "consider some rules");
		System.out.println(ARG_SATURATE[0] + " | " + ARG_SATURATE[1] + "\t\t\t\t\t" + 
		                   "saturate fact");
		System.out.println(ARG_ONESTEPSAT[0] + " | " + ARG_ONESTEPSAT[1] + "\t\t\t" + 
		                   "'one step' saturate fact");
		System.out.println(ARG_PRINTFACT[0] + " | " + ARG_PRINTFACT[1] + "\t\t\t\t" + 
		                   "print fact to stdout");
	}

	public void parseArgs(String[] args) {
		final int n = args.length;
		for (int i = 0 ; i < n ; ++i) {
			//System.out.println("arg[" + i + "]=" + args[i]);
			if (args[i].equals(ARG_HELP[0]) || args[i].equals(ARG_HELP[1])) {
				printHelp();
			}
			else if (args[i].equals(ARG_FILE[0]) || args[i].equals(ARG_FILE[1])) {
				++i;
				if (i < n) _filename = args[i];
			}
			else if (args[i].equals(ARG_QUERY[0]) || args[i].equals(ARG_QUERY[1])) {
				++i;
				if (i < n) _queryString += args[i];
			}
			else if (args[i].equals(ARG_ADDFACT[0]) || args[i].equals(ARG_ADDFACT[1])) {
				++i;
				if (i < n) _factString += args[i];
			}
			else if (args[i].equals(ARG_RMFACT[0]) || args[i].equals(ARG_RMFACT[1])) {
				_removeFact = true;
			}
			else if (args[i].equals(ARG_RULE[0]) || args[i].equals(ARG_RULE[1])) {
				++i;
				if (i < n) _ruleString += args[i];
			}
			else if (args[i].equals(ARG_INPUTFORMAT[0]) || args[i].equals(ARG_INPUTFORMAT[1])) {
				++i;
				if (i < n) _inputFormat = args[i];
				verifyInputFormat();
			}
			else if (args[i].equals(ARG_DRIVER[0]) || args[i].equals(ARG_DRIVER[1])) {
				++i;
				if (i < n) parseDriver(args[i]);
			}
			else if (args[i].equals(ARG_ONESTEPSAT[0]) || args[i].equals(ARG_ONESTEPSAT[1])) {
				if (!_mustSaturate) _mustOneStepSaturate = true;
			}
			else if (args[i].equals(ARG_SATURATE[0]) || args[i].equals(ARG_SATURATE[1])) {
				_mustSaturate = true;
				_mustOneStepSaturate = false;
			}
			else if (args[i].equals(ARG_PRINTFACT[0]) || args[i].equals(ARG_PRINTFACT[1])) {
				_printFact = true;
			}
			else {
				System.err.println("[ignore] Ignoring unrecognized argument: " + args[i]);
			}
		}
	}

	public void parseDriver(String d) {
		if (d.equals("sqlite"))
			_driver = DRIVER_SQLITE;
		//else if (d.equals("mysql"))
			//_driver = DRIVER_MYSQL;
		else {
			System.err.println("[warning] Unrecognized driver: " + d + " > switching to sqlite");
			_driver = DRIVER_SQLITE;
		}
	}

	public void verifyInputFormat() {
		if (!_inputFormat.equals("dlp"))
			System.err.println("[warning] Unrecognized input format: switching to dlp");
		_inputFormat = "dlp";
	}

	public ObjectReader buildReader(String toParse) {
		//if (_inputFormat.equals("dlp")) {
			 return new DlgpParser(new StringReader(toParse));
		//}
	}

	public void parseQuery() throws Exception {
		ObjectReader reader = buildReader(_queryString);
		for (Object o : reader) {
			if (o instanceof DefaultConjunctiveQuery) {
				_query.add((DefaultConjunctiveQuery)o);
			}
			else {
				System.err.println("[ignore] Ignoring non query : " + o);
			}
		}
	}

	public void parseFact() throws Exception {
		//System.out.println("Fact: " + _factString);
		//_factString = "p(X,Y).";
		ObjectReader reader = buildReader(_factString);
		for (Object o : reader) {
			if (o instanceof Atom) {
				if (_removeFact) _atoms.remove((Atom)o);
				else _atoms.add((Atom)o);
			}
			else {
				System.err.println("[ignore] Ignoring non atom : " + o);
			}
		}
	}

	public void parseRule() throws Exception {
		ObjectReader reader = buildReader(_ruleString);
		for (Object o : reader) {
			if (o instanceof Rule) {
				_rules.add((Rule)o);
			}
			else {
				System.err.println("[ignore] Ignoring non rule : " + o);
			}
		}
	}

	public int run(String[] args) {
		this.parseArgs(args);
		int err = 0;

		try { this.createAtomSet(); }
		catch (Exception e) {
			System.err.println("An error occurs while creating atomset : " 
			                   + e + " (" + e.getMessage() + ")");
			e.printStackTrace();
			err |= ERR_ARGS;
			return err;
		}

		if (!_factString.equals("")) {
			try { this.parseFact(); }
			catch (Exception e) {
				System.err.println("An error occurs while parsing or adding atoms : " 
			                   	   + e + " (" + e.getMessage() + ")");
				e.printStackTrace();
				err |= ERR_FACT;
			}
		}

		if (!_ruleString.equals("")) {
			try { this.parseRule(); }
			catch (Exception e) {
				System.err.println("An error occurs while parsing rules : " 
			                   	   + e + " (" + e.getMessage() + ")");
				e.printStackTrace();
				err |= ERR_PARSERULE;
			}
		}

		if (_mustSaturate) {
			try { Graal.executeChase(_atoms, _rules); }
			catch (Exception e) {
				System.err.println("An error occurs while saturating : " 
			                   	   + e + " (" + e.getMessage() + ")");
				e.printStackTrace();
				err |= ERR_APPLYRULE;
			}
		}

		if (_mustOneStepSaturate) {

			try { Graal.executeOneStepChase(_atoms, _rules); }
			catch (Exception e) {
				System.err.println("An error occurs while one step saturating : " 
			                   	   + e + " (" + e.getMessage() + ")");
				e.printStackTrace();
				err |= ERR_APPLYRULE;
			}
		}

		if (!_queryString.equals("")) { 
			//System.out.println("query: " + _queryString);
			try { this.parseQuery(); }
			catch (Exception e) {
				System.err.println("An error occurs while parsing queries : " 
			                   	   + e + " (" + e.getMessage() + ")");
				e.printStackTrace();
				err |= ERR_PARSEQUERY;
			}

			try {
				SubstitutionReader answers = Graal.executeQuery(_query,_atoms);
				for (Substitution s : answers)
					System.out.println(s.toString());
			}
			catch (Exception e) {
				System.err.println("An error occurs while executing queries : " 
			                   	   + e + " (" + e.getMessage() + ")");
				e.printStackTrace();
				err |= ERR_QUERY;
			}
		}

		if (_printFact) {
			try {
				for (Atom a : _atoms)
					System.out.println(a);
			}
			catch (Exception e) {
				System.err.println("An error occurs while printing fact : " 
			                   	   + e + " (" + e.getMessage() + ")");
				e.printStackTrace();
				err |= ERR_PRINTFACT;
			}
			catch (Error e) {
				System.err.println("An error occurs while printing fact : " 
			                   	   + e + " (" + e.getMessage() + ")");
				e.printStackTrace();
				err |= ERR_PRINTFACT;
			}
		}

		return err;
	}

	public static void main(String[] args) {
		CLI cli = new CLI();
		int err = 0;
		if ((err = cli.run(args)) != 0) {
			System.err.println("CLI: some errors have occured: " + err);
		}
	}

	private AtomSet                   _atoms = null;
	private RuleSet                   _rules = new LinkedListRuleSet();
	private ConjunctiveQueriesUnion   _query = new ConjunctiveQueriesUnion();
	private File                      _dbFile = null;

	// for arguments parsing
	private String                    _filename = "_graal_default.db";
	private String                    _queryString = "";
	private String                    _factString = "";
	private String                    _ruleString = "";
	private String                    _inputFormat = "dlp";
	private int                       _driver = DRIVER_SQLITE;
	private boolean                   _mustSaturate = false;
	private boolean                   _mustOneStepSaturate = false;
	private boolean                   _printFact = false;
	private boolean                   _removeFact = true;


	private static final String ARG_HELP[]          =   { "-h", "--help" };
	private static final String ARG_FILE[]          =   { "-f", "--file" };
	private static final String ARG_QUERY[]         =   { "-q", "--query" };
	private static final String ARG_RULE[]          =   { "-r", "--rules" };
	private static final String ARG_ADDFACT[]       =   { "-F", "--fact" };
	private static final String ARG_RMFACT[]        =   { "-d", "--delete-fact" };
	private static final String ARG_SATURATE[]      =   { "-S", "--saturate" };
	private static final String ARG_ONESTEPSAT[]    =   { "-s", "--one-step-saturate" };
	private static final String ARG_PRINTFACT[]     =   { "-p", "--print-fact" };
	private static final String ARG_INPUTFORMAT[]   =   { "-i", "--input-format" };
	private static final String ARG_DRIVER[]        =   { "-b", "--driver" };

};

