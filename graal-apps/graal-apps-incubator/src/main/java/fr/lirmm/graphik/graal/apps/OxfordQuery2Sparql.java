/* Graal v0.7.4
 * Copyright (c) 2014-2015 Inria Sophia Antipolis - Méditerranée / LIRMM (Université de Montpellier & CNRS)
 * All rights reserved.
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * Author(s): Clément SIPIETER
 *            Mélanie KÖNIG
 *            Swan ROCHER
 *            Jean-François BAGET
 *            Michel LECLÈRE
 *            Marie-Laure MUGNIER
 */
 package fr.lirmm.graphik.graal.apps;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.io.oxford.OxfordQueryParser;
import fr.lirmm.graphik.graal.io.sparql.SparqlConjunctiveQueryWriter;


/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class OxfordQuery2Sparql {
	
	@Parameter(names = {"-p", "--prefix"}, description = "Rdf default prefix")
	private String rdfPrefix = "";
	
	@Parameter(names = {"-h", "--help"}, help = true)
	private boolean help;
	
	@Parameter
	private List<String> queries = new LinkedList<String>();
	
	
	public static void main(String[] args) throws IOException {

		OxfordQuery2Sparql options = new OxfordQuery2Sparql();
		JCommander commander = new JCommander(options, args);
		
		if(options.help) {
			commander.usage();
			System.exit(0);
		}
		
		for(String query : options.queries) {
			ConjunctiveQuery q = OxfordQueryParser.parseQuery(query);
			
			
			SparqlConjunctiveQueryWriter writer = new SparqlConjunctiveQueryWriter();
			writer.write(q);
		}
	}

}
