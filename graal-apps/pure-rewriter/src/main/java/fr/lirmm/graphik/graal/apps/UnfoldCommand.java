/**
 * 
 */
package fr.lirmm.graphik.graal.apps;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import fr.lirmm.graphik.graal.backward_chaining.pure.rules.RulesCompilation;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.io.ConjunctiveQueryWriter;
import fr.lirmm.graphik.graal.io.dlp.Dlgp1Parser;
import fr.lirmm.graphik.util.stream.FilterIterator;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
@Parameters(commandDescription = "Unfold a pivotal UCQ")
public class UnfoldCommand extends PureCommand {
	
	public static final String NAME = "unfold";
		
	@Parameter(names = { "-h", "--help" }, description = "Print this message", help = true)
	private boolean help;

	@Parameter(names = { "-c", "--compilation-file" }, description = "The compilation file")
	private String compilationFile = "";

	@Parameter(names = { "-t", "--compilation-type" }, description = "Compilation type H, ID")
	private String compilationType = "ID";
	
	@Parameter(names = { "-q", "--queries" }, description = "The queries to rewrite in DLGP", required = true)
	private String queriesString = null;

	private ConjunctiveQueryWriter writer;

	////////////////////////////////////////////////////////////////////////////
	// 
	////////////////////////////////////////////////////////////////////////////
	
	public UnfoldCommand(ConjunctiveQueryWriter writer) {
		this.writer = writer;
	}
	
	////////////////////////////////////////////////////////////////////////////
	// 
	////////////////////////////////////////////////////////////////////////////
	
	public void run(JCommander commander) throws IOException {
		
		if (this.help) {
			commander.usage(NAME);
			System.exit(0);
		}

		RulesCompilation compilation = Util.selectCompilationType(this.compilationType);
		
		if(this.isVerbose()) {
			compilation.setProfiler(this.getProfiler());
		}
		
		compilation.load(Collections.<Rule>emptyList().iterator(), new FilterIterator<Object, Rule>(new Dlgp1Parser(
				new File(this.compilationFile)), new RulesFilter()));
		
		List<ConjunctiveQuery> queries = Util.parseQueries(this.queriesString); 
		
		Iterable<ConjunctiveQuery> unfold = compilation.unfold(queries);
		
		// display
		for(ConjunctiveQuery q : unfold) {
			writer.write(q);
		}
		
	}

}
