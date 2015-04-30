/**
 * 
 */
package fr.lirmm.graphik.graal.apps;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import fr.lirmm.graphik.graal.backward_chaining.pure.rules.HierarchicalCompilation;
import fr.lirmm.graphik.graal.backward_chaining.pure.rules.IDCompilation;
import fr.lirmm.graphik.graal.backward_chaining.pure.rules.NoCompilation;
import fr.lirmm.graphik.graal.backward_chaining.pure.rules.RulesCompilation;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.ruleset.LinkedListRuleSet;
import fr.lirmm.graphik.graal.core.ruleset.RuleSet;
import fr.lirmm.graphik.graal.io.RuleWriter;
import fr.lirmm.graphik.graal.io.dlp.Dlgp1Parser;
import fr.lirmm.graphik.util.stream.FilterIterator;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
final class Util {

	private Util() {
	};

	// //////////////////////////////////////////////////////////////////////////
	//
	// //////////////////////////////////////////////////////////////////////////

	static RuleSet parseOntology(String ontologyFile)
			throws FileNotFoundException {
		RuleSet rules = new LinkedListRuleSet();
		Dlgp1Parser parser = new Dlgp1Parser(new File(ontologyFile));
		for (Object o : parser) {
			if (o instanceof Rule) {
				rules.add((Rule) o);
			}
		}
		return rules;
	}

	static RulesCompilation selectCompilationType(String compilationName)
			throws PureException {
		RulesCompilation compilation = null;
		if (CompileCommand.HIERACHICAL_COMPILATION_NAME.equals(compilationName)) {
			compilation = new HierarchicalCompilation();
		} else if (CompileCommand.ID_COMPILATION_NAME.equals(compilationName)) {
			compilation = new IDCompilation();
		} else if (CompileCommand.NO_COMPILATION_NAME.equals(compilationName)) {
			compilation = new NoCompilation();
		} else {
			throw new PureException("Unknown compilation type: "
					+ compilationName);
		}
		return compilation;
	}

	static List<ConjunctiveQuery> parseQueries(String queryOrQueriesFileName)
			throws PureException {
		List<ConjunctiveQuery> queries = new LinkedList<ConjunctiveQuery>();
		File file = new File(queryOrQueriesFileName);
		if (file.exists()) {
			try {
				Iterator<ConjunctiveQuery> it = new FilterIterator<Object, ConjunctiveQuery>(
						new Dlgp1Parser(file), new ConjunctiveQueryFilter());
				while (it.hasNext()) {
					queries.add(it.next());
				}
			} catch (FileNotFoundException e) {
				throw new PureException("Query file exists but not found !", e);
			}
		} else {
			queries.add(Dlgp1Parser.parseQuery(queryOrQueriesFileName));
		}
		return queries;
	}

	private static final String COMPILATION_TYPE_PREFIX = "compilation-type: ";
	private static final int COMPILATION_TYPE_PREFIX_LEN = COMPILATION_TYPE_PREFIX
			.length();

	static void writeCompilation(RulesCompilation compilation, RuleWriter writer)
			throws IOException {
		String compilationName = "UNKNOWN";
		if (compilation instanceof IDCompilation) {
			compilationName = CompileCommand.ID_COMPILATION_NAME;
		} else if (compilation instanceof HierarchicalCompilation) {
			compilationName = CompileCommand.HIERACHICAL_COMPILATION_NAME;
		}

		writer.writeComment(COMPILATION_TYPE_PREFIX + compilationName + "\n");

		for (Rule r : compilation.getSaturation()) {
			writer.write(r);
		}
	}

	static RulesCompilation loadCompilation(File file, Iterator<Rule> rules)
			throws PureException,
			FileNotFoundException {
		Scanner scanner = new Scanner(file);
		String compilationType = "";
		while (scanner.hasNextLine() && compilationType.isEmpty()) {
			String line = scanner.nextLine();
			if (line.startsWith("% " + COMPILATION_TYPE_PREFIX)) {
				compilationType = line
						.substring(COMPILATION_TYPE_PREFIX_LEN + 2);
			}
		}

		RulesCompilation compilation = null;
		if (compilationType.startsWith(CompileCommand.ID_COMPILATION_NAME)) {
			compilation = new IDCompilation();
		} else if (compilationType
				.startsWith(CompileCommand.HIERACHICAL_COMPILATION_NAME)) {
			compilation = new HierarchicalCompilation();
		} else {
			throw new PureException("compilation type inference failed");
		}

		loadCompilation(compilation, file, rules);

		return compilation;
	}

	static void loadCompilation(RulesCompilation compilation, File file,
			Iterator<Rule> rules)
			throws FileNotFoundException {
		compilation.load(rules,
				new FilterIterator<Object, Rule>(new Dlgp1Parser(file),
						new RulesFilter()));
	}

}