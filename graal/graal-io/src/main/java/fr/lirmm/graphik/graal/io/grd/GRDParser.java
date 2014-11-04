/**
 * 
 */
package fr.lirmm.graphik.graal.io.grd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.Substitution;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.TreeMapSubstitution;
import fr.lirmm.graphik.graal.grd.GraphOfRuleDependencies;
import fr.lirmm.graphik.graal.io.dlgp.DlgpParser;
import fr.lirmm.graphik.graal.parser.ParseException;
import fr.lirmm.graphik.util.LinkedSet;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
public class GRDParser {

	private static final class GRD extends GraphOfRuleDependencies {

		GRD() {
			super();
		}

		protected void addRule(Rule r) {
			super.addRule(r);
		}

		protected void addDependency(Rule src, Substitution sub, Rule dest) {
			super.addDependency(src, sub, dest);
		}

	}

	private static final Logger logger = LoggerFactory
			.getLogger(GRDParser.class);
	
	private static GRDParser instance;

	private GRDParser() {
	}

	public GraphOfRuleDependencies parse(File file) throws FileNotFoundException, ParseException {
		return this.parse(new BufferedReader(new FileReader(file)));
	}
	
	public GraphOfRuleDependencies parse(BufferedReader reader)
			throws ParseException {
		GRD grd = new GRD();
		Map<String, Rule> rules = new TreeMap<String, Rule>();

		String line;
		try {
			line = reader.readLine();
			while (line != null) {
				parseLine(line, grd, rules);
				line = reader.readLine();
			}
		} catch (IOException e) {
			throw new ParseException("Error while parsiong GRD file.", e);
		}
		return grd;
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// STATIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	public static synchronized GRDParser getInstance() {
		if (instance == null)
			instance = new GRDParser();

		return instance;
	}

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * @param g
	 * @param line
	 */
	private static void parseLine(String line, GRD grd,
			Map<String, Rule> rules) {
		if (line.length() > 0) {
			if (line.charAt(0) == '[') {
				parseRule(line, grd, rules);
			} else {
				parseDependency(line, grd, rules);
			}
		}
	}

	private static void parseRule(String line, GRD grd,
			Map<String, Rule> rules) {
		Rule r = DlgpParser.parseRule(line);
		rules.put(r.getLabel(), r);
		grd.addRule(r);
	}

	private static void parseDependency(String line,
			GRD grd, Map<String, Rule> rules) {
		Pattern pattern = Pattern
				.compile("(\\S+)\\s*-->\\s*(\\S+)\\s*\\{(.*)\\}");
		Matcher matcher = pattern.matcher(line);
		Rule src, dest;

		if (matcher.find()) {
			src = rules.get(matcher.group(1));
			dest = rules.get(matcher.group(2));
			for (String unificator : matcher.group(3).split("\\}\\s*\\{")) {
				Substitution sub = parseSubstitution(unificator);
				if (logger.isDebugEnabled()) {
					logger.debug("add dependency: " + src.getLabel() + "--"
							+ sub + "-->" + dest.getLabel());
				}
				grd.addDependency(src, sub, dest);
			}
		}
	}

	/**
	 * @param unificatorString
	 * @return
	 */
	private static Substitution parseSubstitution(String unificatorString) {
		Substitution unificator = new TreeMapSubstitution();
		Pattern pattern = Pattern.compile("(\\S+)\\s*->\\s*(\\S+)");
		String src, dest;
		Term termSrc, termDest;

		for (String termSub : unificatorString.split("\\s*,\\s*")) {
			Matcher matcher = pattern.matcher(termSub);
			if (matcher.find()) {
				src = matcher.group(1);
				dest = matcher.group(2);
				termSrc = new Term(
						src,
						(Character.isUpperCase(src.charAt(0))) ? Term.Type.VARIABLE
								: Term.Type.CONSTANT);
				termDest = new Term(
						dest,
						(Character.isUpperCase(src.charAt(0))) ? Term.Type.VARIABLE
								: Term.Type.CONSTANT);
				unificator.put(termSrc, termDest);
			}
		}

		return unificator;
	}
}
