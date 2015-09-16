package fr.lirmm.graphik.graal.apps;

import java.io.FileInputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.grd.GraphOfRuleDependencies;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.graal.io.dlp.DlgpWriter;
import fr.lirmm.graphik.graal.rulesetanalyser.Analyser;
import fr.lirmm.graphik.graal.rulesetanalyser.RuleSetPropertyHierarchy;
import fr.lirmm.graphik.graal.rulesetanalyser.property.AGRDProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.BTSProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.DisconnectedProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.DomainRestrictedProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.FESProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.FUSProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.FrontierGuardedProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.FrontierOneProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.GBTSProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.LinearProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.RangeRestrictedProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.RuleSetProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.StickyProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.WeaklyAcyclicProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.WeaklyFrontierGuardedSetProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.WeaklyGuardedSetProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.WeaklyStickyProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.util.AnalyserRuleSet;
import fr.lirmm.graphik.util.Apps;
import fr.lirmm.graphik.util.graph.scc.StronglyConnectedComponentsGraph;

/**
 * Analyse a rule set.
 * 
 * The input file must be DLGP formatted.
 * 
 * For details about the various arguments use '--help'.
 * 
 * What remains:
 *   - first, we should implement some tests to check if everything
 *   works correctly;
 *   - second, we should implement another main that will do
 *   conversions that old kiabora (may it rest in peace) did;
 *   - finally, upgrade the servlet so it calls this program.
 */
public class Kiabora {

	private static final Logger LOGGER = LoggerFactory.getLogger(Kiabora.class);

	public static final String PROGRAM_NAME = "kiabora";
	public static final Map<String,RuleSetProperty> propertyMap =
		new TreeMap<String,RuleSetProperty>();

	private static long currentRuleID = 0;

	public static void main(String args[]) {
		Kiabora options = new Kiabora();

		JCommander commander = null;
		try {
			commander = new JCommander(options, args);
		} catch (com.beust.jcommander.ParameterException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}

		if (options.help) {
			commander.usage();
			System.exit(0);
		}

		if (options.version) {
			Apps.printVersion(PROGRAM_NAME);
			System.exit(0);
		}

		initPropertyMap();

		// init parser
		DlgpParser parser = null;
		List<Rule> rules = new LinkedList<Rule>();

		if (options.input_filepath.equals("-"))
			parser = new DlgpParser(System.in);
		else {
			try { parser = new DlgpParser(new FileInputStream(options.input_filepath)); }
			catch (Exception e) {
				System.err.println("Could not open file: " + options.input_filepath);
				System.err.println(e);
				e.printStackTrace();
				System.exit(1);
			}
		}

		// parse rule set
		while (parser.hasNext()) {
			Object o = parser.next();
			if (o instanceof Rule) {
				Rule r = (Rule)o;
				if (r.getLabel() == null || r.getLabel().equals(""))
					r.setLabel("R" + currentRuleID++);
				rules.add((Rule)o);
			}
		}

		AnalyserRuleSet ruleset = new AnalyserRuleSet(rules);

		if (options.with_unifiers)
			ruleset.enableUnifiers(true);

		if (options.fast_unification)
			ruleset.setDependencyChecker(GraphOfRuleDependencies.DependencyChecker.DEFAULT);


		// set up analyser
		Map<String,RuleSetProperty> properties = new TreeMap<String,RuleSetProperty>();
		for (String label : options.ruleset_properties) {
			if (label.equals("*"))
				properties.putAll(propertyMap);
			else {
				if (propertyMap.get(label) != null)
					properties.put(label,propertyMap.get(label));
				else if (LOGGER.isWarnEnabled())
					LOGGER.warn("Requesting unknown property: " + label);
			}
		}
		RuleSetPropertyHierarchy hierarchy = new RuleSetPropertyHierarchy(properties.values());

		Analyser analyser = new Analyser();
		analyser.setProperties(hierarchy);
		analyser.setRuleSet(ruleset);


		if (options.print_ruleset) {
			System.out.println("====== RULE SET ======");
			printRuleSet(ruleset);
			System.out.println("");
		}

		if (options.print_grd) {
			System.out.println("======== GRD =========");
			printGRD(ruleset);
			System.out.println("");
		}

		if (options.print_scc) {
			System.out.println("======== SCC =========");
			printSCC(ruleset);
			System.out.println("");
		}

		if (options.print_sccg) {
			System.out.println("===== SCC GRAPH ======");
			printSCCGraph(ruleset);
			System.out.println("");
		}

		if (options.print_rule_pties) {
			System.out.println("== RULE PROPERTIES ===");
			printRuleProperties(analyser);
			System.out.println("");
		}

		if (options.print_pties) {
			System.out.println("===== PROPERTIES =====");
			printProperties(analyser);
			System.out.println("");
		}

		if (options.print_scc_pties) {
			System.out.println("=== SCC PROPERTIES ===");
			printSCCProperties(analyser);
			System.out.println("");
		}

		if (options.combine_fes) {
			System.out.println("=== COMBINE (FES) ====");
			printCombineFES(analyser);
			System.out.println("");
		}

		if (options.combine_fus) {
			System.out.println("=== COMBINE (FUS) ====");
			printCombineFUS(analyser);
			System.out.println("");
		}

	}

	public static void printRuleSet(AnalyserRuleSet ruleset) {
		for (Rule r : ruleset) {
			System.out.print(DlgpWriter.writeToString(r));
		}
	}

	public static void printGRD(AnalyserRuleSet ruleset) {
		System.out.println(ruleset.getGraphOfRuleDependencies().toString());
	}

	public static void printSCC(AnalyserRuleSet ruleset) {
		StringBuilder out = new StringBuilder();
		StronglyConnectedComponentsGraph<Rule> scc = ruleset.getStronglyConnectedComponentsGraph();
		boolean first;
		for (int v : scc.vertexSet()) {
			out.append("C" + v + " = {");
			first = true;
			for (Rule r : scc.getComponent(v)) {
				if (first) first = false;
				else out.append(", ");
				out.append(r.getLabel());
			}
			out.append("}\n");
		}
		System.out.println(out);
	}

	public static void printSCCGraph(AnalyserRuleSet ruleset) {
		StringBuilder out = new StringBuilder();
		StronglyConnectedComponentsGraph<Rule> scc = ruleset.getStronglyConnectedComponentsGraph();
		boolean first;
		for (int v : scc.vertexSet()) {
			out.append("C" + v);
			first = true;
			for (int t : scc.outgoingEdgesOf(v)) {
				if (first) {
					first = false;
					out.append(" ---> ");
				}
				else out.append(", ");
				out.append("C"+scc.getEdgeTarget(t));
			}
			out.append("\n");
		}
		System.out.println(out);
	}
	
	public static void printRuleProperties(Analyser analyser) {
		int cell_size = 6;
		StringBuilder out = new StringBuilder();
		Map<String, Integer> basePties = analyser.ruleProperties().iterator().next();
		Iterator<Rule> rules = analyser.getRuleSet().iterator();

		if (basePties == null)
			return;

		out.append("+");
		out.append(StringUtils.center("", (cell_size + 1) * basePties.entrySet().size() - 1, '-'));
		out.append("+");
		out.append("\n");

		for (Map<String, Integer> pties : analyser.ruleProperties()) {
			for (Map.Entry<String, Integer> e : pties.entrySet()) {
				out.append("|");
				if (e.getValue() == 0)
					out.append(StringUtils.center("?", cell_size));
				else if (e.getValue() < 0)
					out.append(StringUtils.center("-", cell_size));
				else
					out.append(StringUtils.center("X", cell_size));
			}
			out.append("|");
			out.append(StringUtils.center(rules.next().getLabel(), cell_size));
			out.append("\n");
		}

		out.append("+");
		out.append(StringUtils.center("", (cell_size + 1) * basePties.entrySet().size() - 1, '-'));
		out.append("+\n");
		for (Map.Entry<String, Integer> e : basePties.entrySet()) {
			out.append("|");
			out.append(StringUtils.center(e.getKey(), cell_size));
		}
		out.append("|\n");
		out.append("+");
		out.append(StringUtils.center("", (cell_size + 1) * basePties.entrySet().size() - 1, '-'));
		out.append("+");

		System.out.println(out);
	}

	public static void printProperties(Analyser analyser) {
		int cell_size = 6;
		StringBuilder out = new StringBuilder();
		Map<String, Integer> pties = analyser.ruleSetProperties();

		out.append("+");
		out.append(StringUtils.center("", (cell_size+1)*pties.entrySet().size()-1, '-'));
		out.append("+");
		out.append("\n");
		for (Map.Entry<String, Integer> e : pties.entrySet()) {
			out.append("|");
			if (e.getValue() == 0) 
				out.append(StringUtils.center("?", cell_size));
			else if (e.getValue() < 0)
				out.append(StringUtils.center("-", cell_size));
			else
				out.append(StringUtils.center("X", cell_size));
		}
		out.append("|\n");
		out.append("+");
		out.append(StringUtils.center("", (cell_size+1)*pties.entrySet().size()-1, '-'));
		out.append("+\n");
		for (Map.Entry<String, Integer> e : pties.entrySet()) {
			out.append("|");
			out.append(StringUtils.center(e.getKey(), cell_size));
		}
		out.append("|\n");
		out.append("+");
		out.append(StringUtils.center("", (cell_size+1)*pties.entrySet().size()-1, '-'));
		out.append("+");
		System.out.println(out);
	}

	public static void printSCCProperties(Analyser analyser) {
		int cell_size = 6;
		StringBuilder out = new StringBuilder();
		Map<String,Integer> basePties = analyser.sccProperties().iterator().next();

		int cIndex = 0;
		if (basePties == null) return;

		out.append("+");
		out.append(StringUtils.center("", (cell_size+1)*basePties.entrySet().size()-1, '-'));
		out.append("+");
		out.append("\n");

		for (Map<String,Integer> pties : analyser.sccProperties()) {
			for (Map.Entry<String, Integer> e : pties.entrySet()) {
				out.append("|");
				if (e.getValue() == 0) 
					out.append(StringUtils.center("?", cell_size));
				else if (e.getValue() < 0)
					out.append(StringUtils.center("-", cell_size));
				else
					out.append(StringUtils.center("X", cell_size));
			}
			out.append("|");
			out.append(StringUtils.center("C"+cIndex++,cell_size));
			out.append("\n");
		}

		out.append("+");
		out.append(StringUtils.center("", (cell_size+1)*basePties.entrySet().size()-1, '-'));
		out.append("+\n");
		for (Map.Entry<String, Integer> e : basePties.entrySet()) {
			out.append("|");
			out.append(StringUtils.center(e.getKey(), cell_size));
		}
		out.append("|\n");
		out.append("+");
		out.append(StringUtils.center("", (cell_size+1)*basePties.entrySet().size()-1, '-'));
		out.append("+");

		System.out.println(out);
	}

	public static void printCombineFES(Analyser analyser) {
		int combine[] = analyser.combineFES();
		if (combine == null) {
			System.out.println("None!");
			return;
		}

		StringBuilder out = new StringBuilder();
		for (int i = 0 ; i < combine.length ; ++i) {
			out.append("C" + i + ": ");
			if ((combine[i] & Analyser.COMBINE_FES) != 0)
				out.append("FES");
			else if ((combine[i] & Analyser.COMBINE_FUS) != 0)
				out.append("FUS");
			else if ((combine[i] & Analyser.COMBINE_BTS) != 0)
				out.append("BTS");
			out.append("\n");
		}

		System.out.println(out);
	}

	public static void printCombineFUS(Analyser analyser) {
		int combine[] = analyser.combineFUS();
		if (combine == null) {
			System.out.println("None!");
			return;
		}

		StringBuilder out = new StringBuilder();
		for (int i = 0 ; i < combine.length ; ++i) {
			out.append("C" + i + ": ");
			if ((combine[i] & Analyser.COMBINE_FES) != 0)
				out.append("FES");
			else if ((combine[i] & Analyser.COMBINE_FUS) != 0)
				out.append("FUS");
			else if ((combine[i] & Analyser.COMBINE_BTS) != 0)
				out.append("BTS");
			out.append("\n");
		}

		System.out.println(out);
	}

	/**
	 * Prepare the list of rule set properties.
	 * If you have implemented a new rule set property, and you want
	 * an easy way to test it, you are in the right place.
	 * Just add a line that will add an instance of your new class,
	 * compile, and everything will (should) work!
	 */
	public static void initPropertyMap() {
		propertyMap.put("agrd", AGRDProperty.instance());
		propertyMap.put("bts",  BTSProperty.instance());
		propertyMap.put("disc", DisconnectedProperty.instance());
		propertyMap.put("dr",   DomainRestrictedProperty.instance());
		propertyMap.put("fes",  FESProperty.instance());
		propertyMap.put("fg",   FrontierGuardedProperty.instance());
		propertyMap.put("fr1",  FrontierOneProperty.instance());
		propertyMap.put("fus",  FUSProperty.instance());
		propertyMap.put("gbts", GBTSProperty.instance());
		propertyMap.put("lin",  LinearProperty.instance());
		propertyMap.put("rr",   RangeRestrictedProperty.instance());
		propertyMap.put("s",    StickyProperty.instance());
		propertyMap.put("wa",   WeaklyAcyclicProperty.instance());
		propertyMap.put("wfg",  WeaklyFrontierGuardedSetProperty.instance());
		propertyMap.put("wg",   WeaklyGuardedSetProperty.instance());
		propertyMap.put("ws",   WeaklyStickyProperty.instance());
	}

	@Parameter(names = { "-f", "--input-file" },
	           description = "Rule set input file (use '-' for stdin).")
	private String input_filepath = "-";

	@Parameter(names = { "-p", "--properties" },
	           description = "Select which properties must be checked (use '*' to select all).",
	           variableArity = true)
	private List<String> ruleset_properties = new LinkedList<String>();

	@Parameter(names = { "-g", "--grd" },
	           description = "Print the Graph of Rule Dependencies.")
	private boolean print_grd = false;

	@Parameter(names = { "-s", "--scc" },
	           description = "Print the GRD Strongly Connected Components.")
	private boolean print_scc = false;

	@Parameter(names = { "-G", "--scc-graph" },
	           description = "Print the graph of the GRD Strongly Connected Components.")
	private boolean print_sccg = false;

	@Parameter(names = { "-r", "--rule-set" },
	           description = "Print the rule set (can be usefull if some rules were not labelled in the input file).")
	private boolean print_ruleset = false;

	@Parameter(names = { "-P", "--rule-properties" }, description = "")
	private boolean print_rule_pties = false;

	@Parameter(names = { "-S", "--scc-properties" },
	           description = "Print properties for each GRD SCC.")
	private boolean print_scc_pties = false;

	@Parameter(names = { "-R", "--ruleset-properties" },
	           description = "Print properties for the whole rule set.")
	private boolean print_pties = false;

	@Parameter(names = { "-c", "--combine-fes" },
	           description = "Combine GRD connected components in attempt to find some decidable combination while maximising the forward chaining (chase).")
	private boolean combine_fes = false;

	@Parameter(names = { "-b", "--combine-fus" },
	           description = "Combine GRD connected components in attempt to find some decidable combination while maximising the backward chaining (query reformulation).")
	private boolean combine_fus = false;

	@Parameter(names = { "-u", "--unifiers" },
	           description = "Compute all unifiers between rules in order to print them in the GRD.")
	private boolean with_unifiers = false;

	@Parameter(names = { "-U", "--fast-unification" },
	           description = "Enable a faster unification, Kiabora may detect dependencies where there is none.")
	private boolean fast_unification = false;

	@Parameter(names = { "-h", "--help" },
	           description = "Print this message.")
	private boolean help = false;

	@Parameter(names = { "-V", "--version" }, description = "Print version information")
	private boolean version = false;

};

