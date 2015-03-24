/**
 * 
 */
package fr.lirmm.graphik.graal.apps;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import fr.lirmm.graphik.graal.backward_chaining.pure.rules.HierarchicalCompilation;
import fr.lirmm.graphik.graal.backward_chaining.pure.rules.IDCompilation;
import fr.lirmm.graphik.graal.backward_chaining.pure.rules.RulesCompilation;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.ruleset.RuleSet;
import fr.lirmm.graphik.graal.io.RuleWriter;
import fr.lirmm.graphik.graal.io.dlp.Dlgp1Writer;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
@Parameters(commandDescription = "Compile an ontology")
class CompileCommand extends PureCommand {
	
	public static final String NAME = "compile";
	
	public static final String ID_COMPILATION_NAME = "ID";
	public static final String HIERACHICAL_COMPILATION_NAME = "H";
	
	private RuleWriter writer;

	@Parameter(names = { "-h", "--help" }, description = "Print this message", help = true)
	private boolean help;

	@Parameter(description = "<DLGP ontology file>", required = true)
	private List<String> ontologyFile = new LinkedList<String>();

	@Parameter(names = { "-f", "--file" }, description = "Output file for this compilation")
	private String outputFile = null;

	@Parameter(names = { "-t", "--type" }, description = "Compilation type " + ID_COMPILATION_NAME + " or " + HIERACHICAL_COMPILATION_NAME, required = false)
	private String compilationType = "ID";
	
	////////////////////////////////////////////////////////////////////////////
	// 
	////////////////////////////////////////////////////////////////////////////
	
	public CompileCommand(RuleWriter writer) {
		this.writer = writer;
	}
	
	////////////////////////////////////////////////////////////////////////////
	// 
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * @param commander
	 * @throws FileNotFoundException
	 */
	public void run(JCommander commander) throws FileNotFoundException {
		if (this.help) {
			commander.usage(NAME);
			System.exit(0);
		}

		RuleSet rules = Util.parseOntology(this.ontologyFile.get(0));
		RulesCompilation compilation;
		if ("H".equals(this.compilationType)) {
			compilation = new HierarchicalCompilation();
		} else {
			compilation = new IDCompilation();
		}

		compilation.setProfiler(this.getProfiler());
		compilation.compile(rules.iterator());

		try {
			RuleWriter w = writer;
			if (this.outputFile != null && !outputFile.isEmpty()) {
				w = new Dlgp1Writer(new File(this.outputFile));
			}
			for(Rule r : compilation.getSaturation()) {
				w.write(r);
			}
		} catch (IOException e) {
		}
	}
}
