package fr.lirmm.graphik.kb.stream;

import java.util.Iterator;

import fr.lirmm.graphik.kb.core.Substitution;

public interface SubstitutionReader extends Iterator<Substitution>,
        Iterable<Substitution> {

    boolean hasNext();

    Substitution next();

    Iterator<Substitution> iterator();

    void close();

}
