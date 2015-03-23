/**
 * 
 */
package fr.lirmm.graphik.graal.apps;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.backward_chaining.pure.rules.HierarchicalCompilation;
import fr.lirmm.graphik.graal.backward_chaining.pure.rules.IDCompilation;
import fr.lirmm.graphik.graal.backward_chaining.pure.rules.NoCompilation;
import fr.lirmm.graphik.graal.backward_chaining.pure.rules.RulesCompilation;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.ruleset.LinkedListRuleSet;
import fr.lirmm.graphik.graal.core.ruleset.RuleSet;
import fr.lirmm.graphik.graal.io.dlp.Dlgp1Parser;
import fr.lirmm.graphik.util.stream.FilterIterator;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
final class Util {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Util.class);
	
	private Util(){};
	
	////////////////////////////////////////////////////////////////////////////
	// 
	////////////////////////////////////////////////////////////////////////////

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
	
	static RulesCompilation selectCompilationType(String compilationName) {
		RulesCompilation compilation = null;
		if (CompileCommand.HIERACHICAL_COMPILATION_NAME.equals(compilationName)) {
			compilation = new HierarchicalCompilation();
		} else if (CompileCommand.ID_COMPILATION_NAME.equals(compilationName)) {
			compilation = new IDCompilation();
		} else {
			compilation = new NoCompilation();
		}
		return compilation;
	}
	
	static List<ConjunctiveQuery> parseQueries(String queryOrQueriesFileName) {
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
				LOGGER.error("File exists but not found !", e);
			}
		} else {
			queries.add(Dlgp1Parser.parseQuery(queryOrQueriesFileName));
		}
		return queries;
	}
	

}
