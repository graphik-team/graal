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
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.Substitution;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.impl.DefaultConjunctiveQuery;
import fr.lirmm.graphik.graal.core.ruleset.LinkedListRuleSet;
import fr.lirmm.graphik.graal.core.ruleset.RuleSet;
import fr.lirmm.graphik.graal.core.term.Term;
import fr.lirmm.graphik.graal.cqa.AtomIndex;
import fr.lirmm.graphik.graal.cqa.FGH;
import fr.lirmm.graphik.graal.cqa.FGHRuleApplicationHandler;
import fr.lirmm.graphik.graal.forward_chaining.NaiveChase;
import fr.lirmm.graphik.graal.forward_chaining.halting_condition.ChaseHaltingCondition;
import fr.lirmm.graphik.graal.forward_chaining.halting_condition.ChaseStopConditionWithHandler;
import fr.lirmm.graphik.graal.forward_chaining.halting_condition.RestrictedChaseStopCondition;
import fr.lirmm.graphik.graal.homomorphism.ComplexHomomorphism;
import fr.lirmm.graphik.graal.homomorphism.Homomorphism;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.graal.store.rdbms.DefaultRdbmsStore;
import fr.lirmm.graphik.graal.store.rdbms.driver.SqliteDriver;
import fr.lirmm.graphik.graal.store.rdbms.homomorphism.SqlHomomorphism;

public class CLI_FGH {

	public final String PROGRAM_NAME = "graal-cli-fgh";

	public static void main(String... args) {
		static CLI_FGH options = new CLI_FGH();
		JCommander commander = new JCommander(options,args);

		if (options.help) {
			commander.usage();
			System.exit(0);
		}

		try {
			System.out.println("Initialising database file...");
			File f = new File(options.db_file);
			AtomSet atomset = new DefaultRdbmsStore(new SqliteDriver(f));
			System.out.println("Done!");

			RuleSet rules = new LinkedListRuleSet();
			LinkedList<ConjunctiveQuery> constraints = new LinkedList<ConjunctiveQuery>();

			System.out.println("Initialising atom index file...");
			AtomIndex index = new AtomIndex(options.index_file);
			System.out.println("Done!");

			FGH fgh = new FGH();

			Homomorphism solver = new ComplexHomomorphism(SqlHomomorphism.instance());

			FGHRuleApplicationHandler onRule = new FGHRuleApplicationHandler(index,fgh);
			onRule.setSolver(solver);

			ChaseHaltingCondition haltCondition = new ChaseStopConditionWithHandler(new RestrictedChaseStopCondition(),onRule);
			NaiveChase chase = new NaiveChase(rules, atomset, solver, haltCondition);

			if (options.input_file != "") {
				System.out.println("Reading data from dlp file: " + options.input_file);
				Reader reader;
				if (options.input_file.equals("-")) reader = new InputStreamReader(System.in);
				else reader = new FileReader(options.input_file);

				DlgpParser parser = new DlgpParser(reader);

				for (Object o : parser) {
					if (o instanceof Atom)
						//atomset.addUnbatched((Atom)o); TODO
						atomset.add((Atom)o);
					else if (o instanceof Rule)
						rules.add((Rule)o);
					else if (o instanceof ConjunctiveQuery)
						constraints.add((ConjunctiveQuery)o);
					else
						System.out.println("Ignoring non recognized object: " + o);
				}
				// TODO
				//atomset.commitAtoms();
				System.out.println("Done!");
			}

			System.out.println("Adding initial facts to index...");
			int init_size = 0;
			for (Atom a : atomset) {
				index.get(a);
				++init_size;
			}
			System.out.println("Done!");

			if (options.executingChase) {
				System.out.println("Executing chase...");
				chase.execute();
				System.out.println("Done!");
			}

			System.out.println("Counting atoms...");
			int nbAtoms = 0;
			for (Atom a : atomset)
				++nbAtoms;
			System.out.println("Done: "+nbAtoms);

			if (options.computingBasicWeights) {
				// print weight file
				System.out.println("Creating basic weights file: " + options.weight_file);
				File wf = new File(options.weight_file);
				FileWriter out = new FileWriter(wf);

				Pattern p = Pattern.compile("rule[0-9]+.*");
				out.write(nbAtoms);
				out.write("\n");
				for (int i = 0 ; i < nbAtoms ; ++i) {
					Atom a = index.get(i);
					if (i < init_size) 
						out.write("1.0 1.0\n"); // 1");
					else {
						Matcher m = p.matcher(a.getPredicate().getIdentifier()
								.toString());
						if (m.find()) // this is a "rule" predicate => last term is used to represent confidence value
							// TODO perhaps GRAAL computes type in a good way:
							// (Double)getValue() instead
							out.write(((LinkedList<Term>)a.getTerms()).getLast().getIdentifier().toString());
						else
							out.write("1.0");
						out.write(" 0.0\n"); // 0");
					}
				}
				System.out.println("Done!");
			}

			if (options.computingFGH) {
				System.out.println("Creating FGH file...");
				fgh.writeToFile(options.fgh_file);
				System.out.println("Done!");
			}

			if (options.computingConflicts) {
				File f2 = new File(options.conflict_file);
				FileWriter out = new FileWriter(f2);
				for (ConjunctiveQuery constraint : constraints) {
					DefaultConjunctiveQuery q = new DefaultConjunctiveQuery(constraint);
					q.setAnswerVariables(new LinkedList<Term>(q.getAtomSet().getTerms()));
					for (Substitution s : solver.execute(q,atomset)) {
						AtomSet conflict = s.createImageOf(q.getAtomSet());
						int conflict_size = 0;
						for (Atom a : conflict)
							++conflict_size;
						out.write(conflict_size);
						out.write(' ');
						for (Atom a : conflict) {
							out.write(index.get(a));
							out.write(' ');
						}
						out.write("\n");
					}
				}
			}
			else if (options.computingUCQ) {
				File f2 = new File(options.ucq_file);
				FileWriter out = new FileWriter(f2);
				for (ConjunctiveQuery constraint : constraints) {
					DefaultConjunctiveQuery q = new DefaultConjunctiveQuery(constraint);
					q.setAnswerVariables(new LinkedList<Term>(q.getAtomSet().getTerms()));
					for (Substitution s : solver.execute(q,atomset)) {
						AtomSet conflict = s.createImageOf(q.getAtomSet());
						int conflict_size = 0;
						for (Atom a : conflict)
							++conflict_size;
						out.write(conflict_size);
						out.write(' ');
						for (Atom a : conflict) {
							out.write(index.get(a));
							out.write(' ');
						}
						out.write("\n");
					}
				}
			}

			// because finalize() may not be called........
			if (options.computingIndex) {
				index.writeToFile();
			}

		}
		catch (Exception e) {
			System.err.println(e);
			e.printStackTrace();
		}
	}

	@Parameter(names = { "-S", "--chase", "--saturation" }, description = "Execute chase")
	private boolean executingChase = false;

	@Parameter(names = { "-C", "--conflicts", "--naive-conflicts" }, description = "Compute naive conflicts")
	private boolean computingConflicts = false;

	@Parameter(names = { "-I", "--index", "--compute-index" }, description = "Compute index")
	private boolean computingIndex = false;

	@Parameter(names = { "-W", "--basic-weights" }, description = "Compute basic weights")
	private boolean computingBasicWeights = false;

	@Parameter(names = { "-G", "--compute-fgh" }, description = "Compute fact generation hypergraph")
	private boolean computingFGH = false;

	@Parameter(names = { "-U", "--compute-ucq" }, description = "Compute ucq (incompatible with compute conflicts)")
	private boolean computingUCQ = false;

	@Parameter(names= { "-h", "--help" }, description = "Print this message")
	private boolean help = false;

	@Parameter(names = { "-d", "--db", "--db-file" }, description = "Output database file")
	private String db_file = "_default_graal.db";

	@Parameter(names = { "-f", "--input-file" }, description = "Input DLP file")
	private String input_file = "-";

	@Parameter(names = { "-i", "--index-file" }, description = "Index file")
	private String index_file = "_default.index";

	@Parameter(names = { "-w", "--basic-weight-file" }, description = "Basic weights file")
	private String weight_file = "_default.basic-weights";

	@Parameter(names = { "-g", "--fgh", "--fact-generation-file" }, description = "Fact generation hypergraph file")
	private String fgh_file = "_default.fgh";

	@Parameter(names = { "-c", "--conflicts-file", "--naive-conflicts-file" }, description = "Naive conflicts file")
	private String conflict_file = "_default.naive-conflicts";

	@Parameter(names = { "-u", "--ucq-file" }, description = "UCQ output file")
	private String ucq_file = "_default.ucq_answers";


};

