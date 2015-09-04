package fr.lirmm.graphik.graal.apps;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import fr.lirmm.graphik.graal.io.GraalWriter;
import fr.lirmm.graphik.graal.io.Parser;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.graal.io.dlp.DlgpWriter;
import fr.lirmm.graphik.graal.io.ruleml.RuleMLWriter;
import fr.lirmm.graphik.graal.io.owl.OWL2Parser;


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

		if (options.input_file.equals("-"))
			in = System.in;
		else {
			try { in = new FileInputStream(options.input_file); }
			catch (Exception e) {
				System.err.println("Could not open file: " + options.input_file);
				System.err.println(e);
				e.printStackTrace();
				System.exit(1);
			}
		}

		if (options.output_file.equals("-"))
			out = System.out;
		else {
			try { out = new FileOutputStream(options.output_file); }
			catch (Exception e) {
				System.err.println("Could not open file: " + options.output_file);
				System.err.println(e);
				e.printStackTrace();
				System.exit(1);
			}
		}

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

		switch (options.output_format) {
			case "dlp":
			case "dlgp":
			case "datalog+":
				writer = new DlgpWriter(out);
				break;
			case "ruleml":
				writer = new RuleMLWriter(out);
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
	           description = "Output format ('dlp', 'ruleml').")
	private String output_format = "dlp";

	@Parameter(names = { "-h", "--help" },
	           description = "Print this message.")
	private boolean help = false;

	private GraalConverter() { }
};

