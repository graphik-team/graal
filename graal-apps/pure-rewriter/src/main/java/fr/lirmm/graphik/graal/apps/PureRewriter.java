/**
 * 
 */
package fr.lirmm.graphik.graal.apps;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

import fr.lirmm.graphik.graal.io.dlp.Dlgp1Writer;
import fr.lirmm.graphik.util.Apps;
import fr.lirmm.graphik.util.Profiler;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * @author Mélanie Konïg (LIRMM)
 * 
 */
public class PureRewriter {

	private static Profiler profiler;
	private static PureRewriter options;
	private static Dlgp1Writer writer = new Dlgp1Writer();

	@Parameter(names = { "-h", "--help" }, description = "Print this message", help = true)
	private boolean help;

	@Parameter(names = { "-v", "--verbose" }, description = "Enable verbose mode")
	private boolean verbose = false;

	@Parameter(names = { "-V", "--version" }, description = "Print version information")
	private boolean version = false;

	public static void main(String args[]) {

		options = new PureRewriter();
		JCommander commander = new JCommander(options);
		commander.setProgramName("java -jar PureRewriter.jar");

		CompileCommand cmdCompile = new CompileCommand(writer);
		RewriteCommand cmdRewrite = new RewriteCommand(writer);
		UnfoldCommand cmdUnfold = new UnfoldCommand(writer);

		commander.addCommand(CompileCommand.NAME, cmdCompile);
		commander.addCommand(RewriteCommand.NAME, cmdRewrite);
		commander.addCommand(UnfoldCommand.NAME, cmdUnfold);

		try {
			commander.parse(args);
		} catch (ParameterException e) {
			System.err.println("\nError: " + e.getMessage() + "\n");
			commander.usage();
			System.exit(1);
		}

		if (options.help) {
			commander.usage();
			System.exit(0);
		}

		if (options.version) {
			Apps.printVersion("pure-rewriter");
			System.exit(0);
		}

		if (options.verbose) {
			profiler = new Profiler(System.err);
			cmdCompile.enableVerbose(true);
			cmdRewrite.enableVerbose(true);
			cmdUnfold.enableVerbose(true);
			cmdCompile.setProfiler(profiler);
			cmdRewrite.setProfiler(profiler);
			cmdUnfold.setProfiler(profiler);
		}

		if (commander.getParsedCommand() == null) {
			System.err.println("\nError: Expected a command.\n");
			commander.usage();
			System.exit(1);
		}

		// Main part
		try {
			String command = commander.getParsedCommand();
			if (CompileCommand.NAME.equals(command)) {
				cmdCompile.run(commander);
			} else if (RewriteCommand.NAME.equals(command)) {
				cmdRewrite.run(commander);
			} else if (UnfoldCommand.NAME.equals(command)) {
				cmdUnfold.run(commander);
			}
		} catch (Exception e) {
			System.err.println("An error occured: " + e.getMessage());
			System.exit(1);
		}

	}

}
