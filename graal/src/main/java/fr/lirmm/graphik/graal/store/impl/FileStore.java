package fr.lirmm.graphik.graal.store.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.Set;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.Query;
import fr.lirmm.graphik.graal.core.SymbolGenerator;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.Term.Type;
import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
import fr.lirmm.graphik.graal.core.stream.SubstitutionReader;
import fr.lirmm.graphik.graal.parser.misc.StringAtomReader;
import fr.lirmm.graphik.graal.parser.misc.StringFormat;
import fr.lirmm.graphik.graal.store.Store;
import fr.lirmm.graphik.util.stream.ObjectReader;

@SuppressWarnings("all")
@edu.umd.cs.findbugs.annotations.SuppressWarnings("")
public class FileStore implements Store {

    private static final long serialVersionUID = -5704133692186133745L;
    
    private File file;
    private StringFormat representation;

    public FileStore(URI uri, StringFormat representation) {
        this.file = new File(uri);
        this.representation = representation;
    }

    public void add(Iterable<Atom> atoms) throws AtomSetException {

        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(this.file));

            for(Atom a : atoms) {
                writer.write(representation.getAtomSeparator());
                writer.write(representation.toString(a));
            }

        } catch (IOException e) {
            throw new AtomSetException(e.getMessage(), e);
        } finally {
            if(writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    throw new AtomSetException(e.getMessage(), e);
                }
            }
        }

    }

    public ObjectReader iterator() {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(this.file));
            return new StringAtomReader(reader, representation);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public SymbolGenerator getFreeVarGen() {
        // TODO implement this method
        throw new Error("This method isn't implemented");
    }

    @Override
    public boolean contains(Atom atom) {
        // TODO implement this method
        throw new Error("This method isn't implemented");
    }

    @Override
    public Set<Term> getTerms() {
        // TODO implement this method
        throw new Error("This method isn't implemented");
    }

    @Override
    public boolean add(Atom atom) {
        // TODO implement this method
        throw new Error("This method isn't implemented");
    }

    @Override
    public boolean remove(Atom atom) {
        // TODO implement this method
        throw new Error("This method isn't implemented");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.lirmm.graphik.kb.IAtomSet#getTerms(fr.lirmm.graphik.kb.ITerm.Type)
     */
    @Override
    public Set<Term> getTerms(Type type) {
        // TODO implement this method
        throw new Error("This method isn't implemented");
    }

	/* (non-Javadoc)
	 * @see fr.lirmm.graphik.alaska.store.IWriteableStore#remove(fr.lirmm.graphik.util.stream.ObjectReader)
	 */
	@Override
	public void remove(ObjectReader<Atom> stream) throws AtomSetException {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	/* (non-Javadoc)
	 * @see fr.lirmm.graphik.kb.core.AtomSet#getAllPredicate()
	 */
	@Override
	public ObjectReader<Predicate> getAllPredicate() throws AtomSetException {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	

}
