/**
 * 
 */
package fr.lirmm.graphik.graal.apps;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import fr.lirmm.graphik.graal.io.dlgp.DlgpWriter;
import fr.lirmm.graphik.graal.io.owl.OWLParser;
import fr.lirmm.graphik.graal.io.owl.OWLParserException;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class OWL2DLP {

	@Parameter(names = { "-h", "--help" }, description = "Print this message", help = true)
	private boolean help;

	@Parameter(names = { "-v", "--verbose" }, description = "Enable verbose mode")
	private boolean verbose = false;

	@Parameter(names = { "--version" }, description = "Print version information")
	private boolean version = false;

	@Parameter(names = { "-f", "--file" }, description = "OWL input file")
	private String inputFile = "";

	@Parameter(names = { "-o", "--output" }, description = "The output file")
	private String outputFile = "";

	@Parameter(names = { "-p", "--prefix" }, description = "disable prefix")
	private Boolean prefixDisable = false;

	public static void main(String args[]) throws OWLOntologyCreationException,
			IOException, OWLParserException {

		DlgpWriter writer;
		OWL2DLP options = new OWL2DLP();
		JCommander commander = new JCommander(options, args);

		if (options.help) {
			commander.usage();
			System.exit(0);
		}

		if (options.version) {
			printVersion();
			System.exit(0);
		}

		OWLParser parser;
		if (options.inputFile.isEmpty()) {
			parser = new OWLParser(System.in);
		} else {
			parser = new OWLParser(new File(options.inputFile));
		}

		if (options.outputFile.isEmpty()) {
			writer = new DlgpWriter(System.out);
		} else {
			writer = new DlgpWriter(new File(options.outputFile));
		}

		parser.prefixEnable(!options.prefixDisable);

		// MAIN
		for (Object o : parser) {
			writer.write(o);
		}

		writer.close();

	}
	
	private static void printVersion() {
		Manifest manifest;
		InputStream is;
		Attributes att;
		URL pathToManifest;

		String version;
		String vendor;
		String buildDate;

		// GET DATA
		try {
			pathToManifest = new URL(getPathToManifest());
			is = pathToManifest.openStream();
			manifest = new Manifest(is);
			att = manifest.getMainAttributes();

			version = att.getValue("Specification-Version");
			vendor = att.getValue("Specification-Vendor");
			buildDate = att.getValue("Built-On");

			is.close();
		} catch (Exception ex) {
			version = vendor = buildDate = "?";
		}

		// PRINT DATA
		System.out.print("owl2dlp version \"");
		System.out.print(version);
		System.out.println("\"");
		System.out.print("Built on ");
		System.out.println(buildDate);
		System.out.print("Produced by ");
		System.out.println(vendor);
	}

	private static String getPathToManifest() {

		// 1 - Lire le nom de la classe
		String classSimpleName = OWL2DLP.class.getSimpleName() + ".class";
		// classSimpleName = VersionUtil.class

		// 2 - Récupérer le chemin physique de la classe
		String pathToClass = OWL2DLP.class.getResource(classSimpleName)
				.toString();
		// pathToClass =
		// file:/C:/workspace/VersionUtil/bin/com/abdennebi/version/VersionUtil.class
		// pathToClass =
		// jar:file:/C:/version.jar!/com/abdennebi/version/VersionUtil.class

		// 3 - Récupérer le chemin de la classe à partir de la racine du
		// classpath
		String classFullName = OWL2DLP.class.getName().replace('.', '/')
				+ ".class";
		// classFullName = com/abdennebi/version/VersionUtil.class

		// 4 - Récupérer le chemin complet vers MANIFEST.MF
		String pathToManifest = pathToClass.substring(0, pathToClass.length()
				- (classFullName.length()))
				+ "META-INF/MANIFEST.MF";
		// pathToManifest =
		// file:/C:/workspace/VersionUtil/bin/META-INF/MANIFEST.MF
		// pathToManifest = jar:file:/C:/version.jar!/META-INF/MANIFEST.MF

		return pathToManifest;
	}

}
