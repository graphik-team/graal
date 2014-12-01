/**
 * 
 */
package fr.lirmm.graphik.graal.apps;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.NegativeConstraint;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.Substitution;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.graph.MemoryGraphAtomSet;
import fr.lirmm.graphik.graal.core.ruleset.LinkedListRuleSet;
import fr.lirmm.graphik.graal.core.ruleset.RuleSet;
import fr.lirmm.graphik.graal.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.forward_chaining.StaticChase;
import fr.lirmm.graphik.graal.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.homomorphism.HomomorphismFactoryException;
import fr.lirmm.graphik.graal.homomorphism.StaticHomomorphism;
import fr.lirmm.graphik.graal.io.dlgp.DlgpParser;
import fr.lirmm.graphik.graal.io.dlgp.DlgpWriter;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class DLPProgram {
	public static void main(String[] args) throws ChaseException, IOException, HomomorphismFactoryException, HomomorphismException {
		DlgpWriter writer = new DlgpWriter();
		DlgpParser parser = new DlgpParser(System.in);
		if(args.length > 0)
			 parser = new DlgpParser(new File(args[0]));
		
		AtomSet atomSet = new MemoryGraphAtomSet();
		RuleSet ruleSet = new LinkedListRuleSet();
		LinkedList<ConjunctiveQuery> querySet = new LinkedList<ConjunctiveQuery>();
		
        for(Object o : parser) {
        	if(o instanceof NegativeConstraint)
        		System.err.println("Constraint not supported");
        	else if(o instanceof Rule)
        		ruleSet.add((Rule)o);
        	else if(o instanceof Atom)
        		atomSet.add((Atom)o);
        	else if(o instanceof ConjunctiveQuery)
        		querySet.add((ConjunctiveQuery)o);
        }
        parser.close();
        
        StaticChase.executeChase(atomSet, ruleSet);
        writer.write("% facts\n");
        writer.write(atomSet);
        
        writer.write("\n\n% queries\n");
        for(ConjunctiveQuery q : querySet) {
        	writer.write(q);
        	for(Substitution s : StaticHomomorphism.executeQuery(q, atomSet)) {
        		writer.write(s.toString());
        		writer.write("\n");
        	}
        }
        writer.flush();

	}
}
