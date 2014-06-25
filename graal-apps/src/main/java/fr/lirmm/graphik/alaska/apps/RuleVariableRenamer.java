/**
 * 
 */
package fr.lirmm.graphik.alaska.apps;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import fr.lirmm.graphik.graal.Alaska;
import fr.lirmm.graphik.graal.core.DefaultRule;
import fr.lirmm.graphik.graal.core.HashMapSubstitution;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.Substitution;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.obda.io.dlgp.DlgpParser;
import fr.lirmm.graphik.obda.io.dlgp.DlgpWriter;

/**
 * @author clement
 *
 */
public class RuleVariableRenamer {
	
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
						substitution.put(var, new Term(var.toString() + "_" + objectNumber, Term.Type.VARIABLE));
					}
					AtomSet body = Alaska.substitut(rule.getBody(), substitution);
					AtomSet head = Alaska.substitut(rule.getHead(), substitution);
					String label = rule.getLabel();
					if(label.isEmpty()) {
						label = "R"+objectNumber;
					}
					rule = new DefaultRule(label, body, head);
					writer.write(rule);
					
					++objectNumber;
				} else {
					writer.close();
					throw new Error("Untreated kind of logical object, please contribute !");
				}
			}
		} catch(IOException e) {
			writer.close();
			throw e;
		}
		
		writer.close();
	}

}
