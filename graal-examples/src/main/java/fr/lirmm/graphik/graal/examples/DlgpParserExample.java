package fr.lirmm.graphik.graal.examples;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.NegativeConstraint;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
import fr.lirmm.graphik.graal.io.dlp.DlpParser;
import fr.lirmm.graphik.graal.io.dlp.DlpWriter;
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
      
    	DlpParser parser = new DlpParser(new BufferedReader(new FileReader("./src/main/resources/example.dlp")));
        
    	DlpWriter writer = new DlpWriter();
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
