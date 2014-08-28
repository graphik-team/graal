package fr.lirmm.graphik.graal.apps;

/**
 * 
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

import org.apache.commons.lang3.StringUtils;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.grd.GraphOfRuleDependencies;
import fr.lirmm.graphik.graal.io.dlgp.DlgpParser;
import fr.lirmm.graphik.graal.io.dlgp.DlgpWriter;
import fr.lirmm.graphik.graal.io.grd.GRDParser;
import fr.lirmm.graphik.graal.rulesetanalyser.RuleAnalyser;
import fr.lirmm.graphik.graal.rulesetanalyser.property.RuleProperty;
import fr.lirmm.graphik.util.graph.scc.StronglyConnectedComponentsGraph;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
public class KiaboraLike {

	private static DlgpWriter writer = new DlgpWriter();

	@Parameter(names = { "-f", "--file" }, description = "DLP file")
	private String file = "";

	@Parameter(names = { "-h", "--help" }, help = true)
	private boolean help;

	@Parameter(names = { "--grd" })
	private boolean grd = false;

	public static void main(String[] args) throws IOException {

		KiaboraLike options = new KiaboraLike();
		JCommander commander = new JCommander(options, args);

		if (options.help) {
			commander.usage();
			System.exit(0);
		}

		GraphOfRuleDependencies grd = null;
		BufferedReader reader;
		if (options.file.isEmpty()) {
			reader = new BufferedReader(new InputStreamReader(System.in));
		} else {
			reader = new BufferedReader(new FileReader(options.file));
		}

		if (options.grd)
			grd = GRDParser.getInstance().parse(reader);
		else {
			LinkedList<Rule> rules = new LinkedList<Rule>();
			DlgpParser parser = new DlgpParser(reader);
			int i = -1;
			for (Object o : parser) {
				if (o instanceof Rule) {
					Rule r = (Rule) o;
					if (r.getLabel().isEmpty()) {
						r.setLabel("R" + ++i);
					}
					rules.add((Rule) o);
				}
			}
			grd = new GraphOfRuleDependencies(rules);
		}

		execute(grd);
	}

	public static void execute(GraphOfRuleDependencies grd) throws IOException {
		RuleAnalyser ra = new RuleAnalyser(grd);
		StronglyConnectedComponentsGraph<Rule> scc = ra
				.getStronglyConnectedComponentsGraph();
		ra.checkAll();

		System.out.println("\n\nInput Rule Base");
		System.out.println("===============\n");
		for (Rule r : grd.getRules()) {
			writer.write(r);
		}

		System.out.println("\n\nGraph of Rule Dependencies");
		System.out.println("==========================\n");
		System.out.println(grd);

		System.out.println("\n\nStrongly Connected Components in the GRD");
		System.out.println("========================================\n");
		for (int v : scc.getVertices()) {
			System.out.print("C" + v + " = {");
			boolean isFirst = true;
			for (Rule r : scc.getComponent(v)) {
				if (!isFirst)
					System.out.print(", ");
				System.out.print(r.getLabel());
				isFirst = false;
			}
			System.out.println('}');
		}

		System.out.println("\n\nGraph of Strongly Connected Components");
		System.out.println("======================================\n");
		for (int src : scc.getVertices()) {
			for (int target : scc.getOutbound(src)) {
				System.out.println("C" + src + " ---> C" + target);
			}
		}

		System.out.println("\n\nRecognized Rule Classes");
		System.out.println("=======================\n");
		int cellSize = 6;
		System.out.print(StringUtils.center(StringUtils.left("C", cellSize),
				cellSize));
		for (RuleProperty rp : ra.getAllProperty()) {
			String pString = StringUtils.center(
					StringUtils.left(rp.getLabel(), cellSize), cellSize);
			System.out.print("|" + pString);
		}
		System.out.println("|");

		System.out.println(StringUtils.center("", (cellSize + 1)
				* (ra.getAllProperty().size() + 1), '-'));

		for (int c : scc.getVertices()) {
			RuleAnalyser subRA = ra.getSubRuleAnalyser(scc.getComponent(c));
			subRA.checkAll();

			System.out.print(StringUtils.center(
					StringUtils.left("C" + c, cellSize), cellSize));
			for (RuleProperty rp : ra.getAllProperty()) {
				System.out.print("|");
				Boolean b = subRA.check(rp);
				if (b == null) {
					System.out.print(StringUtils.center("?", cellSize));
				} else if (b) {
					System.out.print(StringUtils.center("X", cellSize));
				} else {
					System.out.print(StringUtils.center(" ", cellSize));
				}
			}
			System.out.println("|");
		}

		System.out.println(StringUtils.center("", (cellSize + 1)
				* (ra.getAllProperty().size() + 1), '-'));

		// /////////////////////////////////////////////////////////////////////
		// display combine
		System.out.println("\n\nCombined Algorithms FES/FUS/BTS");
		System.out.println("===============================\n");

		if (ra.isDecidable()) {
			System.out.println("Decidable combination found");
		} else {
			System.out.println("Decidable combination not found");
		}

		System.out.println("\n\nPriority: FES");
		System.out.println("-------------\n");
		int[] combination = ra.getCombineWithFESPriority();
		printDecidableCombination(combination);

		System.out.println("\n\nPriority: FUS");
		System.out.println("-------------\n");
		combination = ra.getCombineWithFUSPriority();
		printDecidableCombination(combination);

	}

	private static void printDecidableCombination(int[] combination) {
		for (int i = 0; i < combination.length; ++i) {
			if (combination[i] == 1) {
				System.out.println("C" + i + "    " + "FES");
			} else if (combination[i] == 2) {
				System.out.println("C" + i + "    " + "FUS");
			} else if (combination[i] == 4) {
				System.out.println("C" + i + "    " + "BTS");
			} else {
				System.out.println("C" + i);
			}
		}
	}

}
