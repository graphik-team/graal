/**
 * 
 */
package fr.lirmm.graphik.graal.grd;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.Substitution;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.TreeMapSubstitution;
import fr.lirmm.graphik.graal.io.dlgp.DlgpParser;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public class GraphOfRuleDependencies {

	Map<String, Rule> rules;
	Map<Rule, Map<Substitution, Rule>> graph;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////
	
	public GraphOfRuleDependencies() {
		this.graph = new HashMap<Rule, Map<Substitution, Rule>>();
		this.rules = new TreeMap<String, Rule>();
	}

	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	public void addRule(Rule r) {
		this.rules.put(r.getLabel(), r);
		this.graph.put(r, new HashMap<Substitution, Rule>());
	}
	
	public Collection<Rule> getRules() {
		return this.rules.values();
	}

	public void addDependency(Rule src, Substitution sub, Rule dest) {
		this.graph.get(src).put(sub, dest);
	}

	public Map<Substitution, Rule> getOutEdges(Rule src) {
		return this.graph.get(src);
	}
	
	public void parseGrd(BufferedReader reader) throws IOException {
		String line = reader.readLine();
		while (line != null) {
			this.parseLine(line);
			line = reader.readLine();
		}
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// OVERRIDE METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		for (Map.Entry<Rule, Map<Substitution, Rule>> graphEntry : this.graph.entrySet()){
			
			for (Map.Entry<Substitution, Rule> edgeEntry : graphEntry.getValue().entrySet()){
			    s.append(graphEntry.getKey().getLabel());
			    s.append("--");
			    s.append(edgeEntry.getKey());
			    s.append("-->");
			    s.append(edgeEntry.getValue().getLabel());
			    s.append('\n');
			}
		}
		return s.toString();
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// STATIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	
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
	
	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	/**
	 * @param g
	 * @param line
	 */
	private void parseLine(String line) {
		if(line.length() > 0) {
			if(line.charAt(0) == '[') {
				this.parseRule(line);
			} else {
				this.parseDependency(line);
			}
		}
	}
	
	private void parseRule(String line) {
		this.addRule(DlgpParser.parseRule(line));
	}
	
	private void parseDependency(String line) {
		Pattern pattern = Pattern
				.compile("(\\S+)\\s*-->\\s*(\\S+)\\s*\\{(.*)\\}");
		Matcher matcher = pattern
				.matcher(line);
		Rule src, dest;

		if (matcher.find()) {
			src = this.rules.get(matcher.group(1));
			dest = this.rules.get(matcher.group(2));
			for (String unificator : matcher.group(3).split("\\}\\s*\\{")) {
				this.addDependency(src, parseSubstitution(unificator), dest);
			}
		}

	}
}
