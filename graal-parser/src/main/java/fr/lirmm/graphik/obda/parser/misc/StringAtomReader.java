package fr.lirmm.graphik.obda.parser.misc;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.kb.core.Atom;
import fr.lirmm.graphik.util.stream.AbstractReader;

/**
 * @deprecated use {@link fr.lirmm.graphik.obda.io.basic.BasicParser} instead
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class StringAtomReader extends AbstractReader<Atom> {
	
	private static final Logger logger = LoggerFactory.getLogger(StringAtomReader.class);

	private Reader reader;
	private StringFormat representation;
	private Atom atom;
	
	// /////////////////////////////////////////////////////////////////////////
    //	CONSTRUCTORS
    // /////////////////////////////////////////////////////////////////////////

	public StringAtomReader(Reader reader, StringFormat representation) {
		this.reader = reader;
		this.representation = representation;
	}
	
	/**
     * @param string
     * @param representation
     */
    public StringAtomReader(String string,
            StringFormat representation) {
        this.reader = new StringReader(string);
        this.representation = representation;
    }
    
    // /////////////////////////////////////////////////////////////////////////
    //	METHODS
    // /////////////////////////////////////////////////////////////////////////

    public boolean hasNext() { 
		atom = null;
		 
	 	try {
			atom = representation.readAtom(this.reader);
			if(logger.isDebugEnabled())
				logger.debug("Write : "+atom);
		} catch (IOException e) {
			logger.error("Error during atom parsing", e);
		}
		
		return atom != null;
	}
	
	public Atom next() {
		try {
			return (atom == null)? representation.readAtom(this.reader) : atom;		
		} catch (IOException e) {
			logger.error("Error during atom parsing", e);
			return null;
		}
	}
	

}
