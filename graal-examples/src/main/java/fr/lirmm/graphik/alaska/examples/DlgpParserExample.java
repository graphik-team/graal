package fr.lirmm.graphik.alaska.examples;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import fr.lirmm.graphik.kb.core.Atom;
import fr.lirmm.graphik.kb.core.Rule;
import fr.lirmm.graphik.kb.exception.AtomSetException;
import fr.lirmm.graphik.obda.io.dlgp.DlgpParser;
import fr.lirmm.graphik.obda.io.dlgp.DlgpWriter;
import fr.lirmm.graphik.obda.parser.ParseException;

/**
 * 
 */

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class DlgpParserExample {
    
    public static void main(String[] args ) throws ParseException, AtomSetException, InterruptedException, IOException{
      
    	DlgpParser parser = new DlgpParser(new BufferedReader(new FileReader("./src/main/resources/univ-bench.dlp")));
        
    	DlgpWriter writer = new DlgpWriter();
        for(Object o : parser) {
        	if(o instanceof Rule)
        		writer.write((Rule)o);
        	else if(o instanceof Atom)
        		writer.write((Atom)o);
        }
        writer.close();
        
    }
};
