/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2015)
 *
 * Contributors :
 *
 * Clément SIPIETER <clement.sipieter@inria.fr>
 * Mélanie KÖNIG
 * Swan ROCHER
 * Jean-François BAGET
 * Michel LECLÈRE
 * Marie-Laure MUGNIER <mugnier@lirmm.fr>
 *
 *
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
 /**
 * 
 */
package fr.lirmm.graphik.graal.apps;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.core.DefaultRule;
import fr.lirmm.graphik.graal.core.HashMapSubstitution;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.graal.io.dlp.DlgpWriter;

/**
 * @author clement
 *
 */
public class RuleVariableRenamer {
	
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(RuleVariableRenamer.class);
	
	public static void main(String[] args) throws IOException {
		DlgpParser parser;
		DlgpWriter writer;
		if(args.length >= 1) {
			parser = new DlgpParser(new File(args[0]));
			if(args.length >= 2) {
				writer = new DlgpWriter(new File(args[1]));
			} else {
				writer = new DlgpWriter(System.out);
			}
		} else {
			parser = new DlgpParser(System.in);
			writer = new DlgpWriter(System.out);
		}
		
		Substitution substitution;
		int objectNumber = 0;
		
		try{
			while (parser.hasNext()) {
				Object o = parser.next();
				if(o instanceof Rule) {
					Rule rule = (Rule)o;
					Set<Term> vars = rule.getBody().getTerms(Term.Type.VARIABLE);
					vars.addAll(rule.getHead().getTerms(Term.Type.VARIABLE));
					substitution = new HashMapSubstitution();
					for(Term var : vars) {
						substitution.put(
								var,
								DefaultTermFactory.instance().createVariable(
										var.toString() + "_" + objectNumber));
					}
					InMemoryAtomSet body = substitution.createImageOf(rule.getBody());
					InMemoryAtomSet head = substitution.createImageOf(rule.getHead());
					String label = rule.getLabel();
					if(label.isEmpty()) {
						label = "R"+objectNumber;
					}
					rule = new DefaultRule(label, body, head);
					writer.write(rule);
					
					++objectNumber;
				} else {
					writer.close();
					if(LOGGER.isWarnEnabled()) {
						LOGGER.warn("Untreated kind of logical object (" + o.getClass() + "), please contribute !");
					}
				}
			}
		} catch(IOException e) {
			writer.close();
			throw e;
		}
		
		writer.close();
	}

}
