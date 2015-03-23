/**
 * 
 */
package fr.lirmm.graphik.graal.apps;

import java.io.FileNotFoundException;

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

	@Parameter(names = { "-d", "--debug" }, description = "Enable debug mode", hidden = true)
	private boolean debug = false;

	public static void main(String args[]) throws Exception {

		options = new PureRewriter();
		JCommander commander = new JCommander(options);
		commander.setProgramName("java -jar PureRewriter.jar");

		CompileCommand cmdCompile = new CompileCommand(profiler, writer);
		RewriteCommand cmdRewrite = new RewriteCommand(profiler, writer, options.verbose);

		commander.addCommand(CompileCommand.NAME, cmdCompile);
		commander.addCommand(RewriteCommand.NAME, cmdRewrite);

		try {
			commander.parse(args);
		} catch (ParameterException e) {
			System.err.println("\nError: " + e.getMessage() + "\n");
			commander.usage();
			System.exit(1);
		}

		if (options.debug) {
			Thread.sleep(20000);
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
			}
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}
	
}
