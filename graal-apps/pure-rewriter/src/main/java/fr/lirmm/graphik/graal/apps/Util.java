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
package fr.lirmm.graphik.graal.apps;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import fr.lirmm.graphik.graal.backward_chaining.pure.HierarchicalCompilation;
import fr.lirmm.graphik.graal.backward_chaining.pure.IDCompilation;
import fr.lirmm.graphik.graal.backward_chaining.pure.NoCompilation;
import fr.lirmm.graphik.graal.backward_chaining.pure.RulesCompilation;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.ruleset.LinkedListRuleSet;
import fr.lirmm.graphik.graal.core.ruleset.RuleSet;
import fr.lirmm.graphik.graal.io.dlp.Directive;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.graal.io.dlp.DlgpWriter;
import fr.lirmm.graphik.util.Prefix;
import fr.lirmm.graphik.util.stream.filter.FilterIterator;

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

	static Pair<LinkedList<Prefix>, RuleSet> parseOntology(
			String ontologyFile)
			throws FileNotFoundException {
		RuleSet rules = new LinkedListRuleSet();
		LinkedList<Prefix> prefixes = new LinkedList<Prefix>();
		DlgpParser parser = new DlgpParser(new File(ontologyFile));
		for (Object o : parser) {
			if (o instanceof Rule) {
				rules.add((Rule) o);
			} else if (o instanceof Prefix) {
				prefixes.add((Prefix) o);
			}
		}
		return new ImmutablePair<LinkedList<Prefix>, RuleSet>(prefixes, rules);
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
						new DlgpParser(file), new ConjunctiveQueryFilter());
				while (it.hasNext()) {
					queries.add(it.next());
				}
			} catch (FileNotFoundException e) {
				throw new PureException("Query file exists but not found !", e);
			}
		} else {
			queries.add(DlgpParser.parseQuery(queryOrQueriesFileName));
		}
		return queries;
	}

	static void writeCompilation(RulesCompilation compilation, DlgpWriter writer)
			throws IOException {
		String compilationName = "UNKNOWN";
		if (compilation instanceof IDCompilation) {
			compilationName = CompileCommand.ID_COMPILATION_NAME;
		} else if (compilation instanceof HierarchicalCompilation) {
			compilationName = CompileCommand.HIERACHICAL_COMPILATION_NAME;
		}

		writer.writeDirective(new Directive(Directive.Type.COMMENT,
				compilationName));

		for (Rule r : compilation.getSaturation()) {
			writer.write(r);
		}
	}

	static Pair<LinkedList<Prefix>, RulesCompilation> loadCompilation(
			File file, Iterator<Rule> rules)
			throws PureException, FileNotFoundException {
		String compilationType = "";
		RuleSet saturation = new LinkedListRuleSet();
		LinkedList<Prefix> prefixes = new LinkedList<Prefix>();

		DlgpParser parser = new DlgpParser(file);
		for(Object o : parser) {
			if (o instanceof Rule) {
				saturation.add((Rule) o);
			} else if (o instanceof Directive
					&& ((Directive) o).getType().equals(Directive.Type.COMMENT)) {
				compilationType = (String) ((Directive) o).getValue();
			} else if (o instanceof Prefix) {
				prefixes.add((Prefix) o);
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

		
		compilation.load(rules, saturation.iterator());
		
		return new ImmutablePair<LinkedList<Prefix>, RulesCompilation>(
				prefixes, compilation);
	}



}