package fr.lirmm.graphik.graal.apps;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import fr.lirmm.graphik.graal.api.io.GraalWriter;
import fr.lirmm.graphik.graal.api.io.Parser;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.graal.io.dlp.DlgpWriter;
import fr.lirmm.graphik.graal.io.iris_dtg.IrisDtgWriter;
import fr.lirmm.graphik.graal.io.owl.OWL2Parser;
import fr.lirmm.graphik.graal.io.ruleml.RuleMLWriter;


public class GraalConverter {
	
	public static final String   PROGRAM_NAME   = "graal-converter";

	public static void main(String args[]) {
		GraalConverter options = new GraalConverter();

		JCommander commander = new JCommander(options,args);

		if (options.help) {
			commander.usage();
			System.exit(0);
		}

		InputStream     in      = null;
		OutputStream    out     = null;
		GraalWriter     writer  = null;
		Parser          reader  = null;

		System.err.print("[info] Reading from ");
		if (options.input_file.equals("-")) {
			System.err.println("<STDIN>");
			in = System.in;
		}
		else {
			try {
				in = new FileInputStream(options.input_file);
				System.err.println("'" + options.input_file + "'"); 
			}
			catch (Exception e) {
				System.err.println("Could not open file: " + options.input_file);
				System.err.println(e);
				e.printStackTrace();
				System.exit(1);
			}
		}

		System.err.print("[info] Writing to ");
		if (options.output_file.equals("-")) {
			out = System.out;
			System.err.println("<STDOUT>");
		}
		else {
			try {
				out = new FileOutputStream(options.output_file);
				System.err.println("'" + options.output_file + "'");
			}
			catch (Exception e) {
				System.err.println("Could not open file: " + options.output_file);
				System.err.println(e);
				e.printStackTrace();
				System.exit(1);
			}
		}

		System.err.println("[info] Input format: " + options.input_format);
		switch (options.input_format) {
			case "dlp":
			case "dlgp":
			case "datalog+":
				reader = new DlgpParser(in);
				break;
			case "owl":
			case "owl2":
				try { reader = new OWL2Parser(in); }
				catch (Exception e) {
					System.err.println("Something went wrong when creating OWL2Parser: " + e);
					System.exit(2);
				}
				break;
			default:
				System.err.println("Unrecognized input format: " + options.input_format);
				System.exit(2);
		}

		System.err.println("[info] Output format: " + options.output_format);
		switch (options.output_format) {
			case "dlp":
			case "dlgp":
			case "datalog+":
				writer = new DlgpWriter(out);
				break;
			case "ruleml":
				writer = new RuleMLWriter(out);
				break;
			case "iris-dtg":
				writer = new IrisDtgWriter(out);
				break;
			default:
				System.err.println("Unrecognized output format: " + options.output_format);
				System.exit(2);
		}

		Object o = null;
		while (reader.hasNext()) {
			try { o = reader.next(); }
			catch (Exception e) { System.err.println("Exception while reading: " + e); }
			try { writer.write(o); }
			catch (Exception e) {
				System.err.println("Exception while writing: " + e);
				System.err.println("Couldn't write " + o);
			}
		}
		try { writer.close(); }
		catch (Exception e) {
			System.err.println("Exception while closing output: " + e);
			System.exit(3);
		}

	}

	@Parameter(names = { "-i", "--input-file" },
	           description = "Input file (use '-' for stdin)")
	private String input_file = "-";

	@Parameter(names = { "-o", "--output-file" },
	           description = "Output file (use '-' for stdout)")
	private String output_file = "-";

	@Parameter(names = { "-I", "--input-format" },
	           description = "Input format ('dlp', 'owl').") 
	private String input_format = "dlp";

	@Parameter(names = { "-O", "--output-format" },
	           description = "Output format ('dlp', 'ruleml', 'iris-dtg').")
	private String output_format = "dlp";

	@Parameter(names = { "-h", "--help" },
	           description = "Print this message.")
	private boolean help = false;

	private GraalConverter() { }
};

