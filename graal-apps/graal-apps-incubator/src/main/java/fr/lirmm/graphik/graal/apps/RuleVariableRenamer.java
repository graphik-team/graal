/**
 * 
 */
package fr.lirmm.graphik.graal.apps;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.core.DefaultRule;
import fr.lirmm.graphik.graal.core.HashMapSubstitution;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.Substitution;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.core.term.Term;
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
			for(Object o : parser) {
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
					AtomSet body = substitution.createImageOf(rule.getBody());
					AtomSet head = substitution.createImageOf(rule.getHead());
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
