package fr.lirmm.graphik.alaska.examples;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.NegativeConstraint;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
import fr.lirmm.graphik.graal.io.dlgp.DlgpParser;
import fr.lirmm.graphik.graal.io.dlgp.DlgpWriter;
import fr.lirmm.graphik.graal.parser.ParseException;

/**
 * 
 */

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class DlgpParserExample {
    
    public static void main(String[] args ) throws ParseException, AtomSetException, InterruptedException, IOException{
      
    	DlgpParser parser = new DlgpParser(new BufferedReader(new FileReader("./src/main/resources/example.dlp")));
        
    	DlgpWriter writer = new DlgpWriter();
        for(Object o : parser) {
        	if(o instanceof NegativeConstraint)
        		writer.write((NegativeConstraint) o);
        	else if(o instanceof Rule)
        		writer.write((Rule)o);
        	else if(o instanceof Atom)
        		writer.write((Atom)o);
        	else if(o instanceof ConjunctiveQuery)
        		writer.write((ConjunctiveQuery) o);
        }
        writer.close();
        
    }
};
