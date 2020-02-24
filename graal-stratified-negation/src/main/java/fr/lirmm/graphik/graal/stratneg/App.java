package fr.lirmm.graphik.graal.stratneg;

import java.io.File;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.google.errorprone.annotations.Var;

public class App {

  private static final String PROGRAM_NAME = "graal-stratified-negation";
  private static final String VERSION = "1.0";
  @Parameter(names = {"-f", "--input-file"}, description = "Rule set input file.")
  private String input_filepath = "-";
  @Parameter(names = {"-g", "--grd"}, description = "Print the Graph of Rule Dependencies.")
  private boolean print_grd = false;
  @Parameter(names = {"-s", "--print-scc"},
      description = "Print the Strongly Connected Components.")
  private boolean print_scc = false;
  @Parameter(names = {"-r", "--rule-set"}, description = "Print the rule set.")
  private boolean print_ruleset = false;
  @Parameter(names = {"-c", "--forward-chaining"},
      description = "Apply forward chaining on the specified Fact Base.")
  private String facts_filepath = "-";
  @Parameter(names = {"-h", "--help"}, description = "Print this message.")
  private boolean help = false;
  @Parameter(names = {"-v", "--version"}, description = "Print version information")
  private boolean version = false;

  @SuppressWarnings("deprecation")
  public static void main(String[] args) {

    App options = new App();
    @Var
    JCommander commander = null;
    try {
      commander = new JCommander(options, args);
    } catch (com.beust.jcommander.ParameterException e) {
      System.err.println(e.getMessage());
      System.exit(1);
    }

    if (options.help) {
      System.out.println(
          "For more details about this tool see : https://github.com/arthur-boixel/graal-stratified-negation ");
      commander.usage();
      System.exit(0);
    }

    if (options.version) {
      printVersion();
      System.exit(0);
    }

    if (options.input_filepath.compareTo("-") == 0) {
      System.out.println("Error, you need a Rule Base or at least launch the GUI");
      System.exit(0);
    }

    LabeledGraphOfRuleDependencies grd =
        new LabeledGraphOfRuleDependencies(new File(options.input_filepath));

    if (options.print_ruleset) {
      System.out.println(Utils.getRulesText(grd.getRules()));
    }

    if (options.print_grd) {
      System.out.println(Utils.getGrdText(grd));
    }

    if (options.print_scc) {
      System.out.println(Utils.getSccText(grd.getStronglyConnectedComponentsGraph()));
    }

    System.out.print("===== ANALYSIS : ");

    if (!grd.hasCircuitWithNegativeEdge()) {

      System.out.println("STRATIFIABLE =====");

      if (options.facts_filepath.compareTo("-") != 0) {
        System.out.println(Utils.getSaturationFromFile(options.facts_filepath, grd));
      }
    } else {
      System.out.println("NOT STRATITIFABLE =====");
    }
  }

  private static void printVersion() {
    System.out.println(PROGRAM_NAME + " version " + VERSION);
  }
}
