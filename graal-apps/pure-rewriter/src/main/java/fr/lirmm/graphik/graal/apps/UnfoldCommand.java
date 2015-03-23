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
import fr.lirmm.graphik.util.Profilable;
import fr.lirmm.graphik.util.Profiler;
import fr.lirmm.graphik.util.stream.FilterIterator;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
@Parameters(commandDescription = "Unfold a pivotal UCQ")
public class UnfoldCommand implements Profilable {
	
	public static final String NAME = "unfold";
		
	@Parameter(names = { "-h", "--help" }, description = "Print this message", help = true)
	private boolean help;

	@Parameter(names = { "-c", "--compilation-file" }, description = "The compilation file")
	private String compilationFile = "";

	@Parameter(names = { "-t", "--compilation-type" }, description = "Compilation type H, ID")
	private String compilationType = "ID";
	
	@Parameter(names = { "-q", "--queries" }, description = "The queries to rewrite in DLGP", required = true)
	private String queriesString = null;

	private Profiler profiler;
	private ConjunctiveQueryWriter writer;

	////////////////////////////////////////////////////////////////////////////
	// 
	////////////////////////////////////////////////////////////////////////////
	
	public UnfoldCommand(Profiler profiler, ConjunctiveQueryWriter writer) {
		this.writer = writer;
		this.profiler = profiler;
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
		compilation.load(Collections.EMPTY_LIST.iterator(), new FilterIterator<Object, Rule>(new Dlgp1Parser(
				new File(this.compilationFile)), new RulesFilter()));
		
		List<ConjunctiveQuery> queries = Util.parseQueries(this.queriesString); 
		
		// unfolding
		if (this.getProfiler() != null) {
			this.getProfiler().start("unfolding time");
		}

		Iterable<ConjunctiveQuery> unfold = compilation.unfold(queries);

		if (this.getProfiler() != null) {
			this.getProfiler().stop("unfolding time");
		}
		
		for(ConjunctiveQuery q : unfold) {
			writer.write(q);
		}
		
	}

	@Override
	public void setProfiler(Profiler profiler) {
		this.profiler = profiler;
	}

	@Override
	public Profiler getProfiler() {
		return this.profiler;
	}

}
